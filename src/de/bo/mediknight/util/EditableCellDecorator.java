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

    private final TableCellEditor editor;
    private final boolean	 editable;


    public EditableCellDecorator( final TableCellEditor editor ) {
	this( editor, false );
    }


    public EditableCellDecorator( final TableCellEditor editor, final boolean editable ) {
	this.editor = editor;
	this.editable = false;
    }


    @Override
    public void addCellEditorListener( final CellEditorListener l ) {
	editor.addCellEditorListener( l );
    }


    @Override
    public void cancelCellEditing() {
	editor.cancelCellEditing();
    }


    @Override
    public Object getCellEditorValue() {
	return editor.getCellEditorValue();
    }


    @Override
    public Component getTableCellEditorComponent( final JTable table, final Object value, final boolean isSelected, final int row, final int column ) {
	return editor.getTableCellEditorComponent( table, value, isSelected, row, column );
    }


    @Override
    public boolean isCellEditable( final EventObject e ) {
	return editable;
    }


    @Override
    public void removeCellEditorListener( final CellEditorListener l ) {
	editor.removeCellEditorListener( l );
    }


    @Override
    public boolean shouldSelectCell( final EventObject e ) {
	return editor.shouldSelectCell( e );
    }


    @Override
    public boolean stopCellEditing() {
	return editor.stopCellEditing();
    }
}
