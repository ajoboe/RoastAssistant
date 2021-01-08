package com.andrewkjacobson.android.roastassistant1.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;

import java.util.List;

@Dao
public abstract class RoastDao extends BaseDao<RoastEntity> {
    @Query("SELECT * FROM roast_entity") // add ORDER BY date DESC
    public abstract LiveData<List<RoastEntity>> getAll();

    @Query("SELECT * FROM roast_entity WHERE id=:roastId")
    public abstract LiveData<RoastEntity> get(int roastId);
}
