package com.andrewkjacobson.android.roastassistant.db.entity;

import junit.framework.TestCase;

public class RoastEntityTest extends TestCase {

    private RoastEntity roastEntity;

    public void setUp() throws Exception {
        super.setUp();
        roastEntity = new RoastEntity();
    }

    public void testSetId() {
        roastEntity = new RoastEntity();
        roastEntity.setId(1);
        assertEquals(1, roastEntity.getId());
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

    public void testEquals() {
        RoastEntity r1 = new RoastEntity();
        RoastEntity r2 = new RoastEntity();
        r1.setId(666);
        r2.setId(999);
        assertNotSame(r1, r2);
        assertNotSame(r1.getId(), r2.getId());
        r1 = r2;
        assertEquals(r1, r2);
        assertEquals(r1.getId(), r2.getId());
    }
}