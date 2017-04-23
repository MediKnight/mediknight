package main.java.de.baltic_online.mediknight.tables;

import java.awt.Component;
import java.util.EventObject;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;


/**
 * Displays an editable and scrollable object for Strings.
 * 
 * @author ECSTRPL
 */
public class StringTableCellEditor implements TableCellEditor {

    private final JScrollPane			       scrollPane;
    private final JEditorPane			       stringValue;
    private CopyOnWriteArrayList< CellEditorListener > listeners;


    public StringTableCellEditor( final JTable table ) {
	scrollPane = new JScrollPane();
	stringValue = new JEditorPane();
	listeners = new CopyOnWriteArrayList<>();

	scrollPane.getViewport().add( stringValue );
	stringValue.setFont( table.getFont() );
	stringValue.setOpaque( true );
	stringValue.setBorder( null );
	stringValue.setBackground( table.getBackground() );
    }


    /**
     * Returns the JScrollPane GUI-Object initialized with the corresponding data.
     */
    @Override
    public Component getTableCellEditorComponent( final JTable table, final Object object, final boolean isSelected, final int row, final int column ) {
	if( object != null ) {
	    stringValue.setText( (String) object );
	}

	return scrollPane;
    }


    @Override
    public boolean isCellEditable( final EventObject event ) {
	return true;
    }


    @Override
    public void removeCellEditorListener( final CellEditorListener listener ) {
	for( CellEditorListener elem : listeners ) {
	    if( elem == listener ) {
		listeners.remove( elem );
		break;
	    }
	}
    }


    @Override
    public boolean shouldSelectCell( final EventObject event ) {
	return true;
    }


    @Override
    public boolean stopCellEditing() {
	for( CellEditorListener elem : listeners ) {
	    elem.editingStopped( new ChangeEvent( this ) );
	}

	return true;
    }


    @Override
    public Object getCellEditorValue() {
	return stringValue.getText();
    }


    @Override
    public void cancelCellEditing() {
	for( CellEditorListener elem : listeners ) {
	    elem.editingCanceled( new ChangeEvent( this ) );
	}
    }


    @Override
    public void addCellEditorListener( CellEditorListener listener ) {
	listeners.add( listener );
    }
}
