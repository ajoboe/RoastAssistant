package com.andrewkjacobson.android.roastassistant.db;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RoastRoomDatabaseTest extends TestCase {

    @Test
    public void testGetDatabase() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(appContext);
        assertNotNull(db);
    }
}