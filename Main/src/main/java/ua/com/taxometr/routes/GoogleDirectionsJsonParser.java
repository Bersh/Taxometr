package ua.com.taxometr.routes;

import com.google.android.maps.GeoPoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 12.08.12
 */
public class GoogleDirectionsJsonParser {
    private static final String ROUTE_TAG_EN = "Route";
    private static final String ROUTE_TAG_RU = "Маршрут";

    private final Road road;
    boolean isPlacemark;
    boolean isRoute;
    boolean isItemIcon;
    private String tmpString;


    /**
     * Default constructor
     */
    public GoogleDirectionsJsonParser() {
        road = new Road();
    }

    public Road getRoad(String inputString) {
        try {
            final JSONObject json = new JSONObject(inputString);
            // routesArray contains ALL routes
            JSONArray routesArray = json.getJSONArray("routes");
            // Grab the first route
            JSONObject route = routesArray.getJSONObject(0);
            // Take all legs from the route
            JSONArray legs = route.getJSONArray("legs");
            // Grab first leg
            JSONObject leg = legs.getJSONObject(0);

            JSONObject tempObject = leg.getJSONObject("distance");
            road.length = tempObject.getDouble("value") / 1000;

            road.route = decodePolyline(route.getJSONObject("overview_polyline").getString("points"));

            road.description = leg.getJSONObject("distance").getString("text") + " " + leg.getJSONObject("duration").getString("text");
        } catch (JSONException e) {

        }

        return road;
    }

    private ArrayList<GeoPoint> decodePolyline(String encoded) {

        ArrayList<GeoPoint> poly = new ArrayList<GeoPoint>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
                    (int) (((double) lng / 1E5) * 1E6));
            poly.add(p);
        }

        return poly;
    }

}
