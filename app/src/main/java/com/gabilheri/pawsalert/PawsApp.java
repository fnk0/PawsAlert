package com.gabilheri.pawsalert;

import android.app.Application;
import android.content.Intent;

import com.gabilheri.pawsalert.data.ServiceUpdateGeofences;
import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.AnimalShelter;
import com.gabilheri.pawsalert.data.models.Favorite;
import com.gabilheri.pawsalert.data.models.SuccessStory;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Keys;
import com.parse.Parse;
import com.parse.ParseObject;

import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/17/16.
 */
public class PawsApp extends Application {

    private static PawsApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Keys.PARSE_APPLICATION_ID, Keys.PARSE_KEY);

        ParseObject.registerSubclass(Animal.class);
        ParseObject.registerSubclass(Favorite.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(AnimalShelter.class);
        ParseObject.registerSubclass(SuccessStory.class);
        mInstance = this;

        Intent service = new Intent(this, ServiceUpdateGeofences.class);
        startService(service);

    }

    public static PawsApp instance() {
        return mInstance;
    }
}
