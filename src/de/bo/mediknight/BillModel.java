package de.bo.mediknight;

import java.util.*;
import java.sql.*;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.ErrorDisplay;
import javax.swing.event.*;

public class BillModel {

    Rechnung rechnung;
    RechnungsPosten[] rechnungsPosten = null;
    Set changeListeners = new HashSet();


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
        Iterator it = changeListeners.iterator();
        ChangeEvent e = new ChangeEvent( this );

        while( it.hasNext() ) {
            ((ChangeListener) it.next()).stateChanged(e);
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