package main.java.de.baltic_online.mediknight.util;

import java.awt.Component;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.EventObject;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

import com.toedter.calendar.JDateChooser;


public class DateChooserTableCellEditor implements TableCellEditor {

    private final JDateChooser dateChooser;   
    private CopyOnWriteArrayList<CellEditorListener> listeners;


    /**
     * @param dateChooser
     */
    public DateChooserTableCellEditor( final JTable table ) {
	final Instant inst = LocalDate.now().atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant();
	dateChooser = new JDateChooser( Date.from( inst ) );
	listeners = new CopyOnWriteArrayList<>();

	dateChooser.setFont( table.getFont() );
	dateChooser.setOpaque( true );
	dateChooser.setBorder( null );
	dateChooser.setBackground( table.getBackground() );

	final int preferredHeight = (int) dateChooser.getPreferredSize().getHeight();
	if( table.getRowHeight() < preferredHeight ) {
	    table.setRowHeight( preferredHeight );
	}
    }


    @Override
    public void addCellEditorListener( final CellEditorListener listener ) {
	listeners.add( listener );
    }


    @Override
    public void cancelCellEditing() {
	for(CellEditorListener elem : listeners ) {
	    elem.editingCanceled( new ChangeEvent( this ) );
	}
    }


    @Override
    public Object getCellEditorValue() {
	return Instant.ofEpochMilli( dateChooser.getDate().getTime() ).atZone( ZoneId.systemDefault() ).toLocalDate();
    }


    @Override
    public Component getTableCellEditorComponent( final JTable table, final Object object, final boolean renderHighlighted, final int row, final int column ) {
	if( object != null ) {
	    final LocalDate tmpDate = (LocalDate) object;
	    
	    dateChooser.setDate( Date.from( tmpDate.atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() ) );
	}
	
	dateChooser.setMinimumSize( dateChooser.getPreferredSize() );

	return dateChooser;
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
    
    public int getClickCount() {
	return 1;
    }
    
    public Component getComponent() {
	return dateChooser;
    }

}
