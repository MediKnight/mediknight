package de.bo.mediknight.borm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

/**
 * This class implements the borm interface to JDBC.  It is able to store Java
 * objects to a relational database, update them, delete them, or retrieve them.
 * Before objects can be managed, you have to register them. For convenience,
 * it provides a default instance <code>Database.current</code> which is used
 * which <code>StorableObject</code> subclasses.
 * @see Storable
 * @see StorableObject
 *
 * @author sma@baltic-online.de
 * @version 1.0
 */
public class Datastore {

    /** The current data store */
    public static Datastore current = new Datastore();

    /** The JDBC connection */
    private Connection connection;

    /**
     * Constructs a new Datastore.  This Datastore isn't connected to a real
     * Datastore yet. Use <code>connect()</code> to establish that connection.
     * If you already have a JDBC connection, consider the other constructor.
     */
    public Datastore() {
    }

    /**
     * Constructs a new Datastore based on an existing JDBC connection. If
     * you want to create your own connection, consider the other constructor.
     *
     * @param connection an existing connection
     */
    public Datastore(Connection connection) {
        this.connection = connection;
    }

    /**
     * Establishes the Datastore connection.
     *
     * @param url JDBC-style connection string (don't forget to load the driver)
     * @param user user name
     * @param passwd user's password
     */
    public void connect(String url, String user, String passwd) throws SQLException {
        connection = DriverManager.getConnection(url, user, passwd);
        // connection.setAutoCommit(true);
    }

    /**
     * Terminates the Datastore connection.
     */
    public void disconnect() throws SQLException {
        connection.close();
        connection = null;
    }

    /**
     * Returns whether the Datastore is connected or not.
     * @return <code>true</code> if the Datastore is connected
     */
    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    /**
     * Returns the datastore's connection or throws an SQLException if it
     * isn't connected yet
     */
    Connection getConnection() throws SQLException {
        if (isConnected())
            return connection;
        throw new SQLException("datastore not connected");
    }

    /**
     * Changes the autocommit state of the underlying connection and
     * returns the previous state.
     */
    public boolean setAutoCommit(boolean auto) throws SQLException {
        boolean state = connection.getAutoCommit();
        if ( state != auto ) {
            connection.setAutoCommit(auto);
        }
        return state;
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    // ----------------------------------------------------------------------

    /**
     * Inserts the specified object into the Datastore.  It is an error if no
     * mapping has been registered for that object.
     *
     * @param object the object that should be stored
     */
    public void insert(Storable object) throws SQLException {
        getMapper(object.getClass()).insert(getConnection(), object);
    }

    /**
     * Updates the specified object to the Datastore.  It is an error if no
     * mapping has been registered for that object.  It's also an error if the
     * object has no unique key.
     *
     * @param object the object that should be updated
     */
    public void update(Storable object) throws SQLException {
        getMapper(object.getClass()).update(getConnection(), object);
    }

    /**
     * Deletes the specified object from the Datastore.  It is an error if no
     * mapping has been registered for that object.  It's also an error if the
     * object has no unique key.
     *
     * @param object the object that should be deleted
     */
    public void delete(Storable object) throws SQLException {
        getMapper(object.getClass()).delete(getConnection(), object);
    }

    /**
     * Reload the specified object based on its unique key. It is an error if no
     * mapping has been registered for that object.  It's also an error if the
     * object has no unique key.
     *
     * @param object the object that should be reloaded
     */
    public void reload(Storable object) throws SQLException {
        getMapper(object.getClass()).reload(getConnection(), object);
    }

    // ----------------------------------------------------------------------

    public Query getQuery(Class objectClass) {
        return getQuery(objectClass, null);
    }

    public Query getQuery(Class objectClass, String query) {
        Query q = new Query(this);
        q.setObjectClass(objectClass);
        q.setQuery(query);
        return q;
    }

    public ResultSet execute(String sql) throws SQLException {
        Statement stmt = getConnection().createStatement();
        if (stmt.execute(sql))
            return stmt.getResultSet();
        stmt.close();
        return null;
    }

    // ----------------------------------------------------------------------

    private Map objectMappings = new HashMap();

    /**
     * Registers an object mapping for use with this Datastore.
     *
     * @param objectMapper the object mapping
     */
    public void register(ObjectMapper objectMapper) {
        objectMappings.put(objectMapper.getObjectClass(), objectMapper);
    }

    /**
     * Registers an object auto mapping for use with this Datastore.  The auto
     * mapping creates attributes for all non-transient fields and assumes
     * table names and columns to be the same as the class name and the field
     * names.
     *
     * @param objectClass the class which shall be auto mapped
     */
    public void register(Class objectClass) {
        register(new ObjectAutoMapper(objectClass));
    }

    /**
     * Returns the object mapping for the specified class. Use
     * <code>@link{register()}</code> to register a mapping.
     *
     * @param clazz the class for which the object mapping is requested
     * @return the registred object mapping
     * @exception SQLException if no mapping has need registered
     */
    protected ObjectMapper getMapper(Class clazz) throws SQLException {
        ObjectMapper objectMapper = (ObjectMapper)objectMappings.get(clazz);
        if (objectMapper == null)
            throw new SQLException("no mapping for class " + clazz);
        return objectMapper;
    }
}