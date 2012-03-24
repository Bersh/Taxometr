package com.taxometr.activites;

import java.util.List;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.taxometr.helpers.LocationHelper;
import com.taxometr.R;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 15.03.12
 */
public class GoogleMapActivity extends MapActivity {
    private static final int MIN_UPDATE_TIME = 3000;
    private static final int MIN_DISTANCE = 1000;
    private MapController mapController;
    private LocationManager locationManager;

    private MapView mapView;
    private String locationProviderType;

    private final LocationListener locationListenerRecenterMap = new LocationTrackingListener();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.google_map_view);
        mapView = (MapView) this.findViewById(R.id.map_view);
        mapView.setBuiltInZoomControls(true);
        final MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
        myLocationOverlay.disableCompass();
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            this.finish();
        }
        final List<String> enabledProviders = locationManager.getProviders(true);
        if (enabledProviders.contains(LocationManager.GPS_PROVIDER)) {
            locationProviderType = LocationManager.GPS_PROVIDER;
        } else if (enabledProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProviderType = LocationManager.NETWORK_PROVIDER;
        } else {
            locationProviderType = LocationManager.PASSIVE_PROVIDER;
        }

        final LocationProvider locationProvider = this.locationManager.getProvider(locationProviderType);
        if (locationProvider != null) {
            this.locationManager.requestLocationUpdates(locationProvider.getName(), MIN_UPDATE_TIME, MIN_DISTANCE,
                    this.locationListenerRecenterMap);
        } else {
            Toast.makeText(this, "Taxometr cannot continue,"
                    + " the GPS location provider is not available"
                    + " at this time.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        final GeoPoint lastKnownPoint = getLastKnownPoint();
        mapController = this.mapView.getController();
        mapController.setZoom(10);
        mapController.animateTo(lastKnownPoint);
    }

    /**
     * Converts last known point from LocationManager to GeoPoint
     *
     * @return GeoPoint coresponds to last known point from LocationManager
     */
    private GeoPoint getLastKnownPoint() {
        final GeoPoint lastKnownPoint;
        final Location lastKnownLocation = this.locationManager.getLastKnownLocation(locationProviderType);
        if (lastKnownLocation != null) {
            lastKnownPoint = LocationHelper.getGeoPoint(lastKnownLocation);
        } else {
            lastKnownPoint = LocationHelper.DEFAULT_LOCATION;
        }
        return lastKnownPoint;
    }

    /**
     * LocationListener to track location changes
     */
    private class LocationTrackingListener implements LocationListener {
        @Override
        public void onLocationChanged(final Location loc) {
            final int lat = (int) (loc.getLatitude() * LocationHelper.MILLION);
            final int lon = (int) (loc.getLongitude() * LocationHelper.MILLION);
            final GeoPoint geoPoint = new GeoPoint(lat, lon);
            mapController.animateTo(geoPoint);
            mapController.setCenter(geoPoint);
        }

        @Override
        public void onProviderDisabled(String s) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle b) {
        }
    }
}