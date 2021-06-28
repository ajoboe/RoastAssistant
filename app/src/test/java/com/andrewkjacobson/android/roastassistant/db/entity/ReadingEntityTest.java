package com.andrewkjacobson.android.roastassistant.db.entity;

import junit.framework.TestCase;


public class ReadingEntityTest extends TestCase {

    private ReadingEntity reading;

    public void setUp() throws Exception {
        super.setUp();
        reading = new ReadingEntity(3,100,75, 1);
    }

    public void testGetSeconds() {
        assertEquals(3, reading.getSeconds());
        ReadingEntity r = new ReadingEntity(-1,100,75, 1);
        assertEquals(-1, r.getSeconds());
        r = new ReadingEntity(0,100,75, 1);
        assertEquals(0, r.getSeconds());
    }

    public void testSetSeconds() {
        reading.setSeconds(1);
        assertEquals(1, reading.getSeconds());
        reading.setSeconds(-1);
        assertEquals(-1, reading.getSeconds());
        reading.setSeconds(0);
        assertEquals(0, reading.getSeconds());
    }

    public void testGetTemperature() {
        assertEquals(100, reading.getTemperature());
        ReadingEntity r = new ReadingEntity(-1,-1,75, 1);
        assertEquals(-1, r.getTemperature());
        r = new ReadingEntity(0,0,75, 1);
        assertEquals(0, r.getTemperature());
    }

    public void testSetTemperature() {
        reading.setTemperature(1);
        assertEquals(1, reading.getTemperature());
        reading.setTemperature(-1);
        assertEquals(-1, reading.getTemperature());
        reading.setTemperature(0);
        assertEquals(0, reading.getTemperature());
    }

    public void testGetPower() {
        assertEquals(75, reading.getPower());
        ReadingEntity r = new ReadingEntity(-1,100,-1, 1);
        assertEquals(-1, r.getPower());
        r = new ReadingEntity(0,100,0, 1);
        assertEquals(0, r.getPower());
    }

    public void testSetPower() {
        reading.setPower(1);
        assertEquals(1, reading.getPower());
        reading.setPower(-1);
        assertEquals(-1, reading.getPower());
        reading.setPower(0);
        assertEquals(0, reading.getPower());
    }

    public void testGetRoastId() {
        reading.setRoastId(1);
        assertEquals(1, reading.getRoastId());
    }

    public void testSetRoastId() {
        reading.setRoastId(1);
        assertEquals(1, reading.getRoastId());
    }

    public void testGetId() {
        reading.setId(1);
        assertEquals(1, reading.getId());
    }

    public void testSetId() {
        reading.setId(1);
        assertEquals(1, reading.getId());
    }

    public void testToString() {
        assertNotNull(reading.toString());
    }

    public void testEquals() {
        assertEquals(reading, reading);
        ReadingEntity r = new ReadingEntity(2, 111,100, 55);
        assertNotSame(r, reading);
        assert r != reading;
    }
}