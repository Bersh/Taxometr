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
    Road road;
    boolean isPlacemark;
    boolean isRoute;
    boolean isItemIcon;
    private final Stack currentElement = new Stack();
    private String tmpString;

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
            if ("name".equalsIgnoreCase(localName)) {
                if (isPlacemark) {
                    isRoute = "Route".equalsIgnoreCase(tmpString);
                    if (!isRoute) {
                        road.points[road.points.length - 1].name = tmpString;
                    }
                } else {
                    road.name = tmpString;
                }
            } else if ("color".equalsIgnoreCase(localName) && !isPlacemark) {
                road.color = Integer.parseInt(tmpString, 16);
            } else if ("width".equalsIgnoreCase(localName) && !isPlacemark) {
                road.width = Integer.parseInt(tmpString);
            } else if ("description".equalsIgnoreCase(localName)) {
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
                        String[] xyParsed = split(tmpString, ",");
                        double lon = Double.parseDouble(xyParsed[0]);
                        double lat = Double.parseDouble(xyParsed[1]);
                        road.points[road.points.length - 1].latitude = lat;
                        road.points[road.points.length - 1].longitude = lon;
                    } else {
                        String[] coodrinatesParsed = split(tmpString, " ");
                        int lenNew = coodrinatesParsed.length;
                        int lenOld = road.route.length;
                        double[][] temp = new double[lenOld + lenNew][2];
                        for (int i = 0; i < lenOld; i++) {
                            temp[i] = road.route[i];
                        }
                        for (int i = 0; i < lenNew; i++) {
                            String[] xyParsed = split(coodrinatesParsed[i], ",");
                            for (int j = 0; j < 2 && j < xyParsed.length; j++) {
                                temp[lenOld + i][j] = Double
                                        .parseDouble(xyParsed[j]);
                            }
                        }
                        road.route = temp;
                    }
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

    private String cleanup(String value) {
        String remove = "<br/>";
        int index = value.indexOf(remove);
        if (index != -1)
            value = value.substring(0, index);
        remove = "&#160;";
        index = value.indexOf(remove);
        int len = remove.length();
        while (index != -1) {
            value = value.substring(0, index).concat(
                    value.substring(index + len, value.length()));
            index = value.indexOf(remove);
        }
        return value;
    }

    public Point[] addPoint(Point[] points) {
        Point[] result = new Point[points.length + 1];
        for (int i = 0; i < points.length; i++)
            result[i] = points[i];
        result[points.length] = new Point();
        return result;
    }

    private static String[] split(String strString, String strDelimiter) {
        String[] strArray;
        int iOccurrences = 0;
        int iIndexOfInnerString = 0;
        int iIndexOfDelimiter = 0;
        int iCounter = 0;
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
        while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
                iIndexOfInnerString)) != -1) {
            iOccurrences += 1;
            iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
        }
        strArray = new String[iOccurrences];
        iIndexOfInnerString = 0;
        iIndexOfDelimiter = 0;
        while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
                iIndexOfInnerString)) != -1) {
            strArray[iCounter] = strString.substring(iIndexOfInnerString,
                    iIndexOfDelimiter);
            iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
            iCounter += 1;
        }

        return strArray;
    }
}