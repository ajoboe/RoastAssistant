package com.andrewkjacobson.android.roastassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.RoastEntity;

import java.util.List;

@Dao
public abstract class RoastDao extends BaseDao<RoastEntity> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(RoastEntity item);

    @Query("SELECT * FROM roast_entity") // add ORDER BY date DESC
    public abstract LiveData<List<RoastEntity>> getAll();

    @Query("SELECT * FROM roast_entity WHERE id=:roastId")
    public abstract LiveData<RoastEntity> getLiveData(int roastId);

    @Query("SELECT * FROM roast_entity WHERE id=(SELECT max(id) FROM roast_entity)")
    public abstract LiveData<RoastEntity> getMostRecent();
}
