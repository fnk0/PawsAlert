package com.gabilheri.pawsalert.ui.add;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.canelmas.let.AskPermission;
import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.helpers.FileUriUtils;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class AddPetActivity extends BaseActivity implements OnMapReadyCallback {

    int PLACE_PICKER_REQUEST = 1498;
    int PHOTO_PICKER_REQUEST = 1499;

    @Bind(R.id.edit_text)
    AppCompatEditText mPetNameEditText;

    @Bind(R.id.petAge)
    AppCompatEditText mPetAgeEditText;

    @Bind(R.id.petDetails)
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

    List<String> mPhotos;
    int mUploaded = 0;
    Animal mAnimal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();
        enableActivityTransition();
        mPetNameEditText.setHint(R.string.pet_name);
        mPhotos = new ArrayList<>();
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

    @AskPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @OnClick(R.id.addPhotos)
    public void addPhotos(View v) {
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
                String filePath = FileUriUtils.getPath(imageURI);

                ImageView v = new ImageView(this);
                v.setScaleType(ImageView.ScaleType.CENTER_CROP);
                v.setId(mPhotos.size());

                Glide.with(this)
                        .load(filePath)
                        .into(v);

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

                mLastLayout.addView(v, pictureParams);
                mPhotos.add(filePath);
            }
        }
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
        mAnimal.setAge(Integer.parseInt(mPetAgeEditText.getText().toString()));
        mAnimal.setPetType(getRadioGroupText(mSegmentType));
        mAnimal.setGender(getRadioGroupText(mSegmentGender));
        mAnimal.setMissing(getRadioGroupText(mSegmentMissing).equals("Missing"));
        mAnimal.setSize(getRadioGroupText(mSegmentSize));
        mAnimal.setNeutered(mIsNeutered.isChecked());
        mAnimal.setMicrochip(mHasMicrochip.isChecked());
        mAnimal.setVaccinations(mHasVaccinations.isChecked());
        mAnimal.setLongitude(mSelectedLocation.longitude);
        mAnimal.setLatitude(mSelectedLocation.latitude);
        mAnimal.setOtherInfo(mPetDetailsEditText.getText().toString());
        mAnimal.setUser((User) ParseUser.getCurrentUser());

        if (mPhotos.size() == 0) {
            //TODO show error to the user
        }

        for (String s : mPhotos) {
            Bitmap bm = BitmapFactory.decodeFile(s);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] byteArray = stream.toByteArray();
            String fileName = mAnimal.getName() + "_" + new Date().getTime() + "_" + mUploaded + ".jpg";
            ParseFile file = new ParseFile(fileName, byteArray);
            mAnimal.addPhoto(file);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Timber.d("Uploaded file...");
                    mUploaded++;
                    if (mUploaded == mPhotos.size()) {
                        mAnimal.toParseObject(mAnimal);
                        mAnimal.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Timber.e(e, "Could not save object!");
                                } else {
                                    Timber.d("Successfully saved pet");
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
