/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */

package de.bo.mediknight;

import java.awt.Component;

import java.util.*;
import java.sql.SQLException;

import de.bo.mediknight.util.MediknightUtilities;
import de.bo.mediknight.domain.*;

/**
 * Title:
 * Description: Data holder for patient/diagnosis locking<br>
 * Copyright:   Copyright (c) 2001<br>
 * Company:     Baltic-Online<br>
 * @author      sml
 * @version 1.0
 *
 * Instances of this class holds informations necessary for locking.<p>
 * This class extends <tt>Observable</tt> so all responsible classes are
 * notified if this information was changed by anyone.
 *
 * @see PatientPresenter
 * @see DiagnosisPresenter
 * @see LockingInfo.Data
 */

public class LockingInfo extends Observable {

    /**
     * The information.
     */
    private Data data;

    /**
     * The information backup (the previous data).
     */
    private Data backup;

    /**
     * The last lock acquired by the application.
     */
    private Lock lastLock;

    /**
     * The last UI component which holds data altered by the user.
     */
    private Component component;

    /**
     * Creates info with empty data and backup information.
     */
    public LockingInfo() {
        data = new Data();
        backup = new Data();
    }

    /**
     * Returns the patient holded by <tt>data</tt>.
     *
     * @return application current patient
     */
    public Patient getPatient() {
        return data.patient;
    }

    /**
     * Returns the diagnosis held by <tt>data</tt>.
     *
     * @return application current diagnosis
     */
    public TagesDiagnose getDiagnosis() {
        return data.diagnosis;
    }

    /**
     * Sets the current patient.
     * <p>
     * Observers are notified if this patient differs from patient in backup data.
     * The observers get the <tt>backup</tt> as an argument.
     *
     * @param patient the application current patient
     */
    public void setPatient(Patient patient) {
        data.patient = patient;
        if ( !data.equals(backup) ) {
            MainFrame.getTracer().trace(MainFrame.DEBUG,"Set current patient to "+patient);
            notifyPresenters();
        }
    }

    /**
     * Sets the current diagnosis.
     * <p>
     * Observers are notified if this diagnosis differs from diagnosis in backup data.
     * The observers get the <tt>backup</tt> as an argument.
     *
     * @param diagnosis the application current diagnosis
     */
    public void setDiagnosis(TagesDiagnose diagnosis) {
        data.diagnosis = diagnosis;
        if ( !data.equals(backup) ) {
            MainFrame.getTracer().trace(MainFrame.DEBUG,"Set current diagnosis to "+diagnosis);
            notifyPresenters();
        }
    }

    /**
     * This method is possible called by the setters.
     */
    private void notifyPresenters() {
        setChanged();
        notifyObservers(backup);
        commit();
    }

    /**
     * This method clones <tt>data</tt> to backup.
     * <p>
     * This method also clears the observable state.
     */
    public void commit() {
        backup = (Data)data.clone();
        clearChanged();
    }

    /**
     * This method clones <tt>backup</tt> to data.
     * <p>
     * This method also clears the observable state.
     */
    public void rollback() {
        data = (Data)backup.clone();
        clearChanged();
    }

    /**
     * This method returns the aspect from <tt>data</tt>.
     *
     * @return aspect calculated from <tt>data</tt>
     */
    public Lock.Aspect getAspect() {
        return getAspect(data);
    }

    /**
     * This method returns the aspect from the given <tt>data</tt>.
     *
     * @param data source for calculating aspect
     * @return new aspect value
     */
    public static Lock.Aspect getAspect(Data data) {
        return (data.diagnosis != null) ?
            new Lock.Aspect(Integer.toString(data.diagnosis.getId())) :
            new Lock.Aspect();
    }

    /**
     * This method calculates the aspect directly from the given patient
     * and diagnosis.
     *
     * @param patient the patient (not used in this version)
     * @param diagnosis the diagnosis
     * @return new aspect value
     */
    public static Lock.Aspect getAspect(Patient patient,TagesDiagnose diagnosis) {
        return getAspect(new Data(patient,diagnosis));
    }

    /**
     * Stores the last lock for further use.
     *
     * @see #lastLock
     */
    public void setLastLock(Lock lock) {
        lastLock = lock;
    }

    /**
     * Returns the last lock.
     *
     * @see #lastLock
     */
    public Lock getLastLock() {
        return lastLock;
    }

    /**
     * Stores the responsible UI component for further use.
     *
     * @see #component
     */
    public void setComponent(Component c) {
        component = c;
    }

    /**
     * Returns the responsible UI component
     *
     * @see #component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Releases the last Lock acquired from a <tt>LockingListener</tt>.
     *
     * @return <tt>true</tt> if a lock would really be released, otherwise
     * <tt>false</tt>
     * @exception SQLException if there are problems with the database
     */
    public boolean releaseLastLock() throws SQLException {
        if ( lastLock != null ) {
            lastLock.release();
            lastLock = null;
            return true;
        }
        return false;
    }

    /**
     * Simple information bean which implements <tt>Cloneable</tt> and
     * overwrites the <tt>equals</tt> method.
     */
    public static class Data implements Cloneable
    {
        private Patient patient;
        private TagesDiagnose diagnosis;

        public Data() {
            this(null,null);
        }

        public Data(Patient patient,TagesDiagnose diagnosis) {
            this.patient = patient;
            this.diagnosis = diagnosis;
        }

        public Patient getPatient() {
            return patient;
        }

        public TagesDiagnose getDiagnosis() {
            return diagnosis;
        }

        /**
         * @return <tt>true</tt> if patient and diagnosis of this instance
         * equals the same type of data considering <tt>null</tt> values
         */
        public boolean equals(Object o) {
            Data d = (Data)o;
            return
                MediknightUtilities.equalsWithNull(patient,d.patient) &&
                MediknightUtilities.equalsWithNull(diagnosis,d.diagnosis);
        }

        public Object clone() {
            try {
                return super.clone();
            }
            catch ( CloneNotSupportedException x ) {
                throw new InternalError("Clone of LockingInfo.Data failed!");
            }
        }

        public String toString() {
            return "Patient: "+patient+", Diagnosis: "+diagnosis;
        }
    }
}
