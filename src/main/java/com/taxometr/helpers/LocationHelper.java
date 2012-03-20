package com.taxometr.helpers;

import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import java.text.DecimalFormat;

public class LocationHelper {

    public static final String CLASSTAG = LocationHelper.class.getSimpleName();

    public static final double MILLION = 1e6;

    private static final DecimalFormat DEC_FORMAT = new DecimalFormat("###.##");

    public static final GeoPoint GOLDEN_GATE = new GeoPoint((int) (37.49 * LocationHelper.MILLION),
            (int) (-122.49 * LocationHelper.MILLION));
    
    private static final String LOGTAG = "taxometr";

    // note GeoPoint stores lat/long as "integer numbers of microdegrees"
    // meaning int*1E6
    // parse Location into GeoPoint
    public static GeoPoint getGeoPoint(final Location loc) {
        int lat = (int) (loc.getLatitude() * LocationHelper.MILLION);
        int lon = (int) (loc.getLongitude() * LocationHelper.MILLION);
        return new GeoPoint(lat, lon);
    }

    // parse geoRssPoint into GeoPoint(<georss:point>36.835 -121.899</georss:point>)
    public static GeoPoint getGeoPoint(final String geoRssPoint) {
        Log.d(LOGTAG, LocationHelper.CLASSTAG + " getGeoPoint - geoRssPoint - " + geoRssPoint);
        GeoPoint returnPoint = null;
        String gPoint = geoRssPoint.trim();
        if (gPoint.contains(" ")) {
            String latString = gPoint.substring(0, gPoint.indexOf(" "));
            String lonString = gPoint.substring(gPoint.indexOf(" "), gPoint.length());
            double latd = Double.parseDouble(latString);
            double lond = Double.parseDouble(lonString);
            int lat = (int) (latd * LocationHelper.MILLION);
            int lon = (int) (lond * LocationHelper.MILLION);
            returnPoint = new GeoPoint(lat, lon);
        }
        return returnPoint;
    }

    // parse double point(-127.50) into String (127.50W)
    public static String parsePoint(final double point, final boolean isLat) {
        Log.d(LOGTAG, LocationHelper.CLASSTAG + " parsePoint - point - " + point + " isLat - " + isLat);
        String result = LocationHelper.DEC_FORMAT.format(point);
        if (result.contains("-")) {
            result = result.substring(1, result.length());
        }
        // latitude is decimal expressed as +- 0-90
        // (South negative, North positive, from Equator)
        if (isLat) {
            if (point < 0) {
                result += "S";
            } else {
                result += "N";
            }
        }
        // longitude is decimal expressed as +- 0-180
        // (West negative, East positive, from Prime Meridian)
        else {
            if (point < 0) {
                result += "W";
            } else {
                result += "E";
            }
        }
        Log.d(LOGTAG, LocationHelper.CLASSTAG + " parsePoint result - " + result);
        return result;
    }
}