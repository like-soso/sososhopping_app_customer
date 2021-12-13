package com.sososhopping.customer.common;

public class StringFormatMethod {

    public static String getRating(Double d){
        return Double.toString(Math.round(d*10)/10.0);
    }

    public static String getDistance(Float f){
        if(f >= 1){
            return String.format(Math.round(f*10)/10.0+"km");
        }else{
            return String.format(Math.round(f*1000)+"m");
        }
    }
}
