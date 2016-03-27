package com.gabilheri.pawsalert.data;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.PrefManager;
import com.gabilheri.pawsalert.data.models.Animal;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/17/16.
 */
public class ServiceUpdateGeofences extends IntentService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final float MILE = 1609.34f;

    GoogleApiClient mGoogleApiClient;
    List<Geofence> mGeofences;
    GeofencingRequest mGeofencingRequest;

    public ServiceUpdateGeofences() {
        super(ServiceUpdateGeofences.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            mGeofences = new ArrayList<>();
            List<Animal> animals = Animal.getQuery()
                    .whereEqualTo("missing", true)
                    .include("user")
                    .find();

            float radius = PrefManager.with(this).getFloat("notification_range", 1f) * MILE;
            for (Animal a : animals) {
                a = a.fromParseObject();
                Geofence geofence = new Geofence.Builder()
                        .setCircularRegion(a.getLatitude(), a.getLongitude(), radius)
                        .setRequestId(a.getObjectId()) // every fence must have an ID
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT) // can also have DWELL
                        .setExpirationDuration(Geofence.NEVER_EXPIRE) // how long do we care about this geofence?
                        //.setLoiteringDelay(60000) // 1 min.
                        .build();

                mGeofences.add(geofence);
            }

            if (mGeofences.size() != 0) {
                mGeofencingRequest = new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofences(mGeofences)
                        .build();

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
                mGoogleApiClient.connect();
            }
        } catch (ParseException ex) {
            Timber.e(ex, "Could not fetch animal data: " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, GeofenceReceiver.class);
        intent.setAction("geofence_transition_action");
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, R.id.geofence_transition_intent, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofencingRequest, pendingIntent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
