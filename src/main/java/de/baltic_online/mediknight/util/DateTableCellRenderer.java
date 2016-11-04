package main.java.de.baltic_online.mediknight.util;

import java.awt.Component;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.toedter.calendar.JDateChooser;


/**
 * Displays a properly formatted date in a cell of a JTable.
 * 
 * @author ECSTRPL
 */
public class DateTableCellRenderer implements TableCellRenderer {

    /**
     * Using the width of the editor GUI component for proper sizing and avoiding to resize the column later.
     */
    private static final int preferredDateChooserWidth = (int)(new JDateChooser( Date.from( LocalDate.now().atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() )).getPreferredSize().getWidth());
    
    /**
     * GUI object for the renderer.
     */
    private final JLabel dateValue;


    public DateTableCellRenderer( final JTable table ) {
	dateValue = new JLabel( getTodaysDate().toString() );
	dateValue.setFont( table.getFont() );
	dateValue.setOpaque( true );
	dateValue.setBorder( null );

	final int preferredHeight = (int) dateValue.getPreferredSize().getHeight();
	if( table.getRowHeight() < preferredHeight ) {
	    table.setRowHeight( preferredHeight );
	}
	
	dateValue.setPreferredSize( new Dimension( preferredDateChooserWidth, preferredHeight) );
    }


    /**
     * Returns an object of JLabel containing the passed date value and set up to fit the table
     * 
     */
    @Override
    public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column ) {
	final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate( FormatStyle.MEDIUM );
	final LocalDate date = value != null ? (LocalDate) value : getTodaysDate();
	
	dateValue.setText( date.format( formatter ) );
	if( isSelected ) {
	    dateValue.setBackground( table.getSelectionBackground() );
	    dateValue.setForeground( table.getSelectionForeground() );
	} else {
	    dateValue.setBackground( table.getBackground() );
	    dateValue.setForeground( table.getForeground() );
	}

	return dateValue;
    }


    /**
     * Convenience method returning a today's LocalDate object.
     * @return
     */
    private LocalDate getTodaysDate() {
	return LocalDate.now();
    }
}
