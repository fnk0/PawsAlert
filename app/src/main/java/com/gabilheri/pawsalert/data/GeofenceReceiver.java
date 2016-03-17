package com.gabilheri.pawsalert.data;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.details.ActivityDetails;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

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
public class GeofenceReceiver extends WakefulBroadcastReceiver implements FindCallback<Animal> {

    final static String GROUP_PETS_MISSING = "group_pets_missing";

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive(context, intent)");
        mContext = context;
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event != null){
            if(event.hasError()){
                Timber.d("Geofence Error:" + event.getErrorCode());
            } else {
                int transition = event.getGeofenceTransition();
                if(transition == Geofence.GEOFENCE_TRANSITION_ENTER
                        || transition == Geofence.GEOFENCE_TRANSITION_DWELL
                        || transition == Geofence.GEOFENCE_TRANSITION_EXIT){
                    String[] geofenceIds = new String[event.getTriggeringGeofences().size()];
                    for (int index = 0; index < event.getTriggeringGeofences().size(); index++) {
                        geofenceIds[index] = event.getTriggeringGeofences().get(index).getRequestId();
                    }
                    if (transition == Geofence.GEOFENCE_TRANSITION_ENTER
                            || transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                        // Entered Geofence area

                        List<ParseQuery<Animal>> queries = new ArrayList<>();
                        for(String s : geofenceIds) {
                            ParseQuery<Animal> aQuery = Animal.getQuery()
                                    .whereEqualTo(Const.OBJECT_ID, s);
                            queries.add(aQuery);
                        }

                        ParseQuery<Animal> mainQuery = ParseQuery.or(queries);
                        mainQuery.include("user");
                        mainQuery.findInBackground(this);
                    } else {
                        // Exited geofence area
                        Timber.d("Exited Geofence area.");
                    }
                }
            }
        }
    }

    @Override
    public void done(List<Animal> objects, ParseException e) {
        if (e == null) {

            List<Notification> notifications = new ArrayList<>();
            int[] ids = new int[objects.size()];
            int counter = 0;
            for(Animal a : objects) {
                Animal animal = a.fromParseObject(a);

                int notificationID = animal.getObjectId().hashCode();
                Intent intent = new Intent(mContext, ActivityDetails.class);
                intent.putExtra(Const.OBJECT_ID, animal.getObjectId());
                intent.putExtra(Const.IMAGE_EXTRA, animal.getPhotos().get(0).getUrl());
                intent.putExtra(Const.NOTIFICATION_ID, notificationID);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, counter, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                ids[counter] = notificationID;
                counter++;
                // Build the notification, setting the group appropriately
                Notification notif = new NotificationCompat.Builder(mContext)
                        .setContentTitle("Missing Pet: " + animal.getName())
                        .setContentText("Tap for more details.")
                        .setSmallIcon(R.drawable.ic_paw_print)
                        .setGroup(GROUP_PETS_MISSING)
                        .setContentIntent(contentIntent)
                        .build();

                notifications.add(notif);
            }
            sendNotification(notifications, ids);
        } else {
            Timber.e(e, "Error fetching data: " + e.getLocalizedMessage());
        }
    }

    public void sendNotification(List<Notification> notifications, int[] ids) {

        // Issue the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        for(int i = 0; i < notifications.size(); i++) {
            notificationManager.notify(ids[i],notifications.get(i));
        }
    }

}
