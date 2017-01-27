/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.domain;

import java.sql.SQLException;
import java.util.List;

import de.baltic_online.borm.AttributeAccess;
import de.baltic_online.borm.AttributeMapper;
import de.baltic_online.borm.AttributeType;
import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.ObjectMapper;
import de.baltic_online.borm.Query;


/**
 * @author sma@baltic-online.de
 */
public class VerordnungsPosten extends KnightObject implements Comparable< VerordnungsPosten > {

    // Persistent attributes ------------------------------------------------

    static {
	final ObjectMapper om = new ObjectMapper( VerordnungsPosten.class, "verordnungsposten" );
	om.add( new AttributeMapper( "gruppe", "gruppe", true, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "nummer", "nummer", true, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "name", "name", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "text", "text", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	Datastore.current.register( om );
    }


    public static List< KnightObject > retrieve() throws SQLException {
	final Query q = Datastore.current.getQuery( VerordnungsPosten.class );
	final List< KnightObject > list = toList( q.execute() );
	for( final KnightObject knightObject : list ) {
	    knightObject.setIdentity();
	}
	return list;
    }

    private int	    gruppe;
    private int	    nummer;
    private String  name;

    private String  text;

    private boolean hid	= false;


    @Override
    public int compareTo( final VerordnungsPosten o ) {
	if( o == null ) {
	    return 1;
	}

	final VerordnungsPosten v = o;
	final int cmp = gruppe - v.gruppe;
	return cmp == 0 ? nummer - v.nummer : cmp;
    }


    public int getGruppe() {
	return gruppe;
    }


    public String getName() {
	return name;
    }


    public int getNummer() {
	return nummer;
    }


    public String getText() {
	return text;
    }


    @Override
    public boolean hasIdentity() {
	return hid;
    }


    public void setGruppe( final int gruppe ) {
	this.gruppe = gruppe;
    }


    // Retrieval ------------------------------------------------------------

    @Override
    public void setIdentity() {
	hid = true;
    }


    // Framework ------------------------------------------------------------

    public void setName( final String name ) {
	this.name = name;
    }


    public void setNummer( final int nummer ) {
	this.nummer = nummer;
    }


    // Comparable -----------------------------------------------------------

    public void setText( final String text ) {
	this.text = text;
    }
}
