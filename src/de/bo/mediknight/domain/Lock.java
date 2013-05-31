/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import de.bo.borm.*;
import java.util.*;
import java.sql.SQLException;

/**
 * This class implements locks for a given patient.
 * <p>
 * Acquired locks would be cached by a hashtable.
 * Locks must be released external
 *
 * @author sml@baltic-online.de
 *
 * @version 1.2
 */

public class Lock extends KnightObject {

    /**
     * Container for all locks.
     */
    private static Hashtable<String, Lock> locks;

    /**
     * Initialize the Datastore and the ObjectMapper and
     * creates a new hashtable for cached locks.
     */
    static {
        ObjectMapper om = new ObjectMapper(Lock.class,"patientlock");
        om.add(new AttributeMapper("patientId","patient_id",true,AttributeAccess.METHOD,AttributeType.INTEGER));
        om.add(new AttributeMapper("aspect","aspekt",true,AttributeAccess.METHOD,AttributeType.STRING));
        Datastore.current.register(om);

        locks = new Hashtable<String, Lock>();
    }

    /**
     * The patient which the lock is for.
     */
    private int patientId;

    /**
     * The corresponding aspect value.
     */
    private String aspect;

    /**
     * Indicates the released state of this lock.
     */
    private boolean released;

    /**
     * The creation time of the lock.
     */
    private long timestamp;

    /**
     * Timeout value for this lock in seconds.
     */
    private int timeout;

    /**
     * The "has Id" value.
     */
    private boolean hid;;

    public Lock() {}

    /**
     * Locks could only be created from the method acquireLock.
     * @see #acquireLock
     */
    private Lock(int patientId,String aspect,int timeout) {
        this.patientId = patientId;
        this.aspect = aspect;

        released = false;
        timestamp = System.currentTimeMillis();
        this.timeout = timeout;

        hid = false;
    }

    public void setPatientId(int id) {
        patientId = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }

    public String getAspect() {
        return aspect;
    }

    // Framework ------------------------------------------------------------

    protected boolean hasIdentity() {
        return hid;
    }

    public void setIdentity() throws SQLException {
        hid = true;
    }

    /**
     * finalize() is called by the garbage collector. make sure the lock is
     * released when the object is garbage collected.
     *
     * @since 1.0
     */
    protected void finalize() throws Throwable {
        release();
    }

    public boolean equals(Object o) {
        Lock l = (Lock)o;
        return
            patientId == l.patientId &&
            aspect.equals(l.aspect);
    }

    public String toString() {
        return patientId + ", "+aspect;
    }

    // Management ------------------------------------------------------

    private boolean isValid() throws SQLException {
        if ( timeout > 0 &&
            (System.currentTimeMillis() - timestamp) / 1000 > (long)timeout ) {

            release();
        }

        return !released;
    }

    private void refresh() {
        timestamp = System.currentTimeMillis();
    }

    /**
     * Acquire a lock.
     *
     * @param timeout a timeout value. If <tt>timeout</tt> is greater than
     * <tt>0</tt> the lock will be removed within <tt>timeout</tt> seconds.
     * @return the lock if the lock could be acquired,
     * otherwise <tt>null</tt>
     */
    public synchronized static Lock acquireLock(int patientId,Aspect aspect,int timeout)
        throws SQLException {

        String key = patientId + "," + aspect;
        Lock lock = locks.get(key);
        if ( lock != null ) {
            if ( lock.isValid() ) {
                tracer.trace(DATA,"Acquire lock "+lock+" from cache");
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
            lock = new Lock(patientId,aspect.toString(),timeout);
            lock.save();
            tracer.trace(DATA,"Acquire lock "+lock+" from table");
            locks.put(key,lock);
            return lock;
        }
        catch ( SQLException sqlx ) {
            return null;
        }
    }

    public synchronized static boolean release(int patientId,Aspect aspect)
        throws SQLException {

        String key = patientId + "," + aspect;
        Lock lock = locks.get(key);
        if ( lock != null ) {
            lock.release();
            return true;
        }

        return false;
    }

    /**
     * This method tries to release all known locks (e.g. the locks stored
     * in the hashtable).
     * <p>
     * If an exception occures the method continues releasing locks,
     * marks this exception and throws it later.
     */
    public synchronized static void releaseAll()
        throws SQLException {

        SQLException x = null;

        for ( Enumeration<String> e=locks.keys(); e.hasMoreElements(); ) {
            Lock lock = locks.get(e.nextElement());
            try {
                lock.release();
            }
            catch ( SQLException sqlx ) {
                x = sqlx;
            }
        }

        if ( x != null ) {
            throw x;
        }
    }

    public synchronized void release() throws SQLException {
        if ( !released ) {
            delete();
            released = true;
            locks.remove(patientId + "," + aspect);
            tracer.trace(DATA,"Release lock "+this);
        }
    }

    public static class Aspect
    {
        private String string;

        public Aspect() {
            this("");
        }

        public Aspect(String string) {
            this.string = string;
        }

        public String toString() {
            return string;
        }

        public boolean equals(Object o) {
            Aspect a = (Aspect)o;
            return string.equals(a.string);
        }
    }
}
