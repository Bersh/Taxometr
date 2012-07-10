package ua.com.taxometr.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.util.Log;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import ua.com.taxometr.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * useful functions and constants for location
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 */
public class LocationHelper {
    /**
     * Class tag for logging
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
     * log tag for logging
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
     * Timeout for determining location in milliseconds
     */
    public static final int GPS_TIMEOUT = 30000;

    /**
     * Default constructor
     */
    private LocationHelper() {
    }

    /**
     * Parse Location into GeoPoint  <br/>
     * note GeoPoint stores lat/long as "integer numbers of microdegrees"
     * meaning int*1E6
     *
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
     *
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
     *
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
     * @param locationManager      location manager
     * @param locationProviderType location provider type
     * @return GeoPoint corresponds to last known point from LocationManager
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
     *
     * @param latitude  latitude
     * @param longitude longitude
     * @param context   context
     * @return address by given coordinates
     * @throws IOException if {@link android.location.Geocoder} is not available
     */
    public static Address getAddressByCoordinates(double latitude, double longitude, Context context) throws IOException {
        final Geocoder geocoder = new Geocoder(context);
        try {
            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0);
        } catch (IOException e) {
            Log.e(LOGTAG, CLASSTAG + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Returns assress in string format by given coordinates
     * @param latitude latitude
     * @param longitude longitude
     * @param context context
     * @return address string
     * @throws IOException if {@link android.location.Geocoder} is not available
     */
    public static String getAddressStringByCoordinates(double latitude, double longitude, Context context) throws IOException {
        final Geocoder geocoder = new Geocoder(context);
        final Address address;
        try {
            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0);
        } catch (IOException e) {
            Log.e(LOGTAG, CLASSTAG + e.getMessage(), e);
            throw e;
        }

        return getAddressString(address);
    }

    /**
     * Returns address in string format by given {@link android.location.Address} object
     * @param address {@link android.location.Address} object
     * @return address string
     */
    private static String getAddressString(Address address) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            final String s = address.getAddressLine(i);
            if (s != null) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(s);
            }
        }
        return sb.toString();
    }

    /**
     * Return string address representation by given {@link com.google.android.maps.GeoPoint}
     *
     * @param geoPoint geo point
     * @param context  context
     * @return address by given coordinates
     * @throws IOException if {@link android.location.Geocoder} is not available
     */
    public static String getAddressStringByGeoPoint(GeoPoint geoPoint, Context context) throws IOException {
        final double latitude = geoPoint.getLatitudeE6() / MILLION;
        final double longitude = geoPoint.getLongitudeE6() / MILLION;
        final Geocoder geocoder = new Geocoder(context);
        final Address address;
        try {
            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0);
        } catch (IOException e) {
            Log.e(LOGTAG, CLASSTAG + e.getMessage(), e);
            throw e;
        }

        return getAddressString(address);
    }

    /**
     * Return {@link com.google.android.maps.GeoPoint} by given address string
     * @param addressString address string
     * @param context context
     * @return {@link com.google.android.maps.GeoPoint} by given address string
     * @throws IOException if {@link android.location.Geocoder} is not available
     */
    @SuppressWarnings("ThrowCaughtLocally")
    public static GeoPoint getGeoPointByAddressString(String addressString, Context context) throws IOException {
        final Geocoder geocoder = new Geocoder(context);
        final Address address;
        try {
            final List<Address> addresses = geocoder.getFromLocationName(addressString, 1);
            if(addresses.isEmpty()) {
                throw new IOException("No address found");
            }
            address = addresses.get(0);
        } catch (IOException e) {
            Log.e(LOGTAG, CLASSTAG + e.getMessage(), e);
            throw e;
        }
        final Double latitude = address.getLatitude() * MILLION;
        final Double longitude = address.getLongitude() * MILLION;
        return new GeoPoint(latitude.intValue(), longitude.intValue());
    }

    /**
     * Requset location updates from {@link android.location.LocationProvider}
     * @param context context
     * @param locationManager {@link android.location.LocationManager} instance
     * @param listener {@link android.location.LocationListener} for location changes
     */
    public static void requestLocationUpdates(Context context, LocationManager locationManager, LocationListener listener) {
        if (locationManager == null) {
            Toast.makeText(context, context.getString(R.string.err_gps_not_available),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(false);
        final String locationProviderType = locationManager.getBestProvider(criteria, true);
        LocationProvider locationProvider = locationManager.getProvider(locationProviderType);
        if (locationProvider != null) {
            locationManager.requestLocationUpdates(locationProvider.getName(), LocationHelper.MIN_UPDATE_TIME, LocationHelper.MIN_DISTANCE,
                    listener);
        }

        // Because on some devices GPS location works bad
        if (!locationProviderType.equals(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
            if (locationProvider != null) {
                locationManager.requestLocationUpdates(locationProvider.getName(), LocationHelper.MIN_UPDATE_TIME, LocationHelper.MIN_DISTANCE,
                        listener);
            }
        }
    }

    /**
     * Tests is GPS available
     *
     * @param context context
     * @return true if GPS available
     */
    public static boolean testGpsAvailable(Context context) {
        final PackageManager pm = context.getPackageManager();
        final boolean hasGPS = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        LocationManager locationManager = null;

        //On some devices without GPS hasGPS might be true
        if (hasGPS) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        return ((locationManager != null) && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)));
    }
}