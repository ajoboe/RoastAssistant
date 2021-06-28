package com.andrewkjacobson.android.roastassistant.model;

public interface Roast {
    long getId();
    void setId(long id);
    boolean isRunning();
    void setRunning(boolean isRunning);
    boolean isFinished();
    void setFinished(boolean isFinished);
    long getStartTime();
    void setStartTime(long seconds);
    void startRoast();
    void endRoast();
}
