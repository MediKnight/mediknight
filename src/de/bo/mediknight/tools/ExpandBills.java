package de.bo.mediknight.tools;

import java.io.*;
import java.util.*;
import java.sql.*;

public class ExpandBills {
    private final static String MEDIKNIGHT_PROPERTIES = "mediknight.properties";
    private final static String PROPERTY_FILENAME = "de/bo/mediknight/resources/" + MEDIKNIGHT_PROPERTIES;

    private Connection connection;

    private ExpandBills(Connection connection) {
        this.connection = connection;
    }

    void convert() throws SQLException {
        String sql1 = "select id,diagnose_id,datum,object from rechnung";
        String sql2 = "update rechnung set object=?,text=? where id=?";
        Statement st = connection.createStatement();
        PreparedStatement ps = connection.prepareStatement(sql2);
        ResultSet rs = st.executeQuery(sql1);

        while ( rs.next() ) {
            Bill b = new Bill();
            b.id = rs.getInt(1);
            b.diagnosisId = rs.getInt(2);
            b.date = rs.getDate(3);
            b.object = rs.getString(4);
            //System.out.println(b);

            b.convert();

            ps.setString(1,b.object);
            ps.setString(2,b.text);
            ps.setInt(3,b.id);
            ps.executeUpdate();
        }

        rs.close();
        st.close();
    }

    static Connection createConnection() throws Exception {
        InputStream is = null;

        try {
            is = new FileInputStream( new File( MEDIKNIGHT_PROPERTIES ) );
        } catch( FileNotFoundException e ) {
            is = ExpandBills.class.getClassLoader().getResourceAsStream( PROPERTY_FILENAME );
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
            ExpandBills expander = new ExpandBills(createConnection());
            expander.convert();
        }
        catch ( Exception x ) {
            x.printStackTrace();
        }
    }

    static class Bill {
        int id;
        int diagnosisId;
        java.sql.Date date;
        String object;
        String text;

        void convert() {
            if ( object != null ) {
                Properties props = new Properties();
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(object.getBytes());
                    props.load(bais);
                }
                catch (IOException x) {
                    x.printStackTrace();
                }

                String kp = getKeyPrefix(0);
                String rtext = props.getProperty(kp+"rtext");

                if ( rtext != null )
                    text = rtext;
                else
                    text = "";

                /* Vorsicht beim unkommentieren des Blocks.
                   Könnte alle Rechnungstexte vernichten!
                */
                /*
                props.setProperty(kp+"rtext","");

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream(100000);
                    props.store(bos,"");
                    bos.flush();
                    object = bos.toString();
                }
                catch (IOException x) { // should not be thrown on ByteArrayOutputStream
                    x.printStackTrace();
                }
                */
            }
            else {
                text = "";
            }
        }

        static String getKeyPrefix(int n) {
            return "be." + n + ".";
        }

        public String toString() {
            return id + " " + diagnosisId + " " + date + "\n" + object;
        }
    }
}