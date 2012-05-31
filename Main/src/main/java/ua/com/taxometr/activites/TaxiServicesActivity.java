package ua.com.taxometr.activites;

import android.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import ua.com.taxometr.helpers.DBHelper;

/**
 * Activity for displaying list of taxi services
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 16.04.12
 */
public class TaxiServicesActivity extends ListActivity {
    private static final String ISO_RUSSIAN = "rus";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create DBHelper for database manipulating
        final DBHelper dbHelper = new DBHelper(this);

        try {
            final SQLiteDatabase db = dbHelper.getReadableDatabase();
            //determine russian or english name use
            final String currentLanguage = this.getResources().getConfiguration().locale.getISO3Language();
            final String nameField;
            if (currentLanguage.equals(ISO_RUSSIAN)) {
                nameField = "name_rus";
            } else {
                nameField = "name_en";
            }

            // get current city and country
            final SharedPreferences prefs = getSharedPreferences(StartActivity.PREFS_NAME, Context.MODE_PRIVATE);
            final String city = prefs.getString(StartActivity.CITY_KEY, "");
            final String country = prefs.getString(StartActivity.COUNTRY_KEY, "");
            //get route length
            final Float length = prefs.getFloat(GoogleMapActivity.ROUTE_LENGTH_KEY, 0);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
            final boolean isRouteCalculated = length > 0;
            final String orderBy = "(init_price + price_per_km * " + length + ")";

            final Cursor cursor;
            final ListAdapter cursorAdapter;

            if (isRouteCalculated) {
                //get taxi services from db
                final String moneyColumnName = "money";
                cursor = db.query("taxi_services a, cities b, countries c",
                        new String[]{"a._id", "a.name_en", "a.name_rus", orderBy + " " + moneyColumnName},
                        "a.city_id = b._id and b.country_id = c._id and c." + nameField + " = ? and b." + nameField + " = ?",
                        new String[]{country, city}, null, null, orderBy);
                cursorAdapter = new SimpleCursorAdapter(this, R.layout.simple_list_item_2,
                        cursor, new String[]{nameField, moneyColumnName}, new int[]{R.id.text1, R.id.text2});
            } else {
                //get taxi services from db
                cursor = db.query("taxi_services a, cities b, countries c",
                        new String[]{"a._id", "a.name_en", "a.name_rus"},
                        "a.city_id = b._id and b.country_id = c._id and c." + nameField + " = ? and b." + nameField + " = ?",
                        new String[]{country, city}, null, null, null);
                cursorAdapter = new SimpleCursorAdapter(this, R.layout.simple_list_item_1,
                        cursor, new String[]{nameField}, new int[]{R.id.text1});
            }

            //apply cursor adapter
            setListAdapter(cursorAdapter);
        } finally {
            dbHelper.close();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Intent intent = new Intent(this, PhonesListActivity.class);
        intent.putExtra(PhonesListActivity.TAXI_SERVICE_ID_NAME, l.getItemIdAtPosition(position));
        startActivity(intent);
    }


}
