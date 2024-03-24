package com.example.mehranm5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mehranm5.database.AppDatabase;
import com.example.mehranm5.ui.month.EventModel;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CalendarMonthView extends View {


    public CalendarMonthView(Context context) {
        super(context);
        init();
    }

    public CalendarMonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public CalendarMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CalendarMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private String days[] = {"S", "M", "T", "W", "T", "F", "S"};
    Calendar calendar;
    long current = System.currentTimeMillis();
    String currentday = "";

    TextPaint titleDaysPaint;
    TextPaint daysPaint;
    Paint line;

    Paint currentPaint;

    Gson gson;
    List<EventModel> eventModels;
    List<EventModel> eventsM;

    Paint eventPaint;
    TextPaint eventTextPaint;

    private void init() {
        gson = new Gson();
        eventsM = new ArrayList<>();
        eventModels = new ArrayList<>();
        titleDaysPaint = new TextPaint();
        titleDaysPaint.setColor(Color.parseColor("#434343"));
        titleDaysPaint.setTextSize(dpToPx(12));
        titleDaysPaint.setTextAlign(Paint.Align.CENTER);

        eventTextPaint = new TextPaint();
        eventTextPaint.setColor(Color.parseColor("#FFFFFF"));
        eventTextPaint.setTextSize(dpToPx(8));
        eventTextPaint.setTextAlign(Paint.Align.CENTER);

        daysPaint = new TextPaint();
        daysPaint.setColor(Color.parseColor("#434343"));
        daysPaint.setTextSize(dpToPx(14));
        daysPaint.setTextAlign(Paint.Align.CENTER);

        line = new Paint();
        line.setColor(Color.parseColor("#E3E0E7"));
        line.setStrokeWidth(dpToPx(1));

        eventPaint = new Paint();
        currentPaint = new Paint();
        currentPaint.setColor(Color.parseColor("#475D96"));

        calendar = new GregorianCalendar();


        Calendar calendarNow = new GregorianCalendar();
        currentday = calendarNow.get(Calendar.YEAR) + "" + calendarNow.get(Calendar.MONTH) + "" + calendarNow.get(Calendar.DAY_OF_MONTH);


        setCalendar(current);


    }

    public void setCalendar(long current) {
        this.current = current;
        calendar.setTimeInMillis(current);
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
        eventModels.addAll(AppDatabase.getAppDatabase(getContext()).eventDao().getAll());
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setCalendar(current);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        calendar.setTimeInMillis(current);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());

        if (!dayName.equals("Sun")) {
            for (int i = 0; i < 31; i++) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                if (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sun"))
                    break;
            }
        }

        float widthSplit = getWidth() / 7f;

        float startX = 0;

        for (int i = 0; i < 7; i++) {
            canvas.drawText(days[i], (startX + (widthSplit / 2)), dpToPx(20), titleDaysPaint);
            canvas.drawLine(startX, dpToPx(20), startX, getHeight(), line);
            startX += widthSplit;
        }

        float heightSplit = (getHeight() - dpToPx(30)) / 6f;

        float startY = dpToPx(30);
        for (int i = 0; i < 6; i++) {
            canvas.drawLine(0, startY, getWidth(), startY, line);
            startY += heightSplit;
        }


        startX = 0;
        startY = dpToPx(30);
        for (int i = 0; i < 42; i++) {
            daysPaint.setColor(Color.parseColor("#434343"));
            String key = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH);
            if (key.equals(currentday)) {
                canvas.drawCircle(startX + (widthSplit / 2), startY + dpToPx(15), dpToPx(13), currentPaint);
                daysPaint.setColor(Color.parseColor("#FFFFFF"));
            }


            canvas.drawText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), startX + (widthSplit / 2), startY + dpToPx(20), daysPaint);

            eventsM.clear();
            for (int j = 0; j < eventModels.size(); j++) {
                if (eventModels.get(j).getDay().equals(key))
                    eventsM.add(eventModels.get(j));
            }
            float sizeEvent = dpToPx(10);
            int countEventView = (int) ((heightSplit - dpToPx(30)) / sizeEvent);
            float startYEvent =startY + dpToPx(30);
            for (int j = 0; j < Math.min(eventsM.size(), countEventView); j++) {
                eventPaint.setColor(eventsM.get(j).getEventColor());
                canvas.drawRoundRect(startX+dpToPx(1),startYEvent,startX+widthSplit-dpToPx(1),startYEvent+dpToPx(10),10,10,eventPaint);
                canvas.drawText(eventsM.get(j).getTitle(),startX+dpToPx(15),startYEvent+dpToPx(8),eventTextPaint);
                startYEvent+=dpToPx(10.5f);
            }
            if (calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).equals("Sat")) {
                startY += heightSplit;
                startX = 0;
            } else
                startX += widthSplit;

            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }


    }

    private float dpToPx(float dp) {
        return getResources().getDisplayMetrics().density * dp;
    }
}
