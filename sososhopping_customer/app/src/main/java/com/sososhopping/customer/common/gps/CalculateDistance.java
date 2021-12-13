package com.sososhopping.customer.common.gps;

import android.util.Log;

import com.sososhopping.customer.common.types.Location;

public class CalculateDistance {


    public static float distance(Location start, Location target) {

        final int R = 6371; // Radius of the earth

        double theta = start.getLng() - target.getLng();
        double distance = Math.sin(Math.toRadians(start.getLat()))
                * Math.sin(Math.toRadians(target.getLat()))
                + Math.cos(Math.toRadians(start.getLat()))
                * Math.cos(Math.toRadians(target.getLat()))
                * Math.cos(Math.toRadians(theta));

        distance = Math.acos(distance);
        distance = Math.toDegrees(distance);
        distance = distance * 60 * 1.1515;
        distance = distance * 1.609344; //km


        /*double latDistance = Math.toRadians(target.getLat() - start.getLat());
        double lonDistance = Math.toRadians(target.getLng() - start.getLat());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(start.getLat())) * Math.cos(Math.toRadians(target.getLat()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = (R * c);*/

        /*double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);*/

        return (float) distance;
    }
}
