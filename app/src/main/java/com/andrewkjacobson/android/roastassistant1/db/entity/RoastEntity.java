package com.andrewkjacobson.android.roastassistant1.db.entity;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.andrewkjacobson.android.roastassistant1.model.Roast;

import java.time.Instant;

/**
 * Represents a single roast
 */
@Entity(tableName = "roast_entity")
public class RoastEntity extends RoastComponent implements Roast {

    // fields
    @PrimaryKey
    private int id;

    private int elapsed = 0;
    private boolean isRunning = false;
    private boolean isFinished = false;
    private long startTime = -1;


//    @SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
//    @Embedded
//    private DetailsEntity details;
//    @Embedded
//    ArrayList<Reading> readings = new ArrayList<>();


    // constructors
    @RequiresApi(api = Build.VERSION_CODES.O)
    public RoastEntity() {
        this.id = Long.valueOf(Instant.now().getEpochSecond()).intValue();
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public RoastEntity() {
//        this(0,100);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public RoastEntity(int startingTemperature, int startingPower) {
//        recordReading(startingTemperature,startingPower);
//        details = new DetailsEntity();
//        this.id = Long.valueOf(Instant.now().getEpochSecond()).intValue();
//    }

//    protected RoastEntity(Parcel in) {
//        roastId = in.readInt();
//        details = in.readParcelable(DetailsEntity.class.getClassLoader());
////        mReadings = in.readSparseArray(ReadingEntity.class.getClassLoader());
//        readings = in.createTypedArrayList(ReadingEntity.CREATOR);
//        secondsElapsed = in.readInt();
//        firstCrackTime = in.readInt();
//        isRunning = in.readByte() != 0;
//        roastTimeAddend = in.readInt();
//    }


    // public methods
    @Override
    public void setId(int roastId) {
        this.id = roastId;
    }

    @Override
    public void setElapsed(int seconds) {
        this.elapsed = seconds;
    }

    @Override
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getElapsed() {
        return elapsed;
    }

    @Override
    public void incrementElapsed() {
        elapsed++;
    }

    @Override
    public void startRoast() {
        isRunning = true;
    }

    @Override
    public void endRoast() {
        isRunning = false;
        isFinished = true;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    @Override
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

//
//    public DetailsEntity getDetails() {
//        return details;
//    }

//    public void setDetails(DetailsEntity details) {
//        this.details = details;
//    }

//    public ArrayList<Reading> getReadings() {
//        return readings;
//    }

//    public void setReadings(ArrayList<Reading> mReadings) {
//        this.readings = mReadings;
//    }
//
//    public int getFirstCrackTime() {
//        return firstCrackTime;
//    }
//
//    public void setFirstCrackTime(int firstCrackTime) {
//        this.firstCrackTime = firstCrackTime;
//    }
//
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
//    public Reading getCurrentReading() {
//        return readings.get(readings.size()-1);
//    }
//
//    public Reading get1cReading() {
//        return readings.get(firstCrackTime);
//    }
//
//
//    public void set1c() {
//        firstCrackTime = secondsElapsed;
//    }
//
//    public void set1c(int time) {
//        firstCrackTime = time;
//    }
//
//    public void set1c(Reading reading) {
//        set1c(reading.getSeconds());
//        addReading(reading);
//    }
//
//    public boolean firstCrackOccurred() {
//        return firstCrackTime != -1;
//    }
//
//    public float getFirstCrackPercent() {
//            return (float) getFirstCrackTime() / ((float) getElapsed()) * 100;
//    }
//
//    /**
//     * If it's running, stop it. If it's stopped, start it.
//     *
//     * @return the new status
//     */
//    public boolean toggleRoast() {
//        return isRunning = !isRunning;
//    }
//
//    }
//
//    /**
//     * Records the current time, temperature and power. Sets as current reading.
//     *
//     * @param temperature
//     * @param power
//     */
//    public void recordReading(int temperature, int power) {
//        addReading(new ReadingEntity(getElapsed(), temperature, power));
//    }
//
//    public void recordReading(Reading reading) {
//        addReading(reading);
//    }
//
//    /**
//     * Adds reading to the list but doesn't mark as the current reading
//     * @param reading
//     */
//    private void addReading(Reading reading) {
//        readings.add(reading);
//    }
//
//    /**
//     * Describe the kinds of special objects contained in this Parcelable
//     * instance's marshaled representation. For example, if the object will
//     * the return value of this method must include the
//     *
//     * @return a bitmask indicating the set of special object types marshaled
//     * by this Parcelable object instance.
//     */
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
}
