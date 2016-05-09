package com.gabilheri.pawsalert.ui.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.queryManagers.AnimalShelterManager;
import com.gabilheri.pawsalert.helpers.Const;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.paypal.android.sdk.payments.PayPalService;

import java.math.BigDecimal;
import java.util.Locale;

import butterknife.Bind;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/12/16.
 */
public class ActivityShelterDetails extends BaseActivity
        implements AnimalShelterManager.AnimalShelterCallback, OnMapReadyCallback, View.OnClickListener {

    @Bind(R.id.shelterImage)
    AppCompatImageView mShelterImageIV;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.locationTV)
    AppCompatTextView mLocationTV;

    @Bind(R.id.details)
    AppCompatTextView mDetailsTV;

    @Bind(R.id.shelterHours)
    AppCompatTextView mShelterHours;

    @Bind(R.id.map)
    MapView mMapView;

    @Bind(R.id.detailsTitle)
    AppCompatTextView mDetailsTitleTV;

    @Bind(R.id.actionShare)
    LinearLayout mActionShare;

    @Bind(R.id.actionCall)
    LinearLayout mActionCall;

    @Bind(R.id.actionEmail)
    LinearLayout mActionEmail;

    @Bind(R.id.actionWeb)
    LinearLayout mActionWeb;

    GoogleMap mGoogleMap;
    AnimalShelter mAnimalShelter;
    String pObjectID;

    MaterialDialog mDonateDialog;
    BigDecimal mDonateAmount;

    AnimalShelterManager mAnimalShelterManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();
        Bundle extras = getIntent().getExtras();
        mAnimalShelter = new AnimalShelter();
        mActionEmail.setOnClickListener(this);
        mActionShare.setOnClickListener(this);
        mActionCall.setOnClickListener(this);
        mActionWeb.setOnClickListener(this);

        mAnimalShelterManager = new AnimalShelterManager(this);

        if (extras != null) {
            pObjectID = extras.getString(Const.OBJECT_ID);

            String pictureUrl = extras.getString(Const.IMAGE_EXTRA);
            loadImage(pictureUrl, mShelterImageIV);

            mAnimalShelterManager.getAnimalShelter(pObjectID);

            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionCall:
                makePhoneCall(mAnimalShelter.getPhoneNumber());
                break;
            case R.id.actionShare:
                shareURL("http://www.stillwaterpaws.com/shelter.html?id=" + mAnimalShelter.getObjectId());
                break;
            case R.id.actionEmail:
                sendEmail(mAnimalShelter.getEmail());
                break;
            case R.id.actionWeb:
                String url = mAnimalShelter.getWebsite();
                if (!url.contains("http")) {
                    url = "http://" + url;
                }
                openURL(url);
                break;
        }
    }

    @Override
    public void onAnimalShelter(AnimalShelter animalShelter) {
        mAnimalShelter = animalShelter;
        mAnimalShelter = mAnimalShelter.fromParseObject();
        mLocationTV.setText(mAnimalShelter.getAddress());
        mCollapsingToolbarLayout.setTitle(mAnimalShelter.getShelterName());
        mShelterHours.setText(String.format(
                Locale.getDefault(), "%s - %s", mAnimalShelter.getOpenTime(), mAnimalShelter.getCloseTime()));
        mDetailsTitleTV.setText(getString(R.string.about));
        mDetailsTV.setText(mAnimalShelter.getShelterDescription());
        setLocation();
    }

    @Override
    public void onErrorFetchingAnimalShelter(Exception ex) {
        showSnackbar("Error fetching animal shelter. Please try again later.");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setLocation();
    }

    public void setLocation() {
        if (mAnimalShelter != null && mGoogleMap != null) {
            try {
                LatLng loc = new LatLng(mAnimalShelter.getLatitude(), mAnimalShelter.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(loc));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, mGoogleMap.getCameraPosition().zoom));
            } catch (Exception ex) {
                Timber.d("Error parsing lat/lng");
            }
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_shelter_details;
    }
}
