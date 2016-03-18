package com.gabilheri.pawsalert.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.view.MenuItem;

import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.ui.home.HomeActivity;
import com.gabilheri.pawsalert.ui.home.PetListFragment;

import java.util.ArrayList;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/17/16.
 */
public class ActivityNotificationPetList extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        enableBackNav();

        if (extras != null) {
            ArrayList<String> ids = extras.getStringArrayList(Const.OBJECT_ID);
            int notificationID = extras.getInt(Const.NOTIFICATION_ID);
            PetListFragment petListFragment = PetListFragment.newInstance(ids);

            if (notificationID != 0) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.cancel(notificationID);
            }

            addFragmentToContainer(petListFragment, "PetsFragment");
        } else {
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, HomeActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
