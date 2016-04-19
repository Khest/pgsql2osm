package no.hbv.pgsql2osm;

import org.postgis.PGgeometry;

import java.util.*;

/**
 * Created by Knut Johan Hesten on 2016-02-25.
 */
public class Feature extends GeomPoints {
    //TODO: Beholde referanse mellom to punkt dersom de danner en uavbrutt linje, men finnes i to ulike rader
    private StringBuilder nodes;
    private StringBuilder ways;
    private static String tmpInput;

//    private OsmCategory osmCategory;
    private String drawableTag;
    private Queue<String> categories;
    private Queue<String> nodeReferences;

    Feature(String drawableTag) {
//        this.osmCategory = cat;
        this.drawableTag = drawableTag;
        this.nodes = new StringBuilder();
        this.ways = new StringBuilder();

        this.categories = new ArrayDeque<>();
        this.nodeReferences = new ArrayDeque<>();
    }

    public void addCategory(Object value) {
        if (value.getClass() == String.class) {
            this.categories.add(Const.cleanString(value.toString()));
        } else {
            this.categories.add(value.toString());
        }
    }

    public StringBuilder getNodes() {
        return this.nodes;
    }

    public StringBuilder getWays() {
        return this.ways;
    }

    public String getDrawableTag() {return this.drawableTag; }

    public String getTagValue() {return Const.MAINTAGVALUE; }

    /**
     * Generates XML based on geometry and tag data herein.
     * Only call after the values have been completely loaded.
     */
    public void generateXml() {
        for (int i = 0; i < super.count(); i++) {
            this.nodes
                    .append(node)
                    .append(__)
                    .append(id).append(qt).append(super.getNodeId()).append(qt)
                    .append(__)
                    .append(lat).append(qt).append(super.getNodeLat(i)).append(qt)
                    .append(__)
                    .append(lon).append(qt).append(super.getNodeLon(i)).append(qt)
                    .append(__)
                    .append(version)
                    .append(__)
                    .append(timestamp).append(qt).append(Const.getTimeStamp()).append(qt)
            ;
            if (super.isSinglePolygon()) {
                //Single point, so we append tags and close the node
                this.nodes
                        .append(end)
                        .append(Const.newLine())
                        .append(generateTags())
                        .append(nodeEnd)
                        .append(Const.newLine())
                ;
            } else {
                //Multipolygon, so add this node to the node reference table
                this.nodes.append(slash).append(end).append(Const.newLine());
                this.nodeReferences.add(super.getNodeId());
            }
            super.incrementNodeId();
        }
        //If multipolygon we create the way tag for the node collection and add the reference table as well as tags
        if (!super.isSinglePolygon()) {
            int totalRefCount = nodeReferences.size();
            int passes = Double.valueOf(Math.ceil(totalRefCount / Const.getMaxNodes())).intValue();
                for (int x = 0; x < passes; x++) {
                    int refCount = nodeReferences.size();
                    if (refCount > Const.getMaxNodes()) refCount = Const.getMaxNodes().intValue();
                    this.ways
                            .append(way)
                            .append(__)
                            .append(id).append(qt).append(super.getWayId()).append(qt)
                            .append(__)
                            .append(version)
                            .append(__)
                            .append(timestamp).append(qt).append(Const.getTimeStamp()).append(qt)
                            .append(end).append(Const.newLine());
                    for (int y = 0; y < refCount; y++) {
                        this.ways
                                .append(nd)
                                .append(__)
                                .append(ref).append(qt).append(nodeReferences.remove()).append(qt)
                                .append(slash).append(end)
                                .append(Const.newLine())
                        ;
                    }
                    this.ways
                            .append(generateTags())
                            .append(wayEnd)
                            .append(Const.newLine())
                    ;
                }
        }
    }

    private StringBuilder generateTags() {
        //TODO: loop through available tags
        StringBuilder tags = new StringBuilder();
        //First get main k v pair
        tags
                .append(tag)
                .append(__)
//                .append(k).append(qt).append(this.osmCategory.getIdKVPair()[0]).append(qt)
                .append(k).append(qt).append(this.drawableTag).append(qt)
                .append(__)
//                .append(v).append(qt).append(this.osmCategory.getIdKVPair()[1]).append(qt)
                .append(v).append(qt).append(this.getTagValue()).append(qt)
                .append(slash).append(end)
                .append(Const.newLine())
        ;
        while (!categories.isEmpty()) {
            tags.append(tag)
                    .append(__)
                    .append(k).append(qt).append(categories.remove()).append(qt)
                    .append(__)
                    .append(v).append(qt).append(categories.remove()).append(qt)
                    .append(slash).append(end)
                    .append(Const.newLine())
            ;
        }
        return tags;
    }

    private static final String __ = " ";
    private static final String node = "<node";
    private static final String nodeEnd = "</node>";
    private static final String id = "id=";
    private static final String lat = "lat=";
    private static final String lon = "lon=";
    private static final String visible = "visible=\"true\"";
    private static final String slash = "/";
    private static final String end = ">";
    private static final String tag = "<tag";
    private static final String way = "<way";
    private static final String wayEnd = "</way>";
    private static final String nd = "<nd";
    private static final String ref = "ref=";
    private static final String qt = "\"";
    private static final String k = "k=";
    private static final String v = "v=";
    private static final String version = "version=\"1\"";
    private static final String timestamp = "timestamp=";
}

abstract class GeomPoints {
    private Long nodeId;
    private PGgeometry geom;

    GeomPoints() {}

    public String getNodeLat(int position) {
        //return Const.getDf().format(Mercator.yToLat(geom.getGeometry().getPoint(position).getY()));
        return Const.getDf().format(geom.getGeometry().getPoint(position).getY());
    }

    public String getNodeLon(int position) {
        //return Const.getDf().format(Mercator.xToLon(geom.getGeometry().getPoint(position).getX()));
        return Const.getDf().format(geom.getGeometry().getPoint(position).getX());
    }

    public int count() {
        return geom.getGeometry().numPoints();
    }

    public boolean isSinglePolygon() {
        return this.count() == 1;
    }

    public String getNodeId() {
        return String.valueOf(nodeId);
    }

    public void incrementNodeId() {
        this.nodeId = GeomHelper.getAndIncrementNodeId();
    }

    public String getWayId() {
        return String.valueOf(GeomHelper.getAndIncrementWayId());
    }

    public void setGeometry(PGgeometry geom) {
        this.nodeId = GeomHelper.getAndIncrementNodeId();
        this.geom = geom;
    }
}