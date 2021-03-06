package com.andrewkjacobson.android.roastassistant1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;


// A ViewModel object provides the data for a specific UI component, such as a fragment or activity,
// and contains data-handling business logic to communicate with the model. For example, the
// ViewModel can call other components to load the data, and it can forward user requests to modify
// the data. The ViewModel doesn't know about UI components, so it isn't affected by configuration
// changes, such as recreating an activity when rotating the device.
public class RoastDetailsViewModel extends AndroidViewModel {
    private RoastRepository mRepository;
    private LiveData<List<RoastDetails>> mAllRoasts; // a cache of all roasts

    public RoastDetailsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RoastRepository(application);
        mAllRoasts = mRepository.getAllRoasts();
    }

    public LiveData<List<RoastDetails>> getAllRoasts() {
        return mAllRoasts;
    }

    public void insert(RoastDetails details) {
        mRepository.insert(details);
    }
}
