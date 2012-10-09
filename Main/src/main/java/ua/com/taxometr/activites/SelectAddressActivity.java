package ua.com.taxometr.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.LocationHelper;
import ua.com.taxometr.helpers.MenuHelper;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 22.03.12
 */
public class SelectAddressActivity extends Activity {
    private static final String CLASSTAG = SelectAddressActivity.class.getSimpleName();
    private static final int MAP_ACTIVITY_REQUEST_CODE = 1;
    private EditText address;
    private final LocationListener locationTrackingListener = new LocationTrackingListener();
    private LocationManager locationManager;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_address_view);

        if (TextUtils.isEmpty(Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED))) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.dlg_enable_gps_text))
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(Settings. ACTION_SECURITY_SETTINGS);
                            startActivity(intent);
                        }
                    }).create().show();
        }

        if (isMobileDataEnabled()) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.dlg_enable_network_data_text))
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(intent);
                        }
                    }).create().show();
        }

        final Button mapPointBtn = (Button) findViewById(R.id.btn_map_point);
        final Button myLocationBtn = (Button) findViewById(R.id.btn_my_location);
        if(LocationHelper.isInternetPresent(this) && LocationHelper.isGpsAvailable(this)) {
            final View.OnClickListener mapPointBtnListener = new MapPointBtnListener();
            mapPointBtn.setOnClickListener(mapPointBtnListener);
            final View.OnClickListener myLocationBtnListener = new MyLocationBtnListener();
            myLocationBtn.setOnClickListener(myLocationBtnListener);
        } else {
            mapPointBtn.setEnabled(false);
            myLocationBtn.setEnabled(false);
        }

        final Button acceptBtn = (Button) findViewById(R.id.btn_accept_address);
        final View.OnClickListener acceptBtnListener = new AcceptBtnListener();
        acceptBtn.setOnClickListener(acceptBtnListener);

        address = (EditText) findViewById(R.id.address);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == RESULT_OK) && (requestCode == MAP_ACTIVITY_REQUEST_CODE)) {
            address.setText(data.getExtras().get("address").toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null && locationTrackingListener != null) {
            locationManager.removeUpdates(locationTrackingListener);
        }
    }

    /**
     * OnClickListener for button map_point
     */
    private class MapPointBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(SelectAddressActivity.this, GoogleMapActivity.class);
            startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
        }
    }

    /**
     * OnClickListener for accept button
     */
    private class AcceptBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Intent intent = new Intent();
            intent.putExtra("address", address.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * OnClickListener for button btn_my_location
     */
    private class MyLocationBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            progressDialog = ProgressDialog.show(SelectAddressActivity.this, "", getString(R.string.dlg_progress_obtaining_location), true);
            locationManager = (LocationManager) SelectAddressActivity.this.getSystemService(Context.LOCATION_SERVICE);
            LocationHelper.requestLocationUpdates(SelectAddressActivity.this, locationManager, locationTrackingListener);
            new CountDownTimer(LocationHelper.GPS_TIMEOUT, LocationHelper.GPS_TIMEOUT) {
                @Override
                public void onTick(long l) {
                }

                public void onFinish() {
                    if (progressDialog != null) {
                        progressDialog.dismiss();

                        if (locationManager != null && locationTrackingListener != null) {
                            locationManager.removeUpdates(locationTrackingListener);
                        }
                        Toast.makeText(SelectAddressActivity.this, getString(R.string.err_gps_not_available),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }.start();
        }
    }

    /**
     * LocationListener to track location changes
     */
    private class LocationTrackingListener implements LocationListener {
        @Override
        public void onLocationChanged(final Location loc) {
            try {
                address.setText(LocationHelper.getAddressStringByCoordinates(loc.getLatitude(), loc.getLongitude(), SelectAddressActivity.this));
            } catch (IOException e) {
                Log.e(LocationHelper.LOGTAG, CLASSTAG + " " + e.getMessage(), e);
                Toast.makeText(SelectAddressActivity.this, getString(R.string.err_geocoder_not_available),
                        Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_about_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuHelper menu = new MenuHelper();
        return menu.optionsItemSelected(item, this);
    }

    /**
     * Checks is data transfer throw mobile network enabled
     * @return true if enabled or unconfirmed
     */
    public Boolean isMobileDataEnabled(){
        Object connectivityService = getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean)m.invoke(cm);
        } catch (Exception e) {
            return true;
        }
    }
}