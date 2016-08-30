/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.Storable;
import de.baltic_online.borm.Tracer;


/**
 * Abstract superclass of all database-storable mediknight objects.
 *
 * @author sma@baltic-online.de
 */
public abstract class KnightObject implements Storable {

    public final static String DATA = "data";

    protected static Tracer    tracer;


    public static long getSequenceId() {
	// boolean autoCommit = false;
	final Datastore ds = Datastore.current;
	long id = 0;
	try {
	    // autoCommit = ds.setAutoCommit(false);
	    final ResultSet rs = ds.execute( "select id from sequenceid" );
	    if( rs.next() ) {
		id = rs.getLong( 1 );
		ds.execute( "update sequenceid set id=" + (id + 1L) );
	    } else {
		id = 1L;
		ds.execute( "insert into sequenceid values(" + (id + 1L) + ")" );
	    }
	    // ds.commit();

	    return id;
	} catch( final SQLException x ) {
	    throw new RuntimeException( x.getMessage() );
	} finally {
	    /*
	     * try { ds.setAutoCommit(autoCommit); } catch ( SQLException x ) { throw new RuntimeException(x.getMessage()); }
	     */
	}
    }


    public static Tracer getTracer() {
	return tracer;
    }


    public static String notNull( final String s ) {
	return s != null ? s : "";
    }


    public static void setTracer( final Tracer t ) {
	tracer = t;
    }


    /**
     * Converts an <code>Iterator</code> into a <code>List</code>.
     */
    @SuppressWarnings( "unchecked" )
    public static < T > List< T > toList( final Iterator< Storable > i ) {
	final List< T > list = new ArrayList< T >();
	while( i.hasNext() ) {
	    list.add( (T) i.next() );
	}
	return list;
    }


    /**
     * Remove a persistent object from the database.
     */
    public void delete() throws SQLException {
	if( hasIdentity() ) {
	    // tracer.trace(DATA,"Delete "+toLongString());
	    Datastore.current.delete( this );
	} else {
	    throw new SQLException( "[delete] object has no key" );
	}
    }


    /**
     * Retrieve an auto-increment value for an inserted object.
     * <p>
     * <i>Note: This method is mySQL specific.</i>
     */
    protected int getLastId() throws SQLException {

	int id = 0;

	final ResultSet rs = Datastore.current.execute( "select last_insert_id()" );
	if( !rs.next() ) {
	    throw new SQLException( "[insert] can't read insert id" );
	}
	id = rs.getInt( 1 );
	rs.close();

	return id;
    }


    /**
     * Returns true if the receiver has a valid key.
     */
    protected abstract boolean hasIdentity() throws SQLException;


    /**
     * Recalls the persistant receiver's latest values from the database.
     */
    public void recall() throws SQLException {
	if( hasIdentity() ) {
	    // tracer.trace(DATA,"Reload "+toLongString());
	    Datastore.current.reload( this );
	} else {
	    throw new SQLException( "[recall] object has no key" );
	}
    }


    /**
     * Makes the receiver persistent in the database.
     */
    public void save() throws SQLException {
	if( hasIdentity() ) {
	    // tracer.trace(DATA,"Update "+toLongString());
	    Datastore.current.update( this );
	} else {
	    // tracer.trace(DATA,"Insert "+toLongString());
	    Datastore.current.insert( this );
	    setIdentity();
	}
    }


    /**
     * Sets the receiver's unique key. Called after <code>insert()</code>.
     */
    protected abstract void setIdentity() throws SQLException;


    public String toLongString() {
	return toString();
    }
}
