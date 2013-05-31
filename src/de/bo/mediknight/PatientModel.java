
package de.bo.mediknight;

import java.util.*;
import de.bo.mediknight.domain.Patient;
import javax.swing.event.*;

public class PatientModel {

    boolean newPatient = false;

    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
    Patient patient;


    public PatientModel() {
    }

    public PatientModel( Patient patient ) {
        this.patient = patient;
        try {
            patient.recall();
        } catch (java.sql.SQLException ex) {
        }
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

    public void setPatient(Patient patient) {
        this.patient = patient;
        fireChangeEvent();
    }

    public Patient getPatient() {
        return this.patient;
    }


    public void setNewPatient( boolean state ) {
        newPatient = state;
    }

    public boolean isNewPatient() {
        return newPatient;
    }
}