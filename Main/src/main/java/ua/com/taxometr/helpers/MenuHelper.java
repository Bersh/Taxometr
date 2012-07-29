package ua.com.taxometr.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import ua.com.taxometr.R;
import ua.com.taxometr.activites.LanguageListActivity;

/**
 * Menu for all application's activities
 *
 * @author Ilya Lisovyy <a href="mailto:ip.lisoviy@gmail.com">Ilya Lisovyy</a>
 * @since 05.07.12
 */

public class MenuHelper extends Activity {

    /**
     * Default constructor
     */
    public MenuHelper() {
    }

    /**
     * listener for option menu
     * @param item instance of {@link MenuItem}
     * @param activityContext instance of {@link Context}
     * @return boolean value
     */
    public boolean optionsItemSelected(MenuItem item,Context activityContext) {
        switch (item.getItemId()) {
            case R.id.lang:
                final Intent intent = new Intent(activityContext, LanguageListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activityContext.startActivity(intent);
                return true;
            case R.id.about:
                final Dialog aboutDialog = new Dialog(activityContext);
                aboutDialog.setContentView(R.layout.about_dialog_box);
                aboutDialog.setTitle(activityContext.getString(R.string.app_name));
                aboutDialog.setCancelable(true);

                final ImageView img = (ImageView) aboutDialog.findViewById(R.id.ic_about);
                img.setImageResource(R.drawable.taxi);

                final Button button = (Button) aboutDialog.findViewById(R.id.about);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aboutDialog.cancel();
                    }
                });
                aboutDialog.show();
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }
}
