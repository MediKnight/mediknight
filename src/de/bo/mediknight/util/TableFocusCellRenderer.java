/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.util;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TableFocusCellRenderer implements TableCellRenderer {

    private TableCellRenderer renderer;
    private boolean focus;


    public TableFocusCellRenderer(TableCellRenderer renderer, boolean focus) {
        this.renderer = renderer;
        this.focus    = focus;
    }


    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {

        return renderer.getTableCellRendererComponent(table,
                                                      value,
                                                      isSelected,
                                                      focus,
                                                      row,
                                                      column);
    }

} // class TableFocusCellRenderer