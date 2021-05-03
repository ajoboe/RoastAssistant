package com.andrewkjacobson.android.roastassistant.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.andrewkjacobson.android.roastassistant.db.dao.CrackReadingDao;
import com.andrewkjacobson.android.roastassistant.db.dao.DetailsDao;
import com.andrewkjacobson.android.roastassistant.db.dao.ReadingDao;
import com.andrewkjacobson.android.roastassistant.db.dao.RoastDao;
import com.andrewkjacobson.android.roastassistant.db.entity.CrackReadingEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.DetailsEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.RoastEntity;
import com.andrewkjacobson.android.roastassistant.db.entity.ReadingEntity;


@Database(entities = {RoastEntity.class, DetailsEntity.class, ReadingEntity.class, CrackReadingEntity.class},
        version = 8, exportSchema = false)
public abstract class RoastRoomDatabase extends RoomDatabase {

    public abstract RoastDao roastDao();
    public abstract DetailsDao detailsDao();
    public abstract ReadingDao readingDao();
    public abstract CrackReadingDao crackReadingDao();

    private static RoastRoomDatabase INSTANCE;

    public static RoastRoomDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (RoastRoomDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
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
