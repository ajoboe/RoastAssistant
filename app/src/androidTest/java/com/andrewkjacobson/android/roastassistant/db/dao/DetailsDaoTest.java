package com.andrewkjacobson.android.roastassistant.db.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.andrewkjacobson.android.roastassistant.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;
import com.jraska.livedata.TestObserver;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DetailsDaoTest extends TestCase {
    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    RoastRoomDatabase db;
    DetailsDao dao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, RoastRoomDatabase.class).build();
        dao = db.detailsDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsert() throws InterruptedException {
        DetailsEntity deets = new DetailsEntity();
        deets.setRoastId(666); // primary key
        deets.setAmbientTemperature(69);
        deets.setTastingNotes("yum");

        dao.insert(deets);
        TestObserver.test(dao.get(666))
                .awaitValue()
                .assertValue(deets);
    }

    @Test
    public void testGet() throws InterruptedException {
        DetailsEntity deets = new DetailsEntity();
        deets.setRoastId(666); // primary key
        deets.setAmbientTemperature(69);
        deets.setTastingNotes("yum");

        dao.insert(deets);
        TestObserver.test(dao.get(666))
                .awaitValue()
                .assertValue(deets);
    }
}