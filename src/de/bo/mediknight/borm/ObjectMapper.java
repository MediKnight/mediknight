package de.bo.mediknight.borm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

/**
 * This class implements the way how objects are mapped onto tables and vice
 * versa. It generates all needed SQL statements and uses a bunch of
 * <code>AttributeMapper</code>s to actually do the mapping.
 *
 * @author sma@baltic-online.de
 * @version 1.0
 */
public class ObjectMapper {

    public static boolean debug = false;

    private Class objectClass;

    private String tableName;

    private AttributeMapper[] attributeMappers = new AttributeMapper[0];

    public ObjectMapper(Class objectClass, String tableName) {
        this.objectClass = objectClass;
        this.tableName = tableName;
    }

    public Class getObjectClass() {
        return objectClass;
    }

    public String getTableName() {
        return tableName;
    }

    public ObjectMapper add(AttributeMapper mapper) {
        int len = attributeMappers.length;
        AttributeMapper[] newMappers = new AttributeMapper[len + 1];
        System.arraycopy(attributeMappers, 0, newMappers, 0, len);
        newMappers[len] = mapper;
        attributeMappers = newMappers;
        mapper.setTableName(getTableName());
        mapper.setObjectClass(objectClass);
        return this;
    }

    public boolean hasKey() {
        for (int i = 0; i < attributeMappers.length; i++)
            if (attributeMappers[i].isKey())
                return true;
        return false;
    }

    public AttributeMapper getAttributeMapper(String name) {
        for (int i = 0; i < attributeMappers.length; i++) {
            AttributeMapper am = attributeMappers[i];
            if (am.getAttributeName().equals(name))
                return am;
        }
        return null;
    }

    // ----------------------------------------------------------------------

    void insert(Connection connection, Storable object) throws SQLException {
        List paramMappers = new ArrayList(12);
        String ss = getInsertStatement(paramMappers);
        PreparedStatement stmt = connection.prepareStatement(ss);
        setup(stmt, object, paramMappers);
        if ( debug ) {
            System.out.println(stmt);
            System.out.println(object);
        }
        stmt.executeUpdate();
        // if (stmt.executeUpdate() != 1) throw new SQLException("nothing inserted");
        stmt.close();
    }

    void update(Connection connection, Storable object) throws SQLException {
        if (! hasKey()) throw new SQLException("no primary key specified");
        List paramMappers = new ArrayList(12);
        String ss = getUpdateStatement(paramMappers);
        PreparedStatement stmt = connection.prepareStatement(ss);
        setup(stmt, object, paramMappers);
        if ( debug ) {
            System.out.println(stmt);
            System.out.println(object);
        }
        stmt.executeUpdate();
        // if (stmt.executeUpdate() != 1) throw new SQLException("nothing updated");
        stmt.close();
    }

    void delete(Connection connection, Storable object) throws SQLException {
        if (! hasKey()) throw new SQLException("no primary key specified");
        List paramMappers = new ArrayList(12);
        String ss = getDeleteStatement(paramMappers);
        PreparedStatement stmt = connection.prepareStatement(ss);
        setup(stmt, object, paramMappers);
        if ( debug ) {
            System.out.println(stmt);
            System.out.println(object);
        }
        stmt.executeUpdate();
        // if (stmt.executeUpdate() != 1) throw new SQLException("nothing deleted");
        stmt.close();
    }

    void reload(Connection connection, Storable object) throws SQLException {
        if (! hasKey()) throw new SQLException("no primary key specified");
        List paramMappers = new ArrayList(12);
        PreparedStatement stmt = connection.prepareStatement(getReloadStatement(paramMappers));
        setup(stmt, object, paramMappers);
        ResultSet rs = stmt.executeQuery();
        if (! rs.next()) throw new SQLException("object not found");
        // retrieve non-key values from result set
        int j = 1;
        for (int i = 0; i < attributeMappers.length; i++)
            if (!attributeMappers[i].isKey())
                attributeMappers[i].retrieveAttribute(j++, rs, object);
        rs.close();
        stmt.close();
    }

