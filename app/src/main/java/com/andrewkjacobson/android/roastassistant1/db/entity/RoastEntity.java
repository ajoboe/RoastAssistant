package com.andrewkjacobson.android.roastassistant1.db.entity;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.RoomWarnings;

import com.andrewkjacobson.android.roastassistant1.model.Roast;

import java.time.Instant;
import java.util.ArrayList;

/**
 * Represents a single roast
 */
@Entity(tableName = "RoastEntity")
public class RoastEntity implements Roast {

    // fields
    @PrimaryKey(autoGenerate = false)
    private int roastId;

    @SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
    @Embedded
    private RoastDetailsEntity details;
    @Embedded
    ArrayList<RoastReadingEntity> readings = new ArrayList<>();
    private int secondsElapsed = 0;
    private int firstCrackTime = -1;
    private boolean isRunning = false;
    private int roastTimeAddend;
    private long startTime;

    // constructors
    @RequiresApi(api = Build.VERSION_CODES.O)
    public RoastEntity() {
//        this.roastId = Long.valueOf(Instant.now().getEpochSecond()).intValue();
//        recordReading(0, 100);
        this(0,100);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public RoastEntity(int startingTemperature, int startingPower) {
        recordReading(startingTemperature,startingPower);
        details = new RoastDetailsEntity();
        this.roastId = Long.valueOf(Instant.now().getEpochSecond()).intValue();
    }

//    protected RoastEntity(Parcel in) {
//        roastId = in.readInt();
//        details = in.readParcelable(RoastDetailsEntity.class.getClassLoader());
////        mReadings = in.readSparseArray(RoastReadingEntity.class.getClassLoader());
//        readings = in.createTypedArrayList(RoastReadingEntity.CREATOR);
//        secondsElapsed = in.readInt();
//        firstCrackTime = in.readInt();
//        isRunning = in.readByte() != 0;
//        roastTimeAddend = in.readInt();
//    }


    // public methods
    
    public void setRoastId(int roastId) {
        this.roastId = roastId;
    }

    public RoastDetailsEntity getDetails() {
        return details;
    }

    public void setDetails(RoastDetailsEntity details) {
        this.details = details;
    }

    public ArrayList<RoastReadingEntity> getReadings() {
        return readings;
    }

    public void setReadings(ArrayList<RoastReadingEntity> mReadings) {
        this.readings = mReadings;
    }

    public void setSecondsElapsed(int mSecondsElapsed) {
        this.secondsElapsed = mSecondsElapsed;
    }

    public int getFirstCrackTime() {
        return firstCrackTime;
    }

    public void setFirstCrackTime(int firstCrackTime) {
        this.firstCrackTime = firstCrackTime;
    }

    public void setRunning(boolean mRoastIsRunning) {
        this.isRunning = mRoastIsRunning;
    }

//    public int getTimeOfCurrentReading() {
//        return timeOfCurrentReading;
//    }
//
//    public void setTimeOfCurrentReading(int mTimeOfCurrentReading) {
//        this.timeOfCurrentReading = mTimeOfCurrentReading;
//    }
//
//    public static final Creator<RoastEntity> CREATOR = new Creator<RoastEntity>() {
//        @Override
//        public RoastEntity createFromParcel(Parcel in) {
//            return new RoastEntity(in);
//        }
//
//        @Override
//        public RoastEntity[] newArray(int size) {
//            return new RoastEntity[size];
//        }
//    };

    public int getRoastId() {
        return roastId;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public void incrementSeconds() {
        secondsElapsed++;
    }

    public RoastReadingEntity getCurrentReading() {
        return readings.get(readings.size()-1);
    }

    public RoastReadingEntity get1cReading() {
        return readings.get(firstCrackTime);
    }

    public int getRoastTimeAddend() {
        return roastTimeAddend;
    }

    public void setRoastTimeAddend(int addendInSeconds) {
        roastTimeAddend = addendInSeconds;
    }

    public void set1c() {
        firstCrackTime = secondsElapsed;
    }

    public void set1c(int time) {
        firstCrackTime = time;
    }

    public void set1c(RoastReadingEntity reading) {
        set1c(reading.getTimeStamp());
        addReading(reading);
    }

    public boolean firstCrackOccurred() {
        return firstCrackTime != -1;
    }

    public float getFirstCrackPercent() {
            return (float) getFirstCrackTime() / ((float) getSecondsElapsed()) * 100;
    }

    public void startRoast() {
        secondsElapsed = 0 + getRoastTimeAddend();
        isRunning = true;
    }

    public void endRoast() {
        isRunning = false;
    }

    /**
     * If it's running, stop it. If it's stopped, start it.
     *
     * @return the new status
     */
    public boolean toggleRoast() {
        return isRunning = !isRunning;
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
        addReading(new RoastReadingEntity(getSecondsElapsed(), temperature, power));
    }

    public void recordReading(RoastReadingEntity reading) {
        addReading(reading);
    }

    /**
     * Adds reading to the list but doesn't mark as the current reading
     * @param reading
     */
    private void addReading(RoastReadingEntity reading) {
        readings.add(reading);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * the return value of this method must include the
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    /**
//     * Flatten this object in to a Parcel.
//     *
//     * @param dest  The Parcel in which the object should be written.
//     * @param flags Additional flags about how the object should be written.
//     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
//     */
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(roastId);
//        dest.writeParcelable(details, flags);
////        dest.writeSparseArray(mReadings);
//        dest.writeTypedList(readings);
//        dest.writeInt(secondsElapsed);
//        dest.writeInt(firstCrackTime);
//        dest.writeByte((byte) (isRunning ? 1 : 0));
////        dest.writeInt(timeOfCurrentReading);
//        dest.writeInt(roastTimeAddend);
//    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }
}