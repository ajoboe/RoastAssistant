package com.andrewkjacobson.android.roastassistant1.model;

public interface Roast {
    int getId();
    void setId(int id);
    boolean isRunning();
    boolean isFinished();
    int getElapsed();
    int getAddend();
    long getStartTime();
    void startRoast();
    void incrementElapsed();
    void endRoast();
}
