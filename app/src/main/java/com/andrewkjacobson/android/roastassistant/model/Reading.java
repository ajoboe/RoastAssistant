package com.andrewkjacobson.android.roastassistant.model;

// todo should a Reading be temp OR power, not both??
public interface Reading{
    int getSeconds();

    void setSeconds(int seconds);

    int getTemperature();

    void setTemperature(int temperature);

    int getPower();

    void setPower(int power);

    int getRoastId();

    void setRoastId(int roastId);

    int getId();

    void setId(int id);
}
