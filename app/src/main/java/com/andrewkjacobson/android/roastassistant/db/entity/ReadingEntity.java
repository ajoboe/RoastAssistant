package com.andrewkjacobson.android.roastassistant.db.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.andrewkjacobson.android.roastassistant.model.Reading;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "reading_entity") //, primaryKeys = {"roastId", "seconds"})
public class ReadingEntity extends RoastComponent implements Reading {
    @ForeignKey(entity = RoastEntity.class, parentColumns = "id", childColumns = "roastId",
            onDelete = ForeignKey.CASCADE)
    private int roastId;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int seconds;
    private int temperature;
    private int power;

    /**
     *
     * @param seconds time of reading in seconds
     * @param temperature the reading temperature
     * @param power the reading power percentage
     * @param roastId the id of the roast to which this reading belongs
     */
    public ReadingEntity(int seconds, int temperature, int power, int roastId) {
        setSeconds(seconds);
        setTemperature(temperature);
        setPower(power);
        setRoastId(roastId);
    }

    @Override
    public int getSeconds() {
        return seconds;
    }

    @Override
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public int getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public int getRoastId() {
        return roastId;
    }

    @Override
    public void setRoastId(int roastId) {
        this.roastId = roastId;
    }

    @NotNull
    public String toString() {
        return "Time: " + getSeconds()
                + " seconds. Temperature: " + getTemperature()
                + "° Power: " + getPower() + "%"
                + " RoastId: " + getRoastId()
                + " Id: " + getId();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        return obj instanceof ReadingEntity &&
                getSeconds() == ((ReadingEntity) obj).getSeconds() &&
                getTemperature() == ((ReadingEntity) obj).getTemperature() &&
                getPower() == ((ReadingEntity) obj).getPower() &&
                getRoastId() == ((ReadingEntity) obj).getRoastId();
    }
}
