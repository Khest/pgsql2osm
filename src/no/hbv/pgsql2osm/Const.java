package no.hbv.pgsql2osm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by Knut Johan Hesten on 2016-02-25.
 */
public class Const {
    private static Const ourInstance = new Const();
    private static String isoDateTime;
    private static Double maxNodes;
    private static DecimalFormat df;

    public static final String SQLINTEGER =  "java.lang.Integer";
    public static final String SQLDECIMAL = "java.math.BigDecimal";
    public static final String SQLSTRING = "java.lang.String";
    public static final String SQLGEOM = "org.postgis.PGgeometry";

    public static final String OSMVERSION = Const.getProperty("osmversion");
    public static final String GENERATOR = Const.getProperty("generator");
    public static final String PGSQL2OSMVER = Const.getProperty("pgsql2osmver");
    public static final String COPYRIGHT = Const.getProperty("copyright");

    public static final int NODE = 0;
    public static final int WAY = 1;
    public static final int MAXROWCOUNT = 100000;
    public static final String MAINTAGVALUE = "test";

    public static final int ARRAYSIZE = 1600000;

    public static Const getInstance() {
        return ourInstance;
    }

    private Const() {
        LocalDateTime date = LocalDateTime.now();
        isoDateTime = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
        maxNodes = Double.valueOf(Const.getProperty("maxnodes"));
        df = new DecimalFormat("#.00000", new DecimalFormatSymbols(Locale.US));
    }

    public static String newLine() {
//        return System.getProperty("line.separator");
        return "\n";
    }

    public static String singleSpace() {
        return " ";
    }

    public static String getProperty(String propertyName) {
        String filename = "config.properties";
        Properties prop = new Properties();
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream is = Const.class.getResourceAsStream(filename);
            prop.load(is);
//            InputStream input = Const.class.getClassLoader().getResourceAsStream(filename);
//            prop.load(input);
            return prop.getProperty(propertyName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Double getMaxNodes() {
        return maxNodes;
    }

    public static String getTimeStamp() {
        return isoDateTime;
    }

    public static DecimalFormat getDf() {
        return df;
    }
}