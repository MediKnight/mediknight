package main.java.de.baltic_online.mediknight;

import java.awt.Component;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import main.java.de.baltic_online.mediknight.domain.KnightObject;
import main.java.de.baltic_online.mediknight.domain.Rechnung;
import main.java.de.baltic_online.mediknight.domain.RechnungsGruppe;
import main.java.de.baltic_online.mediknight.util.ErrorDisplay;


public class MacroPresenter implements Presenter, Commitable {

    MacroPanel view;

    MacroModel model;


    public MacroPresenter() {
	this( new MacroModel() );
    }


    public MacroPresenter( final MacroModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
    }


    public void addMacro() {
	final RechnungsGruppe macro = view.getSelectedRechnungsGruppe();
	if( macro != null ) {
	    final BillEntry[] entries = BillEntry.loadEntries( model.getRechnung() );
	    final BillEntry[] macroEntries = BillEntry.loadEntries( macro );
	    final BillEntry[] newEntries = new BillEntry[entries.length + macroEntries.length];

	    System.arraycopy( entries, 0, newEntries, 0, entries.length );
	    System.arraycopy( macroEntries, 0, newEntries, entries.length, macroEntries.length );

	    BillEntry.saveEntries( getModel().getRechnung(), newEntries );

	    /**
	     * @todo The following code actually belongs in the commit() method, but that doesn't work. Fix it.
	     */
	    try {
		getModel().getRechnung().save();
	    } catch( final SQLException e ) {
		new ErrorDisplay( e, "Fehler beim Abspeichern der Rechnung!", "Speichern...", view );
	    }

	    showBill();
	}
    }


    @Override
    public void commit() {
	try {
	    getModel().getRechnung().save();
	} catch( final SQLException e ) {
	    new ErrorDisplay( e, "Fehler beim Abspeichern der Rechnung!", "Speichern...", view );
	}
    }


    public void createMacro() {
	String input = JOptionPane.showInputDialog( view, "Bausteinkennung eingeben", "Baustein erzeugen", JOptionPane.PLAIN_MESSAGE );
	if( input != null ) {
	    try {
		input = input.trim();
		if( input.length() > 0 ) {
		    final RechnungsGruppe macro = new RechnungsGruppe();
		    final Rechnung rechnung = new Rechnung();
		    BillEntry.saveEntries( rechnung, model.getEntries() );
		    model.setRechnung( rechnung );
		    macro.setAbk( input );
		    macro.setObject( model.getRechnung().getObject() );
		    macro.setText( "" );
		    macro.save();

		    showBill();
		}
	    } catch( final SQLException e ) {
		e.printStackTrace();
		/** @todo Exception reporting. */
	    }
	}
    }


    @Override
    public Component createView() {
	final MacroPanel panel = new MacroPanel();
	panel.setPresenter( this );
	view = panel;
	view.setFocusOnList();

	return panel;
    }


    void deleteMacro() {
	final RechnungsGruppe macro = view.getSelectedRechnungsGruppe();
	if( macro != null ) {
	    final int r = JOptionPane.showConfirmDialog( view, "Baustein " + macro.getAbk() + " wirklich löschen?", "Baustein löschen",
		    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );

	    if( r == JOptionPane.YES_OPTION ) {
		try {
		    macro.delete();
		    model.fireChangeEvent();
		} catch( final SQLException e ) {
		    e.printStackTrace();
		    /** @todo Exception reporting. */
		}
	    }
	}
    }


    public MacroModel getModel() {
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


    void showBill() {
	MainFrame.getApplication().bill();
	// ((NewAppWindow) AppWindow.getApplication()).bill();
    }
}