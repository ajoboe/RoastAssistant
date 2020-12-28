package com.andrewkjacobson.android.roastassistant1.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.andrewkjacobson.android.roastassistant1.Settings;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastDetailsEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.RoastRepository;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastReadingEntity;

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
    public static final String KEY_ROAST_ID = "roast id";

    private RoastRepository repository;
    private LiveData<RoastEntity> mRoast;
//    private MutableLiveData<List<RoastReadingEntity>> mReadings;
    private SavedStateHandle savedStateHandle;
    private int mRoastId = -1;
    private Settings settings;

    public RoastViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.repository = new RoastRepository(application);
        this.savedStateHandle = savedStateHandle;
        if(savedStateHandle.contains(KEY_ROAST_ID)) {
            mRoastId = savedStateHandle.get(KEY_ROAST_ID);
        }
    }

    /**
     * Get the current roast. If there isn't one, a new roast is created and returned.
     *
     * @return the current roast
     */
    public LiveData<RoastEntity> getRoast() {
        // if we have a roastId, get that roast
        // otherwise, create a new roast
        if(mRoast == null) {
            if(mRoastId != -1) {
                mRoast = loadRoast(mRoastId);
            } else {
                newRoast();
            }
        }
        return mRoast;
    }

    /**
     * Get the ID for the current roast. If there isn't one, a new roast is created and  the ID returned.
     *
     * @return the current roast ID
     */
    public int getRoastId() {
        if(mRoastId == -1) {
            newRoast();
        }
        
        return mRoastId;
    }

    /**
     * Get a roast by id and set it to the current roast
     *
     * @param roastId the id of the desired roast
     * @return the desired roast
     */
    public LiveData<RoastEntity> loadRoast(int roastId) {
        mRoast = repository.loadRoast(roastId);
        return mRoast;
    }

    /**
     * Works for inserting a new roast or modifying an existing roast.
     *
     * @param roast to insert or modify
     */
    public void insert(RoastEntity roast) {
        repository.insert(roast);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public boolean firstCrackOccurred() {
        return mRoast.getValue().firstCrackOccurred();
    }
    
    public int getSecondsElapsed() {
        return mRoast.getValue().getSecondsElapsed();
    }

    public void incrementSeconds() {
        RoastEntity r = mRoast.getValue();
        r.incrementSeconds();
        insert(r);
    }

    public boolean recordTemperature(String temperature) {
        if(isValidTemperature(temperature)) {
            RoastEntity r = mRoast.getValue();
            r.recordTemperature(Integer.valueOf(temperature));
            insert(r);
            return true;
        }
        return false;
    }

    private boolean isValidTemperature(String temperature) {
        int allowedChange = getSettings().getAllowedTempChange();
        return temperature.length() > 0
                && Integer.valueOf(temperature)
                    < getRoast().getValue().getCurrentReading().getTemperature() + allowedChange
                && Integer.valueOf(temperature)
                    > getRoast().getValue().getCurrentReading().getTemperature() - allowedChange;
    }

    /**
     * Add details to the current roast and send to the repository
     *
     * @param details the details to add
     */
    public void setDetails(RoastDetailsEntity details) {
        RoastEntity r = mRoast.getValue();
        r.setDetails(details);
        insert(r);
    }

    public void newRoast() {
        RoastEntity r = new RoastEntity();
        mRoastId = r.getRoastId();
        repository.insert(r);
        Log.d("RoastViewModel", "roastId is " + String.valueOf(mRoastId));
        mRoast = loadRoast(mRoastId);
    }

    public boolean isRunning() {
        return mRoast.getValue().isRunning();
    }

    public long getStartTime() {
        return mRoast.getValue().getStartTime();
    }

    public void startRoast() {
        RoastEntity r = mRoast.getValue();
        r.startRoast();
        insert(r);
    }

    /**
     * End the roast and send it to the repository
     */
    public void endRoast() {
        RoastEntity r = mRoast.getValue();
        r.endRoast();
        insert(r);
    }

    public void setStartTime(long startTime) {
        RoastEntity r = mRoast.getValue();
        r.setStartTime(startTime);
        insert(r);
    }

    public double getFirstCrackTime() {
        return mRoast.getValue().getFirstCrackTime();
    }

    public boolean isFirstCrack() {
        return firstCrackOccurred() && getFirstCrackTime() == getSecondsElapsed();
    }

    public void recordPower(int power) {
        RoastEntity r = mRoast.getValue();
        r.recordPower(power);
        insert(r);
    }
}
