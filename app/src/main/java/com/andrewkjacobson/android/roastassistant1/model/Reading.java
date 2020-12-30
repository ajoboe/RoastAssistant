package com.andrewkjacobson.android.roastassistant1.model;

public interface Reading {
    int getSeconds();

    void setSeconds(int seconds);

    int getTemperature();

    void setTemperature(int temperature);

    int getPower();

    void setPower(int power);

    int getRoastId();

    void setRoastId(int roastId);
}