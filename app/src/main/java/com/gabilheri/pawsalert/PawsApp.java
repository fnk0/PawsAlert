package com.gabilheri.pawsalert;

import android.app.Application;

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
        mInstance = this;
    }

    public static PawsApp instance() {
        return mInstance;
    }
}
