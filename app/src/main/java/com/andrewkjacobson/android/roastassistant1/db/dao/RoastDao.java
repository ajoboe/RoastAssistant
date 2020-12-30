package com.andrewkjacobson.android.roastassistant1.db.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.andrewkjacobson.android.roastassistant1.db.entity.RoastComponent;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.model.Details;
import com.andrewkjacobson.android.roastassistant1.model.Roast;

import java.util.List;

@Dao
public abstract class RoastDao extends BaseDao<Roast> {
    @Query("SELECT * FROM roast_entity") // add ORDER BY date DESC
    public abstract LiveData<List<Roast>> getAll();

    @Query("SELECT * FROM roast_entity WHERE id=:roastId")
    public abstract LiveData<Roast> get(int roastId);
}
