package ua.com.taxometr;

import com.google.inject.AbstractModule;
import ua.com.taxometr.helpers.LocationHelper;
import ua.com.taxometr.helpers.LocationHelperInterface;

/**
 * Guice module
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 08.11.12
 */
public class TaxometrModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LocationHelperInterface.class).to(LocationHelper.class);
    }
}
