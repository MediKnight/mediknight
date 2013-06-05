package de.bo.mediknight.tools;

import java.io.*;
import java.util.*;
import java.sql.*;

public class ExtractAnamnese {
    private final static String MEDIKNIGHT_PROPERTIES = "mediknight.properties";
    private final static String PROPERTY_FILENAME = "de/bo/mediknight/resources/" + MEDIKNIGHT_PROPERTIES;

    private Connection connection;

    private ExtractAnamnese(Connection connection) {
        this.connection = connection;
    }

    void extract() throws SQLException {
        String allPatients = "select id from patient";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(allPatients);

        while (rs.next()) {
            extractFor(rs.getInt("id"));
        }

        stmt.close();
    }

    void extractFor(int id) throws SQLException {
        System.err.println("Extracting for " + id);
        String patientsBills =
            "select r.id, r.datum, r.object from rechnung r, tagesdiagnose d where " +
            "d.id = r.diagnose_id and d.patient_id = " + id +
            " order by r.datum desc";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(patientsBills);

        if (rs.next()) {
            String anamnese = Bill.extract(rs.getString("r.object"));
            System.err.println(rs.getString("r.datum") + ": " + anamnese);

            if (anamnese != null && anamnese.trim().length() > 0) {
                System.err.println("Updating...");
                String updateAnamnese = "update patient set erstdiagnose = ? where id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateAnamnese);
                updateStmt.setString(1, anamnese);
                updateStmt.setInt(2, id);
                updateStmt.execute();
                updateStmt.close();
            } else {
                System.err.println("Skipping.");
            }
        }

        stmt.close();
    }


    @SuppressWarnings("resource")
    static Connection createConnection() throws Exception {
        InputStream is = null;

        try {
            is = new FileInputStream( new File( MEDIKNIGHT_PROPERTIES ) );
        } catch( FileNotFoundException e ) {
            is = ExpandBills.class.getClassLoader().getResourceAsStream( PROPERTY_FILENAME );
        } catch( Exception e ) {
            is.close();
            throw e;
        }

        Properties p = new Properties();
        p.load(is);
        is.close();

        String driverName = p.getProperty("jdbc.driver.name");
        Class.forName(driverName).newInstance();

        String jdbcURL = p.getProperty("jdbc.url.name");
        String user = p.getProperty("jdbc.db.user");
        String passwd = p.getProperty("jdbc.db.passwd");

        System.out.println("Trying to connect at "+jdbcURL+" with "+user+"/"+passwd);
        return DriverManager.getConnection(jdbcURL,user,passwd);
    }

    public static void main(String[] args) {
        try {
            ExtractAnamnese extractor = new ExtractAnamnese(createConnection());
            extractor.extract();
        }
        catch ( Exception x ) {
            x.printStackTrace();
        }
    }

    static class Bill {
        static String extract(String object) {
            if (object != null) {
                Properties props = new Properties();
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(object.getBytes());
                    props.load(bais);
                }
                catch (IOException x) {
                    x.printStackTrace();
                }

                String kp = getKeyPrefix(0);
                return props.getProperty(kp+"rtext");
            }

            return null;
        }

        static String getKeyPrefix(int n) {
            return "be." + n + ".";
        }
    }
}