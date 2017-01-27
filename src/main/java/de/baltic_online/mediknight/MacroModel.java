package main.java.de.baltic_online.mediknight;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.java.de.baltic_online.mediknight.domain.Rechnung;
import main.java.de.baltic_online.mediknight.domain.RechnungsGruppe;


public class MacroModel {

    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();
    BillEntry[]		  entries;
    Rechnung		  rechnung;
    RechnungsGruppe	  rechnungsGruppe;


    public MacroModel() {
    }


    public MacroModel( final BillEntry[] entries, final Rechnung rechnung ) {
	this.entries = entries;
	this.rechnung = rechnung;
    }


    public MacroModel( final Rechnung rechnung ) {
	this( null, rechnung );
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


    public Rechnung getRechnung() {
	return rechnung;
    }


    public RechnungsGruppe getRechnungsGruppe() {
	return rechnungsGruppe;
    }


    public boolean hasContent() {
	if( entries == null || entries.length < 1 ) {
	    return false;
	} else {
	    return true;
	}
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }


    public void setRechnung( final Rechnung rechnung ) {
	this.rechnung = rechnung;
	fireChangeEvent();
    }


    public void setRechnungsGruppe( final RechnungsGruppe rechnungsGruppe ) {
	this.rechnungsGruppe = rechnungsGruppe;
    }
}