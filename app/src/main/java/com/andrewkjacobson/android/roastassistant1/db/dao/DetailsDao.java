package com.andrewkjacobson.android.roastassistant1.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.andrewkjacobson.android.roastassistant1.db.entity.DetailsEntity;

@Dao
public abstract class DetailsDao extends BaseDao<DetailsEntity> {
    @Query("SELECT * FROM details_entity WHERE roastId=:roastId")
    public abstract LiveData<DetailsEntity> get(int roastId);
}
