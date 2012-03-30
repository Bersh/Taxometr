package ua.com.taxometr.test;

import android.test.ActivityInstrumentationTestCase2;
import ua.com.taxometr.activites.SelectAddressActivity;

/**
 * Test for {@link ua.com.taxometr.activites.SelectAddressActivity} class
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 */
public class SelectAddressActivityTest extends ActivityInstrumentationTestCase2<SelectAddressActivity> {

    /**
     * Default constructor
     */
    public SelectAddressActivityTest() {
        super("ua.com.taxometr", SelectAddressActivity.class);
    }

    /**
     * Test SelectAddressActivity for basic functionality
     */
    public void testSelectAddressActivityBasic() {
        final SelectAddressActivity selectAddressActivity = getActivity();
        assertNotNull(selectAddressActivity);
        assertEquals(selectAddressActivity.getClass(), SelectAddressActivity.class);
    }
}
