/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import de.baltic_online.borm.*;
import java.util.*;
import java.sql.SQLException;

/**
 * @author sma@baltic-online.de
 */
public class RechnungsPosten extends KnightObject
implements Comparable<RechnungsPosten> {

    // Persistent attributes ------------------------------------------------

    static {
        ObjectMapper om = new ObjectMapper(RechnungsPosten.class, "rechnungsposten");
        om.add(new AttributeMapper("gebueH", "gebueh", true, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("GOAE", "goae", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("text", "text", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("preis", "preis", false, AttributeAccess.METHOD, AttributeType.DOUBLE));
        Datastore.current.register(om);
    }

    private String gebueH = "";
    private String GOAE = "";
    private String text = "";
    private double preis = 0.0;

    private transient int gruppe;
    private transient String nummer;
    private transient boolean euro;
    private transient boolean hid;

    public RechnungsPosten() {
        gruppe = -1;
        nummer = null;
        euro = false;
        hid = false;
    }

    public void setGebueH(String g) {
        gebueH = g;
        parseGebueH();
    }

    public String getGebueH() {
        return gebueH;
    }

    public void setGOAE(String s) {
        GOAE = s;
    }

    public String getGOAE() {
        return GOAE;
    }

    public void setText(String _text) {
        text = _text;
    }

    public String getText() {
        return text;
    }

    public void setPreis(double _preis) {
        preis = _preis;
    }

    public double getPreis() {
        return preis;
    }

    // Transient ------------------------------------------------------------

    public int getGruppe() {
        if ( nummer == null )
            parseGebueH();
        return gruppe;
    }

    public String getNummer() {
        if ( nummer == null )
            parseGebueH();
        return nummer;
    }

    private void parseGebueH() {
        gruppe = -1;
        nummer = "";

        try {
            StringTokenizer st = new StringTokenizer(gebueH,".-");
            gruppe = Integer.parseInt(st.nextToken());
            nummer = st.nextToken();
        }
        catch (RuntimeException x) { // ignore
        }
    }

    public boolean isEuro() {
        return euro;
    }

    public void setEuro(boolean euro) {
        this.euro = euro;
    }

    //
    // Retrieval ------------------------------------------------------------

    public static List<KnightObject> retrieve() throws SQLException {
        Query q = Datastore.current.getQuery(RechnungsPosten.class);
        List<KnightObject> list = toList(q.execute());
        for ( Iterator<KnightObject> i = list.iterator(); i.hasNext(); ) {
            i.next().setIdentity();
        }
        return list;
    }

    // Framework ------------------------------------------------------------

    protected boolean hasIdentity() {
        return hid;
    }

    protected void setIdentity() {
        hid = true;
    }

    public String toString() {
        return "Rechnungsposten "+gebueH;
    }

    // Comparable -----------------------------------------------------------

    public int compareTo(RechnungsPosten o) {
        RechnungsPosten r = o;
        // System.out.println("Compare "+this+" with "+r+" ...");
        int g1 = getGruppe();
        int g2 = r.getGruppe();
        if ( g1 == 0 ) g1 = 1000;
        if ( g2 == 0 ) g2 = 1000;
        // System.out.println("Compare "+g1+" with "+g2+" ...");
        if ( g1 != g2 ) return g1 - g2;

        String n1 = getNummer();
        String n2 = r.getNummer();
        try {
            return Integer.parseInt(n1) - Integer.parseInt(n2);
        }
        catch (NumberFormatException x) {
            return n1.compareTo(n2);
        }
    }
}
