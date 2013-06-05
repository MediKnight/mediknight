package de.bo.mediknight;

import java.util.*;
import javax.swing.event.*;

import de.bo.mediknight.PatientHistory;
import de.bo.mediknight.domain.Patient;

public class SearchModel {

    PatientHistory recentPatients = PatientHistory.getInstance();
    List<Patient> foundPatients = new ArrayList<Patient>();

    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();

    public SearchModel() {
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

    public void setRecentPatients( List<Patient> patients ) {
        try {
            recentPatients.setList(patients);
            fireChangeEvent();
        } catch (Exception e) {
            e.printStackTrace();
            /** @todo better exception handling */
        }
    }

    public PatientHistory getPatientHistory() {
        return recentPatients;
    }

    public List<Patient> getRecentPatientsList() {
        return recentPatients.getList();
    }

    public void addRecentPatient(Patient patient) {
        try {
            PatientHistory.getInstance().add(patient);
        } catch (Exception e) {
            e.printStackTrace();
            /** @todo better exception handling */
        }
    }

    public void setFoundPatients( List<Patient> patients ) {
        foundPatients = patients;
        fireChangeEvent();
    }

    public List<Patient> getFoundPatients() {
        return foundPatients;
    }

}
