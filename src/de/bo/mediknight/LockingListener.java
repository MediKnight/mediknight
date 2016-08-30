package de.bo.mediknight;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import de.baltic_online.borm.TraceConstants;
import de.bo.mediknight.domain.KnightObject;
import de.bo.mediknight.domain.Lock;
import de.bo.mediknight.domain.Patient;
import de.bo.mediknight.domain.TagesDiagnose;
import de.bo.mediknight.widgets.Mutable;
import de.bo.mediknight.widgets.MutableChangeEvent;
import de.bo.mediknight.widgets.MutableChangeListener;


public class LockingListener implements MutableChangeListener, TableModelListener, Runnable {

    public static final int  LOCK_TIMEOUT = 4800; // Set to 1.5 hours

    private final Commitable commitable;
    private Component	component;

    private boolean	  pollRun;
    private Thread	   poller;
    private Patient	  patient;
    private TagesDiagnose    diagnosis;
    private Lock	     lock;
    private Lock.Aspect      aspect;


    public LockingListener( final Commitable commitable ) {
	this.commitable = commitable;

	pollRun = false;
	poller = null;
	patient = null;
	aspect = null;
    }


    public void applyTo( final JTable table ) {
	table.getModel().addTableModelListener( this );
    }


    public void applyTo( final Mutable[] mut ) {
	for( final Mutable m : mut ) {
	    if( m != null ) {
		m.addMutableChangeListener( this );
	    }
	}
    }


    @Override
    public void mutableStateChanged( final MutableChangeEvent e ) {
	stateChanged( e );
    }


    @Override
    public void run() {
	try {
	    lock = null;
	    while( pollRun && (lock = patient.acquireLock( aspect, LOCK_TIMEOUT )) == null ) {
		Thread.sleep( 3000 );
	    }

	    if( lock != null ) {
		commitable.reload( component, diagnosis != null ? (KnightObject) diagnosis : (KnightObject) patient );
		lock.release();
	    }

	    MainFrame.getApplication().enableEditing( true );

	    poller = null;
	    pollRun = false;
	} catch( final Exception x ) {
	    MainFrame.getTracer().trace( TraceConstants.ERROR, x );
	}
    }


    public void stateChanged( final EventObject e ) {
	try {
	    final MainFrame app = MainFrame.getApplication();
	    final LockingInfo li = app.getLockingInfo();
	    patient = li.getPatient();
	    diagnosis = li.getDiagnosis();
	    aspect = li.getAspect();
	    component = commitable.getResponsibleComponent();

	    if( patient != null ) {
		final Lock lock = patient.acquireLock( aspect, LOCK_TIMEOUT );
		if( lock != null ) {
		    li.setLastLock( lock );
		    li.setComponent( component );
		} else {
		    app.enableEditing( false );
		    if( poller == null ) {
			poller = new Thread( this );
			pollRun = true;
			poller.start();
		    }
		}
	    }
	} catch( final Exception x ) {
	    MainFrame.getTracer().trace( TraceConstants.ERROR, x );
	}
    }


    @Override
    public void tableChanged( final TableModelEvent e ) {
	stateChanged( e );
    }
}
