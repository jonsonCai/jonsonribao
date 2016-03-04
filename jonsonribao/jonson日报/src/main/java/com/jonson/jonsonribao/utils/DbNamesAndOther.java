package com.jonson.jonsonribao.utils;

/**
 * Created by jonson on 2016/3/3.
 */
public class DbNamesAndOther {
    public static String LEFT_DB = "left_db";
    public static String MAIN_DB = "main_db";
    public static String WEB_DB = "web_db";
    public static String LONG_COMMENT_DB = "long_comment_db";
    public static String SHORT_COMMENT_DB = "short_comment_db";

    public static String CREATE_MAIN_TABLE = "create table main_db (_id Integer primary key autoincrement , urlMD5 varchar(32), date varchar(16) , json text)";
    public static String CREATE_LEFT_TABLE = "create table left_db (_id Integer primary key autoincrement , urlMD5 varchar(32), date varchar(16) , json text)";
    public static String CREATE_WEB_TABLE = "create table web_db (_id Integer primary key autoincrement , urlMD5 varchar(32), date varchar(16) , json text)";
    public static String CREATE_LONG_COMMENT_TABLE = "create table long_comment_db (_id Integer primary key autoincrement , urlMD5 varchar(32), date varchar(16) , json text)";
    public static String CREATE_SHORT_COMMENT_TABLE = "create table short_comment_db (_id Integer primary key autoincrement , urlMD5 varchar(32), date varchar(16) , json text)";
}
