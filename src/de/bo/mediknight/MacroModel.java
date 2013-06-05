package de.bo.mediknight;

import java.util.*;
import java.sql.*;
import javax.swing.event.*;

import de.bo.mediknight.domain.*;

public class MacroModel {
    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
    BillEntry[] entries;
    Rechnung rechnung;
    RechnungsGruppe rechnungsGruppe;

    public MacroModel() {
    }

    public MacroModel( BillEntry[] entries, Rechnung rechnung ) {
        this.entries = entries;
	this.rechnung = rechnung;
    }

    public MacroModel( Rechnung rechnung ) {
	this(null, rechnung);
    }

    public void addChangeListener( ChangeListener l ) {
        changeListeners.add( l );
    }

    public void removeChangeListener( ChangeListener l ) {
        changeListeners.remove( l );
    }

    void fireChangeEvent() {
        Iterator<ChangeListener> it = changeListeners.iterator();
        ChangeEvent e = new ChangeEvent( this );

        while( it.hasNext() ) {
            it.next().stateChanged(e);
        }
    }

    public void setRechnung(Rechnung rechnung) {
        this.rechnung = rechnung;
        fireChangeEvent();
    }

    public Rechnung getRechnung() {
        return rechnung;
    }

    public BillEntry[] getEntries() {
	return entries;
    }

    public boolean hasContent() {
	if ((entries == null) || (entries.length < 1))
	    return false;
	else
	    return true;
    }

    public RechnungsGruppe getRechnungsGruppe() {
	return rechnungsGruppe;
    }

    public void setRechnungsGruppe(RechnungsGruppe rechnungsGruppe) {
	this.rechnungsGruppe = rechnungsGruppe;
    }

    public List<RechnungsGruppe> getComponentList() {
        try {
            return RechnungsGruppe.retrieve();
        } catch( SQLException e ) {
            /** @todo Exception reporting. */
        }

        return null;
    }
}