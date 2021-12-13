package com.sososhopping.customer.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatMethod {
    public static String dateFormatMin(String exdate){
        try {
            Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(exdate);
            return new SimpleDateFormat("yyyy/MM/dd HH:mm").format(date);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String dateFormatDay(String exdate){
        try {
            Date date = new SimpleDateFormat("yyyy/MM/dd").parse(exdate);
            return new SimpleDateFormat("yyyy/MM/dd").format(date);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String dateFormatTimetoDay(String exdate){
        try {
            Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(exdate);
            return new SimpleDateFormat("yyyy/MM/dd").format(date);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String dateFormatToKorean(String exdate){
        try {
            Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(exdate);
            return new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm").format(date);

        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }


    public static String dateFormat(int year, int month, int day){
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return new SimpleDateFormat("yyyy/MM/dd").format(c.getTime());
    }

    public static String timeFormat(int hour, int min){
        StringBuilder sb = new StringBuilder();
        if(hour < 10){
            sb.append(0);
        }
        sb.append(hour);
        sb.append(":");
        if(min < 10){
            sb.append(0);
        }
        sb.append(min);
        return sb.toString();
    }
}
