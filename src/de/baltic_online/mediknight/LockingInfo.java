/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */

package de.baltic_online.mediknight;

import java.awt.Component;
import java.sql.SQLException;
import java.util.Observable;

import de.baltic_online.borm.TraceConstants;
import de.baltic_online.mediknight.domain.Lock;
import de.baltic_online.mediknight.domain.Patient;
import de.baltic_online.mediknight.domain.TagesDiagnose;
import de.baltic_online.mediknight.util.MediknightUtilities;


/**
 * Title: Description: Data holder for patient/diagnosis locking<br>
 * Copyright: Copyright (c) 2001<br>
 * Company: Baltic-Online<br>
 * 
 * @author sml
 * @version 1.0
 *
 *          Instances of this class holds informations necessary for locking.
 *          <p>
 *          This class extends <tt>Observable</tt> so all responsible classes are notified if this information was changed by anyone.
 *
 * @see PatientPresenter
 * @see DiagnosisPresenter
 * @see LockingInfo.Data
 */

public class LockingInfo extends Observable {

    /**
     * Simple information bean which implements <tt>Cloneable</tt> and overwrites the <tt>equals</tt> method.
     */
    public static class Data implements Cloneable {

	private Patient       patient;
	private TagesDiagnose diagnosis;


	public Data() {
	    this( null, null );
	}


	public Data( final Patient patient, final TagesDiagnose diagnosis ) {
	    this.patient = patient;
	    this.diagnosis = diagnosis;
	}


	@Override
	public Object clone() {
	    try {
		return super.clone();
	    } catch( final CloneNotSupportedException x ) {
		throw new InternalError( "Clone of LockingInfo.Data failed!" );
	    }
	}


	/**
	 * @return <tt>true</tt> if patient and diagnosis of this instance equals the same type of data considering <tt>null</tt> values
	 */
	@Override
	public boolean equals( final Object o ) {
	    final Data d = (Data) o;
	    return MediknightUtilities.equalsWithNull( patient, d.patient ) && MediknightUtilities.equalsWithNull( diagnosis, d.diagnosis );
	}


	public TagesDiagnose getDiagnosis() {
	    return diagnosis;
	}


	public Patient getPatient() {
	    return patient;
	}


	@Override
	public String toString() {
	    return "Patient: " + patient + ", Diagnosis: " + diagnosis;
	}
    }


    /**
     * This method returns the aspect from the given <tt>data</tt>.
     *
     * @param data
     *            source for calculating aspect
     * @return new aspect value
     */
    public static Lock.Aspect getAspect( final Data data ) {
	return data.diagnosis != null ? new Lock.Aspect( Integer.toString( data.diagnosis.getId() ) ) : new Lock.Aspect();
    }


    /**
     * This method calculates the aspect directly from the given patient and diagnosis.
     *
     * @param patient
     *            the patient (not used in this version)
     * @param diagnosis
     *            the diagnosis
     * @return new aspect value
     */
    public static Lock.Aspect getAspect( final Patient patient, final TagesDiagnose diagnosis ) {
	return getAspect( new Data( patient, diagnosis ) );
    }

    /**
     * The information.
     */
    private Data      data;

    /**
     * The information backup (the previous data).
     */
    private Data      backup;

    /**
     * The last lock acquired by the application.
     */
    private Lock      lastLock;

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
     * This method clones <tt>data</tt> to backup.
     * <p>
     * This method also clears the observable state.
     */
    public void commit() {
	backup = (Data) data.clone();
	clearChanged();
    }


    /**
     * This method returns the aspect from <tt>data</tt>.
     *
     * @return aspect calculated from <tt>data</tt>
     */
    public Lock.Aspect getAspect() {
	return getAspect( data );
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
     * Returns the diagnosis held by <tt>data</tt>.
     *
     * @return application current diagnosis
     */
    public TagesDiagnose getDiagnosis() {
	return data.diagnosis;
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
     * Returns the patient holded by <tt>data</tt>.
     *
     * @return application current patient
     */
    public Patient getPatient() {
	return data.patient;
    }


    /**
     * This method is possible called by the setters.
     */
    private void notifyPresenters() {
	setChanged();
	notifyObservers( backup );
	commit();
    }


    /**
     * Releases the last Lock acquired from a <tt>LockingListener</tt>.
     *
     * @return <tt>true</tt> if a lock would really be released, otherwise <tt>false</tt>
     * @exception SQLException
     *                if there are problems with the database
     */
    public boolean releaseLastLock() throws SQLException {
	if( lastLock != null ) {
	    lastLock.release();
	    lastLock = null;
	    return true;
	}
	return false;
    }


    /**
     * This method clones <tt>backup</tt> to data.
     * <p>
     * This method also clears the observable state.
     */
    public void rollback() {
	data = (Data) backup.clone();
	clearChanged();
    }


    /**
     * Stores the responsible UI component for further use.
     *
     * @see #component
     */
    public void setComponent( final Component c ) {
	component = c;
    }


    /**
     * Sets the current diagnosis.
     * <p>
     * Observers are notified if this diagnosis differs from diagnosis in backup data. The observers get the <tt>backup</tt> as an argument.
     *
     * @param diagnosis
     *            the application current diagnosis
     */
    public void setDiagnosis( final TagesDiagnose diagnosis ) {
	data.diagnosis = diagnosis;
	if( !data.equals( backup ) ) {
	    MainFrame.getTracer().trace( TraceConstants.DEBUG, "Set current diagnosis to " + diagnosis );
	    notifyPresenters();
	}
    }


    /**
     * Stores the last lock for further use.
     *
     * @see #lastLock
     */
    public void setLastLock( final Lock lock ) {
	lastLock = lock;
    }


    /**
     * Sets the current patient.
     * <p>
     * Observers are notified if this patient differs from patient in backup data. The observers get the <tt>backup</tt> as an argument.
     *
     * @param patient
     *            the application current patient
     */
    public void setPatient( final Patient patient ) {
	data.patient = patient;
	if( !data.equals( backup ) ) {
	    MainFrame.getTracer().trace( TraceConstants.DEBUG, "Set current patient to " + patient );
	    notifyPresenters();
	}
    }
}
