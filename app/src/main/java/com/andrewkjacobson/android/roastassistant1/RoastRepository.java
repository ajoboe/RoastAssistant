package com.andrewkjacobson.android.roastassistant1;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;

import com.andrewkjacobson.android.roastassistant1.db.dao.BaseDao;
import com.andrewkjacobson.android.roastassistant1.db.dao.CrackReadingDao;
import com.andrewkjacobson.android.roastassistant1.db.dao.DetailsDao;
import com.andrewkjacobson.android.roastassistant1.db.dao.ReadingDao;
import com.andrewkjacobson.android.roastassistant1.db.dao.RoastDao;
import com.andrewkjacobson.android.roastassistant1.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastComponent;
import com.andrewkjacobson.android.roastassistant1.model.Crack;
import com.andrewkjacobson.android.roastassistant1.model.Details;
import com.andrewkjacobson.android.roastassistant1.model.Reading;
import com.andrewkjacobson.android.roastassistant1.model.Roast;

import java.util.List;

// Repository modules handle data operations. They provide a clean API so that the rest of the app
// can retrieve this data easily. They know where to get the data from and what API calls to make
// when data is updated. You can consider repositories to be mediators between different data
// sources, such as persistent models, web services, and caches.

public class RoastRepository {
    private RoastDao mRoastDao;
    private DetailsDao mDetailsDao;
    private ReadingDao mReadingDao;
    private CrackReadingDao mCrackReadingDao;

    private LiveData<Roast> mRoast;
    private LiveData<Details> mDetails;
    private LiveData<List<Reading>> mReadings;
    private LiveData<List<Crack>> mCracks;

    public RoastRepository(Application application) {
        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(application);
        mRoastDao = db.roastDao();
        mDetailsDao = db.detailsDao();
        mReadingDao = db.readingDao();
        mCrackReadingDao = db.crackReadingDao();
    }

    public LiveData<Roast> getRoast(int roastId) {
        if(mRoast == null || mRoast.getValue().getId() != roastId) {
            mRoast = mRoastDao.get(roastId);
        }
        return mRoast;
    }

    public LiveData<Details> getDetails(int roastId) {
        if(mDetails == null || mDetails.getValue().getRoastId() != roastId) {
            mDetails = mDetailsDao.get(roastId);
        }
        return mDetails;
    }

    public LiveData<List<Reading>> getReadings(int roastId) {
        if(mReadings == null || mReadings.getValue().get(0).getRoastId() != roastId) {
            mReadings = mReadingDao.getAll();
        }
        return mReadings;
    }

    public LiveData<List<Crack>> getCracks(int roastId) {
        if(mCracks == null || mCracks.getValue().get(0).getRoastId() != roastId) {
            mCracks = mCrackReadingDao.getAll();
        }
        return mCracks;
    }


    public void insert(RoastComponent...item) {
        new insertAsyncTask(mRoastDao).execute(item);
    }

    private class insertAsyncTask extends AsyncTask<RoastComponent, Void, Void> {
        private BaseDao mAsyncTaskDao;

        insertAsyncTask(BaseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final RoastComponent... params) {
            mAsyncTaskDao.insert((params[0]));
            return null;
        }
    }
}
