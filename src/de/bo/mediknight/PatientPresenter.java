package de.bo.mediknight;

import java.awt.Component;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JFrame;

import de.bo.mediknight.domain.KnightObject;
import de.bo.mediknight.domain.Lock;
import de.bo.mediknight.domain.Patient;
import de.bo.mediknight.domain.TagesDiagnose;
import de.bo.mediknight.util.ErrorDisplay;
import de.bo.mediknight.widgets.UndoUtilities;


public class PatientPresenter implements Presenter, Commitable, Observer {

    public static void main( final String[] args ) {
	final JFrame f = new JFrame( "Patient Presenter Example" );

	// f.getContentPane().add( example().createView() );

	f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	f.pack();
	f.setVisible( true );
    }

    PatientPanel view;

    PatientModel model;


    public PatientPresenter() {
	this( new PatientModel() );
    }


    public PatientPresenter( final PatientModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
    }


    @Override
    public void commit() {
	savePatient();
	if( model.getPatient().getFullname().length() > 1 ) {
	    MainFrame.getApplication().setTitle( model.getPatient().getFullname() + " - " + MainFrame.NAME );
	    MainFrame.getApplication().setPatientToNavigator( model.getPatient() );
	} else {
	    MainFrame.getApplication().setTitle( MainFrame.NAME );
	}
    }


    @Override
    public Component createView() {
	final PatientPanel panel = new PatientPanel();
	panel.setPresenter( this );
	view = panel;
	view.setFocusOnCombo();

	new LockingListener( this ).applyTo( UndoUtilities.getMutables( panel ) );

	return panel;
    }


    public void deletePatient() {
	// We only can delete a patient if we could acquire ALL depending locks.
	final Vector< Lock > locks = new Vector< Lock >();
	try {
	    final Patient p = model.getPatient();
	    Lock lock = p.acquireLock( LockingInfo.getAspect( p, null ) );
	    if( lock != null ) {
		locks.add( lock );
		final List< TagesDiagnose > l = p.getTagesDiagnosen();
		for( final TagesDiagnose d : l ) {
		    lock = p.acquireLock( LockingInfo.getAspect( p, d ) );
		    if( lock != null ) {
			locks.add( lock );
		    } else {
			throw new MediException( "Diagnosis in use." );
		    }
		}
		p.delete();
	    } else {
		throw new MediException( "Patient in use." );
	    }

	    MainFrame.getApplication().search();
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Löschen fehlgeschlagen!" );
	} catch( final MediException x ) {
	    new ErrorDisplay( x, "Löschen ist jetzt nicht möglich!" );
	} finally {
	    try {
		for( final Lock lock : locks ) {
		    lock.release();
		}
	    } catch( final Exception x ) {
	    }
	}
    }


    public PatientModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	return view;
    }


    public void patientAnlegen() {
	try {
	    if( !savePatient() ) {
		throw new SQLException( "Cannot create patient" );
	    }
	    MainFrame.getApplication().selectPatient( model.getPatient() );
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Patient anlegen fehlgeschlagen!" );
	}
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
	try {
	    final Patient p = (Patient) knightObject;
	    p.recall();
	    getModel().setPatient( p );
	    view.stateChanged( null );
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Neuladen der Komponente fehlgeschlagen!" );
	}
    }


    public boolean savePatient() {

	if( !view.isPatientOK() ) {
	    return false;
	}

	Lock lock = null;

	try {
	    final Patient p = model.getPatient();
	    lock = p.acquireLock( LockingInfo.getAspect( p, null ) );
	    if( lock != null ) {
		view.getContent();
		p.save();
	    }
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Speichern fehlgeschlagen!" );
	    return false;
	} finally {
	    try {
		lock.release();
	    } catch( final Exception x ) {
	    }
	}
	return true;
    }


    @Override
    public void update( final Observable o, final Object arg ) {
	try {
	    ((LockingInfo) o).releaseLastLock();
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Speichern fehlgeschlagen!" );
	}
    }
}