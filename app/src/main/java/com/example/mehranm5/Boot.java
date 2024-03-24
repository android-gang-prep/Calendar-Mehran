package com.example.mehranm5;

import static com.example.mehranm5.MainActivity.updateWidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mehranm5.database.AppDatabase;
import com.example.mehranm5.ui.month.EventModel;

import java.util.ArrayList;
import java.util.List;

public class Boot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        updateWidget(context,MonthWidget.class);
        updateWidget(context,EventsWidget.class);
    }
}
