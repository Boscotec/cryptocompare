package com.boscotec.crypyocompare;

import android.app.Application;
import timber.log.Timber;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){ Timber.plant(new Timber.DebugTree());}
    }
}
