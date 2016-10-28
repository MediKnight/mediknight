package main.java.de.baltic_online.mediknight.util;

import java.awt.Component;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import com.toedter.calendar.JDateChooser;


/**
 *
 * @author ECSTRPL
 */
public class DateTableCellRenderer implements TableCellRenderer {

    private final JDateChooser dateChooser;


    public DateTableCellRenderer( final JTable table ) {
	dateChooser = new JDateChooser( getTodaysDate() );
	dateChooser.setFont( table.getFont() );
	dateChooser.setOpaque( true );
	dateChooser.setBorder( null );
	dateChooser.setBackground( table.getBackground() );
	setDateChooserColors( table );

	final int preferredHeight = (int) dateChooser.getPreferredSize().getHeight();
	if( table.getRowHeight() < preferredHeight ) {
	    table.setRowHeight( preferredHeight );
	}
    }


    public double getLabelPrefWidth() {
	return dateChooser.getPreferredSize().getWidth();
    }


    @Override
    public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
						    final int column ) {

	dateChooser.setDate( value != null ? Date.from( ((LocalDate) value).atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() ) : getTodaysDate() ); //new Date( ((java.sql.Date) value).getTime() ) : getTodaysDate() ); //TODO So korrekt?
	final JComponent comp = dateChooser.getDateEditor().getUiComponent();
	if( isSelected ) {
	    comp.setBackground( table.getSelectionBackground() );
	    comp.setForeground( table.getSelectionForeground() );
	} else {
	    comp.setBackground( table.getBackground() );
	    comp.setForeground( table.getForeground() );
	}

	return dateChooser;
    }


    private Date getTodaysDate() {
	return Date.from( LocalDate.now().atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() );
    }


    private void setDateChooserColors( final JTable table ) {
	final JTextField tmp = (JTextField) dateChooser.getDateEditor().getUiComponent();

	tmp.setBackground( table.getBackground() );
	tmp.setForeground( table.getForeground() );
	tmp.setSelectedTextColor( table.getSelectionForeground() );
	tmp.setSelectionColor( table.getSelectionBackground() );
    }
}
