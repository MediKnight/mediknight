package de.bo.mediknight.domain;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.baltic_online.borm.AttributeAccess;
import de.baltic_online.borm.AttributeMapper;
import de.baltic_online.borm.AttributeType;
import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.ObjectMapper;
import de.baltic_online.borm.Query;
import de.baltic_online.borm.Storable;


public class UserProperty extends KnightObject {

    static {
	final ObjectMapper om = new ObjectMapper( UserProperty.class, "benutzerprofil" );
	om.add( new AttributeMapper( "id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "key", "bezeichner", true, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "value", "wert", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	Datastore.current.register( om );

    }

    public static final User ALL_USERS = new User( 0, "*" );


    static List< UserProperty > retrieve() throws SQLException {
	final Query q = Datastore.current.getQuery( UserProperty.class );
	final List< UserProperty > l = toList( q.execute() );
	for( final UserProperty userProperty : l ) {
	    final UserProperty p = userProperty;
	    p.setIdentity();
	}
	return l;
    }


    static List< UserProperty > retrieve( final User user ) throws SQLException {

	final Query q = Datastore.current.getQuery( UserProperty.class, "id = ?" );
	final List< UserProperty > list = new ArrayList< UserProperty >();
	final Iterator< Storable > it = q.bind( 1, user.getId() + "" ).execute();
	while( it.hasNext() ) {
	    final UserProperty prop = (UserProperty) it.next();
	    prop.setIdentity();
	    list.add( prop );
	}
	return list;
    }


    /**
     * Retrieves all user information as a Map of the specified user from DB.
     *
     * @param user
     *            The user (must not be <tt>null</tt>).
     */
    public static Map< String, String > retrieveUserInformation( final User user ) throws IllegalArgumentException, SQLException {

	if( user == null ) {
	    throw new IllegalArgumentException( "Invalid user" );
	}

	final Hashtable< String, String > table = new Hashtable< String, String >();
	final Query q = Datastore.current.getQuery( UserProperty.class, "id = ?" );
	final Iterator< Storable > it = q.bind( 1, user.getId() + "" ).execute();
	while( it.hasNext() ) {
	    final UserProperty prop = (UserProperty) it.next();
	    table.put( prop.getKey(), prop.getValue() );
	}
	return table;
    }


    /**
     * Saves a single user property.
     * <p>
     * This method deletes the property from DB if the given value is <tt>null</tt>.
     *
     * @param user
     *            the specified user
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public static void save( final User user, final String key, final String value ) throws SQLException {
	UserProperty up;
	final Query q = Datastore.current.getQuery( UserProperty.class, "id = ? and bezeichner = ?" );
	final List< UserProperty > l = toList( q.bind( 1, user.getId() + "" ).bind( 2, key ).execute() );
	if( l.size() > 0 ) {
	    up = l.get( 0 );
	    up.setIdentity();
	    if( value == null ) {
		up.delete();
	    } else {
		up.setValue( value );
		up.save();
	    }
	} else if( value != null ) {
	    up = new UserProperty();
	    up.setId( user.getId() );
	    up.setKey( key );
	    up.setValue( value );
	    up.save();
	}
    }


    /**
     * Saves all user information of the specified user.
     * <p>
     * For simplicity, all keys and values of the map should be strings.
     *
     * @param user
     *            the specified user
     * @param map
     *            the map of the user properties
     * @exception ClassCastException
     *                if there exists at least one key value pair which contents are no strings.
     */
    public static void saveUserInformation( final User user, final Map< String, String > map ) throws IllegalArgumentException, ClassCastException,
											      SQLException {

	final Iterator< String > i = map.keySet().iterator();
	while( i.hasNext() ) {
	    final String key = i.next();
	    final String value = map.get( key );
	    save( user, key, value );
	}
    }

    private int     id;

    private String  name;

    private String  key;

    private String  value;

    private boolean hid;


    public UserProperty() {
	hid = false;
    }


    public int getId() {
	return id;
    }


    public String getKey() {
	return key;
    }


    public String getName() {
	return name;
    }


    public String getValue() {
	return value;
    }


    @Override
    public boolean hasIdentity() {
	return hid;
    }


    public void setId( final int id ) {
	this.id = id;
    }


    @Override
    public void setIdentity() {
	hid = true;
    }


    public void setKey( final String key ) {
	this.key = key;
    }


    public void setName( final String name ) {
	this.name = name;
    }


    public void setValue( final String value ) {
	this.value = value;
    }
}
