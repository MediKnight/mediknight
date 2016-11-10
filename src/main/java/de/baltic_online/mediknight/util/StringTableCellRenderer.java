package main.java.de.baltic_online.mediknight.util;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


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
	stringValue.setBackground( table.getBackground() ); // TODO: Überhaupt notwendig?

	final int preferredHeight = (int) stringValue.getPreferredSize().getHeight();
	if( table.getRowHeight() < preferredHeight ) {
	    table.setRowHeight( preferredHeight );
	}
    }


    public double getLabelPrefWidth() {
	return stringValue.getPreferredSize().getWidth();
    }


    @Override
    public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
						    final int column ) {
	stringValue.setText( (String) value );
	if( isSelected ) {
	    stringValue.setBackground( table.getSelectionBackground() );
	    stringValue.setForeground( table.getSelectionForeground() );
	} else {
	    stringValue.setBackground( table.getBackground() );
	    stringValue.setForeground( table.getForeground() );
	}

	return stringValue;
    }
}
