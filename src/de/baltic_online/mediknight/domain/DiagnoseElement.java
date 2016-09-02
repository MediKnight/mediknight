/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.domain;

import java.sql.Date;
import java.sql.SQLException;


public class DiagnoseElement extends KnightObject implements ObjectOwner {

    protected int		   id;
    protected int		   diagnoseId;
    protected Date		  datum;
    protected String		object;

    private transient TagesDiagnose diagnose;


    public Date getDatum() {
	return datum;
    }


    public TagesDiagnose getDiagnose() {
	return diagnose;
    }


    public int getDiagnoseId() {
	return diagnoseId;
    }


    public int getId() {
	return id;
    }


    @Override
    public String getObject() {
	return object;
    }


    public Patient getPatient() {
	return diagnose.getPatient();
    }


    @Override
    protected boolean hasIdentity() {
	return id != 0;
    }


    // Transient attributes -------------------------------------------------

    public void setDatum( final Date _datum ) {
	datum = _datum;
    }


    public void setDiagnose( final TagesDiagnose td ) {
	datum = td.getDatum();
	diagnoseId = td.getId();
	diagnose = td;
    }


    public void setDiagnoseId( final int id ) {
	diagnoseId = id;
    }


    public void setId( final int id ) {
	this.id = id;
    }


    // Framework ------------------------------------------------------------

    @Override
    protected void setIdentity() throws SQLException {
	id = getLastId();
    }


    @Override
    public void setObject( final String _object ) {
	object = _object;
    }


    @Override
    public String toString() {
	return "ID " + getId() + " DiagnoseID " + getDiagnoseId() + " Datum " + getDatum() + " Objekt " + getObject();
    }
}
