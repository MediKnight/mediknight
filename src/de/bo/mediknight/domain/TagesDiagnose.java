/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import de.baltic_online.borm.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

/**
 * This class implements a <i>Tagesdiagnose</i> with all its attributes.
 * It has an identity and links back to a <i>Patient</i> and owns zero or
 * one <i>Rechnung</i>.
 *
 * @author sma@baltic-online.de
 */
public class TagesDiagnose extends KnightObject
implements Comparable<TagesDiagnose> {

    // Persistent attributes ------------------------------------------------

    static {
        ObjectMapper om = new ObjectMapper(TagesDiagnose.class, "tagesdiagnose");
        om.add(new AttributeMapper("id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("patientId", "patient_id", true, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("datum", "datum", false, AttributeAccess.METHOD, AttributeType.DATE));
        om.add(new AttributeMapper("text", "text", false, AttributeAccess.METHOD, AttributeType.STRING));
        Datastore.current.register(om);
    }

    private int id;
    private int patientId;
    private Date datum;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int id) {
        patientId = id;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Date getDatum() {
        return datum;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    // Transient attributes -------------------------------------------------

    private transient Patient patient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patientId = patient.getId();
        this.patient = patient;
    }

    private transient Rechnung rechnung;

    void setRechnung(Rechnung r) throws SQLException {
        r.setDiagnose(this);
        r.save();
        rechnung = r;
    }

    public Rechnung getRechnung() throws SQLException {
        if (rechnung == null)
            retrieveRechnung();
        return rechnung;
    }

    private void retrieveRechnung() throws SQLException {
        Query q = Datastore.current.getQuery(Rechnung.class, "diagnose_id="+id);
        List<Rechnung> list = toList(q.execute());
        Iterator<Rechnung> it = list.iterator();
        if ( it.hasNext() ) {
            rechnung = it.next();
            rechnung.setDiagnose(this);
        }
        else
            setRechnung(new Rechnung());
    }

    private transient Verordnung verordnung;

    void setVerordnung(Verordnung r) throws SQLException {
        r.setDiagnose(this);
        r.save();
        verordnung = r;
    }

    public Verordnung getVerordnung() throws SQLException {
        if (verordnung == null)
            retrieveVerordnung();
        return verordnung;
    }

    private void retrieveVerordnung() throws SQLException {
        Query q = Datastore.current.getQuery(Verordnung.class, "diagnose_id="+id);
        Iterator<Storable> it = q.execute();
        if ( it.hasNext() ) {
            verordnung = (Verordnung)it.next();
            verordnung.setDiagnose(this);
        }
        else
            setVerordnung(new Verordnung());
    }

    // Framework ------------------------------------------------------------

    protected boolean hasIdentity() {
        return id != 0;
    }

    protected void setIdentity() throws SQLException {
        id = getLastId();
    }

    public void delete() throws SQLException {
        getRechnung();
        getVerordnung();
        if ( rechnung != null )
            rechnung.delete();
        if ( verordnung != null )
            verordnung.delete();
        super.delete();
    }

    // Testing --------------------------------------------------------------

    public String toString() {
        return getText();
    }

    public String toLongString() {
        try {
            return
                "Tagesdiagnose "+
                " ID "+getId()+
                " PatientenId: "+getPatientId()+
                " Datum: " + getDatum()+
                " Text: "+ getText()+
                " Rechnung: "+ getRechnung()+
                " Verordnung: "+ getVerordnung();
        }
        catch (Exception x) {
            return "Unprintable TagesDiagnose";
        }
    }

    public int compareTo(TagesDiagnose o) {
        if ( o == null )
            return 1;
        return DateTools.onlyDateCompare(datum, o.datum);
    }

    public boolean equals(TagesDiagnose o) {
        /**
         * @todo: Now we use the Comparable implementation for equals.
         * @todo: Normally we test equality by comparing the id, improve!
         */
        return compareTo(o) == 0;
    }
}
