package ua.com.taxometr.helpers;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Class with Database Helper
 *
 * @author Ilya Lisovyy <a href="mailto:ip.lisoviy@gmail.com">Ilya Lisovyy</a>
 * @since 04.06.12
 */
@Singleton
public class DBHelper extends SQLiteOpenHelper {
    /**
     * Constructor
     *
     * @param contextProvider instance of Provider<Context>
     */
    @Inject
    public DBHelper(Provider<Context> contextProvider) {
        super(contextProvider.get(), "Taxometr", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String LOG_TAG = LocationHelper.LOGTAG;
        Log.d(LOG_TAG, "--- onCreate database Taxi---");
        createDBStructure(db);
        createData(db);
    }

    /**
     * Creates db data
     * @param db current database
     * @throws android.database.SQLException if any query fails
     */
    private static void createDBStructure(SQLiteDatabase db) throws SQLException {
        db.execSQL("create table countries ("
                + "_id integer primary key autoincrement,"
                + "name_rus text NOT NULL,"
                + "name_ua text NOT NULL,"
                + "name_en text NOT NULL" + ");");
        db.execSQL("create table cities ("
                + "_id integer primary key autoincrement,"
                + "name_rus text NOT NULL,"
                + "name_ua text NOT NULL,"
                + "name_en text NOT NULL,"
                + "country_id integer NOT NULL REFERENCES countries ON DELETE CASCADE ON UPDATE CASCADE" + ");");
        db.execSQL("create table taxi_services ("
                + "_id integer primary key autoincrement,"
                + "name_rus text NOT NULL,"
                + "name_ua text NOT NULL,"
                + "name_en text NOT NULL,"
                + "init_price currency NOT NULL,"            //countries money
                + "price_per_km currency NOT NULL,"          //countries money
                + "price_per_km_country currency , "
                + "price_downtime currency NOT NULL,"
                + "km_in_init_price integer NOT NULL,"
                + "city_id integer NOT NULL REFERENCES cities ON DELETE CASCADE ON UPDATE CASCADE" + ");");
        db.execSQL("create table phones ("
                + "_id integer primary key autoincrement,"
                + "phone text NOT NULL,"
                + "taxi_id integer NOT NULL REFERENCES taxi_services ON DELETE CASCADE ON UPDATE CASCADE" + ");");
    }

    /**
     * Creates sample data
     *
     * @param db current database
     * @throws android.database.SQLException if any query fails
     */
    private static void createData(SQLiteDatabase db) throws SQLException {
        //Countries
        db.execSQL("insert into countries (_id, name_rus, name_ua, name_en) "
                + "values(0, \"Украина\", \"Україна\", \"Ukraine\")");

        //Cites
        db.execSQL("insert into cities (_id, name_rus, name_ua, name_en, country_id) "
                + "values(0, \"Киев\", \"Київ\", \"Kiev\", 0)");
        db.execSQL("insert into cities (_id, name_rus, name_ua, name_en, country_id) "
                + "values(1, \"Днепропетровск\", \"Дніпропетровськ\", \"Dnipropetrovs'k\", 0)");
		db.execSQL("insert into cities (_id, name_rus, name_ua, name_en, country_id) "
                + "values(2, \"Донецк\", \"Донецьк\", \"Donets'k\", 0)");		

        //Services
        db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(0, \"Мегаполис\", \"Мегаполіс\", \"Megapolis\", 1, 20, 5, 2, 0.5, 10)");
				//Dnepropetrovsk
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(1, \"Pilottaxi\", \"Pilottaxi\", \"Pilottaxi\", 1, 25, 2, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(2, \"Time taxi\", \"Time taxi\", \"Time taxi\", 1, 35, 3, 5, 0.9, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(3, \"Вояж такси\", \"Вояж таксі\", \"Voyage taxi\", 1, 20, 2.5, 2, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(4, \"Гранд такси\", \"Гранд таксі\", \"Grand taxi\", 1, 18, 1.8, 1.5, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(5, \"Дан такси\", \"Дан таксі\", \"Dan taxi\", 1, 25, 2.5, null, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(6, \"Днепр такси бизнес\", \"Дніпро таксі бізнес\", \"Dnepr taxi business\", 1, 30, 3, 2.5, 0.67, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(7, \"Днепр такси эконом\", \"Дніпро таксі економ\", \"Dnepr taxi economy\", 1, 25, 2, 2, 0.33, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(8, \"Днепр такси элит\", \"Дніпро таксі еліт\", \"Dnepr taxi elites\", 1, 50, 5, 5, 1, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(9, \"Лайф такси\", \"Лайф таксі\", \"Life taxi\", 1, 20, 2, 1.7, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(10, \"Любимое такси\", \"Любиме таксі\", \"Lyubimoe taxi\", 1, 20, 1.8, 2.8, 0.67, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(11, \"Наше такси комфорт\", \"Наше таксі комфорт\", \"Nashe taxi comfort\", 1, 30, 2.5, 4.4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(12, \"Наше такси эконом\", \"Наше таксі економ\", \"Nashe taxi economy\", 1, 25, 2, 3.6, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(13, \"Первый таксомоторный\", \"Перший таксомоторний\", \"The first taxomotorniy\", 1, 25, 2, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(14, \"Приват такси\", \"Приват таксі\", \"Privat taxi\", 1, 25, 1.8, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(15, \"Профи-такси\", \"Профі-таксі\", \"Profi-taxi\", 1, 25, 2.5, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(16, \"Радио-такси\", \"Радіо-таксі\", \"Radio-taxi\", 1, 30, 2.5, 4.8, 0.6, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(17, \"Славута-такси\", \"Славута-таксі\", \"Slavuta-taxi\", 1, 25, 2, 2.2, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(18, \"Справедливое такси\", \"Справедливе таксі\", \"Fair taxi\", 1, 24, 2.5, 3.5 , 0.6, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(19, \"Такси Welcom\", \"Таксі Welcom\", \"Taxi Welcom\", 1, 25, 2, 2.5, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(20, \"Такси Девяточка\", \"Таксі Дев'яточка\", \"Taxi Devyatochka\", 1, 20, 2, 3.6, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(21, \"Такси Каприз\", \"Таксі Каприз\", \"Taxi Kapriz\", 1, 20, 1.8, 2.8, 0.42, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(22, \"Такси Лига\", \"Таксі Ліга\", \"Taxi Liga\", 1, 22, 2.2, null, 0.6, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(23, \"Эталон-такси\", \"Еталон-таксі\", \"Etalon-Taxi\", 1, 15, 2, 2.5, 0.5, 5)");
				//Kiev
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(24, \" Пан такси\", \"Пан таксі\", \"Pan taxi\", 0, 24, 3.8, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(25, \" Такси 594\", \"Таксі 594\", \"Taxi 594\", 0, 28, 3, 4, 0.75, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(26, \" Такси Абсолют\", \"Таксі Абсолют\", \"Taxi Absolute\", 0, 35, 3, 3.2, 0.4, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(27, \" Такси Авалон\", \"Таксі Авалон\", \"Taxi Avalon\", 0, 25, 2.5, 3.5, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(28, \" Такси Авеню\", \"Таксі Авеню\", \"Taxi Avenue\", 0, 25, 3.4, 4, 0.6, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(29, \" Такси Арена\", \"Таксі Арена\", \"Taxi Arena\", 0, 25, 3, 4, 0.8, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(30, \" Такси Бонус\", \"Таксі Бонус\", \"Taxi Bonus\", 0, 29, 2.3, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(31, \" Такси Виват\", \"Таксі Віват\", \"Taxi Vivat\", 0, 17, 3.3, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(32, \" Такси Касан\", \"Таксі Касан\", \"Taxi Kasan\", 0, 30, 2.5, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(33, \" Такси Копейка\", \"Таксі Копійка\", \"Taxi Kopeyka\", 0, 25, 4, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(34, \" Такси Кризис\", \"Таксі Криза\", \"Taxi Krizis\", 0, 25, 2.5, 3.5, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(35, \" Такси Лада\", \"Таксі Лада\", \"Taxi Lada\", 0, 30, 3.2, 3.5, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(36, \" Такси Лидер\", \"Таксі Лідер\", \"Taxi Lider\", 0, 17, 3.3, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(37, \" Такси Метеор\", \"Таксі Метеор\", \"Taxi Meteor\", 0, 28, 2.5, 3.5, 0.5, 5)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(38, \" Такси Назари\", \"Таксі Назарі\", \"Taxi Nazari\", 0, 30, 3.2, 3.5, 0.5, 5)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(39, \" Такси Народное\", \"Таксі Народне\", \"Taxi Narodnoe\", 0, 32, 3.3, 4, 0.5, 5)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(40, \" Такси Статус\", \"Таксі Статус\", \"Taxi Status\", 0, 30, 3.5, 4, 0.6, 5)");
        db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(41, \" Такси Плюс\", \"Таксі Плюс\", \"Taxi Plus\", 0, 25, 2.8, 3.5, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(42, \" Такси Премьер\", \"Таксі Прем'єр\", \"Taxi Premyer\", 0, 30, 3, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(43, \" Такси Профи\", \"Таксі Профі\", \"Taxi Profi\", 0, 29, 3, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(44, \" Такси Рандеву\", \"Таксі Рандеву\", \"Taxi Randevu\", 0, 25, 2.6, 5, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(45, \" Такси Супер\", \"Таксі Супер\", \"Taxi Super\", 0, 30, 2.7, 2.8, 0.6, 5)");	
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(46, \" Такси Фавоит\", \"Таксі Фаворіт\", \"Taxi Favorit\", 0, 32, 3.2, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(47, \" Такси Фемида\", \"Таксі Феміда\", \"Taxi Femida\", 0, 50, 2.4, 3.8, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(48, \" Такси Фигаро\", \"Таксі Фігаро\", \"Taxi Figaro\", 0, 49, 3, 5, 0.8, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(49, \" Такси ФM\", \"Таксі ФM\", \"Taxi FM\", 0, 29, 2.5, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(50, \" Такси Фортуна\", \"Таксі Фортуна\", \"Taxi Fortuna\", 0, 28, 3.4, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(51, \" Такси Эко\", \"Таксі Еко\", \"Taxi Eko\", 0, 30, 3.5, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(52, \" Такси Экпресс\", \"Таксі Експресс\", \"Taxi Express\", 0, 50, 4.7, 4, 0.7, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(53, \" Такси Эталон\", \"Таксі Еталон\", \"Taxi Etalon\", 0, 43, 3.6, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(54, \" Такси Ягуар\", \"Таксі Ягуар\", \"Taxi Jaguar\", 0, 25, 2, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(55, \" Топ Такси\", \"Топ Таксі\", \"Top Taxi\", 0, 28, 2.6, 4, 0.5, 5)");
		//Donet'sk
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(56, \" Ваше Такси\", \"Ваше Таксі\", \"Vashe Taxi\", 2, 17.1, 2.6, 3, 0.6, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(57, \" Донецк-такси\", \"Донецьк-таксі\", \"Donets'k-taxi\", 2, 15, 2.5, 3, 1, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(58, \" Донецкое такси\", \"Донецьке таксі\", \"Donets'kое taxi\", 2, 15, 2.5, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(59, \" Донпастранс городское\", \"Донпастранс міське\", \"Donpastrans gorodskoe\", 2, 17.5, 2.5, 3, 0.4, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(60, \" Зеленоглазое такси\", \"Зеленооке таксі\", \"Zelenoglazoe taxi\", 2, 15, 3, null, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(61, \" Информ-Такси\", \"Інформ-Таксі\", \"Inform-Taxi\", 2, 17, 2.6, 3, 0.6, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(62, \" Муниципальное такси\", \"Муніципальне таксі\", \"Municipalnoe taxi\", 2, 15, 3, null, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(63, \" Первый таксопарк\", \"Перший таксопарк\", \"Perviy taxopark\", 2, 17.5, 2.5, 3, 0.4, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(64, \" РеАн-Такси\", \"РеАн-Таксі\", \"ReAn-Taxi\", 2, 15, 3, 3, 0.67, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(65, \" Такси Альфа\", \"Таксі Альфа\", \"Taxi Alfa\", 2, 15, 2.98, null, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(66, \" Такси Блюз\", \"Таксі Блюз\", \"Taxi Blus\", 2, 17.5, 2.5, 3, 0.4, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(67, \" Такси Браво\", \"Таксі Браво\", \"Taxi Bravo\", 2, 23.5, 2.75, null, 0.67, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(68, \" Такси Витязь\", \"Таксі Вітязь\", \"Taxi Vityaz\", 2, 17.1, 2.6, 3, 0.6, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(69, \" Такси Вояж\", \"Таксі Вояж\", \"Taxi Voyage\", 2, 24, 2.7, null, 0.67, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(70, \" Такси Всегда\", \"Таксі Завжди\", \"Taxi Vsegda\", 2, 15, 3, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(71, \" Такси Диал+\", \"Таксі Діал+\", \"Taxi Dial+\", 2, 15, 3, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(72, \" Такси Дон\", \"Таксі Дон\", \"Taxi Don\", 2, 15, 2.5, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(73, \" Такси Козак\", \"Таксі Козак\", \"Taxi Kozak\", 2, 14, 2.3, null, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(74, \" Такси Лайф\", \"Таксі Лайф\", \"Taxi Life\", 2, 15, 2.98, 2.98, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(75, \" Такси ГранД\", \"Таксі ГранД\", \"Taxi GranD\", 2, 23.5, 2.7, null, 0.67, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(76, \" Такси Люкс\", \"Таксі Люкс\", \"Taxi Lux\", 2, 15, 2.5, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(77, \" Такси Мади\", \"Таксі Маді\", \"Taxi Madi\", 2, 16, 3, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(78, \" Такси Мозайка\", \"Таксі Мозайка\", \"Taxi Mozayka\", 2, 21, 3, 4, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(79, \" Такси Надежда\", \"Таксі Надія\", \"Taxi Nadezhda\", 2, 15, 3, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(80, \" Такси Петровское\", \"Таксі Петрівське\", \"Taxi Petrovskoe\", 2, 16, 2.5, 3, 0.6, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(81, \" Такси Профессионал\", \"Таксі Профкссіонал\", \"Taxi Professional\", 2, 16, 3, null, 0.67, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(82, \" Такси Пятерочка\", \"Таксі П'ятірочка\", \"Taxi Pyaterochka\", 2, 14, 3, null, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(83, \" Такси Сервис\", \"Таксі Сервіс\", \"Taxi Servis\", 2, 16, 3, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(84, \" Такси Союз\", \"Таксі Союз\", \"Taxi Soyuz\", 2, 15, 2.8, 2.8, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(85, \" Такси Статус\", \"Таксі Статус\", \"Taxi Status\", 2, 16, 3, null, 0.6, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(86, \" Такси Транзит\", \"Таксі Транзіт\", \"Taxi Tranzit\", 2, 16, 3, null, 0.67, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(87, \" Такси Тройка\", \"Таксі Трійка\", \"Taxi Troyka\", 2, 15, 3, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(88, \" Такси Фаэтон\", \"Таксі Фаетон\", \"Taxi Faeton\", 2, 15, 3, 3, 0.5, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(89, \" Такси Шансон\", \"Таксі Шансон\", \"Taxi Shanson\", 2, 16, 3, null, 0.67, 5)");
		db.execSQL("insert into taxi_services (_id, name_rus, name_ua, name_en, city_id, init_price, price_per_km, price_per_km_country, price_downtime, km_in_init_price) "
                + "values(90, \" Такси Эскорт\", \"Таксі Ескорт\", \"Taxi Eskort\", 2, 15, 2.5, 2.7, 0.5, 5)");
		
				
				
				
        //Phones
		//Dnepropetrovsk
        db.execSQL("insert into phones (phone, taxi_id) values(\"+380939125432\", 0)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380931111111\", 0)");
        db.execSQL("insert into phones (phone, taxi_id) values(\"+380504512365\", 0)");
		//pilot taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380684002840\", 1)");
		//time taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+38056790777\", 2)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+38095111147\", 2)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+38098111147\", 2)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+38063111147\", 2)");
		//voyage taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567851212\", 3)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380959199229\", 3)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380962491169\", 3)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380932717878\", 3)");
		//grand taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567882727\", 4)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380970152727\", 4)");
		//dan taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380562355333\", 5)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567881719\", 5)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567855551\", 5)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380661623333\", 5)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380974441535\", 5)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933975666\", 5)");
		//Dnepr taxi business
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380562333333\", 6)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503404031\", 6)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380675633145\", 6)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933243233\", 6)");
		//Dnepr taxi economy
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380562321321\", 7)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503206241\", 7)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676397434\", 7)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933243234\", 7)");
		// Dnepr taxi elites
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380986940697\", 8)");
		// Life taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+38056787777\", 9)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380967007027\", 9)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380633266266\", 9)");
		//Lyubimoe taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567880808\", 10)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380994990000\", 10)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380976567777\", 10)");
		//Nashe taxi comfort
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567981111\", 11)");
		//Nashe taxi economy
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380562362362\", 12)");
		//The first taxomotorniy
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380562335335\", 13)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380952335335\", 13)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676403535\", 13)");
		//Privat taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567444444\", 14)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380562351351\", 14)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380562346346\", 14)");
		//Profi-taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380563785555\", 15)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380563765555\", 15)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380954445555\", 15)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380674775555\", 15)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380633785555\", 15)");
		//Radio-taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380562335353\", 16)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503610111\", 16)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676324111\", 16)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380939252780\", 16)");
		//Slavuta tsxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380562366366\", 17)");
		//Fair taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380563747474\", 18)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503427474\", 18)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380980747474\", 18)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380633747474\", 18)");
		//Taxi Welcom
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380988443377\", 19)");
		// Taxi Devyatochka
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567169999\", 20)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503424370\", 20)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676362236\", 20)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380634549999\", 20)");
		// Taxi Kapriz
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567898789\", 21)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380673734373\", 21)");
		// Taxi Liga
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567895050\", 22)");
		// Etalon-taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380567893111\", 23)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380987393111\", 23)");
		//Kiev
		// Pan taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380444541454\", 24)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380504477575\", 24)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380675047575\", 24)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933007575\", 24)");
		//Taxi 594
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445949494\", 25)");
		//Taxi Absolute
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380444559595\", 26)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380667040403\", 26)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380675999972\", 26)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380632444422\", 26)");
		//Taxi Avalon
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380442211133\", 27)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380955766228\", 27)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380673246606\", 27)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933290003\", 27)");
		//Taxi Avenue
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445037373\", 28)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380952483333\", 28)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380964813333\", 28)");
		//Taxi Arena
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445021111\", 29)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380975021111\", 29)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380935021111\", 29)");
		//Taxi Bonus
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445781212\", 30)");
		//Taxi Vivat
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445879587\", 31)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380967898822\", 31)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380636250372\", 31)");
		//Taxi Kasan
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445991111\", 32)");
		//Taxi Kopeyka
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380443901390\", 33)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+3805033790090\", 33)");
		//Taxi Krizis
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380444980101\", 34)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380662150601\", 34)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380989003001\", 34)");
		//Taxi Lada
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445355252\", 35)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380955355252\", 35)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380985355252\", 35)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380935355252\", 35)");
		//Taxi Lider
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445780606\", 36)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380506870708\", 36)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380679840606\", 36)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380635780606\", 36)");
		//Taxi Meteor
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380443937393\", 37)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380663203333\", 37)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380986193333\", 37)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380936393333\", 37)");
		//Taxi Nazari
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445876868\", 38)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380954946868\", 38)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380672086868\", 38)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933296868\", 38)");
		//Taxi Narodnoe
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445200520\", 39)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380504440520\", 39)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380674440520\", 39)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933870520\", 39)");
		//Taxi Status
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445020909\", 40)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380667602626\", 40)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380972877815\", 40)");
		//Taxi Plus
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445921313\", 41)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380672332335\", 41)");
		//Taxi Premyer
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445923030\", 42)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380674073636\", 42)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503806767\", 42)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380632331150\", 42)");
		//Taxi Profi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380443550202\", 43)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380985038899\", 43)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380633550202\", 43)");
		//Taxi Randevu
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380443311501\", 44)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676584822\", 44)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380632337070\", 44)");
		//Taxi Super
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380442480606\", 45)");
		//Taxi Favorit
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380442210909\", 46)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380976097484\", 46)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380638809669\", 46)");
		//Taxi Femida
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380444552222\", 47)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380993075222\", 47)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380962414222\", 47)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380930562222\", 47)");
		//Taxi Figaro
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380444932493\", 48)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380958365959\", 48)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380674174337\", 48)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380934117218\", 48)");
		//Taxi FM
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445020502\", 49)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505020502\", 49)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380674400502\", 49)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380632450502\", 49)");
		//Taxi Fortuna
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380442000202\", 50)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380508889191\", 50)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380962200202\", 50)");
		//Taxi Eko
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445940594\", 51)");
		//Taxi Express
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380444951515\", 52)");
		//Taxi Etalon
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445015501\", 53)");
		//Taxi Jaguar
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380442448808\", 54)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380661046076\", 54)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380679698190\", 54)");
		//Top Taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380445997979\", 55)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380994497979\", 55)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380975797979\", 55)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380935457979\", 55)");
		//Donets'k
		//Vashe Taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623811919\", 56)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380506220973\", 56)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505676622\", 56)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380677554606\", 56)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380677766545\", 56)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380638501222\", 56)");
		//Donets'k-taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380622100505\", 57)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380952100505\", 57)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380502100505\", 57)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380962100505\", 57)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380633100505\", 57)");
		//Donets'koe taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623810061\", 58)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380954950495\", 58)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380636016021\", 58)");
		//Donpastrans gorodskoe
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623043103\", 59)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623810102\", 59)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623810403\", 59)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505858585\", 59)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380502848484\", 59)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380963674041\", 59)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380631596688\", 59)");
		//Zelenoglazoe taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380501740063\", 60)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676214063\", 60)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380634864963\", 60)");
		//Inform-Taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623811758\", 61)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623451212\", 61)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505676622\", 61)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380506226973\", 61)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380679971778\", 61)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380638501222\", 61)");
		//Municipalnoe Taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380951111119\", 62)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380952222229\", 62)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380953333339\", 62)");
		//Perviy taxopark
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623859292\", 63)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503489392\", 63)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503289292\", 63)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676259292\", 63)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380633332323\", 63)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380633399992\", 63)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933151555\", 63)");
		//ReAn-Taxi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623854444\", 64)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505251434\", 64)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380983141239\", 64)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380936970734\", 64)");
		//Taxi Alfa
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380951707770\", 65)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380508875016\", 65)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933151577\", 65)");
		//Taxi Blus
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623810403\", 66)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623810102\", 66)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623499411\", 66)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380502848484\", 66)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380997371540\", 66)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380990325090\", 66)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380963674041\", 66)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380631596688\", 66)");
		//Taxi Bravo
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623850909\", 67)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623813223\", 67)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503477347\", 67)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676214131\", 67)");
		//Taxi Vityaz
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623451212\", 68)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623811919\", 68)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380506220973\", 68)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505676622\", 68)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380677554606\", 68)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380677766545\", 68)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380638501222\", 68)");
		//Taxi Voyage
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623301550\", 69)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503477347\", 69)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676214131\", 69)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380634615050\", 69)");
		//Taxi Vsegda
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623850000\", 70)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380951111119\", 70)");
		//Taxi Dial+
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623344000\", 71)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505806801\", 71)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623373464\", 71)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380677040209\", 71)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380637837372\", 71)");
		//Taxi Don
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623810061\", 72)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380504950495\", 72)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380954950495\", 72)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380679178267\", 72)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380636016021\", 72)");
		//Taxi Kozak
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623812233\", 73)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505781818\", 73)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380677628181\", 73)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380638101785\", 73)");
		//Taxi Life
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623337777\", 74)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623867777\", 74)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380502327777\", 74)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380964700377\", 74)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380636182377\", 74)");
		//Taxi GranD
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623000000\", 75)");
		//Taxi Lux
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623810606\", 76)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380506068989\", 76)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380951407227\", 76)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380679678899\", 76)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380938679995\", 76)");
		//Taxi Madi
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623343332\", 77)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380952800055\", 77)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380636056583\", 77)");
		//Taxi Mozayka
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623404444\", 78)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380953934444\", 78)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380981134444\", 78)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380686851444\", 78)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380938770000\", 78)");
		//Taxi Nadezhda
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623812300\", 79)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380506262815\", 79)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505652255\", 79)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380679579449\", 79)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380637883636\", 79)");
		//Taxi Petrovskoe
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623136696\", 80)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380622034004\", 80)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380509047611\", 80)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380958602220\", 80)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380636016111\", 80)");
		//Taxi Professional
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503891589\", 81)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676281589\", 81)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380633151589\", 81)");
		//Taxi Pyaterochka
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380622100555\", 82)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380958355555\", 82)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380958455555\", 82)");
		//Taxi Servis
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623341351\", 83)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505673878\", 83)");
		//Taxi Soyuz
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623859898\", 84)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623040000\", 84)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380999755885\", 84)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676253040\", 84)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380931888685\", 84)");
		//Taxi Status
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623331111\", 85)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623301111\", 85)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623309998\", 85)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380622084444\", 85)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380953401111\", 85)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676181111\", 85)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380634431111\", 85)");
		//Taxi Tranzit
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623451000\", 86)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623851000\", 86)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503451000\", 86)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676232550\", 86)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380933451000\", 86)");
		//Taxi Troyka
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623812300\", 87)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505652255\", 87)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380506262815\", 87)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380679579449\", 87)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380637883636\", 87)");
		//Taxi Faeton
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623450405\", 88)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623456565\", 88)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380506427772\", 88)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380674270105\", 88)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380939696393\", 88)");
		//Taxi Shanson
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380503891589\", 89)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380508718088\", 89)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380504713596\", 89)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380999133088\", 89)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380676220882\", 89)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380635664088\", 89)");
		//Taxi Eskort
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380623380303\", 90)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380505261963\", 90)");
		db.execSQL("insert into phones (phone, taxi_id) values(\"+380677622627\", 90)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
