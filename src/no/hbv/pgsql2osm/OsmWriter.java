package no.hbv.pgsql2osm;

import java.io.*;

/**
 * Created by Knut Johan Hesten on 2016-02-26.
 * Last updated by Knut Johan Hesten on 2016-06-08
 */
class OsmWriter implements AutoCloseable{
    private StringBuilder sb;
    private static final int bufferSize = 16384;
    private final String fileName;

    private final Writer nodeWriter, wayWriter, xmlWriter;

    OsmWriter(String fileName) throws IOException{
        this.fileName = fileName;
        this.sb = new StringBuilder();

        this.nodeWriter = new BufferedWriter(new FileWriter(Const.TEMP_FILE_NODES), bufferSize);
        this.wayWriter = new BufferedWriter(new FileWriter(Const.TEMP_FILE_WAYS), bufferSize);
        this.xmlWriter = new BufferedWriter(new FileWriter(Const.XML_FILE_NAME), bufferSize);

        deleteFile(Const.TEMP_FILE_WAYS);
        deleteFile(Const.TEMP_FILE_NODES);
        deleteFile(Const.XML_FILE_NAME);
        if (fileExists(fileName) && !deleteFile(fileName)) {
            System.out.println("Unable to delete existing file. Check permissions and try again");
            System.exit(1);
            return;
        }
        sb.append(XmlDeclaration());
        sb.append(OsmDeclaration());

        this.writeOsmToDisk(sb, fileName);
        this.sb = new StringBuilder();

        this.writeBuffered(tagFileDeclaration(), Const.XML);
    }

    void writeBuffered(StringBuilder input, int type) throws IOException {
        switch (type) {
            case Const.NODE:
                nodeWriter.append(input);
                break;
            case Const.WAY:
                wayWriter.append(input);
//                nodeWriter.flush();
                break;
            case Const.XML:
                xmlWriter.append(input);
                break;
        }
    }

    // TODO: 2016-06-08 unused
    public void writeOsmToDisk(StringBuilder input, int type) {
        if (type == Const.NODE) {
            this.writeOsmToDisk(input, Const.TEMP_FILE_NODES);
        } else if (type == Const.WAY) {
            this.writeOsmToDisk(input, Const.TEMP_FILE_WAYS);
        }
    }

    private void writeOsmToDisk(StringBuilder input, String fileName) {
        try (FileWriter fw = new FileWriter(fileName, true)) {
            if (input.length() == 0) {
                fw.write(sb.toString());
                this.sb = new StringBuilder();
            } else {
                fw.write(input.toString());
            }
        } catch (IOException ex) {
            System.out.println("Unable to write: " + ex.getMessage());
        }
    }

    private void combineTempFiles() throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(Const.TEMP_FILE_NODES))) {
            writeFromInputStream(br);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(Const.TEMP_FILE_WAYS))) {
            writeFromInputStream(br);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    private void writeFromInputStream(BufferedReader br) throws Exception {
        try (FileWriter fw = new FileWriter(fileName, true)) {
            String readLine = br.readLine();
//            FileWriter fw = new FileWriter(fileName, true);
            while (readLine != null) {
                fw.write(readLine);
                fw.write(Const.newLine());
                readLine = br.readLine();
            }
//            fw.close();
        }
    }

    private void writeEOF() {
        try {
            this.writeOsmToDisk(setBounds(), fileName);
            this.nodeWriter.close();
            this.wayWriter.close();
            combineTempFiles();

//            this.writeOsmToDisk(ways, fileName);
            this.writeOsmToDisk(new StringBuilder(this.getEndOfFile()), fileName);
            this.writeBuffered(getEndOfXml(), Const.XML);
            this.xmlWriter.close();
        } catch (Exception ex) {
            System.out.printf("Unable to write to disk: ").printf(ex.getMessage());
        }
    }

    private String XmlDeclaration() {
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + Const.newLine();
    }

    private String OsmDeclaration() {
        return "<osm version=\"" + Const.OSM_VERSION + "\" generator=\"" + Const.GENERATOR + "\">" + Const.newLine();
    }


    @SuppressWarnings("SpellCheckingInspection")
    private StringBuilder setBounds() {
        StringBuilder sb = new StringBuilder();
        sb.append(" <bounds ");
        sb.append("minlat=\"");
        sb.append(GeomHelper.getMinY());
        sb.append("\" ");
        sb.append("minlon=\"");
        sb.append(GeomHelper.getMinX());
        sb.append("\" ");
        sb.append("maxlat=\"");
        sb.append(GeomHelper.getMaxY());
        sb.append("\" ");
        sb.append("maxlon=\"");
        sb.append(GeomHelper.getMaxX());
        sb.append("\"/>");
        sb.append(Const.newLine());
        return sb;
    }

    private StringBuilder tagFileDeclaration() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> ");
        sb.append(Const.newLine());
        sb.append("<tag-mapping xmlns=\"http://mapsforge.org/tag-mapping\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append(Const.newLine());
        sb.append("xsi:schemaLocation=\"http://mapsforge.org/tag-mapping ../resources/tag-mapping.xsd\" default-zoom-appear=\"16\" ");
        sb.append(Const.newLine());
        sb.append("profile-name=\"default-profile\"> ");
        sb.append(Const.newLine());

        sb.append("<ways>").append(Const.newLine());
        sb.append("<osm-tag key=\"lock\" value=\"yes\" zoom-appear=\"14\" />").append(Const.newLine());
        sb.append("<!-- mapsforge artificial tags for land/sea areas, do not remove -->").append(Const.newLine());
        sb.append("<osm-tag key=\"natural\" value=\"sea\" zoom-appear=\"0\" />").append(Const.newLine());
        sb.append("<osm-tag key=\"natural\" value=\"nosea\" zoom-appear=\"0\" />").append(Const.newLine());
        sb.append("</ways>").append(Const.newLine());

        return sb;
    }

    private static StringBuilder getEndOfXml() { return new StringBuilder().append(Const.newLine()).append("</tag-mapping>");}

    private String getEndOfFile() {
         return "</osm>";
    }

    private boolean deleteFile(String fileName) {
        return new File(fileName).delete();
    }

    private boolean fileExists(String fileName) {
        return new File(fileName).exists();
    }

    @Override
    public void close() throws Exception {
        this.writeEOF();
        if (fileExists(Const.TEMP_FILE_NODES) && deleteFile(Const.TEMP_FILE_NODES) && fileExists(Const.TEMP_FILE_WAYS) && deleteFile(Const.TEMP_FILE_WAYS))
            System.out.println("Temporary files deleted successfully");
    }
}
