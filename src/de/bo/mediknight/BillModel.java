package de.bo.mediknight;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.bo.mediknight.domain.Rechnung;
import de.bo.mediknight.domain.RechnungsPosten;

public class BillModel {

    Rechnung rechnung;
    RechnungsPosten[] rechnungsPosten = null;
    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();


    public BillModel( Rechnung rechnung ) {
        this.rechnung = rechnung;
        try {
            rechnung.recall();
        } catch (SQLException ex) {
        }
    }


    public void addChangeListener( ChangeListener l ) {
        changeListeners.add( l );
    }

    public void removeChangeListener( ChangeListener l ) {
        changeListeners.remove( l );
    }

    void fireChangeEvent() {
        Iterator<ChangeListener> it = changeListeners.iterator();
        ChangeEvent e = new ChangeEvent( this );

        while( it.hasNext() ) {
            it.next().stateChanged(e);
        }
    }

    public void setRechnung( Rechnung rechnung ) {
        this.rechnung = rechnung;
        fireChangeEvent();
    }

    public Rechnung getRechnung() {
        return rechnung;
    }


    public RechnungsPosten[] getRechnungsPosten() throws SQLException {
        rechnungsPosten = MainFrame.getApplication().getRechnungsPosten();
        return rechnungsPosten;
    }


}