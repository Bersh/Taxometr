package ua.com.taxometr.activites;

//import java.lang.*;
import android.app.Activity;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ua.com.taxometr.R;
import android.util.Log;
import android.content.ActivityNotFoundException;
import ua.com.taxometr.helpers.MenuHelper;

/**
 * Date: 02.04.12
 * @author divgorbunov <a href="mailto:divgorbunov@gmail.com">Dmitriy Gorbunov</a>
 * @since 02.04.12
 */
public class CallActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_view);
        final String phoneNumber = getIntent().getStringExtra("phoneNumber");
        final TextView lblNumber = (TextView) findViewById(R.id.lbl_call_number);
        lblNumber.setText(phoneNumber);

        final Button btnAccept = (Button) findViewById(R.id.btn_accept_call);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call();
                try
                {
                    final Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                }
                catch (ActivityNotFoundException activityException)
                {
                    Log.e("Error", "Call failed", activityException);
                }

                final Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        final Button btnCancel = (Button) findViewById(R.id.btn_cancel_call);

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
        final MenuHelper menu = new MenuHelper();
        return menu.optionsItemSelected(item,this);
    }

}
