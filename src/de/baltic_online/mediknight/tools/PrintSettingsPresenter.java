package de.baltic_online.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JFrame;

import de.baltic_online.mediknight.Commitable;
import de.baltic_online.mediknight.MainFrame;
import de.baltic_online.mediknight.Presenter;
import de.baltic_online.mediknight.domain.KnightObject;


/**
 * Hier sind alle Eigenschaften des Programms gespeichert. Zum Beispiel welche Daten das Logo enthält.
 *
 */
public class PrintSettingsPresenter implements Presenter, Commitable {

    public static Map< String, String > getSettings() {
	final PrintSettingsModel model = new PrintSettingsModel();
	return model.getMap();
    }


    public static void main( final String[] args ) throws Exception {

	try {
	    MainFrame.initProperties();
	    MainFrame.initTracer();
	    MainFrame.initDB();
	} catch( final Exception e ) {
	    e.printStackTrace();
	}

	final PrintSettingsModel model = new PrintSettingsModel();
	final PrintSettingsPresenter presenter = new PrintSettingsPresenter( model );

	final JFrame f = new JFrame( "PrintSettings Test" );
	f.addWindowListener( new WindowAdapter() {

	    @Override
	    public void windowClosing( final WindowEvent e ) {
		System.exit( 0 );
	    }
	} );
	f.getContentPane().setLayout( new BorderLayout() );
	f.getContentPane().add( presenter.createView(), BorderLayout.CENTER );
	f.pack();
	f.setVisible( true );
    }

    PrintSettingsModel model;

    PrintSettingsPanel view;


    public PrintSettingsPresenter( final PrintSettingsModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
    }


    @Override
    public void commit() {
	view.saveEntries();
    }


    @Override
    public Component createView() {
	view = new PrintSettingsPanel( this );

	return view;
    }


    public PrintSettingsModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	/** @todo: implement this! */
	return null;
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
    }
}