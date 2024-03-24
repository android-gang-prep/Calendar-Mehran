package com.example.mehranm5.ui.event;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class HeaderView extends View {
    public HeaderView(Context context) {
        super(context);
        init();
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    Paint currentPaint;

    List<HeaderModel> headerModels;
    TextPaint header;
    TextPaint headerM;
    String currentday = "";

    private void init() {
        headerModels=new ArrayList<>();
        header=new TextPaint();
        header.setTextAlign(Paint.Align.CENTER);
        header.setColor(Color.parseColor("#000000"));
        header.setTextSize(dpToPx(20));

        headerM=new TextPaint();
        headerM.setTextAlign(Paint.Align.CENTER);
        headerM.setColor(Color.parseColor("#838383"));
        headerM.setTextSize(dpToPx(16));


        Calendar calendarNow = new GregorianCalendar();
        currentday = calendarNow.get(Calendar.YEAR) + "" + calendarNow.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + "" + calendarNow.get(Calendar.DAY_OF_MONTH);
        currentPaint = new Paint();
        currentPaint.setColor(Color.parseColor("#475D96"));
    }

    public void addData(List<HeaderModel> headers){
        headerModels=new ArrayList<>(headers);
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < headerModels.size(); i++) {
            String key = headerModels.get(i).getYear() + "" + headerModels.get(i).getMonth() + "" + headerModels.get(i).getDay();
            header.setColor(Color.parseColor("#000000"));
            if (key.equals(currentday)) {
                canvas.drawCircle(getWidth()/2, headerModels.get(i).getY()+dpToPx(20), dpToPx(17), currentPaint);
                header.setColor(Color.parseColor("#ffffff"));
            }
            canvas.drawText(headerModels.get(i).getDisplayDay(),getWidth()/2,headerModels.get(i).getY(),headerM);
            canvas.drawText(headerModels.get(i).getDay(),getWidth()/2,headerModels.get(i).getY()+dpToPx(25),header);
        }
    }

    private float dpToPx(float dp) {
        return getResources().getDisplayMetrics().density * dp;
    }

}
