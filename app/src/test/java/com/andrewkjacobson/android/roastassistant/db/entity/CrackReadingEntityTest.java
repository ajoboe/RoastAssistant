package com.andrewkjacobson.android.roastassistant.db.entity;

import junit.framework.TestCase;

public class CrackReadingEntityTest extends TestCase {

    public void testSetHasOccurred() {
        CrackReadingEntity c = new CrackReadingEntity(400, 288, 50,
                1,false, 1);
        assertFalse(c.hasOccurred());
        c.setHasOccurred(true);
        assertTrue(c.hasOccurred());
    }

    public void testHasOccurred() {
        CrackReadingEntity c = new CrackReadingEntity(400, 288, 50,
                1,false, 1);
        assertFalse(c.hasOccurred());
        c = new CrackReadingEntity(400, 288, 50,
                1,true, 1);
        assertTrue(c.hasOccurred());
    }

    public void testSetCrackNumber() {
        CrackReadingEntity c = new CrackReadingEntity(400, 288, 50,
                1,false, 1);
        c.setCrackNumber(2);
        assertSame(2, c.getCrackNumber());
    }

    public void testGetCrackNumber() {
        CrackReadingEntity c = new CrackReadingEntity(400, 288, 50,
                1,false, 1);
        c.setCrackNumber(2);
        assertSame(2, c.getCrackNumber());
    }

    public void testTestToString() {
        CrackReadingEntity c = new CrackReadingEntity(400, 288, 50,
                1,false, 1);
        assertNotNull(c.toString());
    }
}