package com.boscotec.crypyocompare.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Johnbosco on 17-Oct-17.
 */
@Dao
public interface ExchangeRateDao {
    @Query("SELECT * FROM exchange")
    List<ExchangeRate> getAll();

    @Query("SELECT * FROM exchange WHERE id IN (:userIds)")
    List<ExchangeRate> loadAllByIds(int[] userIds);

    //@Query("SELECT * FROM exchange WHERE from LIKE :from AND " + "to LIKE :to LIMIT 1")
    //ExchangeRate findByName(String from, String to);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ExchangeRate... rates);

    @Delete
    void delete(ExchangeRate rates);

    @Update
    void update(ExchangeRate rate);
}
