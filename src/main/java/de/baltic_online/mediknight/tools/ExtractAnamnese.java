package main.java.de.baltic_online.mediknight.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class ExtractAnamnese {

    static class Bill {

	static String extract( final String object ) {
	    if( object != null ) {
		final Properties props = new Properties();
		try {
		    final ByteArrayInputStream bais = new ByteArrayInputStream( object.getBytes() );
		    props.load( bais );
		} catch( final IOException x ) {
		    x.printStackTrace();
		}

		final String kp = getKeyPrefix( 0 );
		return props.getProperty( kp + "rtext" );
	    }

	    return null;
	}


	static String getKeyPrefix( final int n ) {
	    return "be." + n + ".";
	}
    }

    private final static String	MEDIKNIGHT_PROPERTIES = "mediknight.properties";

    private final static String	PROPERTY_FILENAME     = "properties/" + MEDIKNIGHT_PROPERTIES;


    @SuppressWarnings( "resource" )
    static Connection createConnection() throws Exception {
	InputStream is = null;

	try {
	    is = new FileInputStream( new File( MEDIKNIGHT_PROPERTIES ) );
	} catch( final FileNotFoundException e ) {
	    is = ExpandBills.class.getClassLoader().getResourceAsStream( PROPERTY_FILENAME );
	} catch( final Exception e ) {
	    is.close();
	    throw e;
	}

	final Properties p = new Properties();
	p.load( is );
	is.close();

	final String driverName = p.getProperty( "jdbc.driver.name" );
	Class.forName( driverName ).newInstance();

	final String jdbcURL = p.getProperty( "jdbc.url.name" );
	final String user = p.getProperty( "jdbc.db.user" );
	final String passwd = p.getProperty( "jdbc.db.passwd" );

	System.out.println( "Trying to connect at " + jdbcURL + " with " + user + "/" + passwd );
	return DriverManager.getConnection( jdbcURL, user, passwd );
    }


    public static void main( final String[] args ) {
	try {
	    final ExtractAnamnese extractor = new ExtractAnamnese( createConnection() );
	    extractor.extract();
	} catch( final Exception x ) {
	    x.printStackTrace();
	}
    }

    private final Connection connection;


    private ExtractAnamnese( final Connection connection ) {
	this.connection = connection;
    }


    void extract() throws SQLException {
	final String allPatients = "select id from patient";
	final Statement stmt = connection.createStatement();
	final ResultSet rs = stmt.executeQuery( allPatients );

	while( rs.next() ) {
	    extractFor( rs.getInt( "id" ) );
	}

	stmt.close();
    }


    void extractFor( final int id ) throws SQLException {
	System.err.println( "Extracting for " + id );
	final String patientsBills = "select r.id, r.datum, r.object from rechnung r, tagesdiagnose d where " + "d.id = r.diagnose_id and d.patient_id = " + id
		+ " order by r.datum desc";
	final Statement stmt = connection.createStatement();
	final ResultSet rs = stmt.executeQuery( patientsBills );

	if( rs.next() ) {
	    final String anamnese = Bill.extract( rs.getString( "r.object" ) );
	    System.err.println( rs.getString( "r.datum" ) + ": " + anamnese );

	    if( anamnese != null && anamnese.trim().length() > 0 ) {
		System.err.println( "Updating..." );
		final String updateAnamnese = "update patient set erstdiagnose = ? where id = ?";
		final PreparedStatement updateStmt = connection.prepareStatement( updateAnamnese );
		updateStmt.setString( 1, anamnese );
		updateStmt.setInt( 2, id );
		updateStmt.execute();
		updateStmt.close();
	    } else {
		System.err.println( "Skipping." );
	    }
	}

	stmt.close();
    }
}