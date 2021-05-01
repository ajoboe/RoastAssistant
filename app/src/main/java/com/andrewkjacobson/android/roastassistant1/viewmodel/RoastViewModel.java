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
import com.andrewkjacobson.android.roastassistant1.model.Reading;
import com.andrewkjacobson.android.roastassistant1.model.Roast;
import com.andrewkjacobson.android.roastassistant1.ui.SettingsActivity;

import java.util.ArrayList;
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

    private int mRoastId = -1;
    private final LiveData<RoastEntity> mRoastLiveData;
    private RoastEntity mRoast;
    private final LiveData<DetailsEntity> mDetailsLiveData;
    private final LiveData<List<ReadingEntity>> mReadingsLiveData;
    private final LiveData<List<CrackReadingEntity>> mCracksLiveData;
    private Settings settings;
    private ReadingEntity mCurrentReading;
    private final List<CrackReadingEntity> mCracks;

    public RoastViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.application = application;
        this.repository = new RoastRepository(application);
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

        mRoastLiveData = repository.getRoastLiveData(mRoastId);
        mDetailsLiveData = repository.getDetailsLiveData(mRoastId);
        mReadingsLiveData = repository.getReadingsLiveData(mRoastId);
        mCracksLiveData = repository.getCracksLiveData(mRoastId);

        mCracks = new ArrayList<>();
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

    public LiveData<DetailsEntity> getDetails() {
        return mDetailsLiveData;
    }

    public LiveData<List<ReadingEntity>> getReadingsLiveData() {
        return mReadingsLiveData;
    }

    public LiveData<List<CrackReadingEntity>> getCracksLiveData() {
        return mCracksLiveData;
    }

    public Settings getSettings() {
        return settings;
    }

    private void setSettings(Settings settings) {
        this.settings = settings;
    }

    public int getElapsed() {
        long startTime = mRoast.getStartTime();
        if(startTime <= 0) return 0;
        return  Math.toIntExact(elapsedRealtime() - startTime) / 1000;
    }

    public boolean recordTemperature(String temperature) {
        try {
            if (!isValidTemperature(temperature)) return false;

            int power = getCurrentReading().getPower();
            if (!isRunning()) repository.deleteAllReadings();
            mCurrentReading = new ReadingEntity(
                    getElapsed(),
                    Integer.valueOf(temperature),
                    power,  // power from prev reading
                    getRoastId());
            repository.insert(mCurrentReading);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean record1c(String temperature) {
        try {
            if (!isValidTemperature(temperature)) return false;

            CrackReadingEntity crack = new CrackReadingEntity(
                    getElapsed(),
                    Integer.valueOf(temperature),
                    getCurrentReading().getPower(),
                    1,
                    true,
                    getRoastId());
            mCracks.add(crack); // cache the crack
            repository.insert(crack);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void recordPower(int power) {
        int temperature = getCurrentReading().getTemperature();
        if(!isRunning()) repository.deleteAllReadings();

        mCurrentReading = new ReadingEntity(
                getElapsed(),
                temperature,
                power,
                getRoastId());
        repository.insert(mCurrentReading);
    }

    // todo remove the getValue when revamping
    private boolean isValidTemperature(String temperature) throws NumberFormatException{
        if (mReadingsLiveData.getValue().size() < 3)
            return true; // large leaps in temp allowed at start
        int allowedChange = getSettings().getAllowedTempChange();
        return temperature.length() > 0
                && Integer.valueOf(temperature)
                < getCurrentReading().getTemperature() + allowedChange
                && Integer.valueOf(temperature)
                > getCurrentReading().getTemperature() - allowedChange;
    }

    private Reading getCurrentReading() {
        if(mCurrentReading != null) return mCurrentReading;

        return new ReadingEntity(0,
                    settings.getStartingTemperature(),
                    settings.getStartingPower(),
                    getRoastId());
    }

    public void setDetails(DetailsEntity details) {
        details.setRoastId(getRoastId());
        repository.insert(details);
    }

    public boolean isRunning() {
        return mRoast.isRunning();
    }

    public long getStartTime() {
        return mRoast.getStartTime();
    }

    public void startRoast() {
        int chronoAddend =  -(getSettings().getRoastTimeInSecAddend() * 1000);
        long startTime = elapsedRealtime() + chronoAddend;
        setStartTime(startTime);
        mRoast.startRoast();
        repository.update(mRoast);
    }

    /**
     * End the roast and send it to the repository
     */
    public void endRoast() {
        mRoast.endRoast();
        repository.update(mRoast);
    }

    private void setStartTime(long startTime) {
        mRoast.setStartTime(startTime);
        repository.update(mRoast);
    }

    /**
     * Get the current first crack reading. Null if it hasn't occurred.
     *
     * @return the first crack reading or null if it hasn't occurred.
     */
    private CrackReadingEntity getFirstCrack() {
        return mCracks == null ? null : mCracks.get(0); // todo need to revamp the crack stuff
    }

    public float getFirstCrackPercent() {
        return getFirstCrackLookaheadPercent(0);
    }

    /**
     * Get a future first crack percent.
     * @param seconds into the future
     * @return the percent
     */
    public float getFirstCrackLookaheadPercent(int seconds) {
        return (float) getFirstCrack().getSeconds() / ((float) getElapsed() + seconds) * 100;
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
                    Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_ROAST_TIME_ADDEND, "0")),
                    Integer.parseInt(sharedPreferences.getString(SettingsActivity.KEY_PREF_FIRST_CRACK_LOOKAHEAD_TIME, "0"))
            ));
        } catch (NullPointerException e) {
            this.setSettings(new Settings(
                    Integer.parseInt("60"),
                    Integer.parseInt("50"),
                    Integer.parseInt("68"),
                    Integer.parseInt("100"),
                    Integer.parseInt("0"),
                    Integer.parseInt("0")));
        }
    }
}
