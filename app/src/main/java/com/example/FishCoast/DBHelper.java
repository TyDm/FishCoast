package com.example.FishCoast;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, "myDB", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table clientstable (" + "id integer primary key autoincrement," +
                "company tinytext," + "city tinytext," + "street tinytext," + "phone tinytext," + "price tinyinteger" +");");
        db.execSQL("create table pricetable (" + "id integer primary key autoincrement," +
                "type tinyinteger," + "name tinytext," + "category tinytext," + "cost float," +
                "unit tinyinteger" + ");");
        db.execSQL("create table orderstable (" + "id integer primary key autoincrement," +
                "orderid integer," + "clientid integer," + "name tinytext," + "cost float," +
                "unit tinyinteger," + "quantity float," + "orderdate datetime," + "deliverydate datetime" + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if ((oldVersion == 2) & (newVersion == 3)){

        }
    }
}
