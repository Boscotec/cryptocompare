package com.boscotec.crypyocompare.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Johnbosco on 17-Oct-17.
 */

@Entity(tableName = "exchange")
public class ExchangeRate {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "from")
    private String from;

    @ColumnInfo(name = "to")
    private String to;

    public ExchangeRate(long id, String from, String to){
        this.id = id;
        this.from = from;
        this.to = to;
    }

    public long getId(){return id;}
    public String getFrom(){return from;}
    public String getTo(){return to;}
}

