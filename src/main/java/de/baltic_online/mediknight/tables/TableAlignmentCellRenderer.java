/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.tables;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class TableAlignmentCellRenderer implements TableCellRenderer {

    private final TableCellRenderer renderer;
    private final int		    alignment;


    public TableAlignmentCellRenderer( final TableCellRenderer renderer, final int alignment ) {
	this.renderer = renderer;
	this.alignment = alignment;
    }


    @Override
    public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
						    final int column ) {

	final Component c = renderer.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
	if( c instanceof JLabel ) {
	    ((JLabel) c).setHorizontalAlignment( alignment );
	}
	return c;
    }
}