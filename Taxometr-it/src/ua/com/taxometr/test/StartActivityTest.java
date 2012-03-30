package ua.com.taxometr.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
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

        final Button btnFrom = (Button) startActivity.findViewById(R.id.btn_from);
        final Button btnTo = (Button) startActivity.findViewById(R.id.btn_to);
        final Button btnCalcRoute = (Button) startActivity.findViewById(R.id.btn_calc_route);

        assertNotNull(btnFrom);
        assertNotNull(btnTo);
        assertNotNull(btnCalcRoute);

        assertEquals(btnFrom.getText(), startActivity.getString(R.string.btn_from_text));
        assertEquals(btnTo.getText(), startActivity.getString(R.string.btn_to_text));
        assertEquals(btnCalcRoute.getText(), startActivity.getString(R.string.btn_calc_route_text));
    }
}

