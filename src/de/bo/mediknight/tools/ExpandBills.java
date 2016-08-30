package de.bo.mediknight.tools;

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


public class ExpandBills {

    static class Bill {

	static String getKeyPrefix( final int n ) {
	    return "be." + n + ".";
	}

	int	   id;
	int	   diagnosisId;
	java.sql.Date date;
	String	object;

	String	text;


	void convert() {
	    if( object != null ) {
		final Properties props = new Properties();
		try {
		    final ByteArrayInputStream bais = new ByteArrayInputStream( object.getBytes() );
		    props.load( bais );
		} catch( final IOException x ) {
		    x.printStackTrace();
		}

		final String kp = getKeyPrefix( 0 );
		final String rtext = props.getProperty( kp + "rtext" );

		if( rtext != null ) {
		    text = rtext;
		} else {
		    text = "";
		}

		/*
		 * Vorsicht beim unkommentieren des Blocks. Könnte alle Rechnungstexte vernichten!
		 */
		/*
		 * props.setProperty(kp+"rtext",""); try { ByteArrayOutputStream bos = new ByteArrayOutputStream(100000); props.store(bos,""); bos.flush();
		 * object = bos.toString(); } catch (IOException x) { // should not be thrown on ByteArrayOutputStream x.printStackTrace(); }
		 */
	    } else {
		text = "";
	    }
	}


	@Override
	public String toString() {
	    return id + " " + diagnosisId + " " + date + "\n" + object;
	}
    }

    private final static String MEDIKNIGHT_PROPERTIES = "mediknight.properties";

    private final static String PROPERTY_FILENAME     = "de/bo/mediknight/resources/" + MEDIKNIGHT_PROPERTIES;


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
	    final ExpandBills expander = new ExpandBills( createConnection() );
	    expander.convert();
	} catch( final Exception x ) {
	    x.printStackTrace();
	}
    }

    private final Connection connection;


    private ExpandBills( final Connection connection ) {
	this.connection = connection;
    }


    void convert() throws SQLException {
	final String sql1 = "select id,diagnose_id,datum,object from rechnung";
	final String sql2 = "update rechnung set object=?,text=? where id=?";
	final Statement st = connection.createStatement();
	final PreparedStatement ps = connection.prepareStatement( sql2 );
	final ResultSet rs = st.executeQuery( sql1 );

	while( rs.next() ) {
	    final Bill b = new Bill();
	    b.id = rs.getInt( 1 );
	    b.diagnosisId = rs.getInt( 2 );
	    b.date = rs.getDate( 3 );
	    b.object = rs.getString( 4 );
	    // System.out.println(b);

	    b.convert();

	    ps.setString( 1, b.object );
	    ps.setString( 2, b.text );
	    ps.setInt( 3, b.id );
	    ps.executeUpdate();
	}

	rs.close();
	st.close();
    }
}