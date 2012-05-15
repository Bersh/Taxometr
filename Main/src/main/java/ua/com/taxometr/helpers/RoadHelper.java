package ua.com.taxometr.helpers;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import android.content.Context;
import de.akquinet.android.androlog.Log;
import ua.com.taxometr.routes.KMLHandler;
import ua.com.taxometr.routes.Road;

/**
 * Helper for creating route
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 26.04.12
 */
public class RoadHelper {

    /**
     * Default constructor
     */
    private RoadHelper() {
    }

    /**
     * Parse KML file
     * @param is input stream
     * @return {@link ua.com.taxometr.routes.Road} object
     */
    public static Road getRoute(InputStream is) {
        final KMLHandler handler = new KMLHandler();
        try {
            final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(is, handler);
        } catch (ParserConfigurationException e) {
            Log.e(e.getMessage());
        } catch (SAXException e) {
            Log.e(e.getMessage());
        } catch (IOException e) {
            Log.e(e.getMessage());
        }
        return handler.getRoad();
    }

    /**
     * Build url for route calculation
     * @param fromLat start point latitude
     * @param fromLon start point longitude
     * @param toLat destination point latitude
     * @param toLon destination point longitude
     * @param context current context
     * @return route url for Google Maps service
     */
    public static String getUrl(double fromLat, double fromLon, double toLat, double toLon, Context context) {// connect to map web service
        final StringBuilder urlString = new StringBuilder();
        final String currentLanguage = context.getResources().getConfiguration().locale.getLanguage();
        urlString.append("http://maps.google.com/maps?f=d&hl=");
        urlString.append(currentLanguage); //current language
        urlString.append("&saddr=");// from
        urlString.append(Double.toString(fromLat));
        urlString.append(",");
        urlString.append(Double.toString(fromLon));
        urlString.append("&daddr=");// to
        urlString.append(Double.toString(toLat));
        urlString.append(",");
        urlString.append(Double.toString(toLon));
        urlString.append("&ie=UTF8&0&om=0&output=kml");
        return urlString.toString();
    }

}


