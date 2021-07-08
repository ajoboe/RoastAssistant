package com.andrewkjacobson.android.roastassistant;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.andrewkjacobson.android.roastassistant.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant.model.Roast;
import com.jraska.livedata.TestObserver;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
        repository.deleteAllReadings();
        repository.deleteAllCracks();
    }

    @Test
    public void testGetRoastLiveData() {
        RoastEntity r = new RoastEntity(); // todo hide setId...make it protected?
        r.setStartTime(123);
        repository.insert(r, (id) -> {
            try {
                TestObserver.test(repository.getRoastLiveData(id))
                        .awaitValue()
                        .assertValue(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testGetDetailsLiveData() {
        DetailsEntity d = new DetailsEntity(); // todo hide setId...make it protected?
        d.setBatchSize(125);
        repository.insert(d, (id) -> {
            try {
                TestObserver.test(repository.getDetailsLiveData(id))
                        .awaitValue()
                        .assertValue(d);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testGetReadingsLiveData() throws InterruptedException {
        List<ReadingEntity> readings = new ArrayList<>(4);
        readings.add(new ReadingEntity(0, 250, 100, 999));
        readings.add(new ReadingEntity(5, 150, 90, 999));
        readings.add(new ReadingEntity(10, 160, 80, 999));

        for(ReadingEntity r : readings) {
            repository.insert(r); // todo need a multi-insert
        }

        Thread.sleep(100); // todo should use the version w/ callback...need multi-insert first
        List<ReadingEntity> ret = TestObserver.test(repository.getReadingsLiveData(999))
                .awaitValue()
                .value();

        assertEquals(readings.size(), ret.size());
        assertTrue(ret.containsAll(readings));
    }

    @Test
    public void testGetCracksLiveData() throws InterruptedException {
        List<CrackReadingEntity> cracks = new ArrayList<>(4);
        cracks.add(new CrackReadingEntity(0, 250, 100, 1, true, 999 ));
        cracks.add(new CrackReadingEntity(5, 150, 90, 2, false, 999));
        cracks.add(new CrackReadingEntity(10, 160, 80, 1, true, 999));

        for(ReadingEntity r : cracks) {
            repository.insert(r); // todo need a multi-insert
        }

        Thread.sleep(100); // todo should use the version w/ callback...need multi-insert first
        List<CrackReadingEntity> ret = TestObserver.test(repository.getCracksLiveData(999))
                .awaitValue()
                .value();

        assertEquals(cracks.size(), ret.size());
        assertTrue(ret.containsAll(cracks));
    }

    @Test
    public void testGetMostRecentRoast() throws InterruptedException {
        for(int i = 0; i <= 2; i++) {
            RoastEntity r = new RoastEntity();
            r.setStartTime(Instant.now().toEpochMilli());
            repository.insert(r);
        }

        List<RoastEntity> all = TestObserver.test(repository.getAllRoasts())
                .value();

        long lastStartTime = 0;
        for(RoastEntity r : all) {
            if(r.getStartTime() > lastStartTime) {
                lastStartTime = r.getStartTime();
            }
        }
        // todo judges most recent by start time--therefore, this should be set automatically
        RoastEntity ret = TestObserver.test(repository.getMostRecentRoast())
                .awaitValue()
                .value();

        assertEquals(lastStartTime, ret.getStartTime());
    }

    public void testInsert() {
        fail();
    }

    public void testMultiInsert() {
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