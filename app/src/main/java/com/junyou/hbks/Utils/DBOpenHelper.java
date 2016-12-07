package com.junyou.hbks.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// http://blog.csdn.net/howlaa/article/details/46707159
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "test.db";
    private static final int VERSION = 11;

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);//it's location is data/data/pakage/database
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS exam_type (id integer primary key autoincrement, type_name varchar(100), type_id INTEGER");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
