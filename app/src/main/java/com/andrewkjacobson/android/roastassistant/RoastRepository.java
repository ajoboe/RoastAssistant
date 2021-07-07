package com.andrewkjacobson.android.roastassistant;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.andrewkjacobson.android.roastassistant.db.RoastRoomDatabase;
import com.andrewkjacobson.android.roastassistant.db.dao.BaseDao;
import com.andrewkjacobson.android.roastassistant.db.dao.CrackReadingDao;
import com.andrewkjacobson.android.roastassistant.db.dao.DetailsDao;
import com.andrewkjacobson.android.roastassistant.db.dao.ReadingDao;
import com.andrewkjacobson.android.roastassistant.db.dao.RoastDao;
import com.andrewkjacobson.android.roastassistant.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.ReadingEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.RoastComponent;
import com.andrewkjacobson.android.roastassistant.db.entity.RoastEntity;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    public RoastRepository(Context context) {
        RoastRoomDatabase db = RoastRoomDatabase.getDatabase(context);
        mRoastDao = db.roastDao();
        mDetailsDao = db.detailsDao();
        mReadingDao = db.readingDao();
        mCrackReadingDao = db.crackReadingDao();
    }

    public LiveData<RoastEntity> getRoastLiveData(long roastId) {
        if(mRoastLiveData == null || mRoastLiveData.getValue().getRoastId() != roastId) {
            mRoastLiveData = mRoastDao.getLiveData(roastId);
        }
        return mRoastLiveData;
    }

    public LiveData<DetailsEntity> getDetailsLiveData(long roastId) {
        if(mDetailsLiveData == null || mDetailsLiveData.getValue().getRoastId() != roastId) {
            mDetailsLiveData = mDetailsDao.get(roastId);
        }
        return mDetailsLiveData;
    }

    public LiveData<List<ReadingEntity>> getReadingsLiveData(long roastId) {
        if(mReadingsLiveData == null || mReadingsLiveData.getValue().get(0).getRoastId() != roastId) {
            mReadingsLiveData = mReadingDao.getAll(roastId);
        }
        return mReadingsLiveData;
    }

    public LiveData<List<CrackReadingEntity>> getCracksLiveData(long roastId) {
        if(mCracksLiveData == null || mCracksLiveData.getValue().get(0).getRoastId() != roastId) {
            mCracksLiveData = mCrackReadingDao.getAll(roastId);
        }
        return mCracksLiveData;
    }

    public LiveData<RoastEntity> getMostRecentRoast() {
        return mRoastDao.getMostRecent();
    }

    // todo use a Future here. see: https://www.baeldung.com/java-util-concurrent
    public void insert(RoastComponent item) {
        if(item instanceof RoastEntity) {
            new insertAsyncTask(mRoastDao, item).execute((RoastEntity) item);
        } else if(item instanceof DetailsEntity) {
            new insertAsyncTask(mDetailsDao, item).execute((DetailsEntity) item);
        } else if(item instanceof CrackReadingEntity) {
            new insertAsyncTask(mCrackReadingDao, item).execute((CrackReadingEntity) item);
        } else if(item instanceof ReadingEntity) {
            new insertAsyncTask(mReadingDao, item).execute((ReadingEntity) item);
        }
        // todo BAD BAD BAD BAD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        while(item.getRoastId() < 1); // TODO VERY BAD!!! JUST MAKING THE TEST PASS
//        // instead, wait for a callback to return
//        long id =
        // todo return a LiveData?...can't need the id to grab it
//        return item.getRoastId(); // todo get the id dangit
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        Callable<Long> insertCallable = () -> mRoastDao.insert((RoastEntity)item);
//        return executorService.submit(insertCallable);
    }

    public void insert(RoastComponent item, insertAsyncTask.InsertTaskDelegate task) {
        insert(item);
    }

    public void update(RoastComponent item) {
        if(item instanceof RoastEntity) {
            new updateAsyncTask(mRoastDao).execute((RoastEntity) item);
        } else if(item instanceof DetailsEntity) {
            new updateAsyncTask(mDetailsDao).execute((DetailsEntity) item);
        } else if(item instanceof CrackReadingEntity) {
            new updateAsyncTask(mCrackReadingDao).execute((CrackReadingEntity) item);
        } else if(item instanceof ReadingEntity) {
            new updateAsyncTask(mReadingDao).execute((ReadingEntity) item);
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

    public void deleteAllCracks() {
        new deleteAllAsyncTask(mCrackReadingDao).execute();
    }

    private static class insertAsyncTask extends AsyncTask<RoastComponent, Void, Long> {
        //declare a delegate with type of protocol declared in this task
        private InsertTaskDelegate delegate;

        //here is the task protocol to can delegate on other object
        public interface InsertTaskDelegate {
            void onTaskFinished(long id);
        }

        private BaseDao mAsyncTaskDao;
        private RoastComponent roastComponent;

        insertAsyncTask(BaseDao dao, RoastComponent roastComponent) {
            mAsyncTaskDao = dao;
            this.roastComponent = roastComponent;
        }

        @Override
        protected Long doInBackground(final RoastComponent... params) {
//            Callable<Long> insertCallable = () -> mAsyncTaskDao.insert(params[0]);
//            return params[0].getRoastId();
            return mAsyncTaskDao.insert(params[0]);
//            return mAsyncTaskDao.insert(params[0]);
        }

//        /**
//         * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
//         * {@link #doInBackground(RoastComponent[])} has finished.</p>
//         *
//         * <p>The default implementation simply invokes {@link #onCancelled()} and
//         * ignores the result. If you write your own implementation, do not call
//         * <code>super.onCancelled(result)</code>.</p>
//         *
//         * @param result The result, if any, computed in
//         *              {@link #doInBackground(RoastComponent[])}, can be null
//         * @see #cancel(boolean)
//         * @see #isCancelled()
//         */
//        @Override
//        protected void onCancelled(Long result) {
//            if(result == null) {
//                Log.w(getClass().toString(), "Insert canceled. No new rowId.");
//            } else {
//                Log.w(getClass().toString(), "Insert canceled. rowId: " + result);
//            }
//        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.
         * To better support testing frameworks, it is recommended that this be
         * written to tolerate direct execution as part of the execute() call.
         * The default version does nothing.</p>
         *
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param id The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         */
        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
//            Log.w(getClass().toString(),"New rowId: " + result);
//            roastComponent.setRoastId(result.longValue());
            if(delegate != null) {
                delegate.onTaskFinished(id);
            }
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
        private BaseDao mAsyncTaskDao;

        deleteAllAsyncTask(BaseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mAsyncTaskDao instanceof ReadingDao) {
                ((ReadingDao) mAsyncTaskDao).deleteAll();
            } else if(mAsyncTaskDao instanceof CrackReadingDao) {
                ((CrackReadingDao) mAsyncTaskDao).deleteAll();
            } else {
                throw new UnsupportedOperationException();
            }

            return null;
        }
    }
}
