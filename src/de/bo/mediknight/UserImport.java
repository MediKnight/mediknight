package de.bo.mediknight;

import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class UserImport {

    Properties properties;
    Connection connection;

    static int getInteger(String src,int defvalue) {
        try {
            return Integer.parseInt(src);
        }
        catch ( NumberFormatException nfx ) {
            return defvalue;
        }
    }

    UserImport(Properties properties) throws Exception {
        this.properties = properties;

        initDB();
    }

    void importData() throws SQLException, IOException {
        int n = getInteger(properties.getProperty("user.count"),0);
        for ( int i=0; i<n; i++ ) {
            String pName = "user."+i+".name";
            String pPassword = "user."+i+".password";
            String pFile = "user."+i+".imagefile";

            insertUser(
                properties.getProperty(pName),
                properties.getProperty(pPassword),
                properties.getProperty(pFile)
            );
        }
    }

    void insertUser(String name,String password,String filename)
        throws SQLException, IOException {

        byte[] idata = null;
        String sql = null;
        if ( filename.length() != 0 ) {
            File f = new File(filename);
            idata = new byte[(int)f.length()];
            FileInputStream fis = new FileInputStream(f);
            fis.read(idata);
            fis.close();
            sql = "insert into benutzer (name,passwort,zugriff,bild) values(?,?,-1,?)";
        }
        else {
            sql = "insert into benutzer (name,passwort,zugriff) values(?,?,-1)";
        }

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1,name);
        ps.setString(2,password);
        if ( idata != null )
            ps.setObject(3,idata);
        ps.executeUpdate();
        ps.close();
    }

    void initDB() throws Exception {
        String driverName = properties.getProperty("jdbc.driver.name");
        Driver driver = (Driver)Class.forName(driverName).newInstance();
        DriverManager.registerDriver(driver);

        String jdbcURL = properties.getProperty("jdbc.url.name");
        String user = properties.getProperty("jdbc.db.user");
        String passwd = properties.getProperty("jdbc.db.passwd");

        connection = DriverManager.getConnection(jdbcURL,user,passwd);
    }

    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(args[0]);
            props.load(fis);
            fis.close();

            UserImport ui = new UserImport(props);
            ui.importData();
        }
        catch ( ArrayIndexOutOfBoundsException aioobx ) {
            System.err.println("Usage: UserImport <property-file>");
        }
        catch ( IOException iox ) {
            System.err.println("Error reading property-file");
        }
        catch ( Exception x ) {
            x.printStackTrace();
        }
    }
}