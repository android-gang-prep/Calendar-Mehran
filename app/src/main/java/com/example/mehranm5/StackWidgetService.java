package com.example.mehranm5;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(getApplicationContext(), intent);
    }
}
