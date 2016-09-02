package de.baltic_online.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.JFrame;

import de.baltic_online.mediknight.Commitable;
import de.baltic_online.mediknight.MainFrame;
import de.baltic_online.mediknight.Presenter;
import de.baltic_online.mediknight.domain.KnightObject;


public class ColorPresenter implements Presenter, Commitable {

    public static void main( final String[] args ) {
	try {
	    MainFrame.initProperties();
	    MainFrame.initTracer();
	    MainFrame.initDB();
	    // MediknightTheme.install();

	} catch( final Exception e ) {
	    e.printStackTrace();
	}

	final JFrame frame = new JFrame();
	frame.getContentPane().setLayout( new BorderLayout() );
	final ColorPresenter presenter = new ColorPresenter();
	frame.getContentPane().add( presenter.createView(), BorderLayout.CENTER );
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	frame.setVisible( true );
	frame.pack();
    }

    ColorModel model;

    ColorPanel view;


    public ColorPresenter() {
	model = new ColorModel();
    }


    @Override
    public void activate() {
    }


    @Override
    public void commit() {
	try {
	    model.saveProperties( view.getProperties() );
	} catch( final Exception e ) {
	    e.printStackTrace();
	}
    }


    @Override
    public Component createView() {
	view = new ColorPanel( model.getProperties() );
	return view;
    }


    @Override
    public Component getResponsibleComponent() {
	return null;
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
    }


    public void saveProperties( final HashMap< String, Object > map ) {
    }
}