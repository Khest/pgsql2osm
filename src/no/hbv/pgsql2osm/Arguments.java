package no.hbv.pgsql2osm;

/**
 * Created by Knut Johan Hesten on 2016-03-03.
 */
public class Arguments {

    private String adr = "localhost:5432";
    private String dbname = "test";
    private String usr = "postgres";
    private String pw = "lacream";
    private String schemaName = "fylker_kombinert";

    public Arguments() {

    }

    public void setAddress(String address) {
        this.adr = address;
    }

    public void setDb(String db) {
        this.dbname = db;
    }

    public void setUserName(String userName) { this.usr = userName; }

    public void setPassword(String password) {
        this.pw = password;
    }

    public void setSchemaName(String schemaName) { this.schemaName = schemaName; }


    public String getAdr() {
        return adr;
    }

    public String getDbname() {
        return dbname;
    }

    public String getUsr() {
        return usr;
    }

    public String getPw() {
        return pw;
    }

    public String getSchemaName() { return this.schemaName; }
}


