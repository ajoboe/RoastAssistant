package com.andrewkjacobson.android.roastassistant.db;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import junit.framework.TestCase;

public class RoastRoomDatabaseTest extends TestCase {

    public void testGetDatabase() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(appContext);
        assertNotNull(db);
    }
}