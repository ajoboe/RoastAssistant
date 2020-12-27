package com.andrewkjacobson.android.roastassistant1.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.andrewkjacobson.android.roastassistant1.Settings;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastDetailsEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.RoastRepository;


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
    private MutableLiveData<RoastEntity> roast;
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
    public MutableLiveData<RoastEntity> getRoast() {
        // if we have a roastId, get that roast
        // otherwise, create a new roast
        if(roast == null) {
            if(mRoastId != -1) {
                roast = loadRoast(mRoastId);
            } else {
                newRoast();
            }
        }
        return roast;
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
    public MutableLiveData<RoastEntity> loadRoast(int roastId) {
        roast = repository.loadRoast(roastId);
        return roast;
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
        return roast.getValue().firstCrackOccurred();
    }
    
    public int getSecondsElapsed() {
        return roast.getValue().getSecondsElapsed();
    }

    public void incrementSeconds() {
        roast.setValue(roast.getValue().incrementSeconds());
    }

    public boolean recordTemperature(String temperature) {
        if(isValidTemperature(temperature)) {
            roast.setValue(roast.getValue().recordTemperature(Integer.valueOf(temperature)));
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
        roast.setValue(roast.getValue().setRoastDetails(details));
        insert(roast.getValue());
    }

    public void newRoast() {
        roast = new MutableLiveData<RoastEntity>();
        mRoastId = roast.getValue().getRoastId();
    }

    public boolean isRunning() {
        return roast.getValue().isRunning();
    }

    public long getStartTime() {
        return roast.getValue().getStartTime();
    }

    public void startRoast() {
        roast.setValue(roast.getValue().startRoast());
    }

    /**
     * End the roast and send it to the repository
     */
    public void endRoast() {
        roast.setValue(roast.getValue().endRoast());
        repository.insert(getRoast().getValue());
    }

    public void setStartTime(long startTime) {
        roast.setValue(roast.getValue().setChronoStartTime(startTime));
    }

    public double getFirstCrackTime() {
        return roast.getValue().getFirstCrackTime();
    }

    public boolean isFirstCrack() {
        return firstCrackOccurred() && getFirstCrackTime() == getSecondsElapsed();
    }

    public void recordPower(int power) {
        roast.setValue(roast.getValue().recordPower(power));
    }
}
