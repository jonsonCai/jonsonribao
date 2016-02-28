package com.jonson.jonsonribao.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jonson on 2016/2/18.
 * SharedPreferences工具
 */
public class SpUtils {

    private static final String SP_NAME = "config";

    /**
     * 设置String到SP
     * @param context
     * @param key
     * @param value
     */
    public static void setString2Sp(Context context , String key , String value){
        getSp(context).edit().putString(key, value);
    }

    /**
     * 在SP中获取String
     * @param content
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getStringBySp(Context content , String key , String defaultValue){
        return getSp(content).getString(key , defaultValue);
    }

    /**
     * 设置int到Sp中
     * @param context
     * @param key
     * @param value
     */
    public static void setInt2Sp(Context context , String key , int value){
        getSp(context).edit().putInt(key , value);
    }

    /**
     * 在Sp中获取int
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getIntBySp(Context context , String key , int defaultValue){
        return getSp(context).getInt(key , defaultValue);
    }

    /**
     * 设置boolean到Sp中
     * @param context
     * @param key
     * @param value
     */
    public static void setBoolean2Sp(Context context , String key , boolean value){
        getSp(context).edit().putBoolean(key , value);
    }

    /**
     * 在Sp中获取boolean
     * @param context
     * @param key
     * @param defaultValue
     */
    public static boolean getBooleanBySp(Context context , String key , boolean defaultValue){
        return getSp(context).getBoolean(key , defaultValue);
    }



    /**
     * 拿到SharedPreferences
     * @param context
     * @return
     */
    private static SharedPreferences getSp(Context context){
        return context.getSharedPreferences(SP_NAME, context.MODE_PRIVATE);
    }
}
