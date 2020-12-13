package com.andrewkjacobson.android.roastassistant1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RoastDao {

    @Insert
    void insert(RoastDetails details);

    @Query("DELETE FROM roast_details_table")
    void deleteAll();

    @Query("SELECT * FROM roast_details_table") // add ORDER BY date DESC
    LiveData<List<Roast>> getAllRoasts();
}
