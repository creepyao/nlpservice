package com.yao.util;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wpy on 2017/11/16.
 */
public class DateUtil {
    public static Date StringToDate(String date_string,String date_formate){
        SimpleDateFormat sdf = new SimpleDateFormat(date_formate);
        try {
            Date date = sdf.parse(date_string);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DataToString(Date datetime,String date_format){
        SimpleDateFormat sdf = new SimpleDateFormat(date_format);
        try {
            String date_string = sdf.format(datetime);
            return date_string;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

    /**
     * 日期是一年中的第几天
     */
    public static int dayOfYear(Date date){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 当前日期倒退n天
     * @throws Exception
     */
    public static Date backDays(int n){
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - n);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endDate;
    }

    /**
     * 指定日期倒退n天
     * @throws Exception
     */
    public static Date backDays(int n,Date beginDate){
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - n);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endDate;
    }

    @Test
    public void test() throws Exception{
        Date newDate = DateUtil.StringToDate("2012-11-27 11:31:41.111","yyyy-MM-dd hh:mm:ss.SSSSSS");
        System.out.print(newDate);
    }
}
