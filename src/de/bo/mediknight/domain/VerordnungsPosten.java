/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import de.bo.mediknight.borm.*;
import java.util.*;
import java.sql.SQLException;

/**
 * @author sma@baltic-online.de
 */
public class VerordnungsPosten extends KnightObject implements Comparable {

    // Persistent attributes ------------------------------------------------

    static {
        ObjectMapper om = new ObjectMapper(VerordnungsPosten.class, "verordnungsposten");
        om.add(new AttributeMapper("gruppe", "gruppe", true, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("nummer", "nummer", true, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("name", "name", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("text", "text", false, AttributeAccess.METHOD, AttributeType.STRING));
        Datastore.current.register(om);
    }

    private int gruppe;
    private int nummer;
    private String name;
    private String text;

    private boolean hid = false;

    public void setGruppe(int gruppe) {
        this.gruppe = gruppe;
    }

    public int getGruppe() {
        return gruppe;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public int getNummer() {
        return nummer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    // Retrieval ------------------------------------------------------------

    public static List retrieve() throws SQLException {
        Query q = Datastore.current.getQuery(VerordnungsPosten.class);
        List list = toList(q.execute());
        for ( Iterator i = list.iterator(); i.hasNext(); ) {
            ((KnightObject)i.next()).setIdentity();
        }
        return list;
    }

    // Framework ------------------------------------------------------------

    public boolean hasIdentity() {
        return hid;
    }

    public void setIdentity() {
        hid = true;
    }

    // Comparable -----------------------------------------------------------

    public int compareTo(Object o) {
        if ( o == null )
            return 1;

        VerordnungsPosten v = (VerordnungsPosten)o;
        int cmp = gruppe - v.gruppe;
        return (cmp == 0) ? nummer - v.nummer : cmp;
    }
}
