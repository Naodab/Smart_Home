package com.smarthome.mobile.app;

import android.app.Application;

import com.smarthome.mobile.network.SessionManager;

public class MyApp extends Application {
    private static MyApp instance;
    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sessionManager = new SessionManager(this);
    }

    public static MyApp getInstance() {
        return instance;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
