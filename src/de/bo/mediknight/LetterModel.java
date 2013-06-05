package de.bo.mediknight;

import java.util.*;
import java.sql.*;
import de.bo.mediknight.domain.*;
import javax.swing.event.*;

public class LetterModel {

    Rechnung rechnung;
    RechnungsPosten[] rechnungsPosten = null;
    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();


    public LetterModel( Rechnung rechnung ) {
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


    public List<TagesDiagnose> getTagesDiagnosen() {
        try {
            return rechnung.getPatient().getTagesDiagnosen();
        } catch( SQLException e ) {
            e.printStackTrace(); /** @todo Exception reporting. */
        }

        return null;
    }
}