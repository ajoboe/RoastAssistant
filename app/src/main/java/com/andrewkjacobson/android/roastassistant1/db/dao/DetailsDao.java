package com.andrewkjacobson.android.roastassistant1.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Query;
import com.andrewkjacobson.android.roastassistant1.model.Details;

public abstract class DetailsDao extends BaseDao<Details> {
    @Query("SELECT * FROM details_entity WHERE roastId=:roastId")
    public abstract LiveData<Details> get(int roastId);
}
