package com.taxometr;

import com.google.android.maps.MapActivity;
import android.os.Bundle;

/**
 * @author ibershadskiy <a href="mailto:Ilya.Bershadskiy@exigenservices.com">Ilya Bershadskiy</a>
 * @since 15.03.12
 */
public class MainActivity extends MapActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}