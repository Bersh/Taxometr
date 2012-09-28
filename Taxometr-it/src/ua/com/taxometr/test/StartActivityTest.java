package ua.com.taxometr.test;

import android.app.LauncherActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.ListView;
import ua.com.taxometr.R;
import ua.com.taxometr.activites.StartActivity;

/**
 * Test for {@link ua.com.taxometr.activites.StartActivity} class
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 */
public class StartActivityTest extends ActivityInstrumentationTestCase2<StartActivity> {

    /**
     * Default constructor
     */
    public StartActivityTest() {
        super("ua.com.taxometr", StartActivity.class);
    }

    /**
     * Test StartActivity for basic functionality
     */
    public void testStartActivityBasic() {
        final StartActivity startActivity = getActivity();
        assertNotNull(startActivity);
        assertEquals(startActivity.getClass(), StartActivity.class);

        final ListView menuItems = (ListView) startActivity.findViewById(R.id.menu_list);
        final Button btnCalcRoute = (Button) startActivity.findViewById(R.id.btn_calc_route);
        assertNotNull(menuItems);
        assertNotNull(btnCalcRoute);

        assertEquals(menuItems.getItemAtPosition(0).toString(), startActivity.getString(R.string.btn_from_text));
        assertEquals(menuItems.getItemAtPosition(1).toString(), startActivity.getString(R.string.btn_to_text));
        assertEquals(btnCalcRoute.getText(), startActivity.getString(R.string.btn_calc_route_text));
//        TouchUtils.clickView(this, btnFrom);
    }
}

