/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import de.bo.mediknight.borm.*;


/**
 * This class implement a <i>Verordnung</i> which stores one serialized recept
 * object. It has a link back to one <i>TagesDiagnose</i>.
 *
 * @author sma@baltic-online.de
 */
public class Verordnung extends DiagnoseElement {

    // Persistent attributes ------------------------------------------------

    static {
        ObjectMapper om = new ObjectMapper(Verordnung.class, "verordnung");
        om.add(new AttributeMapper("id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("diagnoseId", "diagnose_id", false, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("datum", "datum", false, AttributeAccess.METHOD, AttributeType.DATE));
        om.add(new AttributeMapper("object", "object", false, AttributeAccess.METHOD, AttributeType.STRING));
        Datastore.current.register(om);
    }

    public String toString() {
        return "Verordnung: "+super.toString();
    }
}