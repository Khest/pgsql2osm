package no.hbv.pgsql2osm;

import java.io.IOException;
import java.io.InputStream;
import java.text.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by Knut Johan Hesten on 2016-02-25.
 * Last updated by Knut Johan Hesten on 2016-06-08
 */
class Const {
    private static String           isoDateTime;
    private static Double           maxNodes;
    private static DecimalFormat    df;

    static final String             SQL_INTEGER =  "java.lang.Integer";
    static final String             SQL_DECIMAL = "java.math.BigDecimal";
    static final String             SQL_STRING = "java.lang.String";
    static final String             SQL_GEOMETRY = "org.postgis.PGgeometry";

    static final String             OSM_VERSION = Const.getProperty("osmversion");
    static final String             GENERATOR = Const.getProperty("generator");
    static final String             VERSION = Const.getProperty("pgsql2osmver");
    static final String             COPYRIGHT = Const.getProperty("creator");

    static final String             TEMP_FILE_WAYS = "ways.tmp";
    static final String             TEMP_FILE_NODES = "nodes.tmp";
    static final String             XML_FILE_NAME = "map.xml";

    static final int                NODE = 0;
    static final int                WAY = 1;
    static final int                XML = 2;
    private static final int        MAXROWCOUNT = 100000;
    static final String             MAIN_TAG_VALUE = "drawable";
    private static final String     REPLACE_AMPERSAND = " og ";
    private static final String     REPLACE_SLASH = "";
    private static final String     REPLACE_LESS_MORE = "";
    private static final String     REPLACE_INVALID_SYMBOL = "";
    private static final String     WARNING_NO_TEXT = "String error";

    private static final int        ARRAYSIZE = 1600000;
    private static final double     MAXPOSITIVELATLON = 180.0;
    private static final double     MAXNEGATIVELATLON = -180.0;

    static boolean                  TREAT_DECIMAL_AS_INTEGER = true;

    private Const() {
        LocalDateTime date = LocalDateTime.now();
        isoDateTime = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
        maxNodes = Double.valueOf(Const.getProperty("maxnodes"));
        df = new DecimalFormat("##.########", new DecimalFormatSymbols(Locale.US));
    }

    static String newLine() {
        return "\n";
    }

    static String tabCharacter() {
        return "\t";
    }

    static String cleanString(String s) {
        s = s
                .replaceAll("\"", Const.REPLACE_SLASH)
                .replaceAll("/", Const.REPLACE_SLASH)
                .replaceAll("&", Const.REPLACE_AMPERSAND)
                .replaceAll(">", Const.REPLACE_LESS_MORE)
                .replaceAll("<", Const.REPLACE_LESS_MORE)
                .replaceAll("\\W", Const.REPLACE_INVALID_SYMBOL)
                .trim()
                ;
        if (s.length() == 0) {
            return Const.WARNING_NO_TEXT;
        }
        return s;
    }

    private static String getProperty(String propertyName) {
        String filename = "config.properties";
        Properties prop = new Properties();
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream is = Const.class.getResourceAsStream(filename);
            prop.load(is);
            return prop.getProperty(propertyName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static Double getMaxNodes() {
        return maxNodes;
    }

    static String getTimeStamp() {
        return isoDateTime;
    }

    static DecimalFormat getDf() {
        return df;
    }
}