    // ----------------------------------------------------------------------

    private String getInsertStatement(List paramMappers)  {

        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO ");
        sb.append(getTableName());
        sb.append(" (");
        for (int i = 0; i < attributeMappers.length; i++) {
            if (i > 0)
                sb.append(',');
            sb.append(attributeMappers[i].getColumnName());
        }
        sb.append(") VALUES (");
        for (int i = 0; i< attributeMappers.length; i++) {
            if (i > 0)
                sb.append(',');
            sb.append('?');
            paramMappers.add(attributeMappers[i]);
        }
        sb.append(')');
        return sb.toString();
    }

    private String getUpdateStatement(List paramMappers) {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE ");
        sb.append(getTableName());
        sb.append(" SET ");
        boolean first = true;
        for (int i = 0; i < attributeMappers.length; i++) {
            if (!attributeMappers[i].isKey()) {
                if (first)
                    first = false;
                else
                    sb.append(',');
                sb.append(attributeMappers[i].getColumnName());
                sb.append("=?");
                paramMappers.add(attributeMappers[i]);
            }
        }
        keyWhereClause(sb, paramMappers);
        return sb.toString();
    }

    private String getDeleteStatement(List paramMappers) {
        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM ");
        sb.append(getTableName());
        keyWhereClause(sb, paramMappers);
        return sb.toString();
    }

