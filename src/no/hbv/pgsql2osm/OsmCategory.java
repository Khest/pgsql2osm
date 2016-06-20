package no.hbv.pgsql2osm;

/**
 * Created by Knut Johan Hesten on 2016-02-25.
 */
public enum OsmCategory {
    // TODO: 2016-06-08 marked for delete
    DEFAULT(true),
    DYBDEAREAL(true, "dybdeverdi"),
    DYBDEKONTUR(true, "dybdeverdi"),
    DYBDEPUNKT(false, "dybdeverdi"),
    FLYTEBRYGGE_A(true),
    FLYTEBRYGGE_L(true),
    GRUNNE(true, "dybdeverdi"),
    KONST_KYST_GR(true),
    KONST_KYST_L(true),
    KYSTKONTUR(true),
    KYSTSPERRE(true),
    SKJAER(false),
    TORRDOKK_A(true),
    TORRDOKK_GR(true),
    TORRFALL_GR(true, "dybdeverdi")
    ;

    private boolean isMultiPoly;
    private String[] categories;

    OsmCategory(boolean isMultiPoly) {
        this.isMultiPoly = isMultiPoly;
        this.categories = new String[0];
    }

    OsmCategory(boolean isMultiPoly, String k1) {
        this.isMultiPoly = isMultiPoly;
        this.categories = new String[1];
        this.categories[0] = k1;
    }

    OsmCategory(boolean isMultiPoly, String k1, String k2) {
        this.isMultiPoly = isMultiPoly;
        this.categories = new String[2];
        this.categories[0] = k1;
        this.categories[1] = k2;
    }

    public String[] getIdKVPair() {
        return new String[]{this.name().toLowerCase(), "test"};
//        return new String[]{"AREAL", "AREAL"};
    }

    public String[] getCategories() {
        if (this.categories == null) return new String[0];
        return this.categories;
    }

    public int getNumCat() {
        if (this.categories == null) return 0;
        return this.categories.length;
    }

    public boolean isMultiPoly() {
        return isMultiPoly;
    }
}
