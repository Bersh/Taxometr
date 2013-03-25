package ua.com.taxometr.activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.inject.Inject;
import de.akquinet.android.androlog.Log;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.LocationHelper;
import ua.com.taxometr.helpers.LocationHelperInterface;
import ua.com.taxometr.helpers.RoadHelper;
import ua.com.taxometr.routes.Road;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static ua.com.taxometr.helpers.LocationHelperInterface.LOGTAG;

/**
 * New activity for map. Now with MapFragment
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 11.02.13
 */
@ContentView(R.layout.map_view)
public class MapActivity extends RoboFragmentActivity implements LocationListener, LocationSource {

    private static final String CLASSTAG = MapActivity.class.getSimpleName();
    private static final int MAP_ZOOM_LEVEL = 14;

    /**
     * key for route length data
     */
    public static final String ROUTE_LENGTH_KEY = "LENGTH";

    private GoogleMap map;
    private LocationSource.OnLocationChangedListener mListener;

    @Inject
    protected LocationManager locationManager;

    @InjectExtra(value = "isRouteMode", optional = true)
    private boolean isInRouteMode = false;  //activity started to display route?

    @InjectView(R.id.btn_accept)
    private Button acceptBtn;

    @InjectView(R.id.btn_my_location)
    private ImageButton myLocationBtn;

    @Inject
    protected LocationHelperInterface locationHelper;


