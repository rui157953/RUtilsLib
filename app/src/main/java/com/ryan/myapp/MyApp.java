package com.ryan.myapp;

import android.app.Application;

import com.ryan.rutilslib.CrashHandler;

/**
 * Created by Ryan on 2017/12/4.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
