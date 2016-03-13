package com.gabilheri.pawsalert.ui.details;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;
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
import com.parse.ParseQuery;

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

            String pictureUrl = extras.getString(Const.IMAGE_EXTRA);
            loadImage(pictureUrl, mHeaderImageView);

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
