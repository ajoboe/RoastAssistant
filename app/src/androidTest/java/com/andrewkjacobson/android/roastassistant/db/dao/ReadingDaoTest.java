package com.andrewkjacobson.android.roastassistant.db.dao;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.andrewkjacobson.android.roastassistant.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant.db.entity.ReadingEntity;
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
public class ReadingDaoTest extends TestCase {
    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    private ReadingDao readingDao;
    private List<ReadingEntity> expected;
    private RoastRoomDatabase db;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, RoastRoomDatabase.class).build();
        readingDao = db.readingDao();
        expected = new ArrayList<>();

        ReadingEntity r = new ReadingEntity(3, 200, 75, 999);
        r.setId((int) readingDao.insert(r)); // this needs to happen in the repo and id should probably be completely hidden AND therefore we can't really do the serializing
        expected.add(r);

        r = new ReadingEntity(9, 230, 50, 999);
        r.setId((int) readingDao.insert(r));
        expected.add(r);
    }

    @After
    public void tearDown() {
        readingDao.deleteAll();
        db.close();
    }

    @Test
    public void testGet() {
        long roastId = expected.get(0).getRoastId();
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
        ReadingEntity r = new ReadingEntity(77, 666, 25, 888);
        readingDao.insert(r);
        try {
            TestObserver.test(readingDao.get(r.getRoastId(), r.getSeconds()))
                    .awaitValue()
                    .assertValue(r);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdate() throws InterruptedException {
        ReadingEntity r = expected.get(0);
        r.setPower(123);
        r.setTemperature(123);

        readingDao.update(r);
        TestObserver.test(readingDao.get(r.getRoastId(), r.getSeconds()))
                .awaitValue()
                .assertValue(r);
    }

    @Test
    public void testDelete() {
        ReadingEntity r = expected.get(0);
        try {
            // make sure it's there
            TestObserver.test(readingDao.get(r.getRoastId(), r.getSeconds()))
                    .awaitValue()
                    .assertValue(r);// todo shouldn't delete and get have the same prams??
            // make sure the correct number of items are there
            assertEquals(expected.size(),
                    TestObserver.test(readingDao.getAll(999))
                            .awaitValue()
                            .value()
                            .size()
            );
            // delete it and see if it's gone
            readingDao.delete(r.getRoastId(), r.getSeconds());
            TestObserver.test(readingDao.get(r.getRoastId(), r.getSeconds()))
                    .awaitValue()
                    .assertNullValue();

            // check that the correct number of items are left
            assertEquals(expected.size() - 1,
                TestObserver.test(readingDao.getAll(999)) // by number of items
                    .awaitValue()
                    .value()
                    .size()
            );
            // check that one of the items that's supposed to be there is still there
            TestObserver.test(readingDao.get(expected.get(0).getRoastId(),
                                             expected.get(0).getSeconds()))
                    .awaitValue()
                    .assertHasValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBaseDelete() throws InterruptedException {
        ReadingEntity r = expected.get(0);
        readingDao.delete(r);
        TestObserver.test(readingDao.get(r.getRoastId(), r.getSeconds()))
                .awaitValue()
                .assertNullValue();
    }
}