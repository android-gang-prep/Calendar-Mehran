package com.example.mehranm5.ui.month;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class EventModel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String event;
    private int eventColor;
    private String day;
    private long date;

    public EventModel(String title, String event, int eventColor, String day, long date) {
        this.title = title;
        this.event = event;
        this.eventColor = eventColor;
        this.day = day;
        this.date = date;
    }


    public int getEventColor() {
        return eventColor;
    }

    public void setEventColor(int eventColor) {
        this.eventColor = eventColor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
