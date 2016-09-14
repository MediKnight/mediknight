package main.java.de.baltic_online.mediknight.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import main.java.de.baltic_online.mediknight.widgets.JDateChooser;


public class MediknightUtilities {

    // private static final String CURRENCY = " DM";

    private static DateParser[] dateParser;
    static {
	dateParser = new DateParser[] { new SimpleDateParser( "dd.MM.yyyy", SimpleDateParser.DAY | SimpleDateParser.MONTH | SimpleDateParser.YEAR ),
	    new SimpleDateParser( "dd.MM.", SimpleDateParser.DAY | SimpleDateParser.MONTH ),
	    new SimpleDateParser( "dd.MM", SimpleDateParser.DAY | SimpleDateParser.MONTH ), new SimpleDateParser( "dd.", SimpleDateParser.DAY ),
	    new SimpleDateParser( "dd", SimpleDateParser.DAY ) };
    }


    public static boolean equalsWithNull( final Object o1, final Object o2 ) {
	return o1 == null || o2 == null ? o1 == o2 : o1.equals( o2 );
    }


    public static String formatDate( final java.util.Date date ) {
	final SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy" );
	if( date == null ) {
	    return sdf.format( new java.util.Date() );
	}

	return sdf.format( date );
    }


    /**
     * Returns the <tt>NumberFormat</tt> instance used by all currency outputs.
     */
    public static NumberFormat getNumberFormat() {
	final NumberFormat nf = NumberFormat.getInstance( Locale.GERMAN );
	nf.setMaximumFractionDigits( 2 );
	nf.setMinimumFractionDigits( 2 );

	return nf;
    }


    public static TableCellRenderer getTCRCenter() {
	return new TableAlignmentCellRenderer( new DefaultTableCellRenderer(), SwingConstants.CENTER );
    }


    public static TableCellRenderer getTCRRight() {
	return new TableAlignmentCellRenderer( new DefaultTableCellRenderer(), SwingConstants.RIGHT );
    }


    public static java.sql.Date parseDate( final String toParse ) {
	for( final DateParser element : dateParser ) {
	    try {
		final java.util.Date d = element.parse( toParse );
		return new java.sql.Date( d.getTime() );
	    } catch( final java.text.ParseException x ) { // try next parser
	    }
	}
	return null;
    }


    public static int[] readCSV( final String data ) {
	final StringTokenizer st = new StringTokenizer( data, "," );
	final Vector< String > v = new Vector< String >( 100, 10 );
	while( st.hasMoreTokens() ) {
	    v.add( st.nextToken() );
	}

	final int[] n = new int[v.size()];
	for( int i = 0; i < n.length; i++ ) {
	    try {
		n[i] = Integer.parseInt( v.get( i ).toString() );
	    } catch( final NumberFormatException x ) {
		n[i] = 0;
	    }
	}

	return n;
    }


    public static java.util.Date showDateChooser( final java.awt.Component component, final java.util.Date date ) {
	Calendar c = Calendar.getInstance();
	c.setTime( date );
	c = JDateChooser.showDialog( component, c );
	if( c != null ) {
	    return c.getTime();
	} else {
	    return null;
	}
    }


    public static String writeCSV( final int[] data ) {
	final StringBuffer sb = new StringBuffer();
	for( int i = 0; i < data.length; i++ ) {
	    if( i > 0 ) {
		sb.append( "," );
	    }
	    sb.append( data[i] );
	}
	return sb.toString();
    }
}
