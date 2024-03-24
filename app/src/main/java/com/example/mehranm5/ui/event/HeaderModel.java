package com.example.mehranm5.ui.event;

public class HeaderModel {
    private String year;
    private String month;
    private String day;
    private String displayDay;
    private float y;

    public HeaderModel(String year, String month, String day, String displayDay, float y) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.displayDay = displayDay;
        this.y = y;
    }

    public String getDisplayDay() {
        return displayDay;
    }

    public void setDisplayDay(String displayDay) {
        this.displayDay = displayDay;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
