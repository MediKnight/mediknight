package main.java.de.baltic_online.mediknight;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.java.de.baltic_online.mediknight.domain.Rechnung;
import main.java.de.baltic_online.mediknight.domain.RechnungsPosten;


public class BillModel {

    Rechnung	      rechnung;
    RechnungsPosten[]     rechnungsPosten = null;
    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();


    public BillModel( final Rechnung rechnung ) {
	this.rechnung = rechnung;
	try {
	    rechnung.recall();
	} catch( final SQLException ex ) {
	}
    }


    public void addChangeListener( final ChangeListener l ) {
	changeListeners.add( l );
    }


    void fireChangeEvent() {
	final Iterator< ChangeListener > it = changeListeners.iterator();
	final ChangeEvent e = new ChangeEvent( this );

	while( it.hasNext() ) {
	    it.next().stateChanged( e );
	}
    }


    public Rechnung getRechnung() {
	return rechnung;
    }


    public RechnungsPosten[] getRechnungsPosten() throws SQLException {
	rechnungsPosten = MainFrame.getApplication().getRechnungsPosten();
	return rechnungsPosten;
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }


    public void setRechnung( final Rechnung rechnung ) {
	this.rechnung = rechnung;
	fireChangeEvent();
    }

}