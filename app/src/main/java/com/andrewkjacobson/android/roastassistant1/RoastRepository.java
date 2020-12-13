package com.andrewkjacobson.android.roastassistant1;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import java.util.List;

// Repository modules handle data operations. They provide a clean API so that the rest of the app
// can retrieve this data easily. They know where to get the data from and what API calls to make
// when data is updated. You can consider repositories to be mediators between different data
// sources, such as persistent models, web services, and caches.

public class RoastRepository {
    private RoastDao mRoastDao;
    private LiveData<List<Roast>> mAllRoasts;

    RoastRepository(Application application) {
        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(application);
        mRoastDao = db.roastDetailsDao();
        mAllRoasts = mRoastDao.getAllRoasts();
    }

    LiveData<List<Roast>> getAllRoasts() {
        return mAllRoasts;
    }

    public void insert(Roast roast) {
        new insertAsyncTask(mRoastDao).execute(roast);
    }

    private static class insertAsyncTask extends AsyncTask<Roast, Void, Void> {

        private RoastDao mAsyncTaskDao;

        insertAsyncTask(RoastDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Roast... params) {
            mAsyncTaskDao.insert((params[0]));
            return null;
        }
    }

}
