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
 *
 * @author ECSTRPL
 */
public class DateTableCellRenderer implements TableCellRenderer {

    private static final int preferredDateChooserWidth = (int)(new JDateChooser( Date.from( LocalDate.now().atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() )).getPreferredSize().getWidth());
    private final JLabel dateChooser;


    public DateTableCellRenderer( final JTable table ) {
	dateChooser = new JLabel( getTodaysDate().toString() );
	dateChooser.setFont( table.getFont() );
	dateChooser.setOpaque( true );
	dateChooser.setBorder( null );
	dateChooser.setBackground( table.getBackground() );
	setDateChooserColors( table );

	final int preferredHeight = (int) dateChooser.getPreferredSize().getHeight();
	if( table.getRowHeight() < preferredHeight ) {
	    table.setRowHeight( preferredHeight );
	}
	
	dateChooser.setPreferredSize( new Dimension( preferredDateChooserWidth, preferredHeight) );
    }


    public double getLabelPrefWidth() {
	return dateChooser.getPreferredSize().getWidth();
    }


    @Override
    public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
						    final int column ) {

	final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate( FormatStyle.MEDIUM );
//	final Date date = value != null ? Date.from( ((LocalDate) value).atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() ) : getTodaysDate();
	final LocalDate date = value != null ? (LocalDate) value : getTodaysDate();
	
	dateChooser.setText( date.format( formatter ) ); //new Date( ((java.sql.Date) value).getTime() ) : getTodaysDate() ); //TODO So korrekt?
//	final JComponent comp = dateChooser.getDateEditor().getUiComponent();
	if( isSelected ) {
	    dateChooser.setBackground( table.getSelectionBackground() );
	    dateChooser.setForeground( table.getSelectionForeground() );
	} else {
	    dateChooser.setBackground( table.getBackground() );
	    dateChooser.setForeground( table.getForeground() );
	}

	return dateChooser;
    }


    private LocalDate getTodaysDate() {
//	return Date.from( LocalDate.now().atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() );
	return LocalDate.now();
    }


    private void setDateChooserColors( final JTable table ) {
//	final JTextField tmp = (JTextField) dateChooser.getDateEditor().getUiComponent();

	dateChooser.setBackground( table.getBackground() );
	dateChooser.setForeground( table.getForeground() );
//	tmp.setSelectedTextColor( table.getSelectionForeground() );
//	tmp.setSelectionColor( table.getSelectionBackground() );
    }
}
