package com.gabilheri.pawsalert.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseDrawerPagerActivity;
import com.gabilheri.pawsalert.base.PrefManager;
import com.gabilheri.pawsalert.data.GeofencesAlarm;
import com.gabilheri.pawsalert.data.ServiceUpdateGeofences;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.add.AddPetActivity;
import com.gabilheri.pawsalert.ui.details.ActivityDetails;
import com.gabilheri.pawsalert.ui.sign_in.SignInActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/18/16.
 */
public class HomeActivity extends BaseDrawerPagerActivity
        implements View.OnClickListener, OnMapReadyCallback, FindCallback<Animal> {

    public static final int ADD_PET = 100;

    PetListFragment mMissingFragment;
    PetListFragment mAdoptFragment;
//    PetListFragment mFavoritesFragment;

    GoogleMap mGoogleMap;
    MapFragment mMapFragment;
    List<Animal> mItems;

    boolean mapInit = false;

    BitmapDescriptor redMarker = null;
    BitmapDescriptor blueMarker = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableFab(true, this);

        if (!PrefManager.with(this).getBoolean(Const.SERVICE_STARTED, false)) {
            GeofencesAlarm alarm = new GeofencesAlarm();
            alarm.setAlarm(this);
            PrefManager.with(this).save(Const.SERVICE_STARTED, true);
        }

        float radius = PrefManager.with(this).getFloat("notification_range", 1f) * ServiceUpdateGeofences.MILE;
        Timber.d("Radius: " + radius);

        mMissingFragment = PetListFragment.newInstance(PetListFragment.FRAGMENT_MISSING, null);
        mAdoptFragment = PetListFragment.newInstance(PetListFragment.FRAGMENT_ADOPT, null);
//        mFavoritesFragment = PetListFragment.newInstance(PetListFragment.FRAGMENT_FAVORITE, null);

        mMapFragment = MapFragment.newInstance();
        mMapFragment.getMapAsync(this);

        addFragment(getString(R.string.missing), mMissingFragment);
        addFragment(getString(R.string.adopt), mAdoptFragment);
//        addFragment(getString(R.string.favorites), mFavoritesFragment);
        addFragment("MAP", mMapFragment);

        super.initPager();
        mNavigationView.setCheckedItem(R.id.home);
        mItems = new ArrayList<>();
        initMapData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (mCurrentUser != null) {
            Intent intent = new Intent(this, AddPetActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.putExtra(Const.TRANSITION_LAYOUT, true);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, mFloatingActionButton, Const.TRANSITION_LAYOUT);
                startActivityForResult(intent, ADD_PET, options.toBundle());
            } else {
                startActivityForResult(intent, ADD_PET);
            }
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.putExtra(Const.TRANSITION_LAYOUT, true);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, mFloatingActionButton, Const.TRANSITION_LAYOUT);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        }
    }

    void initMapData() {
        Animal.getQuery()
                .include("user")
                .findInBackground(this);
    }

    @Override
    public void done(List<Animal> objects, ParseException e) {
        mItems.clear();
        for(Animal a : objects) {
            mItems.add(a.fromParseObject(a));
        }
        initMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        CameraPosition position = new CameraPosition.Builder()
                .zoom(13f)
                .target(new LatLng(36.1157, -97.0586))
                .build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);

        mGoogleMap.moveCamera(update);

        initMap();
    }

    public void initMap() {
        if (mGoogleMap != null && mItems != null && mItems.size() != 0 && !mapInit) {
            mapInit = true;

            final HashMap<Marker, Animal> markerMap = new HashMap<>();

            redMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_location_red);
            blueMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_location_blue);

            for(Animal item : mItems) {

                BitmapDescriptor icon = item.isMissing() ? redMarker : blueMarker;

                LatLng m = new LatLng(item.getLatitude(), item.getLongitude());
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(m)
                        .icon(icon)
                        .snippet("Click on this window for more details.")
                        .title(item.getName())
                );

                markerMap.put(marker, item);

                mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        startDetailActivity(markerMap.get(marker));
                    }
                });
            }

        }
    }

    public void startDetailActivity(Animal animal) {
        Intent intent = new Intent(this, ActivityDetails.class);
        intent.putExtra(Const.OBJECT_ID, animal.getObjectId());
        String picUrl = animal.getPhotos().get(0).getUrl();
        intent.putExtra(Const.IMAGE_EXTRA, picUrl);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mMissingFragment.queryData();
            mAdoptFragment.queryData();
        }
    }
}
