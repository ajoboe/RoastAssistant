package com.andrewkjacobson.android.roastassistant1.db.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Entity;

import com.andrewkjacobson.android.roastassistant1.model.Details;

@Entity(tableName = "details_entity")
public class DetailsEntity extends RoastComponent implements Details, Parcelable {

   @PrimaryKey(autoGenerate = true)
    private int id;

    @ForeignKey(entity = RoastEntity.class, parentColumns = "id", childColumns = "roastId",
            onDelete = ForeignKey.CASCADE)
    private int roastId;

    private String date;
    private String beanType;
    private float batchSize;
    private float yield;
    private String roastDegree;

    private String roastNotes;
    private String tastingNotes;
    private String roaster;
    private int ambientTemperature;

    @Ignore
    public DetailsEntity(int roastId) {
        this();
        this.roastId = roastId;
    }

    public DetailsEntity() {
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

    @Ignore
    protected DetailsEntity(Parcel in) {
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

    public static final Parcelable.Creator<DetailsEntity> CREATOR = new Parcelable.Creator<DetailsEntity>() {
        @Override
        public DetailsEntity createFromParcel(Parcel in) {
            return new DetailsEntity(in);
        }

        @Override
        public DetailsEntity[] newArray(int size) {
            return new DetailsEntity[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getRoastId() {
        return roastId;
    }

    @Override
    public void setRoastId(int roastId) {
        this.roastId = roastId;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getBeanType() {
        return beanType;
    }

    @Override
    public void setBeanType(String beanType) {
        this.beanType = beanType;
    }

    @Override
    public float getBatchSize() {
        return batchSize;
    }

    @Override
    public void setBatchSize(float batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public float getYield() {
        return yield;
    }

    @Override
    public void setYield(float yield) {
        this.yield = yield;
    }

    @Override
    public float getWeightLossPercentage() {
        return getBatchSize() > 0 ? (float)(getBatchSize() - getYield()) / (float) getBatchSize() : 0;
    }

    @Override
    public String getRoastDegree() {
        return roastDegree;
    }

    @Override
    public void setRoastDegree(String roastDegree) {
        this.roastDegree = roastDegree;
    }

    @Override
    public String getRoastNotes() {
        return roastNotes;
    }

    @Override
    public void setRoastNotes(@NonNull String roastNotes) {
        this.roastNotes = roastNotes;
    }

    @Override
    public String getTastingNotes() {
        return tastingNotes;
    }

    @Override
    public void setTastingNotes(String tastingNotes) {
        this.tastingNotes = tastingNotes;
    }

    @Override
    public String getRoaster() {
        return roaster;
    }

    @Override
    public void setRoaster(String roaster) {
        this.roaster = roaster;
    }

    @Override
    public int getAmbientTemperature() {
        return ambientTemperature;
    }

    @Override
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
