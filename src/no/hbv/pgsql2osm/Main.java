package no.hbv.pgsql2osm;

import java.sql.*;

public class Main {


    // list of <node with id lat lon />
    // list of <node with id lat lon > - for point
    //  inside list of <tag k="category" v="value"/>
    // </node>
    // list of <way with id> - represents continuous lines
    //  list of <nd ref="node id"> inside <way> - for each node in line
    //  after, <tag k="category" v="value"/>
    // </way>
    public static void main(String[] args) {
        Main main = new Main();
        main.commandParser();
    }

    void commandParser() {
//        try (OsmWriter osmWriter = new OsmWriter("output.osm")){


            //Todo handles input and sends messages further up the event chain


            //TODO replace with input values


            //TODO get list of schemas
        try {
            dbConn dbConn = new dbConn();
            Connection conn = dbConn.getConnection("localhost", "test", "postgres", "lacream");
            String schemaName = "stedsnavn";
            String fileName = "output.osm";
            Schemas schemas = new Schemas(conn, schemaName);

            System.out.printf("Current schema ").printf(schemaName).println();
            try (OsmWriter osmWriter = new OsmWriter(fileName)) {
                while (!schemas.getListOfTableNames().empty()) {
//                    OsmWriter2 osmWriter = new OsmWriter2(fileName);
                    Tables clt = new Tables();
                    clt.getTable(conn, schemaName, schemas.getListOfTableNames().pop(), osmWriter);
//                    osmWriter.writeOsmToDisk(clt.getNodes(), Const.NODE);
//                    osmWriter.writeOsmToDisk(clt.getWays(), Const.WAY);
//                    if (osmWriter.sbLength()) System.out.println("sb length warning");
                    System.gc();
                }
//            OsmWriter2 osmWriter2 = new OsmWriter2(fileName);
//            osmWriter2.writeEOF();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

//            Tables clt = new Tables(conn, schemaName);
//            System.out.printf("Current schema ").printf(schemaName).println();
//            while (!clt.getListOfTableNames().empty()) {
//                clt.getTable(conn, schemaName, clt.getListOfTableNames().pop(), osmWriter);
//                osmWriter.writeOsmToDisk(clt.getNodes(), Const.NODE);
//                osmWriter.writeOsmToDisk(clt.getWays(), Const.WAY);
//                clt.cleanup();
//            }
            //TODO check if output is write to osm - ALWAYS write to osm
            //TODO get input filename OR get default if not set
//        } catch (SQLException sqlEx) {
//            System.out.printf(sqlEx.getMessage()).println();
//        } catch (Exception ex) {
//            System.out.printf(ex.getMessage()).println();
//        }

    }

    void parseCommand() {

    }


    void getTagCategory() {

    }

    void getTagValue() {

    }

    public class dbConn {
        public Connection getConnection(String address, String dbName, String userName, String password) {
            try {
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection("jdbc:postgresql://" + address + "/" + dbName, userName, password);
            } catch (ClassNotFoundException e) {
                System.out.println("Could not load postgresql driver");
            } catch (SQLException e) {
                System.out.println("SQL error");
            }
            return null;
        }
    }

    private Arguments decodeCommands(String[] args) throws Exception{
        if (args.length == 0) {
            printHelp();
        }
        Arguments ar = new Arguments();
        try {
            for (int i = 0; i > args.length; i += 2) {
                switch (args[i]) {
                    case "-a":
                        ar.setAddress(args[i + 1]);
                        break;
                    case "-d":
                        ar.setDb(args[i + 1]);
                        break;
                    case "-u":
                        ar.setUserName(args[i + 1]);
                        break;
                    case "-p":
                        ar.setPassword(args[i + 1]);
                        break;
                    case "-s":
                        ar.setSchemaName(args[i + 1]);
                        break;
                    case "-h":
                        printHelp();
                        break;
                    //TODO: Invalid argument handling
                }
            }
            if (args.length == 0) {
                printHelp();
            } else {
                return ar;
            }
        } catch (IllegalArgumentException ex) {
            printHelp();
        }
        return null;
    }

    private void printHelp() {
        String helpText =
                "pgsql2osm version " + Const.PGSQL2OSMVER + " Created by " + Const.COPYRIGHT + Const.newLine()

                ;
    }
}
