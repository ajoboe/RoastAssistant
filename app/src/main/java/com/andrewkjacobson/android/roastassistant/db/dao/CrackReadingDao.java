package com.andrewkjacobson.android.roastassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.andrewkjacobson.android.roastassistant.db.entity.CrackReadingEntity;
import java.util.List;

@Dao
public abstract class CrackReadingDao extends BaseDao<CrackReadingEntity> {
        @Query("SELECT * FROM crack_reading_entity WHERE roastId=:roastId ORDER BY seconds ASC")
        public abstract LiveData<List<CrackReadingEntity>> getAll(long roastId);

        @Query("SELECT * FROM crack_reading_entity WHERE roastId=:roastId AND seconds=:seconds")
        public abstract LiveData<CrackReadingEntity> get(long roastId, int seconds);

        @Query("DELETE FROM crack_reading_entity")
        public abstract void deleteAll();
}
