package ua.com.taxometr;


import android.content.pm.ApplicationInfo;
import com.google.inject.Module;
import de.akquinet.android.androlog.Log;
import roboguice.application.RoboApplication;
import roboguice.config.AbstractAndroidModule;
import ua.com.taxometr.helpers.LocationHelper;
import ua.com.taxometr.helpers.LocationHelperInterface;

import java.lang.reflect.Method;
import java.util.List;

public class TaxometrApplication extends RoboApplication {
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

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new TaxometrModule());
    }

    static class TaxometrModule extends AbstractAndroidModule {
        @Override
        protected void configure() {
            bind(LocationHelperInterface.class).to(LocationHelper.class);
        }
    }
}
