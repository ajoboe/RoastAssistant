package com.andrewkjacobson.android.roastassistant;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.andrewkjacobson.android.roastassistant.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant.model.Roast;
import com.jraska.livedata.TestObserver;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RoastRepositoryTest extends TestCase {

    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    public RoastRepository repository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        repository = new RoastRepository(context);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetRoastLiveData() throws InterruptedException {
        RoastEntity r = new RoastEntity(); // todo hide setId...make it protected?
        r.setStartTime(123);
//        long id = repository.insert(r);
        repository.insertRoast(r, (id) -> {
            try {
                TestObserver.test(repository.getRoastLiveData(id))
                        .awaitValue()
                        .assertValue(r);
            } catch (InterruptedException e) {

            }
        });
    }

    public void testGetDetailsLiveData() {
        fail();
    }

    public void testGetReadingsLiveData() {
        fail();
    }

    public void testGetCracksLiveData() {
        fail();
    }

    public void testGetMostRecentRoast() {
        fail();
    }

    public void testInsert() {
        fail();
    }

    public void testUpdate() {
        fail();
    }

    public void testDelete() {
        fail();
    }

    public void testDeleteAllReadings() {
        fail();
    }
}