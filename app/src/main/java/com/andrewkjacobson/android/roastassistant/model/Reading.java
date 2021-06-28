package com.andrewkjacobson.android.roastassistant.model;

// todo should a Reading be temp OR power, not both??
public interface Reading{
    int getSeconds();

    void setSeconds(int seconds);

    int getTemperature();

    void setTemperature(int temperature);

    int getPower();

    void setPower(int power);

    long getRoastId();

    void setRoastId(long roastId);

    long getId();

    void setId(long id);
}
