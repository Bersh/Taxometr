package ua.com.taxometr.routes;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author ibershadskiy <a href="mailto:Ilya.Bershadskiy@exigenservices.com">Ilya Bershadskiy</a>
 * @since 26.04.12
 */
public class KMLHandler extends DefaultHandler {
    private static final String ROUTE_TAG_EN = "Route";
    private static final String ROUTE_TAG_RU = "Маршрут";

    private final Road road;
    boolean isPlacemark;
    boolean isRoute;
    boolean isItemIcon;
    private String tmpString;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Stack<String> currentElement = new Stack<String>();

    /**
     * Default constructor
     */
    public KMLHandler() {
        road = new Road();
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        currentElement.push(localName);
        if ("Placemark".equalsIgnoreCase(localName)) {
            isPlacemark = true;
            road.points = addPoint(road.points);
        } else if ("ItemIcon".equalsIgnoreCase(localName)) {
            if (isPlacemark) {
                isItemIcon = true;
            }
        }
        tmpString = "";
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        final String chars = new String(ch, start, length).trim();
        tmpString = tmpString + chars;
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (tmpString.length() > 0) {
            if (!"name".equalsIgnoreCase(localName)) {
                if (!"color".equalsIgnoreCase(localName) || isPlacemark) {
                    if (!"width".equalsIgnoreCase(localName) || isPlacemark) {
                        if ("description".equalsIgnoreCase(localName)) {
                            if (isPlacemark) {
                                final String description = cleanup(tmpString);
                                if (!isRoute) {
                                    road.points[road.points.length - 1].description = description;
                                } else {
                                    road.description = description;
                                }
                            }
                        } else if ("href".equalsIgnoreCase(localName)) {
                            if (isItemIcon) {
                                road.points[road.points.length - 1].iconUrl = tmpString;
                            }
                        } else if ("coordinates".equalsIgnoreCase(localName)) {
                            if (isPlacemark) {
                                if (!isRoute) {
                                    final String[] xyParsed = split(tmpString, ",");
                                    road.points[road.points.length - 1].latitude = Double.parseDouble(xyParsed[1]);
                                    road.points[road.points.length - 1].longitude = Double.parseDouble(xyParsed[0]);
                                } else {
                                    final String[] coodrinatesParsed = split(tmpString, " ");
                                    final int lenNew = coodrinatesParsed.length;
                                    final int lenOld = road.route.length;
                                    final double[][] temp = new double[lenOld + lenNew][2];
                                    System.arraycopy(road.route, 0, temp, 0, lenOld);
                                    for (int i = 0; i < lenNew; i++) {
                                        final String[] xyParsed = split(coodrinatesParsed[i], ",");
                                        for (int j = 0; j < 2 && j < xyParsed.length; j++) {
                                            temp[lenOld + i][j] = Double
                                                    .parseDouble(xyParsed[j]);
                                        }
                                    }
                                    road.route = temp;
                                }
                            }
                        }
                    } else {
                        road.width = Integer.parseInt(tmpString);
                    }
                } else {
                    road.color = Integer.parseInt(tmpString, 16);
                }
            } else {
                if (isPlacemark) {
                    isRoute = ROUTE_TAG_EN.equalsIgnoreCase(tmpString) || ROUTE_TAG_RU.equalsIgnoreCase(tmpString);
                    if (!isRoute) {
                        road.points[road.points.length - 1].name = tmpString;
                    }
                } else {
                    road.name = tmpString;
                }
            }
        }
        currentElement.pop();
        if ("Placemark".equalsIgnoreCase(localName)) {
            isPlacemark = false;
            if (isRoute) {
                isRoute = false;
            }
        } else if ("ItemIcon".equalsIgnoreCase(localName)) {
            if (isItemIcon) {
                isItemIcon = false;
            }
        }
    }

    /**
     * Removes unnecessary elements in string
     * @param value string to clean up
     * @return string without elements
     */
    @SuppressWarnings({"ReuseOfLocalVariable", "AssignmentToMethodParameter"})
    private static String cleanup(String value) {
        String remove = "<br/>";
        int index = value.indexOf(remove);
        if (index != -1) {
            value = value.substring(0, index);
        }
        remove = "&#160;";
        index = value.indexOf(remove);
        final int len = remove.length();
        while (index != -1) {
            value = value.substring(0, index).concat(
                    value.substring(index + len, value.length()));
            index = value.indexOf(remove);
        }
        return value;
    }

    /**
     * Adds new route point
     * @param points array of route points
     * @return array of route points
     */
    public Point[] addPoint(Point[] points) {
        final Point[] result = new Point[points.length + 1];
        System.arraycopy(points, 0, result, 0, points.length);
        result[points.length] = new Point();
        return result;
    }

    /**
     * Split string using given string delimiter
     * @param strString  source string
     * @param strDelimiter string delimiter
     * @return source string splited for array of strings
     */
    @SuppressWarnings({"AssignmentToMethodParameter", "ReuseOfLocalVariable"})
    private static String[] split(String strString, String strDelimiter) {
        if (strString == null) {
            throw new IllegalArgumentException("Input string cannot be null.");
        }
        if (strDelimiter.length() <= 0 || strDelimiter == null) {
            throw new IllegalArgumentException(
                    "Delimeter cannot be null or empty.");
        }
        if (strString.startsWith(strDelimiter)) {
            strString = strString.substring(strDelimiter.length());
        }
        if (!strString.endsWith(strDelimiter)) {
            strString += strDelimiter;
        }
        int iOccurrences = 0;
        int iIndexOfInnerString = 0;
        int iIndexOfDelimiter = strString.indexOf(strDelimiter, iIndexOfInnerString);
        while (iIndexOfDelimiter != -1) {
            iOccurrences += 1;
            iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
            iIndexOfDelimiter = strString.indexOf(strDelimiter, iIndexOfInnerString);
        }
        final String[] strArray = new String[iOccurrences];
        iIndexOfInnerString = 0;
        iIndexOfDelimiter = 0;
        iIndexOfDelimiter = strString.indexOf(strDelimiter, iIndexOfInnerString);
        int iCounter = 0;
        while (iIndexOfDelimiter != -1) {
            strArray[iCounter] = strString.substring(iIndexOfInnerString,
                    iIndexOfDelimiter);
            iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
            iCounter += 1;
            iIndexOfDelimiter = strString.indexOf(strDelimiter, iIndexOfInnerString);
        }

        return strArray;
    }

    public Road getRoad() {
        return road;
    }
}