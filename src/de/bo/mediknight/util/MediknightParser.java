package de.bo.mediknight.util;

import java.text.*;
import java.util.Calendar;
import de.bo.mediknight.widgets.JDateChooser;

public class MediknightParser {

    private static DateParser[] dateParser;
    static {
        dateParser = new DateParser[] {
            new SimpleDateParser("dd.MM.yyyy",
                SimpleDateParser.DAY |
                SimpleDateParser.MONTH |
                SimpleDateParser.YEAR),
            new SimpleDateParser("dd.MM.",
                SimpleDateParser.DAY |
                SimpleDateParser.MONTH),
            new SimpleDateParser("dd.MM",
                SimpleDateParser.DAY |
                SimpleDateParser.MONTH),
            new SimpleDateParser("dd.",SimpleDateParser.DAY),
            new SimpleDateParser("dd",SimpleDateParser.DAY)
        };
    }

    public static java.sql.Date parseDate(String toParse) {
        for ( int i=0; i<dateParser.length; i++ ) {
            try {
                java.util.Date d = dateParser[i].parse(toParse);
                return new java.sql.Date(d.getTime());
            }
            catch ( java.text.ParseException x ) { // try next parser
            }
        }
        return null;
    }

    public static String formatDate(java.util.Date date) {
        if ( date == null )
            return "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(date);
    }

    public static java.util.Date showDateChooser(java.awt.Component component) {
        Calendar c = JDateChooser.showDialog( component, Calendar.getInstance());
        if ( c != null )
            return c.getTime();
	else
	    return null;

    }


}