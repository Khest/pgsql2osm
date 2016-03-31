package no.hbv.pgsql2osm;

import java.io.*;

/**
 * Created by Knut Johan Hesten on 2016-02-26.
 */
public class OsmWriter2 {
    private StringBuilder sb;
    //    private StringBuilder ways;
    private final String fileName;
    private final String tempFileNodes = "nodes.tmp";
    private final String tempFileWays = "ways.tmp";

//    private static FileWriter fw;

    OsmWriter2(String fileName) {
        this.fileName = fileName;

//        this.ways = new StringBuilder();
        if (fileExists(fileName) && !deleteFile(fileName)) {
            System.out.println("Unable to delete existing file. Check permissions and try again");
            System.exit(1);
        }

//        this.sb.delete(0, this.sb.length());

    }

    public void writeOsmToDisk(StringBuilder input, int type) {
        if (type == Const.NODE) {
            this.writeOsmToDisk(input, tempFileNodes);
        } else if (type == Const.WAY) {
            this.writeOsmToDisk(input, tempFileWays);
        }
    }

    private void writeOsmToDisk(StringBuilder input, String fileName) {
        try (FileWriter fw = new FileWriter(fileName, true)) {
            fw.write(input.toString());
        } catch (IOException ex) {
            System.out.println("Unable to write: " + ex.getMessage());
        }
    }

    private void combineTempFiles() throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(tempFileNodes))) {
            writeFromInputStream(br);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(tempFileWays))) {
            writeFromInputStream(br);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    private void writeFromInputStream(BufferedReader br) throws Exception {
        String readLine = br.readLine();
        FileWriter fw = new FileWriter(fileName, true);
        while (readLine != null) {
            fw.write(readLine);
            fw.write(Const.newLine());
            readLine = br.readLine();
        }
        fw.flush();
    }

    protected void writeEOF() {
        try {
            this.sb = new StringBuilder();
            sb.append(XmlDeclaration());
            sb.append(OsmDeclaration());
            this.writeOsmToDisk(sb, fileName);
            this.writeOsmToDisk(setBounds(), fileName);

            combineTempFiles();

//            this.writeOsmToDisk(ways, fileName);
            this.writeOsmToDisk(new StringBuilder(this.getEndOfFile()), fileName);
        } catch (Exception ex) {
            System.out.printf("Unable to write to disk: ").printf(ex.getMessage());
        }
    }

    private String XmlDeclaration() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Const.newLine();
    }

    private String OsmDeclaration() {
        return "<osm version=\"" + Const.OSMVERSION + "\" generator=\"" + Const.GENERATOR + "\">" + Const.newLine();
    }

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

    private String getEndOfFile() {
        return "</osm>";
    }

    private boolean deleteFile(String fileName) {
        return new File(fileName).delete();
    }

    private boolean fileExists(String fileName) {
        return new File(fileName).exists();
    }

//    public boolean sbLength() {
//        return this.sb.length() >= (Integer.MAX_VALUE - 20000);
//    }

}
