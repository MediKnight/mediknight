package de.bo.mediknight.widgets;

import java.awt.Component;
import java.util.EventObject;

public class JTable extends javax.swing.JTable {

    public boolean editCellAt(int row, int column, EventObject e) {
        boolean canEdit = super.editCellAt(row, column, e);
        if (canEdit) {
            Component c = getEditorComponent();
            if (c instanceof javax.swing.JTextField) {
                ((javax.swing.JTextField)c).select(0, 9999);
                c.repaint();
            }
        }
        return canEdit;
    }
}