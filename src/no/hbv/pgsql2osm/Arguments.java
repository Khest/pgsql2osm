package no.hbv.pgsql2osm;

/**
 * Created by Knut Johan Hesten on 2016-03-03.
 * Last updated by Knut Johan Hesten on 2016-06-08
 */
class Arguments {
    // TODO: 2016-06-08 each set sets and checks value or throws IllegalArgumentException
    private String adr = "localhost:5432";
    private String dbname = "test";
    private String usr = "postgres";
    private String pw = "lacream";
    private String schemaName = "fylker_kombinert";

    Arguments() { }

    void setAddress(String address) {
        this.adr = address;
    }

    void setDb(String db) {
        this.dbname = db;
    }

    void setUserName(String userName) {
        this.usr = userName;
    }

    void setPassword(String password) {
        this.pw = password;
    }

    void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }


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


