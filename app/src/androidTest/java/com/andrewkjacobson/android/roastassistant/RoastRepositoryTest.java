package com.andrewkjacobson.android.roastassistant;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.andrewkjacobson.android.roastassistant.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.RoastEntity;
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
    public void tearDown() {
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
            repository.insert(r);
            Thread.sleep(2);
        }

        List<RoastEntity> all = TestObserver.test(repository.getAllRoasts())
                .value();

        long lastCreatedTime = 0;
        for(RoastEntity r : all) {
            if(r.getCreatedTime() > lastCreatedTime) {
                lastCreatedTime = r.getCreatedTime();
            }
        }
        RoastEntity ret = TestObserver.test(repository.getMostRecentRoast())
                .awaitValue()
                .value();

        assertEquals(lastCreatedTime, ret.getCreatedTime());
    }

    @Test
    public void testInsertDetails() {
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
    public void testInsertCrackReadingEntity() throws InterruptedException {
        CrackReadingEntity exp = new CrackReadingEntity(1,222,90,1,false,888);
        repository.insert(exp, (id) -> {
            try {
                assertTrue(
                        TestObserver.test(repository.getCracksLiveData(888))
                            .awaitValue()
                            .value().contains(exp)
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        CrackReadingEntity exp2 = new CrackReadingEntity(2,333,55,2,true,777);
        repository.insert(exp2);
        Thread.sleep(100);
        assertTrue(
            TestObserver.test(repository.getCracksLiveData(777))
                .awaitValue()
                .value()
                .contains(exp2)
        );
    }

    @Test
    public void testInsertReadingEntity() throws InterruptedException {
        ReadingEntity exp = new ReadingEntity(1,222,90,888);
        repository.insert(exp, (id) -> {
            try {
                assertTrue(
                        TestObserver.test(repository.getReadingsLiveData(888))
                                .awaitValue()
                                .value().contains(exp)
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        ReadingEntity exp2 = new ReadingEntity(2,333,55,777);
        repository.insert(exp2);
        Thread.sleep(100);
        assertTrue(
                TestObserver.test(repository.getReadingsLiveData(777))
                        .awaitValue()
                        .value()
                        .contains(exp2)
        );
    }
    @Test
    public void testInsertRoastEntity() {
        RoastEntity r = new RoastEntity();
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
    public void testMultiInsert() {
        List<ReadingEntity> readings = new ArrayList<>(4);
        readings.add(new ReadingEntity(0, 250, 100, 999));
        readings.add(new ReadingEntity(5, 150, 90, 999));
        readings.add(new ReadingEntity(10, 160, 80, 999));
        ReadingEntity[] r = readings.toArray(readings.toArray(new ReadingEntity[0]));
        repository.insertAll(r);

        ReadingEntity[] r2 = new ReadingEntity[1];
        r2[0] = new ReadingEntity(15, 111, 33, 111);
        repository.insertAll(r2);
        try {
            Thread.sleep(100); // todo should use the version w/ callback
            List<ReadingEntity> ret = TestObserver.test(repository.getReadingsLiveData(999))
                    .awaitValue()
                    .value();
            assertTrue(ret.containsAll(readings));

            List<ReadingEntity> ret2 = TestObserver.test(repository.getReadingsLiveData(111))
                    .awaitValue()
                    .value();
            assertEquals(r2[0], ret2.get(0));
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdate() throws InterruptedException {
        ReadingEntity exp = new ReadingEntity(1,222,90,888);
        repository.insert(exp, (id) -> {
            try {
                assertTrue(
                        TestObserver.test(repository.getReadingsLiveData(888))
                                .awaitValue()
                                .value().contains(exp)
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        exp.setTemperature(333);
        repository.update(exp);
        List<ReadingEntity> act = TestObserver.test(repository.getReadingsLiveData(888))
                .awaitValue()
                .value();

        assertEquals(exp, act.get(act.size() - 1));

    }

    @Test
    public void testDelete() {
        ReadingEntity exp = new ReadingEntity(1,222,90,888);
        repository.insert(exp, (id) -> {
            try {
                assertTrue(
                        TestObserver.test(repository.getReadingsLiveData(888))
                                .awaitValue()
                                .value().contains(exp)
                );
                repository.delete(exp);
                Thread.sleep(50);
                assertFalse(
                    TestObserver.test(repository.getReadingsLiveData(888))
                        .awaitValue()
                        .value()
                        .contains(exp));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testDeleteAllReadings() throws InterruptedException {
        List<ReadingEntity> readings = new ArrayList<>(4);
        readings.add(new ReadingEntity(0, 250, 100, 999));
        readings.add(new ReadingEntity(5, 150, 90, 999));
        readings.add(new ReadingEntity(10, 160, 80, 999));

        for(ReadingEntity r : readings) {
            repository.insert(r); // todo need a multi-insert
        }

        Thread.sleep(50); // todo should use the version w/ callback...need multi-insert first
        List<ReadingEntity> ret = TestObserver.test(repository.getReadingsLiveData(999))
                .awaitValue()
                .value();
        assertEquals(readings.size(), ret.size());
        assertTrue(ret.containsAll(readings));

        repository.deleteAllReadings();
        Thread.sleep(100);
        assertEquals(0,
            TestObserver.test(repository.getReadingsLiveData(999))
                .awaitValue()
                .value().size());
    }
}