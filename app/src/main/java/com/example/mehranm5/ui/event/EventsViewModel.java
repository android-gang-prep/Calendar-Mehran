package com.example.mehranm5.ui.event;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mehranm5.database.AppDatabase;
import com.example.mehranm5.ui.month.EventModel;

import java.util.List;

public class EventsViewModel extends AndroidViewModel {

    private LiveData<List<EventModel>> events;

    public EventsViewModel(Application application) {
        super(application);
        events = AppDatabase.getAppDatabase(application).eventDao().getAllL();
    }

    public LiveData<List<EventModel>> getEvents() {
        return events;
    }
}