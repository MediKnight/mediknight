package de.bo.mediknight;

import java.awt.Component;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.JOptionPane;

import de.bo.mediknight.domain.Rechnung;
import de.bo.mediknight.domain.RechnungsGruppe;


public class CreateMacroPresenter extends AbstractPresenter {

    CreateMacroPanel view;
    CreateMacroModel model;


    public CreateMacroPresenter( final CreateMacroModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
	view.activate();
    }


    public void createMacro() {
	final String name = view.getMacroName();
	RechnungsGruppe macro = findMacroByName( name );

	if( macro != null ) {
	    final int result = JOptionPane.showConfirmDialog( null,
		    "Es existiert bereits ein Baustein mit diesem Namen.\nSoll dieser Baustein überschrieben werden?", "Baustein vorhanden",
		    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );

	    if( result == JOptionPane.YES_OPTION ) {
		try {
		    macro.delete();
		} catch( final SQLException e ) {
		    e.printStackTrace();
		    /** TODO Error reporting. */
		}
	    } else {
		return;
	    }
	}

	try {
	    macro = new RechnungsGruppe();
	    final Rechnung rechnung = new Rechnung();
	    BillEntry.saveEntries( rechnung, model.getEntries() );
	    macro.setAbk( name );
	    macro.setObject( rechnung.getObject() );
	    macro.setText( "" );
	    macro.save();
	} catch( final SQLException e ) {
	    e.printStackTrace();
	    /** TODO Error reporting! */
	}

	MainFrame.getApplication().bill();
    }


    @Override
    public Component createView() {
	view = new CreateMacroPanel();
	view.setPresenter( this );

	return view;
    }


    public RechnungsGruppe findMacroByName( final String name ) {
	final Iterator< RechnungsGruppe > it = model.getComponentList().iterator();

	while( it.hasNext() ) {
	    final RechnungsGruppe macro = it.next();
	    if( macro.getAbk().equals( name ) ) {
		return macro;
	    }
	}

	return null;
    }


    public CreateMacroModel getModel() {
	return model;
    }


    public void macroSelected() {
	view.setMacroName( view.getSelectedMacro().toString() );
    }
}