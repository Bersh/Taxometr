package ua.com.taxometr.helpers;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 17.04.12
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
            createDBStructure(db);
            createSampleData(db);
            Log.e("Taxometr_DB", "Tables created!");
        } catch (SQLException e) {
            Log.e("Taxometr_DB", e.getMessage(), e);
            Log.e("Taxometr_DB", "Tasdgklsdjklgjsdklgjklsdjgklsdjklhgsdjgvkla");
            throw e;
        }
    }

    /**
     * Creates database structure
     * @param db current database
     * @throws android.database.SQLException if any query fails
     */
    private static void createDBStructure(SQLiteDatabase db) throws SQLException{
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
    }

    /**
     * Creates sample data
     * @param db current database
     * @throws android.database.SQLException if any query fails
     */
    private static void createSampleData(SQLiteDatabase db) throws SQLException{
        db.execSQL("insert into countries (_id, name_rus, name_en) values(0, \"Россия\", \"Russia\")");
        db.execSQL("insert into countries (_id, name_rus, name_en) values(1, \"Украина\", \"Ukraine\")");

        db.execSQL("insert into cities (_id, name_rus, name_en, country_id) values(0, \"Москва\", \"Moscow\", 0)");
        db.execSQL("insert into cities (_id, name_rus, name_en, country_id) values(1, \"Киев\", \"Kiev\", 1)");


        db.execSQL("insert into taxi_services (_id, name_rus, name_en, city_id) values(0, \"Мегаполис\", \"Megapolis\", 1)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_en, city_id) values(1, \"Каприз\", \"Kapriz\", 1)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_en, city_id) values(3, \"Хит такси\", \"Hit taxi\", 0)");

        db.execSQL("insert into phones (phone, taxi_id) values(\"+380939125432\", 0)");
        db.execSQL("insert into phones (phone, taxi_id) values(\"+380931111111\", 0)");
        db.execSQL("insert into phones (phone, taxi_id) values(\"+380504512365\", 0)");
        db.execSQL("insert into phones (phone, taxi_id) values(\"+380674512395\", 1)");
        db.execSQL("insert into phones (phone, taxi_id) values(\"+380502315276\", 2)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}