package de.baltic_online.mediknight;

import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

import de.baltic_online.mediknight.domain.KnightObject;
import de.baltic_online.mediknight.domain.Patient;


public class SearchPresenter implements Presenter, Commitable {

    public static void main( final String[] args ) {
	final JFrame f = new JFrame( "Search Presenter Example" );

	// f.getContentPane().add( example().createView() );

	f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	f.pack();
	f.setVisible( true );
    }

    SearchModel model;

    SearchPanel view;

    // specifies whether to trigger ListSelectionEvents
    boolean     triggerSelectionEvents = true;


    public SearchPresenter() {
	this( new SearchModel() );
    }


    public SearchPresenter( final SearchModel model ) {
	this.model = model;

	try {
	    final PatientHistory history = PatientHistory.getInstance();
	    model.setRecentPatients( history.load() );
	} catch( final Exception e ) {
	    // de.baltic_online.mediknight.panes.MediknightPane.showExceptionDialog(e);
	    e.printStackTrace();
	}
    }


    @Override
    public void activate() {
	view.setFocusOnSearchTF();
    }


    public void clearHistory() {
	model.setRecentPatients( new ArrayList< Patient >() );
    }


    public void clearSelection( final JList< Patient > list ) {
	if( !triggerSelectionEvents ) {
	    return;
	}

	triggerSelectionEvents = false;
	list.setSelectedIndices( new int[] {} );
	triggerSelectionEvents = true;
    }


    @Override
    public void commit() {
	try {
	    model.getPatientHistory().save();
	} catch( final SQLException e ) {
	    e.printStackTrace();
	    /** @todo Exception */
	}
    }


    @Override
    public Component createView() {
	final SearchPanel panel = new SearchPanel();
	panel.setPresenter( this );
	view = panel;
	view.setFocusOnSearchTF();

	return panel;
    }


    public SearchModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	return null;
    }


    public void indexButtonPressed( final JButton b ) {
	searchFor( b.getText() );
    }


    public void patientSelected() {
	final Patient patient = (Patient) view.getSelection();
	if( patient == null ) {
	    JOptionPane.showMessageDialog( view, "Bitte wählen sie einen Patienten!", "Kein Patient gewählt", JOptionPane.ERROR_MESSAGE );
	    return;
	}

	model.addRecentPatient( patient );

	MainFrame.getApplication().setWaitCursor();

	try {
	    MainFrame.getApplication().selectPatient( patient );
	} catch( final Exception e ) {
	    e.printStackTrace();
	    /** @todo Exceptions have to be reported visually. */
	} finally {
	    MainFrame.getApplication().setDefaultCursor();
	}
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
    }


    public void searchFor( final String s ) {

	List< Patient > data = null;
	try {
	    MainFrame.getApplication().setWaitCursor();
	    data = Patient.retrieve( s );
	    Collections.sort( data );

	    model.setFoundPatients( data );
	} catch( final SQLException e ) {
	    e.printStackTrace();
	    /** @todo Exceptions have to be reported visually. */
	} finally {
	    MainFrame.getApplication().setDefaultCursor();
	    // resetState(data.length == 0);

	    // don't set the focus on an empty result list
	    if( data.size() > 0 ) {
		view.setFocusOnPatientList();
	    } else {
		// view.setFocusOnSearchTF();

		view.setSelectedTF();
	    }
	}
    }
}