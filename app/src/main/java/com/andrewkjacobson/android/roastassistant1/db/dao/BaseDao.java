package com.andrewkjacobson.android.roastassistant1.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(T item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(T item);

    @Transaction
    public void upsert(T item) {
        long id = insert(item);
        if(id == -1) {
            update(item);
        }
    }

    @Delete
    public abstract void delete(T...items);
}
