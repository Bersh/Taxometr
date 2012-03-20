package com.taxometr.activites;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.taxometr.R;
import com.taxometr.helpers.LocationHelper;

import java.util.List;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 15.03.12
 */
public class MainActivity extends MapActivity {
    private MapController mapController;
    private LocationManager locationManager;

    private LocationProvider locationProvider;
    private MapView mapView;
    private Drawable defaultMarker;
    private String locationProviderType;

    private final LocationListener locationListenerRecenterMap =
            new LocationListener() {
                public void onLocationChanged(final Location loc) {
                    int lat = (int) (loc.getLatitude()
                            * LocationHelper.MILLION);
                    int lon = (int) (loc.getLongitude()
                            * LocationHelper.MILLION);
                    GeoPoint geoPoint = new GeoPoint(lat, lon);
                    mapController.animateTo(geoPoint);
                }

                public void onProviderDisabled(String s) {
                }

                public void onProviderEnabled(String s) {
                }

                public void onStatusChanged(String s,
                                            int i, Bundle b) {
                }
            };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.main);
        this.mapView = (MapView) this.findViewById(R.id.map_view);

        this.defaultMarker = getResources().getDrawable(R.drawable.red_pin);
        this.defaultMarker.setBounds(0, 0,
                this.defaultMarker.getIntrinsicWidth(),
                this.defaultMarker.getIntrinsicHeight());
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;  //TODO Implement
    }

    @Override
    public void onStart() {
        super.onStart();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            this.finish();
        }
        List<String> enabledProviders = locationManager.getProviders(true);
        if (enabledProviders.contains(LocationManager.GPS_PROVIDER)) {
            locationProviderType = LocationManager.GPS_PROVIDER;
        } else if (enabledProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProviderType = LocationManager.NETWORK_PROVIDER;
        } else {
            locationProviderType = LocationManager.PASSIVE_PROVIDER;
        }
        locationProvider = this.locationManager.getProvider(locationProviderType);
        if (locationProvider != null) {
            this.locationManager.requestLocationUpdates(
                    locationProvider.getName(), 3000, 1000,
                    this.locationListenerRecenterMap);
        } else {
            Toast.makeText(this, "Taxometr cannot continue,"
                    + " the GPS location provider is not available"
                    + " at this time.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        GeoPoint lastKnownPoint = getLastKnownPoint();
        this.mapController = this.mapView.getController();
        this.mapController.setZoom(10);
        this.mapController.animateTo(lastKnownPoint);
    }

    private GeoPoint getLastKnownPoint() {
        GeoPoint lastKnownPoint;
        Location lastKnownLocation = this.locationManager.getLastKnownLocation(locationProviderType);
        if (lastKnownLocation != null) {
            lastKnownPoint = LocationHelper.getGeoPoint(lastKnownLocation);
        } else {
            lastKnownPoint = LocationHelper.GOLDEN_GATE;
        }
        return lastKnownPoint;
    }


}