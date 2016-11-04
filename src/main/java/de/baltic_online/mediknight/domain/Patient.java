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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
 * The main class of the mediknight hierarchy. Most other classes are in relation (directly or indirectly) to this class. Instances of this class resemble a
 * <i>patient </i> with all his attributes.
 *
 * @author mr@baltic-online.de
 * @author sma@baltic-online.de
 */
public class Patient extends KnightObject implements Comparable< Patient > {

    // Persistent attributes ------------------------------------------------

    static {
	final ObjectMapper om = new ObjectMapper( Patient.class, "patient" );
	om.add( new AttributeMapper( "id", "id", true, AttributeAccess.METHOD, AttributeType.INTEGER ) );
	om.add( new AttributeMapper( "name", "name", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "vorname", "vorname", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "titel", "titel", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "anrede", "anrede", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "adresse1", "adresse1", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "adresse2", "adresse2", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "adresse3", "adresse3", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "telefonPrivat", "telefonprivat", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "telefonArbeit", "telefonarbeit", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "fax", "fax", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "handy", "handy", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "email", "email", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "bemerkung", "bemerkung", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "achtung", "achtung", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "geburtsDatumAsSqlDate", "geburtsdatum", false, AttributeAccess.METHOD, AttributeType.DATE ) );
	om.add( new AttributeMapper( "erstDiagnoseDatumAsSqlDate", "erstdiagnosedatum", false, AttributeAccess.METHOD, AttributeType.DATE ) );
	om.add( new AttributeMapper( "erstDiagnose", "erstDiagnose", false, AttributeAccess.METHOD, AttributeType.STRING ) );
	om.add( new AttributeMapper( "privatPatient", "privatpatient", false, AttributeAccess.METHOD, AttributeType.BOOLEAN ) );
	Datastore.current.register( om );
    }


    public static Patient retrieve( final int id ) throws SQLException {
	final Query q = Datastore.current.getQuery( Patient.class, "id = ?" );
	final Iterator< Storable > it = q.bind( 1, new Integer( id ) ).execute();

	if( !it.hasNext() ) {
	    return null;
	}

	final Patient result = (Patient) it.next();

	if( it.hasNext() ) {
	    throw new Error( "patient table corrupt!" );
	} else {
	    return result;
	}
    }


    public static List< Patient > retrieve( String pattern ) throws SQLException {
	pattern += "%";
	final Query q = Datastore.current.getQuery( Patient.class, "name like ?" );
	return toList( q.bind( 1, pattern ).execute() );
    }

    private int	   id;

    private String	name;

    private String	vorname;

    private String	titel;

    private String	anrede;

    private String	adresse1;

    private String	adresse2;

    private String	adresse3;

    private String	telefonPrivat;

    private String	telefonArbeit;

    private String	fax;

    private String	handy;

    private String	email;

    private String	bemerkung;

    private String	achtung;

    private LocalDate geburtsDatum;

    private LocalDate erstDiagnoseDatum;

    private String	erstDiagnose;

    private boolean       privatPatient;


    public Patient() {
    }


    /**
     * Acquire the lock of this patient.
     *
     * @return the lock if the lock could be acquired, otherwise <tt>null</tt>
     */
    public Lock acquireLock( final Lock.Aspect aspect ) throws SQLException {
	return acquireLock( aspect, 0 );
    }


    /**
     * Acquire the lock of this patient.
     *
     * At most one lock per patient instance will be created. The lock will be removed after a given time or when the patient object will be recreated.
     *
     * @param timeout
     *            a timeout value. If <tt>timeout</tt> is greater than <tt>0</tt> the lock will be removed within <tt>timeout</tt> seconds.
     * @return the lock if the lock could be acquired, otherwise <tt>null</tt>
     */
    public Lock acquireLock( final Lock.Aspect aspect, final int timeout ) throws SQLException {
	return Lock.acquireLock( getId(), aspect, timeout );
    }


    /**
     * Adds an empty daily diagnosis with the current date to this patient.
     * <p>
     * If a previous diagnosis entry exists, bill and medication of the previous diagnosis will be copied.
     */
    public void addTagesDiagnose() throws SQLException {
	final TagesDiagnose td = new TagesDiagnose();
	td.setDatum( LocalDate.now() );
	addTagesDiagnose( td );

	final TagesDiagnose[] a = getTagesDiagnosen().toArray( new TagesDiagnose[0] );
	Arrays.sort( a );

	final int n = a.length;
	if( n >= 2 ) { // Copies previous data ...
	    final TagesDiagnose prevDiagnosis = a[n - 2];

	    final Verordnung prevMedication = prevDiagnosis.getVerordnung();
	    final Verordnung newMedication = new Verordnung();
	    newMedication.setObject( prevMedication.getObject() );
	    td.setVerordnung( newMedication );

	    final Rechnung prevBill = prevDiagnosis.getRechnung();
	    final Rechnung newBill = new Rechnung();
	    newBill.setObject( prevBill.getObject() );
	    td.setRechnung( newBill );
	    td.save();
	}
    }


