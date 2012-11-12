package ua.com.taxometr.activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.maps.*;
import com.google.inject.Inject;
import roboguice.activity.RoboMapActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import ua.com.taxometr.R;
import ua.com.taxometr.TaxometrApplication;
import ua.com.taxometr.helpers.LocationHelper;
import ua.com.taxometr.helpers.LocationHelperInterface;
import ua.com.taxometr.helpers.RoadHelper;
import ua.com.taxometr.mapOverlays.AddressItemizedOverlay;
import ua.com.taxometr.mapOverlays.RouteOverlay;
import ua.com.taxometr.routes.Road;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static ua.com.taxometr.helpers.LocationHelper.LOGTAG;
import static ua.com.taxometr.helpers.LocationHelper.getGeoPointByAddressString;

/**
 * Activity with google map view
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 15.03.12
 */
@ContentView(R.layout.google_map_view)
public class GoogleMapActivity extends RoboMapActivity {
    private static final String CLASSTAG = GoogleMapActivity.class.getSimpleName();
    private static final int MAP_ZOOM_LEVEL = 18;

    /**
     * key for route length data
     */
    public static final String ROUTE_LENGTH_KEY = "LENGTH";

    private MapController mapController;

    @InjectView(R.id.map_view)
    private MapView mapView;
    private final LocationListener locationTrackingListener = new LocationTrackingListener();
    private AddressItemizedOverlay addressItemizedOverlay;

    @Inject
    private LocationManager locationManager;

    @InjectView(R.id.btn_accept)
    private Button acceptBtn;

    @InjectExtra("isRouteMode")
    private boolean isInRouteMode;  //activity started to display route?
    private Road road;
    private ProgressDialog progressDialog;

    @InjectView(R.id.btn_my_location)
    private ImageButton myLocationBtn;

    @Inject
    LocationHelperInterface locationHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapView.setBuiltInZoomControls(false);

        if (locationManager == null) {
            this.finish();
            return;
        }

        final MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
        myLocationOverlay.disableCompass();
        myLocationOverlay.enableMyLocation();

        final View.OnClickListener acceptBtnListener = new AcceptBtnListener();
        acceptBtn.setOnClickListener(acceptBtnListener);

