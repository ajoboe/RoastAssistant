package com.andrewkjacobson.android.roastassistant.db.entity;

import junit.framework.TestCase;

import java.time.Instant;

public class RoastEntityTest extends TestCase {

    private RoastEntity roastEntity;

    public void setUp() throws Exception {
        super.setUp();
        roastEntity = new RoastEntity();
    }

    public void testSetId() {
        roastEntity = new RoastEntity();
        roastEntity.setRoastId(1);
        assertEquals(1, roastEntity.getRoastId());
    }

    public void testSetRunning() {
        roastEntity = new RoastEntity();
        assertFalse(roastEntity.isRunning());
        roastEntity.setRunning(true);
        assertTrue(roastEntity.isRunning());
        roastEntity.setRunning(false);
        assertFalse(roastEntity.isRunning());
    }

    public void testGetId() {
        roastEntity = new RoastEntity();
        assertNotNull(roastEntity);
    }

    public void testStartRoast() {
        roastEntity = new RoastEntity();
        assertFalse(roastEntity.isRunning());
        roastEntity.startRoast();
        assertTrue(roastEntity.isRunning());
    }

    public void testEndRoast() {
        roastEntity = new RoastEntity();
        assertFalse(roastEntity.isRunning());
        roastEntity.startRoast();
        assertTrue(roastEntity.isRunning());
        roastEntity.endRoast();
        assertFalse(roastEntity.isRunning());
    }

    public void testIsRunning() {
        roastEntity = new RoastEntity();
        assertFalse(roastEntity.isRunning());
        roastEntity.startRoast();
        assertTrue(roastEntity.isRunning());
        roastEntity.endRoast();
        assertFalse(roastEntity.isRunning());
    }

    public void testIsFinished() {
        roastEntity = new RoastEntity();
        assertFalse(roastEntity.isFinished());
        roastEntity.endRoast(); // end without starting
        assertFalse(roastEntity.isFinished());
        roastEntity.startRoast();
        assertFalse(roastEntity.isFinished());
        roastEntity.endRoast();
        assertTrue(roastEntity.isFinished());
    }

    public void testSetFinished() {
        roastEntity = new RoastEntity();
        assertFalse(roastEntity.isFinished());
        roastEntity.setFinished(false);
        assertFalse(roastEntity.isFinished());
        roastEntity.setFinished(true);
        assertTrue(roastEntity.isFinished());
    }

    public void testSetStartTime() {
        roastEntity = new RoastEntity();
        roastEntity.setStartTime(123456l);
        assertEquals(123456l, roastEntity.getStartTime());
    }

    public void testGetStartTime() {
        roastEntity = new RoastEntity();
        roastEntity.setStartTime(123456l);
        assertEquals(123456l, roastEntity.getStartTime());
    }

    public void testSetCreatedTime() {
        roastEntity = new RoastEntity();
        roastEntity.setCreatedTime(123456l);
        assertEquals(123456l, roastEntity.getCreatedTime());
    }

    public void testGetCreatedTime() {
        roastEntity = new RoastEntity();
        long time = Instant.now().toEpochMilli();
        assertEquals(time, roastEntity.getCreatedTime());
    }

    public void testEquals() {
        RoastEntity r1 = new RoastEntity();
        RoastEntity r2 = new RoastEntity();
        r1.setRoastId(666);
        r2.setRoastId(999);
        assertNotSame(r1, r2);
        assertNotSame(r1.getRoastId(), r2.getRoastId());
        r1 = r2;
        assertEquals(r1, r2);
        assertEquals(r1.getRoastId(), r2.getRoastId());
    }
}