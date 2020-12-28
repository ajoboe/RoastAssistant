package com.andrewkjacobson.android.roastassistant1;

import androidx.room.TypeConverter;

import com.andrewkjacobson.android.roastassistant1.db.entity.RoastReadingEntity;

import java.util.ArrayList;

public class TypeConverters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long fromListOfReadings(ArrayList<RoastReadingEntity> readings) {
        return date == null ? null : date.getTime();
    }

}
