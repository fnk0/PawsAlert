package com.gabilheri.pawsalert.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/17/16.
 */
public class BootReceiver extends BroadcastReceiver {

    GeofencesAlarm canopeoAlarm = new GeofencesAlarm();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            canopeoAlarm.setAlarm(context);
        }
    }
}
