package com.andrewkjacobson.android.roastassistant1;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import java.util.List;

public class RoastRepository {
    private RoastDetailsDao mRoastDao;
    private LiveData<List<RoastDetails>> mAllRoasts;

    RoastRepository(Application application) {
        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(application);
        mRoastDao = db.roastDetailsDao();
        mAllRoasts = mRoastDao.getAllRoasts();
    }

    LiveData<List<RoastDetails>> getAllRoasts() {
        return mAllRoasts;
    }

    public void insert(RoastDetails details) {
        new insertAsyncTask(mRoastDao).execute(details);
    }

    private static class insertAsyncTask extends AsyncTask<RoastDetails, Void, Void> {

        private RoastDetailsDao mAsyncTaskDao;

        insertAsyncTask(RoastDetailsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final RoastDetails... params) {
            mAsyncTaskDao.insert((params[0]));
            return null;
        }
    }

}
