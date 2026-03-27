package com.wswdteam.txtlauncher;

import androidx.appcompat.app.AppCompatDelegate;

public class application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

}
