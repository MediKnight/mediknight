/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import de.baltic_online.borm.*;
import java.util.List;
import java.util.Iterator;
import java.sql.SQLException;

/**
 * @author sma@baltic-online.de
 */
public class RechnungsGruppe extends KnightObject
implements ObjectOwner {

    // Persistent attributes ------------------------------------------------

    static {
        ObjectMapper om = new ObjectMapper(RechnungsGruppe.class, "rechnungsgruppe");
        om.add(new AttributeMapper("abk", "abk", true, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("text", "text", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("object", "object", false, AttributeAccess.METHOD, AttributeType.STRING));
        Datastore.current.register(om);
    }

    private String abk;
    private String text;
    private String object;
    private Object id;

    public RechnungsGruppe() {
        abk = null;
    }

    public void setAbk(String abk) {
        this.abk = abk;
    }

    public String getAbk() {
        return abk;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getObject() {
        return object;
    }

    // Retrieval ------------------------------------------------------------

    public static List<RechnungsGruppe> retrieve() throws SQLException {
        Query q = Datastore.current.getQuery(RechnungsGruppe.class);
        List<RechnungsGruppe> l = toList(q.execute());
        for ( Iterator<RechnungsGruppe> it=l.iterator(); it.hasNext(); )
            it.next().setIdentity();
        return l;
    }

    // Framework ------------------------------------------------------------

    public boolean hasIdentity() {
        return id != null;
    }

    public void setIdentity() {
        id = abk;
    }

    public String toString() {
        return abk;
    }

    public String toLongString() {
        return
            "Rechnungsbaustein "+
            " Abk. "+getAbk()+
            " Text "+getText()+
            " Objekt "+getObject();
    }
}
