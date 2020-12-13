package com.andrewkjacobson.android.roastassistant1;

import androidx.collection.SparseArrayCompat;

/**
 * Represents a single roast
 */
public class Roast {
    RoastDetails mDetails;
    SparseArrayCompat<RoastReading> mReadings;
    private int mSecondsElapsed;
    private int m1cTime = -1;
    private boolean mRoastIsRunning = false;
    private int mTimeOfCurrentReading;
    private int mRoastTimeAddend;

    Roast(RoastReading reading) {
        recordReading(reading);
    }

    Roast(RoastReading reading, RoastDetails details) {
        recordReading(reading);
        setDetails(details);
    }

    public RoastReading getCurrentReading() {
        return mReadings.get(mTimeOfCurrentReading);
    }

    public RoastReading get1cReading() {
        return mReadings.get(m1cTime);
    }

    public int getRoastTimeAddend() {
        return mRoastTimeAddend;
    }

    public void setRoastTimeAddend(int addendInSeconds) {
        mRoastTimeAddend = addendInSeconds;
    }

    public void set1c() {
        m1cTime = mSecondsElapsed;
    }

    public void set1c(int time) {
        m1cTime = time;
    }

    public void set1c(RoastReading reading) {
        set1c(reading.getTimeStamp());
        insertReading(reading);
    }

    public RoastDetails getDetails() {
        return mDetails;
    }

    private void setDetails(RoastDetails details) {
        this.mDetails = details;
    }

    public boolean firstCrackOccurred() {
        return m1cTime != -1;
    }

    public void startRoast() {
        mSecondsElapsed = 0 + getRoastTimeAddend();
        mRoastIsRunning = true;
    }

    public void endRoast() {
        mRoastIsRunning = false;
    }

    /**
     * If it's running, stop it. If it's stopped, start it.
     *
     * @return the new status
     */
    public boolean toggleRoast() {
        return mRoastIsRunning = !mRoastIsRunning;
    }

    public boolean isRunning() {
        return mRoastIsRunning;
    }
    /**
     *
     * @param temperature
     */
    public void recordTemperature(int temperature) {
        recordReading(temperature, getCurrentReading().getPowerPercentage());
    }

    /**
     *
     * @param power
     */
    public void recordPower(int power) {
        recordReading(getCurrentReading().getTemperature(), power);
    }

    /**
     * Records the current time, temperature and power. Sets as current reading.
     *
     * @param temperature
     * @param power
     */
    public void recordReading(int temperature, int power) {
        mTimeOfCurrentReading = mSecondsElapsed;
        insertReading(new RoastReading(mTimeOfCurrentReading, temperature, power));
    }

    public void recordReading(RoastReading reading) {
        mTimeOfCurrentReading = reading.getTimeStamp();
        insertReading(reading);
    }

    /**
     * Adds reading to the list but doesn't mark as the current reading
     * @param reading
     */
    private void insertReading(RoastReading reading) {
        mReadings.put(reading.getTimeStamp(), reading);
    }

}
