package com.junyou.hbks.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;

import com.junyou.hbks.config.Constants;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeManager {

    private static Activity activity;
    private static SharedPreferences sharedP =null;
    private static SharedPreferences.Editor editor = null;

    private static String FIRST_TIME = "first_time";        //第一次进来的时间
    private static String FIRST_TIME_MARK = "first_time_mark";//标记
    private static final String FIRSTTIME_DRAW = "firsttimedraw";
    private static final String DATE_MARK = "date_mark";                //日期记录
    private static final String FIRST_DRAW = "first_draw";              //每天第一次抽奖
    //初始化
    public static void init(Activity activity) {
        TimeManager.activity = activity;
        sharedP = activity.getSharedPreferences("config",activity.MODE_PRIVATE);
        editor  = sharedP.edit();

        int isFirstEnter = activity.getSharedPreferences("config",activity.MODE_PRIVATE).getInt(FIRST_TIME_MARK,-99);
        if (isFirstEnter == -99){
            setFirstTime();
            editor.putInt(FIRST_TIME_MARK,99);
            editor.apply();
            //设置拥有时间
//            setLeftTime(2880);      //48*60分钟 2天时间(单位：分钟)
            setLeftTime(LocalSaveUtil.getLeftTime());       //3天
//            setLeftTime("1");     //
            setFirstTimeDraw("2016-12-01");
            setFirstDraw(false);
        }
    }

    //获取当前系统时间
    private static CharSequence getSystemTime() {
        long sysTime = System.currentTimeMillis();
//        return  DateFormat.format("hh:mm:ss",sysTime);
        return  DateFormat.format("yyyy-MM-dd HH:mm:ss",sysTime);
    }

    //设置刚进来的时间
    public static void setFirstTime(){
        if (null != editor)
        {
            editor.putString(FIRST_TIME,""+ getSystemTime().toString());
            editor.apply();
        }
//        Log.i("TAG","设置第一次进来时间: " + getSystemTime());
    }

    public static String getFirstTime(){
        if (null != activity){
            String leaveTime = activity.getSharedPreferences("config",activity.MODE_PRIVATE).getString(FIRST_TIME,"");
            return leaveTime;
        }
        return  null;
    }

    //使用的时间  从装机到现在（分钟）
    public static int getDiffTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            if(!"".equals(getFirstTime()) && !"".equals(getSystemTime().toString())){
//            Date d1 = df.parse("2016-11-18 10:00:00");
                Date d1 = df.parse(getFirstTime());                 //第一次进来时间
                Date d2 = df.parse(getSystemTime().toString());     //现在时间
                long diff = d2.getTime() - d1.getTime();
                // Log.i("TAG" , "diff===" +diff);
                long days = diff / (1000 * 60 * 60 * 24);
                long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
                long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
                //String diffTime ="天:" + days + " 小时:" + hours + " 分钟:" + minutes;
                BigDecimal b1 = new BigDecimal(days*24*60);
                BigDecimal b2 = new BigDecimal(hours*60);
                BigDecimal b3 = new BigDecimal(minutes);
                int b4 = b1.add(b2).add(b3).intValue();
                return b4;
            }
            return 0;
        }catch (ParseException e){
            e.printStackTrace();
            return 0;
        }
    }
    //分钟 转换成 天+小时+分钟
    public static String minutesToDays(int minutes){
        if (minutes == 0){
            return "时间用完";
        }

        int m = minutes;
        int d = m / 60 / 24;
        int h = m / 60 - d * 24;
        int min = m - d * 24 * 60 - h * 60;
        if (d == 0){
            return "" + h + "时" + min + "分";
        }else{
            return "" + d + "天" + h + "时" + min + "分";
        }
//        return "" + d + "天" + h + "小时";
    }
    //添加时间(分钟)
    public static void addToLeftTime(int lefttime){
        if (lefttime == 0){
            return;
        }
        int time = lefttime;
        int localTime = getLeftTime();
        int totalTime = time + localTime;
        setLeftTime(totalTime);
//        Log.i("TAG","添加后时间："+totalTime);
    }
    //设置剩余时间
    public static void setLeftTime(int lefttime){
        LocalSaveUtil.setLeftTime(lefttime);
    }
    //获取剩余时间
    public static int getLeftTime(){
        return  LocalSaveUtil.getLeftTime();
    }
    //是否没时间了
    public static boolean isTimeout(){
        try{
            int totalTime = getLeftTime(); //总时间
            int useTime   = getDiffTime();   //使用时间
            int leftTime = totalTime - useTime;             //时间差
//            Log.i("TAG","总:" + totalTime + " 使用:" + useTime + " 差:" + leftTime);
            if (leftTime <= 0){
                setFirstTime();
                setLeftTime(0);
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //是否是新的一天
    public static boolean isNewDayFirstEnter(){
        String curDate = getCurDate();
        String defaultTime = activity.getSharedPreferences("config",activity.MODE_PRIVATE).getString(DATE_MARK,"empty");
        if ("empty".equals(defaultTime)){
            //第一次进来，是新的一天
//            Log.i("TAG","新的一天1111");
            if (null != editor){
                editor.putString(DATE_MARK,curDate);
                editor.apply();
                editor.putInt(Constants.USE_DAY,1);                 //使用的天数
                editor.apply();
                editor.putBoolean(Constants.IS_SERVICE_ON,true);
                editor.apply();
            }
            setNewDay(true);
            return true;
        }else{
            String saveTime = activity.getSharedPreferences("config",activity.MODE_PRIVATE).getString(DATE_MARK,"empty");
            if (curDate.equals(saveTime)){
                //不是新的一天
//                Log.i("TAG","不是新的一天");
                return false;
            }else{
                //是新的一天
//                Log.i("TAG","新的一天22222");
                if (null != editor){
                    editor.putString(DATE_MARK,curDate);
                    editor.apply();
                    int use_day = activity.getSharedPreferences("config",activity.MODE_PRIVATE).getInt(Constants.USE_DAY,0);
                    editor.putInt(Constants.USE_DAY,use_day +1);
                    editor.apply();
                }
                setNewDay(true);
                return true;
            }
        }
    }

    public static boolean getFirstDraw(){
        if (null != editor){
            boolean isFirstDraw = activity.getSharedPreferences("config",activity.MODE_PRIVATE).getBoolean(FIRST_DRAW,false);
            return  isFirstDraw;
        }
        return false;
    }

    public static void setFirstDraw(boolean isFirstDdraw){
        if (null != editor){
            editor.putBoolean(FIRST_DRAW,isFirstDdraw);
            editor.apply();
        }
    }

    public static boolean getNewDay(){
        if (null != editor){
           boolean isNewDay = activity.getSharedPreferences("config",activity.MODE_PRIVATE).getBoolean(Constants.IS_NEW_DAY,true);
            return  isNewDay;
        }
        return false;
    }

    public static boolean isDrawNewDay(){
        String firstTime = getFirstTimeDraw();
        String curTime = getCurTimeDraw();

        Log.i("TAG","draw firstTime: " + firstTime + ",  curTime: " + curTime);

        if (!"".equals(firstTime) && !"".equals(curTime)){
            if (!firstTime.equals(curTime)){
                Log.i("TAG","draw new day...");
                setFirstDraw(false);
                return true;
            }
        }
        Log.i("TAG","draw not new day...");
        return false;
    }

    public static void setFirstTimeDraw(String firstTime){
        if (null != editor) {
            editor.putString(FIRSTTIME_DRAW,""+ firstTime);
            editor.apply();
        }
    }

    public static String getFirstTimeDraw(){
        if (null != activity){
            String leaveTime = activity.getSharedPreferences("config",activity.MODE_PRIVATE).getString(FIRSTTIME_DRAW,"");
            return leaveTime;
        }
        return  null;
    }

    //获取当前系统时间
    public static String getCurTimeDraw() {
        long sysTime = System.currentTimeMillis();
        return  String.valueOf(DateFormat.format("yyyy-MM-dd",sysTime));
    }

    public static void setNewDay(boolean isNewDay){
        if (null != editor){
            editor.putBoolean(Constants.IS_NEW_DAY,isNewDay);
            editor.apply();
        }
    }

    public static int getUseDay(){
        int use_day = activity.getSharedPreferences("config",activity.MODE_PRIVATE).getInt(Constants.USE_DAY,1);
        return use_day;
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    //获取当前日期，年-月-日
    public static String getCurDate(){
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater.get().format(cal.getTime());
        return curDate;
    }

    public static void setServiceOnOrOff(boolean onOrOff){
        if (onOrOff){
            if (null != editor){
                editor.putBoolean(Constants.IS_SERVICE_ON,true);
                editor.apply();
            }
        }else{
            if (null != editor){
                editor.putBoolean(Constants.IS_SERVICE_ON,false);
                editor.apply();
            }
        }
    }

    public static boolean getServiceOnOrOff(){
        if (activity != null){
            boolean ison = activity.getSharedPreferences("config",activity.MODE_MULTI_PROCESS).getBoolean(Constants.IS_SERVICE_ON,true);
            return ison;
        }
        return true;
    }

    public static void setLifeLongUse(boolean isLifeLong){
        if (isLifeLong){
            if (null != editor){
                editor.putBoolean(Constants.IS_ALLLIFEUSE,true);    //终身使用
                editor.apply();
            }
        }else{
            if (null != editor){
                editor.putBoolean(Constants.IS_ALLLIFEUSE,false);
                editor.apply();
            }
        }
    }
}
