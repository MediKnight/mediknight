/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

import de.baltic_online.borm.AttributeAccess;
import de.baltic_online.borm.AttributeMapper;
import de.baltic_online.borm.AttributeType;
import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.ObjectMapper;


/**
 * This class implements locks for a given patient.
 * <p>
 * Acquired locks would be cached by a hashtable. Locks must be released external
 *
 * @author sml@baltic-online.de
 *
 * @version 1.2
 */

public class Lock extends KnightObject {

    public static class Aspect {

	private final String string;


	public Aspect() {
	    this( "" );
	}


	public Aspect( final String string ) {
	    this.string = string;
	}


	@Override
	public boolean equals( final Object o ) {
	    final Aspect a = (Aspect) o;
	    return string.equals( a.string );
	}


	@Override
	public String toString() {
	    return string;
	}
    }

    /**
     * Container for all locks.
     */
    private static Hashtable< String, Lock > locks;

    /**
     * Initialize the Datastore and the ObjectMapper and creates a new hashtable for cached locks.
     */
    static {
	final ObjectMapper om = new ObjectMapper( Lock.class, "patientlock" );
	om.add( new AttributeMapper( "patientId", "patient_id", true, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "aspect", "aspekt", true, AttributeAccess.METHOD, AttributeType.STRING ) );
	Datastore.current.register( om );

	locks = new Hashtable< String, Lock >();
    }


    /**
     * Acquire a lock.
     *
     * @param timeout
     *            a timeout value. If <tt>timeout</tt> is greater than <tt>0</tt> the lock will be removed within <tt>timeout</tt> seconds.
     * @return the lock if the lock could be acquired, otherwise <tt>null</tt>
     */
    public synchronized static Lock acquireLock( final int patientId, final Aspect aspect, final int timeout ) throws SQLException {

	final String key = patientId + "," + aspect;
	Lock lock = locks.get( key );
	if( lock != null ) {
	    if( lock.isValid() ) {
		tracer.trace( DATA, "Acquire lock " + lock + " from cache" );
		// lock allready acquired and not released
		// refresh timeout and return this lock
		lock.refresh();
		return lock;
	    }
	    // lock no longer valid,
	    // continue acquiring
	}

	// lock now allways null
	try {
	    lock = new Lock( patientId, aspect.toString(), timeout );
	    lock.save();
	    tracer.trace( DATA, "Acquire lock " + lock + " from table" );
	    locks.put( key, lock );
	    return lock;
	} catch( final SQLException sqlx ) {
	    return null;
	}
    }


    public synchronized static boolean release( final int patientId, final Aspect aspect ) throws SQLException {

	final String key = patientId + "," + aspect;
	final Lock lock = locks.get( key );
	if( lock != null ) {
	    lock.release();
	    return true;
	}

	return false;
    }


    /**
     * This method tries to release all known locks (e.g. the locks stored in the hashtable).
     * <p>
     * If an exception occures the method continues releasing locks, marks this exception and throws it later.
     */
    public synchronized static void releaseAll() throws SQLException {

	SQLException x = null;

	for( final Enumeration< String > e = locks.keys(); e.hasMoreElements(); ) {
	    final Lock lock = locks.get( e.nextElement() );
	    try {
		lock.release();
	    } catch( final SQLException sqlx ) {
		x = sqlx;
	    }
	}

	if( x != null ) {
	    throw x;
	}
    }

    /**
     * The patient which the lock is for.
     */
    private int     patientId;

    /**
     * The corresponding aspect value.
     */
    private String  aspect;    ;

    /**
     * Indicates the released state of this lock.
     */
    private boolean released;

    /**
     * The creation time of the lock.
     */
    private long    timestamp;

    /**
     * Timeout value for this lock in seconds.
     */
    private int     timeout;

    /**
     * The "has Id" value.
     */
    private boolean hid;


    public Lock() {
    }


    /**
     * Locks could only be created from the method acquireLock.
     * 
     * @see #acquireLock
     */
    private Lock( final int patientId, final String aspect, final int timeout ) {
	this.patientId = patientId;
	this.aspect = aspect;

	released = false;
	timestamp = System.currentTimeMillis();
	this.timeout = timeout;

	hid = false;
    }


    // Framework ------------------------------------------------------------

    @Override
    public boolean equals( final Object o ) {
	final Lock l = (Lock) o;
	return patientId == l.patientId && aspect.equals( l.aspect );
    }


    /**
     * finalize() is called by the garbage collector. make sure the lock is released when the object is garbage collected.
     *
     * @since 1.0
     */
    @Override
    protected void finalize() throws Throwable {
	release();
    }


    public String getAspect() {
	return aspect;
    }


    public int getPatientId() {
	return patientId;
    }


    @Override
    protected boolean hasIdentity() {
	return hid;
    }


    // Management ------------------------------------------------------

    private boolean isValid() throws SQLException {
	if( timeout > 0 && (System.currentTimeMillis() - timestamp) / 1000 > timeout ) {

	    release();
	}

	return !released;
    }


    private void refresh() {
	timestamp = System.currentTimeMillis();
    }


    public synchronized void release() throws SQLException {
	if( !released ) {
	    delete();
	    released = true;
	    locks.remove( patientId + "," + aspect );
	    tracer.trace( DATA, "Release lock " + this );
	}
    }


    public void setAspect( final String aspect ) {
	this.aspect = aspect;
    }


    @Override
    public void setIdentity() throws SQLException {
	hid = true;
    }


    public void setPatientId( final int id ) {
	patientId = id;
    }


    @Override
    public String toString() {
	return patientId + ", " + aspect;
    }
}
