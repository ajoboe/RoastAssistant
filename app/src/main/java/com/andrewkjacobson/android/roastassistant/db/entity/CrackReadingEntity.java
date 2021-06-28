package com.andrewkjacobson.android.roastassistant.db.entity;

import androidx.room.Entity;

import com.andrewkjacobson.android.roastassistant.model.Crack;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "crack_reading_entity")
public class CrackReadingEntity extends ReadingEntity implements Crack {
    private boolean hasOccurred = false;
    private int crackNumber = 1;

    /**
     * @param seconds     time of reading in seconds
     * @param temperature the reading temperature
     * @param power       the reading power percentage
     */
    public CrackReadingEntity(int seconds, int temperature, int power, int crackNumber, boolean hasOccurred, long roastId) {
        super(seconds, temperature, power, roastId);
        this.crackNumber = crackNumber;
        this.hasOccurred = hasOccurred;
    }

    @Override
    public void setHasOccurred(boolean hasOccurred) {
        this.hasOccurred = hasOccurred;
    }

    @Override
    public boolean hasOccurred() {
        return hasOccurred;
    }

    @Override
    public void setCrackNumber(int crackNumber) {
        this.crackNumber = crackNumber;
    }

    @Override
    public int getCrackNumber() {
        return crackNumber;
    }

    @NotNull
    @Override
    public String toString() {
        return super.toString() + " Crack #" + getCrackNumber()
                                + " Has occurred: " + hasOccurred();
    }
}
