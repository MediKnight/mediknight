package main.java.de.baltic_online.mediknight;

import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import main.java.de.baltic_online.mediknight.domain.KnightObject;
import main.java.de.baltic_online.mediknight.domain.Lock;
import main.java.de.baltic_online.mediknight.domain.Patient;
import main.java.de.baltic_online.mediknight.domain.RechnungsPosten;
import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;
import main.java.de.baltic_online.mediknight.util.ErrorDisplay;
import main.java.de.baltic_online.mediknight.widgets.UndoUtilities;


public class BillPresenter implements Presenter, Commitable, Observer {

    public static void main( final String[] args ) {
	final JFrame f = new JFrame( "Bill Presenter Example" );

	// f.getContentPane().add( example().createView() );

	f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	f.pack();
	f.setVisible( true );
    }

    Bill1Panel viewPage1;
    Bill1Panel view;

    BillModel  model;


    public BillPresenter() {
    }


    public BillPresenter( final BillModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
	view.activate();
    }


    public void addItem() {
	// TODO Review the following code and decide if this is still
	// necessary...
	// The following code is copied from BillPane:
	//
	// --- "Notloesung": nicht schoen, aber notwendig, um voruebergehend
	// Funktionalitaet zu sichern (-> JTable-Problem beim Entfernen
	// von Zeilen mit Eingabe-Fokus).
	// Eigentlich muss nur in der jeweils editierbaren Spalte der
	// entsprechende Editor modifiziert werden, aber die "get"-
	// Methode des Spalteneditors liefert stets einen null-Wert.
	// (Synchronisation?)).
	/*
	 * if(view.getSelectedBillItem() != null) { TableCellEditor editor = billTable.getDefaultEditor(billTable.getClass()); editor.cancelCellEditing(); }
	 */

	// view.billModel.fireTableDataChanged();

	final MediKnight app = MediKnight.getApplication();
	final LockingInfo li = app.getLockingInfo();
	final Patient patient = li.getPatient();
	Lock lock = null;
	try {
	    if( patient != null ) {
		lock = patient.acquireLock( li.getAspect(), LockingListener.LOCK_TIMEOUT );
		if( lock != null ) {
		    final RechnungsPosten[] posten = view.getSelectedRechnungsPostenItems();
		    if( posten.length == 0 ) {
			return;
		    }

		    final BillEntry entriesToBeAdded[] = new BillEntry[posten.length];
		    for( int i = 0; i < posten.length; ++i ) {
			entriesToBeAdded[i] = new BillEntry();
			entriesToBeAdded[i].setItem( posten[i] );
			entriesToBeAdded[i].setCount( 1.0 );
		    }

		    final int numberOfNewEntries = entriesToBeAdded.length;

		    final BillEntry[] entries = BillEntry.loadEntries( model.getRechnung() );
		    final BillEntry[] newEntries = new BillEntry[entries.length + numberOfNewEntries];

		    int referencePosition = view.getSelectedBillRow();
		    if( referencePosition != -1 ) {
			System.arraycopy( entries, 0, newEntries, 0, referencePosition + 1 );
		    }

		    referencePosition += 1;

		    System.arraycopy( entriesToBeAdded, 0, newEntries, referencePosition, numberOfNewEntries );
		    System.arraycopy( entries, referencePosition, newEntries, referencePosition + numberOfNewEntries, entries.length - referencePosition );

		    BillEntry.saveEntries( getModel().getRechnung(), newEntries );
		    // Now inform the model that it has actually changed.
		    model.getRechnung().save();
		} else {
		    return;
		}
	    }
	} catch( final SQLException sqle ) {
	} finally {
	    try {
		lock.release();
	    } catch( final Exception ex ) {
	    }
	    getModel().fireChangeEvent();
	}
    }


    @Override
    public void commit() {
	model.getRechnung().setGoae( view.isGOAE() );
	try {
	    model.getRechnung().save();
	} catch( final SQLException e ) {
	    new ErrorDisplay( e, "Fehler beim Abspeichern der Rechnung!", "Speichern...", view );
	}
    }


    public void createMacro() {
	if( view.getSelectedItems().length == 1 ) {
	    final int action = JOptionPane.showConfirmDialog( MediKnight.getApplication().getRootPane(),
		    "Der Baustein, den Sie erzeugen, wird nur einen Eintrag enthalten\n" + "Ist dieses erwünscht?", MediKnight.NAME, JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE );

	    if( action == JOptionPane.NO_OPTION ) {
		return;
	    }
	}
	MediKnight.getApplication().createMacro( view.getSelectedItems() );
    }


    @Override
    public Component createView() {
	view = new Bill1Panel( model.getRechnung() );
	view.setPresenter( this );

	new LockingListener( this ).applyTo( view.getBillTable() );
	new LockingListener( this ).applyTo( UndoUtilities.getMutables( view ) );

	return view;
    }


    public void deleteEntry() {
	final int[] row = view.getSelectedBillRows();

	BillEntry[] entries = BillEntry.loadEntries( model.getRechnung() );

	final ArrayList< BillEntry > list = new ArrayList< BillEntry >();

	for( int i = 0; i < entries.length; i++ ) {
	    boolean check = true;
	    for( final int element : row ) {
		if( i == element ) {
		    check = false;
		}
	    }
	    if( check ) {
		list.add( entries[i] );
	    }
	}

	entries = new BillEntry[entries.length - row.length];

	entries = list.toArray( entries );

	BillEntry.saveEntries( getModel().getRechnung(), entries );

	// Now inform the model that it has actually changed.
	getModel().fireChangeEvent();
    }


    public BillModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	return null;
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
	try {
	    ((TagesDiagnose) knightObject).getRechnung().recall();
	    view.update();

	} catch( final SQLException ex ) {
	}
    }


    public void showLetter() {
	commit();
	MediKnight.getApplication().letter();
    }


    public void showMacro() {
	MediKnight.getApplication().macro( view.getSelectedItems() );
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
