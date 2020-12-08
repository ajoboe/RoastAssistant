package com.andrewkjacobson.android.roastassistant1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

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
