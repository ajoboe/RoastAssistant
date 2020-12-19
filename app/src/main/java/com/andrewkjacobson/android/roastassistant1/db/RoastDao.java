package com.andrewkjacobson.android.roastassistant1.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;

import java.util.List;

@Dao
public interface RoastDao {

    /**
     *
     * @param roast
     * @return the row id
     */
    @Insert
    long insert(RoastEntity roast);

    @Query("DELETE FROM RoastEntity")
    void deleteAll();

    @Query("SELECT * FROM RoastEntity") // add ORDER BY date DESC
    LiveData<List<RoastEntity>> getAllRoasts();

    @Query("SELECT * FROM RoastEntity WHERE id = :roastId")  // todo Need a where clause with the id
    LiveData<RoastEntity> getRoast(int roastId);
}
