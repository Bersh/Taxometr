package ua.com.taxometr.activites;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import ua.com.taxometr.R;

/**
 * Activity with taxi services from database
 *
 * @author Ilya Lisovyy <a href="mailto:ip.lisoviy@gmail.com">Ilya Lisovyy</a>
 * @since 02.06.12
 */

public class DatabaseActivity extends Activity {

    final String LOG_TAG = "myLogs:";

    boolean listener = true;

    public static final String PREFS_NAME = "TaxometrPrefs";

    ListView lv;

    DBHelper dbHelper;

    SQLiteDatabase db;

    /**
     * key for city data
     */
    public static final String CITY_KEY = "CITY";

    /**
     * key for country data
     */
    public static final String COUNTRY_KEY = "COUNTRY";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_view);

        lv = (ListView) findViewById(R.id.lv);

        //create object for manegment DB versions
        dbHelper = new DBHelper(this);

        //connect to DB
        db = dbHelper.getWritableDatabase();

        //data from queries
        Cursor agencies ;

        //get language
        final String fullName;
        final String language = getApplicationContext().getResources().getConfiguration().locale.getLanguage();
        if (language.equals("ru")) fullName = "fullNameRU";
        else fullName = "fullNameEN";
        Log.d(LOG_TAG, language);

        listener = true;

        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String country = prefs.getString(CITY_KEY,"");//"Ukraine";     //uncomment this for debug
        String town = prefs.getString(COUNTRY_KEY,"");//"Dnepropetrovsk";

        //taxiAgencies
        agencies = db.query("TaxiAgencies a, Towns b, Countries c", new String[] {"a.*"},
                "a.townId = b._id and b.countryId = c._id and c." + fullName + " = ? and b."  + fullName + " = ?",
                new String[] {country, town}, null, null, "a.costKM");

        if (agencies.moveToFirst()){
            // create adapter for list view
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1, agencies,new String[]{fullName},new int[]{android.R.id.text1});
            lv.setAdapter(adapter);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.err_find_taxi_ser, Toast.LENGTH_SHORT);
            toast.show();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                if (listener){
                    // connect to DB
                    db = dbHelper.getWritableDatabase();
                    //find id from selected taxi agency
                    String agencyName = ((Cursor)lv.getItemAtPosition(position)).getString(((Cursor)lv.getItemAtPosition(position)).getColumnIndex(fullName));

                    //telephine numbers
                    Cursor numbers = db.query("TelephoneNumbers a, TaxiAgencies b", new String[] {"a.*"},
                            "a.taxiAgencyId = b._id and b."+ fullName + " = ?" ,
                            new String[] {agencyName}, null, null, null);

                    if (numbers.moveToFirst()){
                        // create adapter
                        SimpleCursorAdapter adapterNum = new SimpleCursorAdapter(DatabaseActivity.this,
                                android.R.layout.simple_list_item_1, numbers,
                                new String[]{"telephoneNumber"},new int[]{android.R.id.text1});
                        lv.setAdapter(adapterNum);
                        listener = false;
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                R.string.err_find_numbers, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    // close DB connection
                    dbHelper.close();
                }
            }
        });
        // close DB connection
        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "Taxi", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database Taxi---");
            // create tables with fields
            db.execSQL("create table Countries ("
                    + "_id integer primary key autoincrement,"
                    + "fullNameEN text,"
                    + "fullNameRU text"+");");
            db.execSQL("create table Towns ("
                    + "_id integer primary key autoincrement,"
                    + "fullNameEN text,"
                    + "fullNameRU text,"
                    + "countryId integer NOT NULL REFERENCES Countries ON DELETE CASCADE ON UPDATE CASCADE"+");");
            db.execSQL("create table TaxiAgencies ("
                    + "_id integer primary key autoincrement,"
                    + "fullNameEN text,"
                    + "fullNameRU text,"
                    + "costKM integer,"     //countries money
                    + "minCost integer,"    //countries money
                    + "townId integer NOT NULL REFERENCES Towns ON DELETE CASCADE ON UPDATE CASCADE" +");");
            db.execSQL("create table TelephoneNumbers ("
                    + "_id integer primary key autoincrement,"
                    + "telephoneNumber text,"
                    + "taxiAgencyId integer NOT NULL REFERENCES TaxiAgencies ON DELETE CASCADE ON UPDATE CASCADE"+");");

            // object for data
            ContentValues cv = new ContentValues();
            //inserts
            Log.d(LOG_TAG, "--- Inserts: ---");
            //Countries_________________________________________________________________________________________________
            cv.put("fullNameEN", "Ukraine");
            cv.put("fullNameRU", "Украина");
            long rowID = db.insert("Countries", null, cv);
            Log.d(LOG_TAG, "Countries : row inserted, ID = " + rowID);
            cv.clear();
            cv.put("fullNameEN", "Russian Federation");
            cv.put("fullNameRU", "Российская Федерация");
            rowID = db.insert("Countries", null, cv);
            Log.d(LOG_TAG, "Countries : row inserted, ID = " + rowID);
            cv.clear();
            cv.put("fullNameEN", "Belarus");
            cv.put("fullNameRU", "Беларусь");
            rowID = db.insert("Countries", null, cv);
            Log.d(LOG_TAG, "Countries : row inserted, ID = " + rowID);
            cv.clear();
            //Towns_____________________________________________________________________________________________________
            cv.put("fullNameEN", "Dnepropetrovsk");
            cv.put("fullNameRU", "Днепропетровск");
            cv.put("countryId", 1);
            rowID = db.insert("Towns", null, cv);
            Log.d(LOG_TAG, "Towns : row inserted, ID = " + rowID);
            cv.clear();
            cv.put("fullNameEN", "Kiev");
            cv.put("fullNameRU", "Киев");
            cv.put("countryId", 1);
            rowID = db.insert("Towns", null, cv);
            Log.d(LOG_TAG, "Towns : row inserted, ID = " + rowID);
            cv.clear();
            cv.put("fullNameEN", "Lviv");
            cv.put("fullNameRU", "Львов");
            cv.put("countryId", 1);
            rowID = db.insert("Towns", null, cv);
            Log.d(LOG_TAG, "Towns : row inserted, ID = " + rowID);
            cv.clear();
            //TaxiAgencies______________________________________________________________________________________________
            cv.put("fullNameEN", "Allo");
            cv.put("fullNameRU", "Алло");
            cv.put("costKM", 3);
            cv.put("minCost", 25);
            cv.put("townId", 1);
            rowID = db.insert("TaxiAgencies", null, cv);
            Log.d(LOG_TAG, "TaxiAgencies : row inserted, ID = " + rowID);
            cv.clear();
            cv.put("fullNameEN", "Dnepr - Taxi");
            cv.put("fullNameRU", "Днепр - Такси");
            cv.put("costKM", 2);
            cv.put("minCost", 20);
            cv.put("townId", 1);
            rowID = db.insert("TaxiAgencies", null, cv);
            Log.d(LOG_TAG, "TaxiAgencies : row inserted, ID = " + rowID);
            cv.clear();
            cv.put("fullNameEN", "Eurotaxi");
            cv.put("fullNameRU", "Евротакси");
            cv.put("costKM", 1);
            cv.put("minCost", 20);
            cv.put("townId", 1);
            rowID = db.insert("TaxiAgencies", null, cv);
            Log.d(LOG_TAG, "TaxiAgencies : row inserted, ID = " + rowID);
            cv.clear();
            //Telephones numbers________________________________________________________________________________________
            cv.put("telephoneNumber", "380676605452");
            cv.put("taxiAgencyId", 1);
            rowID = db.insert("TelephoneNumbers", null, cv);
            Log.d(LOG_TAG, "Telephonenumbers : row inserted, ID = " + rowID);
            cv.clear();
            cv.put("telephoneNumber", "380636855758");
            cv.put("taxiAgencyId", 1);
            rowID = db.insert("TelephoneNumbers", null, cv);
            Log.d(LOG_TAG, "Telephonenumbers : row inserted, ID = " + rowID);
            cv.clear();
            cv.put("telephoneNumber", "38056333111");
            cv.put("taxiAgencyId", 1);
            rowID = db.insert("TelephoneNumbers", null, cv);
            Log.d(LOG_TAG, "Telephonenumbers : row inserted, ID = " + rowID);
            cv.clear();
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
