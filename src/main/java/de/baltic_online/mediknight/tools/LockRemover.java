package main.java.de.baltic_online.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;

import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.Query;
import de.baltic_online.borm.Storable;
import main.java.de.baltic_online.mediknight.MediKnight;
import main.java.de.baltic_online.mediknight.MediknightTheme;
import main.java.de.baltic_online.mediknight.domain.Lock;
import main.java.de.baltic_online.mediknight.domain.Patient;
import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;


class LockEntry {

    Lock   lock;
    String name;
    String surname;
    String description;


    LockEntry( final Lock lock, final String name, final String surname, final String description ) {
	this.lock = lock;
	this.name = name;
	this.surname = surname;
	this.description = description;
    }


    @Override
    public String toString() {
	String s = name;
	if( surname != null && surname.length() > 0 ) {
	    s += ", " + surname;
	}

	s += ": " + description;

	return s;
    }
}


public class LockRemover extends JFrame {

    private static final long serialVersionUID = 1L;
    static Properties	      properties;


    public static void initDB() throws Exception {
	final String driverName = properties.getProperty( "jdbc.driver.name" );
	Class.forName( driverName ).newInstance();

	final String jdbcURL = properties.getProperty( "jdbc.url.name" );

	System.out.println( "Connecting to " + jdbcURL );

	final String user = properties.getProperty( "jdbc.db.user" );
	final String passwd = properties.getProperty( "jdbc.db.passwd" );
	Datastore.current.connect( jdbcURL, user, passwd );
	registerMappers();
    }


    @SuppressWarnings( "resource" )
    public static Properties initProperties() throws IOException {
	InputStream is = null;

	try {
	    is = new FileInputStream( new File( MediKnight.MEDIKNIGHT_PROPERTIES ) );
	} catch( final FileNotFoundException e ) {
	    is = MediKnight.class.getClassLoader().getResourceAsStream( MediKnight.PROPERTY_FILENAME );
	}

	final Properties properties = new Properties();
	properties.load( is );
	is.close();

	return properties;
    }


    public static void main( final String[] args ) throws Exception {
	properties = initProperties();
	MediknightTheme.install( properties );
	UIManager.setLookAndFeel( new Plastic3DLookAndFeel() );

	final LockRemover frame = new LockRemover();
	frame.setSize( 500, 300 );
	frame.setVisible( true );
    }


    private static void registerMappers() {
	// Wow, now that's ugly! It seems like with newer JDK releases,
	// static class initializers are not called anymore only by
	// mentioning the class alone. Therefore we have to create
	// dummy instances of the persistent classes.
	// Consider this a bad hack!
	new Lock();
	new TagesDiagnose();
    }

    JPanel	       mainPanel      = new JPanel();
    GridBagLayout      gridBagLayout1 = new GridBagLayout();
    JRadioButton       selectiveRB    = new JRadioButton();
    JScrollPane	       listSP	      = new JScrollPane();
    JList< LockEntry > lockList	      = new JList< LockEntry >();
    JRadioButton       everythingRB   = new JRadioButton();
    Border	       border1;
    JPanel	       buttonPanel    = new JPanel();

    JButton	       quitButton     = new JButton();
    JButton	       commitButton   = new JButton();

    JButton	       updateButton   = new JButton();

    GridLayout	       gridLayout1    = new GridLayout();

    ButtonGroup	       group1	      = new ButtonGroup();

    List< LockEntry >  locks	      = new ArrayList< LockEntry >();


    public LockRemover() throws Exception {
	super( "Sperrungen aufheben" );
	setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	initDB();
	jbInit();
	initUI();
	connectUI();
	updateEnablement();
    }


    void commit() {
	List< LockEntry > entries = null;

	if( selectiveRB.isSelected() ) {
	    entries = lockList.getSelectedValuesList();
	} else {
	    entries = locks;
	}

	if( confirmDeletion( entries ) ) {
	    for( int i = 0; i < entries.size(); i++ ) {
		try {
		    entries.get( i ).lock.setIdentity();
		    entries.get( i ).lock.delete();
		} catch( final SQLException e ) {
		    e.printStackTrace();
		}
	    }

	    updateList();
	}
    }


