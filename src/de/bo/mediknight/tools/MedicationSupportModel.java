package de.bo.mediknight.tools;

import java.util.*;
import javax.swing.event.*;
import de.bo.mediknight.domain.*;
import java.sql.SQLException;

public class MedicationSupportModel {

    VerordnungsPosten[] posten;

    Set changeListeners = new HashSet();

    public MedicationSupportModel() {
    }

    public VerordnungsPosten[] getVerordnungsposten() throws SQLException {
	try {
	    posten = (VerordnungsPosten[])
		VerordnungsPosten.retrieve().toArray(new VerordnungsPosten[0]);
	    Arrays.sort(posten);
            return posten;
	} catch (NullPointerException e) {
	    return new VerordnungsPosten[0];
	}
    }

    public void delete(int i) throws SQLException {
	posten[i].delete();
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
}