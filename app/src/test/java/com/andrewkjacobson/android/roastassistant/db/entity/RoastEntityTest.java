package com.andrewkjacobson.android.roastassistant.db.entity;

import junit.framework.TestCase;

public class RoastEntityTest extends TestCase {

    // todo try and make two with the same id

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
        roastEntity.startRoast();
        assertTrue(roastEntity.isRunning());
    }

    public void testGetId() {
    }

    public void testStartRoast() {
    }

    public void testEndRoast() {
    }

    public void testIsRunning() {
    }

    public void testIsFinished() {
    }

    public void testSetFinished() {
    }

    public void testSetStartTime() {
    }

    public void testGetStartTime() {
    }

    public void testEquals() {
        RoastEntity r1 = new RoastEntity();
        RoastEntity r2 = new RoastEntity();
        assertNotSame(r1, r2);
        assertNotSame(r1.getId(), r2.getId());
        r1 = r2;
        assertEquals(r1, r2);
        assertEquals(r1.getId(), r2.getId());
    }
}