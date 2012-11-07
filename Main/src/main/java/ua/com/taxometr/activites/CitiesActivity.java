package ua.com.taxometr.activites;

import android.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.google.inject.Inject;
import roboguice.activity.RoboListActivity;
import ua.com.taxometr.helpers.DBHelper;
import ua.com.taxometr.helpers.MenuHelper;

/**
 * Activity with list of cities
 *
 * @author Ilya Lisovyy <a href="mailto:ip.lisoviy@gmail.com">Ilya Lisovyy</a>
 * @since 13.06.12
 */
public class CitiesActivity extends RoboListActivity {

    private SQLiteDatabase db;

    @Inject
    private DBHelper dbHelper;
    private Cursor cities;
    private String localeName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = dbHelper.getWritableDatabase();
        localeName = TaxiServicesListActivity.getLocaleName(this);
        cities = db.query("cities a",
                new String[]{"a._id", "a." + localeName, "a.country_id"}, null, null, null, null, "a." + localeName);
        if (cities.moveToFirst()) {
            final ListAdapter citiesAdapter = new SimpleCursorAdapter(this,
                    R.layout.simple_list_item_1, cities,
                    new String[]{"a." + localeName}, new int[]{R.id.text1});
            setListAdapter(citiesAdapter);
        } else {
            finish();
        }
    }

    @Override
    protected void onListItemClick(final ListView listView, View v, final int position, long id) {
        final String city = ((Cursor) listView.getItemAtPosition(position))
                .getString(((Cursor) listView.getItemAtPosition(position)).getColumnIndex(localeName));
        final int country_id = ((Cursor) listView.getItemAtPosition(position))
                .getInt(((Cursor) listView.getItemAtPosition(position)).getColumnIndex("country_id"));
        final Cursor countries = db.query("countries a", new String[]{"a._id", "a." + localeName}, "a._id = ? ",
                new String[]{String.valueOf(country_id)}, null, null, null);
        String country = "";
        if (countries.moveToFirst()) {
            country = countries.getString(countries.getColumnIndex(localeName));
        } else {
            finish();
        }

        final SharedPreferences prefs = getSharedPreferences(StartActivity.PREFS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(StartActivity.COUNTRY_KEY, country);
        editor.putString(StartActivity.CITY_KEY, city);
        editor.commit();
        final Intent intent = new Intent(CitiesActivity.this, TaxiServicesListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.cities != null) {
            this.cities.close();
            this.cities = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(ua.com.taxometr.R.layout.menu_about_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuHelper menu = new MenuHelper();
        return menu.optionsItemSelected(item, this);
    }
}