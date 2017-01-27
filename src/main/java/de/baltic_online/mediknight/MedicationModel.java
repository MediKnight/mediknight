package main.java.de.baltic_online.mediknight;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;
import main.java.de.baltic_online.mediknight.domain.Verordnung;
import main.java.de.baltic_online.mediknight.domain.VerordnungsPosten;


public class MedicationModel {

    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();
    TagesDiagnose	  diagnose;
    VerordnungsPosten[]	  posten;


    public MedicationModel() {
    }


    public MedicationModel( final TagesDiagnose diagnose ) {
	this.diagnose = diagnose;
	try {
	    diagnose.recall();
	} catch( final SQLException ex ) {
	}
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


    public TagesDiagnose getDiagnose() {
	return diagnose;
    }


    public Verordnung getVerordnung() {
	try {
	    return diagnose.getVerordnung();
	} catch( final SQLException e ) {
	    /** @todo Exception reporting. */
	}

	return null;
    }


    public VerordnungsPosten[] getVerordnungsposten() throws SQLException {
	if( posten != null ) {
	    return posten;
	}
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


    public void setDiagnose( final TagesDiagnose diagnose ) {
	this.diagnose = diagnose;
	fireChangeEvent();
    }

}