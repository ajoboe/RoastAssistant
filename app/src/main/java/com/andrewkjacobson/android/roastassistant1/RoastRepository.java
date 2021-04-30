package com.andrewkjacobson.android.roastassistant1;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.andrewkjacobson.android.roastassistant1.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant1.db.dao.BaseDao;
import com.andrewkjacobson.android.roastassistant1.db.dao.CrackReadingDao;
import com.andrewkjacobson.android.roastassistant1.db.dao.DetailsDao;
import com.andrewkjacobson.android.roastassistant1.db.dao.ReadingDao;
import com.andrewkjacobson.android.roastassistant1.db.dao.RoastDao;
import com.andrewkjacobson.android.roastassistant1.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.DetailsEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastComponent;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;

import java.util.List;

// Repository modules handle data operations. They provide a clean API so that the rest of the app
// can retrieve this data easily. They know where to get the data from and what API calls to make
// when data is updated. You can consider repositories to be mediators between different data
// sources, such as persistent models, web services, and caches.

public class RoastRepository {
    private final RoastDao mRoastDao;
    private final DetailsDao mDetailsDao;
    private final ReadingDao mReadingDao;
    private final CrackReadingDao mCrackReadingDao;

    private LiveData<RoastEntity> mRoastLiveData;
    private LiveData<DetailsEntity> mDetailsLiveData;
    private LiveData<List<ReadingEntity>> mReadingsLiveData;
    private LiveData<List<CrackReadingEntity>> mCracksLiveData;

    public RoastRepository(Application application) {
        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(application);
        mRoastDao = db.roastDao();
        mDetailsDao = db.detailsDao();
        mReadingDao = db.readingDao();
        mCrackReadingDao = db.crackReadingDao();
    }

    public LiveData<RoastEntity> getRoastLiveData(int roastId) {
        if(mRoastLiveData == null || mRoastLiveData.getValue().getId() != roastId) {
            mRoastLiveData = mRoastDao.getLiveData(roastId);
        }
        return mRoastLiveData;
    }

    public LiveData<DetailsEntity> getDetailsLiveData(int roastId) {
        if(mDetailsLiveData == null || mDetailsLiveData.getValue().getRoastId() != roastId) {
            mDetailsLiveData = mDetailsDao.get(roastId);
        }
        return mDetailsLiveData;
    }

    public LiveData<List<ReadingEntity>> getReadingsLiveData(int roastId) {
        if(mReadingsLiveData == null || mReadingsLiveData.getValue().get(0).getRoastId() != roastId) {
            mReadingsLiveData = mReadingDao.getAll(roastId);
        }
        return mReadingsLiveData;
    }

    public LiveData<List<CrackReadingEntity>> getCracksLiveData(int roastId) {
        if(mCracksLiveData == null || mCracksLiveData.getValue().get(0).getRoastId() != roastId) {
            mCracksLiveData = mCrackReadingDao.getAll(roastId);
        }
        return mCracksLiveData;
    }

    // todo use a Future here. see: https://www.baeldung.com/java-util-concurrent
    public void insert(RoastComponent item) {
        if(item instanceof RoastEntity) {
            new insertAsyncTask(mRoastDao).execute((RoastEntity) item);
        } else if(item instanceof DetailsEntity) {
            new insertAsyncTask(mDetailsDao).execute((DetailsEntity) item);
        } else if(item instanceof CrackReadingEntity) {
            new insertAsyncTask(mCrackReadingDao).execute((CrackReadingEntity) item);
        } else if(item instanceof ReadingEntity) {
            new insertAsyncTask(mReadingDao).execute((ReadingEntity) item);
        }
    }

    public void update(RoastComponent item) {
        if(item instanceof RoastEntity) {
            new updateAsyncTask(mRoastDao).execute(item);
        } else if(item instanceof DetailsEntity) {
            new updateAsyncTask(mDetailsDao).execute(item);
        } else if(item instanceof CrackReadingEntity) {
            new updateAsyncTask(mCrackReadingDao).execute(item);
        } else if(item instanceof ReadingEntity) {
            new updateAsyncTask(mReadingDao).execute(item);
        }
    }

    public void upsert(RoastComponent... item) {
        if(item instanceof RoastEntity[]) {
            new upsertAsyncTask(mRoastDao).execute(item);
        } else if(item instanceof DetailsEntity[]) {
            new upsertAsyncTask(mDetailsDao).execute(item);
        } else if(item instanceof CrackReadingEntity[]) {
            new upsertAsyncTask(mCrackReadingDao).execute(item);
        } else if(item instanceof ReadingEntity[]) {
            new upsertAsyncTask(mReadingDao).execute(item);
        }
    }

    public void delete(RoastComponent...item) {
        if(item instanceof RoastEntity[]) {
            new deleteAsyncTask(mRoastDao).execute(item);
        } else if(item instanceof DetailsEntity[]) {
            new deleteAsyncTask(mDetailsDao).execute(item);
        } else if(item instanceof CrackReadingEntity[]) {
            new deleteAsyncTask(mCrackReadingDao).execute(item);
        } else if(item instanceof ReadingEntity[]) {
            new deleteAsyncTask(mReadingDao).execute(item);
        }
    }

    public void deleteAllReadings() {
        new deleteAllAsyncTask(mReadingDao).execute();
    }

    private class insertAsyncTask extends AsyncTask<RoastComponent, Void, Long> {
        private BaseDao mAsyncTaskDao;

        insertAsyncTask(BaseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Long doInBackground(final RoastComponent... params) {
            return mAsyncTaskDao.insert(params[0]);
        }

        /**
         * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
         * {@link #doInBackground(RoastComponent[])} has finished.</p>
         *
         * <p>The default implementation simply invokes {@link #onCancelled()} and
         * ignores the result. If you write your own implementation, do not call
         * <code>super.onCancelled(result)</code>.</p>
         *
         * @param result The result, if any, computed in
         *              {@link #doInBackground(RoastComponent[])}, can be null
         * @see #cancel(boolean)
         * @see #isCancelled()
         */
        @Override
        protected void onCancelled(Long result) {
            if(result == null) {
                Log.w(getClass().toString(), "Insert canceled. No new rowId.");
            } else {
                Log.w(getClass().toString(), "Insert canceled. rowId: " + result);
            }
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.
         * To better support testing frameworks, it is recommended that this be
         * written to tolerate direct execution as part of the execute() call.
         * The default version does nothing.</p>
         *
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param result The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         */
        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            Log.w(getClass().toString(),"New rowId: " + result);
        }

    }

    private class updateAsyncTask extends AsyncTask<RoastComponent, Void, Void> {
        private BaseDao mAsyncTaskDao;

        updateAsyncTask(BaseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final RoastComponent... params) {
            mAsyncTaskDao.update((params[0]));
            return null;
        }
    }

    private class upsertAsyncTask extends AsyncTask<RoastComponent, Void, Void> {
        private BaseDao mAsyncTaskDao;

        upsertAsyncTask(BaseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final RoastComponent... params) {
            mAsyncTaskDao.upsert((params[0]));
            return null;
        }
    }

    private class deleteAsyncTask extends AsyncTask<RoastComponent, Void, Void> {
        private BaseDao mAsyncTaskDao;

        deleteAsyncTask(BaseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final RoastComponent... params) {
            mAsyncTaskDao.delete((params[0]));
            return null;
        }
    }

    private class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private ReadingDao mAsyncTaskDao;

        deleteAllAsyncTask(ReadingDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }
}
