package de.bo.mediknight.widgets;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

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