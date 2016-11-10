package main.java.de.baltic_online.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Arrays;

// for main-method
import javax.swing.JFrame;

import main.java.de.baltic_online.mediknight.Commitable;
import main.java.de.baltic_online.mediknight.MediKnight;
import main.java.de.baltic_online.mediknight.Presenter;
import main.java.de.baltic_online.mediknight.domain.KnightObject;
import main.java.de.baltic_online.mediknight.domain.RechnungsPosten;


public class MasterDataSupportPresenter implements Presenter, Commitable {

    public static void main( final String[] args ) {
	try {
	    MediKnight.initProperties();
	    MediKnight.initTracer();
	    MediKnight.initDB();
	} catch( final Exception e ) {
	    e.printStackTrace();
	}

	RechnungsPosten[] posten = null;
	try {
	    if( posten == null ) {
		posten = RechnungsPosten.retrieve().toArray( new RechnungsPosten[0] );
		Arrays.sort( posten );
	    }
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}

	final JFrame frame = new JFrame();
	frame.getContentPane().setLayout( new BorderLayout() );
	final MasterDataSupportModel model = new MasterDataSupportModel( posten );
	final MasterDataSupportPresenter presenter = new MasterDataSupportPresenter( model );
	frame.getContentPane().add( presenter.createView(), BorderLayout.CENTER );
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	frame.setVisible( true );
	frame.pack();

    }

    MasterDataSupportModel model;

    MasterDataSupportPanel view;


    public MasterDataSupportPresenter( final MasterDataSupportModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
    }


    @Override
    public void commit() {
    }


    @Override
    public Component createView() {
	view = new MasterDataSupportPanel( model.getRechnungsPosten() );
	view.setPresenter( this );

	return view;
    }


    public MasterDataSupportModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	return null;
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
    }

}