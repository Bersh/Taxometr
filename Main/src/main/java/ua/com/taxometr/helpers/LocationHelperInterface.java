package ua.com.taxometr.helpers;

import android.content.Context;
import android.location.Address;
import com.google.android.maps.GeoPoint;

import java.io.IOException;

public interface LocationHelperInterface {
    /**
     * Class tag for logging
     */
    String CLASSTAG = LocationHelper.class.getSimpleName();
    /**
     * million
     */
    double MILLION = 1e6;
    /**
     * Golden Gate location
     */
    GeoPoint DEFAULT_LOCATION = new GeoPoint((int) (30.30 * LocationHelperInterface.MILLION),
            (int) (50.27 * LocationHelperInterface.MILLION));
    /**
     * log tag for logging
     */
    String LOGTAG = "taxometr";
    /**
     * the minimum time interval for notifications, in milliseconds.
     */
    int MIN_UPDATE_TIME = 3000;
    /**
     * the minimum distance interval for notifications, in meters
     */
    int MIN_DISTANCE = 10;
    /**
     * Timeout for determining location in milliseconds
     */
    int GPS_TIMEOUT = 30000;

    /**
     * Return address by given coordinates
     *
     * @param latitude  latitude
     * @param longitude longitude
     * @param context   context
     * @return address by given coordinates
     * @throws java.io.IOException if {@link android.location.Geocoder} is not available
     */
    Address getAddressByCoordinates(double latitude, double longitude, Context context) throws IOException;

    /**
     * Returns assress in string format by given coordinates
     *
     * @param latitude  latitude
     * @param longitude longitude
     * @param context   context
     * @return address string
     * @throws java.io.IOException if {@link android.location.Geocoder} is not available
     */
    String getAddressStringByCoordinates(double latitude, double longitude, Context context) throws IOException;

    /**
     * Return string address representation by given {@link com.google.android.maps.GeoPoint}
     *
     * @param geoPoint geo point
     * @param context  context
     * @return address by given coordinates
     * @throws java.io.IOException if {@link android.location.Geocoder} is not available
     */
    String getAddressStringByGeoPoint(GeoPoint geoPoint, Context context) throws IOException;
}
