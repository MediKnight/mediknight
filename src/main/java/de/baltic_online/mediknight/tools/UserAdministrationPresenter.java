package main.java.de.baltic_online.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;

import main.java.de.baltic_online.mediknight.Commitable;
import main.java.de.baltic_online.mediknight.MainFrame;
import main.java.de.baltic_online.mediknight.Presenter;
import main.java.de.baltic_online.mediknight.domain.KnightObject;


public class UserAdministrationPresenter implements Presenter, Commitable {

    public static void main( final String[] args ) {
	try {
	    MainFrame.initProperties();
	    MainFrame.initTracer();
	    MainFrame.initDB();
	    // MediknightTheme.install();

	} catch( final Exception e ) {
	    e.printStackTrace();
	}
	final UserAdministrationModel model = new UserAdministrationModel();

	final JFrame frame = new JFrame();
	frame.getContentPane().setLayout( new BorderLayout() );
	final UserAdministrationPresenter presenter = new UserAdministrationPresenter( model );
	frame.getContentPane().add( presenter.createView(), BorderLayout.CENTER );
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	frame.setVisible( true );
	frame.pack();
    }

    UserAdministrationModel model;

    UserAdministrationPanel view;


    public UserAdministrationPresenter() {

    }


    public UserAdministrationPresenter( final UserAdministrationModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
    }


    @Override
    public void commit() {
	view.save();
    }


    @Override
    public Component createView() {
	view = new UserAdministrationPanel();
	view.setPresenter( this );

	return view;
    }


    public UserAdministrationModel getModel() {
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