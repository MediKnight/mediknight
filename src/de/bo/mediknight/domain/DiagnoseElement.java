/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import java.sql.Date;
import java.sql.SQLException;

public class DiagnoseElement extends KnightObject
implements ObjectOwner {

    protected int id;
    protected int diagnoseId;
    protected Date datum;
    protected String object;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDiagnoseId(int id) {
        diagnoseId = id;
    }

    public int getDiagnoseId() {
        return diagnoseId;
    }

    public void setDatum(Date _datum) {
        datum = _datum;
    }

    public Date getDatum() {
        return datum;
    }

    public void setObject(String _object) {
        object = _object;
    }

    public String getObject() {
        return object;
    }

    // Transient attributes -------------------------------------------------

    private transient TagesDiagnose diagnose;

    public void setDiagnose(TagesDiagnose td) {
        datum = td.getDatum();
        diagnoseId = td.getId();
        diagnose = td;
    }

    public TagesDiagnose getDiagnose() {
        return diagnose;
    }

    public Patient getPatient() {
        return diagnose.getPatient();
    }

    // Framework ------------------------------------------------------------

    protected boolean hasIdentity() {
        return id != 0;
    }

    protected void setIdentity() throws SQLException {
        id = getLastId();
    }

    public String toString() {
        return
            "ID "+getId()+
            " DiagnoseID "+getDiagnoseId()+
            " Datum "+getDatum()+
            " Objekt "+getObject();
    }
}
