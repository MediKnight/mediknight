package main.java.de.baltic_online.mediknight;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.java.de.baltic_online.mediknight.domain.RechnungsGruppe;


public class CreateMacroModel {

    BillEntry[]	   entries;
    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();


    public CreateMacroModel( final BillEntry[] entries ) {
	this.entries = entries;
    }


    public void addChangeListener( final ChangeListener l ) {
	changeListeners.add( l );
    }


    void fireChangeEvent() {
	final Iterator< ChangeListener > it = changeListeners.iterator();
	final ChangeEvent e = new ChangeEvent( this );

	while( it.hasNext() ) {
	    it.next().stateChanged( e );
	}
    }


    public List< RechnungsGruppe > getComponentList() {
	try {
	    return RechnungsGruppe.retrieve();
	} catch( final SQLException e ) {
	    /** @todo Exception reporting. */
	}

	return null;
    }


    public BillEntry[] getEntries() {
	return entries;
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }
}