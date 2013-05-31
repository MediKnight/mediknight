package de.bo.mediknight.tools;

import java.util.*;
import javax.swing.event.*;

import de.bo.mediknight.domain.*;


public class PrintSettingsModel {

    Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
    private Map<String, String> content;


    public PrintSettingsModel() {

	try {
	    content = UserProperty.retrieveUserInformation(UserProperty.ALL_USERS);

	    String[][] defaults = {
		{"print.logo", "Praxis\n\n<b>Heilpraktiker"},
		{"print.sender", "Baltic-Online Computer GmbH, Alter Markt 1-2, 24103 Kiel"},
		{"print.font", "serif"},
		{"print.bill.final", "Wir danken für Ihren Besuch!"},
		{"print.medication.final", "Wir danken für Ihren Besuch!"}
	    };
	    for ( int i=0; i<defaults.length; i++ ) {
		String[] kv = defaults[i];
		if ( !content.containsKey(kv[0]) )
		    content.put(kv[0],kv[1]);
	    }
	} catch (java.sql.SQLException e) {
	    e.printStackTrace();
	    content = null;
	}
    }

    public void saveProperties() {
        try {
            UserProperty.saveUserInformation(UserProperty.ALL_USERS,content);
        }
        catch (java.sql.SQLException sqlx) {
            sqlx.printStackTrace();
        }
    }

    public void alterMap(String key, String value) {
	if (key != null && key.length() > 0)
	    content.put(key, value);
	saveProperties();
	fireChangeEvent();
    }

    public Map<String, String> getMap() {
	return content;
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
}