package com.gabilheri.pawsalert.ui.details;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.canelmas.let.AskPermission;
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
import com.parse.ParseQuery;

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

    @Bind(R.id.petDetails)
    TextView mPetDetailsTV;

    @Bind(R.id.map)
    MapView mMapView;

    GoogleMap mGoogleMap;
    Animal mAnimal;
    String pObjectID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            pObjectID = extras.getString(Const.OBJECT_ID);

            String pictureUrl = extras.getString(Const.PET_IMAGE);
            loadImage(pictureUrl);

            ParseQuery<Animal> query = Animal.getQuery();
            query.include("user");
            query.getInBackground(pObjectID, this);

            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
        }
    }

    @AskPermission(Manifest.permission.CALL_PHONE)
    @OnClick(R.id.fabCall)
    public void call(View v) {
        if (mAnimal != null) {
            try {
                User u = mAnimal.getUser();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + u.getPhoneNumber()));
                startActivity(callIntent);
            } catch (SecurityException ex) {
                Timber.e(ex, ex.getLocalizedMessage());
            }
        }
    }

    public void loadImage(String url) {
        Glide.with(this)
                .load(url)
                .into(mHeaderImageView);
    }

    @Override
    public void done(Animal object, ParseException e) {
        mAnimal = object.fromParseObject(object);
        mCollapsingToolbarLayout.setTitle(mAnimal.getName());
        String years = "years";
        if (mAnimal.getAge() < 2) {
            years = "year";
        }

        mPetAgeTV.setText(String.format(Locale.getDefault(), "%d %s", mAnimal.getAge(), years));

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
        imgView.setImageResource(checked ? R.drawable.ic_checked : R.drawable.ic_unchecked);
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
