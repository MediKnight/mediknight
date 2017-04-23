package main.java.de.baltic_online.mediknight.tables;

import java.awt.Component;
import java.awt.FontMetrics;

import javax.swing.JEditorPane;
import javax.swing.JTable;

import main.java.de.baltic_online.mediknight.MediKnight;
import main.java.de.baltic_online.mediknight.util.MediKnightInsets;


/**
 *
 * @author ECSTRPL
 */
public class StringTableCellRenderer implements MediKnightTableCellRenderer {

    private final JEditorPane stringValue;


    public StringTableCellRenderer( final JTable table ) {
	stringValue = new JEditorPane();
	stringValue.setFont( table.getFont() );
	stringValue.setOpaque( true );
	stringValue.setBorder( null );
	stringValue.setBackground( table.getBackground() );
	stringValue.setMargin( new MediKnightInsets( MediKnight.HORIZONTAL_TEXT_INSET, MediKnight.VERTICAL_TEXT_INSET ) );
    }


    /**
     * Returns a GUI-Object for the given string.
     */
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


    /**
     * Returns the preferred height for the given String object or the minimum height value.
     * 
     */
    @Override
    public int getPreferredRowHeight( final JTable table, final int row, final int column ) {
	stringValue.setText( (String) table.getModel().getValueAt( row, column ) );

	final FontMetrics metrics = stringValue.getFontMetrics( stringValue.getFont() );
	final int minTextHeight = metrics.getHeight() * 10;
	final int height = (int) stringValue.getPreferredSize().getHeight();

	return height < minTextHeight ? minTextHeight : height;
    }


    /**
     * Returns the preferred row width for the string object.
     */
    @Override
    public int getPreferredRowWidth( final JTable table, final int row, final int column ) {
	stringValue.setText( (String) table.getModel().getValueAt( row, column ) );

	return (int) stringValue.getPreferredSize().getWidth();
    }
}
