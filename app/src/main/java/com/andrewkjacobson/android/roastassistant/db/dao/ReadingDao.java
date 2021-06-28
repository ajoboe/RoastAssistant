package com.andrewkjacobson.android.roastassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.andrewkjacobson.android.roastassistant.db.entity.ReadingEntity;

import java.util.List;

@Dao
public abstract class ReadingDao extends BaseDao<ReadingEntity> {
    @Query("SELECT * FROM reading_entity WHERE roastId=:roastId ORDER BY seconds ASC")
    public abstract LiveData<List<ReadingEntity>> getAll(long roastId);

    @Query("SELECT * FROM reading_entity WHERE roastId=:roastId AND seconds=:seconds")
    public abstract LiveData<ReadingEntity> get(long roastId, int seconds);

    @Query("DELETE FROM reading_entity WHERE roastId=:roastId AND seconds=:seconds")
    public abstract void delete(long roastId, int seconds);

    @Query("DELETE FROM reading_entity")
    public abstract void deleteAll();
}
