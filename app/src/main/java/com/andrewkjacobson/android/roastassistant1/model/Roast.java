package com.andrewkjacobson.android.roastassistant1.model;

public interface Roast {
    int getId();
    void setId(int id);
    boolean isRunning();
    void setRunning(boolean isRunning);
    boolean isFinished();
    void setFinished(boolean isFinished);
//    int getElapsed();
//    void setElapsed(int seconds);
//    void incrementElapsed();
    long getStartTime();
    void setStartTime(long seconds);
    void startRoast();
    void endRoast();
}
