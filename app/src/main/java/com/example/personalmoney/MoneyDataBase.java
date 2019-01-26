package com.example.personalmoney;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MoneyDataBase extends SQLiteOpenHelper
{
    public MoneyDataBase(Context context) {
        super(context, "money.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mymoney (id integer primary key autoincrement,"+"date TEXT,"+"amount integer,"+"other TEXT); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
