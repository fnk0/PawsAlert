package com.gabilheri.pawsalert.ui.shelter;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.helpers.FileUriUtils;
import com.gabilheri.pawsalert.helpers.PictureUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/9/16.
 */
public class ActivityAddShelter extends BaseActivity
        implements OnMapReadyCallback, TimePickerDialog.OnTimeSetListener {

    int PLACE_PICKER_REQUEST = 1498;
    int PHOTO_PICKER_REQUEST = 1499;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.shelterImage)
    AppCompatImageView mShelterImage;

    @Bind(R.id.selectedLocation)
    AppCompatTextView mSelectedLocationTV;

    @Bind(R.id.map)
    MapView mLocationMapView;

    @Bind(R.id.openTime)
    AppCompatButton mOpenTimeBTN;

    @Bind(R.id.closeTime)
    AppCompatButton mCloseTimeBTN;

    @Bind(R.id.shelterWeb)
    AppCompatEditText mShelterWebsite;

    @Bind(R.id.shelterName)
    AppCompatEditText mShelterName;

    @Bind(R.id.shelterPhoneNumber)
    AppCompatEditText mShelterPhoneNumber;

    @Bind(R.id.addDescription)
    AppCompatEditText mShelterDescription;

    @Bind(R.id.shelterEmail)
    AppCompatEditText mShelterEmailET;

    Place mSelectedLocation;

    TimePickerDialog mTimePickerDialog;
    boolean mOpenTime;
    String mPhotoPath;
    User mCurrentUser;
    AnimalShelter mAnimalShelter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();
        setTitle("");
        mCollapsingToolbar.setTitle("");
        mCurrentUser = (User) ParseUser.getCurrentUser();
    }

    @OnClick(R.id.fabAddPicture)
    public void addPicture(View v) {
        Intent intent = new Intent();
        intent.setType(Const.MIME_IMAGE_ALL);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PHOTO_PICKER_REQUEST);
    }

    @OnClick(R.id.selectLocation)
    public void addLocation(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnTextChanged(R.id.shelterName)
    public void onShelterNameChanged(CharSequence text) {
        setTitle(text);
        mCollapsingToolbar.setTitle(text);
    }

    @OnClick({R.id.openTime, R.id.closeTime})
    public void openTimeDialog(View v) {
        mTimePickerDialog = new TimePickerDialog(this, this, 8, 0, false);
        switch (v.getId()) {
            case R.id.openTime:
                mTimePickerDialog.setTitle("Open Time");
                mOpenTime = true;
                break;
            case R.id.closeTime:
                mTimePickerDialog.setTitle("Close Time");
                mOpenTime = false;
                break;
        }
        mTimePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String amPm;
        if (hourOfDay < 12) {
            amPm = "am";
        } else {
            amPm = "pm";
            hourOfDay -= 12;
        }
        String text = String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, minute, amPm);
        if (mOpenTime) {
            mOpenTimeBTN.setText(text);
        } else {
            mCloseTimeBTN.setText(text);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                mLocationMapView.setVisibility(View.VISIBLE);
                mSelectedLocationTV.setVisibility(View.VISIBLE);
                mSelectedLocation = PlacePicker.getPlace(this, data);
                mSelectedLocationTV.setText(mSelectedLocation.getName());
                mLocationMapView.onCreate(null);
                mLocationMapView.getMapAsync(this);
                MapsInitializer.initialize(this);
            } else if (requestCode == PHOTO_PICKER_REQUEST) {
                Uri imageURI = data.getData();
                mPhotoPath = FileUriUtils.getPath(imageURI);
                Glide.with(this)
                        .load(mPhotoPath)
                        .into(mShelterImage);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mLocationMapView != null) {
            try {
                googleMap.addMarker(new MarkerOptions().position(mSelectedLocation.getLatLng()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLocation.getLatLng(),
                        googleMap.getCameraPosition().zoom));
            } catch (Exception ex) {
                Timber.d("Error parsing lat/lng");
            }
        }
    }

    @OnClick(R.id.createShelter)
    public void createShelter(View v) {
        mAnimalShelter = new AnimalShelter();
        mAnimalShelter.setOwner(mCurrentUser);
        mAnimalShelter.setAddress(mSelectedLocation.getAddress().toString());
        mAnimalShelter.setShelterName(mShelterName.getText().toString());
        mAnimalShelter.setLatitude(mSelectedLocation.getLatLng().latitude);
        mAnimalShelter.setLongitude(mSelectedLocation.getLatLng().longitude);
        mAnimalShelter.setOpenTime(mOpenTimeBTN.getText().toString());
        mAnimalShelter.setCloseTime(mCloseTimeBTN.getText().toString());
        mAnimalShelter.setDescription(mShelterDescription.getText().toString());
        mAnimalShelter.setPhoneNumber(mShelterPhoneNumber.getText().toString());
        mAnimalShelter.setWebsite(mShelterWebsite.getText().toString());
        mAnimalShelter.setEmail(mShelterEmailET.getText().toString());

        showProgressDialog("Creating Animal Shelter...", null);

        ParseFile file = PictureUtils.getParseFileFromPath(mPhotoPath, mAnimalShelter.getShelterName(), mCurrentUser.getUsername());
        mAnimalShelter.setCoverPhoto(file);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mAnimalShelter.toParseObject(mAnimalShelter);
                mAnimalShelter.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dismissDialog();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add_shelter;
    }
}
