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

/**
 * Activity with taxi services from database
 *
 * @author Ilya Lisovyy <a href="mailto:ip.lisoviy@gmail.com">Ilya Lisovyy</a>
 * @since 02.06.12
 */

public class TaxiServicesListActivity extends ListActivity {

    //Check field for list listeners
    private boolean listener = true;

    private DBHelper dbHelper;

    private SQLiteDatabase db;

    //Locale name for choose database fields
    private String fullName;

    private static final String ISO_RUSSIAN = "ru";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create object for manegment DB versions
        dbHelper = new DBHelper(this);

        //connect to DB
        db = dbHelper.getWritableDatabase();

        //get language
        final String language = getApplicationContext().getResources().getConfiguration().locale.getLanguage();
        if (language.equals(ISO_RUSSIAN)) {
            fullName = "fullNameRU";
        } else {
            fullName = "fullNameEN";
        }

        final String LOG_TAG = "myLogs:";
        Log.d(LOG_TAG, language);

        listener = true;

        final SharedPreferences prefs = getSharedPreferences(StartActivity.PREFS_NAME, Context.MODE_PRIVATE);
        final String country = prefs.getString(StartActivity.CITY_KEY,"");//"Ukraine";     //uncomment this for debug
        final String town = prefs.getString(StartActivity.COUNTRY_KEY,"");//"Dnepropetrovsk";

        //taxiAgencies
        //data from queries
        final String orderBy = "a.initCost + a.costKM * (11 - a.KM_in_initCost)";
        final Cursor agencies = db.query("TaxiAgencies a, Towns b, Countries c", new String[]{"a.*"},
                "a.townId = b._id and b.countryId = c._id and c." + fullName + " = ? and b." + fullName + " = ?",
                new String[]{country, town}, null, null, orderBy);

        if (agencies.moveToFirst()){
            // create adapter for list view
            final ListAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1, agencies,new String[]{fullName},new int[]{android.R.id.text1});
            setListAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(),R.string.err_find_taxi_ser, Toast.LENGTH_SHORT).show();
        }
        // close DB connection
        dbHelper.close();
    }

    @Override
    protected void onListItemClick(final ListView listView, View v, final int position, long id) {
        if (listener){
            // connect to DB
            db = dbHelper.getWritableDatabase();
            //find id from selected taxi agency
            final String agencyName = ((Cursor)listView.getItemAtPosition(position)).getString(((Cursor)listView.getItemAtPosition(position)).getColumnIndex(fullName));

            //telephine numbers
            final Cursor numbers = db.query("TelephoneNumbers a, TaxiAgencies b", new String[] {"a.*"},
                    "a.taxiAgencyId = b._id and b."+ fullName + " = ?" ,
                    new String[] {agencyName}, null, null, null);

            if (numbers.moveToFirst()){
                // create adapter
                final ListAdapter adapterNum = new SimpleCursorAdapter(TaxiServicesListActivity.this,
                        android.R.layout.simple_list_item_1, numbers,
                        new String[]{"telephoneNumber"},new int[]{android.R.id.text1});
                setListAdapter(adapterNum);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final String phoneNumber = ((Cursor)listView.getItemAtPosition(i)).getString(((Cursor)listView.getItemAtPosition(i)).getColumnIndex("telephoneNumber"));
                        final Intent intent = new Intent(TaxiServicesListActivity.this, CallActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent);
                    }
                });

                listener = false;
            } else {
                Toast.makeText(getApplicationContext(),R.string.err_find_numbers, Toast.LENGTH_SHORT).show();
            }
            // close DB connection
            dbHelper.close();
        }
    }
}
