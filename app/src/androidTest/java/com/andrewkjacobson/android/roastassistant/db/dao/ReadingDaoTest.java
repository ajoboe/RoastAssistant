package com.andrewkjacobson.android.roastassistant.db.dao;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.andrewkjacobson.android.roastassistant.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant.viewmodel.RoastViewModel;
import com.jraska.livedata.TestObserver;

import junit.framework.TestCase;

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

    @Before
    public void setUp() {
        application = mock(Application.class);
//        viewModel = new RoastViewModel(application, null);

        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(application);
        readingDao = db.readingDao();

    }

//    @Test
//    public void testGet() {
//        fail("NOT IMPLEMENTED...");
//    }

    @Test
    public void testGetAll() {
//        fail("NOT IMPLEMENTED...");

        ReadingEntity r = new ReadingEntity(3, 200, 75, 999);
//        viewModel.recordTemperature("100");
        readingDao.insert(r);
        LiveData<List<ReadingEntity>> readings = readingDao.getAll(999);
//        LiveData<List<ReadingEntity>> readings = viewModel.getReadingsLiveData();

        List expected = new ArrayList<ReadingEntity>();
        expected.add(r);

        try {
            assertEquals(r,
                TestObserver.test(readings)
                    .awaitValue()
                    .assertHasValue()
                    .value()
                    .get(0));

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted Exception");
        }
    }

//    @Test
//    public void testDeleteAll() {
//        fail("NOT IMPLEMENTED...");
//    }
}