package com.andrewkjacobson.android.roastassistant1.db.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

public abstract class BaseDao<T> {
    @Insert
    public abstract long insert(T...items);

    @Update
    public abstract void update(T...items);

    @Delete
    public abstract void delete(T...items);
}
