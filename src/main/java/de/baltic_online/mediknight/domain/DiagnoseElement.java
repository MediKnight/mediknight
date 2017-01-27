/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.domain;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;


public class DiagnoseElement extends KnightObject implements ObjectOwner {

    protected int		    id;
    protected int		    diagnoseId;
    protected LocalDate		    datum;
    protected String		    object;

    private transient TagesDiagnose diagnose;


    public LocalDate getDatum() {
	return datum;
    }


    public java.sql.Date getDatumAsSqlDate() {
	return datum != null ? java.sql.Date.valueOf( datum ) : null;
    }


    public Date getDatumAsDate() {
	return datum != null ? Date.from( datum.atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() ) : null;
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

    public void setDatum( final LocalDate datum ) {
	this.datum = datum;
    }


    public void setDatumAsSqlDate( final java.sql.Date datum ) {
	this.datum = datum != null ? datum.toLocalDate() : null;
    }


    public void setDatumAsDate( final Date datum ) {
	this.datum = datum != null ? Instant.ofEpochMilli( datum.getTime() ).atZone( ZoneId.systemDefault() ).toLocalDate() : null;
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
	final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate( FormatStyle.MEDIUM );

	return "ID " + getId() + " DiagnoseID " + getDiagnoseId() + " Datum " + getDatum().format( dateFormatter ) + " Objekt " + getObject();
    }
}
