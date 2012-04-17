package ua.com.taxometr.helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.akquinet.android.androlog.Log;

/**
 * Helper for database operations
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 16.04.12
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * Database helper constructor
     *
     * @param context context
     */
    public DBHelper(Context context) {
        super(context, "TaxometrDB", null, 1);
    }

    /**
     * Fills database by sample data
     *
     * @param db database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //create database structure
            db.execSQL("create table countries ("
                    + "_id integer primary key autoincrement, "
                    + "name_rus text, "
                    + "name_en text);");

            db.execSQL("create table cities ("
                    + "_id integer primary key autoincrement, "
                    + "name_rus text, "
                    + "name_en text, "
                    + "country_id integer, "
                    + "foreign key(country_id) references countries(_id));");

            db.execSQL("create table taxi_services ("
                    + "_id integer primary key autoincrement, "
                    + "name_rus text, "
                    + "name_en text, "
                    + "city_id integer, "
                    + "foreign key(city_id) references cities(_id));");

            db.execSQL("create table phones ("
                    + "_id integer primary key autoincrement, "
                    + "phone text, "
                    + "taxi_id integer, "
                    + "foreign key(taxi_id) references taxi_services(_id));");

            //create sample data
            db.execSQL("insert into countries (name_rus, name_en) values(\"Россия\", \"Russia\")");
            db.execSQL("insert into countries (name_rus, name_en) values(\"Украина\", \"Ukraine\")");

            Log.e("Taxometr_DB", "Tables created!");
        } catch (SQLException e) {
            Log.e("Taxometr_DB", e.getMessage(), e);
            Log.e("Taxometr_DB", "Tasdgklsdjklgjsdklgjklsdjgklsdjklhgsdjgvkla");
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
