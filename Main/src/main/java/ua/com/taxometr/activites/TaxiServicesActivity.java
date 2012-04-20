package ua.com.taxometr.activites;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.DBHelper;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 16.04.12
 */
public class TaxiServicesActivity extends Activity {
    DBHelper dbHelper;
    private static final String ISO_RUSSIAN = "rus";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_services_list_view);

        //create DBHelper for database manipulating
        dbHelper = new DBHelper(this);

        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        //determine russian or english name use
        final String currentLanguage = this.getResources().getConfiguration().locale.getISO3Language();
        final String nameField;
        if(currentLanguage.equals(ISO_RUSSIAN)) {
            nameField = "name_rus";
        } else {
            nameField = "name_en";
        }

        //get data from db
        final Cursor cursor = db.query("taxi_services", new String[] {"_id", "name_en", "name_rus"}, null, null, null, null, null);
        final ListAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                cursor, new String[] {nameField}, new int[] { android.R.id.text1 });

        //apply cursor adapter
        final ListView lvTaxiServices = (ListView) findViewById(R.id.taxi_services_list);
        lvTaxiServices.setAdapter(cursorAdapter);
        dbHelper.close();
    }

}
