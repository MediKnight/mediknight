package de.bo.mediknight.borm;

import java.sql.SQLException;

/**
 * Useful superclass of all domain classes of database-storable objects.
 * @see de.bo.mediknight.borm.Storable
 *
 * @author sma@baltic-online.de
 * @version 1.0
 */
public abstract class StorableObject implements Storable {

    public StorableObject() {
    }

    protected Datastore getDatastore() {
        return Datastore.current;
    }

    public void insert() throws SQLException {
        getDatastore().insert(this);
    }

    public void update() throws SQLException {
        getDatastore().update(this);
    }

    public void delete() throws SQLException {
        getDatastore().delete(this);
    }

    public void reload() throws SQLException {
        getDatastore().reload(this);
    }

    public Query getQuery(String query) {
        return getDatastore().getQuery(getClass(), query);
    }
}