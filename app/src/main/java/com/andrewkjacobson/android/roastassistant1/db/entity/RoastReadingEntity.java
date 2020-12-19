package com.andrewkjacobson.android.roastassistant1.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "roast_reading_table")
public class RoastReadingEntity implements Parcelable {
    public int getReadingId() {
        return readingId;
    }

    public void setReadingId(int readingId) {
        this.readingId = readingId;
    }

    @PrimaryKey(autoGenerate = true)
    private int readingId;

    private int timeStamp;
    private int temperature;
    private int powerPercentage;

    /**
     *
     * @param timeStamp time of reading in seconds
     * @param temperature the reading temperature
     * @param powerPercentage the reading power percentage
     */
    public RoastReadingEntity(int timeStamp, int temperature, int powerPercentage) {
        setTimeStamp(timeStamp);
        setTemperature(temperature);
        setPowerPercentage(powerPercentage);
    }

    RoastReadingEntity(Parcel in) {
        setTimeStamp(in.readInt());
        setTemperature(in.readInt());
        setPowerPercentage(in.readInt());
    }

    public static final Creator<RoastReadingEntity> CREATOR = new Creator<RoastReadingEntity>() {
        @Override
        public RoastReadingEntity createFromParcel(Parcel in) {
            return new RoastReadingEntity(in);
        }

        @Override
        public RoastReadingEntity[] newArray(int size) {
            return new RoastReadingEntity[size];
        }
    };

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getPowerPercentage() {
        return powerPercentage;
    }

    public void setPowerPercentage(int powerPercentage) {
        this.powerPercentage = powerPercentage;
    }

    public String toString() {
        return "Timestamp: " + Integer.toString(getTimeStamp())
                + " seconds. Temperature: " + Integer.toString(getTemperature())
                + ". Power: " + Integer.toString(getPowerPercentage()) + "%";
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getTimeStamp());
        dest.writeInt(getTemperature());
        dest.writeInt(getPowerPercentage());
    }
}
