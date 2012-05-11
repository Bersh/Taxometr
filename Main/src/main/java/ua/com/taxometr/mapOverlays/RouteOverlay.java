package ua.com.taxometr.mapOverlays;

import java.util.ArrayList;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import ua.com.taxometr.helpers.LocationHelper;
import ua.com.taxometr.routes.Road;

/**
 * Map overlay for displaying route
 *
 * @author ibershadskiy <a href="mailto:Ilya.Bershadskiy@exigenservices.com">Ilya Bershadskiy</a>
 * @since 08.05.12
 */
public class RouteOverlay extends Overlay {
    Road road;
    ArrayList<GeoPoint> points;

    /**
     * Constructor for route map overlay
     * @param road {@link ua.com.taxometr.routes.Road}
     * @param mv current {@link com.google.android.maps.MapView}
     */
    public RouteOverlay(Road road, MapView mv) {
        this.road = road;
        if (road.route.length > 0) {
            points = new ArrayList<GeoPoint>();
            for (int i = 0; i < road.route.length; i++) {
                points.add(new GeoPoint((int) (road.route[i][1] * LocationHelper.MILLION),
                        (int) (road.route[i][0] * LocationHelper.MILLION)));
            }
/*            final int moveToLat = (points.get(0).getLatitudeE6() + (points.get(
                    points.size() - 1).getLatitudeE6() - points.get(0)
                    .getLatitudeE6()) / 2);
            final int moveToLong = (points.get(0).getLongitudeE6() + (points.get(
                    points.size() - 1).getLongitudeE6() - points.get(0)
                    .getLongitudeE6()) / 2);*/
            final GeoPoint moveTo = new GeoPoint(points.get(0).getLatitudeE6(), points.get(0).getLongitudeE6());

            final MapController mapController = mv.getController();
            mapController.animateTo(moveTo);
        }
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

