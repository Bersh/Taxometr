package ua.com.taxometr.activites;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.DBHelper;
import ua.com.taxometr.helpers.LocationHelper;

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

    //adapters for ListView
    private ListAdapter adapter;

    private ListAdapter adapterNum;

    private static final String ISO_RUSSIAN = "ru";

    private static final String ISO_UKRAINIAN = "uk";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create object for manegment DB versions
        dbHelper = new DBHelper(this);

        //connect to DB
        db = dbHelper.getWritableDatabase();

        //get language and set names of cities,countries,taxi services
        final String language = getApplicationContext().getResources().getConfiguration().locale.getLanguage();
        if (language.equals(ISO_RUSSIAN)) {
            localName = "name_rus";
        } else if(language.equals(ISO_UKRAINIAN)){
            localName = "name_ua";
        } else {
            localName = "name_en";
        }

        final String LOG_TAG = LocationHelper.LOGTAG;
        Log.d(LOG_TAG, language);

        listener = true;

        final SharedPreferences prefs = getSharedPreferences(StartActivity.PREFS_NAME, Context.MODE_PRIVATE);
        final String country = prefs.getString(StartActivity.COUNTRY_KEY,"");//"Ukraine";     //uncomment this for debug
        final String town = prefs.getString(StartActivity.CITY_KEY,"");//"Dnepropetrovsk";
        final float length = prefs.getFloat(GoogleMapActivity.ROUTE_LENGTH_KEY,0F);

        //taxiAgencies
        //data from queries
        final String orderBy;
        if(length<=10){
            orderBy = "a.init_price + a.price_per_km * (11 - a.km_in_init_price)";
        }else{
            orderBy = "(a.init_price + a.price_per_km * (" + length + " - a.km_in_init_price))";
        }

        agencies = db.query("taxi_services a, cities b, countries c",
                new String[]{"a._id","a."+localName,"a.init_price","a.price_per_km","a.price_per_km_country","a.km_in_init_price"},
                "a.city_id = b._id and b.country_id = c._id and c." + localName + " = ? and b." + localName + " = ?",
                new String[]{country, town}, null, null, orderBy);

        if (agencies.moveToFirst()){
            final ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

            int count = 1;
            do{
                final AtomicReference<Object> img = new AtomicReference<Object>();
                if (count%2==0){
                    img.set(R.drawable.pass);
                } else if (count%3==0){
                    img.set(R.drawable.taxi);
                } else {
                    img.set(R.drawable.wait);
                }

                final StringBuilder priceKM = new StringBuilder(getString(R.string.price_for_km)+" "+agencies.getInt(agencies.getColumnIndex("price_per_km"))+" "+getString(R.string.currency)+" ");
                final int priceCountryKM = agencies.getInt(agencies.getColumnIndex("price_per_km_country"));
                if (priceCountryKM!=0){
                    priceKM.append("(").append(getString(R.string.price_for_km_country)).append(" ")
                            .append(priceCountryKM).append(" ").append(getString(R.string.currency)).append(")");
                }
                final HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put(TAXISERVICE, agencies.getString(agencies.getColumnIndex(localName)));
                if(length!=0){
                    int cost = new Float(agencies.getInt(agencies.getColumnIndex("price_per_km"))*length).intValue();
                    final int initPrice = agencies.getInt(agencies.getColumnIndex("init_price"));
                    if ( cost < initPrice) {
                        cost = initPrice;
                    }
                    hm.put(COST_LENGTH,new StringBuilder(getString(R.string.price_length))
                            .append(" ").append(cost).append(" ").append(getString(R.string.currency)));
                }
                hm.put(PRICEKM, priceKM);
                hm.put(INITPRICE,getString(R.string.init_price)+" "+agencies.getInt(agencies.getColumnIndex("init_price"))
                        +" "+getString(R.string.currency)+" ("+ agencies.getInt(agencies.getColumnIndex("km_in_init_price"))+" "+getString(R.string.km_in_init_price)+")");
                hm.put(IMGKEY, img.get());
                items.add(hm);
                ++count;
            }while (agencies.moveToNext());

            // create adapter for list view
            if(length!=0){
                adapter = new SimpleAdapter(this,
                        items, R.layout.taxi_services_list_view_length,
                        new String[]{ TAXISERVICE,COST_LENGTH,PRICEKM,INITPRICE,IMGKEY },
                        new int[]{R.id.text1,R.id.text2,R.id.text3,R.id.text4,R.id.img});
            }else{
                adapter = new SimpleAdapter(this,
                        items, R.layout.taxi_services_list_view,
                        new String[]{ TAXISERVICE,PRICEKM,INITPRICE,IMGKEY },
                        new int[]{R.id.text1,R.id.text2,R.id.text3,R.id.img});
            }
            setListAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(),R.string.err_find_taxi_ser, Toast.LENGTH_SHORT).show();
            final Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onListItemClick(final ListView listView, View v, final int position, long id) {

        if (listener){
            //find id from selected taxi agency
            final String agencyName = (String)((HashMap<String,Object>)listView.getItemAtPosition(position)).get(TAXISERVICE);

            //telephine numbers
            numbers = db.query("phones a, taxi_services b", new String[] {"a._id","a.phone"},
                    "a.taxi_id = b._id and b."+ localName + " = ?" ,
                    new String[] {agencyName}, null, null, null);

            if (numbers.moveToFirst()){
                // create adapter
                adapterNum = new SimpleCursorAdapter(TaxiServicesListActivity.this,
                        android.R.layout.simple_list_item_1, numbers,
                        new String[]{"phone"},new int[]{android.R.id.text1});
                setListAdapter(adapterNum);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final String phoneNumber = ((Cursor)listView.getItemAtPosition(i)).getString(((Cursor)listView.getItemAtPosition(i)).getColumnIndex("phone"));
                        final Intent intent = new Intent(TaxiServicesListActivity.this, CallActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent);
                    }
                });

                listener = false;
            } else {
                Toast.makeText(getApplicationContext(),R.string.err_find_numbers, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getListAdapter().equals(adapterNum)){
            setListAdapter(adapter);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (this.dbHelper != null){
            this.dbHelper.close();
            this.dbHelper = null;
        }
        if (this.db != null) {
            this.db.close();
            this.db = null;
        }
        if (this.agencies != null){
            this.agencies.close();
            this.agencies = null;
        }
        if (this.numbers != null){
            this.numbers.close();
            this.numbers = null;
        }

    }
}
