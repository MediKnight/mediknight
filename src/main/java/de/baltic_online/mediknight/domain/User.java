package main.java.de.baltic_online.mediknight.domain;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.baltic_online.borm.AttributeAccess;
import de.baltic_online.borm.AttributeMapper;
import de.baltic_online.borm.AttributeType;
import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.ObjectMapper;
import de.baltic_online.borm.Query;


/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class User extends KnightObject {

    static {
	final ObjectMapper om = new ObjectMapper( User.class, "benutzer" );
	om.add( new AttributeMapper( "id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "name", "name", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "password", "passwort", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "grants", "zugriff", false, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "imageData", "bild", false, AttributeAccess.METHOD, AttributeType.OBJECT ) );
	Datastore.current.register( om );
    }

    private static User admin = null;


    public static User getDefaultAdmin() throws SQLException {
	if( admin == null ) {
	    admin = new User();
	    admin.name = "admin";
	    admin.password = "";
	    admin.save();
	}
	return admin;
    }


    public static List< User > retrieve() throws SQLException {
	final Query q = Datastore.current.getQuery( User.class );
	return toList( q.execute() );
    }

    private int    id;
    private String name;
    private String password;

    private int    grants;

    private Object imageData;


    public User() {
	grants = -1;
    }


    User( final int id, final String name ) {
	this.id = id;
	this.name = name;
	grants = -1;
    }


    @Override
    public void delete() throws SQLException {
	if( isAdmin() ) {
	    throw new SQLException( "Could not delete superuser!" );
	}

	final Iterator< UserProperty > i = UserProperty.retrieve( this ).iterator();
	while( i.hasNext() ) {
	    i.next().delete();
	}

	super.delete();
    }


    @Override
    public boolean equals( final Object o ) {
	return o != null && o instanceof User && id == ((User) o).id;
    }


    public int getGrants() {
	return grants;
    }


    public int getId() {
	return id;
    }


    public Object getImageData() {
	return imageData;
    }


    public String getName() {
	return name;
    }


    public String getPassword() {
	return password;
    }


    @Override
    protected boolean hasIdentity() throws SQLException {
	return id != 0;
    }


    // Framework ------------------------------------------------------------

    public boolean isAdmin() {
	return id == 1;
    }


    public Map< String, String > retrieveInformation() throws SQLException {
	return UserProperty.retrieveUserInformation( this );
    }


    public void saveInformation( final Map< String, String > map ) throws SQLException {
	UserProperty.saveUserInformation( this, map );
    }


    public void setGrants( final int grants ) {
	this.grants = grants;
    }


    // Retrieval ------------------------------------------------------------

    public void setId( final int id ) {
	this.id = id;
    }


    @Override
    protected void setIdentity() throws SQLException {
	id = getLastId();
    }


    // Other ----------------------------------------------------------------

    public void setImageData( final Object imageData ) {
	this.imageData = imageData;
    }


    public void setName( final String name ) {
	if( name == null ) {
	    throw new IllegalArgumentException();
	}

	this.name = name;
    }


    public void setPassword( final String password ) {
	this.password = password;
    }


    @Override
    public String toString() {
	return name;
    }
}
