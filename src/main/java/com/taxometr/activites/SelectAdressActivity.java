package com.taxometr.activites;

import com.taxometr.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
}