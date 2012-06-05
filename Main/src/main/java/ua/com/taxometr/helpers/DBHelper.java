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
        super(context, "Taxometr", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String LOG_TAG = LocationHelper.LOGTAG;
        Log.d(LOG_TAG, "--- onCreate database Taxi---");
        // create tables with fields
        db.execSQL("create table countries ("
                + "_id integer primary key autoincrement,"
                + "name_rus text NOT NULL,"
                + "name_ua text NOT NULL,"
                + "name_en text NOT NULL"+");");
        db.execSQL("create table cities ("
                + "_id integer primary key autoincrement,"
                + "name_rus text NOT NULL,"
                + "name_ua text NOT NULL,"
                + "name_en text NOT NULL,"
                + "country_id integer NOT NULL REFERENCES countries ON DELETE CASCADE ON UPDATE CASCADE"+");");
        db.execSQL("create table taxi_services ("
                + "_id integer primary key autoincrement,"
                + "name_rus text NOT NULL,"
                + "name_ua text NOT NULL,"
                + "name_en text NOT NULL,"
                + "init_price integer NOT NULL,"            //countries money
                + "price_per_km integer NOT NULL,"          //countries money
                + "price_per_km_country integer,"           //countries money
                + "km_in_init_price integer NOT NULL,"      //countries money
                + "city_id integer NOT NULL REFERENCES cities ON DELETE CASCADE ON UPDATE CASCADE" +");");
        db.execSQL("create table phones ("
                + "_id integer primary key autoincrement,"
                + "phone text NOT NULL,"
                + "taxi_id integer NOT NULL REFERENCES taxi_services ON DELETE CASCADE ON UPDATE CASCADE"+");");

        // object for data
        final ContentValues cv = new ContentValues();
        //inserts
        Log.d(LOG_TAG, "--- Inserts: ---");
        //countries_________________________________________________________________________________________________
        cv.put("name_rus", "Украина");
        cv.put("name_ua","Україна");
        cv.put("name_en", "Ukraine");
        db.insert("countries", null, cv);
        Log.d(LOG_TAG, "countries : row inserted");
        cv.clear();
        cv.put("name_rus", "Российская Федерация");
        cv.put("name_ua","Російська Федерація");
        cv.put("name_en", "Russian Federation");
        db.insert("countries", null, cv);
        Log.d(LOG_TAG, "countries : row inserted");
        cv.clear();
        cv.put("name_rus", "Беларусь");
        cv.put("name_ua","Білорусь");
        cv.put("name_en", "Belarus");
        db.insert("countries", null, cv);
        Log.d(LOG_TAG, "countries : row inserted");
        cv.clear();
        //cities_____________________________________________________________________________________________________
        cv.put("name_rus", "Днепропетровск");
        cv.put("name_ua","Дніпропетровськ");
        cv.put("name_en", "Dnepropetrovsk");
        cv.put("country_id", 1);
        db.insert("cities", null, cv);
        Log.d(LOG_TAG, "cities : row inserted");
        cv.clear();
        cv.put("name_rus", "Киев");
        cv.put("name_ua","Київ");
        cv.put("name_en", "Kiev");
        cv.put("country_id", 1);
        db.insert("cities", null, cv);
        Log.d(LOG_TAG, "cities : row inserted");
        cv.clear();
        cv.put("name_rus", "Львов");
        cv.put("name_ua", "Львів");
        cv.put("name_en", "Lviv");
        cv.put("country_id", 1);
        db.insert("cities", null, cv);
        Log.d(LOG_TAG, "cities : row inserted");
        cv.clear();
        //taxi_services______________________________________________________________________________________________
        cv.put("name_rus", "Алло");
        cv.put("name_ua","Алло");
        cv.put("name_en", "Allo");
        cv.put("init_price", 25);
        cv.put("price_per_km", 3);
        cv.put("price_per_km_country", 5);
        cv.put("km_in_init_price",10);
        cv.put("city_id", 1);
        db.insert("taxi_services", null, cv);
        Log.d(LOG_TAG, "taxi_services : row inserted");
        cv.clear();
        cv.put("name_rus", "Днепр - Такси");
        cv.put("name_ua","Дніпро - Таксі");
        cv.put("name_en", "Dnepr - Taxi");
        cv.put("init_price", 20);
        cv.put("price_per_km", 2);
        cv.put("km_in_init_price",10);
        cv.put("city_id", 1);
        db.insert("taxi_services", null, cv);
        Log.d(LOG_TAG, "taxi_services : row inserted");
        cv.clear();
        cv.put("name_rus", "Евротакси");
        cv.put("name_ua","Євротаксі");
        cv.put("name_en", "Eurotaxi");
        cv.put("init_price", 20);
        cv.put("price_per_km", 1);
        cv.put("km_in_init_price",10);
        cv.put("city_id", 1);
        db.insert("taxi_services", null, cv);
        Log.d(LOG_TAG, "taxi_services : row inserted");
        cv.clear();
        //phones________________________________________________________________________________________
        cv.put("phone", "380676605452");
        cv.put("taxi_id", 1);
        db.insert("phones", null, cv);
        Log.d(LOG_TAG, "phones : row inserted");
        cv.clear();
        cv.put("phone", "380636855758");
        cv.put("taxi_id", 1);
        db.insert("phones", null, cv);
        Log.d(LOG_TAG, "phones : row inserted");
        cv.clear();
        cv.put("phone", "38056333111");
        cv.put("taxi_id", 1);
        db.insert("phones", null, cv);
        Log.d(LOG_TAG, "phones : row inserted");
        cv.clear();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
