package com.andrewkjacobson.android.roastassistant.db.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.andrewkjacobson.android.roastassistant.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant.db.entity.RoastEntity;
import com.jraska.livedata.TestObserver;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RoastDaoTest extends TestCase {
    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    private RoastRoomDatabase db;
    private RoastDao dao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, RoastRoomDatabase.class).build();
        dao = db.roastDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsert() throws InterruptedException {
        RoastEntity roast = new RoastEntity();
        RoastEntity roast2 = new RoastEntity();
        roast.startRoast();
        roast2.startRoast();
        roast2.endRoast();
        long id = dao.insert(roast);
        roast.setRoastId(id);
        long id2 = dao.insert(roast2);
        roast2.setRoastId((int) id2);

        assertEquals(id, roast.getRoastId());
        TestObserver.test(dao.getLiveData((int) id))
                .awaitValue()
                .assertValue(roast);
        assertNotSame(roast,
            TestObserver.test(dao.getLiveData((int) id2))
                .awaitValue()
                .value());
    }

    @Test
    public void testGetAll() throws InterruptedException {
        RoastEntity roast = new RoastEntity();
        RoastEntity roast2 = new RoastEntity();
        roast.startRoast();
        roast2.startRoast();
        roast2.endRoast();
        long id = dao.insert(roast);
        roast.setRoastId((int)id);
        long id2 = dao.insert(roast2);
        roast.setRoastId((int)id2);
        assert !roast.equals(roast2);

        List<RoastEntity> roasts = TestObserver.test(dao.getAll()).awaitValue().value();
        assertEquals(2, roasts.size());
        assertEquals(id, roasts.get(0).getRoastId());
    }

    @Test
    public void testGetLiveData() throws InterruptedException {
        RoastEntity roast = new RoastEntity();
        roast.startRoast();
        long id = dao.insert(roast);
        roast.setRoastId((int) id);

        assertEquals(id, roast.getRoastId());
        TestObserver.test(dao.getLiveData((int) id))
                .awaitValue()
                .assertValue(roast);
    }

//    @Test
//    public void testGetMostRecent() throws InterruptedException {
//        RoastEntity roast = new RoastEntity();
//        RoastEntity roast2 = new RoastEntity();
//        roast.startRoast(); // todo needs to set the start time in startRoast()!!!!!!
//        roast2.startRoast();
//        roast2.endRoast();
//        long id = dao.insert(roast);
//        roast.setId((int)id);
//        long id2 = dao.insert(roast2);
//        roast.setId((int)id2);
//
//        RoastEntity retrieved = TestObserver.test(dao.getMostRecent())
//                .awaitValue().value();
//
//        assertEquals(roast2.getId(),
//            retrieved.getId());
//    }
}