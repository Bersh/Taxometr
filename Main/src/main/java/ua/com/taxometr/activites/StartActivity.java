package ua.com.taxometr.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import de.akquinet.android.androlog.Log;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.LocationHelper;
import ua.com.taxometr.helpers.MenuHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 22.03.12
 */
public class StartActivity extends Activity {

    private static final int BTN_FROM_REQUEST_CODE = 1;
    private static final int BTN_TO_REQUEST_CODE = 2;
    private static final String CLASSTAG = StartActivity.class.getSimpleName();

    /**
     * name for shared preferences
     */
    public static final String PREFS_NAME = "TaxometrPrefs";

    /**
     * key for city data
     */
    public static final String CITY_KEY = "CITY";

    /**
     * key for country data
     */
    public static final String COUNTRY_KEY = "COUNTRY";

    private LocationManager locationManager;

    private static String fromAddress;// = "Днепропетровск, пр. Карла Маркса 88";     //uncomment this for debug. If needed
    private static String toAddress;// = "Днепропетровск, ул. Артема 3";

    final private ArrayList<HashMap<String, Object>> menuItems = new ArrayList<HashMap<String, Object>>();
    private static final String ITEMKEY = "menu_item";
    private static final String SUBITEMKEY = "menu_subitem";
    private static final String IMGKEY = "iconfromraw";

    private final LocationListener locationTrackingListener = new LocationTrackingListener();
    private Button btnCalcRoute;
    private ListView menuListView;

    /**
     * Shown when determining city
     */
    private ProgressDialog progressDialog;

    @SuppressWarnings("ReuseOfLocalVariable")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);

        menuListView = (ListView) findViewById(R.id.menu_list);

        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put(ITEMKEY, getString(R.string.btn_from_text));//add title for current row
        hm.put(SUBITEMKEY, getString(R.string.adress_info));//add text for current row
        hm.put(IMGKEY, R.drawable.from_icon); //add icon for current row

        menuItems.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(ITEMKEY, getString(R.string.btn_to_text));
        hm.put(SUBITEMKEY, getString(R.string.adress_info));
        hm.put(IMGKEY, R.drawable.where_icon);

        menuItems.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(ITEMKEY, getString(R.string.btn_taxi_services_list));
        hm.put(SUBITEMKEY, getString(R.string.taxi_info));
        hm.put(IMGKEY, R.drawable.taxi_icon);
        menuItems.add(hm);

        final ListAdapter adapter = new SimpleAdapter(this,
                menuItems,
                R.layout.list_item, new String[]{
                ITEMKEY,         //title
                SUBITEMKEY,        //text
                IMGKEY          //icon
        }, new int[]{
                R.id.text1, //object that displays title
                R.id.text2, //object that displays text
                R.id.img}); //object that displays item icon

        menuListView.setAdapter(adapter);
        menuListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        menuListView.setOnItemClickListener(new MainListOnItemClickListener());

        btnCalcRoute = (Button) findViewById(R.id.btn_calc_route);
        btnCalcRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, GoogleMapActivity.class);
                intent.putExtra("isRouteMode", true);
                intent.putExtra("fromAddress", fromAddress);
                intent.putExtra("toAddress", toAddress);
                startActivity(intent);

            }
        });

       btnCalcRoute.setEnabled(false);
    }

    @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1:
                fromAddress = data.getStringExtra("address");
                menuItems.get(0).put(SUBITEMKEY, fromAddress);
                menuListView.invalidateViews();
                break;
            case 2:
                toAddress = data.getStringExtra("address");
                menuItems.get(1).put(SUBITEMKEY, toAddress);
                menuListView.invalidateViews();
                break;
            default:
        }

        btnCalcRoute.setEnabled(!"".equals(fromAddress) && !"".equals(toAddress) /*&& Geocoder.isPresent()*/ && LocationHelper.isInternetPresent(this) && LocationHelper.isGpsAvailable(this));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null && locationTrackingListener != null) {
            locationManager.removeUpdates(locationTrackingListener);
        }
    }


    /**
     * LocationListener to track location changes
     */
    private class LocationTrackingListener implements LocationListener {
        @Override
        public void onLocationChanged(final Location loc) {
            try {
                final Address address = LocationHelper.getAddressByCoordinates(loc.getLatitude(), loc.getLongitude(), StartActivity.this);
                final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString(CITY_KEY, address.getAddressLine(1));
                editor.putString(COUNTRY_KEY, address.getAddressLine(3));
                editor.commit();
                final Intent intent = new Intent(StartActivity.this, TaxiServicesListActivity.class);
                startActivity(intent);
            } catch (IOException e) {
                Log.e(LocationHelper.LOGTAG, CLASSTAG + " " + e.getMessage(), e);
                Toast.makeText(StartActivity.this, getString(R.string.err_geocoder_not_available),
                        Toast.LENGTH_LONG).show();
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
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit))
                .setMessage(getString(R.string.confirmation_exit))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }).create().show();
    }

    @Override
    public void onStart() {
        super.onStart();
        menuItems.get(0).put(ITEMKEY, getString(R.string.btn_from_text));
        menuItems.get(1).put(ITEMKEY, getString(R.string.btn_to_text));
        menuItems.get(2).put(ITEMKEY, getString(R.string.btn_taxi_services_list));
        menuItems.get(2).put(SUBITEMKEY, getString(R.string.taxi_info));
        if (fromAddress != null) {
            menuItems.get(0).put(SUBITEMKEY, fromAddress);
        } else {
            menuItems.get(0).put(SUBITEMKEY, getString(R.string.adress_info));
        }

        if (toAddress != null) {
            menuItems.get(1).put(SUBITEMKEY, toAddress);
        } else {
            menuItems.get(1).put(SUBITEMKEY, getString(R.string.adress_info));
        }
    }

    /**
     * Listener for main menu
     *
     * @author Ilya Lisovyy <a href="mailto:ip.lisoviy@gmail.com">Ilya Lisovyy</a>
     * @since 14.06.12
     */
    private class MainListOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            switch (position) {
                case 0:
                    Intent intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                    startActivityForResult(intent, BTN_FROM_REQUEST_CODE);
                    break;
                case 1:
                    intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                    startActivityForResult(intent, BTN_TO_REQUEST_CODE);
                    break;
                case 2:
                    if (LocationHelper.isGpsAvailable(StartActivity.this)) {
                        progressDialog = ProgressDialog.show(StartActivity.this, "", getString(R.string.dlg_progress_obtaining_location), true);
                        LocationHelper.requestLocationUpdates(StartActivity.this, locationManager, locationTrackingListener);

                        new CountDownTimer(LocationHelper.GPS_TIMEOUT, LocationHelper.GPS_TIMEOUT) {
                            @Override
                            public void onTick(long l) {
                            }

                            @Override
                            public void onFinish() {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();

                                    if (locationManager != null && locationTrackingListener != null) {
                                        locationManager.removeUpdates(locationTrackingListener);
                                    }
                                    startCitiesActivity();
                                }
                            }
                        }.start();
                    } else {
                        startCitiesActivity();
                    }
                    break;
                default:
                    break;
            }

            android.util.Log.d("TestTag", "itemClick: position = " + position + ", id = "
                    + id);
        }

        /**
         * Just starts {@link CitiesActivity}
         */
        private void startCitiesActivity() {
            final Intent intent = new Intent(StartActivity.this, CitiesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuHelper menu = new MenuHelper();
        return menu.optionsItemSelected(item, this);
    }

}