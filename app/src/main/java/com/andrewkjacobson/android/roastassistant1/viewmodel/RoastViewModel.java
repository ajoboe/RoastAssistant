package com.andrewkjacobson.android.roastassistant1.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.RoastRepository;

import java.util.List;


// A ViewModel object provides the data for a specific UI component, such as a fragment or activity,
// and contains data-handling business logic to communicate with the model. For example, the
// ViewModel can call other components to load the data, and it can forward user requests to modify
// the data. The ViewModel doesn't know about UI components, so it isn't affected by configuration
// changes, such as recreating an activity when rotating the device.

/**
 * This ViewModel corresponds to the RoastFragment
 */
public class RoastViewModel extends AndroidViewModel {
    private RoastRepository repository;
    private LiveData<List<RoastEntity>> allRoasts; // a cache of all roasts
//    private LiveData<RoastEntity> currentRoast;

    public RoastViewModel(@NonNull Application application) {
        super(application);
        repository = new RoastRepository(application);
        allRoasts = repository.getAllRoasts();
    }

    public LiveData<List<RoastEntity>> getAllRoasts() {
        return allRoasts;
    }

    public  LiveData<RoastEntity> getRoast(int roastId) {
        return repository.getRoast(roastId);
    }

    /**
     * Works for inserting a new roast or modifying an existing roast.
     *
     * @param roast to insert or modify
     */
    public void insert(RoastEntity roast) {
        repository.insert(roast);
    }
}
