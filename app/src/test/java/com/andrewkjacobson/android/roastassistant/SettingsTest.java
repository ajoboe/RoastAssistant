package com.andrewkjacobson.android.roastassistant;

import junit.framework.TestCase;

public class SettingsTest extends TestCase {

    public Settings s;
    int temperatureCheckFrequency = 15;
    int allowedTempChange = 101;
    int startingTemp = 250;
    int startingPow = 100;
    int addend = 15;
    int firstCrackLookahead = 15;
    int expectedRoastLength = 600;
    int maxGraphTemperature = 401;
    int minGraphTemperature = 50;

    public void setUp() throws Exception {
        super.setUp();
        s = new Settings(temperatureCheckFrequency, allowedTempChange, startingTemp,
                startingPow, addend, firstCrackLookahead, expectedRoastLength,
                maxGraphTemperature, minGraphTemperature);
    }

    public void testGetTemperatureCheckFrequency() {
        assertEquals(temperatureCheckFrequency, s.getTemperatureCheckFrequency());
    }

    public void testGetAllowedTempChange() {
        assertEquals(allowedTempChange, s.getAllowedTempChange());
    }

    public void testGetStartingTemperature() {
        assertEquals(startingTemp, s.getStartingTemperature());
    }

    public void testGetStartingPower() {
        assertEquals(startingPow, s.getStartingPower());
    }

    public void testGetRoastTimeInSecAddend() {
        assertEquals(addend, s.getRoastTimeInSecAddend());
    }

    public void testGetFirstCrackLookaheadTime() {
        assertEquals(firstCrackLookahead, s.getFirstCrackLookaheadTime());
    }

    public void testGetExpectedRoastLength() {
        assertEquals(expectedRoastLength, s.getExpectedRoastLength());
    }

    public void testGetMaxGraphTemperature() {
        assertEquals(maxGraphTemperature, s.getMaxGraphTemperature());
    }

    public void testGetMinGraphTemperature() {
        assertEquals(minGraphTemperature, s.getMinGraphTemperature());
    }
}