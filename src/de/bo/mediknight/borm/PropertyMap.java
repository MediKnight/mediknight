package de.bo.mediknight.borm;

import java.lang.reflect.*;
import java.util.*;
import java.util.Iterator;
import java.beans.*;

public class PropertyMap implements Map {

    private Object bean;
    private Map getters;
    private Map setters;

    public PropertyMap(Object bean) {
        if (bean == null)
            throw new IllegalArgumentException("given object must not be null");
        try {
            PropertyDescriptor[] pds;
            pds = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
            getters = new HashMap(pds.length);
            setters = new HashMap(pds.length);
            for (int i = 0; i < pds.length; i++) {
                String name = pds[i].getName();
                getters.put(name, pds[i].getReadMethod());
                setters.put(name, pds[i].getWriteMethod());
            }
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException("cannot introspect given object");
        }
        this.bean = bean;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    public int size() {
        return getters.size();
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(Object key) {
        return getters.containsKey(key);
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.  More formally, returns <tt>true</tt> if and only if
     * this map contains at least one mapping to a value <tt>v</tt> such that
     * <tt>(value==null ? v==null : value.equals(v))</tt>.
     */
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    /**
     * Returns the value to which this map maps the specified key.  Returns
     * <tt>null</tt> if the map contains no mapping for this key.  A return
     * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
     * map contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     */
    public Object get(Object key) {
        try {
            Method m = (Method)getters.get(key);
            if (m == null)
                return null;
            return m.invoke(bean, null);
        } catch (InvocationTargetException e) {
            throw runtimify(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("you need a public getter method");
        }
    }

    public Object put(Object key, Object value) {
        try {
            Method m = (Method)setters.get(key);
            if (m == null)
                throw new IllegalArgumentException("this property is read only or does not exist");
            return m.invoke(bean, new Object[]{ value });
        } catch (InvocationTargetException e) {
            throw runtimify(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("you need a public setter method");
        }
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException("you cannot remove properties");
    }


    // Bulk Operations

    public void putAll(Map t) {
        Iterator i = t.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry)i.next();
            put(e.getKey(), e.getValue());
        }
    }

    public void clear() {
        Iterator i = keySet().iterator();
        while (i.hasNext())
            put(i.next(), null);
    }

    // Views

    public Set keySet() {
        return getters.keySet();
    }

    public Collection values() {
        Collection c = new ArrayList(getters.size());
        Iterator i = keySet().iterator();
        while (i.hasNext())
            c.add(get(i.next()));
        return c;
    }

    public Set entrySet() {
        Set s = new HashSet(getters.size());
        Iterator i = keySet().iterator();
        while (i.hasNext()) {
            Object key = i.next();
            s.add(new Entry(key, get(key)));
        }
        return s;
    }

    private static class Entry implements Map.Entry {

        private Object key;
        private Object value;

        private Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }

        public boolean equals(Object o) {
            if (! (o instanceof Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            return key == null ?
                e.getKey() == null : key.equals(e.getKey()) &&
                value == null ?
                    e.getValue() == null : value.equals(e.getValue());
        }

        public int hashCode() {
            return key == null ? 0 : key.hashCode() ^
                (value == null ? 0 : value.hashCode());
        }
    }

    // Comparison and hashing

    public boolean equals(Object o) {
        return o instanceof PropertyMap && ((PropertyMap)o).bean.equals(o);
    }

    public int hashCode() {
        return bean.hashCode();
    }

    private RuntimeException runtimify(final InvocationTargetException e) {
        if (e.getTargetException() instanceof RuntimeException)
            return (RuntimeException)e.getTargetException();
        return new RuntimeException() {
            public Throwable getTargetException() {
                return e;
            }
        };
    }
}