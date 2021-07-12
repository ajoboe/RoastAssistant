package com.andrewkjacobson.android.roastassistant.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

import com.andrewkjacobson.android.roastassistant.db.entity.ReadingEntity;

import java.util.List;

@Dao
public abstract class BaseDao<T> {
    @Insert
    public abstract long insert(T item);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(T... items);

    @Update
    public abstract void update(T item);

    @Delete
    public abstract void delete(T... items);
}
