package com.andrewkjacobson.android.roastassistant1.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.andrewkjacobson.android.roastassistant1.model.Reading;
import com.andrewkjacobson.android.roastassistant1.model.ReadingDeleteMe;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "reading_entity", primaryKeys = {"roastId", "seconds"})
public class ReadingEntity implements Reading {
    @ForeignKey(entity = RoastEntity.class, parentColumns = "id", childColumns = "roastId",
            onDelete = ForeignKey.CASCADE)
    private int roastId;

    private int seconds;
    private int temperature;
    private int power;

    /**
     *
     * @param seconds time of reading in seconds
     * @param temperature the reading temperature
     * @param power the reading power percentage
     */
    public ReadingEntity(int seconds, int temperature, int power) {
        setSeconds(seconds);
        setTemperature(temperature);
        setPower(power);
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
        return "Timestamp: " + getSeconds()
                + " seconds. Temperature: " + getTemperature()
                + "° Power: " + getPower() + "%";
    }
}
