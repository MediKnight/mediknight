package de.bo.mediknight.util;

import java.text.*;
import java.util.*;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;

import de.bo.mediknight.widgets.JDateChooser;

import de.bo.mediknight.MainFrame;

public class MediknightUtilities {

    //private static final String CURRENCY = " DM";

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
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if ( date == null )
            return sdf.format( new java.util.Date() );

        return sdf.format(date);
    }

    public static java.util.Date showDateChooser(java.awt.Component component, java.util.Date date) {
	Calendar c = Calendar.getInstance();
	c.setTime( date );
        c = JDateChooser.showDialog( component, c );
        if ( c != null )
            return c.getTime();
        else
            return null;
    }


    public static TableCellRenderer getTCRRight() {
        return new TableAlignmentCellRenderer( new DefaultTableCellRenderer(), SwingConstants.RIGHT );
    }

    public static TableCellRenderer getTCRCenter() {
        return new TableAlignmentCellRenderer( new DefaultTableCellRenderer(), SwingConstants.CENTER  );
    }

    /**
     * Returns the <tt>NumberFormat</tt> instance used by all
     * currency outputs.
     */
    public static NumberFormat getNumberFormat() {
        NumberFormat nf =
            NumberFormat.getInstance( Locale.GERMAN );
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        return nf;
    }

    public static boolean equalsWithNull(Object o1,Object o2) {
        return ( o1 == null || o2 == null ) ? o1 == o2 : o1.equals(o2);
    }

    public static int[] readCSV(String data) {
        StringTokenizer st = new StringTokenizer(data,",");
        Vector v = new Vector(100,10);
        while ( st.hasMoreTokens() ) {
            v.add(st.nextToken());
        }

        int[] n = new int[v.size()];
        for ( int i=0; i<n.length; i++ ) {
            try {
                n[i] = Integer.parseInt(v.get(i).toString());
            }
            catch ( NumberFormatException x ) {
                n[i] = 0;
            }
        }

        return n;
    }

    public static String writeCSV(int[] data) {
        StringBuffer sb = new StringBuffer();
        for ( int i=0; i<data.length; i++ ) {
            if ( i > 0 ) {
                sb.append(",");
            }
            sb.append(data[i]);
        }
        return sb.toString();
    }
}
