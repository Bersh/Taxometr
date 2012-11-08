package ua.com.taxometr;


import android.app.Application;
import android.content.pm.ApplicationInfo;
import de.akquinet.android.androlog.Log;

import java.lang.reflect.Method;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 */
public class TaxometrApplication extends Application {
    @Override
    public void onCreate() {
        int applicationFlags = getApplicationInfo().flags;
        if ((applicationFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            try {
                Class strictMode = Class.forName("android.os.StrictMode");
                Method enableDefaults = strictMode.getMethod("enableDefaults");
                enableDefaults.invoke(strictMode);
            } catch (Throwable throwable) {
                Log.d("No StrictMode");
            }
        }
        super.onCreate();
    }
}
