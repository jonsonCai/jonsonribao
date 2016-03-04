package com.jonson.jonsonribao.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by jonson on 2016/3/3.
 */
public class MyDbUtils {

    private final SQLiteDatabase db;

    public MyDbUtils(Context context) {

        DbHelperUtils dbHelperUtils = new DbHelperUtils(context);
        db = dbHelperUtils.getWritableDatabase();
    }

    /**
     * 将json添加到数据库
     * @param urlMD5
     * @param jsonText
     * @param dbTable
     * @return
     */
    public boolean addDb(String urlMD5 ,String date , String jsonText , String dbTable){

        Cursor cursor = db.query(dbTable, new String[]{"json", "date"}, "urlMD5=?", new String[]{urlMD5}, null, null, null);
        if(cursor.moveToNext()){
            String dbDate = cursor.getString(cursor.getColumnIndex("date"));

            String currentDate = DateUtils.date2String(new Date());
            if(currentDate.equals(dbDate)){
                return true;
            }else{
                //修改数据
                ContentValues contentValues = new ContentValues();
                contentValues.put("date" , date);
                contentValues.put("json" , jsonText);
                updateDb(dbTable , contentValues , "urlMD5=?" , new String[]{urlMD5});
                return true;
            }
        }
        Log.e("my", "添加到数据库");
        ContentValues contentValues = new ContentValues();
        contentValues.put("urlMD5" , urlMD5);
        contentValues.put("date" , date);
        contentValues.put("json", jsonText);
        long insert = db.insert(dbTable, null, contentValues);
        if(insert == -1){
            return false;
        }else{
            return true;
        }

    }

    /**
     * 通过url的md5查找对应的json
     * @param urlMD5
     * @param dbTable
     * @return
     */
    public HashMap<String , String> findDb(String urlMD5 , String dbTable){
        Cursor cursor = db.query(dbTable, new String[]{"json", "date"}, "urlMD5=?", new String[]{urlMD5}, null, null, null);
        if(cursor.moveToNext()){
            String json = cursor.getString(cursor.getColumnIndex("json"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            HashMap<String , String> result = new HashMap<String , String>();
            result.put("dbJson" , json);
            result.put("dbDate" , date);
            Log.e("my", "从数据库获取数据");
            return result;
        }
        return null;
    }


    /**
     * 清除缓存
     * @param context
     */
    public void removeDb(Context context){
        context.deleteDatabase("cache.db");
    }

    public void updateDb(String table , ContentValues contentValues , String where , String[] whereArgs){
        db.update(table, contentValues, where, whereArgs);
    }
}
