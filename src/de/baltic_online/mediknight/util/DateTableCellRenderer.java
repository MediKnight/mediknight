package de.baltic_online.mediknight.util;

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

    private final JDateChooser chooser;


    public DateTableCellRenderer( final JTable table ) {
	chooser = new JDateChooser( getTodaysDate() );
	chooser.setFont( table.getFont() );
	chooser.setOpaque( true );
	chooser.setBorder( null );

	setDateChooserColors( table );

	final int preferredHeight = (int) chooser.getPreferredSize().getHeight();
	if( table.getRowHeight() < preferredHeight ) {
	    table.setRowHeight( preferredHeight );
	}
    }


    public double getLabelPrefWidth() {
	return chooser.getPreferredSize().getWidth();
    }


    @Override
    public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
						    final int column ) {

	chooser.setDate( value != null ? new Date( ((java.sql.Date) value).getTime() ) : getTodaysDate() );
	final JComponent comp = chooser.getDateEditor().getUiComponent();
	if( isSelected ) {
	    comp.setBackground( table.getSelectionBackground() );
	    comp.setForeground( table.getSelectionForeground() );
	} else {
	    comp.setBackground( table.getBackground() );
	    comp.setForeground( table.getForeground() );
	}

	return chooser;
    }


    private Date getTodaysDate() {
	return Date.from( LocalDate.now().atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() );
    }


    private void setDateChooserColors( final JTable table ) {
	final JTextField tmp = (JTextField) chooser.getDateEditor().getUiComponent();

	tmp.setBackground( table.getBackground() );
	tmp.setForeground( table.getForeground() );
	tmp.setSelectedTextColor( table.getSelectionForeground() );
	tmp.setSelectionColor( table.getSelectionBackground() );
    }
}
