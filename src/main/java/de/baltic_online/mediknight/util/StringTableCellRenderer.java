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
public class StringTableCellRenderer implements TableCellRenderer {

    private final JLabel stringValue;


    public StringTableCellRenderer( final JTable table ) {
	stringValue = new JLabel();
	stringValue.setFont( table.getFont() );
	stringValue.setOpaque( true );
	stringValue.setBorder( null );
	stringValue.setBackground( table.getBackground() );

	final int preferredHeight = (int) stringValue.getPreferredSize().getHeight();
	if( table.getRowHeight() < preferredHeight ) {
	    table.setRowHeight( preferredHeight );
	}
    }


    public double getLabelPrefWidth() {
	return stringValue.getPreferredSize().getWidth();
    }


    @Override
    public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column ) {
	stringValue.setText( (String) value);
	if( isSelected ) {
	    stringValue.setBackground( table.getSelectionBackground() );
	    stringValue.setForeground( table.getSelectionForeground() );
	} else {
	    stringValue.setBackground( table.getBackground() );
	    stringValue.setForeground( table.getForeground() );
	}

	return stringValue;
    }


    private void setDateChooserColors( final JTable table ) {
//	final JTextField tmp = (JTextField) dateChooser.getDateEditor().getUiComponent();

	stringValue.setBackground( table.getBackground() );
	stringValue.setForeground( table.getForeground() );
//	tmp.setSelectedTextColor( table.getSelectionForeground() );
//	tmp.setSelectionColor( table.getSelectionBackground() );
    }
}
