package de.bo.mediknight;

import java.util.EventObject;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.table.*;

import de.bo.mediknight.borm.TraceConstants;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.widgets.*;


public class LockingListener implements
MutableChangeListener, TableModelListener, Runnable {

    public static final int LOCK_TIMEOUT = 4800;  // Set to 1.5 hours

    private Commitable commitable;
    private Component component;

    private boolean pollRun;
    private Thread poller;
    private Patient patient;
    private TagesDiagnose diagnosis;
    private Lock lock;
    private Lock.Aspect aspect;

    public LockingListener(Commitable commitable) {
        this.commitable = commitable;

        pollRun = false;
        poller = null;
        patient = null;
        aspect = null;
    }

    public void applyTo(Mutable[] mut) {
        for ( int i=0; i<mut.length; i++ ) {
            Mutable m = mut[i];
            if ( m != null ) {
                m.addMutableChangeListener(this);
            }
        }
    }

    public void applyTo(JTable table) {
        table.getModel().addTableModelListener(this);
    }

    public void tableChanged(TableModelEvent e) {
        stateChanged(e);
    }

    public void mutableStateChanged(MutableChangeEvent e) {
        stateChanged(e);
    }

    public void stateChanged(EventObject e) {
        try {
            MainFrame app = MainFrame.getApplication();
            LockingInfo li = app.getLockingInfo();
            patient = li.getPatient();
            diagnosis = li.getDiagnosis();
            aspect = li.getAspect();
            component = commitable.getResponsibleComponent();

            if ( patient != null ) {
                Lock lock = patient.acquireLock(aspect,LOCK_TIMEOUT);
                if ( lock != null ) {
                    li.setLastLock(lock);
                    li.setComponent(component);
                } else {
                    app.enableEditing(false);
                    if ( poller == null ) {
                        poller = new Thread(this);
                        pollRun = true;
                        poller.start();
                    }
                }
            }
        }
        catch ( Exception x ) {
            MainFrame.getTracer().trace(TraceConstants.ERROR_T,x);
        }
    }

    public void run() {
        try {
            lock = null;
            while ( pollRun &&
                (lock=patient.acquireLock(aspect,LOCK_TIMEOUT)) == null ) {
                Thread.sleep(3000);
            }

            if ( lock != null ) {
                commitable.reload(
                    component,
                    (diagnosis!=null) ? (KnightObject)diagnosis : (KnightObject)patient);
                lock.release();
            }

            MainFrame.getApplication().enableEditing(true);

            poller = null;
            pollRun = false;
        }
        catch ( Exception x ) {
            MainFrame.getTracer().trace(TraceConstants.ERROR_T,x);
        }
    }
}
