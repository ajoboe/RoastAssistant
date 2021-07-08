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
    @PrimaryKey(autoGenerate = true)
    private long roastId;

    private boolean isRunning = false;
    private boolean isFinished = false;
    private long startTime = -1;
    private long createdTime;

    // constructors
    public RoastEntity() {
//        this.id = Long.valueOf(Instant.now().getEpochSecond()).intValue();
        this.createdTime = Instant.now().toEpochMilli();
    }

    // public methods
    @Override
    public void setRoastId(long roastId) {
        this.roastId = roastId;
    }

    @Override
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public long getRoastId() {
        return roastId;
    }

    @Override
    public void startRoast() {
        isRunning = true;
    }

    @Override
    public void endRoast() {
        if(isRunning()) {
            isRunning = false;
            isFinished = true;
        }
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

    /**
     *  Do not use; this is set automatically in the constructor. This method is for use by
     *  Room only.
     *
     * @param epochMilli Do not use.
     */
    @Override
    public void setCreatedTime(long epochMilli) {
        this.createdTime = epochMilli;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        return obj instanceof RoastEntity && getRoastId() == ((RoastEntity) obj).getRoastId();
    }
}
