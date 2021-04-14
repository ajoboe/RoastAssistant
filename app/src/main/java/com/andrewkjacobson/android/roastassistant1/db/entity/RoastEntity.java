package com.andrewkjacobson.android.roastassistant1.db.entity;

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

    private boolean isRunning = false;
    private boolean isFinished = false;
    private long startTime = -1;

    // constructors
    public RoastEntity() {
        this.id = Long.valueOf(Instant.now().getEpochSecond()).intValue();
    }

    // public methods
    @Override
    public void setId(int roastId) {
        this.id = roastId;
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
}
