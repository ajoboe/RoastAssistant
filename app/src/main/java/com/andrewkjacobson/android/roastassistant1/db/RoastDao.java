package com.andrewkjacobson.android.roastassistant1.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RoastEntity roast);

    @Query("DELETE FROM RoastEntity")
    void deleteAll();

    @Query("SELECT * FROM RoastEntity") // add ORDER BY date DESC
    LiveData<List<RoastEntity>> getAllRoasts();

    @Query("SELECT * FROM RoastEntity WHERE roastId=:roastId")
    LiveData<RoastEntity> getRoast(int roastId);
}
