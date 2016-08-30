package de.bo.mediknight.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.bo.mediknight.widgets.JDateChooser;


public class MediknightParser {

    private static DateParser[] dateParser;
    static {
	dateParser = new DateParser[] { new SimpleDateParser( "dd.MM.yyyy", SimpleDateParser.DAY | SimpleDateParser.MONTH | SimpleDateParser.YEAR ),
	    new SimpleDateParser( "dd.MM.", SimpleDateParser.DAY | SimpleDateParser.MONTH ),
	    new SimpleDateParser( "dd.MM", SimpleDateParser.DAY | SimpleDateParser.MONTH ), new SimpleDateParser( "dd.", SimpleDateParser.DAY ),
	    new SimpleDateParser( "dd", SimpleDateParser.DAY ) };
    }


    public static String formatDate( final java.util.Date date ) {
	if( date == null ) {
	    return "";
	}

	final SimpleDateFormat sdf = new SimpleDateFormat( "dd.MM.yyyy" );
	return sdf.format( date );
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


    public static java.util.Date showDateChooser( final java.awt.Component component ) {
	final Calendar c = JDateChooser.showDialog( component, Calendar.getInstance() );
	if( c != null ) {
	    return c.getTime();
	} else {
	    return null;
	}

    }

}