    private String getReloadStatement(List paramMappers) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ");
        boolean first = true;
        for (int i = 0; i < attributeMappers.length; i++) {
            if (!attributeMappers[i].isKey()) {
                if (first)
                    first = false;
                else
                    sb.append(',');
                sb.append(attributeMappers[i].getColumnName());
            }
        }
        sb.append(" FROM ");
        sb.append(getTableName());
        keyWhereClause(sb, paramMappers);
        return sb.toString();
    }

    private void keyWhereClause(StringBuffer sb, List paramMappers) {
        sb.append(" WHERE ");
        boolean first = true;
        for (int i = 0; i < attributeMappers.length; i++) {
            if (attributeMappers[i].isKey()) {
                if (first)
                    first = false;
                else
                    sb.append(" AND ");
                sb.append('(');
                sb.append(attributeMappers[i].getColumnName());
                sb.append("=?)");
                paramMappers.add(attributeMappers[i]);
            }
        }
    }

    private void setup(PreparedStatement stmt, Storable object, List paramMappers)
        throws SQLException {

        Iterator i = paramMappers.iterator();
        int j = 1;
        while (i.hasNext())
            ((AttributeMapper)i.next()).storeAttribute(j++, stmt, object);
    }

    // ----------------------------------------------------------------------

    Iterator select(Connection connection, Query q) throws SQLException {
        List paramValues = new ArrayList();
        PreparedStatement stmt = connection.prepareStatement(getSelectStatement(q, paramValues));
        int j = 1;
        for (Iterator i = paramValues.iterator(); i.hasNext(); ) {
            stmt.setObject(j++, i.next());
        }
        return new SelectIterator(stmt.executeQuery());
    }

    private String getSelectStatement(Query q, List paramValues) throws SQLException {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT DISTINCT ");
        // add object's column names
        for (int i = 0; i < attributeMappers.length; i++) {
            if (i > 0)
                sb.append(',');
            sb.append(attributeMappers[i].getTableName());
            sb.append('.');
            sb.append(attributeMappers[i].getColumnName());
        }

        Set tableNames = new HashSet();
        tableNames.add(getTableName());
        String whereClause = q.parseQuery(tableNames, paramValues);

        sb.append(" FROM ");
        boolean first = true;
        for (Iterator i = tableNames.iterator(); i.hasNext();) {
            if (first)
                first = false;
            else
                sb.append(',');
            sb.append(i.next());
        }

        if (whereClause.length() > 0) {
            sb.append(" WHERE ");
            sb.append(whereClause);
        }
        return sb.toString();
    }

    class SelectIterator implements Iterator {
        private ResultSet rs;

        SelectIterator(ResultSet rs) {
            this.rs = rs;
        }

        private Boolean alreadyAsked;

        public boolean hasNext() throws RuntimeSQLException {
            if (alreadyAsked == null) {
                try {
                    alreadyAsked = rs.next() ? Boolean.TRUE : Boolean.FALSE;
                } catch (SQLException e) {
                    throw new RuntimeSQLException("[Query][Iterator]", e);
                }
            }
            return alreadyAsked.booleanValue();
        }

        public Object next() throws RuntimeSQLException {
            hasNext();
            alreadyAsked = null;

            try {
                Storable object = (Storable)objectClass.newInstance();
                int j = 1;
                for (int i = 0; i < attributeMappers.length; i++)
                    attributeMappers[i].retrieveAttribute(j++, rs, object);
                return object;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeSQLException("[Iterator]", e);
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new RuntimeSQLException("[Iterator]", e);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeSQLException("[Iterator]", e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void finalize() throws Throwable {
            rs.close();
            super.finalize();
        }
    }

    // ----------------------------------------------------------------------

    // experimental
    String getCreateStatement() {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE ");
        sb.append(getTableName());
        sb.append(" (");
        for (int i = 0; i < attributeMappers.length; i++) {
            if (i > 0)
                sb.append(',');
            attributeMappers[i].createStmt(sb);
        }
        if (hasKey()) {
            sb.append(", PRIMARY KEY (");
            boolean first = true;
            for (int i = 0; i < attributeMappers.length; i++) {
                if (attributeMappers[i].isKey()) {
                    if (first)
                        first = false;
                    else
                        sb.append(',');
                    sb.append(attributeMappers[i].getColumnName());
                }
            }
            sb.append(')');
        }
        sb.append(')');
        return sb.toString();
    }

    // experimental
    String getDestroyStatement() {
        return "drop table " + getTableName();
    }

    // ----------------------------------------------------------------------

    class Key {
        private Storable object;
        private int hashCode;

        Key(Storable object) {
            this.object = object;
            hashCode = ObjectMapper.this.hashCode();
            for (int i = 0; i < attributeMappers.length; i++) {
                if (attributeMappers[i].isKey()) {
                    hashCode <<= 3;
                    hashCode ^= attributeMappers[i].getValue(object).hashCode();
                }
            }
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(Object object) {
            if (this == object)
                return true;
            if (!(object instanceof Key))
                return false;
            Key key = (Key)object;
            if (getMapper() != key.getMapper())
                return false;
            for (int i = 0; i < attributeMappers.length; i++) {
                if (attributeMappers[i].isKey()) {
                    Object v1 = getValue(i);
                    Object v2 = key.getValue(i);
                    if (!(v1 == null && v2 == null || v1.equals(v2)))
                        return false;
                }
            }
            return true;
        }

        private Object getValue(int index) {
            return attributeMappers[index].getValue(object);
        }

        private ObjectMapper getMapper() {
            return ObjectMapper.this;
        }
    }

    Key getKey(Storable object) {
        return new Key(object);
    }

    Object[] getValues(Storable object) {
        Object[] values = new Object[attributeMappers.length];
        for (int i = 0; i < attributeMappers.length; i++)
            values[i] = attributeMappers[i].getValue(object);
        return values;
    }

    void setValues(Storable object, Object[] values) {
        for (int i = 0; i < attributeMappers.length; i++)
            attributeMappers[i].setValue(object, values[i]);
    }

    void bindValues(Map values, Storable object) {
        for (int i = 0; i < attributeMappers.length; i++)
            values.put(attributeMappers[i].getAttributeName(),
                       attributeMappers[i].getValue(object));
    }
}