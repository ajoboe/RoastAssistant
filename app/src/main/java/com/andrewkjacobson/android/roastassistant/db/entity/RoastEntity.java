package com.andrewkjacobson.android.roastassistant.db.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.andrewkjacobson.android.roastassistant.model.Roast;

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

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        return obj instanceof RoastEntity && getId() == ((RoastEntity) obj).getId();
    }
}
