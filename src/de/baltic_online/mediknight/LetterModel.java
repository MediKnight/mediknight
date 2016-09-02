package de.baltic_online.mediknight;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.baltic_online.mediknight.domain.Rechnung;
import de.baltic_online.mediknight.domain.RechnungsPosten;
import de.baltic_online.mediknight.domain.TagesDiagnose;


public class LetterModel {

    Rechnung	      rechnung;
    RechnungsPosten[]     rechnungsPosten = null;
    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();


    public LetterModel( final Rechnung rechnung ) {
	this.rechnung = rechnung;
	try {
	    rechnung.recall();
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


    public Rechnung getRechnung() {
	return rechnung;
    }


    public List< TagesDiagnose > getTagesDiagnosen() {
	try {
	    return rechnung.getPatient().getTagesDiagnosen();
	} catch( final SQLException e ) {
	    e.printStackTrace();
	    /** @todo Exception reporting. */
	}

	return null;
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }


    public void setRechnung( final Rechnung rechnung ) {
	this.rechnung = rechnung;
	fireChangeEvent();
    }
}