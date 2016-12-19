package com.junyou.hbks.Utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalSaveUtil {
    private static final String COIN_NUM = "coin_num";
    private static final String POINT_NUM = "point_num";
    private static final String USER_ACCOUNT = "user_account";

    private static Activity mActivity;
    private static SQLiteDatabase mDataBase;
    public static void init(Activity act){
        mActivity = act;
        //db是否存在，存在则不创建，不存在则创建
        DBOpenHelper helper = new DBOpenHelper(mActivity);
        mDataBase = helper.getReadableDatabase();

        if (checkDBIsEmpty(mDataBase)){
            int idNum = (int) ((Math.random() * 9 + 1) * 100000);//随机生成6位数账号
            Log.i("TAG","db is empty" );
            ContentValues values = new ContentValues();
            //向ContentValues中存放数据
            values.put("id", idNum);
            values.put("coinNum",10);
            values.put("pointNum",30);
            values.put("timeNum",4320);     //三天
//            values.put("timeNum",2880);     //两天
//            values.put("timeNum",1);
            mDataBase.insert("user", null, values);
        }else{
            Log.i("TAG","db is not empty" );
        }
        //查询
        Cursor cursor = mDataBase.query("user", new String[]{"id","coinNum","pointNum","timeNum"}, null, null, null, null, null, null);
        //利用游标遍历所有数据对象
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int coinNum = cursor.getInt(cursor.getColumnIndex("coinNum"));
            int pointNum = cursor.getInt(cursor.getColumnIndex("pointNum"));
            int timeNum = cursor.getInt(cursor.getColumnIndex("timeNum"));
            //日志打印输出
            Log.i("TAG","db data: id:" + id + " ,coin:" + coinNum +" ,point" +  pointNum + " ,time" + timeNum);
        }
        cursor.close();
    }

    public static int getCoinNum(){
        int coinNum = 0;
        if (null != mDataBase){
            Cursor cursor = mDataBase.query("user", new String[]{"id","coinNum","pointNum","timeNum"}, null, null, null, null, null, null);
            while(cursor.moveToNext()){
                coinNum = cursor.getInt(cursor.getColumnIndex("coinNum"));
                Log.i("TAG","coin from db:" + coinNum);
                cursor.close();
                return coinNum;
            }
        }
        return  0;
    }

    public static void setCoinNum(int coinNum){
        if (coinNum <0){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("coinNum", coinNum);
        if (null != mDataBase){
            mDataBase.update("user", values, null, null);
        }
    }

    public static int getPointNum(){
        int pointNum = 0;
        if (null != mDataBase){
            Cursor cursor = mDataBase.query("user", new String[]{"id","coinNum","pointNum","timeNum"}, null, null, null, null, null, null);
            while(cursor.moveToNext()){
                pointNum = cursor.getInt(cursor.getColumnIndex("pointNum"));
                Log.i("TAG","point from db: " + pointNum);
                cursor.close();
                return pointNum;
            }
        }
        return  0;
    }

    public static void setPointNum(int pointNum){
        if (pointNum<0){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("pointNum", pointNum);
        if (null != mDataBase){
            mDataBase.update("user", values, null, null);
        }
    }
//用户账号
    public static void setAccount(int account){
        if (account <0){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("id", account);
        if (null != mDataBase){
            mDataBase.update("user", values, null, null);
        }
    }

    public static int getAccount(){
        int pointNum = 0;
        if (null != mDataBase){
            Cursor cursor = mDataBase.query("user", new String[]{"id","coinNum","pointNum","timeNum"}, null, null, null, null, null, null);
            while(cursor.moveToNext()){
                pointNum = cursor.getInt(cursor.getColumnIndex("id"));
                Log.i("TAG","id from db: " + pointNum);
                cursor.close();
                return pointNum;
            }
        }
        return 0 ;
    }

    public static int getLeftTime(){
        int leftTime = 0;
        if (null != mDataBase){
            Cursor cursor = mDataBase.query("user", new String[]{"id","coinNum","pointNum","timeNum"}, null, null, null, null, null, null);
            while(cursor.moveToNext()){
                leftTime = cursor.getInt(cursor.getColumnIndex("timeNum"));
//                Log.i("TAG","timeNum from db: " + leftTime);
                cursor.close();
                return leftTime;
            }
        }
        return 0;
    }

    public static void setLeftTime(int leftTime){
        if (leftTime <0){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("timeNum", leftTime);
        if (null != mDataBase){
            mDataBase.update("user", values, null, null);
        }
    }

    private static boolean checkDBIsEmpty(SQLiteDatabase database){
        Cursor cursor = null;
        int num;
        try {
            cursor = database.rawQuery("select * from user", null);
            num = cursor.getCount();
//            Log.i("TAG","数据条数：" + num);
            if (num>0){
                cursor.close() ;
                return false;
            }else{
                cursor.close() ;
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }finally {
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }
    }

    public static void closeDB(){
        if (null != mDataBase){
            mDataBase.close();
        }
    }
}

