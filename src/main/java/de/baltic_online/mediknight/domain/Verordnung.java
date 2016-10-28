/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.domain;

import de.baltic_online.borm.AttributeAccess;
import de.baltic_online.borm.AttributeMapper;
import de.baltic_online.borm.AttributeType;
import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.ObjectMapper;


/**
 * This class implement a <i>Verordnung</i> which stores one serialized recept object. It has a link back to one <i>TagesDiagnose</i>.
 *
 * @author sma@baltic-online.de
 */
public class Verordnung extends DiagnoseElement {

    // Persistent attributes ------------------------------------------------

    static {
	final ObjectMapper om = new ObjectMapper( Verordnung.class, "verordnung" );
	om.add( new AttributeMapper( "id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "diagnoseId", "diagnose_id", false, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "datumAsSqlDate", "datum", false, AttributeAccess.METHOD, AttributeType.DATE ) );
	om.add( new AttributeMapper( "object", "object", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	Datastore.current.register( om );
    }


    @Override
    public String toString() {
	return "Verordnung: " + super.toString();
    }
}