package com.andrewkjacobson.android.roastassistant.model;

public interface Roast {
    int getId();
    void setId(int id);
    boolean isRunning();
    void setRunning(boolean isRunning);
    boolean isFinished();
    void setFinished(boolean isFinished);
    long getStartTime();
    void setStartTime(long seconds);
    void startRoast();
    void endRoast();
}