    public void addTagesDiagnose( final TagesDiagnose td ) throws SQLException {
	td.setPatient( this );
	td.save();
    }


    /**
     * Deletes all empty daily diagnosis entries expect the entry of today. (shrink and expand together)
     */
    public void adjustTagesDiagnosen() throws SQLException {
	final List< TagesDiagnose > list = getTagesDiagnosen();
	final Date today = new java.util.Date();
	final Iterator< TagesDiagnose > it = list.iterator();
	boolean needUpdate = true;
	while( it.hasNext() ) {
	    final TagesDiagnose td = it.next();
	    if( DateTools.onlyDateCompare( today, td.getDatumAsDate() ) != 0 ) {
		final String text = td.getText();
		if( text == null || text.trim().length() == 0 ) {
		    td.delete();
		}
	    } else {
		needUpdate = false;
	    }
	}
	if( needUpdate ) {
	    addTagesDiagnose();
	}
    }


    @Override
    public int compareTo( final Patient o ) {
	return getName().compareTo( o.getName() );
    }


    @Override
    public void delete() throws SQLException {
	final Iterator< TagesDiagnose > it = getTagesDiagnosen().iterator();
	while( it.hasNext() ) {
	    it.next().delete();
	}

	super.delete();
    }


    @Override
    public boolean equals( final Object o ) {
	if( o == null || !(o instanceof Patient) ) {
	    return false;
	}

	final Patient p = (Patient) o;
	if( id == 0 || p.id == 0 ) {
	    return false;
	}

	return id == p.id;
    }


    public void expandTagesDiagnosen() throws SQLException {
	final List< TagesDiagnose > list = getTagesDiagnosen();
	if( list.size() == 0 ) {
	    addTagesDiagnose();
	} else {
	    final java.util.Date today = new java.util.Date();
	    boolean needUpdate = true;
	    for( final TagesDiagnose td : list ) {
		if( DateTools.onlyDateCompare( today, td.getDatumAsDate() ) == 0 ) {
		    needUpdate = false;
		    break;
		}
	    }
	    if( needUpdate ) {
		addTagesDiagnose();
	    }
	}
    }


    public String getAchtung() {
	return notNull( achtung );
    }


    public String getAdresse1() {
	return notNull( adresse1 );
    }


    public String getAdresse2() {
	return notNull( adresse2 );
    }


    public String getAdresse3() {
	return notNull( adresse3 );
    }


    public int getAge() {
	if( geburtsDatum == null ) {
	    return -1;
	} else {
	    final Calendar today = Calendar.getInstance();
	    final Calendar birthday = Calendar.getInstance();
	    birthday.setTime( getGeburtsDatumAsDate() );
	    final Calendar birthdayThisYear = (Calendar) birthday.clone();
	    birthdayThisYear.set( Calendar.YEAR, today.get( Calendar.YEAR ) );
	    int age = today.get( Calendar.YEAR ) - birthday.get( Calendar.YEAR );
	    if( birthdayThisYear.after( today ) ) {
		age--;
	    }
	    return age;
	}
    }


    public String getAnrede() {
	return notNull( anrede );
    }


    public String getBemerkung() {
	return notNull( bemerkung );
    }


    public String getEmail() {
	return notNull( email );
    }


    public String getErstDiagnose() {
	return erstDiagnose;
    }


    public LocalDate getErstDiagnoseDatum() {
	return erstDiagnoseDatum;
    }
    
    public java.sql.Date getErstDiagnoseDatumAsSqlDate() {
	return erstDiagnoseDatum != null ? java.sql.Date.valueOf( erstDiagnoseDatum ) : null;
    }
    
    public Date getErstDiagnoseDatumAsDate() {
	return erstDiagnoseDatum != null ? Date.from( erstDiagnoseDatum.atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() ) : null;
    }


    public String getFax() {
	return notNull( fax );
    }


    public String getFullname() {
	return getVorname() + " " + getName();
    }


    public LocalDate getGeburtsDatum() {
	return geburtsDatum;
    }
    
    public java.sql.Date getGeburtsDatumAsSqlDate() {
	return geburtsDatum != null ? java.sql.Date.valueOf( geburtsDatum ) : null;
    }
    
    public Date getGeburtsDatumAsDate() {
	return geburtsDatum != null ? Date.from( geburtsDatum.atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() ) : null;
    }

    public String getHandy() {
	return notNull( handy );
    }


    public int getId() {
	return id;
    }


    public TagesDiagnose getLetzteTagesDiagnose() throws SQLException {
	final Object[] a = getTagesDiagnosen().toArray();
	Arrays.sort( a );
	if( a.length > 0 ) {
	    return (TagesDiagnose) a[a.length - 1];
	}
	return null;
    }


    public String getName() {
	return notNull( name );
    }


