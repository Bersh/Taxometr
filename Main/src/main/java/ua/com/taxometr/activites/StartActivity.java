package ua.com.taxometr.activites;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import de.akquinet.android.androlog.Log;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.LocationHelper;

import java.io.IOException;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 22.03.12
 */
public class StartActivity extends Activity {
    private static final int BTN_FROM_REQUEST_CODE = 1;
    private static final int BTN_TO_REQUEST_CODE = 2;
    private static final String CLASSTAG = StartActivity.class.getSimpleName();

    /**
     * name for shared preferences
     */
    public static final String PREFS_NAME = "TaxometrPrefs";

    /**
     * key for city data
     */
    public static final String CITY_KEY = "CITY";

    /**
     * key for country data
     */
    public static final String COUNTRY_KEY = "COUNTRY";
    private Button btnFrom;
    private Button btnTo;
    private LocationManager locationManager;

    /**
     * Shown when determining city
     */
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);

        btnFrom = (Button) findViewById(R.id.btn_from);
        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                startActivityForResult(intent, BTN_FROM_REQUEST_CODE);
            }
        });

        btnTo = (Button) findViewById(R.id.btn_to);
        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                startActivityForResult(intent, BTN_TO_REQUEST_CODE);
            }
        });
        final Button btnCall = (Button) findViewById(R.id.btn_call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, CallActivity.class);
                final String phoneNumber = "tel:" + "0000000000";
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);

            }
        });

        final Button btnCalcRoute = (Button) findViewById(R.id.btn_calc_route);
        btnCalcRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                GeoPoint geoPoint = LocationHelper.getGeoPointByAddressString(btnTo.getText().toString(), StartActivity.this);
                } catch (IOException e) {
                    Log.e(e.getMessage());
                }
/*
                final Intent intent = new Intent(StartActivity.this, GoogleMapActivity.class);
                intent.putExtra("isRouteMode", true);
                intent.putExtra("isRouteMode", true);
                startActivity(intent);
*/

            }
        });

        final Button btnTaxiServicesList = (Button) findViewById(R.id.btn_taxi_services_list);
        btnTaxiServicesList.setOnClickListener(new BtnTaxiServicesListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1:
                btnFrom.setText(getString(R.string.btn_from_text) + "\n" + data.getStringExtra("address"));
                break;
            case 2:
                btnTo.setText(getString(R.string.btn_to_text) + "\n" + data.getStringExtra("address"));
                break;
            default:
        }
    }

    /**
     * OnClickListener for btn_taxi_services_list
     */
    private class BtnTaxiServicesListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            progressDialog = ProgressDialog.show(StartActivity.this, "", getString(R.string.dlg_progress), true);
            locationManager = (LocationManager) StartActivity.this.getSystemService(Context.LOCATION_SERVICE);
            LocationHelper.requestLocationUpdates(StartActivity.this, locationManager, new LocationTrackingListener());
        }
    }

    /**
     * LocationListener to track location changes
     */
    private class LocationTrackingListener implements LocationListener {
        @Override
        public void onLocationChanged(final Location loc) {
            try {
                final Address address = LocationHelper.getAddressByCoordinates(loc.getLatitude(), loc.getLongitude(), StartActivity.this);
                final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString(CITY_KEY, address.getAddressLine(1));
                editor.putString(COUNTRY_KEY, address.getAddressLine(3));
                editor.commit();
                final Intent intent = new Intent(StartActivity.this, TaxiServicesActivity.class);
                startActivity(intent);
            } catch (IOException e) {
                Log.e(LocationHelper.LOGTAG, CLASSTAG + " " + e.getMessage(), e);
                Toast.makeText(StartActivity.this, getString(R.string.err_geocoder_not_available),
                        Toast.LENGTH_SHORT).show();
            } finally {
                locationManager.removeUpdates(this);
                progressDialog.dismiss();
            }
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