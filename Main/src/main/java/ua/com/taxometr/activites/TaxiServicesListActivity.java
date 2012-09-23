package ua.com.taxometr.activites;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.DBHelper;
import ua.com.taxometr.helpers.MenuHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Activity with taxi services from database
 *
 * @author Ilya Lisovyy <a href="mailto:ip.lisoviy@gmail.com">Ilya Lisovyy</a>
 * @since 02.06.12
 */

public class TaxiServicesListActivity extends ListActivity {

    private static final String TAXISERVICE = "nameTS";     //name for taxi service
    private static final String COST_LENGTH = "cost";       //cost of travel
    private static final String PRICEKM = "priceKM";        //price for 1 km
    private static final String INITPRICE = "initPrice";    //price for sit into taxi
    private static final String IMGKEY = "icon";

    //Check field for list listeners
    private boolean listener = true;

    private DBHelper dbHelper;

    private SQLiteDatabase db;

    //data for taxi agencies
    private Cursor agencies;

    //data for telephone numbers
    private Cursor numbers;

    //Locale name for choose database fields
    private String localName;

    private ListAdapter adapterNum;

    private static final String ISO_RUSSIAN = "ru";

    private static final String ISO_UKRAINIAN = "uk";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create object for manegment DB versions
        dbHelper = new DBHelper(this);

        //connect to DB
        db = dbHelper.getWritableDatabase();

        localName = getLocaleName(this);

        listener = true;

        final SharedPreferences prefs = getSharedPreferences(StartActivity.PREFS_NAME, Context.MODE_PRIVATE);
        final String country = prefs.getString(StartActivity.COUNTRY_KEY, "");//"Ukraine";     //uncomment this for debug
        final String town = prefs.getString(StartActivity.CITY_KEY, "");//"Dnepropetrovsk";
        final float length = prefs.getFloat(GoogleMapActivity.ROUTE_LENGTH_KEY, 0F);
        final Boolean isCalledFromStartActivity = prefs.getBoolean(StartActivity.IS_CALLED_FROM_START_ACTIVITY_KEY, false);

        //make query for taxi agencies
        final String orderBy;
        if (length <= 10) {
            orderBy = "a.init_price + a.price_per_km * (11 - a.km_in_init_price)";
        } else {
            orderBy = "(a.init_price + a.price_per_km * (" + length + " - a.km_in_init_price))";
        }

        agencies = db.query("taxi_services a, cities b, countries c",
                new String[]{"a._id", "a." + localName, "a.init_price", "a.price_per_km", "a.price_per_km_country", "a.km_in_init_price"},
                "a.city_id = b._id and b.country_id = c._id and c." + localName + " = ? and b." + localName + " = ?",
                new String[]{country, town}, null, null, orderBy);

