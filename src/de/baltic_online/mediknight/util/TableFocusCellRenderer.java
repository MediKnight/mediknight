/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.util;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class TableFocusCellRenderer implements TableCellRenderer {

    private final TableCellRenderer renderer;
    private final boolean	   focus;


    public TableFocusCellRenderer( final TableCellRenderer renderer, final boolean focus ) {
	this.renderer = renderer;
	this.focus = focus;
    }


    @Override
    public Component getTableCellRendererComponent( final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
						    final int column ) {

	return renderer.getTableCellRendererComponent( table, value, isSelected, focus, row, column );
    }

} // class TableFocusCellRenderer