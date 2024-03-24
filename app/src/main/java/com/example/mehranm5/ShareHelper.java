package com.example.mehranm5;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareHelper {
    public SharedPreferences preferences;

    public SharedPreferences.Editor editor;

    private static ShareHelper shareHelper;

    public static ShareHelper getShareHelper() {
        return shareHelper;
    }

    public ShareHelper(Context context) {
        this.preferences = context.getSharedPreferences("data", 0);
        this.editor = preferences.edit();
    }

    public static void newShareHelper(Context context) {
        shareHelper = new ShareHelper(context);
    }
}
