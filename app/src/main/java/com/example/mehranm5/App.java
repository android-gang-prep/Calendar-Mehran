package com.example.mehranm5;

import android.app.Application;

import java.io.File;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        File dexOutputDir = getCodeCacheDir();
        dexOutputDir.setReadOnly();
        ShareHelper.newShareHelper(getApplicationContext());
    }
}
