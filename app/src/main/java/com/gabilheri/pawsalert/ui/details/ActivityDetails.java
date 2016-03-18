package com.gabilheri.pawsalert.ui.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
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

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
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

    GoogleMap mGoogleMap;
    Animal mAnimal;
    String pObjectID;
    String mPictureUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();

        Bundle extras = getIntent().getExtras();

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

        if (pObjectID == null) {
            Uri data = getIntent().getData();
            if (data != null) {
                pObjectID = data.getQueryParameter("id");
            }
        }

        if (pObjectID != null) {
            ParseQuery<Animal> query = Animal.getQuery();
            query.include("user");
            query.getInBackground(pObjectID, this);

            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
        }
    }

    @OnClick(R.id.fabCall)
    public void call(View v) {
        if (mAnimal != null) {
            User u = mAnimal.getUser();
            makePhoneCall(u.getPhoneNumber());
        }
    }

    @Override
    public void done(Animal object, ParseException e) {
        mAnimal = object.fromParseObject(object);

        if (mAnimal.getAdoptionFee() != null) {
            mAdoptionFeeLayout.setVisibility(View.VISIBLE);
            mAdoptionFeeTV.setText(String.format(Locale.getDefault(), "$ %s.00", mAnimal.getAdoptionFee()));
        }

        if (mPictureUrl == null) {
            mPictureUrl = mAnimal.getPhotos().get(0).getUrl();
            loadImage(mPictureUrl, mHeaderImageView);
        }


        mCollapsingToolbarLayout.setTitle(mAnimal.getName());
        mPetAgeTV.setText(mAnimal.getAge());
        setImage(mIsNeuteredIV, mAnimal.isNeutered());
        setImage(mHasVaccinationsIV, mAnimal.hasVaccinations());
        setImage(mHasMicrochipIV, mAnimal.hasMicrochip());
        mPetDetailsTV.setText(mAnimal.getOtherInfo());
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

        for(ParseFile pf : mAnimal.getPhotos()) {
            urls.add(pf.getUrl());
        }

        i.putStringArrayListExtra(Const.IMAGE_EXTRA, urls);
        startActivity(i);
    }

    public void setLocation() {
        if (mAnimal != null && mGoogleMap != null) {
            try {
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