        final ImageButton zoomInBtn = (ImageButton) findViewById(R.id.btn_zoom_in);
        final ImageButton zoomOutBtn = (ImageButton) findViewById(R.id.btn_zoom_out);

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
                } else {
                    progressDialog = ProgressDialog.show(GoogleMapActivity.this, "", getString(R.string.dlg_progress_obtaining_location), true);
                }
            }
        });

        mapController = this.mapView.getController();
        mapController.setZoom(MAP_ZOOM_LEVEL);

        final Intent intent = getIntent();

        if (isInRouteMode) {
            final String fromAddress = intent.getStringExtra("fromAddress");
            final String toAddress = intent.getStringExtra("toAddress");
            final GeoPoint fromPoint;
            final GeoPoint toPoint;
            try {
                fromPoint = getGeoPointByAddressString(fromAddress, this);
                toPoint = getGeoPointByAddressString(toAddress, this);
            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.err_geocoder_not_available),
                        Toast.LENGTH_LONG).show();
                Log.e(CLASSTAG, e.getMessage());
                return;
            }
            new RouteCalculationTask().execute(fromPoint, toPoint);
        } else {  //activity started to select address
            addressItemizedOverlay = new AddressItemizedOverlay(getResources().getDrawable(R.drawable.red_pin));
            mapView.getOverlays().add(addressItemizedOverlay);
            mapView.getOverlays().add(myLocationOverlay);
            mapView.setOnTouchListener(new MapOnTouchListener());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LocationHelper.isGpsAvailable(GoogleMapActivity.this)) {
            final Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setSpeedRequired(false);
            final String locationProviderType = locationManager.getBestProvider(criteria, true);
            final LocationProvider locationProvider = locationManager.getProvider(locationProviderType);
            if (locationProvider != null) {
                myLocationBtn.setEnabled(true);
                if (!isInRouteMode) {
                    locationManager.requestLocationUpdates(locationProvider.getName(), LocationHelper.MIN_UPDATE_TIME, LocationHelper.MIN_DISTANCE,
                            locationTrackingListener);
                }
            }
        } else {
            myLocationBtn.setEnabled(false);
            Toast.makeText(this, getString(R.string.err_gps_location_provider_is_not_available), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null && locationTrackingListener != null) {
            locationManager.removeUpdates(locationTrackingListener);
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        return isInRouteMode;
    }

    private class RouteCalculationTask extends AsyncTask<GeoPoint, Void, Void> {
        private GeoPoint fromPoint;
        private GeoPoint toPoint;

        @Override
        protected Void doInBackground(GeoPoint[] points) {
            fromPoint = points[0];
            toPoint = points[1];
            final String url = RoadHelper.getUrl(fromPoint.getLatitudeE6() / LocationHelper.MILLION,
                    fromPoint.getLongitudeE6() / LocationHelper.MILLION,
                    toPoint.getLatitudeE6() / LocationHelper.MILLION,
                    toPoint.getLongitudeE6() / LocationHelper.MILLION, GoogleMapActivity.this);
            final InputStream inputStream = getConnection(url);
            road = RoadHelper.getRoute(readInputStream(inputStream));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final TextView routeInfo = (TextView) findViewById(R.id.txt_route_info);
            routeInfo.setText(road.description);
            routeInfo.setTextColor(Color.BLACK);
            final RouteOverlay routeOverlay = new RouteOverlay(road, mapView);
            final List<Overlay> listOfOverlays = mapView.getOverlays();
            listOfOverlays.clear();
            listOfOverlays.add(routeOverlay);
            mapView.invalidate();
            mapController.animateTo(road.route.get(0));
            acceptBtn.setEnabled(true);
        }

        private String readInputStream(InputStream inputStream) {
            BufferedReader buf = null;
            try {
                buf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                Log.e(LOGTAG, e.getMessage());
            }
            if (buf == null) return "";

            final StringBuilder sb = new StringBuilder();
            while (true) {
                String s = null;
                try {
                    s = buf.readLine();
                } catch (IOException e) {
                    Log.e(LOGTAG, e.getMessage());
                }
                if (s == null || s.length() == 0) {
                    break;
                }
                sb.append(s);
            }
            return sb.toString();
        }

        /**
         * Get connection with google service
         *
         * @param url url for route request
         * @return result xml in KML format as stream
         */
        private InputStream getConnection(String url) {
            InputStream is = null;
            try {
                final URLConnection conn = new URL(url).openConnection();
                is = conn.getInputStream();
            } catch (MalformedURLException e) {
                Log.e(CLASSTAG, e.getMessage());
            } catch (IOException e) {
                Log.e(CLASSTAG, e.getMessage());
            }
            return is;
        }
    }

    /**
     * OnTouchListener for MapView <br/>
     * Adds overlay items to {@link GoogleMapActivity#addressItemizedOverlay}
     */
    private class MapOnTouchListener implements View.OnTouchListener {

        @SuppressWarnings("NumericCastThatLosesPrecision")
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
        @SuppressWarnings("NumericCastThatLosesPrecision")
        @Override
        public void onClick(View view) {
            if (isInRouteMode) { // if rote was displayed
                try {
                    //determine current city and country
                    final Address address = locationHelper.getAddressByCoordinates(road.route.get(0).getLatitudeE6() / LocationHelper.MILLION,
                            road.route.get(0).getLongitudeE6() / LocationHelper.MILLION, GoogleMapActivity.this);
                    final SharedPreferences prefs = getSharedPreferences(StartActivity.PREFS_NAME, Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = prefs.edit();
                    //put city and country in SharedPreferences
                    editor.putString(StartActivity.CITY_KEY, address.getAddressLine(1));
                    editor.putString(StartActivity.COUNTRY_KEY, address.getAddressLine(3));
                    editor.putFloat(ROUTE_LENGTH_KEY, (float) road.length);
                    editor.commit();
                    //start TaxiServiceListActivity
                    final Intent intent = new Intent(GoogleMapActivity.this, TaxiServicesListActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    Log.e(LocationHelper.LOGTAG, CLASSTAG + " " + e.getMessage(), e);
                    Toast.makeText(GoogleMapActivity.this, getString(R.string.err_geocoder_not_available),
                            Toast.LENGTH_LONG).show();
                }
            } else {  //activity was started to select point in map
                final Intent resultIntent = new Intent();
                int resultCode = RESULT_OK;
                String address = "";
                try {
                    address = locationHelper.getAddressStringByGeoPoint(addressItemizedOverlay.getItem(0).getPoint(), GoogleMapActivity.this);
                } catch (IOException e) {
                    Log.e(CLASSTAG, e.getMessage(), e);
                    resultCode = RESULT_CANCELED;
                }
                resultIntent.putExtra("address", address);
                setResult(resultCode, resultIntent);
                finish();
            }
        }
    }

    /**
     * LocationListener to track location changes
     */
    private class LocationTrackingListener implements LocationListener {
        @SuppressWarnings("NumericCastThatLosesPrecision")
        @Override
        public void onLocationChanged(final Location loc) {
            try {
                final int lat = (int) (loc.getLatitude() * LocationHelper.MILLION);
                final int lon = (int) (loc.getLongitude() * LocationHelper.MILLION);
                final GeoPoint geoPoint = new GeoPoint(lat, lon);
                mapController.animateTo(geoPoint);
                mapController.setCenter(geoPoint);
            } finally {
                locationManager.removeUpdates(this);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }

        @Override
        public void onProviderDisabled(String s) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_about_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return ((TaxometrApplication) getApplication()).getMenu().optionsItemSelected(item, this);
    }

}