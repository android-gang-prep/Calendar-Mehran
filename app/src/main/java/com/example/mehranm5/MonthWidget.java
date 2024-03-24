package com.example.mehranm5;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.mehranm5.database.AppDatabase;
import com.example.mehranm5.ui.month.EventModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MonthWidgetConfigureActivity MonthWidgetConfigureActivity}
 */
public class MonthWidget extends AppWidgetProvider {
    private static float dpToPx(Context context, float dp) {
        return context.getResources().getDisplayMetrics().density * dp;
    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.month_widget);
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        if (width < 1){
            MainActivity.updateWidget(context,MonthWidget.class);
            return;
        }


        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);


        String days[] = {"S", "M", "T", "W", "T", "F", "S"};

        String currentday = "";




        List<EventModel> eventsM = new ArrayList<>();
        List<EventModel> eventModels = new ArrayList<>();
        TextPaint  titleDaysPaint = new TextPaint();
        titleDaysPaint.setColor(Color.parseColor("#434343"));
        titleDaysPaint.setTextSize(dpToPx(context, 3));
        titleDaysPaint.setTextAlign(Paint.Align.CENTER);
        titleDaysPaint.setAntiAlias(true);

        TextPaint eventTextPaint = new TextPaint();
        eventTextPaint.setColor(Color.parseColor("#FFFFFF"));
        eventTextPaint.setTextSize(dpToPx(context, 2));
        eventTextPaint.setTextAlign(Paint.Align.LEFT);
        eventTextPaint.setAntiAlias(true);

        TextPaint daysPaint = new TextPaint();
        daysPaint.setColor(Color.parseColor("#434343"));
        daysPaint.setTextSize(dpToPx(context, 3.5f));
        daysPaint.setTextAlign(Paint.Align.CENTER);
        daysPaint.setAntiAlias(true);

        Paint line = new Paint();
        line.setColor(Color.parseColor("#E3E0E7"));
        line.setStrokeWidth(dpToPx(context, 0.25f));
        line.setAntiAlias(true);

        Paint eventPaint = new Paint();
        eventPaint.setAntiAlias(true);


        Paint currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(Color.parseColor("#475D96"));

        Calendar  calendar = new GregorianCalendar();


        Calendar calendarNow = new GregorianCalendar();
        currentday = calendarNow.get(Calendar.YEAR) + "" + calendarNow.get(Calendar.MONTH) + "" + calendarNow.get(Calendar.DAY_OF_MONTH);
        calendarNow.add(Calendar.MONTH,ShareHelper.getShareHelper().preferences.getInt("addWidget",0));

        views.setTextViewText(R.id.month,calendarNow.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault()));
        calendar.setTimeInMillis(calendarNow.getTimeInMillis());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());


        if (!dayName.equals("Sun")) {
            for (int i = 0; i < 31; i++) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                if (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun"))
                    break;
            }
        }

        eventModels.clear();
        eventModels.addAll(AppDatabase.getAppDatabase(context).eventDao().getAll());

        float widthSplit = width / 7f;

        float startX = 0;

        for (int i = 0; i < 7; i++) {
            canvas.drawText(days[i], (startX + (widthSplit / 2)), dpToPx(context, 5), titleDaysPaint);
            canvas.drawLine(startX, dpToPx(context, 5), startX, height, line);
            startX += widthSplit;
        }

        float heightSplit = (height - dpToPx(context, 7.5f)) / 6f;

        float startY = dpToPx(context, 7.5f);
        for (int i = 0; i < 6; i++) {
            canvas.drawLine(0, startY, width, startY, line);
            startY += heightSplit;
        }


        startX = 0;
        startY = dpToPx(context, 7.5f);
        for (int i = 0; i < 42; i++) {
            daysPaint.setColor(Color.parseColor("#434343"));
            String key = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH);
            if (key.equals(currentday)) {
                canvas.drawCircle(startX + (widthSplit / 2), startY + dpToPx(context, 3.75f), dpToPx(context, 3.25f), currentPaint);
                daysPaint.setColor(Color.parseColor("#FFFFFF"));
            }


            canvas.drawText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), startX + (widthSplit / 2), startY + dpToPx(context, 5), daysPaint);

            eventsM.clear();
            for (int j = 0; j < eventModels.size(); j++) {
                if (eventModels.get(j).getDay().equals(key))
                    eventsM.add(eventModels.get(j));
            }
            float sizeEvent = dpToPx(context, 2.5f);
            int countEventView = (int) ((heightSplit - dpToPx(context, 7.5f)) / sizeEvent);
            float startYEvent = startY + dpToPx(context, 7.5f);
            for (int j = 0; j < Math.min(eventsM.size(), countEventView); j++) {
                eventPaint.setColor(eventsM.get(j).getEventColor());
                canvas.drawRoundRect(startX + dpToPx(context, 0.25f), startYEvent, startX + widthSplit - dpToPx(context, 0.25f), startYEvent + dpToPx(context, 2.5f), 10, 10, eventPaint);
                canvas.drawText(eventsM.get(j).getTitle(), startX + dpToPx(context, 0.25f), startYEvent + dpToPx(context, 2), eventTextPaint);
                startYEvent += dpToPx(context, 2.625f);
            }
            if (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sat")) {
                startY += heightSplit;
                startX = 0;
            } else
                startX += widthSplit;

            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }


        views.setImageViewBitmap(R.id.img, bitmap);
        Intent intent = new Intent(context, MonthWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_ADD");
        views.setOnClickPendingIntent(R.id.next, PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE));

        Intent manfi = new Intent(context, MonthWidget.class);
        manfi.setAction("android.appwidget.action.APPWIDGET_MANFI");
        views.setOnClickPendingIntent(R.id.back, PendingIntent.getBroadcast(context, 0, manfi, PendingIntent.FLAG_IMMUTABLE));


        Intent add = new Intent(context, MainActivity.class);
        add.setAction("ADD");
        views.setOnClickPendingIntent(R.id.add, PendingIntent.getActivity(context, 0, add, PendingIntent.FLAG_IMMUTABLE));

        // Instruct the widget manager to update the widget
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
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            MonthWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, 60000*10);
        Intent alarmIntent = new Intent(context, MonthWidget.class);
        alarmIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, MonthWidget.class));
        alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1001, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000*10, pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        Intent alarmIntent = new Intent(context, MonthWidget.class);
        alarmIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, MonthWidget.class));
        alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1001, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_ADD"))
            ShareHelper.getShareHelper().editor.putInt("addWidget",ShareHelper.getShareHelper().preferences.getInt("addWidget",0)+1).apply();
        else if (intent.getAction().equals("android.appwidget.action.APPWIDGET_MANFI"))
            ShareHelper.getShareHelper().editor.putInt("addWidget",ShareHelper.getShareHelper().preferences.getInt("addWidget",0)-1).apply();

        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_ADD")||intent.getAction().equals("android.appwidget.action.APPWIDGET_MANFI")){
            int[] ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(new ComponentName(context, MonthWidget.class));
            for (int appWidgetId : ids) {
                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId);
            }
        }

    }
}