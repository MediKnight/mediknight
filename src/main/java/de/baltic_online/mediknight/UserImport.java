package main.java.de.baltic_online.mediknight;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class UserImport {

    static int getInteger( final String src, final int defvalue ) {
	try {
	    return Integer.parseInt( src );
	} catch( final NumberFormatException nfx ) {
	    return defvalue;
	}
    }


    public static void main( final String[] args ) {
	try {
	    final Properties props = new Properties();
	    final FileInputStream fis = new FileInputStream( args[0] );
	    props.load( fis );
	    fis.close();

	    final UserImport ui = new UserImport( props );
	    ui.importData();
	} catch( final ArrayIndexOutOfBoundsException aioobx ) {
	    System.err.println( "Usage: UserImport <property-file>" );
	} catch( final IOException iox ) {
	    System.err.println( "Error reading property-file" );
	} catch( final Exception x ) {
	    x.printStackTrace();
	}
    }

    Properties properties;

    Connection connection;


    UserImport( final Properties properties ) throws Exception {
	this.properties = properties;

	initDB();
    }


    void importData() throws SQLException, IOException {
	final int n = getInteger( properties.getProperty( "user.count" ), 0 );
	for( int i = 0; i < n; i++ ) {
	    final String pName = "user." + i + ".name";
	    final String pPassword = "user." + i + ".password";
	    final String pFile = "user." + i + ".imagefile";

	    insertUser( properties.getProperty( pName ), properties.getProperty( pPassword ), properties.getProperty( pFile ) );
	}
    }


    void initDB() throws Exception {
	final String driverName = properties.getProperty( "jdbc.driver.name" );
	final Driver driver = (Driver) Class.forName( driverName ).newInstance();
	DriverManager.registerDriver( driver );

	final String jdbcURL = properties.getProperty( "jdbc.url.name" );
	final String user = properties.getProperty( "jdbc.db.user" );
	final String passwd = properties.getProperty( "jdbc.db.passwd" );

	connection = DriverManager.getConnection( jdbcURL, user, passwd );
    }


    void insertUser( final String name, final String password, final String filename ) throws SQLException, IOException {

	byte[] idata = null;
	String sql = null;
	if( filename.length() != 0 ) {
	    final File f = new File( filename );
	    idata = new byte[(int) f.length()];
	    final FileInputStream fis = new FileInputStream( f );
	    fis.read( idata );
	    fis.close();
	    sql = "insert into benutzer (name,passwort,zugriff,bild) values(?,?,-1,?)";
	} else {
	    sql = "insert into benutzer (name,passwort,zugriff) values(?,?,-1)";
	}

	final PreparedStatement ps = connection.prepareStatement( sql );
	ps.setString( 1, name );
	ps.setString( 2, password );
	if( idata != null ) {
	    ps.setObject( 3, idata );
	}
	ps.executeUpdate();
	ps.close();
    }
}