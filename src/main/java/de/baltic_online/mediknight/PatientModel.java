package main.java.de.baltic_online.mediknight;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.java.de.baltic_online.mediknight.domain.Patient;


public class PatientModel {

    boolean	       newPatient      = false;

    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();
    Patient	       patient;


    public PatientModel() {
    }


    public PatientModel( final Patient patient ) {
	this.patient = patient;
	try {
	    patient.recall();
	} catch( final java.sql.SQLException ex ) {
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


    public Patient getPatient() {
	return patient;
    }


    public boolean isNewPatient() {
	return newPatient;
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }


    public void setNewPatient( final boolean state ) {
	newPatient = state;
    }


    public void setPatient( final Patient patient ) {
	this.patient = patient;
	fireChangeEvent();
    }
}