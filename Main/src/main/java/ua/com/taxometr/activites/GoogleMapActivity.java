package ua.com.taxometr.activites;

import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.maps.*;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.LocationHelper;
import ua.com.taxometr.mapOverlays.AddressItemizedOverlay;

import java.io.IOException;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 15.03.12
 */
public class GoogleMapActivity extends MapActivity {
    private static final String CLASSTAG = GoogleMapActivity.class.getSimpleName();

    private MapController mapController;

    private MapView mapView;

    private final LocationListener locationListenerRecenterMap = new LocationTrackingListener();
    private AddressItemizedOverlay addressItemizedOverlay;
    private LocationManager locationManager;
    private Button acceptBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_view);
        mapView = (MapView) this.findViewById(R.id.map_view);
        mapView.setBuiltInZoomControls(false);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            this.finish();
            return;
        }

        final MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
        myLocationOverlay.disableCompass();
        myLocationOverlay.enableMyLocation();
        addressItemizedOverlay = new AddressItemizedOverlay(getResources().getDrawable(R.drawable.red_pin));
        mapView.getOverlays().add(addressItemizedOverlay);
        mapView.getOverlays().add(myLocationOverlay);
        mapView.setOnTouchListener(new MapOnTouchListener());
        final View.OnClickListener acceptBtnListener = new AcceptBtnListener();
        acceptBtn = (Button) findViewById(R.id.btn_accept);
        acceptBtn.setOnClickListener(acceptBtnListener);

        final ImageButton zoomInBtn = (ImageButton) findViewById(R.id.btn_zoom_in);
        final ImageButton zoomOutBtn = (ImageButton) findViewById(R.id.btn_zoom_out);
        final ImageButton myLocationBtn = (ImageButton) findViewById(R.id.btn_my_location);
        zoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.zoomIn();
            }
        });

        zoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapController.zoomOut();
            }
        });

        myLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final GeoPoint myLocation = myLocationOverlay.getMyLocation();
                if (myLocation != null) {
                    mapController.animateTo(myLocation);
                    mapController.setCenter(myLocation);
                }
            }
        });

        mapController = this.mapView.getController();
        mapController.setZoom(18);
    }

    @Override
    public void onResume() {
        super.onResume();
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(false);
        final String locationProviderType = locationManager.getBestProvider(criteria, true);
        final LocationProvider locationProvider = locationManager.getProvider(locationProviderType);
        if (locationProvider != null) {
            locationManager.requestLocationUpdates(locationProvider.getName(), LocationHelper.MIN_UPDATE_TIME, LocationHelper.MIN_DISTANCE,
                    locationListenerRecenterMap);
        } else {
            Toast.makeText(this, "Taxometr cannot continue,"
                    + " the GPS location provider is not available"
                    + " at this time.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

/*    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListenerRecenterMap);
    }*/

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListenerRecenterMap);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    /**
     * OnTouchListener for MapView <br/>
     * Add's overlay items to {@link GoogleMapActivity#addressItemizedOverlay}
     */
    private class MapOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                final Projection projection = mapView.getProjection();
                final GeoPoint geoPoint = projection.fromPixels((int) event.getX(), (int) event.getY());
                addressItemizedOverlay.addOverlay(new OverlayItem(geoPoint, "", ""));
                mapView.invalidate();
                acceptBtn.setEnabled(true);
            }

            return false;
        }
    }

    /**
     * OnClickListener for Accept button
     *
     * @see android.view.View.OnClickListener
     */
    private class AcceptBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            final Intent resultIntent = new Intent();
            int resultCode = RESULT_OK;
            String address = "";
            try {
                address = LocationHelper.getAddressStringByGeoPoint(addressItemizedOverlay.getItem(0).getPoint(), GoogleMapActivity.this);
            } catch (IOException e) {
                Log.e(CLASSTAG, e.getMessage(), e);
                resultCode = RESULT_CANCELED;
            }
            resultIntent.putExtra("address", address);
            setResult(resultCode, resultIntent);
            finish();
        }
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