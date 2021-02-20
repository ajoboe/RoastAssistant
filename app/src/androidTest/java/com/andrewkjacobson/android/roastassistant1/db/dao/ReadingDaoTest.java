package com.andrewkjacobson.android.roastassistant1.db.dao;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.andrewkjacobson.android.roastassistant1.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant1.viewmodel.RoastViewModel;
import com.jraska.livedata.TestObserver;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class ReadingDaoTest extends TestCase {
    @Rule
        public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    private RoastViewModel viewModel;
    private Application application;

    @Before
    public void setUp() {
        application = mock(Application.class);
        viewModel = new RoastViewModel(application, null);
    }

    @Test
    public void testGetAll() {
        ReadingEntity r = new ReadingEntity(3, 200, 75, 999);
        viewModel.recordTemperature("100");
//        readingDao.insert(r);
//        LiveData<List<ReadingEntity>> readings = readingDao.getAll(999);
        LiveData<List<ReadingEntity>> readings = viewModel.getReadings();

        try {
            TestObserver.test(readings)
                    .awaitValue()
                    .assertHasValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Interrupted Exception");
        }
    }

//    @Test
//    public void testGet() {
//        createDb();
//        ReadingEntity r = new ReadingEntity(3, 200, 75, 999);
//        readingDao.insert(r);
//
//        LiveData<ReadingEntity> readings = readingDao.get(999, 3);
//        System.out.println(readings);
//        assertEquals(readings.getValue(), r);
//    }
}