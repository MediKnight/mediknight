/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.domain;

import de.bo.mediknight.borm.*;
import java.util.*;
import java.sql.Date;
import java.sql.SQLException;

/**
 * The main class of the mediknight hierarchy. Most other classes are in relation
 * (directly or indirectly) to this class. Instances of this class resemble a
 * <i>patient</i> with all his attributes.
 *
 * @author mr@baltic-online.de
 * @author sma@baltic-online.de
 */
public class Patient extends KnightObject
implements Comparable {

    // Persistent attributes ------------------------------------------------

    static {
        ObjectMapper om = new ObjectMapper(Patient.class, "patient");
        om.add(new AttributeMapper("id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER));
        om.add(new AttributeMapper("name", "name", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("vorname", "vorname", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("titel", "titel", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("anrede", "anrede", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("adresse1", "adresse1", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("adresse2", "adresse2", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("adresse3", "adresse3", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("telefonPrivat", "telefonprivat", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("telefonArbeit", "telefonarbeit", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("fax", "fax", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("handy", "handy", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("email", "email", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("bemerkung", "bemerkung", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("achtung", "achtung", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("geburtsDatum", "geburtsdatum", false, AttributeAccess.METHOD, AttributeType.DATE));
        om.add(new AttributeMapper("erstDiagnoseDatum", "erstdiagnosedatum", false, AttributeAccess.METHOD, AttributeType.DATE));
        om.add(new AttributeMapper("erstDiagnose", "erstDiagnose", false, AttributeAccess.METHOD, AttributeType.STRING));
        om.add(new AttributeMapper("privatPatient", "privatpatient", false, AttributeAccess.METHOD, AttributeType.BOOLEAN));
        Datastore.current.register(om);
    }

    private int id;
    private String name;
    private String vorname;
    private String titel;
    private String anrede;
    private String adresse1;
    private String adresse2;
    private String adresse3;
    private String telefonPrivat;
    private String telefonArbeit;
    private String fax;
    private String handy;
    private String email;
    private String bemerkung;
    private String achtung;
    private java.sql.Date geburtsDatum;
    private java.sql.Date erstDiagnoseDatum;
    private String erstDiagnose;
    private boolean privatPatient;

    public Patient() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setName(String _name) {
        name = _name;
    }

    public String getName(){
        return notNull(name);
    }

    public void setVorname(String _vorname){
        vorname = _vorname;
    }

    public String getVorname(){
        return notNull(vorname);
    }

    public String getFullname() {
        return getVorname() + " " + getName() ;
    }

    public void setTitel(String _titel){
        titel = _titel;
    }

    public String getTitel(){
        return notNull(titel);
    }

    public void setAnrede(String _anrede){
        anrede = _anrede;
    }

    public String getAnrede(){
        return notNull(anrede);
    }

    public void setAdresse1(String _adresse1){
        adresse1 = _adresse1;
    }

    public String getAdresse1(){
        return notNull(adresse1);
    }

    public void setAdresse2(String _adresse2){
        adresse2 = _adresse2;
    }

    public String getAdresse2(){
        return notNull(adresse2);
    }

    public void setAdresse3(String _adresse3){
        adresse3 = _adresse3;
    }

    public String getAdresse3(){
        return notNull(adresse3);
    }

    public void setTelefonPrivat(String _tp){
        telefonPrivat = _tp;
    }

    public String getTelefonPrivat(){
        return notNull(telefonPrivat);
    }

    public void setTelefonArbeit(String _ta){
        telefonArbeit = _ta;
    }

    public String getTelefonArbeit(){
        return notNull(telefonArbeit);
    }

    public void setFax(String _fax){
        fax = _fax;
    }

    public String getFax(){
        return notNull(fax);
    }

    public void setEmail(String _email){
        email = _email;
    }

    public String getEmail(){
        return notNull(email);
    }

    public void setHandy(String _handy){
        handy = _handy;
    }

    public String getHandy(){
        return notNull(handy);
    }

    public void setBemerkung(String _bem){
        bemerkung = _bem;
    }

    public String getBemerkung(){
        return notNull(bemerkung);
    }

    public void setAchtung(String _acht){
        achtung = _acht;
    }

    public String getAchtung(){
        return notNull(achtung);
    }

    public void setGeburtsDatum(java.sql.Date _gb){
        geburtsDatum = _gb;
    }

    public java.sql.Date getGeburtsDatum(){
        return geburtsDatum;
    }

    public void setErstDiagnoseDatum(java.sql.Date _ed){
        erstDiagnoseDatum = _ed;
    }

    public java.sql.Date getErstDiagnoseDatum(){
        return erstDiagnoseDatum;
    }

    public void setErstDiagnose(String _ed){
        erstDiagnose = _ed;
    }

    public String getErstDiagnose(){
        return erstDiagnose;
    }

    public void setPrivatPatient(boolean b) {
        privatPatient = b;
    }

    public boolean isPrivatPatient() {
        return privatPatient;
    }

    // Transient attributes -------------------------------------------------

    public List getTagesDiagnosen() throws SQLException {
        Query q = Datastore.current.getQuery(TagesDiagnose.class,"patient_id="+id);
        List list = toList(q.execute());

        // set backlinks
        Iterator it = list.iterator();
        while ( it.hasNext() )
            ((TagesDiagnose)it.next()).setPatient(this);

        return list;
    }

    public void addTagesDiagnose(TagesDiagnose td) throws SQLException {
        td.setPatient(this);
        td.save();
    }

    /**
     * Adds an empty daily diagnosis with the current date to this patient.
     * <p>
     * If a previous diagnosis entry exists, bill and medication of
     * the previous diagnosis will be copied.
     */
    public void addTagesDiagnose() throws SQLException {
        TagesDiagnose td = new TagesDiagnose();
        td.setDatum(new java.sql.Date(new java.util.Date().getTime()));
        addTagesDiagnose(td);

        Object[] a = getTagesDiagnosen().toArray();
        Arrays.sort(a);

        int n = a.length;
        if ( n >= 2 ) {
            // Copies previous data ...
            TagesDiagnose prevDiagnosis = (TagesDiagnose)a[n-2];

            Verordnung prevMedication = prevDiagnosis.getVerordnung();
            Verordnung newMedication = new Verordnung();
            newMedication.setObject(prevMedication.getObject());
            td.setVerordnung(newMedication);

            Rechnung prevBill = prevDiagnosis.getRechnung();
            Rechnung newBill = new Rechnung();
            newBill.setObject(prevBill.getObject());
            td.setRechnung(newBill);
            td.save();
        }
    }

    public void removeTagesDiagnose(TagesDiagnose td) throws SQLException {
        td.delete();
    }

    /**
     * Deletes all empty daily diagnosis entries expect the
     * entry of today.
     * (shrink and expand together)
     */
    public void adjustTagesDiagnosen() throws SQLException {
        java.util.List list = getTagesDiagnosen();
        java.util.Date today = new java.util.Date();
        Iterator it = list.iterator();
        boolean needUpdate = true;
        while ( it.hasNext() ) {
            TagesDiagnose td = (TagesDiagnose)it.next();
            if ( DateTools.onlyDateCompare(today,td.getDatum())!=0 ) {
                String text = td.getText();
                if ( text == null || text.trim().length() == 0 )
                    td.delete();
            }
            else
                needUpdate = false;
        }
        if ( needUpdate )
            addTagesDiagnose();
    }

    public void expandTagesDiagnosen() throws SQLException {
        java.util.List list = getTagesDiagnosen();
        if ( list.size() == 0 )
            addTagesDiagnose();
        else {
            java.util.Date today = new java.util.Date();
            boolean needUpdate = true;
            for ( Iterator it=list.iterator(); it.hasNext(); ) {
                TagesDiagnose td = (TagesDiagnose)it.next();
                if ( DateTools.onlyDateCompare(today,td.getDatum())==0 ) {
                    needUpdate = false;
                    break;
                }
            }
            if ( needUpdate )
                addTagesDiagnose();
        }
    }

    /**
     * This method removes all empty diagnosis records owned by this
     * patient.
     */
    public void shrinkTagesDiagnosen() throws SQLException {
        Iterator it = getTagesDiagnosen().iterator();
        while ( it.hasNext() ) {
            TagesDiagnose td = (TagesDiagnose)it.next();
            String text = td.getText();
            if ( text == null || text.trim().length() == 0 )
                td.delete();
        }
    }

    public TagesDiagnose getLetzteTagesDiagnose() throws SQLException {
        Object[] a = getTagesDiagnosen().toArray();
        Arrays.sort(a);
        if ( a.length > 0 )
            return (TagesDiagnose)a[a.length-1];
        return null;
    }

    // Retrieval ------------------------------------------------------------

    public static List retrieve(String pattern) throws SQLException {
        pattern += "%";
        Query q = Datastore.current.getQuery(Patient.class, "name like ?");
        return toList(q.bind(1,pattern).execute());
    }

    public static Patient retrieve(int id) throws SQLException {
        Query q = Datastore.current.getQuery(Patient.class, "id = ?");
        List list = toList(q.bind(1,new Integer(id)).execute());
        int n = list.size();
        if ( n == 1 ) {
            return (Patient)list.get(0);
        }
        else if ( n == 0 ) {
            return null;
        }
        throw new Error("patient table corrupt!");
    }

    // Locking --------------------------------------------------------------

    /**
     * Acquire the lock of this patient.
     *
     * @return the lock if the lock could be acquired,
     * otherwise <tt>null</tt>
     */
    public Lock acquireLock(Lock.Aspect aspect) throws SQLException {
        return acquireLock(aspect,0);
    }

    /**
     * Acquire the lock of this patient.
     *
     * At most one lock per patient instance will be created.
     * The lock will be removed after a given time or when the patient object
     * will be recreated.
     *
     * @param timeout a timeout value. If <tt>timeout</tt> is greater than
     * <tt>0</tt> the lock will be removed within <tt>timeout</tt> seconds.
     * @return the lock if the lock could be acquired,
     * otherwise <tt>null</tt>
     */
    public Lock acquireLock(Lock.Aspect aspect,int timeout) throws SQLException {
        return Lock.acquireLock(getId(),aspect,timeout);
    }

    // Framework ------------------------------------------------------------

    protected boolean hasIdentity() {
        return id != 0;
    }

    protected void setIdentity() throws SQLException {
        id = getLastId();
    }

    public void delete() throws SQLException {
        Iterator it = getTagesDiagnosen().iterator();
        while ( it.hasNext() )
            ((TagesDiagnose)it.next()).delete();

        super.delete();
    }

    // Testing --------------------------------------------------------------

    public String toString() {
        return getName()+", "+getVorname();
    }

    public String toLongString() {
        return
            "Patient "+getId()+
            " "+getName()+", "+getVorname();
    }

    // Comparable

    public int compareTo(Object o) {
        return getName().compareTo(((Patient)o).getName());
    }

    public boolean equals(Object o) {
        if ( o == null || !(o instanceof Patient) )
            return false;

        Patient p = (Patient)o;
        if ( id == 0 || p.id == 0 )
            return false;

        return id == p.id;
    }
}
