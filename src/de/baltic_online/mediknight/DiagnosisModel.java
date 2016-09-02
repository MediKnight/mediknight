package de.baltic_online.mediknight;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.baltic_online.mediknight.domain.Patient;
import de.baltic_online.mediknight.domain.TagesDiagnose;


public class DiagnosisModel {

    Patient	       patient;
    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();


    public DiagnosisModel() {
    }


    public DiagnosisModel( final Patient patient ) {
	this.patient = patient;
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


    public int getNrOfDatasets() {
	int result = 0;

	try {
	    result = patient.getTagesDiagnosen().size();
	} catch( final SQLException e ) {
	    e.printStackTrace();
	}

	return result;
    }


    public Patient getPatient() {
	return patient;
    }


    public List< TagesDiagnose > getTagesDiagnosen() throws SQLException {
	List< TagesDiagnose > list = new ArrayList< TagesDiagnose >();
	list = patient.getTagesDiagnosen();
	Collections.sort( list );
	Collections.reverse( list );

	return list;
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }


    public void setPatient( final Patient patient ) {
	this.patient = patient;
	fireChangeEvent();
    }
}