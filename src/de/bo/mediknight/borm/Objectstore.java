/* This code isn't finished yet!
 * TODO:
 *  - get rid of sqlexceptions
 *  - keep identities
 *  - add automatic 1:n, 0:1 mappings
 *  - implement reload strategy
 */
package de.bo.mediknight.borm;

import java.lang.ref.WeakReference;
import java.util.*;
import java.sql.SQLException;

/**
 * This class implements a lightweight persistent object manager to which one
 * can add and remove storable objects.  It supports simple object-level
 * transactions.
 *
 * @see PersistentObject
 *
 * @author sma@baltic-online.de
 * @version 1.0
 */
public class Objectstore {

    public static final Objectstore current = new Objectstore(Datastore.current);

    private Datastore datastore;
    private Set deletionCandidates = new HashSet();
    private Set additionCandidates = new HashSet();
    private Map objectMap = Collections.synchronizedMap(new WeakHashMap());

    /**
     * Constructs a new persistent object store.
     * @param datastore the datastore on which this instance is based
     */
    public Objectstore(Datastore datastore) {
        this.datastore = datastore;
    }

    /**
     * Returns true, if the specified object is persistent, that is, has
     * already been stored to a database.
     * @param object the object to test
     */
    public boolean isPersistent(Storable object) {
        return objectMap.containsKey(object);
    }

    /**
     * Makes the specified object persistent, that is, stores it into the
     * database with the next call of <code>commit()</code>.  It is an error
     * if you call this method twice for one object.  Please note that upon
     * <code>commit()</code>, all referenced <code>Storable</code> objects are
     * also made persistent.
     * @param object the object that shall become persistent
     */
    public synchronized void makePersistent(Storable object) {
        if (isPersistent(object))
            throw new RuntimeException("object already persistent");
        try {
            objectMap.put(object, new Cache(object));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        deletionCandidates.remove(object);
        additionCandidates.add(object);
    }

    /**
     * Deletes the specified object from the datastore with the next call of
     * <code>commit()</code>.  It is an error if you call this method for
     * objects which aren't already persistent.
     * @param object the object that shall become transient
     */
    public synchronized void delete(Storable object) {
        if (!isPersistent(object))
            throw new RuntimeException("object is not persistent");
        deletionCandidates.add(object);
        additionCandidates.remove(object);
    }

    /**
     * Deletes the specified object plus all references persistent objects
     * from the datastore with the next call of <code>commit()</code>.
     */
    public synchronized void deleteAll(Storable object) {
        Cache c = (Cache)objectMap.get(object);
        if (c != null)
            c.delete(object);
        throw new RuntimeException("object is not persistent");
    }

    /**
     * Commits all channges, additions and removals to the database.
     */
    public synchronized void commit() {
        try {
            Iterator i;
            i = deletionCandidates.iterator();
            while (i.hasNext()) {
                Storable object = (Storable)i.next();
                doDelete(object);
                objectMap.remove(object);
            }
            i = objectMap.values().iterator();
            while (i.hasNext()) {
                ((Cache)i.next()).commit();
            }
            clearCandidates();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reverts all changes and ignores additions and removals.
     */
    public synchronized void rollback() {
        Iterator i = objectMap.values().iterator();
        while (i.hasNext()) {
            ((Cache)i.next()).rollback();
        }
        clearCandidates();
    }

    /** Clears addition and removal queues. */
    private void clearCandidates() {
        deletionCandidates.clear();
        additionCandidates.clear();
    }

    /** Performs the deletion of an object */
    protected void doDelete(Object object) {
        try {
            datastore.delete((Storable)object);
            objectMap.remove(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Cache getCache(Storable object) throws SQLException {
        Cache c = (Cache)objectMap.get(object);
        if (c == null) {
            c = new Cache(object);
            objectMap.put(object, c);
        }
        return c;
    }


    /** Performs insertion of an object */
    protected void doInsert(Storable object) {
        try {
            datastore.insert(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Performs update of an object */
    protected void doUpdate(Storable object) {
        try {
            datastore.update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This class implements an object value cache which keeps the object's
     * values of the last known persistent state.  All changes are done to
     * the original object so this cache keeps the old values which are used
     * to detect if an update is needed and to rollback changes to an object.
     */
    class Cache {
        ObjectMapper mapper;
        Storable object;
        Object[] values;

        private Cache(Storable object) throws SQLException {
            this.mapper = datastore.getMapper(object.getClass());
            this.object = object;
        }


        void delete(Storable object) {
            values = mapper.getValues(object);
            for (int i = 0; i < values.length; i++)
                if (values[i] instanceof Storable)
                    deleteAll((Storable)values[i]);
        }

        void commit() throws SQLException {
            Object[] currentValues = mapper.getValues(object);
            if (values == null) {
                doInsert(object);
                values = currentValues;
            } else {
                boolean needUpdate = false;
                for (int i = 0; i < values.length; i++) {
                    Object v = currentValues[i];
                    if (v == null && values[i] == null || v.equals(values[i]))
                        continue;
                    needUpdate = true;
                    if (v instanceof Storable) {
                        getCache((Storable)v).commit();
                    }
                }
                if (needUpdate) {
                    doUpdate(object);
                    values = currentValues;
                }
            }
        }

        void rollback() {
            if (values != null)
                mapper.setValues(object, values);
        }
    }

    /**
     * Returns an iterator on all persistent instances.
     */
    public Iterator extent(Class clazz) throws SQLException {
        return new IdentityIterator(datastore.getQuery(clazz).execute());
    }

    private class IdentityIterator implements Iterator {
        private Iterator i;
        IdentityIterator(Iterator i) {
            this.i = i;
        }
        public boolean hasNext() throws RuntimeSQLException {
            return i.hasNext();
        }

        public Object next() throws RuntimeSQLException {
            try {
                return identity((Storable)i.next());
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns the references object.  This is used as follows:
     * <pre>class Patient {
     *   Diagnose getDiagnose() {
     *     return (Diagnose)objectStore().getReference(Diagnose.class,
     *                                                 "patient_id=:id",
     *                                                 this);
     *   }
     * }</pre>
     */
    public Storable getReference(Class clazz, String query, Storable object) throws SQLException {
        Iterator i = datastore.getQuery(clazz, query).bind(object).execute();
        if (!i.hasNext())
            return null;
        return identity((Storable)i.next());
    }

    private Map classMap = new HashMap();

    private Storable identity(Storable object) throws SQLException {
        ObjectMapper m = datastore.getMapper(object.getClass());
        ObjectMapper.Key key = m.getKey(object);
        Map map = (Map)classMap.get(m);
        if (map == null) {
            map = new HashMap();
            map.put(key, new WeakReference(object));
            classMap.put(m, map);
            return object;
        }
        WeakReference existing = (WeakReference)map.get(key);
        if (existing == null || existing.get() == null) {
            map.put(key, new WeakReference(object));
            return object;
        }
        return (Storable)existing.get();
    }
}