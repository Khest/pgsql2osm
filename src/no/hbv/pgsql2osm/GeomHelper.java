package no.hbv.pgsql2osm;

import org.postgis.Geometry;
import org.postgis.PGgeometry;

/**
 * Created by Knut Johan Hesten on 2016-02-25.
 */
class GeomHelper {
    static private Double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = 0.0, maxY = 0.0;
    static private long idNode = 0;
    static private long idWay = 128000000;
    static protected void setMinX(Double minX) {
        if (GeomHelper.minX > minX) {
            GeomHelper.minX = minX;
        }
    }
    static protected void setMinY(Double minY) {
        if (GeomHelper.minY > minY) {
            GeomHelper.minY = minY;
        }
    }
    static protected void setMaxX(Double maxX) {
        if (GeomHelper.maxX < maxX) {
            GeomHelper.maxX = maxX;
        }
    }
    static protected void setMaxY(Double maxY) {
        if (GeomHelper.maxY < maxY) {
            GeomHelper.maxY = maxY;
        }
    }

//    public static String getMinX() { return Const.getDf().format(Mercator.xToLon(minX)); }
//    public static String getMinY() { return Const.getDf().format(Mercator.yToLat(minY)); }
//    public static String getMaxX() { return Const.getDf().format(Mercator.xToLon(maxX)); }
//    public static String getMaxY() { return Const.getDf().format(Mercator.yToLat(maxY)); }
    public static String getMinX() { return Const.getDf().format(minX); }
    public static String getMinY() { return Const.getDf().format(minY); }
    public static String getMaxX() { return Const.getDf().format(maxX); }
    public static String getMaxY() { return Const.getDf().format(maxY); }

    static protected long getAndIncrementNodeId() {
        return idNode++;
    }

    static protected long getAndIncrementWayId() {
        return idWay++;
    }
}
