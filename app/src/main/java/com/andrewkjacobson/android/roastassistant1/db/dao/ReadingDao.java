package com.andrewkjacobson.android.roastassistant1.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Query;
import com.andrewkjacobson.android.roastassistant1.model.Reading;

import java.util.List;

public abstract class ReadingDao extends BaseDao<Reading> {
    @Query("SELECT * FROM reading_entity ORDER BY seconds ASC")
    public abstract LiveData<List<Reading>> getAll();

    @Query("SELECT * FROM reading_entity WHERE roastId=:roastId AND seconds=:seconds")
    public abstract LiveData<Reading> get(int roastId, int seconds);
}
