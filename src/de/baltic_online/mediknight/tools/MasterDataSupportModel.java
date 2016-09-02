package de.baltic_online.mediknight.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.baltic_online.mediknight.domain.RechnungsPosten;


public class MasterDataSupportModel {

    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();
    RechnungsPosten[]     posten;


    public MasterDataSupportModel() {
	try {
	    if( posten == null ) {
		posten = RechnungsPosten.retrieve().toArray( new RechnungsPosten[0] );
		Arrays.sort( posten );
	    }
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
    }


    public MasterDataSupportModel( final RechnungsPosten[] posten ) {
	this.posten = posten;
    }


    public void addChangeListener( final ChangeListener l ) {
	changeListeners.add( l );
    }


    public void addItem( final RechnungsPosten item ) {
	try {
	    item.save();

	    posten = RechnungsPosten.retrieve().toArray( new RechnungsPosten[0] );
	    Arrays.sort( posten );

	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
	fireChangeEvent();
    }


    public void deleteEntries( final int[] entries ) {

	try {
	    for( final int entrie : entries ) {
		posten[entrie].delete();
	    }

	    posten = RechnungsPosten.retrieve().toArray( new RechnungsPosten[0] );
	    Arrays.sort( posten );

	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
	fireChangeEvent();
    }


    void fireChangeEvent() {
	final Iterator< ChangeListener > it = changeListeners.iterator();
	final ChangeEvent e = new ChangeEvent( this );

	while( it.hasNext() ) {
	    it.next().stateChanged( e );
	}
    }


    public RechnungsPosten[] getRechnungsPosten() {
	return posten;
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }


    public void setRechnungsPosten( final RechnungsPosten[] posten ) {
	this.posten = posten;
	Arrays.sort( posten );
	fireChangeEvent();
    }

}