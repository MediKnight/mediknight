package de.bo.mediknight;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.bo.mediknight.domain.Patient;


public class SearchModel {

    PatientHistory	recentPatients  = PatientHistory.getInstance();
    List< Patient >       foundPatients   = new ArrayList< Patient >();

    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();


    public SearchModel() {
    }


    public void addChangeListener( final ChangeListener l ) {
	changeListeners.add( l );
    }


    public void addRecentPatient( final Patient patient ) {
	try {
	    PatientHistory.getInstance().add( patient );
	} catch( final Exception e ) {
	    e.printStackTrace();
	    /** @todo better exception handling */
	}
    }


    void fireChangeEvent() {
	final Iterator< ChangeListener > it = changeListeners.iterator();
	final ChangeEvent e = new ChangeEvent( this );

	while( it.hasNext() ) {
	    it.next().stateChanged( e );
	}
    }


    public List< Patient > getFoundPatients() {
	return foundPatients;
    }


    public PatientHistory getPatientHistory() {
	return recentPatients;
    }


    public List< Patient > getRecentPatientsList() {
	return recentPatients.getList();
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }


    public void setFoundPatients( final List< Patient > patients ) {
	foundPatients = patients;
	fireChangeEvent();
    }


    public void setRecentPatients( final List< Patient > patients ) {
	try {
	    recentPatients.setList( patients );
	    fireChangeEvent();
	} catch( final Exception e ) {
	    e.printStackTrace();
	    /** @todo better exception handling */
	}
    }

}
