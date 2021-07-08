package com.andrewkjacobson.android.roastassistant.db.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Entity;

import com.andrewkjacobson.android.roastassistant.model.Details;

@Entity(tableName = "details_entity")
public class DetailsEntity extends RoastComponent implements Details {

    @PrimaryKey
    @ForeignKey(entity = RoastEntity.class, parentColumns = "id", childColumns = "roastId",
            onDelete = ForeignKey.CASCADE)
    private long roastId;

    private String date;
    private String beanType;
    private float batchSize;
    private float yield;
    private String roastDegree;

    private String roastNotes;
    private String tastingNotes;
    private String roaster;
    private int ambientTemperature;

    // todo do we need both constructors
    @Ignore
    public DetailsEntity(long roastId) {
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

    @Override
    public long getRoastId() {
        return roastId;
    }

    @Override
    public void setRoastId(long roastId) {
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
        return getBatchSize() > 0 ? (getBatchSize() - getYield()) / getBatchSize() : 0;
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
    public final boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        return obj instanceof DetailsEntity && getRoastId() == ((DetailsEntity) obj).getRoastId();
    }

    @Override
    public void setAmbientTemperature(int ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }
}
