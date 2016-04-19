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

    public static Double getMinX() { return Mercator.xToLon(minX); }
    public static Double getMinY() { return Mercator.yToLat(minY); }
    public static Double getMaxX() { return Mercator.xToLon(maxX); }
    public static Double getMaxY() { return Mercator.yToLat(maxY); }

    static protected long getAndIncrementNodeId() {
        return idNode++;
    }

    static protected long getAndIncrementWayId() {
        return idWay++;
    }
}
