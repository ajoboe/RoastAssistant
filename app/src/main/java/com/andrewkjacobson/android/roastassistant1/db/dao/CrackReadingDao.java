package com.andrewkjacobson.android.roastassistant1.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Query;
import com.andrewkjacobson.android.roastassistant1.model.Crack;

import java.util.List;

public abstract class CrackReadingDao extends BaseDao<Crack> {
        @Query("SELECT * FROM crack_reading_entity ORDER BY seconds ASC")
        public abstract LiveData<List<Crack>> getAll();

        @Query("SELECT * FROM crack_reading_entity WHERE roastId=:roastId AND seconds=:seconds")
        public abstract LiveData<Crack> get(int roastId, int seconds);
}
