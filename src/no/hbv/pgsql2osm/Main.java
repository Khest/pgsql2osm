package no.hbv.pgsql2osm;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;

/**
 *  pgsql2osm, a tool to convert a database containing geometric data to an osm file
 * Copyright (C) 2016  Knut Johan Hesten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */

public class Main {

    private static final String ERR_NO_SQL_DRIVER =     "Could not load postgresql driver";
    private static final String ERR_GENERAL_SQL_ERROR = "SQL error";

    public static void main(String[] args) {
        Main main = new Main();
        main.commandParser(args);
    }

    private void commandParser(String[] args) {
//        try (OsmWriter osmWriter = new OsmWriter("output.osm")){

        //TODO: CHECK if there exists tool that does what this does

            //Todo handles input and sends messages further up the event chain


            //TODO replace with input values


            //TODO get list of schemas

        //TODO check if output is write to osm - ALWAYS write to osm
        //TODO get input filename OR get default if not set

        //TODO: Keep reference between two sets of points if they are part of a unbroken linestring


        // TODO: Future ideas:
        // TODO: specify schemas and exclude schemas
        // TODO: specify tables and exclude tables (mutually exclusive option with previous
        // TODO: hook into mapsforge-map-writer to write directly to .map file
        try {
            dbConn dbConn = new dbConn();
            Connection conn = dbConn.getConnection("localhost", "test", "postgres", " "); //NEED TO SET PASSWORD

            String schemaName = "kombinert_andre";
            String fileName = "output.osm";

            Schemas schemas = new Schemas(conn, schemaName);

            System.out.printf("Current schema ").printf(schemaName).println();
            try (OsmWriter osmWriter = new OsmWriter(fileName)) {
                Instant start, end, tblStart, tblEnd;
                start = Instant.now();
                while (!schemas.empty()) {
                    tblStart = Instant.now();
                    Tables clt = new Tables();
                    clt.getTable(conn, schemaName, schemas.pop(), osmWriter);

                    System.gc();

                    tblEnd = Instant.now();
                    String message = "Table " + schemas.count() + " of " + schemas.total() + " completed in " + Duration.between(tblStart, tblEnd).toString() + Const.newLine();
                    System.out.printf(message);
                }
                end = Instant.now();
                String message = "Operation completed in " + Duration.between(start, end).toString() + Const.newLine();
                System.out.printf(message);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }



    }

    void parseCommand() {

    }


    void getTagCategory() {

    }

    void getTagValue() {

    }

    private class dbConn {
        Connection getConnection(String address, String dbName, String userName, String password) {
            try {
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection("jdbc:postgresql://" + address + "/" + dbName, userName, password);
            } catch (ClassNotFoundException e) {
                System.out.println(ERR_NO_SQL_DRIVER);
            } catch (SQLException e) {
                System.out.println(ERR_GENERAL_SQL_ERROR);
            }
            return null;
        }
    }

    private Arguments decodeCommands(String[] args) throws Exception{
        // TODO: 2016-06-08 https://commons.apache.org/proper/commons-cli/usage.html
        // TODO: 2016-06-08 ALTERNATIVE: http://jcommander.org/
        // TODO: 2016-06-08 address, database, username, password, schema name, help text
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
                    case "--help":
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
        return ar;
    }

    private void printHelp() {
        String helpText =
                "pgsql2osm version " + Const.VERSION + " Created by " + Const.COPYRIGHT + Const.newLine()

                ;
    }
}
