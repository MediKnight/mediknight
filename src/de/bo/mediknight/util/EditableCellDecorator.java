/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.util;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class EditableCellDecorator implements TableCellEditor {

    private TableCellEditor editor;
    private boolean editable;

    public EditableCellDecorator(TableCellEditor editor) {
        this(editor,false);
    }

    public EditableCellDecorator(TableCellEditor editor,boolean editable) {
        this.editor = editor;
        this.editable = false;
    }

    public Object getCellEditorValue() {
        return editor.getCellEditorValue();
    }

    public boolean isCellEditable(EventObject e) {
        return editable;
    }

    public boolean shouldSelectCell(EventObject e) {
        return editor.shouldSelectCell(e);
    }

    public boolean stopCellEditing() {
        return editor.stopCellEditing();
    }

    public Component getTableCellEditorComponent(JTable table,Object value,
                                                    boolean isSelected,
                                                    int row,int column) {
        return editor.getTableCellEditorComponent(table,value,isSelected,row,column);
    }

    public void cancelCellEditing() {
        editor.cancelCellEditing();
    }

    public void addCellEditorListener(CellEditorListener l) {
        editor.addCellEditorListener(l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
        editor.removeCellEditorListener(l);
    }
}
