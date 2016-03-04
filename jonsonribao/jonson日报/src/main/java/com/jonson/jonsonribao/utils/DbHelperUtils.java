package com.jonson.jonsonribao.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jonson on 2016/3/3.
 */
public class DbHelperUtils extends SQLiteOpenHelper{


    public DbHelperUtils(Context context) {
        super(context, "cache.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DbNamesAndOther.CREATE_MAIN_TABLE);
        db.execSQL(DbNamesAndOther.CREATE_LEFT_TABLE);
        db.execSQL(DbNamesAndOther.CREATE_WEB_TABLE);
        db.execSQL(DbNamesAndOther.CREATE_LONG_COMMENT_TABLE);
        db.execSQL(DbNamesAndOther.CREATE_SHORT_COMMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
