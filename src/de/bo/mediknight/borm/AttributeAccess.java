package de.bo.mediknight.borm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This interface defines an attribute access strategy for storable objects.
 * @see AttributeMapper
 *
 * @author sma@baltic-online.de
 * @version 1.0
 */
public interface AttributeAccess {

    /** Returns the attribute's value. */
    public Object getValue(Storable object);

    /** Sets the attribute's value. */
    public void setValue(Storable object, Object value);

    /** Returns true if the attribute can store <code>null</code> values. */
    public boolean allowsNullValue();

    /**
     * Creates a specific attribute access instances.  AttributeAccess objects
     * are their own factories and this object makes new instances using the
     * specified <code>attributeName</code> and <code>objectClass</code>.
     */
    public AttributeAccess make(String attributeName, Class objectClass);

    public static final AttributeAccess FIELD = new FieldAccess();
    public static final AttributeAccess METHOD = new MethodAccess();

    static class FieldAccess implements AttributeAccess {

        private Field field;

        private FieldAccess() {
        }

        private FieldAccess(String attributeName, Class objectClass) {
            try {
                field = objectClass.getDeclaredField(attributeName);
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new InternalError("can't create accessor - no public field " + attributeName);
            }
        }

        public AttributeAccess make(String attributeName, Class objectClass) {
            return new FieldAccess(attributeName, objectClass);
        }

        public Object getValue(Storable object) {
            try {
                return field.get(object);
            } catch (IllegalAccessException e) {
                throw new InternalError("it's forbidden to read field " + field.getName());
            }
        }

        public void setValue(Storable object, Object value) {
            try {
                field.set(object, value);
            } catch (IllegalAccessException e) {
                throw new InternalError("it's forbidden to write field " + field.getName());
            }
        }

        public boolean allowsNullValue() {
            return !field.getType().isPrimitive();
        }
    }

    static class LesserFieldAccess extends FieldAccess {
    }

    static class MethodAccess implements AttributeAccess {

        private Method getter;
        private Method setter;

        private MethodAccess() {
        }

        private MethodAccess(String attributeName, Class objectClass) {
            String name =  capitalize(attributeName);
            try {
                getter = objectClass.getMethod("get" + name, null);
            } catch (NoSuchMethodException e) {
                try {
                    getter = objectClass.getMethod("is" + name, null);
                } catch (NoSuchMethodException ee) {
                    throw new InternalError("can't create accessor - no public getter method found for " + attributeName);
                }
            }
            try {
                setter = objectClass.getMethod("set" + name, new Class[]{ getter.getReturnType() });
            } catch (NoSuchMethodException e) {
                throw new InternalError("can't create accessor - no public setter method found for " + attributeName);
            }
        }

        public AttributeAccess make(String attributeName, Class objectClass) {
            return new MethodAccess(attributeName, objectClass);
        }

        public Object getValue(Storable data) {
            try {
                return getter.invoke(data, null);
            } catch (InvocationTargetException e) {
                throw new InternalError("exception while calling getter " + getter.getName());
            } catch (IllegalAccessException e) {
                throw new InternalError("it's forbidden to call getter " + getter.getName());
            }
        }

        public void setValue(Storable data, Object value) {
            try {
                setter.invoke(data, new Object[]{ value });
            } catch (InvocationTargetException e) {
                throw new InternalError("exception while calling setter " + setter.getName());
            } catch (IllegalAccessException e) {
                throw new InternalError("it's forbidden to call setter " + setter.getName());
            } catch (RuntimeException e) {
                System.err.println("No value for "+setter.getName());
            }
        }

        public boolean allowsNullValue() {
            return !getter.getReturnType().isPrimitive();
        }

        private String capitalize(String s) {
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
    }
}