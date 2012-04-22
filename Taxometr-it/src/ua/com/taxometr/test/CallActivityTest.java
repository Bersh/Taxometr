package ua.com.taxometr.test;

import android.test.ActivityInstrumentationTestCase2;
import ua.com.taxometr.activites.CallActivity;
import android.widget.Button;
import android.widget.TextView;
import ua.com.taxometr.R;

public class CallActivityTest extends ActivityInstrumentationTestCase2<CallActivity>
{
    CallActivity mActivity;
    //TextView mView;
    Button mButton1, mButton2;
    String resourceString1,resourceString2,resourceString3;

    public CallActivityTest() {
        super("ua.com.taxometr", CallActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
//        mView = (TextView) mActivity.findViewById(R.id.lbl_ask_call);
        mButton1 = (Button) mActivity.findViewById(R.id.btn_accept_call);
        mButton2 = (Button) mActivity.findViewById(R.id.btn_cancel_call);
        resourceString1 = mActivity.getString(R.string.lbl_ask);
        resourceString2 = mActivity.getString(R.string.btn_ok_text);
        resourceString3 = mActivity.getString(R.string.btn_cancel_text);
    }

    public void testPreconditions() {
        assertNotNull(mActivity);
//        assertNotNull(mView);
        assertNotNull(mButton1);
        assertNotNull(mButton2);
    }

    public void testText() {
       // assertEquals(resourceString1,(String)mView.getText());
        assertEquals(resourceString2,(String)mButton1.getText());
        assertEquals(resourceString3,(String)mButton2.getText());
    }
    /*
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
    */
}