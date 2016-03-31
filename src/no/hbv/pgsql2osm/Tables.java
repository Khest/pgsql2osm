package no.hbv.pgsql2osm;

import org.postgis.PGgeometry;

import java.io.IOException;
import java.sql.*;
import java.util.Stack;

/**
 * Created by Knut Johan Hesten on 2016-02-26.
 */
public class Tables {
//    private StringBuilder nodes;
//    private StringBuilder ways;
//    Stack<String> tableList;

    Tables() throws SQLException { //Connection conn, String schemaName, String tableName
//        this.nodes = new StringBuilder();
//        this.ways = new StringBuilder();

//        this.tableList = getListOfTableNames(conn, schemaName);
    }

    void setMinMaxGeometryBounds(Connection conn, String schemaName, String tableName, String geomColumn) throws SQLException {
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
//                OsmCategory osmCategory;
//                try {
//                    osmCategory = OsmCategory.valueOf(metaData.getTableName(1).toUpperCase());
//                } catch (Exception ex) {
//                    osmCategory = OsmCategory.DEFAULT;
//                }
                int currentRowNumber = 0;
                int colCount = metaData.getColumnCount();

                while (rs.next()) {
                    Feature ft = new Feature(schemaName + "_" + tableName);
                    currentRowNumber++;
                    System.out.print("\r");
                    System.out.print(currentRowNumber);
                    System.out.print(" / ");
                    System.out.print(rowCount);
                    for (int i = 1; i <= colCount; i++) {
                        switch (metaData.getColumnClassName(i)) {
                            case Const.SQLINTEGER:
                                //TODO identify Integer and see if it is useful
                                break;
                            case Const.SQLDECIMAL:
                                //TODO identify double/BigDecimal and see if it is useful
                                if (rs.getBigDecimal(i) != null) {
                                    ft.addCategory(metaData.getColumnLabel(i));
                                    ft.addCategory(rs.getBigDecimal(i));
                                }
//                                for (int j = 1; j < osmCategory.getNumCat(); j++) {
//
//                                    if (osmCategory.getCategories()[j].equals(metaData.getColumnName(i))) {
//                                        ft.addCategory(osmCategory.getCategories()[j]);
//                                        ft.addCategory(rs.getBigDecimal(i));
//                                    }
//                                }
                                break;
                            case Const.SQLSTRING:
                                //TODO identify String and see if it is useful
                                if (rs.getString(i) != null) {
                                    ft.addCategory(metaData.getColumnLabel(i));
                                    ft.addCategory(rs.getString(i));
                                }
//                                for (int j = 1; j < osmCategory.getNumCat(); j++) {
//                                    if (osmCategory.getCategories()[j].equals(metaData.getColumnName(i))) {
//                                        ft.addCategory(osmCategory.getCategories()[j]);
//                                        ft.addCategory(rs.getString(i));
//                                    }
//                                }
                                break;
                            case Const.SQLGEOM:
                                if (geomColumnName.length() == 0)
                                    geomColumnName = metaData.getColumnName(i);
                                ft.setGeometry((PGgeometry) rs.getObject(i));
                                break;
                            default:
                                break;
                        }
                    }
                    ft.generateXml();

                    osmWriter.writeBuffered(ft.getNodes(), Const.NODE);
                    osmWriter.writeBuffered(ft.getWays(), Const.WAY);

//                    osmWriter.writeOsmToDisk(ft.getNodes(), Const.NODE);
//                    osmWriter.writeOsmToDisk(ft.getWays(), Const.WAY);

//                    this.nodes.append(ft.getNodes());
//                    this.ways.append(ft.getWays());
//                    if (currentRowNumber >= Const.MAXROWCOUNT) {
//                        osmWriter.writeOsmToDisk(this.getNodes(), Const.NODE);
//                        osmWriter.writeOsmToDisk(this.getWays(), Const.WAY);
//                        this.nodes = new StringBuilder();
//                        this.ways = new StringBuilder();
//                        System.out.println();
//                        currentRowNumber = 0;
//                        rowCount -= Const.MAXROWCOUNT;
//                    } else if (currentRowNumber == rowCount){
//                        //TODO Write directly to file
//                        osmWriter.writeOsmToDisk(this.getNodes(), Const.NODE);
//                        osmWriter.writeOsmToDisk(this.getWays(), Const.WAY);
//                        System.out.println("End of table");
//                    }

//                    if (nodes.length() >= (Integer.MAX_VALUE - 20000) || ways.length() >= (Integer.MAX_VALUE - 20000))
//                        System.out.println("ways/nodes length warning");

                }

            }
        }
        setMinMaxGeometryBounds(conn, schemaName, tableName, geomColumnName);
        System.out.println();
    }

//    public Stack<String> getListOfTableNames() {
//        return this.tableList;
//    }
//
//    private Stack<String> getListOfTableNames(Connection conn, String schemaName) throws SQLException {
//        String sql = "SELECT table_name" +
//                " FROM information_schema.tables" +
//                " WHERE table_schema='" + schemaName + "'" +
//                " AND table_type='BASE TABLE';";
//        PreparedStatement stmt = conn.prepareStatement(sql);
//        ResultSet tablesRS = stmt.executeQuery();
//        Stack<String> tablesStack = new Stack<>();
//        while (tablesRS.next()) {
//            tablesStack.push(tablesRS.getString("table_name"));
//        }
//        return tablesStack;
//    }


//    public StringBuilder getNodes() {
//        return this.nodes;
//    }
//
//    public StringBuilder getWays() {
//        return this.ways;
//    }
//
//    public void cleanup() {
//        this.ways = new StringBuilder();
//        this.nodes = new StringBuilder();
//    }
}