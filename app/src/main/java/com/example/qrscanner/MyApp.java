package com.example.qrscanner;

import android.app.Application;


public class MyApp extends Application {
    private static MyApp INSTANCE;

    public static MyApp getInstance() {
        if (INSTANCE == null) INSTANCE = new MyApp();
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

}
