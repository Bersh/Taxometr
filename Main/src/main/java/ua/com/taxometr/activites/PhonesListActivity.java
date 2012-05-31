package ua.com.taxometr.activites;

import android.R;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import ua.com.taxometr.helpers.DBHelper;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 30.05.12
 */
public class PhonesListActivity extends ListActivity {
    /**
     * text id using to put taxi service id data  in intent
     */
    public static final String TAXI_SERVICE_ID_NAME = "TaxiServiceId";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final long taxiServiceId = intent.getLongExtra(TAXI_SERVICE_ID_NAME, -1);
        //create DBHelper for database manipulating
        final DBHelper dbHelper = new DBHelper(this);

        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();
            //get taxi service phone numbers from db
            final Cursor cursor = db.query("phones",
                    new String[]{"_id", "phone"},
                    "taxi_id = ?",
                    new String[]{Long.toString(taxiServiceId)}, null, null, null);
            final ListAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.simple_list_item_1,
                    cursor, new String[]{"phone"}, new int[]{R.id.text1});
            //apply cursor adapter
            setListAdapter(cursorAdapter);
        } finally {
            dbHelper.close();
        }
    }

}