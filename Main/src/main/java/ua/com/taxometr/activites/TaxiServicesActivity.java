package ua.com.taxometr.activites;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;
import ua.com.taxometr.R;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 16.04.12
 */
public class TaxiServicesActivity extends Activity {
    DBHelper dbHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.deleteDatabase("TaxometrDB");

        setContentView(R.layout.taxi_services_list_view);
        ListView lvTaxiServices = (ListView) findViewById(R.id.taxi_services_list);

        //create DBHelper for database manipulating
        dbHelper = new DBHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
/*
        db.execSQL("create table countries ("
                + "_id integer primary key autoincrement, "
                + "name_rus text, "
                + "name_en);");
        db.execSQL("insert into countries (name_rus, name_en) values(\"Россия\", \"Russia\")");
        db.execSQL("insert into countries (name_rus, name_en) values(\"Украина\", \"Ukraine\")");
*/

        Cursor c = db.query("countries", new String[] {"name_en"}, null, null, null, null, null);
        if (c.moveToFirst()) {
            int nameEnId = c.getColumnIndex("name_en");
            Log.e("Taxometr_DB", "name_rus_id = " + nameEnId);
            do {
                Log.e("Taxometr_DB", "name_rus = " + c.getString(nameEnId));
            } while (c.moveToNext());
        } else {
            Log.d("Taxometr_DB", "No records found");
        }
        dbHelper.close();
/*        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                c, )

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                dbHelper.getReadableDatabase().);*/

    }

}
