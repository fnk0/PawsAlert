package com.gabilheri.pawsalert.ui.details;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.widgets.FlipAnimation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcusandreog@gmail.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/8/16.
 */
public class ActivityDetails extends BaseActivity
        implements GetCallback<Animal>, OnMapReadyCallback {

    int PLACE_PICKER_REQUEST = 1498;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.petImage)
    AppCompatImageView mHeaderImageView;

    @Bind(R.id.petAge)
    TextView mPetAgeTV;

    @Bind(R.id.hasVaccinations)
    ImageView mHasVaccinationsIV;

    @Bind(R.id.hasMicrochip)
    ImageView mHasMicrochipIV;

    @Bind(R.id.isNeutered)
    ImageView mIsNeuteredIV;

    @Bind(R.id.details)
    TextView mPetDetailsTV;

    @Bind(R.id.map)
    MapView mMapView;

    @Bind(R.id.adoptionFeeLayout)
    LinearLayout mAdoptionFeeLayout;

    @Bind(R.id.adoptionFee)
    AppCompatTextView mAdoptionFeeTV;

    @Bind(R.id.fabCall)
    FloatingActionButton mFab;

    @Bind(R.id.card_info)
    CardView mCardInfo;

    // EDIT STUFF
    @Bind(R.id.editDetails)
    AppCompatEditText mEditDetailsET;

    @Bind(R.id.updateLocation)
    Button mUpdateLocation;

    @Bind(R.id.card_edit_info)
    CardView mCardEditInfo;

    @Bind(R.id.editHasMicrochip)
    SwitchCompat mEditHasMicrochip;

    @Bind(R.id.editHasVaccinations)
    SwitchCompat mEditHasVaccinations;

    @Bind(R.id.editIsNeutered)
    SwitchCompat mEditIsNeutered;

    @Bind(R.id.editPetAge)
    AppCompatEditText mEditPetAge;

    @Bind(R.id.editAdoptionFee)
    AppCompatEditText mEditAdoptionFee;

    // TOOLBAR STUFF
    @Bind(R.id.card_disable_pet)
    CardView mDisableListingLayout;

    @Bind(R.id.disableListing)
    SwitchCompat mDisableListing;

    @Bind(R.id.fabDone)
    FloatingActionButton mFabDone;

    GoogleMap mGoogleMap;
    Animal mAnimal;
    String pObjectID;
    String mPictureUrl;
    User mCurrentUser;
    boolean isEditMode;
    LatLng mSelectedLocation;

    FlipAnimation fowardAnimation;
    FlipAnimation backAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();

        Bundle extras = getIntent().getExtras();

        fowardAnimation = new FlipAnimation(mFab, mFabDone);
        backAnimation = new FlipAnimation(mFabDone, mFab);
        mFabDone.setVisibility(View.GONE);
        backAnimation.reverse();

        if (extras != null) {
            pObjectID = extras.getString(Const.OBJECT_ID);
            mPictureUrl = extras.getString(Const.IMAGE_EXTRA);
            loadImage(mPictureUrl, mHeaderImageView);

            int notificationID = extras.getInt(Const.NOTIFICATION_ID);

            if (notificationID != 0) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.cancel(notificationID);
            }
        }

        try {
            mCurrentUser = (User) ParseUser.getCurrentUser();
        } catch (Exception ex) {
            Timber.e(ex, "Could not get Parse user...");
        }

        if (pObjectID == null) {
            Uri data = getIntent().getData();
            if (data != null) {
                pObjectID = data.getQueryParameter("id");
            }
        }

        queryAnimal();
    }

    @OnCheckedChanged(R.id.disableListing)
    public void enableDisableListing() {
        mAnimal.setDisabled(mDisableListing.isChecked());
        mAnimal.toParseObject(mAnimal);
        mAnimal.saveInBackground();
    }

    public void queryAnimal() {
        if (pObjectID != null) {
            ParseQuery<Animal> query = Animal.getQuery();
            query.include("user");
            query.getInBackground(pObjectID, this);
            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
        }
    }

    public void changeFabIcon() {
        mFab.setImageResource(isEditMode ? R.drawable.ic_action_done : R.drawable.ic_edit);
//        mFab.setImageResource(R.drawable.ic_edit);
        int color = getResources().getColor(isEditMode ? R.color.green_500 : R.color.accent_color);
        mFab.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void animateFab() {
        if (isEditMode) {
            mFab.startAnimation(backAnimation);
            mFabDone.startAnimation(backAnimation);
        } else {
            mFab.startAnimation(fowardAnimation);
            mFabDone.startAnimation(fowardAnimation);
        }

    }

    @OnClick(R.id.fabDone)
    public void savePet() {
        handleFabPress();
    }

    @OnClick(R.id.fabCall)
    public void call(View v) {
        if (mCurrentUser != null && mCurrentUser.equals(mAnimal.getUser())) {
            handleFabPress();
        } else {
            if (mAnimal != null) {
                User u = mAnimal.getUser();
                makePhoneCall(u.getPhoneNumber());
            }
        }
    }

    public void handleFabPress() {
//        animateFab();
        isEditMode = !isEditMode;
        changeFabIcon();
        if (!isEditMode) {
            // updatePet
            mAnimal.setAge(mEditPetAge.getText().toString());
            mAnimal.setMicrochip(mEditHasMicrochip.isChecked());
            mAnimal.setVaccinations(mEditHasVaccinations.isChecked());
            mAnimal.setNeutered(mEditIsNeutered.isChecked());
            mAnimal.setAdoptionFee(mEditAdoptionFee.getText().toString().replaceAll("\\$", ""));
            mAnimal.setDisabled(mDisableListing.isChecked());
            if (mSelectedLocation != null) {
                mAnimal.setLatitude(mSelectedLocation.latitude);
                mAnimal.setLongitude(mSelectedLocation.longitude);
            }
            showProgressDialog(null, "Updating pet listing...");
            mAnimal.toParseObject(mAnimal);
            mAnimal.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    dismissDialog();
                    if (e == null) {
                        updateAnimal();
                        showSnackbar("Successfully updated pet listing.");
                    }
                }
            });
        }

        // change views to edit mode
        mCardEditInfo.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        mEditDetailsET.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        mUpdateLocation.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

        mCardInfo.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        mPetDetailsTV.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.updateLocation)
    public void updateLocation(View v) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                mMapView.setVisibility(View.VISIBLE);
                Place place = PlacePicker.getPlace(this, data);
                mSelectedLocation = place.getLatLng();
                setLocation();
            }
        }
    }

    @Override
    public void done(Animal object, ParseException e) {
        mAnimal = object.fromParseObject(object);
        updateAnimal();
    }

    public void updateAnimal() {
        if (mCurrentUser != null && mCurrentUser.equals(mAnimal.getUser())) {
            mDisableListingLayout.setVisibility(View.VISIBLE);
            mDisableListing.setChecked(mAnimal.isDisabled());
            changeFabIcon();
        }

        if (mAnimal.getAdoptionFee() != null) {
            mAdoptionFeeLayout.setVisibility(View.VISIBLE);
            String text = String.format(Locale.getDefault(), "$ %s", mAnimal.getAdoptionFee());
            mAdoptionFeeTV.setText(text);
            mEditAdoptionFee.setText(text);
            mEditAdoptionFee.setVisibility(View.VISIBLE);
        }

        if (mPictureUrl == null) {
            mPictureUrl = mAnimal.getPhotos().get(0).getUrl();
            loadImage(mPictureUrl, mHeaderImageView);
        }

        mCollapsingToolbarLayout.setTitle(mAnimal.getName());

        mPetAgeTV.setText(mAnimal.getAge());
        mEditPetAge.setText(mAnimal.getAge());

        setImage(mIsNeuteredIV, mAnimal.isNeutered());
        mEditIsNeutered.setChecked(mAnimal.isNeutered());

        setImage(mHasVaccinationsIV, mAnimal.hasVaccinations());
        mEditHasVaccinations.setChecked(mAnimal.hasVaccinations());

        mEditHasMicrochip.setChecked(mAnimal.hasMicrochip());
        setImage(mHasMicrochipIV, mAnimal.hasMicrochip());

        mPetDetailsTV.setText(mAnimal.getOtherInfo());
        mEditDetailsET.setText(mAnimal.getOtherInfo());
        setLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setLocation();
    }

    public void setImage(ImageView imgView, boolean checked) {
        imgView.setImageResource(checked ? R.drawable.ic_check_yes : R.drawable.ic_check_no);
    }

    @OnClick(R.id.seeMorePhotos)
    public void seeMorePhotos(View v) {
        Intent i = new Intent(this, ActivityPictures.class);
        i.putExtra(Const.TITLE_EXTRA, mAnimal.getName());

        ArrayList<String> urls = new ArrayList<>();

        for (ParseFile pf : mAnimal.getPhotos()) {
            urls.add(pf.getUrl());
        }

        i.putStringArrayListExtra(Const.IMAGE_EXTRA, urls);
        startActivity(i);
    }

    public void setLocation() {
        if (mAnimal != null && mGoogleMap != null) {
            try {
                mGoogleMap.clear();
                LatLng loc = new LatLng(mAnimal.getLatitude(), mAnimal.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(loc));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, mGoogleMap.getCameraPosition().zoom));
            } catch (Exception ex) {
                Timber.d("Error parsing lat/lng");
            }
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_pet_details;
    }
}
