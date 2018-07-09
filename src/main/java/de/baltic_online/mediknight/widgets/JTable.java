package main.java.de.baltic_online.mediknight.widgets;

import java.awt.Component;
import java.awt.Rectangle;
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
    
    
    @Override
    public void changeSelection(    int row, int column, boolean toggle, boolean extend)
    {
        super.changeSelection(row, column, toggle, extend);
     
        if (editCellAt(row, column))
        {
            Component editor = getEditorComponent();
            editor.requestFocusInWindow();
        }
    }
    
    
    public Rectangle getCellBounds( int i, double height ) {
	JTable jt= new JTable();
        jt.getX();
	final Rectangle cellBounds= new Rectangle(jt.getX(), jt.getY(), jt.getWidth(), jt.getHeight());	
	return cellBounds;
    }
}