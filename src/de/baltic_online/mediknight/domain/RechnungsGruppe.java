/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.domain;

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
public class RechnungsGruppe extends KnightObject implements ObjectOwner {

    // Persistent attributes ------------------------------------------------

    static {
	final ObjectMapper om = new ObjectMapper( RechnungsGruppe.class, "rechnungsgruppe" );
	om.add( new AttributeMapper( "abk", "abk", true, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "text", "text", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "object", "object", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	Datastore.current.register( om );
    }


    public static List< RechnungsGruppe > retrieve() throws SQLException {
	final Query q = Datastore.current.getQuery( RechnungsGruppe.class );
	final List< RechnungsGruppe > l = toList( q.execute() );
	for( final RechnungsGruppe rechnungsGruppe : l ) {
	    rechnungsGruppe.setIdentity();
	}
	return l;
    }

    private String abk;
    private String text;
    private String object;

    private Object id;


    public RechnungsGruppe() {
	abk = null;
    }


    public String getAbk() {
	return abk;
    }


    @Override
    public String getObject() {
	return object;
    }


    public String getText() {
	return text;
    }


    @Override
    public boolean hasIdentity() {
	return id != null;
    }


    public void setAbk( final String abk ) {
	this.abk = abk;
    }


    // Retrieval ------------------------------------------------------------

    @Override
    public void setIdentity() {
	id = abk;
    }


    // Framework ------------------------------------------------------------

    @Override
    public void setObject( final String object ) {
	this.object = object;
    }


    public void setText( final String text ) {
	this.text = text;
    }


    @Override
    public String toLongString() {
	return "Rechnungsbaustein " + " Abk. " + getAbk() + " Text " + getText() + " Objekt " + getObject();
    }


    @Override
    public String toString() {
	return abk;
    }
}
