package com.andrewkjacobson.android.roastassistant.model;

public interface Roast {
    long getRoastId();
    void setRoastId(long id);
    boolean isRunning();
    void setRunning(boolean isRunning);
    boolean isFinished();
    void setFinished(boolean isFinished);
    long getStartTime();
    void setStartTime(long seconds);
    void startRoast();
    void endRoast();
    void setCreatedTime(long epochMilli);
    long getCreatedTime();
}
