package ua.com.taxometr.activites;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ua.com.taxometr.R;
import ua.com.taxometr.helpers.MenuHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Activity with select language
 *
 * @author Ilya Lisovyy <a href="mailto:ip.lisoviy@gmail.com">Ilya Lisovyy</a>
 * @since 02.06.12
 */
public class LanguageListActivity extends ListActivity {

    private static final String LOCALEKEY = "LOCALE";
    private static final String IMGKEY = "ICON";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ArrayList<HashMap<String, Object>> languages = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm = new HashMap<String, Object>();

        hm.put(LOCALEKEY, "English");
        hm.put(IMGKEY, R.drawable.gb);
        languages.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(LOCALEKEY, "Українська");
        hm.put(IMGKEY, R.drawable.ukraine);
        languages.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(LOCALEKEY, "Русский");
        hm.put(IMGKEY, R.drawable.russia);
        languages.add(hm);

        final ListAdapter adapter = new SimpleAdapter(this,
                languages, R.layout.language_list_view,
                new String[]{LOCALEKEY, IMGKEY},
                new int[]{R.id.text1, R.id.img});
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(final ListView listView, View v, final int position, long id) {
        String language = "en";
        switch (position) {
            case 0:
                language = "en";
                break;
            case 1:
                language = "uk";
                break;
            case 2:
                language = "ru";
                break;
            default:
        }
        setLanguage(language);
        final Intent intent = new Intent(LanguageListActivity.this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * set locale
     *
     * @param language instance of {@link java.lang.String}
     */
    public void setLanguage(String language) {
        final Locale locale = new Locale(language);
        Locale.setDefault(locale);
        final Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_about_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuHelper menu = new MenuHelper();
        return menu.optionsItemSelected(item, this);
    }

}