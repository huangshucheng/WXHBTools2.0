package com.junyou.hbks.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "hbks.db";
    private static final int VERSION = 1;

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBOpenHelper(Context context) {
        super(context, getMyDatabaseName(context), null, VERSION);//it's location is data/data/pakage/database
//        super(context,DBNAME, null, VERSION);
//        Log.i("TAG","path:  " + getMyDatabaseName(context));
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("TAG","create a database");
//        String sql = "create table user(id int,name varchar(20),coinNum int,pointNum int,timeNum int)";
        //String sql = "create table user(id int,coinNum int,pointNum int,timeNum int)";
        String sql = "create table user(id int,coinNum int,pointNum int,timeNum int,isGiveThreeDay int)";
        //执行创建数据库操作
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("TAG","update a Database");
    }

    private static String getMyDatabaseName(Context context){
        String databasename = DBNAME;
        boolean isSdcardEnable =false;
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){//SDCard是否插入
            isSdcardEnable = true;
        }
        String dbPath = null;
        if(isSdcardEnable){
            dbPath =Environment.getExternalStorageDirectory().getPath() +"/database/";
            Log.i("TAG","hava sd Card:" + dbPath);
        }else{//未插入SDCard，建在内存中
            dbPath =context.getFilesDir().getPath() + "/database/";
            Log.i("TAG","no sd Card:" + dbPath);
        }
        File dbp = new File(dbPath);
        if(!dbp.exists()){
            dbp.mkdirs();
        }
        databasename = dbPath +DBNAME;
        return databasename;
    }
}
