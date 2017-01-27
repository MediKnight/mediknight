package main.java.de.baltic_online.mediknight.tables;

import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.toedter.calendar.JDateChooser;


/**
 * Displays a properly formatted date in a cell of a JTable.
 * 
 * @author ECSTRPL
 */
public class DateTableCellRenderer implements MediKnightTableCellRenderer {

    /**
     * GUI object for the renderer.
     */
    private final JLabel    dateValue;

    final DateTimeFormatter formatter;


    public DateTableCellRenderer( final JTable table ) {
	dateValue = new JLabel( getTodaysDate().toString() );
	dateValue.setFont( table.getFont() );
	dateValue.setOpaque( true );
	dateValue.setBorder( null );
	dateValue.setPreferredSize( new JDateChooser().getPreferredSize() );
	dateValue.setHorizontalAlignment( SwingConstants.CENTER );
	dateValue.setVerticalAlignment( SwingConstants.NORTH );
	formatter = DateTimeFormatter.ofLocalizedDate( FormatStyle.MEDIUM );
    }


    /**
     * Returns an object of JLabel containing the passed date value and set up to fit the table
     * 
     */
    @Override
    public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
						    final int column ) {
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
     * 
     * @return A LocalDate object initialized with now.
     */
    private LocalDate getTodaysDate() {
	return LocalDate.now();
    }


    /**
     * Returns the JLabel's preferred height.
     */
    @Override
    public int getPreferredRowHeight( final JTable table, final int row, final int column ) {
	return (int) dateValue.getPreferredSize().getHeight();
    }


    /**
     * Returns the JLabel's preferred Width
     */
    public int getPreferredRowWidth( final JTable table, final int row, final int column ) {
	return (int) dateValue.getPreferredSize().getWidth();
    }
}
