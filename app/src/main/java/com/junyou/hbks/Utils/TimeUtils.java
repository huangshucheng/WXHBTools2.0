package com.junyou.hbks.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    /**
     * 获取现在时间
     */
    public static String getCurTime(){
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
//        Log.i("TAG", "现在时间:" + curDate);
        return curDate;
    }

    /*
        保存的时间和现在时间进行对比
        日期格式：2016-10-09
     */
    public static int friendly_time(String saveDate){

        if (saveDate.isEmpty()){
            return 0;
        }

        Date time = toDate(saveDate);
        if(time == null) {
            return 0;
        }

        Calendar cal = Calendar.getInstance();

        if (cal == null){
            return 0;
        }

        //判断是否是同一天

        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);

        if(curDate.equals(paramDate)){
            return 0;
        }

        long ct = cal.getTimeInMillis()/86400000;   //日历的时间
        long lt = time.getTime()/86400000;          //输入的时间
        int days = (int)(ct - lt);                  //时间差

        if (days >0){
//            Log.i("TAG", "时间差:" + days +"天");
            return days-1;
        }

        return 0;
    }
    /*
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        if(time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        //判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);

        if(curDate.equals(paramDate)){
            int hour = (int)((cal.getTimeInMillis() - time.getTime())/3600000);
            if(hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000,1)+"分钟前";
            else
                ftime = hour+"小时前";
            return ftime;
        }

        long lt = time.getTime()/86400000;          //输入的时间
        long ct = cal.getTimeInMillis()/86400000;   //日历的时间
        int days = (int)(ct - lt);                  //时间差

        if(days == 0){
            int hour = (int)((cal.getTimeInMillis() - time.getTime())/3600000);
            if(hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000,1)+"分钟前";
            else
                ftime = hour+"小时前";
        }
        else if(days == 1){
            ftime = "昨天";
        }
        else if(days == 2){
            ftime = "前天";
        }
        else if(days > 2 && days <= 10){
            ftime = days+"天前";
        }
        else if(days > 10){
//            ftime = dateFormater2.get().format(time);
            ftime = days+"天前";
        }
        return ftime;
    }
    */
    /**
     * 将字符串转位日期类型
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater2.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

}
