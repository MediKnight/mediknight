/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

import de.baltic_online.borm.AttributeAccess;
import de.baltic_online.borm.AttributeMapper;
import de.baltic_online.borm.AttributeType;
import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.ObjectMapper;
import de.baltic_online.borm.Query;


/**
 * @author sma@baltic-online.de
 */
public class RechnungsPosten extends KnightObject implements Comparable< RechnungsPosten > {

    // Persistent attributes ------------------------------------------------

    static {
	final ObjectMapper om = new ObjectMapper( RechnungsPosten.class, "rechnungsposten" );
	om.add( new AttributeMapper( "gebueH", "gebueh", true, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "GOAE", "goae", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "text", "text", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "preis", "preis", false, AttributeAccess.METHOD, AttributeType.DOUBLE ) );
	Datastore.current.register( om );
    }


    public static List< KnightObject > retrieve() throws SQLException {
	final Query q = Datastore.current.getQuery( RechnungsPosten.class );
	final List< KnightObject > list = toList( q.execute() );
	for( final KnightObject knightObject : list ) {
	    knightObject.setIdentity();
	}
	return list;
    }

    private String	    gebueH = "";
    private String	    GOAE   = "";
    private String	    text   = "";

    private double	    preis  = 0.0;
    private transient int     gruppe;
    private transient String  nummer;
    private transient boolean euro;

    private transient boolean hid;


    public RechnungsPosten() {
	gruppe = -1;
	nummer = null;
	euro = false;
	hid = false;
    }


    @Override
    public int compareTo( final RechnungsPosten o ) {
	final RechnungsPosten r = o;
	// System.out.println("Compare "+this+" with "+r+" ...");
	int g1 = getGruppe();
	int g2 = r.getGruppe();
	if( g1 == 0 ) {
	    g1 = 1000;
	}
	if( g2 == 0 ) {
	    g2 = 1000;
	}
	// System.out.println("Compare "+g1+" with "+g2+" ...");
	if( g1 != g2 ) {
	    return g1 - g2;
	}

	final String n1 = getNummer();
	final String n2 = r.getNummer();
	try {
	    return Integer.parseInt( n1 ) - Integer.parseInt( n2 );
	} catch( final NumberFormatException x ) {
	    return n1.compareTo( n2 );
	}
    }


    public String getGebueH() {
	return gebueH;
    }


    public String getGOAE() {
	return GOAE;
    }


    public int getGruppe() {
	if( nummer == null ) {
	    parseGebueH();
	}
	return gruppe;
    }


    public String getNummer() {
	if( nummer == null ) {
	    parseGebueH();
	}
	return nummer;
    }


    public double getPreis() {
	return preis;
    }


    public String getText() {
	return text;
    }


    // Transient ------------------------------------------------------------

    @Override
    protected boolean hasIdentity() {
	return hid;
    }


    public boolean isEuro() {
	return euro;
    }


    private void parseGebueH() {
	gruppe = -1;
	nummer = "";

	try {
	    final StringTokenizer st = new StringTokenizer( gebueH, ".-" );
	    gruppe = Integer.parseInt( st.nextToken() );
	    nummer = st.nextToken();
	} catch( final RuntimeException x ) { // ignore
	}
    }


    public void setEuro( final boolean euro ) {
	this.euro = euro;
    }


    public void setGebueH( final String g ) {
	gebueH = g;
	parseGebueH();
    }


    //
    // Retrieval ------------------------------------------------------------

    public void setGOAE( final String s ) {
	GOAE = s;
    }


    // Framework ------------------------------------------------------------

    @Override
    protected void setIdentity() {
	hid = true;
    }


    public void setPreis( final double _preis ) {
	preis = _preis;
    }


    public void setText( final String _text ) {
	text = _text;
    }


    // Comparable -----------------------------------------------------------

    @Override
    public String toString() {
	return "Rechnungsposten " + gebueH;
    }
}
