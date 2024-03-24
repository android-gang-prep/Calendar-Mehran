package com.example.mehranm5.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mehranm5.ui.month.EventModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {EventModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EventDao eventDao();
    private static AppDatabase appDatabase;


    public static AppDatabase getAppDatabase(Context context) {
        if (appDatabase==null)
            appDatabase= Room.databaseBuilder(context,AppDatabase.class,"calendar").allowMainThreadQueries().build();

        return appDatabase;
    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    public void addEvent(EventModel eventModel){
        executorService.execute(() -> eventDao().insert(eventModel));
    }
}
