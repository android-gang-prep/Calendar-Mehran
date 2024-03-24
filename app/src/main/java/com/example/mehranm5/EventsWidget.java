package com.example.mehranm5;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class EventsWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.events_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        Intent intent = new Intent(context, StackWidgetService.class);
        views.setRemoteAdapter(R.id.list, intent);

        Intent add = new Intent(context, MainActivity.class);
        add.setAction("ADD");
        views.setScrollPosition(R.id.list, 100);

        views.setOnClickPendingIntent(R.id.add, PendingIntent.getActivity(context, 0, add, PendingIntent.FLAG_IMMUTABLE));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, 60000 * 10);
        Intent alarmIntent = new Intent(context, EventsWidget.class);
        alarmIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, EventsWidget.class));
        alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1000, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000 * 10, pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        Intent alarmIntent = new Intent(context, EventsWidget.class);
        alarmIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, EventsWidget.class));
        alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1000, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}