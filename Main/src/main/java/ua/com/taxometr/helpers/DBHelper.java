package ua.com.taxometr.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class with Database Helper
 * @author Ilya Lisovyy <a href="mailto:ip.lisoviy@gmail.com">Ilya Lisovyy</a>
 * @since 04.06.12
 */
public class DBHelper extends SQLiteOpenHelper {
    /**
     * Constructor
     * @param context instance of {@link android.content.Context}
     */
    public DBHelper(Context context) {
        super(context, "Taxi", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String LOG_TAG = "myLogs:";
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
                + "initCost integer,"   //countries money
                + "KM_in_initCost,"      //countries money
                + "townId integer NOT NULL REFERENCES Towns ON DELETE CASCADE ON UPDATE CASCADE" +");");
        db.execSQL("create table TelephoneNumbers ("
                + "_id integer primary key autoincrement,"
                + "telephoneNumber text,"
                + "taxiAgencyId integer NOT NULL REFERENCES TaxiAgencies ON DELETE CASCADE ON UPDATE CASCADE"+");");

        // object for data
        final ContentValues cv = new ContentValues();
        //inserts
        Log.d(LOG_TAG, "--- Inserts: ---");
        //Countries_________________________________________________________________________________________________
        cv.put("fullNameEN", "Ukraine");
        cv.put("fullNameRU", "Украина");
        db.insert("Countries", null, cv);
        Log.d(LOG_TAG, "Countries : row inserted");
        cv.clear();
        cv.put("fullNameEN", "Russian Federation");
        cv.put("fullNameRU", "Российская Федерация");
        db.insert("Countries", null, cv);
        Log.d(LOG_TAG, "Countries : row inserted");
        cv.clear();
        cv.put("fullNameEN", "Belarus");
        cv.put("fullNameRU", "Беларусь");
        db.insert("Countries", null, cv);
        Log.d(LOG_TAG, "Countries : row inserted");
        cv.clear();
        //Towns_____________________________________________________________________________________________________
        cv.put("fullNameEN", "Dnepropetrovsk");
        cv.put("fullNameRU", "Днепропетровск");
        cv.put("countryId", 1);
        db.insert("Towns", null, cv);
        Log.d(LOG_TAG, "Towns : row inserted");
        cv.clear();
        cv.put("fullNameEN", "Kiev");
        cv.put("fullNameRU", "Киев");
        cv.put("countryId", 1);
        db.insert("Towns", null, cv);
        Log.d(LOG_TAG, "Towns : row inserted");
        cv.clear();
        cv.put("fullNameEN", "Lviv");
        cv.put("fullNameRU", "Львов");
        cv.put("countryId", 1);
        db.insert("Towns", null, cv);
        Log.d(LOG_TAG, "Towns : row inserted");
        cv.clear();
        //TaxiAgencies______________________________________________________________________________________________
        cv.put("fullNameEN", "Allo");
        cv.put("fullNameRU", "Алло");
        cv.put("costKM", 3);
        cv.put("initCost", 25);
        cv.put("KM_in_initCost",10);
        cv.put("townId", 1);
        db.insert("TaxiAgencies", null, cv);
        Log.d(LOG_TAG, "TaxiAgencies : row inserted");
        cv.clear();
        cv.put("fullNameEN", "Dnepr - Taxi");
        cv.put("fullNameRU", "Днепр - Такси");
        cv.put("costKM", 2);
        cv.put("initCost", 20);
        cv.put("KM_in_initCost",10);
        cv.put("townId", 1);
        db.insert("TaxiAgencies", null, cv);
        Log.d(LOG_TAG, "TaxiAgencies : row inserted");
        cv.clear();
        cv.put("fullNameEN", "Eurotaxi");
        cv.put("fullNameRU", "Евротакси");
        cv.put("costKM", 1);
        cv.put("initCost",20);
        cv.put("KM_in_initCost",10);
        cv.put("townId", 1);
        db.insert("TaxiAgencies", null, cv);
        Log.d(LOG_TAG, "TaxiAgencies : row inserted");
        cv.clear();
        //Telephones numbers________________________________________________________________________________________
        cv.put("telephoneNumber", "380676605452");
        cv.put("taxiAgencyId", 1);
        db.insert("TelephoneNumbers", null, cv);
        Log.d(LOG_TAG, "Telephonenumbers : row inserted");
        cv.clear();
        cv.put("telephoneNumber", "380636855758");
        cv.put("taxiAgencyId", 1);
        db.insert("TelephoneNumbers", null, cv);
        Log.d(LOG_TAG, "Telephonenumbers : row inserted");
        cv.clear();
        cv.put("telephoneNumber", "38056333111");
        cv.put("taxiAgencyId", 1);
        db.insert("TelephoneNumbers", null, cv);
        Log.d(LOG_TAG, "Telephonenumbers : row inserted");
        cv.clear();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
