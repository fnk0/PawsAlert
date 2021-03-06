package com.gabilheri.pawsalert.ui.add;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.data.queryManagers.AnimalManager;
import com.gabilheri.pawsalert.data.queryManagers.AnimalShelterManager;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.helpers.FileUriUtils;
import com.gabilheri.pawsalert.helpers.PictureUtils;
import com.gabilheri.pawsalert.ui.widgets.AddImageLayout;
import com.gabilheri.pawsalert.ui.widgets.OnImageRemovedCallback;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/20/16.
 */
public class AddPetActivity extends BaseActivity
        implements OnMapReadyCallback, OnImageRemovedCallback,
        AnimalShelterManager.AnimalShelterCallback, AnimalManager.SaveAnimalCallback, SaveCallback {

    int PLACE_PICKER_REQUEST = 1498;
    int PHOTO_PICKER_REQUEST = 1499;

    @Bind(R.id.edit_text)
    AppCompatEditText mPetNameEditText;

    @Bind(R.id.petAge)
    AppCompatEditText mPetAgeEditText;

    @Bind(R.id.details)
    AppCompatEditText mPetDetailsEditText;

    @Bind(R.id.hasMicrochip)
    SwitchCompat mHasMicrochip;

    @Bind(R.id.isNeutered)
    SwitchCompat mIsNeutered;

    @Bind(R.id.hasVaccinations)
    SwitchCompat mHasVaccinations;

    @Bind(R.id.segment_missing)
    SegmentedGroup mSegmentMissing;

    @Bind(R.id.segment_gender)
    SegmentedGroup mSegmentGender;

    @Bind(R.id.segment_size)
    SegmentedGroup mSegmentSize;

    @Bind(R.id.segmentAge)
    SegmentedGroup mSegmentAge;

    @Bind(R.id.segment_type)
    SegmentedGroup mSegmentType;

    @Bind(R.id.map)
    MapView mLocationMapView;

    @Bind(R.id.selectedLocation)
    AppCompatTextView mSelectedLocationTV;

    @Bind(R.id.photosLayout)
    LinearLayout mPhotosLayout;

    LinearLayout mLastLayout;
    LatLng mSelectedLocation;

    int mUploaded = 0;
    Animal mAnimal;
    AnimalShelter mAnimalShelter;
    AnimalManager mAnimalManager;
    AnimalShelterManager mAnimalShelterManager;
    User mCurrentUser;
    HashMap<String, AddImageLayout> mPhotos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();
        enableActivityTransition();

        mAnimalManager = new AnimalManager(this);
        mAnimalShelterManager = new AnimalShelterManager(this);

        mPetNameEditText.setHint(R.string.pet_name);
        mPhotos = new HashMap<>();
        mCurrentUser = (User) ParseUser.getCurrentUser();

        mAnimalShelterManager.getAnimalShelter(mCurrentUser);
    }

    @OnClick(R.id.selectLocation)
    public void selectLocation(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageRemoved(String path) {

        for (Map.Entry<String, AddImageLayout> entry : mPhotos.entrySet()) {
            removeViewFromParent(entry.getValue());
        }

        mPhotos.remove(path);
        removeViewsFromViewGroup(mPhotosLayout);

        for (Map.Entry<String, AddImageLayout> entry : mPhotos.entrySet()) {
            addImageToPhotoLayout(entry.getValue());
        }
    }

    public void removeViewFromParent(View v) {
        if (v.getParent() != null) {
            ((ViewGroup) v.getParent()).removeView(v);
        }
    }

    public void removeViewsFromViewGroup(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = viewGroup.getChildAt(i);
            viewGroup.removeView(v);
        }
    }

    @OnClick(R.id.addPhotos)
    public void addPhotos(View v) {
        if (mPhotos.size() == Const.MAX_PHOTOS) {
            showSnackbar("You Reached the maximum of " + Const.MAX_PHOTOS + " photos.");
            return;
        }
        Intent intent = new Intent();
        intent.setType(Const.MIME_IMAGE_ALL);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PHOTO_PICKER_REQUEST);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                mLocationMapView.setVisibility(View.VISIBLE);
                mSelectedLocationTV.setVisibility(View.VISIBLE);
                Place place = PlacePicker.getPlace(this, data);
                mSelectedLocation = place.getLatLng();
                mSelectedLocationTV.setText(place.getName());
                mLocationMapView.onCreate(null);
                mLocationMapView.getMapAsync(this);
                MapsInitializer.initialize(this);
            } else if (requestCode == PHOTO_PICKER_REQUEST) {
                Uri imageURI = data.getData();
                String authority = imageURI.getAuthority();
                String filePath = FileUriUtils.getPath(imageURI);

                if(authority.equals(FileUriUtils.GOOGLE_URI) && filePath == null) {
                    filePath = PictureUtils.getImageUrlWithAuthority(this, imageURI);
                }

                if (filePath != null) {
                    if (mPhotos.containsKey(filePath)) {
                        showSnackbar("This photo has already been added.");
                        return;
                    }

                    AddImageLayout addImageLayout = new AddImageLayout(this);
                    addImageLayout.setCallback(this)
                            .setImagePath(filePath);

                    addImageToPhotoLayout(addImageLayout);
                    mPhotos.put(filePath, addImageLayout);
                } else {
                    showSnackbar("Error loading image. Please try another picture");
                }

            }
        }
    }

    public void addImageToPhotoLayout(AddImageLayout addImageLayout) {
        int picSize = mPhotosLayout.getWidth() / 2;

        LinearLayout.LayoutParams pictureParams = new LinearLayout.LayoutParams(picSize, picSize);

        if (mPhotos.size() % 2 == 0) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mLastLayout = new LinearLayout(this);
            mLastLayout.setOrientation(LinearLayout.HORIZONTAL);
            mLastLayout.setLayoutParams(layoutParams);
            mPhotosLayout.addView(mLastLayout);
        }

        mLastLayout.addView(addImageLayout, pictureParams);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mLocationMapView != null) {
            try {
                googleMap.addMarker(new MarkerOptions().position(mSelectedLocation));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLocation, googleMap.getCameraPosition().zoom));
            } catch (Exception ex) {
                Timber.d("Error parsing lat/lng");
            }
        }
    }

    String getRadioGroupText(SegmentedGroup segmentedGroup) {
        int id = segmentedGroup.getCheckedRadioButtonId();
        RadioButton b = ButterKnife.findById(segmentedGroup, id);
        return b.getText().toString();
    }

    @OnClick(R.id.createListing)
    public void createPetListing(View v) {
        mAnimal = new Animal();
        mAnimal.setName(mPetNameEditText.getText().toString());
        mAnimal.setAge(mPetAgeEditText.getText().toString() + " " + getRadioGroupText(mSegmentAge));
        mAnimal.setPetType(getRadioGroupText(mSegmentType));
        mAnimal.setGender(getRadioGroupText(mSegmentGender));
        mAnimal.setMissing(getRadioGroupText(mSegmentMissing).equals("Missing"));
        mAnimal.setSize(getRadioGroupText(mSegmentSize));
        mAnimal.setNeutered(mIsNeutered.isChecked());
        mAnimal.setMicrochip(mHasMicrochip.isChecked());
        mAnimal.setVaccinations(mHasVaccinations.isChecked());
        if (mSelectedLocation != null) {
            mAnimal.setLongitude(mSelectedLocation.longitude);
            mAnimal.setLatitude(mSelectedLocation.latitude);
        }

        mAnimal.setOtherInfo(mPetDetailsEditText.getText().toString());
        mAnimal.setUser(mCurrentUser);
        mAnimal.setDisabled(false);

        if (mAnimalShelter != null) {
            mAnimal.setAnimalShelter(mAnimalShelter);
        }

        if (mPhotos.size() == 0) {
            showSnackbar("You need at least 1 photo.");
            return;
        }

        showProgressDialog("Adding pet listing...", null);

        for (Map.Entry<String, AddImageLayout> s : mPhotos.entrySet()) {
            ParseFile file = PictureUtils.getParseFileFromPath(s.getKey(), mAnimal.getName(), String.valueOf(mUploaded));
            mAnimal.addPhoto(file);
            file.saveInBackground(this);
        }
    }

    @Override
    public void onAnimalShelter(AnimalShelter animalShelter) {
        mAnimalShelter = animalShelter.fromParseObject();
    }

    @Override
    public void onErrorFetchingAnimalShelter(Exception ex) {

    }

    @Override
    public void onAnimalSaved() {
        dismissDialog();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onErrorSavingAnimal(Exception e) {
        dismissDialog();
        showSnackbar("Error creating pet listing. Try again later.");
    }

    @Override
    public void done(ParseException e) {
        mUploaded++;
        if (e == null) {
            if (mUploaded == mPhotos.size()) {
                mAnimalManager.saveAnimal(mAnimal);
            }
        } else {
            Timber.e(e, "Error saving image: " + e.getLocalizedMessage());
            showSnackbar("Error saving image. Please try again later.");
        }

    }
}
