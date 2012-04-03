package ua.com.taxometr.activites;

//import java.lang.*;
import android.app.Activity;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import ua.com.taxometr.R;
import android.util.Log;
import android.content.ActivityNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Kardan
 * Date: 02.04.12
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class CallActivity extends Activity{
    private Button btnAccept;
    private Button btnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_view);
        final String phoneNumber = getIntent().getStringExtra("phoneNumber");

        btnAccept = (Button) findViewById(R.id.btn_accept_call);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(phoneNumber);
                final Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnCancel = (Button) findViewById(R.id.btn_cancel_call);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
    private void call(String phoneNumber)
    {
        try
        {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));
            startActivity(callIntent);
        }
        catch (ActivityNotFoundException activityException)
        {
            Log.e("Error", "Call failed", activityException);
        }

    }

}
