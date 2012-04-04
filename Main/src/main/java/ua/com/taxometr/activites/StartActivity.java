package ua.com.taxometr.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import ua.com.taxometr.R;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 22.03.12
 */
public class StartActivity extends Activity {
    private Button btnFrom;
    private Button btnTo;
    private Button btnCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);

        btnFrom = (Button) findViewById(R.id.btn_from);
        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        btnTo = (Button) findViewById(R.id.btn_to);
        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        btnCall = (Button) findViewById(R.id.btn_call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, CallActivity.class);
                String phoneNumber = "tel:"+"0000000000";
                intent.putExtra("phoneNumber",phoneNumber);
                startActivity(intent);

            }
        });
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

}