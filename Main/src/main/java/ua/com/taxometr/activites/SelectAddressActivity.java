package ua.com.taxometr.activites;

import java.io.IOException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.LocationHelper;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((resultCode == RESULT_OK) && (requestCode == MAP_ACTIVITY_REQUEST_CODE)) {
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
            progressDialog = ProgressDialog.show(SelectAddressActivity.this, "", getString(R.string.dlg_progress), true);
            locationManager = (LocationManager) SelectAddressActivity.this.getSystemService(Context.LOCATION_SERVICE);
            LocationHelper.requestLocationUpdates(SelectAddressActivity.this, locationManager, locationTrackingListener);
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