package com.taxometr.activites;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.taxometr.R;
import com.taxometr.helpers.LocationHelper;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 22.03.12
 */
public class SelectAddressActivity extends Activity {
    private static final int MIN_UPDATE_TIME = 3000;
    private static final int MIN_DISTANCE = 1000;

    private EditText address;
    private final LocationListener locationListener = new LocationTrackingListener();
    private LocationManager locationManager;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_address_view);

        final Button mapPointBtn = (Button) findViewById(R.id.btn_map_point);
        final View.OnClickListener mapPointBtnListener = new MapPointBtnListener();
        mapPointBtn.setOnClickListener(mapPointBtnListener);

        final Button acceptBtn = (Button) findViewById(R.id.btn_accept_address);
        final View.OnClickListener acceptBtnListener = new AcceptBtnListener();
        acceptBtn.setOnClickListener(acceptBtnListener);

        address = (EditText) findViewById(R.id.address);
        final View.OnClickListener myLocationBtnListener = new MyLocationBtnListener();
        findViewById(R.id.btn_my_location).setOnClickListener(myLocationBtnListener);
    }

    /**
     * listener for button map_point
     */
    private class MapPointBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(SelectAddressActivity.this, GoogleMapActivity.class);
            startActivity(intent);
        }
    }

    /**
     * listener for accept button
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

    private class MyLocationBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            progressDialog = ProgressDialog.show(SelectAddressActivity.this, "", getString(R.string.dlg_progress), true);
            try{
                wait(5000);
            } catch (InterruptedException e) {

            }
            progressDialog.dismiss();
            locationManager = (LocationManager) SelectAddressActivity.this.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) {
                Toast.makeText(SelectAddressActivity.this, "Cannot determine your location,"
                        + " the GPS location provider is not available.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setSpeedRequired(false);
            String locationProviderType = locationManager.getBestProvider(criteria, true);
            final LocationProvider locationProvider = locationManager.getProvider(locationProviderType);
            if (locationProvider != null) {
                locationManager.requestLocationUpdates(locationProvider.getName(), MIN_UPDATE_TIME, MIN_DISTANCE,
                        SelectAddressActivity.this.locationListener);
            }
        }
    }

    /**
     * LocationListener to track location changes
     */
    private class LocationTrackingListener implements LocationListener {
        @Override
        public void onLocationChanged(final Location loc) {
            Address addr = LocationHelper.getAddressByCoordinates(loc.getLatitude(), loc.getLongitude(), SelectAddressActivity.this);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 3; i++) {
                String s = addr.getAddressLine(i);
                if (s != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(s);
                }
            }
            address.setText(sb.toString());
            locationManager.removeUpdates(this);
            progressDialog.dismiss();
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