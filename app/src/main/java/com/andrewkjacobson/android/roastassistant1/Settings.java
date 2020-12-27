package com.andrewkjacobson.android.roastassistant1;

public class Settings {
    private int temperatureCheckFrequency;
    private int allowedTempChange;
    private int startingTemperature;
    private int startingPower;
    private int roastTimeInSecAddend;
    private final int expectedRoastLength = 60 * 12; // todo should be a setting
    private final int maxGraphTemperature = 400; // todo should be a setting

    public Settings(int temperatureCheckFrequency, int allowedTempChange, int startingTemperature, int startingPower, int roastTimeInSecAddend) {
        this.temperatureCheckFrequency = temperatureCheckFrequency;
        this.allowedTempChange = allowedTempChange;
        this.startingTemperature = startingTemperature;
        this.startingPower = startingPower;
        this.roastTimeInSecAddend = roastTimeInSecAddend;
    }

    public int getTemperatureCheckFrequency() {
        return temperatureCheckFrequency;
    }

    public int getAllowedTempChange() {
        return allowedTempChange;
    }

    public int getStartingTemperature() {
        return startingTemperature;
    }

    public int getStartingPower() {
        return startingPower;
    }

    public int getRoastTimeInSecAddend() {
        return roastTimeInSecAddend;
    }

    public int getExpectedRoastLength() {
        return expectedRoastLength;
    }

    public int getMaxGraphTemperature() {
        return maxGraphTemperature;
    }
}
