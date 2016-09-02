package de.baltic_online.mediknight.tools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.baltic_online.mediknight.domain.UserProperty;


public class PrintSettingsModel {

    Set< ChangeListener >	 changeListeners = new HashSet< ChangeListener >();
    private Map< String, String > content;


    public PrintSettingsModel() {

	try {
	    content = UserProperty.retrieveUserInformation( UserProperty.ALL_USERS );

	    final String[][] defaults = { { "print.logo", "Praxis\n\n<b>Heilpraktiker" },
		    { "print.sender", "Baltic-Online Computer GmbH, Alter Markt 1-2, 24103 Kiel" }, { "print.font", "serif" },
		    { "print.bill.final", "Wir danken für Ihren Besuch!" }, { "print.medication.final", "Wir danken für Ihren Besuch!" } };
	    for( final String[] default1 : defaults ) {
		final String[] kv = default1;
		if( !content.containsKey( kv[0] ) ) {
		    content.put( kv[0], kv[1] );
		}
	    }
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	    content = null;
	}
    }


    public void addChangeListener( final ChangeListener l ) {
	changeListeners.add( l );
    }


    public void alterMap( final String key, final String value ) {
	if( key != null && key.length() > 0 ) {
	    content.put( key, value );
	}
	saveProperties();
	fireChangeEvent();
    }


    void fireChangeEvent() {
	final Iterator< ChangeListener > it = changeListeners.iterator();
	final ChangeEvent e = new ChangeEvent( this );

	while( it.hasNext() ) {
	    it.next().stateChanged( e );
	}
    }


    public Map< String, String > getMap() {
	return content;
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }


    public void saveProperties() {
	try {
	    UserProperty.saveUserInformation( UserProperty.ALL_USERS, content );
	} catch( final java.sql.SQLException sqlx ) {
	    sqlx.printStackTrace();
	}
    }
}