package no.hbv.pgsql2osm;

import org.postgis.PGgeometry;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by Knut Johan Hesten on 2016-02-26.
 * Updated by Knut Johan Hesten on 2016-06-20
 */
class Tables {
    private static final String pois = "<pois>";
    private static final String poisEnd = "</pois>";
    private static final String ways = "<ways>";
    private static final String waysEnd = "</ways>";
    private static final String osmTag = "<osm-tag";
    private static final String key = "key=";
    private static final String value = "value=";
    private static final String zoom = "zoom-appear=";
    private static final String qt = "\"";
    private static final String slash = "/";
    private static final String end = ">";
    private static final String __ = " ";

    private boolean xmlTaggingDone = false;
    private boolean isSinglePolygon;
    private Queue<String> xmlTags = new LinkedList<>();

    Tables() throws SQLException { //Connection conn, String schemaName, String tableName
//        this.nodes = new StringBuilder();
//        this.ways = new StringBuilder();

//        this.tableList = getListOfTableNames(conn, schemaName);
    }

    private void setMinMaxGeometryBounds(Connection conn, String schemaName, String tableName, String geomColumn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String sql =
                    "select max(st_xmax(" + geomColumn + ")), " +
                            "max(st_ymax(" + geomColumn + ")), " +
                            "min(st_xmin(" + geomColumn + ")), " +
                            "min(st_ymin(" + geomColumn + ")) " +
                            "from " + schemaName + "." + tableName;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                rs.next();
                GeomHelper.setMaxX(rs.getDouble(1));
                GeomHelper.setMaxY(rs.getDouble(2));
                GeomHelper.setMinX(rs.getDouble(3));
                GeomHelper.setMinY(rs.getDouble(4));
            }
        }
    }

    private void writeMapWriterXMLtags(OsmWriter osmWriter) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (isSinglePolygon) {
            sb.append(pois);
        } else {
            sb.append(ways);
        }
        sb.append(Const.newLine());
        sb.append(Const.tabCharacter()).append(osmTag)
                .append(__).append(key)  .append(qt).append(xmlTags.remove()).append(qt)
                .append(__).append(value).append(qt).append(xmlTags.remove()).append(qt)
                .append(__).append(zoom) .append(qt).append("0").append(qt)
                .append(__).append(slash).append(end)
                ;
        sb.append(Const.newLine());
        if (isSinglePolygon) {
            sb.append(poisEnd);
        } else {
            sb.append(waysEnd);
        }
        sb.append(Const.newLine());
        osmWriter.writeBuffered(sb, Const.XML);
    }

    void getTable(Connection conn, String schemaName, String tableName, OsmWriter osmWriter) throws SQLException, IOException {
        String schemaTableName = schemaName + "." + tableName;
        String sql = "SELECT COUNT(*) FROM " + schemaTableName;
        int rowCount;
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rsCount = stmt.executeQuery(sql)) {
                rsCount.next();
                rowCount = rsCount.getInt(1);
            }
        }
        String geomColumnName = "";
        try (Statement stmt = conn.createStatement()) {
            sql = "SELECT * FROM " + schemaTableName;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                ResultSetMetaData metaData = rs.getMetaData();
                System.out.printf(metaData.getTableName(1) + ": ").println();

                int currentRowNumber = 0;
                int colCount = metaData.getColumnCount();

                while (rs.next()) {
                    Feature ft = new Feature(tableName);
                    currentRowNumber++;
                    System.out.print("\r");
                    System.out.print(currentRowNumber);
                    System.out.print(" / ");
                    System.out.print(rowCount);
                    for (int i = 1; i <= colCount; i++) {
                        switch (metaData.getColumnClassName(i)) {
                            case Const.SQL_INTEGER:
                                //TODO identify Integer and see if it is useful
                                break;
                            case Const.SQL_DECIMAL:
                                if (rs.getBigDecimal(i) != null) {
                                    ft.addCategory(metaData.getColumnLabel(i));
                                    if (Const.TREAT_DECIMAL_AS_INTEGER) {
                                        ft.addCategory(Math.floor(rs.getBigDecimal(i).doubleValue()));
                                    } else {
                                        ft.addCategory(rs.getBigDecimal(i));
                                    }
                                }
                                break;
                            case Const.SQL_STRING:
                                if (rs.getString(i) != null) {
                                    ft.addCategory(metaData.getColumnLabel(i));
                                    ft.addCategory(rs.getString(i));
                                }
                                break;
                            case Const.SQL_GEOMETRY:
                                if (geomColumnName.length() == 0)
                                    geomColumnName = metaData.getColumnName(i);
                                ft.setGeometry((PGgeometry) rs.getObject(i));
                                break;
                            default:
                                System.out.printf("Warning: No valid datatype found for column " + metaData.getColumnName(i) + Const.newLine());
                                break;
                        }
                    }
                    ft.generateXml();
                    if (!xmlTaggingDone) {
                        this.isSinglePolygon = ft.isSinglePolygon();
                        this.xmlTags.add(ft.getDrawableTag());
                        this.xmlTags.add(ft.getTagValue());
                        this.xmlTaggingDone = true;
                    }
                    osmWriter.writeBuffered(ft.getNodes(), Const.NODE);
                    osmWriter.writeBuffered(ft.getWays(), Const.WAY);

                }

            }
        }
        this.setMinMaxGeometryBounds(conn, schemaName, tableName, geomColumnName);
        this.writeMapWriterXMLtags(osmWriter);
        System.out.println();
    }
}
