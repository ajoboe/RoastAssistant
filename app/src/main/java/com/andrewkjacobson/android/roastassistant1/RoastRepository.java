package com.andrewkjacobson.android.roastassistant1;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.andrewkjacobson.android.roastassistant1.db.RoastDao;
import com.andrewkjacobson.android.roastassistant1.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;

import java.util.List;

// Repository modules handle data operations. They provide a clean API so that the rest of the app
// can retrieve this data easily. They know where to get the data from and what API calls to make
// when data is updated. You can consider repositories to be mediators between different data
// sources, such as persistent models, web services, and caches.

public class RoastRepository {
    private RoastDao mRoastDao;
    private LiveData<List<RoastEntity>> mAllRoasts;
    private int lastInsertedId = -1;

    public RoastRepository(Application application) {
        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(application);
        mRoastDao = db.roastDao();
        mAllRoasts = mRoastDao.getAllRoasts();
//        mCurrentRoast = mRoastDao.getRoast();
    }

    public int getLastInsertedId() {
//        insert(new RoastEntity(0,0));
//        mRoastDao.getAllRoasts();
//        return mAllRoasts.getValue().get(mAllRoasts.getValue().size()-1).getRoastId();
        return lastInsertedId;
    }

    public LiveData<List<RoastEntity>> getAllRoasts() {
        return mAllRoasts;
    }

    public void insert(RoastEntity roast) {
        new insertAsyncTask(mRoastDao).execute(roast);
    }

    /**
     * Get a roast by id
     *
     * @param roastId id of the desired roast
     * @return the desired roast
     */
    public LiveData<RoastEntity> loadRoast(int roastId) {
        return mRoastDao.getRoast(roastId);
    }


    private class insertAsyncTask extends AsyncTask<RoastEntity, Void, Void> {
        private RoastDao mAsyncTaskDao;

        insertAsyncTask(RoastDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final RoastEntity... params) {
            mAsyncTaskDao.insert((params[0]));
            return null;
        }
    }
}
