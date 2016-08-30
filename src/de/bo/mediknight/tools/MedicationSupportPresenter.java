package de.bo.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.Component;

// for main-method
import javax.swing.JFrame;

import de.bo.mediknight.Commitable;
import de.bo.mediknight.MainFrame;
import de.bo.mediknight.Presenter;
import de.bo.mediknight.domain.KnightObject;
import de.bo.mediknight.domain.VerordnungsPosten;


public class MedicationSupportPresenter implements Presenter, Commitable {

    public static void main( final String[] args ) {
	try {
	    MainFrame.initProperties();
	    MainFrame.initTracer();
	    MainFrame.initDB();
	} catch( final Exception e ) {
	    e.printStackTrace();
	}

	final JFrame frame = new JFrame();
	frame.getContentPane().setLayout( new BorderLayout() );
	final MedicationSupportModel model = new MedicationSupportModel();
	final MedicationSupportPresenter presenter = new MedicationSupportPresenter( model );

	frame.getContentPane().add( presenter.createView(), BorderLayout.CENTER );
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	frame.setVisible( true );
	frame.pack();

    }

    MedicationSupportPanel view;

    MedicationSupportModel model;


    public MedicationSupportPresenter() {
    }


    public MedicationSupportPresenter( final MedicationSupportModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
    }


    public void addItem( final VerordnungsPosten posten ) {

	try {
	    posten.save();
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
	view.update();
    }


    @Override
    public void commit() {
	view.saveText();
    }


    @Override
    public Component createView() {
	view = new MedicationSupportPanel();
	view.setPresenter( this );

	return view;
    }


    public void deleteItem( final int[] rows ) {
	try {
	    for( final int row : rows ) {
		model.delete( row );
	    }
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
	view.update();
    }


    public MedicationSupportModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	return null;
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
    }


    public void saveItem( final VerordnungsPosten p ) {
	try {
	    p.save();
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
    }
}