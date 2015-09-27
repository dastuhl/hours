package de.dastuhl.hours;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Martin on 25.09.2015.
 */
public class Hours extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        //Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
    }

}
