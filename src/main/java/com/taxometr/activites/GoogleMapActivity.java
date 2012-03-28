package com.taxometr.activites;

import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.maps.*;
import com.taxometr.R;
import com.taxometr.helpers.LocationHelper;

import java.util.List;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 15.03.12
 */
public class GoogleMapActivity extends MapActivity {
    private static final int MIN_UPDATE_TIME = 3000;
    private static final int MIN_DISTANCE = 1000;
    private MapController mapController;

    private MapView mapView;

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

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            this.finish();
            return;
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(false);
        String locationProviderType = locationManager.getBestProvider(criteria, true);

/*      final List<String> enabledProviders = locationManager.getProviders(true);
        if (enabledProviders.contains(LocationManager.GPS_PROVIDER)) {
            locationProviderType = LocationManager.GPS_PROVIDER;
        } else if (enabledProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProviderType = LocationManager.NETWORK_PROVIDER;
        } else {
            locationProviderType = LocationManager.PASSIVE_PROVIDER;
        }*/
        Log.d(LocationHelper.LOGTAG, "locationProviderType: " + locationProviderType);   //TODO remove
        final LocationProvider locationProvider = locationManager.getProvider(locationProviderType);
        if (locationProvider != null) {
            locationManager.requestLocationUpdates(locationProvider.getName(), MIN_UPDATE_TIME, MIN_DISTANCE,
                    this.locationListenerRecenterMap);
        } else {
            Toast.makeText(this, "Taxometr cannot continue,"
                    + " the GPS location provider is not available"
                    + " at this time.", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        final GeoPoint lastKnownPoint = LocationHelper.getLastKnownPoint(locationManager, locationProviderType);
        mapController = this.mapView.getController();
        mapController.setZoom(10);
        mapController.animateTo(lastKnownPoint);
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