/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.util;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class TableAlignmentCellRenderer implements TableCellRenderer {

    private TableCellRenderer renderer;
    private int alignment;


    public TableAlignmentCellRenderer(TableCellRenderer renderer,int alignment) {
        this.renderer = renderer;
        this.alignment = alignment;
    }


    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {

        Component c = renderer.
            getTableCellRendererComponent(table,value,isSelected,
                hasFocus,row,column);
        if ( c instanceof JLabel )
            ((JLabel)c).setHorizontalAlignment(alignment);
        return c;
    }
}