    private Road road;
    private ProgressDialog progressDialog;
    private LatLng selectedPoint;
    private Location lastKnownLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if((map == null) || (map.getUiSettings() == null)) {
            Log.e(LocationHelper.LOGTAG, CLASSTAG + " " + R.string.err_map_not_available);
            finish();
            return;
        }
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);

        final Intent intent = getIntent();

        if (isInRouteMode) {
            final String fromAddress = intent.getStringExtra("fromAddress");
            final String toAddress = intent.getStringExtra("toAddress");
            final LatLng fromPoint;
            final LatLng toPoint;
            try {
                fromPoint = LocationHelper.getLatLngByAddressString(fromAddress, this);
                toPoint = LocationHelper.getLatLngByAddressString(toAddress, this);
            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.err_geocoder_not_available),
                        Toast.LENGTH_LONG).show();
                android.util.Log.e(CLASSTAG, e.getMessage());
                return;
            }
            new RouteCalculationTask().execute(fromPoint, toPoint);
        } else {
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    selectedPoint = latLng;
                    map.clear();
                    map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.red_pin))
                            .position(latLng));
                    if (!acceptBtn.isEnabled()) {
                        acceptBtn.setEnabled(true);
                    }
                }
            });

        }

        setButtonsListeners();

        if (locationManager != null) {    //request location updates
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (gpsIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
            } else if (networkIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
            } else {
                //Show an error dialog that GPS is disabled
                Log.e(LocationHelper.LOGTAG, CLASSTAG + " " + R.string.err_gps_not_available);
                Toast.makeText(this, getString(R.string.err_gps_not_available),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            //Show some generic error dialog because something must have gone wrong with location manager
            Log.e(LocationHelper.LOGTAG, CLASSTAG + " " + R.string.err_gps_not_available);
            Toast.makeText(this, getString(R.string.err_gps_not_available),
                    Toast.LENGTH_LONG).show();
        }
        setUpMapIfNeeded();
    }

    /**
     * Listeners for additional buttons sets here
     */
    private void setButtonsListeners() {
        final ImageButton zoomInBtn = (ImageButton) findViewById(R.id.btn_zoom_in);
        final ImageButton zoomOutBtn = (ImageButton) findViewById(R.id.btn_zoom_out);

        zoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        acceptBtn.setOnClickListener(new AcceptBtnOnClickListener());

        myLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Location myLocation = map.getMyLocation();
                if (myLocation != null) {
                    LatLng position = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLng(position));
                } else {
                    progressDialog = ProgressDialog.show(MapActivity.this, "", getString(R.string.dlg_progress_obtaining_location), true);
                }
            }
        });
    }

    @Override
    public void onPause() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();

        if (locationManager != null) {
            map.setMyLocationEnabled(true);
        }
    }


    private void setUpMapIfNeeded() {
        if (map != null) {
            map.setMyLocationEnabled(true);

            //Register the LocationSource
            map.setLocationSource(this);
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (mListener != null) {

                mListener.onLocationChanged(location);

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (lastKnownLocation == null) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL));
                } else {
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                lastKnownLocation = location;
            }
        } finally {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    /**
     * OnClickListener for Accept button
     *
     * @see android.view.View.OnClickListener
     */
    private class AcceptBtnOnClickListener implements View.OnClickListener {
        @SuppressWarnings("NumericCastThatLosesPrecision")
        @Override
        public void onClick(View view) {
            if (isInRouteMode) { // if rote was displayed
                try {
                    //determine current city and country
                    final Address address = locationHelper.getAddressByCoordinates(road.route.get(0).latitude,
                            road.route.get(0).longitude, MapActivity.this);
                    final SharedPreferences prefs = getSharedPreferences(StartActivity.PREFS_NAME, Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = prefs.edit();
                    //put city and country in SharedPreferences
                    editor.putString(StartActivity.CITY_KEY, address.getAddressLine(1));
                    editor.putString(StartActivity.COUNTRY_KEY, address.getAddressLine(3));
                    editor.putFloat(ROUTE_LENGTH_KEY, (float) road.length);
                    editor.commit();
                    //start TaxiServiceListActivity
                    final Intent intent = new Intent(MapActivity.this, TaxiServicesListActivity.class);
                    startActivity(intent);
                } catch (IOException e) {
                    android.util.Log.e(LocationHelper.LOGTAG, CLASSTAG + " " + e.getMessage(), e);
                    Toast.makeText(MapActivity.this, getString(R.string.err_geocoder_not_available),
                            Toast.LENGTH_LONG).show();
                }
            } else {  //activity was started to select point in map
                final Intent resultIntent = new Intent();
                int resultCode = RESULT_OK;
                String address = "";
                try {
                    address = locationHelper.getAddressStringByLatLng(selectedPoint, MapActivity.this);
                } catch (IOException e) {
                    android.util.Log.e(CLASSTAG, e.getMessage(), e);
                    resultCode = RESULT_CANCELED;
                }
                resultIntent.putExtra("address", address);
                setResult(resultCode, resultIntent);
                finish();
            }
        }
    }

    /**
     * This task will get route from Google Directions and draw it on map
     */
    private class RouteCalculationTask extends AsyncTask<LatLng, Void, Void> {
        private LatLng fromPoint;
        private LatLng toPoint;

        /**
         * Get route from Google Directions
         *
         * @param points point from & point to
         * @return void
         */
        @Override
        protected Void doInBackground(LatLng[] points) {
            fromPoint = points[0];
            toPoint = points[1];
            final String url = RoadHelper.getUrl(fromPoint.latitude, fromPoint.longitude,
                    toPoint.latitude, toPoint.longitude, MapActivity.this);
            final InputStream inputStream = getConnection(url);
            road = RoadHelper.getRoute(readInputStream(inputStream));
            return null;
        }

        /**
         * Draw route in map
         *
         * @param result void
         */
        @Override
        protected void onPostExecute(Void result) {
            final TextView routeInfo = (TextView) findViewById(R.id.txt_route_info);
            map.clear();
            routeInfo.setText(road.description);
            routeInfo.setTextColor(Color.BLACK);

            Polyline polyline = map.addPolyline(new PolylineOptions().addAll(road.route));
            polyline.setColor(Color.GREEN);
            polyline.setWidth(3);
            map.addMarker(new MarkerOptions().position(road.route.get(0))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_marker))
                    .anchor(0.5f, 1));
            map.addMarker(new MarkerOptions().position(road.route.get(road.route.size() - 1))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_marker))
                    .anchor(0.6f, 1));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(road.route.get(0), MAP_ZOOM_LEVEL));
            acceptBtn.setEnabled(true);
        }

        private String readInputStream(InputStream inputStream) {
            BufferedReader buf = null;
            try {
                buf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                android.util.Log.e(LOGTAG, e.getMessage());
            }
            if (buf == null) return "";

            final StringBuilder sb = new StringBuilder();
            while (true) {
                String s = null;
                try {
                    s = buf.readLine();
                } catch (IOException e) {
                    android.util.Log.e(LOGTAG, e.getMessage());
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
                android.util.Log.e(CLASSTAG, e.getMessage());
            } catch (IOException e) {
                android.util.Log.e(CLASSTAG, e.getMessage());
            }
            return is;
        }
    }
}
