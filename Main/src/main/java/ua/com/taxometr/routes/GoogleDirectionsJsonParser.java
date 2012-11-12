package ua.com.taxometr.routes;

import com.google.android.maps.GeoPoint;
import de.akquinet.android.androlog.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 12.08.12
 */
public class GoogleDirectionsJsonParser {

    private final Road road;

    /**
     * Default constructor
     */
    public GoogleDirectionsJsonParser() {
        road = new Road();
    }

    /**
     * Parse road from received json
     *
     * @param inputString received json
     * @return {@link Road}
     */
    public Road getRoad(String inputString) {
        try {
            final JSONObject json = new JSONObject(inputString);
            // routesArray contains ALL routes
            final JSONArray routesArray = json.getJSONArray("routes");
            // Grab the first route
            final JSONObject route = routesArray.getJSONObject(0);
            // Take all legs from the route
            final JSONArray legs = route.getJSONArray("legs");
            // Grab first leg
            final JSONObject leg = legs.getJSONObject(0);

            final JSONObject tempObject = leg.getJSONObject("distance");
            road.length = tempObject.getDouble("value") / 1000;

            road.route = decodePolyline(route.getJSONObject("overview_polyline").getString("points"));

            road.description = leg.getJSONObject("distance").getString("text") + " " + leg.getJSONObject("duration").getString("text");
        } catch (JSONException e) {
            Log.e(e.getMessage());
        }

        return road;
    }

    /**
     * Decodes polyline from GoogleDirections JSON
     * Black magic. Do not touch
     *
     * @param encoded encoded polyline
     * @return list of geo points from polyline
     */
    private static ArrayList<GeoPoint> decodePolyline(CharSequence encoded) {

        final ArrayList<GeoPoint> poly = new ArrayList<GeoPoint>();
        int index = 0;
        final int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
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
            final int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            final GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
                    (int) (((double) lng / 1E5) * 1E6));
            poly.add(p);
        }

        return poly;
    }

}
