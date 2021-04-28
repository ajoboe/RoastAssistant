package com.andrewkjacobson.android.roastassistant1.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.andrewkjacobson.android.roastassistant1.R;
import com.andrewkjacobson.android.roastassistant1.RoastRepository;
import com.andrewkjacobson.android.roastassistant1.Settings;
import com.andrewkjacobson.android.roastassistant1.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.DetailsEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.model.Crack;
import com.andrewkjacobson.android.roastassistant1.model.Reading;
import com.andrewkjacobson.android.roastassistant1.model.Roast;
import com.andrewkjacobson.android.roastassistant1.ui.SettingsActivity;

import java.util.List;

import static android.os.SystemClock.elapsedRealtime;


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
    private final Application application;
    private final RoastRepository repository;
    private final SavedStateHandle savedStateHandle;

    private int mRoastId = -1;
    private final LiveData<RoastEntity> mRoastLiveData;
    private RoastEntity mRoast;
    private final LiveData<DetailsEntity> mDetails;
    private final LiveData<List<ReadingEntity>> mReadings;
    private final LiveData<List<CrackReadingEntity>> mCracks;
    private Settings settings;
    private boolean mFirstCrackOccurred = false;

    public RoastViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.application = application;
        this.repository = new RoastRepository(application);
        this.savedStateHandle = savedStateHandle;
        loadSettings();

        if(savedStateHandle != null && savedStateHandle.contains(KEY_ROAST_ID)) {
            mRoastId = savedStateHandle.get(KEY_ROAST_ID);
        } else {
            mRoast = new RoastEntity(); // empty roast w/ id
            mRoastId = mRoast.getId();    //Long.valueOf(Instant.now().getEpochSecond()).intValue();
            repository.insert(mRoast);
            repository.insert(new DetailsEntity(mRoastId));
            repository.insert(new ReadingEntity(0,
                    settings.getStartingTemperature(),
                    settings.getStartingPower(),
                    mRoastId));
        }

        mRoastLiveData = repository.getRoast(mRoastId);
        mDetails = repository.getDetails(mRoastId);
        mReadings = repository.getReadings(mRoastId);
        mCracks = repository.getCracks(mRoastId);
    }

    /**
     * Get the ID for the current roast. If there isn't one, a new roast is created and  the ID returned.
     *
     * @return the current roast ID
     */
    public int getRoastId() {
        return mRoastId;
    }

    /**
     * Get the current roast.
     *
     * @return the current roast
     */
    public LiveData<RoastEntity> getRoastLiveData() {
         return mRoastLiveData;
    }

    public Roast getRoast() {
        return mRoast;
    }

