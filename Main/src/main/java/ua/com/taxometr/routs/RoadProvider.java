package ua.com.taxometr.routs;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 * @author ibershadskiy <a href="mailto:Ilya.Bershadskiy@exigenservices.com">Ilya Bershadskiy</a>
 * @since 26.04.12
 */
public class RoadProvider {

    public static Road getRoute(InputStream is) {
        final KMLHandler handler = new KMLHandler();
        try {
            final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(is, handler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return handler.mRoad;
    }

    public static String getUrl(double fromLat, double fromLon, double toLat,
                                double toLon) {// connect to map web service
        StringBuffer urlString = new StringBuffer();
        urlString.append("http://maps.google.com/maps?f=d&hl=en");
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


