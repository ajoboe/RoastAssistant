package com.andrewkjacobson.android.roastassistant1.db.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;
import androidx.room.Entity;

@Entity(tableName = "roast_details_table")
public class RoastDetailsEntity implements Parcelable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date;
    private String beanType;
    private float batchSize;
    private float yield;
    private String roastDegree;

    private String roastNotes;
    private String tastingNotes;
    private String roaster;
    private int ambientTemperature;

    public RoastDetailsEntity() {
        this.date = "";
        this.beanType = "";
        this.batchSize = 0;
        this.yield = 0;
        this.roastDegree = "";
        this.roastNotes = "";
        this.tastingNotes = "";
        this.roaster = "";
        this.ambientTemperature = 0;
    }

//    public RoastDetailsEntity() {
//        // empty constructor
//    }

    protected RoastDetailsEntity(Parcel in) {
        date = in.readString();
        beanType = in.readString();
        batchSize = in.readFloat();
        yield = in.readFloat();
        roastDegree = in.readString();
        roastNotes = in.readString();
        tastingNotes = in.readString();
        roaster = in.readString();
        ambientTemperature = in.readInt();
    }

    public static final Creator<RoastDetailsEntity> CREATOR = new Creator<RoastDetailsEntity>() {
        @Override
        public RoastDetailsEntity createFromParcel(Parcel in) {
            return new RoastDetailsEntity(in);
        }

        @Override
        public RoastDetailsEntity[] newArray(int size) {
            return new RoastDetailsEntity[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBeanType() {
        return beanType;
    }

    public void setBeanType(String beanType) {
        this.beanType = beanType;
    }

    public float getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(float batchSize) {
        this.batchSize = batchSize;
    }

    public float getYield() {
        return yield;
    }

    public void setYield(float yield) {
        this.yield = yield;
    }

    public float getWeightLossPercentage() {
        return getBatchSize() > 0 ? (float)(getBatchSize() - getYield()) / (float) getBatchSize() : 0;
    }

    public String getRoastDegree() {
        return roastDegree;
    }

    public void setRoastDegree(String roastDegree) {
        this.roastDegree = roastDegree;
    }

    public String getRoastNotes() {
        return roastNotes;
    }

    public void setRoastNotes(@NonNull String roastNotes) {
        this.roastNotes = roastNotes;
    }

    public String getTastingNotes() {
        return tastingNotes;
    }

    public void setTastingNotes(String tastingNotes) {
        this.tastingNotes = tastingNotes;
    }

    public String getRoaster() {
        return roaster;
    }

    public void setRoaster(String roaster) {
        this.roaster = roaster;
    }

    public int getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setAmbientTemperature(int ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
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
        dest.writeString(getDate());
        dest.writeString(getBeanType());
        dest.writeFloat(getBatchSize());
        dest.writeFloat(getYield());
        dest.writeString(getRoastDegree());
        dest.writeString(getRoastNotes());
        dest.writeString(getTastingNotes());
        dest.writeString(getRoaster());
        dest.writeInt(getAmbientTemperature());
    }
}
