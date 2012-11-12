package ua.com.taxometr.activites;

//import java.lang.*;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import ua.com.taxometr.R;
import ua.com.taxometr.TaxometrApplication;

/**
 * Date: 02.04.12
 *
 * @author divgorbunov <a href="mailto:divgorbunov@gmail.com">Dmitriy Gorbunov</a>
 * @since 02.04.12
 */
@ContentView(R.layout.call_view)
public class CallActivity extends RoboActivity {
    @InjectView(R.id.lbl_call_number)
    private TextView lblNumber;

    @InjectView(R.id.btn_accept_call)
    private Button btnAccept;

    @InjectExtra("phoneNumber")
    private String phoneNumber;

    @InjectView(R.id.btn_cancel_call)
    private Button btnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lblNumber.setText(phoneNumber);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Error", "Call failed", activityException);
                }

                final Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

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
