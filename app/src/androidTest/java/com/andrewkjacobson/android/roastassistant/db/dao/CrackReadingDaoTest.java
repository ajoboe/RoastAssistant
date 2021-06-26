package com.andrewkjacobson.android.roastassistant.db.dao;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.andrewkjacobson.android.roastassistant.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant.db.entity.CrackReadingEntity;
import com.jraska.livedata.TestObserver;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CrackReadingDaoTest extends TestCase {
    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    private RoastRoomDatabase db;
    private CrackReadingDao dao;
    private List<CrackReadingEntity> expected;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, RoastRoomDatabase.class).build();
        dao = db.crackReadingDao();
        expected = new ArrayList<>();

        CrackReadingEntity r = new
                CrackReadingEntity(3, 200, 75, 1, true, 999);
        r.setId((int) dao.insert(r));
        expected.add(r);

        r = new CrackReadingEntity(9, 230, 50, 2,false, 999);
        r.setId((int) dao.insert(r));
        expected.add(r);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testGetAll() throws InterruptedException {
        TestObserver.test(dao.getAll(999))
                .awaitValue()
                .assertValue(expected);
    }

    @Test
    public void testGet() throws InterruptedException {
        TestObserver.test(dao.get(999, 3))
                .awaitValue()
                .assertValue(expected.get(0));
    }
}