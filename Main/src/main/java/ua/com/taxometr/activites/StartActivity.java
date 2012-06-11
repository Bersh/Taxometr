package ua.com.taxometr.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView;
import de.akquinet.android.androlog.Log;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.LocationHelper;

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
    //private Button btnFrom;
    //private Button btnTo;
    //private Button btnCalcRoute;
    private Button btnLang;
    private Button btnCall;
    //private Button btnTaxiServicesList;
    private LocationManager locationManager;

    private static String fromAddress ;//= "Днепропетровск, пр. Карла Маркса 88";     //uncomment this for debug. If needed
    private static String toAddress ;//= "Днепропетровск, ул. Артема 3";

    final private ArrayList<HashMap<String, Object>> menuItems = new ArrayList<HashMap<String,Object>>();
    private static final String ITEMKEY = "menu_item";
    private static final String SUBITEMKEY = "menu_subitem";
    private static final String IMGKEY = "iconfromraw";

    private static boolean CalcEnabled = false;
    private static Intent intent = null;

    private final LocationListener locationTrackingListener = new LocationTrackingListener();

    /**
     * Shown when determining city
     */
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);

        ListView listView = (ListView)findViewById(R.id.menu_list);

        HashMap<String, Object> hm;

        hm = new HashMap<String, Object>();
        hm.put(ITEMKEY, getString(R.string.btn_from_text));
        hm.put(SUBITEMKEY, getString(R.string.adress_info));
        hm.put(IMGKEY, R.drawable.from_icon_s); //тут мы её добавляем для отображения

        menuItems.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(ITEMKEY, getString(R.string.btn_to_text));
        hm.put(SUBITEMKEY, getString(R.string.adress_info));
        hm.put(IMGKEY,  R.drawable.where_icon_s); //тут мы её добавляем для отображения

        menuItems.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(ITEMKEY, getString(R.string.btn_taxi_services_list));
        hm.put(SUBITEMKEY, getString(R.string.taxi_info));
        hm.put(IMGKEY,  R.drawable.taxi_icon_s); //тут мы её добавляем для отображения

        menuItems.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(ITEMKEY, getString(R.string.btn_calc_route_text));
        hm.put(SUBITEMKEY, getString(R.string.calc_info));
        hm.put(IMGKEY,  R.drawable.calc_icon_s); //тут мы её добавляем для отображения

        menuItems.add(hm);

        SimpleAdapter adapter = new SimpleAdapter(this,
                menuItems,
                R.layout.list_item, new String[]{
                ITEMKEY,         //верхний текст
                SUBITEMKEY,        //нижний теккт
                IMGKEY          //наша картинка
        }, new int[]{
                R.id.text1, //ссылка на объект отображающий текст
                R.id.text2, //ссылка на объект отображающий текст
                R.id.img}); //добавили ссылку в чем отображать картинки из list.xml

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        CalcEnabled = false;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

                switch (position)
                {


                    case 0:
                        intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                        startActivityForResult(intent, BTN_FROM_REQUEST_CODE);
                        break;
                    case 1:
                        intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                        startActivityForResult(intent, BTN_TO_REQUEST_CODE);
                        break;
                    case 2:
                        progressDialog = ProgressDialog.show(StartActivity.this, "", getString(R.string.dlg_progress_obtaining_location), true);
                        locationManager = (LocationManager) StartActivity.this.getSystemService(Context.LOCATION_SERVICE);
                        LocationHelper.requestLocationUpdates(StartActivity.this, locationManager, locationTrackingListener);
                        break;
                    case 3:
                        if (CalcEnabled)
                        {
                            intent = new Intent(StartActivity.this, GoogleMapActivity.class);
                            intent.putExtra("isRouteMode", true);
                            intent.putExtra("fromAddress", fromAddress);
                            intent.putExtra("toAddress", toAddress);
                            startActivity(intent);
                        }
                        break;
                    default:
                        break;
                }

                android.util.Log.d("TestTag", "itemClick: position = " + position + ", id = "
                        + id);
            }
        });

        btnCall = (Button) findViewById(R.id.btn_call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, CallActivity.class);
                final String phoneNumber = "tel:" + "0000000000";
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);

            }
        });

        btnLang = (Button) findViewById(R.id.btn_language);
        btnLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, LanguageListActivity.class);
                startActivity(intent);
            }
        });

        /*
        btnFrom = (Button) findViewById(R.id.btn_from);
        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                startActivityForResult(intent, BTN_FROM_REQUEST_CODE);
            }
        });

        btnTo = (Button) findViewById(R.id.btn_to);
        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, SelectAddressActivity.class);
                startActivityForResult(intent, BTN_TO_REQUEST_CODE);
            }
        });
        btnCall = (Button) findViewById(R.id.btn_call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(StartActivity.this, CallActivity.class);
                final String phoneNumber = "tel:" + "0000000000";
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);

            }
        });

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
        btnCalcRoute.setEnabled(false);             //comment this for debug. If needed

        btnTaxiServicesList = (Button) findViewById(R.id.btn_taxi_services_list);
        btnTaxiServicesList.setOnClickListener(new BtnTaxiServicesListener());


        */
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
                //btnFrom.setText(getString(R.string.btn_from_text) + "\n" + fromAddress);
                menuItems.get(0).put(SUBITEMKEY,fromAddress);
                break;
            case 2:
                toAddress = data.getStringExtra("address");

                //btnTo.setText(getString(R.string.btn_to_text) + "\n" + toAddress);
                menuItems.get(1).put(SUBITEMKEY,toAddress);
                break;
            default:
        }
        CalcEnabled = (!"".equals(fromAddress) && !"".equals(toAddress));
        //btnCalcRoute.setEnabled(!"".equals(fromAddress) && !"".equals(toAddress));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null && locationTrackingListener != null) {
            locationManager.removeUpdates(locationTrackingListener);
        }
    }

    /**
     * OnClickListener for btn_taxi_services_list
     */
    /*
    private class BtnTaxiServicesListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //comment this 3 strings for debug
            progressDialog = ProgressDialog.show(StartActivity.this, "", getString(R.string.dlg_progress_obtaining_location), true);
            locationManager = (LocationManager) StartActivity.this.getSystemService(Context.LOCATION_SERVICE);
            LocationHelper.requestLocationUpdates(StartActivity.this, locationManager, locationTrackingListener);

            //uncomment this for debug
            //final Intent intent = new Intent(StartActivity.this, TaxiServicesListActivity.class);
            //startActivity(intent);
        }
    } */

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
                        Toast.LENGTH_SHORT).show();
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
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onStart(){
        super.onStart();
        btnCall.setText(getString(R.string.btn_call_text));
        btnLang.setText(getString(R.string.btn_language));
        //btnTaxiServicesList.setText(getString(R.string.btn_taxi_services_list));
        menuItems.get(0).put(ITEMKEY,getString(R.string.btn_from_text));
        menuItems.get(1).put(ITEMKEY,getString(R.string.btn_to_text));
        menuItems.get(2).put(ITEMKEY,getString(R.string.btn_taxi_services_list));
        menuItems.get(2).put(SUBITEMKEY,getString(R.string.taxi_info));
        //btnCalcRoute.setText(getString(R.string.btn_calc_route_text));
        menuItems.get(3).put(ITEMKEY,getString(R.string.btn_calc_route_text));
        menuItems.get(3).put(SUBITEMKEY,getString(R.string.calc_info));
        if (fromAddress!=null) {
            menuItems.get(0).put(SUBITEMKEY,fromAddress);
        } else {

            menuItems.get(0).put(SUBITEMKEY,getString(R.string.adress_info));
        }

        if (toAddress!=null) {
            menuItems.get(1).put(SUBITEMKEY,toAddress);
        } else {

            menuItems.get(1).put(SUBITEMKEY,getString(R.string.adress_info));
        }

        /*if (fromAddress!=null) {
            //btnFrom.setText(getString(R.string.btn_from_text)+"\n"+fromAddress);
        } else {
            //btnFrom.setText(getString(R.string.btn_from_text));
            menuItems.get(0).put(ITEMKEY,getString(R.string.btn_from_text));
        }
        if (toAddress!=null) {
            btnTo.setText(getString(R.string.btn_to_text)+"\n"+toAddress);
        } else {
            //btnTo.setText(getString(R.string.btn_to_text));
            menuItems.get(1).put(ITEMKEY,getString(R.string.btn_to_text));
        }*/
    }

}