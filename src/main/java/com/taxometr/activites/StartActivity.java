package com.taxometr.activites;


import com.taxometr.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 22.03.12
 */
public class StartActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);

        //Button btnSelectAddress = (Button) findViewById(R.id.);
    }

    /**
     * class for buttons from/to
     */
    private class SelectAddressBtn implements View.OnClickListener {
        private SelectAddressBtn(Context context) {
        }

        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(StartActivity.this, SelectAdressActivity.class);
            startActivity(intent);
        }
    }
}