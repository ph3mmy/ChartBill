package com.jcedar.tixee.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Afolayan on 23/1/2016.
 */
public class Tixee extends MultiDexApplication{


    public Tixee() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
