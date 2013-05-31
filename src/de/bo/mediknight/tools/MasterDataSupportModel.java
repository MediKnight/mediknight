package de.bo.mediknight.tools;

import de.bo.mediknight.domain.RechnungsPosten;
import javax.swing.event.*;

import java.util.*;

public class MasterDataSupportModel {

    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
    RechnungsPosten[] posten;

    public MasterDataSupportModel() {
	try {
	    if ( posten == null ) {
		posten = (RechnungsPosten[])
		    RechnungsPosten.retrieve().toArray(new RechnungsPosten[0]);
		Arrays.sort(posten);
	    }
	} catch (java.sql.SQLException e) {
	    e.printStackTrace();
	}
    }

    public MasterDataSupportModel(RechnungsPosten[] posten) {
	this.posten = posten;
    }

    public RechnungsPosten[] getRechnungsPosten() {
	return posten;
    }

    public void setRechnungsPosten( RechnungsPosten[] posten ) {
	this.posten = posten;
	Arrays.sort( posten );
	fireChangeEvent();
    }

    public void addChangeListener( ChangeListener l ) {
        changeListeners.add( l );
    }

    public void addItem( RechnungsPosten item ) {
	try {
	    item.save();

	    posten = (RechnungsPosten[])RechnungsPosten.retrieve().toArray(new RechnungsPosten[0]);
	    Arrays.sort(posten);

	} catch (java.sql.SQLException e) {
	    e.printStackTrace();
	}
    	fireChangeEvent();
    }

    public void deleteEntries(int[] entries) {

	try {
	    for (int i = 0; i < entries.length; i++)
		posten[entries[i]].delete();

	    posten = (RechnungsPosten[])RechnungsPosten.retrieve().toArray(new RechnungsPosten[0]);
	    Arrays.sort(posten);

	} catch (java.sql.SQLException e) {
	    e.printStackTrace();
	}
    	fireChangeEvent();
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

}