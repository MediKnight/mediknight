package de.bo.mediknight.borm;

import java.util.*;
import java.sql.*;

/**
 * This class implements an object query.
 *
 * @author sma@baltic-online.de
 * @version 1.0
 */
public class Query {

    private Datastore datastore;
    private Class objectClass;
    private String query;
    private Map values = new HashMap(5);

    /**
     * Constructs a new query object connnected to the specified datastore.
     */
    Query(Datastore datastore) {
        this.datastore = datastore;
    }

    /**
     * Assigns the class of the storable object that are constructed by this Query.
     *
     * @param objectClass the object class, it must implement <code>Storable</code>
     */
    public void setObjectClass(Class objectClass) {
        this.objectClass = objectClass;
    }

    /**
     * Assigns the query string to this Query. The string can be either pure SQL
     * or contain "?" or ":name" variable references which must be bound using
     * the <code>bind()</code> method.  Names starting with "$" are considered
     * as attribute names - not column names - and are replaced with real the
     * column names later.  An attribute name may be prefixed with a class name
     * which is then translated into the corresponding SQL table name.
     *
     * @param query the SQL query string; everything that follows the "WHERE"
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Binds the specified value to the given variable name.
     */
    public Query bind(String variable, Object value) {
        values.put(variable, value);
        return this;
    }

    /**
     * Binds the specified value to the index'd "?" parameter
     */
    public Query bind(int index, Object value) {
        values.put(new Integer(index), value);
        return this;
    }

    public Query bind(Storable object) throws SQLException {
        datastore.getMapper(object.getClass()).bindValues(values, object);
        return this;
    }

    /**
     * Executes the query and returns an Iterator to iterate the queried objects.
     */
    public Iterator execute() throws SQLException {
        return datastore.getMapper(objectClass).select(datastore.getConnection(), this);
    }

    /**
     * Generates a valid SQL WHERE clause from the Query's query string.
     */
    String parseQuery(Set tableNames, List paramValues) throws SQLException {
        if (query == null || query.equals(""))
            return "";

        StringBuffer sb = new StringBuffer(256);

        int i = 0;
        int count = 0;
        while (i < query.length()) {
            char ch = query.charAt(i++);

            if (ch == '"')
                while ((ch = query.charAt(i++)) != '"')
                    ;
            else if (ch == '\'')
                while ((ch = query.charAt(i++)) != '\'')
                    ;
            else if (ch == '{')
                while ((ch = query.charAt(i++)) != '}')
                    ;
            else if (ch == '[')
                while ((ch = query.charAt(i++)) != ']')
                    ;
            else if (ch == ':') {
                StringBuffer buf = new StringBuffer(16);
                while (i < query.length() && Character.isJavaIdentifierPart(ch = query.charAt(i++)))
                    buf.append(ch);
                i--;
                sb.append('?');
                paramValues.add(getValue(buf.toString()));
            }
            else if (ch == '$') {
                StringBuffer buf = new StringBuffer(16);
                while (i < query.length() && Character.isJavaIdentifierPart(ch = query.charAt(i++)))
                    buf.append(ch);
                i--;
                Class clazz = objectClass;
                String attributeName = buf.toString();
                int j = attributeName.indexOf('.');
                if (j != -1) {
                    String className = attributeName.substring(0, j - 1);
                    try {
                        clazz = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new SQLException("[Query] unknown storable class " + className);
                    }
                    attributeName = attributeName.substring(j + 1);
                }
                ObjectMapper mapper = datastore.getMapper(clazz);
                String tableName = mapper.getTableName();
                tableNames.add(tableName);
                sb.append(tableName);
                sb.append('.');
                AttributeMapper am = mapper.getAttributeMapper(attributeName);
                if (am == null)
                    throw new SQLException("[Query] unknown attribute " + attributeName);
                sb.append(am.getColumnName());
            }
            else if (ch == '?') {
                sb.append(ch);
                paramValues.add(getValue(new Integer(++count)));
            }
            else
                sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Returns a bound value or raises an exception if the variable is unknown.
     */
    private Object getValue(Object key) throws SQLException {
        Object value = values.get(key);
        if (value == null)
            if (!values.keySet().contains(key))
                throw new SQLException("[Query] unbound variable " + key);
        return value;
    }
}