package com.andrewkjacobson.android.roastassistant1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {RoastDetails.class}, version = 1, exportSchema = false)
public abstract class RoastRoomDatabase extends RoomDatabase {

    public abstract RoastDetailsDao roastDetailsDao();
    private static RoastRoomDatabase INSTANCE;

    static RoastRoomDatabase getDatabase(final Context context) {
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
