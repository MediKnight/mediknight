/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import de.bo.mediknight.borm.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract superclass of all database-storable mediknight objects.
 *
 * @author sma@baltic-online.de
 */
public abstract class KnightObject implements Storable {

    public final static String DATA = "data";

    public final static int MYSQL = 0;
    public final static int HYPERSONIC = 1;

    // Important:
    // ==========
    // Change this member to work with MySQL
    private final static int DB = MYSQL;

    protected static Tracer tracer;

    public static void setTracer(Tracer t) {
        tracer = t;
    }

    public static Tracer getTracer() {
        return tracer;
    }

    public static String notNull(String s) {
        return (s != null) ? s : "";
    }

    /**
     * Makes the receiver persistent in the database.
     */
    public void save() throws SQLException {
        if ( hasIdentity() ) {
            //tracer.trace(DATA,"Update "+toLongString());
            Datastore.current.update(this);
        }
        else {
            //tracer.trace(DATA,"Insert "+toLongString());
            if ( DB == MYSQL ) {
                Datastore.current.insert(this);
                setIdentity();
            }
            else if ( DB == HYPERSONIC ) {
                setIdentity();
                Datastore.current.insert(this);
            }
            else {
                throw new SQLException("cannot operate on unknown database type");
            }
        }
    }

    /**
     * Recalls the persistant receiver's latest values from the database.
     */
    public void recall() throws SQLException {
        if ( hasIdentity() ) {
            //tracer.trace(DATA,"Reload "+toLongString());
            Datastore.current.reload(this);
        }
        else
            throw new SQLException("[recall] object has no key");
    }

    /**
     * Remove a persistent object from the database.
     */
    public void delete() throws SQLException {
        if ( hasIdentity() ) {
            //tracer.trace(DATA,"Delete "+toLongString());
            Datastore.current.delete(this);
        }
        else {
            throw new SQLException("[delete] object has no key");
        }
    }

    /**
     * Returns true if the receiver has a valid key.
     */
    protected abstract boolean hasIdentity() throws SQLException;

    /**
     * Sets the receiver's unique key.  Called after <code>insert()</code>.
     */
    protected abstract void setIdentity() throws SQLException;

    /**
     * Retrieve an auto-increment value for an inserted object.
     * <p><i>Note: This method is mySQL specific.</i>
     */
    protected int getLastId() throws SQLException {

        int id = 0;
        if ( DB == MYSQL ) {
            // Hypersonic alternatives:
            // ResultSet rs = Datastore.current.execute("call last_insert_id()");
            // ResultSet rs = Datastore.current.execute("call identity()");
            ResultSet rs = Datastore.current.execute("select last_insert_id()");
            if (!rs.next())
                throw new SQLException("[insert] can't read insert id");
            id = rs.getInt(1);
            rs.close();
            return id;
        } else if ( DB == HYPERSONIC ) {
            id = (int)getSequenceId();
            if ( id == 0 )
                throw new SQLException("cannot retrieve sequence id");
            return id;
        } else {
            throw new SQLException("cannot operate on unknown database type");
        }
    }

    public static long getSequenceId() {
        //boolean autoCommit = false;
        Datastore ds = Datastore.current;
        long id = 0;
        try {
            //autoCommit = ds.setAutoCommit(false);
            ResultSet rs = ds.execute("select id from sequenceid");
            if ( rs.next() ) {
                id = rs.getLong(1);
                ds.execute("update sequenceid set id="+(id+1L));
            }
            else {
                id = 1L;
                ds.execute("insert into sequenceid values("+(id+1L)+")");
            }
            //ds.commit();

            return id;
        }
        catch ( SQLException x ) {
            throw new RuntimeException(x.getMessage());
        }
        finally {
            /*
            try {
                ds.setAutoCommit(autoCommit);
            }
            catch ( SQLException x ) {
                throw new RuntimeException(x.getMessage());
            }
            */
        }
    }

    public String toLongString() {
        return toString();
    }

    /**
     * Converts an <code>Iterator</code> into a <code>List</code>.
     */
    public static List toList(Iterator i) {
        List list = new ArrayList();
        while (i.hasNext())
            list.add(i.next());
        return list;
    }
}
