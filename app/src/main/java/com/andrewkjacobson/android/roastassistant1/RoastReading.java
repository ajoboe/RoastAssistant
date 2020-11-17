package com.andrewkjacobson.android.roastassistant1;

public class RoastReading {
    private int timeStamp;
    private int temperature;
    private int powerPercentage;

    RoastReading(int timeStamp, int temperature, int powerPercentage) {
        setTimeStamp(timeStamp);
        setTemperature(temperature);
        setPowerPercentage(powerPercentage);
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getPowerPercentage() {
        return powerPercentage;
    }

    public void setPowerPercentage(int powerPercentage) {
        this.powerPercentage = powerPercentage;
    }
}
