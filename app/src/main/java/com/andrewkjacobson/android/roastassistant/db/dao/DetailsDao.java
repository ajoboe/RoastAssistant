package com.andrewkjacobson.android.roastassistant.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;

@Dao
public abstract class DetailsDao extends BaseDao<DetailsEntity> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(DetailsEntity item);

    @Query("SELECT * FROM details_entity WHERE roastId=:roastId")
    public abstract LiveData<DetailsEntity> get(long roastId);
}
