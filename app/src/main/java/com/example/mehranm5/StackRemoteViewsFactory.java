package com.example.mehranm5;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.mehranm5.database.AppDatabase;
import com.example.mehranm5.ui.month.EventModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<EventModel> widgetItems = new ArrayList<EventModel>();
    private Context context;
    private int appWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        widgetItems.clear();
        widgetItems.addAll(AppDatabase.getAppDatabase(context).eventDao().getAllEvents(System.currentTimeMillis()));
    }

    @Override
    public void onDataSetChanged() {
        widgetItems.clear();
        widgetItems.addAll(AppDatabase.getAppDatabase(context).eventDao().getAllEvents(System.currentTimeMillis()));
    }

    @Override
    public void onDestroy() {
        widgetItems.clear();
    }

    @Override
    public int getCount() {
        return widgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        @SuppressLint("RemoteViewLayout") RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(widgetItems.get(position).getDate());
        Calendar calendarPrev = new GregorianCalendar();
        if (position > 0)
            calendarPrev.setTimeInMillis(widgetItems.get(position - 1).getDate());
        rv.setViewVisibility(R.id.startMonth, (position == 0 || (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun") && !calendarPrev.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun"))) ? View.VISIBLE : View.GONE);

        if ((position == 0 || (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun") && !calendarPrev.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun")))) {
            String startMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            int startDay = calendar.get(Calendar.DAY_OF_MONTH);

            int stopDay = 0;
            String stopMonth = "";

            for (int i = 0; i < 10; i++) {
                if (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sat")) {
                    stopMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
                    stopDay = calendar.get(Calendar.DAY_OF_MONTH);
                    break;
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            String res = "";
            if (startDay == stopDay) {
                res = startMonth + " " + startDay;
            } else if (startMonth.equals(stopMonth)) {
                res = startMonth + " " + startDay + " - " + stopDay;
            } else {
                res = startMonth + " " + startDay + " - " + stopMonth + " " + stopDay;

            }
            rv.setTextViewText(R.id.startMonth, res);
        }
        rv.setTextViewText(R.id.title, widgetItems.get(position).getTitle());

        rv.setTextViewText(R.id.time, calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + " " + (calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            int color = R.color.black;
            switch (widgetItems.get(position).getEvent()) {
                case "job":
                    color = R.color.job;
                    break;
                case "birthday":
                    color = R.color.birthday;
                    break;
                case "reminder":
                    color = R.color.reminder;
                    break;
                case "education":
                    color = R.color.education;
                    break;
            }
            rv.setColorStateList(R.id.back, "setBackgroundTintList", color);
        }
        rv.setViewVisibility(R.id.dayDisplay, View.GONE);
        rv.setViewVisibility(R.id.day, View.GONE);

        calendar.setTimeInMillis(widgetItems.get(position).getDate());


        if (position == 0 || !widgetItems.get(position - 1).getDay().equals(widgetItems.get(position).getDay())) {
            rv.setTextViewText(R.id.day, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            rv.setTextViewText(R.id.dayDisplay, calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            rv.setViewVisibility(R.id.day, View.VISIBLE);
            rv.setViewVisibility(R.id.dayDisplay, View.VISIBLE);

        }

        return rv;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}