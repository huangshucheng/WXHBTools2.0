package com.junyou.hbks.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalSaveUtil {

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
            values.put("id", idNum);        //id
            values.put("coinNum",100);       //金币数量
            values.put("pointNum",0);       //积分数量
            values.put("isGiveThreeDay",0); //是否显示赠送了三天  0未显示,1显示
            values.put("timeNum",4320);     //初始时间三天
//            values.put("timeNum",2880);     //两天
//            values.put("timeNum",1);
            mDataBase.insert("user", null, values);
        }else{
            Log.i("TAG","db is not empty" );
        }
        //查询
        Cursor cursor = mDataBase.query("user", new String[]{"id","coinNum","pointNum","timeNum","isGiveThreeDay"}, null, null, null, null, null, null);
        //利用游标遍历所有数据对象
        if (null != cursor){
            while(cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int coinNum = cursor.getInt(cursor.getColumnIndex("coinNum"));
                int pointNum = cursor.getInt(cursor.getColumnIndex("pointNum"));
                int timeNum = cursor.getInt(cursor.getColumnIndex("timeNum"));
                int isGive = cursor.getInt(cursor.getColumnIndex("isGiveThreeDay"));
                //日志打印输出
                Log.i("TAG","db data: id:" + id + " ,coin:" + coinNum +" ,point:" +  pointNum + " ,time:" + timeNum + " ,isGive:" +isGive);
            }
            cursor.close();
        }
    }

    public static synchronized int getCoinNum(){
        int coinNum = 0;
        if (null != mDataBase){
            Cursor cursor = mDataBase.query("user", new String[]{"coinNum"}, null, null, null, null, null, null);
            if (null != cursor){
                while(cursor.moveToNext()){
                    coinNum = cursor.getInt(cursor.getColumnIndex("coinNum"));
                    Log.i("TAG","coin from db:" + coinNum);
                    cursor.close();
                    return coinNum;
                }
            }
        }
        return  0;
    }

    public static synchronized void setCoinNum(int coinNum){
        if (coinNum <0){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("coinNum", coinNum);
        if (null != mDataBase){
            mDataBase.update("user", values, null, null);
        }
    }

    public static synchronized int getPointNum(){
        int pointNum = 0;
        if (null != mDataBase){
            Cursor cursor = mDataBase.query("user", new String[]{"pointNum"}, null, null, null, null, null, null);
            if (null != cursor){
                while(cursor.moveToNext()){
                    pointNum = cursor.getInt(cursor.getColumnIndex("pointNum"));
                    Log.i("TAG","point from db: " + pointNum);
                    cursor.close();
                    return pointNum;
                }
            }
        }
        return  0;
    }

    public static synchronized void setPointNum(int pointNum){
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
    public static synchronized void setAccount(int account){
        if (account <0){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("id", account);
        if (null != mDataBase){
            mDataBase.update("user", values, null, null);
        }
    }

    public static synchronized int getAccount(){
        int userId = 0;
        if (null != mDataBase){
            Cursor cursor = mDataBase.query("user", new String[]{"id"}, null, null, null, null, null, null);
            if (null != cursor){
                while(cursor.moveToNext()){
                    userId = cursor.getInt(cursor.getColumnIndex("id"));
                    Log.i("TAG","id from db: " + userId);
                    cursor.close();
                    return userId;
                }
            }
        }
        return 0 ;
    }

    public static synchronized int getLeftTime(){
        int leftTime = 0;
        if (null != mDataBase){
            Cursor cursor = mDataBase.query("user", new String[]{"timeNum"}, null, null, null, null, null, null);
            if (null != cursor){
                while(cursor.moveToNext()){
                    leftTime = cursor.getInt(cursor.getColumnIndex("timeNum"));
//                Log.i("TAG","timeNum from db: " + leftTime);
                    cursor.close();
                    return leftTime;
                }
            }
        }
        return 0;
    }

    public static synchronized void setLeftTime(int leftTime){
        if (leftTime <0){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("timeNum", leftTime);
        if (null != mDataBase){
            mDataBase.update("user", values, null, null);
        }
    }

    public static synchronized void setIsGiveThreeDay(boolean isGive){
        if (isGive){
            ContentValues values = new ContentValues();
            values.put("isGiveThreeDay", 1);
            if (null != mDataBase){
                mDataBase.update("user", values, null, null);
            }
        }else{
            ContentValues values = new ContentValues();
            values.put("isGiveThreeDay", 0);
            if (null != mDataBase){
                mDataBase.update("user", values, null, null);
            }
        }
    }

    public static synchronized boolean getIsGiveThreeDay(){
        int leftTime = 0;
        if (null != mDataBase){
            Cursor cursor = mDataBase.query("user", new String[]{"isGiveThreeDay"}, null, null, null, null, null, null);
            if (null != cursor){
                while(cursor.moveToNext()){
                    leftTime = cursor.getInt(cursor.getColumnIndex("isGiveThreeDay"));
                    cursor.close();
                    if (leftTime == 0){
                        return false;
                    }else{
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkDBIsEmpty(SQLiteDatabase database){
        Cursor cursor = null;
        int num;
        try {
            cursor = database.rawQuery("select * from user", null);
            if (null != cursor){
                num = cursor.getCount();
//            Log.i("TAG","数据条数：" + num);
                if (num>0){
                    cursor.close() ;
                    return false;
                }else{
                    cursor.close() ;
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }finally {
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }
        return true;
    }

    public static void closeDB(){
        if (null != mDataBase){
            mDataBase.close();
        }
    }
}

