package com.taxometr.activites;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.taxometr.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.taxometr.helpers.LocationHelper;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 22.03.12
 */
public class SelectAdressActivity extends Activity {

    private EditText address;

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
            final Intent intent = new Intent(SelectAdressActivity.this, GoogleMapActivity.class);
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
            LocationManager locationManager = (LocationManager) SelectAdressActivity.this.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) {
                Toast.makeText(SelectAdressActivity.this, "Cannot determine your location,"
                        + " the GPS location provider is not available.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setSpeedRequired(false);
            String locationProviderType = locationManager.getBestProvider(criteria, true);
            GeoPoint lastKnownPoint = LocationHelper.getLastKnownPoint(locationManager, locationProviderType);
            address.setText(LocationHelper.getAddressByCoordinates(lastKnownPoint.getLatitudeE6() / LocationHelper.MILLION,
                    lastKnownPoint.getLongitudeE6() / LocationHelper.MILLION,
                    SelectAdressActivity.this));
        }
    }
}