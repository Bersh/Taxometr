package ua.com.taxometr.routes;

/**
 * Point representation
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 26.04.12
 */
public class Point {
    String name;
    String description;
    String iconUrl;
    double latitude;
    double longitude;

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}