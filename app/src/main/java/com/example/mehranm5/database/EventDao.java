package com.example.mehranm5.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mehranm5.ui.month.EventModel;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM events order by date desc")
    List<EventModel> getAll();

    @Query("SELECT * FROM events where date>=:date order by date asc")
    List<EventModel> getAllEvents(long date);

    @Query("SELECT * FROM events where date>=:first and date<=:last")
    List<EventModel> getAll(long first,long last);

    @Query("SELECT * FROM events order by date asc")
    LiveData<List<EventModel>> getAllL();

    @Insert
    void insert(EventModel... events);

    @Delete
    void delete(EventModel event);
}

