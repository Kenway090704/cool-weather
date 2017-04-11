package com.aofei.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kenway on 17/4/11 16:26
 * Email : xiaokai090704@126.com
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "coolweatehr.db";

    /***
     * 创建省
     */
    private static final String CREATE_PROVINCE = "create table Province(id integer primary key autoincrement," +
            "province_name text,province_code text )";

    /**
     * 创建市
     */
    private static final String CREATE_CITY = "create table City(id integer primary key autoincrement,city_name text" +
            ",city_code text,province_id integer)";

    /**
     * 创建县
     */
    private static final String CREATE_COUNTY = "create table County(id integer primary key autoincrement,county_name text," +
            "county_code text,city_id integer)";

    public CoolWeatherOpenHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);//创建Provice表
        db.execSQL(CREATE_CITY);//创建City表
        db.execSQL(CREATE_COUNTY);//创建County表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
