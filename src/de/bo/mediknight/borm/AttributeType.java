package de.bo.mediknight.borm;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * This interface implements a strategy how to store attribute values into
 * <code>PreparedStatement</code>s and how to retrieve them from <code>ResultSet</code>s.
 * <p>The following strategies are predefined
 * <table border=1>
 * <tr><th>Name</th><th>Java type</th><th>SQL92 type</th></tr>
 * <tr><td> INTEGER </td><td> int, Integer     </td><td> INT     </td></tr>
 * <tr><td> BOOLEAN </td><td> boolean, Boolean </td><td> CHAR(1) </td></tr>
 * <tr><td> DOUBLE  </td><td> double, Double   </td><td> DOUBLE  </td></tr>
 * <tr><td> STRING  </td><td> String           </td><td> TEXT    </td></tr>
 * <tr><td> DATE    </td><td> java.sql.Date    </td><td> DATE    </td></tr>
 * <tr><td> OBJECT  </td><td> Object           </td><td> BLOB    </td></tr>
 * </table>
 * @see AttributeMapper
 *
 * @author sma@baltic-online.de
 * @version 1.0
 */
public interface AttributeType {
    /** Stores an attribute value into a prepared statement. */
    public void storeAttribute(int index, PreparedStatement stmt, Object value) throws SQLException;

    /** Retrieves an attribute value from a result set. */
    public Object retrieveAttribute(int index, ResultSet rs) throws SQLException;

    /**
     * Returns the SQL92 data type for the attribute.  This is an experimental
     * feature for automatic table creation and may not work for all databases.
     */
    public String getSQLType();

    public static final AttributeType INTEGER = new IntegerType();
    public static final AttributeType DOUBLE = new DoubleType();
    public static final AttributeType STRING = new StringType();
    public static final AttributeType BOOLEAN = new BooleanType();
    public static final AttributeType DATE = new DateType();
    public static final AttributeType OBJECT = new ObjectType();

    static class IntegerType implements AttributeType {
        public void storeAttribute(int index, PreparedStatement stmt, Object value) throws SQLException {
            if (value == null)
                stmt.setNull(index, Types.INTEGER);
            else
                stmt.setInt(index, ((Integer)value).intValue());
        }

        public Object retrieveAttribute(int index, ResultSet rs) throws SQLException {
            int value = rs.getInt(index);
            return rs.wasNull() ? null : new Integer(value);
        }

        public String getSQLType() {
            return "INT";
        }
    }

    static class DoubleType implements AttributeType {
        public void storeAttribute(int index, PreparedStatement stmt, Object value) throws SQLException {
            if (value == null)
                stmt.setNull(index, Types.DOUBLE);
            else
                stmt.setDouble(index, ((Double)value).doubleValue());
        }

        public Object retrieveAttribute(int index, ResultSet rs) throws SQLException {
            double value = rs.getDouble(index);
            return rs.wasNull() ? null : new Double(value);
        }

        public String getSQLType() {
            return "DOUBLE";
        }
    }

    static class StringType implements AttributeType {
        public void storeAttribute(int index, PreparedStatement stmt, Object value) throws SQLException {
            stmt.setString(index, (String)value);
        }

        public Object retrieveAttribute(int index, ResultSet rs) throws SQLException {
            return rs.getString(index);
        }

        public String getSQLType() {
            return "TEXT";
        }
    }

    static class BooleanType implements AttributeType {
        public void storeAttribute(int index, PreparedStatement stmt, Object value) throws SQLException {
            if (value == null)
                stmt.setNull(index, Types.CHAR);
            else
                stmt.setString(index, ((Boolean)value).booleanValue() ? "Y" : "N");
        }

        public Object retrieveAttribute(int index, ResultSet rs) throws SQLException {
            String value = rs.getString(index);
            if (value == null)
                return null;
            if (value.equalsIgnoreCase("Y"))
                return Boolean.TRUE;
            return Boolean.FALSE;
        }

        public String getSQLType() {
            return "CHAR(1)";
        }
    }

    static class DateType implements AttributeType {
        public void storeAttribute(int index, PreparedStatement stmt, Object value) throws SQLException {
            stmt.setDate(index, (Date)value);
        }

        public Object retrieveAttribute(int index, ResultSet rs) throws SQLException {
            return rs.getDate(index);
        }

        public String getSQLType() {
            return "DATE";
        }
    }

    static class ObjectType implements AttributeType {
        public void storeAttribute(int index, PreparedStatement stmt, Object value) throws SQLException {
            stmt.setObject(index, value);
        }

        public Object retrieveAttribute(int index, ResultSet rs) throws SQLException {
            return rs.getObject(index);
        }

        public String getSQLType() {
            return "BLOB";
        }
    }
}