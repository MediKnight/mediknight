package de.baltic_online.mediknight.widgets;

import java.awt.Component;
import java.util.EventObject;


public class JTable extends javax.swing.JTable {

    private static final long serialVersionUID = 1L;


    @Override
    public boolean editCellAt( final int row, final int column, final EventObject e ) {
	final boolean canEdit = super.editCellAt( row, column, e );
	if( canEdit ) {
	    final Component c = getEditorComponent();
	    if( c instanceof javax.swing.JTextField ) {
		((javax.swing.JTextField) c).select( 0, 9999 );
		c.repaint();
	    }
	}
	return canEdit;
    }
}