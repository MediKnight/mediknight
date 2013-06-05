package de.bo.mediknight.domain;

import de.bo.borm.*;
import de.bo.mediknight.domain.KnightObject;
import java.sql.SQLException;
import java.util.*;

public class UserProperty extends KnightObject {

    static {
        ObjectMapper om = new ObjectMapper(UserProperty.class, "benutzerprofil");
        om.add(new AttributeMapper("id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("key", "bezeichner", true, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("value", "wert", false, AttributeAccess.METHOD, AttributeType.STRING));
        Datastore.current.register(om);

    }

    public static final User ALL_USERS = new User(0,"*");

    private int id;
    private String name;
    private String key;
    private String value;

    private boolean hid;

    public UserProperty() {
        hid = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setIdentity() {
        hid = true;
    }

    public boolean hasIdentity() {
        return hid;
    }

    static List<UserProperty> retrieve() throws SQLException {
        Query q = Datastore.current.getQuery(UserProperty.class);
        List<UserProperty> l = toList(q.execute());
        for ( Iterator<UserProperty> i=l.iterator(); i.hasNext(); ) {
            UserProperty p = (UserProperty)i.next();
            p.setIdentity();
        }
        return l;
    }

    static List<UserProperty> retrieve(User user)
        throws SQLException {

        Query q = Datastore.current.getQuery(UserProperty.class, "id = ?");
        List<UserProperty> list = new ArrayList<UserProperty>();
        Iterator<Object> it = q.bind(1,user.getId()+"").execute();
        while (it.hasNext()) {
            UserProperty prop = (UserProperty)it.next();
            prop.setIdentity();
            list.add(prop);
        }
        return list;
    }

    /**
     * Retrieves all user information as a Map of the specified user from DB.
     *
     * @param user The user (must not be <tt>null</tt>).
     */
    public static Map<String, String> retrieveUserInformation(User user)
        throws IllegalArgumentException, SQLException {

        if ( user == null )
            throw new IllegalArgumentException("Invalid user");

        Hashtable<String, String> table = new Hashtable<String, String>();
        Query q = Datastore.current.getQuery(UserProperty.class, "id = ?");
        Iterator<Object> it = q.bind(1,user.getId()+"").execute();
        while (it.hasNext()) {
            UserProperty prop = (UserProperty)it.next();
            table.put(prop.getKey(),prop.getValue());
        }
        return table;
    }

    /**
     * Saves all user information of the specified user.
     * <p>
     * For simplicity, all keys and values of the map should be strings.
     *
     * @param user the specified user
     * @param map the map of the user properties
     * @exception ClassCastException if there exists at least one key value
     * pair which contents are no strings.
     */
    public static void saveUserInformation(User user,Map<String, String> map)
        throws IllegalArgumentException, ClassCastException, SQLException {

        Iterator<String> i = map.keySet().iterator();
        while ( i.hasNext() ) {
            String key = i.next();
            String value = map.get(key);
            save(user,key,value);
        }
    }

    /**
     * Saves a single user property.
     * <p>
     * This method deletes the property from DB if the given value
     * is <tt>null</tt>.
     *
     * @param user the specified user
     * @param key the key
     * @param value the value
     */
    public static void save(User user,String key,String value)
        throws IllegalArgumentException, SQLException {

        if ( user == null )
            throw new IllegalArgumentException("Invalid user");

        UserProperty up;
        Query q = Datastore.current.getQuery(UserProperty.class,"id = ? and bezeichner = ?");
        List l = toList(q.bind(1,user.getId()+"").bind(2,key).execute());
        if ( l.size() > 0 ) {
            up = (UserProperty)l.get(0);
            up.setIdentity();
            if ( value == null ) {
                up.delete();
            }
            else {
                up.setValue(value);
                up.save();
            }
        }
        else if ( value != null ) {
            up = new UserProperty();
            up.setId(user.getId());
            up.setKey(key);
            up.setValue(value);
            up.save();
        }
    }
}
