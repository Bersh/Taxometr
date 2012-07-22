package ua.com.taxometr.mapOverlays;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import ua.com.taxometr.helpers.LocationHelper;
import ua.com.taxometr.routes.Road;

import java.math.BigDecimal;
import java.util.ArrayList;

import static java.lang.Math.*;

/**
 * Map overlay for displaying route
 *
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 08.05.12
 */
public class RouteOverlay extends Overlay {
    private static final int EARTH_RADIUS = 6371;
    Road road;
    ArrayList<GeoPoint> points;

    /**
     * Constructor for route map overlay
     * @param road {@link ua.com.taxometr.routes.Road}
     * @param mv current {@link com.google.android.maps.MapView}
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public RouteOverlay(Road road, MapView mv) {
        this.road = road;
        if (road.route.length > 0) {
            points = new ArrayList<GeoPoint>();
            for (int i = 0; i < road.route.length; i++) {
                points.add(new GeoPoint((int) (road.route[i][1] * LocationHelper.MILLION),
                        (int) (road.route[i][0] * LocationHelper.MILLION)));
            }

            double length = 0;
            for(int i = 0; i < (points.size() - 1); ++i) {
                final double lat1 = toRadians(points.get(i).getLatitudeE6() / LocationHelper.MILLION);
                final double lat2 = toRadians(points.get(i + 1).getLatitudeE6() / LocationHelper.MILLION);
                final double lon1 = toRadians(points.get(i).getLongitudeE6() / LocationHelper.MILLION);
                final double lon2 = toRadians(points.get(i + 1).getLongitudeE6() / LocationHelper.MILLION);

                length += acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2)
                        * cos(lon2 - lon1)) * EARTH_RADIUS;
            }
            road.length = round(length, 2, BigDecimal.ROUND_HALF_UP);
            final GeoPoint startPoint = new GeoPoint(points.get(0).getLatitudeE6(), points.get(0).getLongitudeE6());

            final MapController mapController = mv.getController();
            mapController.animateTo(startPoint);
        }
    }

    /**
     * Rounds given double to given precision
     * @param unrounded unrounded double value
     * @param precision precision
     * @param roundingMode  rounding mode {@link java.math.BigDecimal#ROUND_HALF_UP}
     * @return rounded value
     */
    private static double round(double unrounded, int precision, int roundingMode)
    {
        final BigDecimal bd = new BigDecimal(unrounded);
        final BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    @Override
    public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
        super.draw(canvas, mv, shadow);
        drawRoute(mv, canvas);
        return true;
    }

    /**
     * Draws route on map's canvas
     * @param mv current {@link com.google.android.maps.MapView}
     * @param canvas canvas
     */
    public void drawRoute(MapView mv, Canvas canvas) {
        if(points.isEmpty()) {
            return;
        }
        final Paint linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3);
        final Paint pointPaint = new Paint();
        pointPaint.setColor(Color.GREEN);
        pointPaint.setStyle(Paint.Style.FILL);
        int x1 = -1;
        int y1 = -1;
        int x2 = -1;
        int y2 = -1;
        for (int i = 0; i < points.size(); i++) {
            final Point point = new Point();
            mv.getProjection().toPixels(points.get(i), point);
            x2 = point.x;
            y2 = point.y;
            if (i > 0) {
                canvas.drawCircle(x1, y1, 5, pointPaint);
                canvas.drawLine(x1, y1, x2, y2, linePaint);
            }
            x1 = x2;
            y1 = y2;
        }
    }
}

