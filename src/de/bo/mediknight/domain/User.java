package de.bo.mediknight.domain;

import de.bo.mediknight.borm.*;
import java.sql.SQLException;
import java.util.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class User extends KnightObject
{
    static {
        ObjectMapper om = new ObjectMapper(User.class, "benutzer");
        om.add(new AttributeMapper("id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("name", "name", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("password", "passwort", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("grants", "zugriff", false, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("imageData", "bild", false, AttributeAccess.METHOD, AttributeType.OBJECT));
        Datastore.current.register(om);
    }

    private static User admin = null;

    private int id;
    private String name;
    private String password;
    private int grants;
    private Object imageData;

    User(int id,String name) {
        this.id = id;
        this.name = name;
        grants = -1;
    }

    public User() {
        grants = -1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if ( name == null )
            throw new IllegalArgumentException();

        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGrants() {
        return grants;
    }

    public void setGrants(int grants) {
        this.grants = grants;
    }

    public Object getImageData() {
        return imageData;
    }

    public void setImageData(Object imageData) {
        this.imageData = imageData;
    }

    // Framework ------------------------------------------------------------

    protected void setIdentity() throws SQLException {
        id = getLastId();
    }

    protected boolean hasIdentity() throws SQLException {
        return id != 0;
    }

    public void delete() throws SQLException {
        if ( isAdmin() )
            throw new SQLException("Could not delete superuser!");

        Iterator i = UserProperty.retrieve(this).iterator();
        while ( i.hasNext() )
            ((UserProperty)i.next()).delete();

        super.delete();
    }

    public boolean equals(Object o) {
        return
            o != null &&
            o instanceof User &&
            id == ((User)o).id;
    }

    // Retrieval ------------------------------------------------------------

    public static List retrieve() throws SQLException {
        Query q = Datastore.current.getQuery(User.class);
        return toList(q.execute());
    }

    public static User getDefaultAdmin() throws SQLException {
        if ( admin == null ) {
            admin = new User();
            admin.name = "admin";
            admin.password = "";
            admin.save();
        }
        return admin;
    }

    // Other ----------------------------------------------------------------

    public boolean isAdmin() {
        return id == 1;
    }

    public Map retrieveInformation() throws SQLException {
        return UserProperty.retrieveUserInformation(this);
    }

    public void saveInformation(Map map) throws SQLException {
        UserProperty.saveUserInformation(this,map);
    }

    public String toString() {
        return name;
    }
}
