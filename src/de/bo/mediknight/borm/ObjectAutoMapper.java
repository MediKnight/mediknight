package de.bo.mediknight.borm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * An <code>ObjectAutoMapper</code> generates a default mapping for a given
 * subclass of <code>Storable</code>. It will create attribute mappings for
 * all non-static, non-final, non-transient fields, regardness of their
 * visibility.  The table name is the class name without package.  If an
 * attribute with name "id" is found, this becomes the primary key.
 */
public class ObjectAutoMapper extends ObjectMapper {

    /**
     * Constructs a new object mapper for storable objects of the specified class.
     * @param objectClass the class for which the mapping shall be generated
     */
    public ObjectAutoMapper(Class objectClass) {
        super(objectClass, getName(objectClass));
        createAttributeMappers(objectClass);
    }

    /**
     * Returns a table name. The method will return the class name without
     * package prefix.
     * @return a table name for the specified class.
     */
    private static String getName(Class objectClass) {
        String name = objectClass.getName();
        int i = name.lastIndexOf('$');
        if (i == -1)
            i = name.lastIndexOf('.');
        if (i != -1)
            name = name.substring(i + 1);
        return name.toLowerCase();
    }

    /**
     * Creates the attribute mappings for the specified class. The method will
     * create field mappings for all non-static, non-final, non-transient fields,
     * regardless of their visibility. If there's a field named "id", this will
     * become the primary key.
     *
     * @param objectClass the class for which the mapping shall be generated
     *
     * @see AttributeMapper
     * @see AttributeAccess
     */
    protected void createAttributeMappers(Class objectClass) {
        if (objectClass.getSuperclass() != Object.class)
            createAttributeMappers(objectClass.getSuperclass());
        Field[] fields = objectClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if ((f.getModifiers() & Modifier.FINAL + Modifier.STATIC + Modifier.TRANSIENT) == 0) {
                add(new AttributeMapper(f.getName(),
                                        f.getName().toLowerCase(),
                                        f.getName().equalsIgnoreCase("id"),
                                        AttributeAccess.FIELD,
                                        getAttributeType(f.getType())));
            }
        }
    }

    /**
     * Returns the <code>AttributeType</code> for a given Java class.  Supported
     * types are int, Integer, double, Double, boolean, Boolean, String,
     * java.sql.Date and Object (actually Serializable) otherwise.
     * @param type the class type for which we need an <code>AttributeType</code>.
     * @return the attribute type mapping strategy.
     */
    private AttributeType getAttributeType(Class type) {
        if (type == Integer.TYPE || type == Integer.class)
            return AttributeType.INTEGER;
        if (type == Double.TYPE || type == Double.class)
            return AttributeType.DOUBLE;
        if (type == Boolean.TYPE || type == Boolean.class)
            return AttributeType.BOOLEAN;
        if (type == String.class)
            return AttributeType.STRING;
        if (type == java.sql.Date.class)
            return AttributeType.DATE;
        return AttributeType.OBJECT;
    }
}