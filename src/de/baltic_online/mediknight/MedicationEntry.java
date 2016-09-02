/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

import de.baltic_online.mediknight.domain.ObjectOwner;


/**
 * This class is similar to BillEntry.
 *
 * @see BillEntry
 */
public class MedicationEntry {

    private static String getKeyPrefix( final int n ) {
	return "me." + n + ".";
    }


    public static MedicationEntry[] loadEntries( final ObjectOwner owner ) {
	final String o = owner.getObject();
	if( o == null ) {
	    return new MedicationEntry[0];
	}

	final Properties props = new Properties();
	try {
	    final ByteArrayInputStream bais = new ByteArrayInputStream( o.getBytes() );
	    props.load( bais );
	} catch( final IOException x ) { // should not be thrown on
					 // ByteArrayInputStream
	    x.printStackTrace();
	}

	final LinkedList< MedicationEntry > list = new LinkedList< MedicationEntry >();
	for( int n = 0;; n++ ) {
	    final String kp = getKeyPrefix( n );
	    final String item = props.getProperty( kp + "posten" );
	    if( item == null ) {
		break;
	    }

	    final MedicationEntry me = new MedicationEntry( item );
	    list.add( me );
	}

	final MedicationEntry[] entries = new MedicationEntry[list.size()];
	int i = 0;
	for( final Iterator< MedicationEntry > it = list.iterator(); it.hasNext(); i++ ) {
	    entries[i] = it.next();
	}

	return entries;
    }


    public static void saveEntries( final ObjectOwner owner, final MedicationEntry[] entries ) {
	final int n = entries.length;
	final Properties props = new Properties();
	for( int i = 0; i < n; i++ ) {
	    final String kp = getKeyPrefix( i );
	    final MedicationEntry me = entries[i];
	    final String item = me.getItem();
	    props.setProperty( kp + "posten", item );
	}
	try {
	    final ByteArrayOutputStream bos = new ByteArrayOutputStream( n * 200 );
	    props.store( bos, "" );
	    owner.setObject( bos.toString() );
	} catch( final IOException x ) { // should not be thrown on
					 // ByteArrayOutputStream
	    x.printStackTrace();
	}
    }

    private String item;


    public MedicationEntry() {
	this( "" );
    }


    public MedicationEntry( final String item ) {
	this.item = item;
    }


    public String getItem() {
	return item;
    }


    public void setItem( final String item ) {
	this.item = item;
    }


    public Vector< String > toVector() {
	final Vector< String > v = new Vector< String >( 1 );
	v.add( item );
	return v;
    }
}