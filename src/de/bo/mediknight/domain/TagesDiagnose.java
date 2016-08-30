/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import de.baltic_online.borm.AttributeAccess;
import de.baltic_online.borm.AttributeMapper;
import de.baltic_online.borm.AttributeType;
import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.ObjectMapper;
import de.baltic_online.borm.Query;
import de.baltic_online.borm.Storable;


/**
 * This class implements a <i>Tagesdiagnose</i> with all its attributes. It has an identity and links back to a <i>Patient</i> and owns zero or one
 * <i>Rechnung</i>.
 *
 * @author sma@baltic-online.de
 */
public class TagesDiagnose extends KnightObject implements Comparable< TagesDiagnose > {

    // Persistent attributes ------------------------------------------------

    static {
	final ObjectMapper om = new ObjectMapper( TagesDiagnose.class, "tagesdiagnose" );
	om.add( new AttributeMapper( "id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "patientId", "patient_id", true, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "datum", "datum", false, AttributeAccess.METHOD, AttributeType.DATE ) );
	om.add( new AttributeMapper( "text", "text", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	Datastore.current.register( om );
    }

    private int		  id;
    private int		  patientId;
    private Date		 datum;
    private String	       text;

    private transient Patient    patient;

    private transient Rechnung   rechnung;

    private transient Verordnung verordnung;


    @Override
    public int compareTo( final TagesDiagnose o ) {
	if( o == null ) {
	    return 1;
	}
	return DateTools.onlyDateCompare( datum, o.datum );
    }


    @Override
    public void delete() throws SQLException {
	getRechnung();
	getVerordnung();
	if( rechnung != null ) {
	    rechnung.delete();
	}
	if( verordnung != null ) {
	    verordnung.delete();
	}
	super.delete();
    }


    public boolean equals( final TagesDiagnose o ) {
	/**
	 * @todo: Now we use the Comparable implementation for equals.
	 * @todo: Normally we test equality by comparing the id, improve!
	 */
	return compareTo( o ) == 0;
    }


    public Date getDatum() {
	return datum;
    }


    public int getId() {
	return id;
    }


    // Transient attributes -------------------------------------------------

    public Patient getPatient() {
	return patient;
    }


    public int getPatientId() {
	return patientId;
    }


    public Rechnung getRechnung() throws SQLException {
	if( rechnung == null ) {
	    retrieveRechnung();
	}
	return rechnung;
    }


    public String getText() {
	return text;
    }


    public Verordnung getVerordnung() throws SQLException {
	if( verordnung == null ) {
	    retrieveVerordnung();
	}
	return verordnung;
    }


    @Override
    protected boolean hasIdentity() {
	return id != 0;
    }


    private void retrieveRechnung() throws SQLException {
	final Query q = Datastore.current.getQuery( Rechnung.class, "diagnose_id=" + id );
	final List< Rechnung > list = toList( q.execute() );
	final Iterator< Rechnung > it = list.iterator();
	if( it.hasNext() ) {
	    rechnung = it.next();
	    rechnung.setDiagnose( this );
	} else {
	    setRechnung( new Rechnung() );
	}
    }


    private void retrieveVerordnung() throws SQLException {
	final Query q = Datastore.current.getQuery( Verordnung.class, "diagnose_id=" + id );
	final Iterator< Storable > it = q.execute();
	if( it.hasNext() ) {
	    verordnung = (Verordnung) it.next();
	    verordnung.setDiagnose( this );
	} else {
	    setVerordnung( new Verordnung() );
	}
    }


    public void setDatum( final Date datum ) {
	this.datum = datum;
    }


    public void setId( final int id ) {
	this.id = id;
    }


    @Override
    protected void setIdentity() throws SQLException {
	id = getLastId();
    }


    // Framework ------------------------------------------------------------

    public void setPatient( final Patient patient ) {
	patientId = patient.getId();
	this.patient = patient;
    }


    public void setPatientId( final int id ) {
	patientId = id;
    }


    void setRechnung( final Rechnung r ) throws SQLException {
	r.setDiagnose( this );
	r.save();
	rechnung = r;
    }


    // Testing --------------------------------------------------------------

    public void setText( final String text ) {
	this.text = text;
    }


    void setVerordnung( final Verordnung r ) throws SQLException {
	r.setDiagnose( this );
	r.save();
	verordnung = r;
    }


    @Override
    public String toLongString() {
	try {
	    return "Tagesdiagnose " + " ID " + getId() + " PatientenId: " + getPatientId() + " Datum: " + getDatum() + " Text: " + getText() + " Rechnung: "
		    + getRechnung() + " Verordnung: " + getVerordnung();
	} catch( final Exception x ) {
	    return "Unprintable TagesDiagnose";
	}
    }


    @Override
    public String toString() {
	return getText();
    }
}
