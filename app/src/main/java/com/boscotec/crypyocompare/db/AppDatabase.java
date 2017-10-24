package com.boscotec.crypyocompare.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Johnbosco on 17-Oct-17.
 */
@Database(entities = {ExchangeRate.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "crypyo")
                    .build();
        }
        return instance;
    }

 public abstract ExchangeRateDao exchangeRateDao();
}
