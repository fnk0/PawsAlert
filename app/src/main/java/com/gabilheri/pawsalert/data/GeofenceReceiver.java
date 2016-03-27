package com.gabilheri.pawsalert.data;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.PrefManager;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.details.ActivityDetails;
import com.gabilheri.pawsalert.ui.notification.ActivityNotificationPetList;
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
    final  static int GROUP_NOTIFICATION_ID = 8791;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive(context, intent)");

        if (!PrefManager.with(context).getBoolean("notifications_enale", true)) {
            return;
        }

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
                        Timber.d("Entered Geofence area");
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
                        removeNotifications(context, geofenceIds);
                    }
                }
            }
        }
    }

    @Override
    public void done(List<Animal> objects, ParseException e) {
        // Nuke all previous notifications and generate unique ids
        NotificationManagerCompat.from(mContext).cancelAll();
        if (e == null) {
            List<Notification> notifications = new ArrayList<>();
            int[] ids = new int[objects.size()];
            int counter = 0;
            ArrayList<String> objectIds = new ArrayList<>();
            for(Animal a : objects) {
                Animal animal = a.fromParseObject();

                objectIds.add(animal.getObjectId());

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
                        .setContentText("Type: " + animal.getPetType())
                        .setSmallIcon(R.drawable.ic_paw_print)
                        .setGroup(GROUP_PETS_MISSING)
                        .setContentIntent(contentIntent)
                        .build();

                notifications.add(notif);
            }
            sendNotification(notifications, ids, objectIds);
        } else {
            Timber.e(e, "Error fetching data: " + e.getLocalizedMessage());
        }
    }

    public void removeNotifications(Context c,  String[] objectIds) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
        for(String s : objectIds) {
            notificationManager.cancel(s.hashCode());
        }
    }

    public void sendNotification(List<Notification> notifications, int[] ids, ArrayList<String> objectIds) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        if (notifications.size() > 1) {
            Intent intent = new Intent(mContext, ActivityNotificationPetList.class);
            intent.putExtra(Const.OBJECT_ID, objectIds);
            intent.putExtra(Const.NOTIFICATION_ID, GROUP_NOTIFICATION_ID);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(mContext, ids.length + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Group notification that will be visible on the phone
            Notification summaryNotification = new NotificationCompat.Builder(mContext)
                    .setContentTitle("There are " + notifications.size() + " pets missing in your area.")
                    .setSmallIcon(R.drawable.ic_paw_print)
                    .setGroup(GROUP_PETS_MISSING)
                    .setGroupSummary(true)
                    .setContentIntent(contentIntent)
                    .build();

            // Issue the notification
            notificationManager.notify(GROUP_NOTIFICATION_ID, summaryNotification);
        }

        for(int i = 0; i < notifications.size(); i++) {
            notificationManager.notify(ids[i],notifications.get(i));
        }
    }

}
