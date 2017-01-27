/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * <code>SimpleDateParser</code> ist ein auf <code>SimpleDateFormat</code> aufbauender <code>DateParser</code>. Er erlaubt unvollst&auml;ndige Datumsangaben, in
 * dem er die nicht spezifizierten Felder mit aktuell g&uuml;ltigen Werten belegt.
 */
public class SimpleDateParser implements DateParser {

    /**
     * Bezeichnung des Tagessegmentes eines Datums.
     */
    public static final int DAY	  = 1;

    /**
     * Bezeichnung des Monatssegmentes eines Datums.
     */
    public static final int MONTH = 2;

    /**
     * Bezeichnung des Jahressegmentes eines Datums.
     */
    public static final int YEAR  = 4;

    /**
     *
     */
    public static final int FULL  = DAY | MONTH | YEAR;

    /**
     * Flagvariable f&uuml;r spezifizierte Datumssegmente.
     */
    int			    segmentSet;

    /**
     * Zum Parsen verwendetes <code>DateFormat</code>.
     */
    DateFormat		    dateFormat;


    /**
     *
     */
    public SimpleDateParser( final DateFormat formatter, final int segmentSet ) {
	dateFormat = formatter;
	dateFormat.setLenient( false );
	this.segmentSet = segmentSet;
    }


    /**
     * Erzeugt einen <code>SimpleDateParser</code> mit angegebenem Format und Spezifikation der durch das <code>SimpleDateFormat</code> gesetzten Datumsteile.
     *
     * @param format
     *            Das f&uuml;r das <code>SimpleDateFormat</code> verwendete Format
     * @param segmentSet
     *            Flagvariable, die angibt, welche Teile des Datums durch das <code>SimpleDateFormat</code> gesetzt werden
     */
    public SimpleDateParser( final String format, final int segmentSet ) {
	this( new SimpleDateFormat( format ), segmentSet );
    }


    /**
     *
     */
    @Override
    public Date parse( final String s ) throws ParseException {

	Date date = null;
	final Calendar calendar = Calendar.getInstance();
	final Calendar now = Calendar.getInstance();

	date = dateFormat.parse( s );
	calendar.setTime( date );

	calendar.clear( Calendar.HOUR_OF_DAY );
	calendar.clear( Calendar.HOUR );
	calendar.clear( Calendar.MINUTE );
	calendar.clear( Calendar.SECOND );
	calendar.clear( Calendar.MILLISECOND );

	if( (segmentSet & DAY) == 0 ) {
	    calendar.set( Calendar.DAY_OF_MONTH, now.get( Calendar.DAY_OF_MONTH ) );
	}

	if( (segmentSet & MONTH) == 0 ) {
	    calendar.set( Calendar.MONTH, now.get( Calendar.MONTH ) );
	}

	if( (segmentSet & YEAR) == 0 ) {
	    calendar.set( Calendar.YEAR, now.get( Calendar.YEAR ) );
	}

	return calendar.getTime();
    }
}
