package com.jereaa;/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::                                                                         :
::  This routine calculates the distance between two points (given the     :
::  latitude/longitude of those points). It is being used to calculate     :
::  the distance between two locations using GeoDataSource (TM) prodducts  :
::                                                                         :
::  Definitions:                                                           :
::    South latitudes are negative, east longitudes are positive           :
::                                                                         :
::  Passed to function:                                                    :
::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :
::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :
::    unit = the unit you desire for results                               :
::           where: 'M' is statute miles (default)                         :
::                  'K' is kilometers                                      :
::                  'N' is nautical miles                                  :
::  Worldwide cities and other features databases with latitude longitude  :
::  are available at https://www.geodatasource.com                         :
::                                                                         :
::  For enquiries, please contact sales@geodatasource.com                  :
::                                                                         :
::  Official Web site: https://www.geodatasource.com                       :
::                                                                         :
::           GeoDataSource.com (C) All Rights Reserved 2018                :
::                                                                         :
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

public class DistanceCalculator {

    /**
     * Calculates the distance between 2 points in the map, using latitude and longitude.
     * Code from https://www.geodatasource.com/developers/java
     *
     * @param lat1 {@code double} number representing latitude value of point 1
     * @param lon1 {@code double} number representing longitude value of point 1
     * @param lat2 {@code double} number representing latitude value of point 2
     * @param lon2 {@code double} number representing longitude value of point 2
     * @param unit {@code String} containing the letter representing the units desired to express the distance (K: Kilometers, M: Miles, N: Nautical Miles)
     * @return {@code double} number representing the distance between the 2 points
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }
}