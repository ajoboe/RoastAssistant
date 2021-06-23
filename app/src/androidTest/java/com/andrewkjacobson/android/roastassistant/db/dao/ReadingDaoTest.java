package com.andrewkjacobson.android.roastassistant.db.dao;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.andrewkjacobson.android.roastassistant.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant.viewmodel.RoastViewModel;
import com.jraska.livedata.TestObserver;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class ReadingDaoTest extends TestCase {
    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    private RoastViewModel viewModel;
    private Application application;
    private ReadingDao readingDao;
    private List<ReadingEntity> expected;

    @Before
    public void setUp() {
        application = mock(Application.class);

        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(application);
        readingDao = db.readingDao();
        expected = new ArrayList<ReadingEntity>();

        ReadingEntity r = new ReadingEntity(3, 200, 75, 999);
        expected.add(r);
        readingDao.insert(r);
        r = new ReadingEntity(9, 230, 50, 999);
        expected.add(r);
        readingDao.insert(r);
    }

    @After
    public void tearDown() {
        readingDao.deleteAll();
    }

    @Test
    public void testGet() {
        int roastId = expected.get(0).getRoastId();
        int seconds = expected.get(0).getSeconds();
        LiveData<ReadingEntity> retrieved = readingDao.get(roastId, seconds);

        try {
            TestObserver.test(retrieved)
                        .awaitValue()
                        .assertValue(expected.get(0));
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted Exception");
        }
    }

    @Test
    public void testGetAll() {
        LiveData<List<ReadingEntity>> retrieved = readingDao.getAll(999);

        try {
            TestObserver.test(retrieved)
                            .awaitValue()
                            .assertValue(expected);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted Exception");
        }
    }

    @Test
    public void testDeleteAll() {
        readingDao.deleteAll();
        LiveData<List<ReadingEntity>> readings = readingDao.getAll(999);

        try {
            assertTrue(TestObserver.test(readings)
                    .awaitValue()
                    .assertHasValue()
                    .value()
                    .isEmpty()
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted Exception");
        }
    }

    @Test
    public void testInsert() {
        fail();
    }

    @Test
    public void testUpdate() {
        fail();
    }

    @Test
    public void testDelete() {
        fail();
        // check that intended item is gone
        // check that other items are still there
            // by number of items
            // by item equality for one
    }

//    @Test
//    public void testUpsert() {
//        fail();
//    }
}