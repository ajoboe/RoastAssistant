package com.andrewkjacobson.android.roastassistant1.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.andrewkjacobson.android.roastassistant1.db.entity.CrackReadingEntity;
import java.util.List;

@Dao
public abstract class CrackReadingDao extends BaseDao<CrackReadingEntity> {
        @Query("SELECT * FROM crack_reading_entity ORDER BY seconds ASC")
        public abstract LiveData<List<CrackReadingEntity>> getAll();

        @Query("SELECT * FROM crack_reading_entity WHERE roastId=:roastId AND seconds=:seconds")
        public abstract LiveData<CrackReadingEntity> get(int roastId, int seconds);
}
