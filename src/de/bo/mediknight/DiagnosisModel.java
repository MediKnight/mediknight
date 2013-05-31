package de.bo.mediknight;

import java.util.*;
import java.sql.*;
import javax.swing.event.*;

import de.bo.mediknight.domain.*;


public class DiagnosisModel {

    Patient patient;
    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();


    public DiagnosisModel() {
    }


    public DiagnosisModel( Patient patient ) {
        this.patient = patient;
    }


    public void setPatient( Patient patient ) {
        this.patient = patient;
        fireChangeEvent();
    }

    public Patient getPatient() {
        return patient;
    }

    public List getTagesDiagnosen() throws SQLException {
    	List list = new ArrayList();
        list = patient.getTagesDiagnosen();
        Collections.sort(list);
        Collections.reverse(list);

        return list;
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