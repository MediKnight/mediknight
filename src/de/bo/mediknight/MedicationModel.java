package de.bo.mediknight;

import java.util.*;
import java.sql.*;
import javax.swing.event.*;

import de.bo.mediknight.domain.*;

public class MedicationModel {
    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
    TagesDiagnose diagnose;
    VerordnungsPosten[] posten;

    public MedicationModel() {
    }

    public MedicationModel( TagesDiagnose diagnose ) {
        this.diagnose = diagnose;
        try {
            diagnose.recall();
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

    public void setDiagnose(TagesDiagnose diagnose) {
        this.diagnose = diagnose;
        fireChangeEvent();
    }

    public TagesDiagnose getDiagnose() {
        return diagnose;
    }


    public Verordnung getVerordnung() {
        try {
            return diagnose.getVerordnung();
        } catch( SQLException e ) {
            /** @todo Exception reporting. */
        }

        return null;
    }

    public VerordnungsPosten[] getVerordnungsposten() throws SQLException {
	if (posten != null)
	    return posten;
	try {
	    posten = (VerordnungsPosten[])
		VerordnungsPosten.retrieve().toArray(new VerordnungsPosten[0]);
	    Arrays.sort(posten);
            return posten;
	} catch (NullPointerException e) {
	    return new VerordnungsPosten[0];
	}
    }

}