/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import de.bo.mediknight.borm.*;

/**
 * This class implements a <i>Rechnung</i> which stores one serialized check
 * object.  It has a link back to one <i>TagesDiagnose</i>.
 *
 * @author sma@baltic-online.de
 * @author sml@baltic-online.de
 */
public class Rechnung extends DiagnoseElement {

    // Persistent attributes ------------------------------------------------

    static {
        ObjectMapper om = new ObjectMapper(Rechnung.class, "rechnung");
        om.add(new AttributeMapper("id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("diagnoseId", "diagnose_id", false, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("datum", "datum", false, AttributeAccess.METHOD, AttributeType.DATE));
        om.add(new AttributeMapper("object", "object", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("text", "text", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("address", "adresse", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("greetings", "gruss", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("goae", "modus", false, AttributeAccess.METHOD, AttributeType.BOOLEAN));
        Datastore.current.register(om);
    }

    private String text;
    private String address;
    private String greetings;
    private boolean goae;

    public Rechnung() {
        text = address = greetings = null;
        goae = false;
    }

    public String toString() {
        return "Rechnung: "+super.toString();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGreetings() {
        return greetings;
    }

    public void setGreetings(String greetings) {
        this.greetings = greetings;
    }

    public boolean isGoae() {
        return goae;
    }

    public void setGoae(boolean goae) {
        this.goae = goae;
    }
}