    public List< TagesDiagnose > getTagesDiagnosen() throws SQLException {
	final Query q = Datastore.current.getQuery( TagesDiagnose.class, "patient_id=" + id );
	final List< TagesDiagnose > list = new ArrayList< TagesDiagnose >();

	// set backlinks
	final Iterator< Storable > it = q.execute();
	while( it.hasNext() ) {
	    final TagesDiagnose diag = (TagesDiagnose) it.next();
	    diag.setPatient( this );
	    list.add( diag );
	}

	return list;
    }


    public String getTelefonArbeit() {
	return notNull( telefonArbeit );
    }


    public String getTelefonPrivat() {
	return notNull( telefonPrivat );
    }


    public String getTitel() {
	return notNull( titel );
    }


    public String getVorname() {
	return notNull( vorname );
    }


    public boolean hasBirthday() {
	if( geburtsDatum == null ) {
	    return false;
	}

	final Calendar today = Calendar.getInstance();
	final Calendar birthday = Calendar.getInstance();
	birthday.setTime( getGeburtsDatumAsDate() );

	return today.get( Calendar.DAY_OF_MONTH ) == birthday.get( Calendar.DAY_OF_MONTH ) && today.get( Calendar.MONTH ) == birthday.get( Calendar.MONTH );
    }


    @Override
    protected boolean hasIdentity() {
	return id != 0;
    }


    public boolean isPrivatPatient() {
	return privatPatient;
    }


    public void removeTagesDiagnose( final TagesDiagnose td ) throws SQLException {
	td.delete();
    }


    public void setAchtung( final String _acht ) {
	achtung = _acht;
    }


    public void setAdresse1( final String _adresse1 ) {
	adresse1 = _adresse1;
    }


    public void setAdresse2( final String _adresse2 ) {
	adresse2 = _adresse2;
    }


    public void setAdresse3( final String _adresse3 ) {
	adresse3 = _adresse3;
    }


    // Transient attributes -------------------------------------------------

    public void setAnrede( final String _anrede ) {
	anrede = _anrede;
    }


    public void setBemerkung( final String _bem ) {
	bemerkung = _bem;
    }


    public void setEmail( final String _email ) {
	email = _email;
    }


    public void setErstDiagnose( final String _ed ) {
	erstDiagnose = _ed;
    }


    public void setErstDiagnoseDatum( final LocalDate ed ) {
	erstDiagnoseDatum = ed;
    }
    
    public void setErstDiagnoseDatumAsSqlDate( final java.sql.Date ed ) {
	erstDiagnoseDatum = ed != null ? ed.toLocalDate() : null;
    }
    
    public void setErstDiagnoseDatumAsDate( final Date ed ) {
	erstDiagnoseDatum = ed != null ? Instant.ofEpochMilli( ed.getTime() ).atZone( ZoneId.systemDefault() ).toLocalDate() : null;
    }


    public void setFax( final String _fax ) {
	fax = _fax;
    }


    public void setGeburtsDatum( final LocalDate gb ) {
	geburtsDatum = gb;
    }
    
    public void setGeburtsDatumAsSqlDate( final java.sql.Date gb ) {
	geburtsDatum = gb != null ? gb.toLocalDate() : null;
    }
    
    public void setGeburtsDatumAsDate( final Date gb ) {
	geburtsDatum = gb != null ? Instant.ofEpochMilli( gb.getTime() ).atZone( ZoneId.systemDefault() ).toLocalDate() : null;
    }


    public void setHandy( final String _handy ) {
	handy = _handy;
    }


    // Retrieval ------------------------------------------------------------

    public void setId( final int id ) {
	this.id = id;
    }


    @Override
    protected void setIdentity() throws SQLException {
	id = getLastId();
    }


    // Locking --------------------------------------------------------------

    public void setName( final String _name ) {
	name = _name;
    }


    public void setPrivatPatient( final boolean b ) {
	privatPatient = b;
    }


    // Framework ------------------------------------------------------------

    public void setTelefonArbeit( final String _ta ) {
	telefonArbeit = _ta;
    }


    public void setTelefonPrivat( final String _tp ) {
	telefonPrivat = _tp;
    }


    public void setTitel( final String _titel ) {
	titel = _titel;
    }


    // Testing --------------------------------------------------------------

    public void setVorname( final String _vorname ) {
	vorname = _vorname;
    }


    /**
     * This method removes all empty diagnosis records owned by this patient.
     */
    public void shrinkTagesDiagnosen() throws SQLException {
	final Iterator< TagesDiagnose > it = getTagesDiagnosen().iterator();
	while( it.hasNext() ) {
	    final TagesDiagnose td = it.next();
	    final String text = td.getText();
	    if( text == null || text.trim().length() == 0 ) {
		td.delete();
	    }
	}
    }


    // Comparable

    @Override
    public String toLongString() {
	return "Patient " + getId() + " " + getName() + ", " + getVorname();
    }


    @Override
    public String toString() {
	return getName() + ", " + getVorname();
    }
}