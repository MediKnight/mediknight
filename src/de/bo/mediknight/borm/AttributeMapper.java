package de.bo.mediknight.borm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An attribute mapper defines the way attributes of <code>Storable</code>s
 * are stored into and retrieved from a <code>DataStore</code>. All attribute
 * mappers must be added to an <code>ObjectMapper</code>.
 *
 * <p>An example:<pre>
 * new AttributeMapper("abc", "abc", false, AttributeAccess.FIELD, AttributeType.STRING);
 * </pre>
 *
 * @author sma@baltic-online.de
 * @version 1.0
 */
public class AttributeMapper {

    private String attributeName;
    private String tableName;
    private String columnName;
    private boolean key;
    private AttributeAccess access;
    private AttributeType type;

    /**
     * Constructs a new attribute mapper.
     * @param attributeName the name that is used in Java.
     * @param columnName the name that is used in the DB.
     * @param key a flag set to <code>true</code> if this attribute is part of
     *   object's the unique key.
     * @param access the way, the mapper accesses the <code>Storable</code>
     * @param type the way, the mapper accesses and converts the types
     */
    public AttributeMapper(String attributeName,
                           String columnName,
                           boolean key,
                           AttributeAccess access,
                           AttributeType type) {

        this.attributeName = attributeName;
        this.columnName = columnName;
        this.key = key;
        this.access = access;
        this.type = type;
    }

    /**
     * Returns the attribute name.
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Returns the column name.
     */
    public String getColumnName() {
        return columnName;
    }

    String getTableName() {
        return tableName;
    }

    void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Returns whether this attribute is part of the object's unique key.
     */
    public boolean isKey() {
        return key;
    }

    /**
     * Called by the <code>ObjectMapper</code> to initialize the attribute
     * mapper after adding it.  This method is used to initialize the
     * <code>AttributeAccess</code> object.
     */
    protected void setObjectClass(Class objectClass) {
        access = access.make(attributeName, objectClass);
    }

    /**
     * Returns the associated attribute value of the specified object.
     */
    protected Object getValue(Storable object) {
        return access.getValue(object);
    }

    /**
     * Sets the associated attribute value of the specified object.
     */
    protected void setValue(Storable object, Object value) {
        access.setValue(object, value);
    }

    /**
     * Gets the attribute value and stores it into the specified SQL statement.
     */
    protected void storeAttribute(int index, PreparedStatement stmt, Storable object) throws SQLException {
        type.storeAttribute(index, stmt, getValue(object));
    }

    /**
     * Retrieves a value from the specified result set and puts it into the
     * storable object.
     */
    protected void retrieveAttribute(int index, ResultSet rs, Storable object) throws SQLException {
        setValue(object, type.retrieveAttribute(index, rs));
    }

    // experimental, see AttributeAccess.getSQLType() and ObjectMapper.createStatement()
    protected void createStmt(StringBuffer sb) {
        sb.append(getColumnName());
        sb.append(' ');
        sb.append(type.getSQLType());
        if (isKey() || !access.allowsNullValue())
            sb.append(" NOT NULL");
    }
}