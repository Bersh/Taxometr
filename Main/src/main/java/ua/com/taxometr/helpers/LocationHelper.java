package ua.com.taxometr.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.inject.Singleton;
import ua.com.taxometr.R;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Useful functions and constants for location
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 */
@Singleton
public class LocationHelper implements LocationHelperInterface {

    /**
     * Basic method to get {@link android.location.Address}  by given coordinates(latitude, longitude). <br/>
     * This method used by all other getAddress methods
     *
     * @param latitude  latitude
     * @param longitude longitude
     * @param context   context
     * @return address bu given coordinates
     */
    @Override
    public Address getAddressByCoordinates(double latitude, double longitude, Context context) {
        final Geocoder geocoder = new Geocoder(context);
        final FutureTask<Address> getLocationByGeoPointTask = new FutureTask<Address>(new GetLocationByCoordinatesTask(latitude, longitude, geocoder));
        new Thread(getLocationByGeoPointTask).start();
        Address address = null;
        try {
            address = getLocationByGeoPointTask.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            getLocationByGeoPointTask.cancel(true);
            Log.d(LOGTAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.e(LOGTAG, e.getMessage());
        } catch (TimeoutException e) {
            Log.d(LOGTAG, "Timeout Exception: getAddressByCoordinates");
        }
        return address;
    }

    @Override
    public String getAddressStringByCoordinates(double latitude, double longitude, Context context) throws IOException {
        Address address = getAddressByCoordinates(latitude, longitude, context);
        return getAddressString(address);
    }

    /**
     * Returns address in string format by given {@link android.location.Address} object
     *
     * @param address {@link android.location.Address} object
     * @return address string
     */
    private static String getAddressString(Address address) {
        final StringBuilder sb = new StringBuilder();
        if (address == null) {
            return "";
        }
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

    @Override
    public String getAddressStringByLatLng(LatLng geoPoint, Context context) throws IOException {
        final double latitude = geoPoint.latitude;
        final double longitude = geoPoint.longitude;
        return getAddressStringByCoordinates(latitude, longitude, context);
    }

    /**
     * Return {@link com.google.android.gms.maps.model.LatLng} by given address string
     *
     * @param addressString address string
     * @param context       context
     * @return {@link com.google.android.gms.maps.model.LatLng} by given address string
     * @throws IOException if {@link android.location.Geocoder} is not available
     */
    @SuppressWarnings("ThrowCaughtLocally")
    public static LatLng getLatLngByAddressString(String addressString, Context context) throws IOException {
        final Geocoder geocoder = new Geocoder(context);
        final Address address;
        try {
            final List<Address> addresses = geocoder.getFromLocationName(addressString, 1);
            if (addresses.isEmpty()) {
                throw new IOException("No address found");
            }
            address = addresses.get(0);
        } catch (IOException e) {
            Log.e(LOGTAG, CLASSTAG + e.getMessage(), e);
            throw e;
        }
        final Double latitude = address.getLatitude();
        final Double longitude = address.getLongitude();
        return new LatLng(latitude, longitude);
    }

    /**
     * Requset location updates from {@link android.location.LocationProvider}
     *
     * @param context         context
     * @param locationManager {@link android.location.LocationManager} instance
     * @param listener        {@link android.location.LocationListener} for location changes
     */
    public static void requestLocationUpdates(Context context, LocationManager locationManager, LocationListener listener) {
        if (locationManager == null) {
            Toast.makeText(context, context.getString(R.string.err_gps_not_available),
                    Toast.LENGTH_LONG).show();
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
    public static boolean isGpsAvailable(Context context) {
        final PackageManager pm = context.getPackageManager();
        final boolean hasGPS = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        LocationManager locationManager = null;

        //On some devices without GPS hasGPS might be true
        if (hasGPS) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        return ((locationManager != null) && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)));
    }

    /**
     * Check if internet is present
     *
     * @param context context
     * @return true if internet is present
     */
    public static boolean isInternetPresent(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * Retrieves address from {@link android.location.Geocoder}  by given latitude, longitude
     */
    private static class GetLocationByCoordinatesTask implements Callable<Address> {
        private final double latitude;
        private final double longitude;
        private final Geocoder geocoder;
                                                                   `
        GetLocationByCoordinatesTask(double latitude, double longitude, Geocoder geocoder) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.geocoder = geocoder;
        }

        @Override
        public Address call() throws IOException {
            try {
                final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                return addresses.get(0);
            } catch (IOException e) {
                Log.e(LOGTAG, CLASSTAG + e.getMessage(), e);
                throw e;
            }
        }
    }

}