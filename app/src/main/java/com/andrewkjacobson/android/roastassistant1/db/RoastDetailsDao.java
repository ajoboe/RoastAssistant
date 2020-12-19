package com.andrewkjacobson.android.roastassistant1.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.andrewkjacobson.android.roastassistant1.db.entity.RoastDetailsEntity;

import java.util.List;

@Dao
public interface RoastDetailsDao {

    /**
     *
     * @param details
     * @return the row id
     */
    @Insert
    long insert(RoastDetailsEntity details);

    @Query("DELETE FROM RoastDetailsEntity")
    void deleteAll();

    @Query("SELECT * FROM RoastDetailsEntity") // add ORDER BY date DESC
    LiveData<List<RoastDetailsEntity>> getAllRoasts();

    @Query("SELECT * FROM RoastDetailsEntity WHERE id = :roastId")  // todo Need a where clause with the id
    LiveData<RoastDetailsEntity> getRoast(int roastId);
}
