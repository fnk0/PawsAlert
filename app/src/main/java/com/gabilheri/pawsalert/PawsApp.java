package com.gabilheri.pawsalert;

import android.app.Application;

import com.gabilheri.pawsalert.data.models.Animal;
import com.gabilheri.pawsalert.data.models.Favorite;
import com.gabilheri.pawsalert.data.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.squareup.leakcanary.LeakCanary;

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
        LeakCanary.install(this);
        Timber.plant(new Timber.DebugTree());

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Rh49lFj76OsfTBrChoM2wn55Lp0raNoMFNrj8LmJ", "k8ALeQtdeP5CZQtvKCWOim6JOC6c3jvG1v56GDaq");

        ParseObject.registerSubclass(Animal.class);
        ParseObject.registerSubclass(Favorite.class);
        ParseObject.registerSubclass(User.class);
        mInstance = this;
    }

    public static PawsApp instance() {
        return mInstance;
    }
}
