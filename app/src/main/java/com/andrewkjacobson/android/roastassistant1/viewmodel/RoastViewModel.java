package com.andrewkjacobson.android.roastassistant1.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.andrewkjacobson.android.roastassistant1.Settings;
import com.andrewkjacobson.android.roastassistant1.db.entity.DetailsEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.RoastRepository;
import com.andrewkjacobson.android.roastassistant1.model.Crack;
import com.andrewkjacobson.android.roastassistant1.model.Details;
import com.andrewkjacobson.android.roastassistant1.model.Reading;
import com.andrewkjacobson.android.roastassistant1.model.Roast;

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
    private SavedStateHandle savedStateHandle;

    private int mRoastId = -1;
    private LiveData<Roast> mRoast;
    private LiveData<Details> mDetails;
    private LiveData<List<Reading>> mReadings;
    private LiveData<List<Crack>> mCracks;
    private Settings settings;

    public RoastViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.repository = new RoastRepository(application);
        this.savedStateHandle = savedStateHandle;
        if(savedStateHandle.contains(KEY_ROAST_ID)) {
            mRoastId = savedStateHandle.get(KEY_ROAST_ID);
            mRoast = repository.getRoast(mRoastId);
        }
    }

    /**
     * Get the ID for the current roast. If there isn't one, a new roast is created and  the ID returned.
     *
     * @return the current roast ID
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getRoastId() {
        if(mRoastId == -1) {
            newRoast();
        }

        return mRoastId;
    }

    /**
     * Get the current roast. If there isn't one, a new roast is created and returned.
     *
     * @return the current roast
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Roast> getRoast() {
        // if we have a roastId, get that roast
        // otherwise, create a new roast
        if(mRoast == null) {
            if(mRoastId != -1) {
                mRoast = repository.getRoast(mRoastId);
            } else {
                newRoast();
            }
        }
        return mRoast;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void newRoast() {
        Roast r = new RoastEntity();
        mRoastId = r.getId();
        repository.insert((RoastEntity) r);
        mRoast = repository.getRoast(mRoastId);


        mDetails = repository.loadDetails(mRoastId);


        mReadings = repository.loadReadings(mRoastId);


        mCracks = repository.getCracks(mRoastId);
    }
//
//    /**
//     * Get a roast by id and set it to the current roast
//     *
//     * @param roastId the id of the desired roast
//     * @return the desired roast
//     */
//    private void loadRoast(int roastId) {
//        mRoast = repository.loadRoast(roastId);
//    }


    private void load(LiveData<Details> mDetails, int mRoastId) {
    }


//    /**
//     * Works for inserting a new roastComponent or modifying an existing roastComponent.
//     *
//     * @param roastComponent to insert or modify
//     */
//    public void insert(RoastComponent roastComponent) {
//        repository.insert(roastComponent);
//    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public boolean firstCrackOccurred() {
        List<Crack> cracks = mCracks.getValue();

        for(Crack c : cracks) {
            if(c.getCrackNumber() == 1 || c.hasOccurred()) {
                return true;
            }
        }
        return false;
    }
    
    public int getElapsed() {
        return mRoast.getValue().getElapsed();
    }

    public void incrementSeconds() {
        Roast r = mRoast.getValue();
        r.incrementElapsed();
        repository.insert((RoastEntity) r);
    }

    public boolean recordTemperature(String temperature) {
        if(isValidTemperature(temperature)) {
            Reading newReading = new ReadingEntity(
                    getElapsed(),
                    Integer.valueOf(temperature),
                    mReadings.getValue().get(mReadings.getValue().size() - 1).getPower()); // power from prev reading
            repository.insert((ReadingEntity) newReading);
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
    public void setDetails(DetailsEntity details) {
        Roast r = mRoast.getValue();
        r.setDetails(details);
        insert(r);
    }

    public boolean isRunning() {
        return mRoast.getValue().isRunning();
    }

    public long getStartTime() {
        return mRoast.getValue().getStartTime();
    }

    public void startRoast() {
        Roast r = mRoast.getValue();
        r.startRoast();
        insert(r);
    }

    /**
     * End the roast and send it to the repository
     */
    public void endRoast() {
        Roast r = mRoast.getValue();
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
        return firstCrackOccurred() && getFirstCrackTime() == getElapsed();
    }

    public void recordPower(int power) {
        RoastEntity r = mRoast.getValue();
        r.recordPower(power);
        insert(r);
    }
}
