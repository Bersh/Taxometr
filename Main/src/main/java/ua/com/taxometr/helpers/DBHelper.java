package ua.com.taxometr.helpers;

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

    private static final String LOGTAG = "TaxometrDB";
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
            Log.e(LOGTAG, "Tables created!");
        } catch (SQLException e) {
            Log.e(LOGTAG, e.getMessage(), e);
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
                + "name_rus text NOT NULL, "
                + "name_en text NOT NULL);");

        db.execSQL("create table cities ("
                + "_id integer primary key autoincrement, "
                + "name_rus text NOT NULL, "
                + "name_en text NOT NULL, "
                + "country_id integer NOT NULL, "
                + "foreign key(country_id) references countries(_id));");

        db.execSQL("create table taxi_services ("
                + "_id integer primary key autoincrement, "
                + "name_rus text NOT NULL, "
                + "name_en text NOT NULL, "
                + "city_id integer NOT NULL, "
                + "init_price currency NOT NULL, "
                + "price_per_km currency NOT NULL, "
                + "foreign key(city_id) references cities(_id));");

        db.execSQL("create table phones ("
                + "_id integer primary key autoincrement, "
                + "phone text NOT NULL, "
                + "taxi_id integer NOT NULL, "
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
        db.execSQL("insert into cities (_id, name_rus, name_en, country_id) values(2, \"Днепропетровск\", \"Dnipropetrovs'k\", 1)");


        db.execSQL("insert into taxi_services (_id, name_rus, name_en, city_id, init_price, price_per_km) values(0, \"Мегаполис\", \"Megapolis\", 2, 20, 5)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_en, city_id, init_price, price_per_km) values(1, \"Каприз\", \"Kapriz\", 2, 25, 1)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_en, city_id, init_price, price_per_km) values(2, \"Киев Такси 1\", \"Kyev taxi 1\", 1, 10, 10)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_en, city_id, init_price, price_per_km) values(3, \"Киев Такси 2\", \"Kyev taxi 2\", 1, 20, 5)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_en, city_id, init_price, price_per_km) values(4, \"Хит такси\", \"Hit taxi\", 0, 20, 5)");

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