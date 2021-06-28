package com.andrewkjacobson.android.roastassistant.db.entity;

import junit.framework.TestCase;

public class DetailsEntityTest extends TestCase {

    public void testGetRoastId() {
        DetailsEntity d = new DetailsEntity(1);
        assertEquals(1, d.getRoastId());
    }

    public void testSetRoastId() {
        DetailsEntity d = new DetailsEntity();
        d.setRoastId(1);
        assertEquals(1, d.getRoastId());
    }

    public void testGetDate() {
        DetailsEntity d = new DetailsEntity();
        d.setDate("1/1/2022");
        assertEquals("1/1/2022", d.getDate());
    }

    public void testSetDate() {
        DetailsEntity d = new DetailsEntity();
        d.setDate("1/1/2022");
        assertEquals("1/1/2022", d.getDate());
    }

    public void testGetBeanType() {
        DetailsEntity d = new DetailsEntity();
        d.setBeanType("PNG");
        assertEquals("PNG", d.getBeanType());
    }

    public void testSetBeanType() {
        DetailsEntity d = new DetailsEntity();
        d.setBeanType("PNG");
        assertEquals("PNG", d.getBeanType());
    }

    public void testGetBatchSize() {
        DetailsEntity d = new DetailsEntity();
        d.setBatchSize(101.22f);
        assertEquals(101.22f, d.getBatchSize());
    }

    public void testSetBatchSize() {
        DetailsEntity d = new DetailsEntity();
        d.setBatchSize(101.22f);
        assertEquals(101.22f, d.getBatchSize());
    }

    public void testGetYield() {
        DetailsEntity d = new DetailsEntity();
        d.setYield(101.22f);
        assertEquals(101.22f, d.getYield());
    }

    public void testSetYield() {
        DetailsEntity d = new DetailsEntity();
        d.setYield(101.22f);
        assertEquals(101.22f, d.getYield());
    }

    public void testGetWeightLossPercentage() {
        DetailsEntity d = new DetailsEntity();
        d.setBatchSize(100);
        d.setYield(87);
        assertEquals(0.13f, d.getWeightLossPercentage());
    }

    public void testGetRoastDegree() {
        DetailsEntity d = new DetailsEntity();
        d.setRoastDegree("hottt");
        assertEquals("hottt", d.getRoastDegree());
    }

    public void testSetRoastDegree() {
        DetailsEntity d = new DetailsEntity();
        d.setRoastDegree("hottt");
        assertEquals("hottt", d.getRoastDegree());
    }

    public void testGetRoastNotes() {
        DetailsEntity d = new DetailsEntity();
        d.setRoastNotes("i made roast \n it plenty good");
        assertEquals("i made roast \n it plenty good", d.getRoastNotes());
    }

    public void testSetRoastNotes() {
        DetailsEntity d = new DetailsEntity();
        d.setRoastNotes("i made roast \n it plenty good");
        assertEquals("i made roast \n it plenty good", d.getRoastNotes());
    }

    public void testGetTastingNotes() {
        DetailsEntity d = new DetailsEntity();
        d.setTastingNotes("much taste");
        assertEquals("much taste", d.getTastingNotes());
    }

    public void testSetTastingNotes() {
        DetailsEntity d = new DetailsEntity();
        d.setTastingNotes("much taste");
        assertEquals("much taste", d.getTastingNotes());
    }

    public void testGetRoaster() {
        DetailsEntity d = new DetailsEntity();
        d.setRoaster("be more");
        assertEquals("be more", d.getRoaster());
        assertNotSame("chicken shit", d.getRoaster());
    }

    public void testSetRoaster() {
        DetailsEntity d = new DetailsEntity();
        d.setRoaster("be more");
        assertEquals("be more", d.getRoaster());
        assertNotSame("chicken shit", d.getRoaster());
    }

    public void testGetAmbientTemperature() {
        DetailsEntity d = new DetailsEntity();
        d.setAmbientTemperature(100);
        assertEquals(100, d.getAmbientTemperature());
        assertNotSame(-100, d.getAmbientTemperature());
    }

    public void testSetAmbientTemperature() {
        DetailsEntity d = new DetailsEntity();
        d.setAmbientTemperature(100);
        assertEquals(100, d.getAmbientTemperature());
        assertNotSame(-100, d.getAmbientTemperature());
    }

    public void testDescribeContents() {
        DetailsEntity d = new DetailsEntity();
        assertNotNull(d.describeContents());
    }
}