        if (agencies.moveToFirst()) {
            final ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

            int count = 1;
            do {
                final AtomicReference<Object> img = new AtomicReference<Object>();
                if (count % 2 == 0) {
                    img.set(R.drawable.pass);
                } else if (count % 3 == 0) {
                    img.set(R.drawable.taxi);
                } else {
                    img.set(R.drawable.wait);
                }

                final StringBuilder priceKM = new StringBuilder(getString(R.string.price_for_km) + " " + agencies.getInt(agencies.getColumnIndex("price_per_km")) + " " + getString(R.string.currency) + " ");
                final float priceCountryKM = agencies.getFloat(agencies.getColumnIndex("price_per_km_country"));
                if (priceCountryKM != 0) {
                    priceKM.append("(").append(getString(R.string.price_for_km_country)).append(" ")
                            .append(priceCountryKM).append(")");
                }
                final HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put(TAXISERVICE, agencies.getString(agencies.getColumnIndex(localName)));
                if (length != 0) {
                    int cost = new Float(agencies.getFloat(agencies.getColumnIndex("price_per_km")) * length).intValue();
                    final Float initPrice = agencies.getFloat(agencies.getColumnIndex("init_price"));
                    if (cost < initPrice) {
                        cost = initPrice.intValue();
                    }
                    hm.put(COST_LENGTH, new StringBuilder(getString(R.string.price_length))
                            .append(" ").append(cost).append(" ").append(getString(R.string.currency)));
                }
                hm.put(PRICEKM, priceKM);
                hm.put(INITPRICE, getString(R.string.init_price) + " " + agencies.getFloat(agencies.getColumnIndex("init_price"))
                        + " " + getString(R.string.currency) + " (" + agencies.getInt(agencies.getColumnIndex("km_in_init_price")) + " " + getString(R.string.km_in_init_price) + ")");
                hm.put(IMGKEY, img.get());
                items.add(hm);
                if (count == 3) {
                    count = 0;
                }
                ++count;
            } while (agencies.moveToNext());

            // create adapter for list view
            final ListAdapter adapter;
            if (length != 0) {
                adapter = new SimpleAdapter(this,
                        items, R.layout.taxi_services_list_view_length,
                        new String[]{TAXISERVICE, COST_LENGTH, PRICEKM, INITPRICE, IMGKEY},
                        new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4, R.id.img});
            } else {
                adapter = new SimpleAdapter(this,
                        items, R.layout.taxi_services_list_view,
                        new String[]{TAXISERVICE, PRICEKM, INITPRICE, IMGKEY},
                        new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.img});
            }
            setListAdapter(adapter);
        } else {
/*            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            if (isCalledFromStartActivity) {
                intent = new Intent(this, CitiesActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), R.string.err_find_taxi_ser, Toast.LENGTH_LONG).show();
            }
            finish();*/
            Toast.makeText(getApplicationContext(), R.string.err_find_taxi_ser, Toast.LENGTH_LONG).show();
            final Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onListItemClick(final ListView listView, View v, final int position, long id) {

        if (listener) {
            //find id from selected taxi agency
            final String agencyName = (String) ((HashMap<String, Object>) listView.getItemAtPosition(position)).get(TAXISERVICE);

            //telephone numbers
            numbers = db.query("phones a, taxi_services b", new String[]{"a._id", "a.phone"},
                    "a.taxi_id = b._id and b." + localName + " = ?",
                    new String[]{agencyName}, null, null, null);

            if (numbers.moveToFirst()) {
                // create adapter
                adapterNum = new SimpleCursorAdapter(TaxiServicesListActivity.this,
                        android.R.layout.simple_list_item_1, numbers,
                        new String[]{"phone"}, new int[]{android.R.id.text1});
                setListAdapter(adapterNum);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final String phoneNumber = ((Cursor) listView.getItemAtPosition(i)).getString(((Cursor) listView.getItemAtPosition(i)).getColumnIndex("phone"));
                        createCallDialog(phoneNumber);
                    }
                });
                listener = false;
            } else {
                Toast.makeText(getApplicationContext(), R.string.err_find_numbers, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * get locale name for database operations
     *
     * @param context instance of android.content.Context
     * @return locale name in country language
     */
    public static String getLocaleName(Context context) {
        //get language and set names of cities,countries,taxi services
        final String language = context.getApplicationContext().getResources().getConfiguration().locale.getLanguage();
        if (language.equals(ISO_RUSSIAN)) {
            return "name_rus";
        } else if (language.equals(ISO_UKRAINIAN)) {
            return "name_ua";
        } else {
            return "name_en";
        }
    }

    @Override
    public void onBackPressed() {
        if (getListAdapter().equals(adapterNum)) {
            final Intent intent = new Intent(TaxiServicesListActivity.this, TaxiServicesListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            super.onBackPressed();
            final SharedPreferences prefs = getSharedPreferences(StartActivity.PREFS_NAME, Context.MODE_PRIVATE);
            final SharedPreferences.Editor propsEditor = prefs.edit();
            propsEditor.remove(StartActivity.COUNTRY_KEY);
            propsEditor.remove(StartActivity.CITY_KEY);
            propsEditor.remove(GoogleMapActivity.ROUTE_LENGTH_KEY);
            propsEditor.remove(StartActivity.IS_CALLED_FROM_START_ACTIVITY_KEY);
            propsEditor.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.dbHelper != null) {
            this.dbHelper.close();
            this.dbHelper = null;
        }
        if (this.db != null) {
            this.db.close();
            this.db = null;
        }
        if (this.agencies != null) {
            this.agencies.close();
            this.agencies = null;
        }
        if (this.numbers != null) {
            this.numbers.close();
            this.numbers = null;
        }
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
        return menu.optionsItemSelected(item, this);
    }

    /**
     * Creates and shows call dialog
     *
     * @param phoneNumber phone number
     */
    private void createCallDialog(final String phoneNumber) {
        final Dialog callDialog = new Dialog(this);
        callDialog.setContentView(R.layout.call_dialog);
        callDialog.setTitle(getString(R.string.app_name));
        callDialog.setCancelable(true);

        final TextView lblNumber = (TextView) callDialog.findViewById(R.id.call_number);
        lblNumber.setText(phoneNumber);

        final Button callButton = (Button) callDialog.findViewById(R.id.btn_call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    TaxiServicesListActivity.this.startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Error", "Call failed", activityException);
                } finally {
                    callDialog.cancel();
                }
            }
        });
        final Button cancelButton = (Button) callDialog.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDialog.cancel();
            }
        });
        callDialog.show();
    }

}
