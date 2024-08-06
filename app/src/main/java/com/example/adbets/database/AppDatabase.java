package com.example.adbets.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.adbets.model.VideoUrl;

@Database(entities = {
        VideoUrl.class,}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "adBeets_db.db";

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDataBase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract VideoUrlDao videoUrlDao();

}
