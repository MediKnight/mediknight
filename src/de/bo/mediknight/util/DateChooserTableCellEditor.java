package de.bo.mediknight.util;

import java.awt.Component;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import com.toedter.calendar.JDateChooser;




public class DateChooserTableCellEditor implements TableCellEditor {
    
    private final JDateChooser dateChooser;
    private int lastRow;
    private int lastColumn;
    private JTable table;

    /**
     * @param dateChooser
     */
    public DateChooserTableCellEditor( final JTable table ) {
        final Instant inst = LocalDate.now().atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant();
        this.table = table;
        dateChooser = new JDateChooser( Date.from( inst ) );

        final int preferredHeight = (int) dateChooser.getPreferredSize().getHeight();
        if( table.getRowHeight() < preferredHeight ) {
            table.setRowHeight( preferredHeight );
        }
    }

    @Override
    public void addCellEditorListener( final CellEditorListener arg0) {        
    }

    @Override
    public void cancelCellEditing() {        
    }

    @Override
    public Object getCellEditorValue() {
        return Instant.ofEpochMilli( dateChooser.getDate().getTime() ).atZone( ZoneId.systemDefault() ).toLocalDate(); 
    }

    @Override
    public boolean isCellEditable(EventObject arg0) {
        return true;
    }

    @Override
    public void removeCellEditorListener(CellEditorListener arg0) {
    }

    @Override
    public boolean shouldSelectCell(EventObject arg0) {
        return true;
    }

    @Override
    public boolean stopCellEditing() {
        table.setValueAt( java.sql.Date.from( dateChooser.getDate().toInstant() ), lastRow, lastColumn);
        
        return true;
    }

    @Override
    public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int row, int column ) {
        if( arg1 != null ) {
            dateChooser.setDate( (Date) arg1 );
            lastRow = row;
            lastColumn = column;
        }
        
        return dateChooser;
    }

}
