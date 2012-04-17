package ua.com.taxometr.test;

import android.test.ActivityInstrumentationTestCase2;
import ua.com.taxometr.activites.CallActivity;
import android.widget.Button;
import android.widget.TextView;
import ua.com.taxometr.R;

public class CallActivityTest extends ActivityInstrumentationTestCase2<CallActivity>
{
    public CallActivityTest() {
        super("ua.com.taxometr", CallActivity.class);
    }

    public void testCallActivityBasic() {
        final CallActivity callActivity = getActivity();
        assertNotNull(callActivity);
        assertEquals(callActivity.getClass(), CallActivity.class);

        final Button btnCall = (Button) callActivity.findViewById(R.id.btn_accept_call);
        final Button btnCancel = (Button) callActivity.findViewById(R.id.btn_cancel_call);
        final TextView txtAsk = (TextView) callActivity.findViewById(R.id.lbl_ask_call);

        assertNotNull(btnCall);
        assertNotNull(btnCancel);
        assertNotNull(txtAsk);

        assertEquals(btnCall.getText(), callActivity.getString(R.string.btn_ok_text));
        assertEquals(btnCancel.getText(), callActivity.getString(R.string.btn_cancel_text));
        assertEquals(txtAsk.getText(), callActivity.getString(R.string.lbl_ask));
    }
}