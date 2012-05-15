package ua.com.taxometr.routes;

/**
 * Representation of route
 * @author ibershadskiy <a href="mailto:iBersh20@gmail.com">Ilya Bershadskiy</a>
 * @since 26.04.12
 */
@SuppressWarnings("ClassNamingConvention")
public class Road {
    /**
     * Road name
     */
    public String name;

    /**
     * Road description
     */
    public String description;

    /**
     * Line color
     */
    public int color;

    /**
     * Line width
     */
    public int width;

    /**
     * Route points
     */
    public double[][] route = new double[][]{};

    /**
     *  HZ
     */
    public Point[] points = new Point[]{};
}
