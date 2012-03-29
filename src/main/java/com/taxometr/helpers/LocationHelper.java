package com.taxometr.helpers;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import com.google.android.maps.GeoPoint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * useful functions and constants for location
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 */
public class LocationHelper {
    /**
     * Class tag fro logging
     */
    public static final String CLASSTAG = LocationHelper.class.getSimpleName();

    /**
     * million
     */
    public static final double MILLION = 1e6;

    private static final DecimalFormat DEC_FORMAT = new DecimalFormat("###.##");

    /**
     * Golden Gate location
     */
    public static final GeoPoint DEFAULT_LOCATION = new GeoPoint((int) (30.30 * LocationHelper.MILLION),
            (int) (50.27 * LocationHelper.MILLION));

    /**
     * log tag fro logging
     */
    public static final String LOGTAG = "taxometr";

    /**
     * the minimum time interval for notifications, in milliseconds.
     */
    public static final int MIN_UPDATE_TIME = 3000;

    /**
     * the minimum distance interval for notifications, in meters
     */
    public static final int MIN_DISTANCE = 10;

    /**
     * Default constructor
     */
    private LocationHelper() {
    }

    /**
     * Parse Location into GeoPoint  <br/>
     * note GeoPoint stores lat/long as "integer numbers of microdegrees"
     * meaning int*1E6
     * @param loc instance of {@link android.location.Location}
     * @return {@link com.google.android.maps.GeoPoint} converted from given location
     */
    public static GeoPoint getGeoPoint(final Location loc) {
        final int lat = (int) (loc.getLatitude() * LocationHelper.MILLION);
        final int lon = (int) (loc.getLongitude() * LocationHelper.MILLION);
        return new GeoPoint(lat, lon);
    }

    /**
     * Parse geoRssPoint into GeoPoint(<georss:point>36.835 -121.899</georss:point>)
     * @param geoRssPoint geo point in rss format
     * @return {@link com.google.android.maps.GeoPoint} converted from rss format
     */
    public static GeoPoint getGeoPoint(final String geoRssPoint) {
        Log.d(LOGTAG, LocationHelper.CLASSTAG + " getGeoPoint - geoRssPoint - " + geoRssPoint);
        GeoPoint returnPoint = null;
        final String gPoint = geoRssPoint.trim();
        if (gPoint.contains(" ")) {
            final String latString = gPoint.substring(0, gPoint.indexOf(" "));
            final String lonString = gPoint.substring(gPoint.indexOf(" "), gPoint.length());
            final double latd = Double.parseDouble(latString);
            final double lond = Double.parseDouble(lonString);
            final int lat = (int) (latd * LocationHelper.MILLION);
            final int lon = (int) (lond * LocationHelper.MILLION);
            returnPoint = new GeoPoint(lat, lon);
        }
        return returnPoint;
    }

    /**
     * Parse double point(-127.50) into String (127.50W)
     * @param point double cordinate
     * @param isLat is latitude
     * @return string coordinate
     */
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

    /**
     * Converts last known point from LocationManager to GeoPoint
     *
     * @param locationManager location manager
     * @param locationProviderType location provider type
     * @return GeoPoint coresponds to last known point from LocationManager
     */
    public static GeoPoint getLastKnownPoint(LocationManager locationManager, String locationProviderType) {
        final GeoPoint lastKnownPoint;
        final Location lastKnownLocation = locationManager.getLastKnownLocation(locationProviderType);
        if (lastKnownLocation != null) {
            lastKnownPoint = LocationHelper.getGeoPoint(lastKnownLocation);
        } else {
            lastKnownPoint = LocationHelper.DEFAULT_LOCATION;
        }
        return lastKnownPoint;
    }

    /**
     * Return address by given coordinates
     * @param latitude latitude
     * @param longitude longitude
     * @param context context
     * @return address by given coordinates
     * @throws IOException if {@link android.location.Geocoder} is not available
     */
    public static Address getAddressByCoordinates(double latitude, double longitude, Context context) throws IOException {
        final Geocoder geocoder = new Geocoder(context);
        try{
            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0);
        } catch (IOException e) {
            Log.e(LOGTAG, "Error", e);
            throw e;
        }
    }
}