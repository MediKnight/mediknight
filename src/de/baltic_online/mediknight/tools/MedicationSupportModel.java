package de.baltic_online.mediknight.tools;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.baltic_online.mediknight.domain.VerordnungsPosten;


public class MedicationSupportModel {

    VerordnungsPosten[]   posten;

    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();


    public MedicationSupportModel() {
    }


    public void addChangeListener( final ChangeListener l ) {
	changeListeners.add( l );
    }


    public void delete( final int i ) throws SQLException {
	posten[i].delete();
    }


    void fireChangeEvent() {
	final Iterator< ChangeListener > it = changeListeners.iterator();
	final ChangeEvent e = new ChangeEvent( this );

	while( it.hasNext() ) {
	    it.next().stateChanged( e );
	}
    }


    public VerordnungsPosten[] getVerordnungsposten() throws SQLException {
	try {
	    posten = VerordnungsPosten.retrieve().toArray( new VerordnungsPosten[0] );
	    Arrays.sort( posten );
	    return posten;
	} catch( final NullPointerException e ) {
	    return new VerordnungsPosten[0];
	}
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }
}