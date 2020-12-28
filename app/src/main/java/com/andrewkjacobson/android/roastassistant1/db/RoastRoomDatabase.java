package com.andrewkjacobson.android.roastassistant1.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.andrewkjacobson.android.roastassistant1.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastDetailsEntity;
import com.andrewkjacobson.android.roastassistant1.db.entity.RoastReadingEntity;


@Database(entities = {RoastEntity.class, RoastDetailsEntity.class, RoastReadingEntity.class},
        version = 4, exportSchema = false)
public abstract class RoastRoomDatabase extends RoomDatabase {

    public abstract RoastDao roastDao();
    private static RoastRoomDatabase INSTANCE;

    public static RoastRoomDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (RoastRoomDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoastRoomDatabase.class, "roast_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object. Eventually need to define a migration strategy.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
