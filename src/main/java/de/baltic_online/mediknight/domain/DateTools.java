/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.domain;

import java.util.Calendar;
import java.util.Date;


public class DateTools {

    public static boolean isToday( final Date d ) {
	final Date today = new Date();
	return onlyDateCompare( d, today ) == 0;
    }


    public static int onlyDateCompare( final Date d1, final Date d2 ) {
	if( d1 == null ) {
	    return d2 == null ? 0 : -1;
	}
	if( d2 == null ) {
	    return 1;
	}

	final Calendar cal1 = Calendar.getInstance();
	final Calendar cal2 = Calendar.getInstance();
	cal1.setTime( d1 );
	cal2.setTime( d2 );

	final int[] fields = { Calendar.YEAR, Calendar.MONTH, Calendar.DATE };
	for( final int field : fields ) {
	    final int cmp = cal1.get( field ) - cal2.get( field );
	    if( cmp != 0 ) {
		return cmp;
	    }
	}
	return 0;
    }
}