//    public void newRoast() {
//        Roast r = new RoastEntity(); // empty roast w/ id
//        mRoastId = r.getId();
//        repository.insert(r);
//        mRoast = repository.getRoast(mRoastId);
//
//        repository.insert(new DetailsEntity(mRoastId));
//        mDetails = repository.getDetails(mRoastId);
//
//        repository.insert(new ReadingEntity(0,
//                settings.getStartingTemperature(),
//                settings.getStartingPower()));
//        mReadings = repository.getReadings(mRoastId);
//
//        mCracks = repository.getCracks(mRoastId);
//    }


    public LiveData<DetailsEntity> getDetails() {
        return mDetails;
    }

    public LiveData<List<ReadingEntity>> getReadingsLiveData() {
//        if(mReadings == null) {
//            mReadings = new MutableLiveData<List<ReadingEntity>>(); // won't work because the ref will change when
//        }
        return mReadings;
    }

    public LiveData<List<CrackReadingEntity>> getCracksLiveData() {
//        if(mCracks == null) {
//            mCracks = new MutableLiveData<List<CrackReadingEntity>>();
//        }
        return mCracks;
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

    private void setSettings(Settings settings) {
        this.settings = settings;
    }

    // todo this should probably not happen here---getValue() is not great
    //          better to observe mCracks and set mFirstCrackOccurred = true
    public boolean firstCrackOccurred() {
        if(mCracks == null || mCracks.getValue() == null || mCracks.getValue().isEmpty()) {
            return false;
        }

        List<CrackReadingEntity> cracks = mCracks.getValue();
        for(Crack c : cracks) {
            if(c.getCrackNumber() == 1 && c.hasOccurred()) {
                return true;
            }
        }
        return false;
    }

    public int getElapsed() {
//        if(mRoastLiveData == null || mRoastLiveData.getValue() == null) return 0;

//        long startTime = mRoastLiveData.getValue().getStartTime();
        long startTime = mRoast.getStartTime();
        if(startTime <= 0) return 0;
        return  Math.toIntExact(elapsedRealtime() - startTime) / 1000;
    }

    public boolean recordTemperature(String temperature) {
        if(!isValidTemperature(temperature)) return false;

        int power = getCurrentPower();
        if(!isRunning()) repository.deleteAllReadings();
        repository.insert(new ReadingEntity(
                getElapsed(),
                Integer.valueOf(temperature),
                power,  // power from prev reading
                getRoastId()));
        return true;
    }

    public boolean record1c(String temperature) {
        if(!isValidTemperature(temperature)) return false;

        repository.insert(new CrackReadingEntity(
                getElapsed(),
                Integer.valueOf(temperature),
                getCurrentPower(),
                1,
                true,
                getRoastId()));
        return true;
    }

    public void recordPower(int power) {
        int temperature = getCurrentTemperature();
        if(!isRunning()) repository.deleteAllReadings();

        repository.insert(new ReadingEntity(
                getElapsed(),
                temperature,
                power,
                getRoastId()));
    }

    private boolean isValidTemperature(String temperature) {
        if(mReadings.getValue().size() < 3) return true; // large leaps in temp allowed at start
        int allowedChange = getSettings().getAllowedTempChange();
        return temperature.length() > 0
                && Integer.valueOf(temperature)
                < getCurrentReading().getTemperature() + allowedChange
                && Integer.valueOf(temperature)
                > getCurrentReading().getTemperature() - allowedChange;
    }

    private Reading getCurrentReading() {
        if(mReadings == null || mReadings.getValue() == null || mReadings.getValue().isEmpty()) {
            return new ReadingEntity(0,
                    settings.getStartingTemperature(),
                    settings.getStartingPower(),
                    getRoastId());
        }
        return mReadings.getValue().get(mReadings.getValue().size() - 1);
    }

    private int getCurrentTemperature() {
        return getCurrentReading().getTemperature();
    }

    private int getCurrentPower() {
        return getCurrentReading().getPower();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDetails(DetailsEntity details) {
        details.setRoastId(getRoastId());
        repository.insert(details);
    }

    public boolean isRunning() {
//        return mRoastLiveData != null && mRoastLiveData.getValue() != null && mRoastLiveData.getValue().isRunning();
        return mRoast.isRunning();
    }

    public long getStartTime() {
//        return mRoastLiveData.getValue().getStartTime();
        return mRoast.getStartTime();
    }

    public void startRoast() {
        int chronoAddend =  -(getSettings().getRoastTimeInSecAddend() * 1000);
        long startTime = elapsedRealtime() + chronoAddend;
        setStartTime(startTime);

//        RoastEntity r = mRoastLiveData.getValue();
//        r.startRoast();
//        repository.update(r);
        mRoast.startRoast();
        repository.update(mRoast);
    }

    /**
     * End the roast and send it to the repository
     */
    public void endRoast() {
//        RoastEntity r = mRoastLiveData.getValue();
//        r.endRoast();
//        repository.update(r);
        mRoast.endRoast();
        repository.update(mRoast);
    }

    // todo is this the right way to update live data??
    public void setStartTime(long startTime) {
//        RoastEntity r = mRoastLiveData.getValue();;
//        if(r == null) r = mRoast;
        mRoast.setStartTime(startTime);
        repository.update(mRoast);
    }

    public double getFirstCrackTime() {
        if(mCracks == null || mCracks.getValue() == null || mCracks.getValue().size() == 0) {
            return -1;
        }
        return mCracks.getValue().get(0).getSeconds();
    }

    private CrackReadingEntity get1cReading() {
        return mCracks.getValue().get(0);
    }

    public float getFirstCrackPercent() {
        return (float) getFirstCrackTime() / ((float) getElapsed()) * 100;
    }

    // todo remove hardcoded defaults
    private void loadSettings() {
        try {
            androidx.preference.PreferenceManager
                    .setDefaultValues(application, R.xml.root_preferences, false);
            SharedPreferences sharedPreferences =
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(application);

            this.setSettings(new Settings(
                    Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_TEMP_CHECK_FREQ, "60")),
                    Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_ALLOWED_TEMP_CHANGE, "50")),
                    Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_STARTING_TEMPERATURE, "68")),
                    Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_STARTING_POWER, "100")),
                    Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_ROAST_TIME_ADDEND, "0"))
            ));
        } catch (NullPointerException e) {
            this.setSettings(new Settings(
                    Integer.parseInt("60"),
                    Integer.parseInt("50"),
                    Integer.parseInt("68"),
                    Integer.parseInt("100"),
                    Integer.parseInt("0")));
        }
    }
}