    boolean confirmDeletion( final List< LockEntry > entries ) {
	String message = "Sollen die folgenden Sperren aufgehoben werden?\n";
	final String[] options = new String[] { "OK", "Abbrechen" };

	for( int i = 0; i < entries.size(); i++ ) {
	    message += "    " + entries.get( i ) + "\n";
	}

	final int option = JOptionPane.showOptionDialog( this, message, "Aufhebung bestätigen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
		null, options, options[1] );
	return option == 0;
    }


    void connectUI() {
	commitButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		commit();
	    }
	} );

	quitButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		exit();
	    }
	} );

	updateButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		updateList();
	    }
	} );

	selectiveRB.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		updateEnablement();
	    }
	} );

	everythingRB.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		updateEnablement();
	    }
	} );

	lockList.addListSelectionListener( new ListSelectionListener() {

	    @Override
	    public void valueChanged( final ListSelectionEvent e ) {
		updateEnablement();
	    }
	} );
    }


    void exit() {
	System.exit( 0 );
    }


    void initUI() throws SQLException {
	updateList();
	selectiveRB.setSelected( true );
    }


    private void jbInit() {
	border1 = BorderFactory.createEmptyBorder( 5, 10, 10, 10 );
	mainPanel.setLayout( gridBagLayout1 );
	selectiveRB.setText( "Einzelne Sperrungen aufheben" );
	everythingRB.setText( "Alle Sperrungen aufheben" );
	mainPanel.setBorder( border1 );
	quitButton.setText( "Beenden" );
	commitButton.setText( "Aufheben" );
	buttonPanel.setLayout( gridLayout1 );
	gridLayout1.setHgap( 5 );
	updateButton.setText( "Aktualisieren" );
	this.getContentPane().add( mainPanel, BorderLayout.CENTER );
	mainPanel.add( selectiveRB,
		new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	mainPanel.add( listSP,
		new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 17, 0, 0 ), 0, 0 ) );
	mainPanel.add( everythingRB,
		new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	mainPanel.add( buttonPanel,
		new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	buttonPanel.add( commitButton, null );
	buttonPanel.add( updateButton, null );
	buttonPanel.add( quitButton, null );
	listSP.getViewport().add( lockList, null );
	group1.add( selectiveRB );
	group1.add( everythingRB );
    }


    List< LockEntry > retrieveActiveLocks() throws SQLException {
	final SimpleDateFormat df = new SimpleDateFormat( "dd.MM.yyyy" );
	final List< LockEntry > results = new ArrayList< LockEntry >();
	final Query query = Datastore.current.getQuery( Lock.class );
	final Iterator< Storable > it = query.execute();
	while( it.hasNext() ) {
	    String description = "Stammdaten";
	    final Lock lock = (Lock) it.next();
	    final Patient patient = Patient.retrieve( lock.getPatientId() );

	    if( lock.getAspect() != null && lock.getAspect().length() > 0 ) {
		final Query tdQuery = Datastore.current.getQuery( TagesDiagnose.class, "id = ?" );
		tdQuery.bind( 1, Integer.valueOf( lock.getAspect() ) );
		final Iterator< Storable > tdIterator = tdQuery.execute();
		if( tdIterator.hasNext() ) {
		    final TagesDiagnose diagnose = (TagesDiagnose) tdIterator.next();
		    description = "Tagesdiagnose vom " + df.format( diagnose.getDatumAsDate() );
		} else {
		    description = "Gelöschte Tagesdiagnose";
		}
	    }

	    final LockEntry entry = new LockEntry( lock, patient.getName(), patient.getVorname(), description );
	    results.add( entry );
	}

	return results;
    }


    void updateEnablement() {
	commitButton.setEnabled( everythingRB.isSelected() || lockList.getSelectedValuesList() != null && lockList.getSelectedValuesList().size() > 0 );
	lockList.setEnabled( selectiveRB.isSelected() );
    }


    void updateList() {
	try {
	    locks = retrieveActiveLocks();
	    lockList.setListData( locks.toArray( new LockEntry[0] ) );
	} catch( final SQLException e ) {
	    e.printStackTrace();
	}
    }
}