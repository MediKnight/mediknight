package de.bo.mediknight;

import java.util.*;
import java.sql.*;
import javax.swing.event.*;

import de.bo.mediknight.domain.*;

public class CreateMacroModel {

    BillEntry[] entries;
    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();

    public CreateMacroModel( BillEntry[] entries ) {
        this.entries = entries;
    }


    public BillEntry[] getEntries() {
        return entries;
    }

    public List getComponentList() {
        try {
            return RechnungsGruppe.retrieve();
        } catch( SQLException e ) {
            /** @todo Exception reporting. */
        }

        return null;
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
}