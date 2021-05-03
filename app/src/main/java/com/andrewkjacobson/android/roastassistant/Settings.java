package com.andrewkjacobson.android.roastassistant;

public class Settings {
    private final int temperatureCheckFrequency;
    private final int allowedTempChange;
    private final int startingTemperature;
    private final int startingPower;
    private final int roastTimeInSecAddend;
    private final int firstCrackLookaheadTime;
    private final int expectedRoastLength = 60 * 10; // todo should be a setting
    private final int maxGraphTemperature = 400; // todo should be a setting
    private final int minGraphTemperature = 50; // todo should be a setting

    public Settings(int temperatureCheckFrequency, int allowedTempChange, int startingTemperature,
                    int startingPower, int roastTimeInSecAddend, int firstCrackLookaheadTime) {
        this.temperatureCheckFrequency = temperatureCheckFrequency;
        this.allowedTempChange = allowedTempChange;
        this.startingTemperature = startingTemperature;
        this.startingPower = startingPower;
        this.roastTimeInSecAddend = roastTimeInSecAddend;
        this.firstCrackLookaheadTime = firstCrackLookaheadTime;
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

    public int getFirstCrackLookaheadTime() { return firstCrackLookaheadTime; }

    public int getExpectedRoastLength() {
        return expectedRoastLength;
    }

    public int getMaxGraphTemperature() {
        return maxGraphTemperature;
    }

    public int getMinGraphTemperature() {
        return minGraphTemperature;
    }
}
