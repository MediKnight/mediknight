/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import java.util.Date;
import java.util.Calendar;

public class DateTools {
    public static int onlyDateCompare(Date d1,Date d2) {
        if ( d1 == null )
            return ( d2 == null ) ? 0 : -1;
        if ( d2 == null )
            return 1;

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);

        int[] fields = {Calendar.YEAR,Calendar.MONTH,Calendar.DATE};
        for ( int i=0; i<fields.length; i++ ) {
            int cmp = cal1.get(fields[i]) - cal2.get(fields[i]);
            if ( cmp != 0 ) return cmp;
        }
        return 0;
    }

    public static boolean isToday(Date d) {
        Date today = new Date();
        return onlyDateCompare(d,today) == 0;
    }
}
