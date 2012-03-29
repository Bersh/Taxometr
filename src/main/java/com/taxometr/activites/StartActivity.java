package com.taxometr.activites;


import com.taxometr.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 22.03.12
 */
public class StartActivity extends Activity {
    private Button btnFrom;
    private Button btnTo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);

        btnFrom = (Button) findViewById(R.id.from_btn);
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