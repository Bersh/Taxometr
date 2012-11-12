package ua.com.taxometr.mapOverlays;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import ua.com.taxometr.routes.Road;

import java.math.BigDecimal;
import java.util.ArrayList;

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
     *
     * @param road {@link ua.com.taxometr.routes.Road}
     * @param mv   current {@link com.google.android.maps.MapView}
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public RouteOverlay(Road road, MapView mv) {
        this.road = road;
        if (!road.route.isEmpty()) {
            points = road.route;

            final GeoPoint startPoint = points.get(0);

            final MapController mapController = mv.getController();
            mapController.animateTo(startPoint);
        }
    }

    /**
     * Rounds given double to given precision
     *
     * @param unrounded    unrounded double value
     * @param precision    precision
     * @param roundingMode rounding mode {@link java.math.BigDecimal#ROUND_HALF_UP}
     * @return rounded value
     */
    private static double round(double unrounded, int precision, int roundingMode) {
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
     *
     * @param mv     current {@link com.google.android.maps.MapView}
     * @param canvas canvas
     */
    public void drawRoute(MapView mv, Canvas canvas) {
        if (points.isEmpty()) {
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

