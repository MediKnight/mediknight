package main.java.de.baltic_online.mediknight;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observer;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputAdapter;

import de.baltic_online.borm.Datastore;
import de.baltic_online.borm.ObjectMapper;
import de.baltic_online.borm.TraceConstants;
import de.baltic_online.borm.Tracer;
import main.java.de.baltic_online.mediknight.dialogs.DatabaseSelectionDialog;
import main.java.de.baltic_online.mediknight.domain.KnightObject;
import main.java.de.baltic_online.mediknight.domain.Lock;
import main.java.de.baltic_online.mediknight.domain.Patient;
import main.java.de.baltic_online.mediknight.domain.Rechnung;
import main.java.de.baltic_online.mediknight.domain.RechnungsPosten;
import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;
import main.java.de.baltic_online.mediknight.domain.User;
import main.java.de.baltic_online.mediknight.domain.UserProperty;
import main.java.de.baltic_online.mediknight.domain.Verordnung;
import main.java.de.baltic_online.mediknight.util.CurrencyNumber;
import main.java.de.baltic_online.mediknight.util.LogWriter;
import main.java.de.baltic_online.mediknight.util.MediknightUtilities;
import main.java.de.baltic_online.mediknight.widgets.FlexGridLayout;
import main.java.de.baltic_online.mediknight.widgets.JButton;
import main.java.de.baltic_online.mediknight.widgets.JGradientPanel;
import main.java.de.baltic_online.mediknight.widgets.JPanel;
import main.java.de.baltic_online.mediknight.widgets.LoginDialog;
import main.java.de.baltic_online.mediknight.widgets.Mutable;


public class MediKnight extends JFrame implements TraceConstants {

    public static class Datasource {

	public String description;

	public String driver;

	public String url;

	public String user;

	public String password;


	public Datasource( final String description, final String driver, final String url, final String user, final String password ) {
	    this.description = description;
	    this.driver = driver;
	    this.url = url;
	    this.user = user;
	    this.password = password;
	}


	@Override
	public String toString() {
	    return description;
	}
    }

    private class GrayedPane extends JComponent {

	private static final long      serialVersionUID	= -2324973092499775232L;

	private final Composite	       composite;

	private Image		       bufferImage;

	private final AWTEventListener eventListener;


	public GrayedPane() {
	    super();

	    composite = AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.7f );
	    bufferImage = null;

	    final MouseInputAdapter mia = new MyMouseInputAdapter( this );
	    addMouseListener( mia );
	    addMouseMotionListener( mia );

	    eventListener = new AWTEventListener() {

		@Override
		public void eventDispatched( final AWTEvent e ) {
		    if( e instanceof KeyEvent && e.getSource() instanceof Mutable ) {
			((KeyEvent) e).consume();
		    }
		}
	    };
	}


	@Override
	public void paint( final Graphics g ) {
	    final Rectangle r = workspace.getBounds();
	    if( bufferImage == null || bufferImage.getHeight( this ) != r.height || bufferImage.getWidth( this ) != r.width ) {

		rebuildBufferImage( r.width, r.height );
	    }

	    final Graphics2D g2 = (Graphics2D) g;
	    g2.setComposite( composite );
	    g2.drawImage( bufferImage, r.x, r.y, this );
	}


	private void rebuildBufferImage( final int w, final int h ) {
	    bufferImage = createImage( w, h );
	    final Graphics g = bufferImage.getGraphics();
	    // Color color = UIManager.getColor("effect").brighter();
	    final Color color = UIManager.getColor( "effect" );
	    g.setColor( color );
	    g.fillRect( 0, 0, w, h );

	    g.setColor( color.brighter().brighter() );
	    final Font font = new Font( "sanserif", Font.BOLD, 36 );
	    g.setFont( font );
	    final FontMetrics fm = g.getFontMetrics( font );
	    final String[] text = { "Bearbeitung nicht möglich!", "", "Die Daten werden von einem", "anderen Benutzer geÄndert!" };
	    for( int i = 0; i < text.length; i++ ) {
		final int th = text.length * (fm.getHeight() + 8);
		final int y = (h - th) / 2 + (fm.getHeight() + 8) * i;
		final int x = (w - fm.stringWidth( text[i] )) / 2;
		g.drawString( text[i], x, y );
	    }
	}


	@Override
	public void setVisible( final boolean visible ) {
	    if( visible ) {
		Toolkit.getDefaultToolkit().addAWTEventListener( eventListener, AWTEvent.KEY_EVENT_MASK );
	    } else {
		Toolkit.getDefaultToolkit().removeAWTEventListener( eventListener );
	    }
	    super.setVisible( visible );
	}
    }

    /**
     * This universal Mouse(Motion)Listener eats all MouseEvents expect those are not under the "blue panel".
     */
    private class MyMouseInputAdapter extends MouseInputAdapter {

	private final Component glasspane;


	public MyMouseInputAdapter( final Component glasspane ) {
	    this.glasspane = glasspane;
	}


	@Override
	public void mouseClicked( final MouseEvent e ) {
	    redispatchMouseEvent( e );
	}


	@Override
	public void mouseDragged( final MouseEvent e ) {
	    redispatchMouseEvent( e );
	}


	@Override
	public void mouseEntered( final MouseEvent e ) {
	    redispatchMouseEvent( e );
	}


	@Override
	public void mouseExited( final MouseEvent e ) {
	    redispatchMouseEvent( e );
	}


	@Override
	public void mouseMoved( final MouseEvent e ) {
	    final Point p = e.getPoint();
	    final Point pc = SwingUtilities.convertPoint( glasspane, p, workspace );
	    glasspane.setCursor( Cursor.getPredefinedCursor( pc.x < 0 ? Cursor.DEFAULT_CURSOR : Cursor.WAIT_CURSOR ) );
	}


	@Override
	public void mousePressed( final MouseEvent e ) {
	    redispatchMouseEvent( e );
	}


	@Override
	public void mouseReleased( final MouseEvent e ) {
	    redispatchMouseEvent( e );
	}


	private void redispatchMouseEvent( final MouseEvent e ) {
	    // Mouse position relativ to the application content (e.g. the
	    // glass pane)
	    final Point p = e.getPoint();
	    // Mouse position relativ to the workspace component
	    Point pc = SwingUtilities.convertPoint( glasspane, p, workspace );

	    if( pc.x < 0 ) { // we are in navigator, do not eat!
		// Mouse position relativ to the navigator component
		final Point pn = SwingUtilities.convertPoint( glasspane, p, navigator );
		// Gets component
		final Component comp = SwingUtilities.getDeepestComponentAt( navigator, pn.x, pn.y );
		if( comp != null ) { // redispatch
		    // Mouse position relativ to the retrieved component
		    pc = SwingUtilities.convertPoint( glasspane, p, comp );
		    // do event
		    comp.dispatchEvent( new MouseEvent( comp, e.getID(), e.getWhen(), e.getModifiers(), pc.x, pc.y, e.getClickCount(), e.isPopupTrigger() ) );
		}
	    }
	}
    }

    private static final long  serialVersionUID	     = -6786914978162974627L;

    public static final int    STANDALONE	     = 0;

    public static final int    CLIENT		     = 1;

    public static final int    SERVER		     = 2;

    public static final String BSEARCH		     = "search";

    public static final String BNEW		     = "new";

    public static final String BPLIST		     = "patientlist";

    public static final String BQUIT		     = "quit";

    public static final String BDETAILS		     = "details";

    public static final String BDIAGNOSIS	     = "diagnosis";

    public static final String BMEDICATION	     = "medication";

    public static final String BBILL		     = "bill";

    public static final String BFINISH		     = "finish";

    public static final String BABOUT		     = "about";

    // Constants describing possible actions, see backAction.
    public static final int    SEARCH		     = 1;

    public static final int    DIAGNOSIS	     = 2;

    public static final int    MEDICATION	     = 3;

    public static final int    BILL		     = 4;

    public static final int    NEW_PATIENT	     = 5;

    public static final int    DETAILS		     = 6;

    public static final int    LETTER		     = 7;

    public static final int    MACRO		     = 8;

    public static final int    CREATE_MACRO	     = 9;

    public static final int    PATIENTLIST	     = 10;

    /** @see javax.swing.KeyStroke#getKeyStroke(java.lang.String) */
    public static final String KEYSEARCH	     = "alt S";

    public static final String KEYNEW		     = "alt N";

    // For Patientlist
    public static final String KEYPLIST		     = "alt P";

    public static final String KEYQUIT		     = "alt Q";

    /** @todo think of a better short cut for details */
    public static final String KEYDETAILS	     = "alt E";

    public static final String KEYDIAGNOSIS	     = "alt D";

    // public static final String KEYMEDICATION = "alt V";
    // public static final String KEYBILL = "alt R";
    public static final String KEYFINISH	     = "alt Z";

    public static final String KEYABOUT		     = "alt A";

    // Resource of property file.
    public final static String MEDIKNIGHT_PROPERTIES = "mediknight.properties";

    public final static String PROPERTY_FILENAME     = "properties/" + MEDIKNIGHT_PROPERTIES;

    // this application
    private static MediKnight  application;

    /**
     * The name of the application.
     */
    public static String       NAME;

    public static String       VERSION;

    // the absolute location of the application
    private static File	       location;

    /**
     * "Tracing-Class".
     * <p>
     * Tracing-calls with this class reports user actions.
     */
    public final static String USER		     = "user";

    // this properties
    private static Properties  properties;

    // Tracer
    private static Tracer      tracer;


    public static Datasource[] collectDatasources() {
	int i = 1;
	final List< Datasource > sources = new ArrayList< Datasource >();

	while( properties.containsKey( "dbserver." + i + ".description" ) ) {
	    final String prefix = "dbserver." + i + ".";
	    final Datasource source = new MediKnight.Datasource( properties.getProperty( prefix + "description" ), properties.getProperty( prefix + "driver" ),
		    properties.getProperty( prefix + "url" ), properties.getProperty( prefix + "user" ), properties.getProperty( prefix + "password" ) );
	    sources.add( source );
	    i++;
	}

	return sources.toArray( new Datasource[0] );
    }


    private static void connectDB( final Datasource source ) throws ClassNotFoundException, SQLException {
	Class.forName( source.driver );
	Datastore.current.connect( source.url, source.user, source.password );
    }


    /**
     * Returns the application itself.
     */
    public static MediKnight getApplication() {
	return application;
    }


    public static File getAppLocation() {
	return location;
    }


    /**
     * Returns application properties.
     */
    public static Properties getProperties() {
	return properties;
    }


    public static Tracer getTracer() {
	return tracer;
    }


    public static void initDB() {
	final Datasource[] sources = collectDatasources();
	boolean initialConnectError = true;

	if( sources.length == 0 ) {
	    System.exit( 1 );
	} else {
	    try {
		connectDB( sources[0] );
		initialConnectError = false;
	    } catch( final ClassNotFoundException e ) {
		e.printStackTrace();
	    } catch( final SQLException e ) {
		e.printStackTrace();
	    }

	    if( initialConnectError ) {
		boolean connected = false;
		Datasource lastSource = null;

		while( !connected ) {
		    final DatabaseSelectionDialog dialog = new DatabaseSelectionDialog( sources );
		    if( initialConnectError ) {
			initialConnectError = false;
			dialog.showNoticeInitialDatabaseNotAvailable( sources[0] );
		    } else {
			dialog.showNoticeDatabaseNotAvailable( lastSource );
			dialog.selectSource( lastSource );
		    }
		    dialog.setVisible( true );

		    if( dialog.isCancelled() ) {
			System.exit( 0 );
		    } else {
			lastSource = dialog.getSelectedSource();
			try {
			    connectDB( lastSource );
			    connected = true;
			} catch( final ClassNotFoundException e1 ) {
			    e1.printStackTrace();
			} catch( final SQLException e1 ) {
			    e1.printStackTrace();
			}
		    }
		}
	    }
	}
    }


    //
    // Determines the absolute location of this application.
    //
    public static void initLocation() {
	final URL url = MediKnight.class.getClassLoader().getResource( "." );

	if( url != null ) {
	    // We run as an ordinary class collection ...
	    location = new File( url.getPath() );
	} else {
	    // We run as a jar ...
	    try {
		final String ud = System.getProperty( "user.dir" );
		final String cp = System.getProperty( "java.class.path" );
		final int spos = cp.lastIndexOf( File.separator );
		if( spos > 0 ) {
		    // Called outside from jar dir
		    location = new File( ud + File.separator + cp.substring( 0, spos ) ).getCanonicalFile();

		} else if( spos == 0 ) {
		    // Called from root dir
		    location = new File( File.separator );

		} else {
		    // Called inside from jar dir
		    location = new File( ud );

		}
	    } catch( final IOException iox ) {
		System.err.println( "Could not determine location of the application." );
		System.exit( 1 );
	    }
	}
    }


    public static void initProperties() throws IOException {
	InputStream is = null;

	try {
	    is = new FileInputStream( new File( MEDIKNIGHT_PROPERTIES ) );
	} catch( final FileNotFoundException e ) {
	    is = MediKnight.class.getClassLoader().getResourceAsStream( PROPERTY_FILENAME );
	}

	properties = new Properties();
	properties.load( is );
	is.close();
	is = null;

	// Merge the user preferences in.
	final Properties userPreferences = new Properties();

	try {
	    is = new FileInputStream( new File( System.getProperty( "user.home" ), ".mediknight.properties" ) );
	    userPreferences.load( is );
	    final Iterator< Object > it = userPreferences.keySet().iterator();

	    while( it.hasNext() ) {
		final Object key = it.next();
		properties.put( key, userPreferences.get( key ) );
	    }
	} catch( final FileNotFoundException e ) {
	    System.err.println( "No user preferences." );
	} finally {
	    if( is != null ) {
		is.close();
	    }
	}
    }


    public static void initTracer() {
	final String[] tcs = { DEBUG, TraceConstants.ERROR, INFO, WARNING, USER, KnightObject.DATA };
	for( final String tc : tcs ) {
	    Tracer.addTraceClass( tc );
	}

	String logPath = properties.getProperty( "log.path" );
	if( logPath == null || logPath.length() == 0 ) {
	    // No explizit Tracer given, trace out to the console
	    tracer = new Tracer( System.out );
	} else {
	    // Find the right place for create logfiles.
	    // First replace '.' with '/' or '\\'
	    final StringBuffer sb = new StringBuffer();
	    final int l = logPath.length();
	    for( int i = 0; i < l; i++ ) {
		final char c = logPath.charAt( i );
		sb.append( c == '.' ? File.separatorChar : c );
	    }
	    logPath = sb.toString();
	    String home = System.getProperty( "user.home" );
	    if( home == null || home.length() == 0 ) {
		home = location.getPath();
	    }
	    logPath = home + File.separator + logPath;

	    // System.out.println("Logfile Basepath: "+logPath);

	    final String logMaxLength = properties.getProperty( "log.maxlength" );
	    final String logMaxCycle = properties.getProperty( "log.maxcycle" );
	    final LogWriter writer = new LogWriter( logPath, Long.parseLong( logMaxLength ), Integer.parseInt( logMaxCycle ) );
	    tracer = new Tracer( writer );
	}
	KnightObject.setTracer( tracer );

	final String traceClasses = properties.getProperty( "trace.classes" );
	final StringTokenizer st = new StringTokenizer( traceClasses, "," );
	while( st.hasMoreTokens() ) {
	    tracer.enable( st.nextToken() );
	}
    }


    public static void main( final String[] args ) {
	SplashWindow splash = null;

	initLocation();
	try {
	    initProperties();
	    MediknightTheme.install( properties );

	    splash = new SplashWindow( "images/mediknight-splash.png" );

	    // determine application name and version
	    NAME = getProperties().getProperty( "name", "MediKnight" );
	    VERSION = getProperties().getProperty( "version", "2.0" ); // TODO: Was bedeutet dies hier?

	    // Global variable 'application' set by the constructor
	    new MediKnight();

	    application.applyProperties();

	    initTracer();

	    // DB-Stuff
	    final String s = properties.getProperty( "borm.debug" );
	    ObjectMapper.debug = Boolean.valueOf( s ).booleanValue();

	    initDB();

	    if( splash != null ) {
		splash.dispose();
	    }

	    // Login
	    final LoginDialog ld = new LoginDialog( application );
	    ld.setTitle( NAME + " Anmeldung" );
	    ld.setVisible( true );
	    if( ld.getUser() == null ) {
		throw new IllegalAccessException( "Anwender hat sich nicht authentifiziert!" );
	    }

	    application.user = ld.getUser();
	    final Map< String, String > map = UserProperty.retrieveUserInformation( application.user );
	    final String bs = map.get( "frame.bounds" );
	    if( bs != null ) {
		final int[] bounds = MediknightUtilities.readCSV( bs );
		application.setBounds( bounds[0], bounds[1], bounds[2], bounds[3] );
	    }

	    application.setVisible( true );

	    tracer.trace( INFO, "*** " + NAME + " Application started ***" );

	    // Start with the SearchPanel
	    application.search();
	} catch( final FileNotFoundException fnfx ) {
	    System.err.println( "No properties found" );
	    System.exit( 1 );
	} catch( final IllegalAccessException iax ) {
	    tracer.trace( TraceConstants.ERROR, iax );
	    System.exit( 1 );
	} catch( final RuntimeException rtx ) {
	    rtx.printStackTrace();
	    System.err.println( "Property error" );
	    System.exit( 1 );
	} catch( final Exception x ) {
	    x.printStackTrace();
	    tracer.trace( TraceConstants.ERROR, x );
	}
    }


    public static void storeProperties( final Properties prop ) throws FileNotFoundException, IOException {
	properties = prop;
	properties.store( new FileOutputStream( MEDIKNIGHT_PROPERTIES ), "" );
    }

    // user for recent patientlist
    private User	      user;

    // we use EUR instead of DM?
    private boolean	      euro;

    private int		      backAction;

    private int		      currentAction;

    private JPanel	      header;

    private JLabel	      subtitle;

    private JLabel	      title;

    private JLabel	      patientLabel;

    // panel container
    private JComponent	      workspace;

    // Statical List of master data
    private RechnungsPosten[] rechnungsPosten;

    //
    private TagesDiagnose     oldDiagnosis = null;

    // gradient painted navigator panel
    private JGradientPanel    navigator;

    // current used glasspane for presenting "disabled" mode
    private GrayedPane	      grayedPane;

    // Locking info
    private final LockingInfo lockingInfo;

    private Presenter	      currentPresenter;

    private boolean	      ignoreCommit;

    private Patient	      oldPatient   = null;


    private MediKnight() {
	registerMappers();
	application = this;

	backAction = currentAction = SEARCH;
	ignoreCommit = false;
	euro = false;

	lockingInfo = new LockingInfo();

	// default locale for this application
	setLocale( Locale.GERMAN );

	setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );

	addWindowListener( new WindowAdapter() {

	    @Override
	    public void windowClosing( final WindowEvent e ) {
		quit( true );
	    }
	} );

	addComponentListener( new ComponentAdapter() {

	    @Override
	    public void componentMoved( final ComponentEvent e ) {
		saveSize();
	    }
	} );

	getContentPane().addComponentListener( new ComponentAdapter() {

	    @Override
	    public void componentResized( final ComponentEvent e ) {
		saveSize();
	    }
	} );

	createUI();
    }


    private void about() {

	final String dialogOne = NAME + " " + VERSION;
	final String dialogTwo = getProperties().getProperty( "dialog.two" );
	final String dialogThree = getProperties().getProperty( "dialog.three" );
	final String attention = getProperties().getProperty( "attention" );

	final Object[] msg = new Object[1];

	msg[0] = new JLabel( "<html><font face=dialog>" + "<font size=5><b>" + dialogOne + "</b></font>" + "<p><b>" + dialogTwo + "</b></p" + "<p>"
		+ dialogThree + "</p>" + "<p>&#160;</p>" + "<p><font size=1>" + attention + "</font></p>" );

	final String[] bt = { "OK" };
	JOptionPane.showOptionDialog( this, msg, "Info", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, bt, bt[0] );
    }


    public void applyAction( final int action ) {
	switch( action ) {
	    case SEARCH:
		search();
		break;

	    case DIAGNOSIS:
		showDiagnosisPane();
		break;

	    case MEDICATION:
		if( getCurrentDiagnosis() == null ) {
		    setCurrentDiagnosis( oldDiagnosis );
		}
		medication();
		break;

	    case DETAILS:
		showDetailsPane();
		break;

	    case NEW_PATIENT:
		newPatient();
		break;

	    case BILL:
		if( getCurrentDiagnosis() == null ) {
		    setCurrentDiagnosis( oldDiagnosis );
		}
		bill();
		break;

	    case LETTER:
		if( getCurrentDiagnosis() == null ) {
		    setCurrentDiagnosis( oldDiagnosis );
		}
		letter();
		break;

	    case MACRO:
		if( getCurrentDiagnosis() == null ) {
		    setCurrentDiagnosis( oldDiagnosis );
		}
		macro();
		break;

	    case CREATE_MACRO:
		/**
		 * @todo At the moment it is not possible to return to the create macro function since the information about selected bill entries
		 */
		bill();
		break;

	    default:
		search();
		break;
	}
    }


    private void applyProperties() {
	final String s = properties.getProperty( "currency" );
	euro = s != null && s.toLowerCase().startsWith( "eur" );
    }


    public void bill() {
	setWaitCursor();
	tracer.trace( USER, "Schalter \"Rechnung\" aktiviert" );

	showBillPane();
    }


    public void commit() {
	if( !ignoreCommit && currentPresenter instanceof Commitable ) {
	    final MediKnight app = getApplication();
	    final LockingInfo li = app.getLockingInfo();
	    final Patient patient = li.getPatient();
	    Lock lock = null;
	    try {
		if( patient != null ) {
		    lock = patient.acquireLock( li.getAspect(), LockingListener.LOCK_TIMEOUT );
		    if( lock != null ) {
			tracer.trace( DEBUG, "Committing on " + currentPresenter );
			((Commitable) currentPresenter).commit();
		    } else {
			return;
		    }
		}
	    } catch( final SQLException sqle ) {
	    } finally {
		try {
		    lock.release();
		} catch( final Exception ex ) {
		}
	    }
	}
    }


    public void createMacro( final BillEntry[] entries ) {
	setHeaderPanel( "Baustein erstellen" );
	final CreateMacroModel model = new CreateMacroModel( entries );
	setPane( new CreateMacroPresenter( model ) );
	currentAction = CREATE_MACRO;
    }


    private void createUI() {
	grayedPane = new GrayedPane();
	setGlassPane( grayedPane );

	Color c1 = UIManager.getColor( "effect" );
	if( c1 == null ) {
	    c1 = Color.BLUE;
	}
	final Color c2 = Color.WHITE;

	setSize( 800, 600 );

	// create title component
	header = new JPanel( new GridBagLayout() );
	header.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
	header.setBackground( c1 );
	title = new JLabel( "Willkommen zu " + NAME );
	title.setForeground( c2 );
	title.setFont( title.getFont().deriveFont( Font.BOLD, 24.0f ) );
	subtitle = new JLabel( NAME + " " + VERSION + " - Abrechnungsprogramm für Heilpraktiker" );
	subtitle.setFont( subtitle.getFont().deriveFont( Font.BOLD ) );
	header.add( title, new GridBagConstraints( 0, 1, 1, 1, 1.0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	header.add( subtitle,
		new GridBagConstraints( 0, 0, 1, 1, 1.0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );

	// create navigator component
	navigator = new JGradientPanel();
	navigator.setGradientColor( c1 );
	navigator.setBackground( c2 );
	navigator.setLayout( new FlexGridLayout( 0, 1, 5, 5 ) );
	navigator.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

	final JToggleButton searchButton = new JToggleButton( "Suchen" );
	searchButton.setName( BSEARCH );
	searchButton.setMnemonic( 'S' );
	searchButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		search();
	    }
	} );
	searchButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KEYSEARCH ), "search" );
	searchButton.getActionMap().put( "search", new AbstractAction() {

	    private static final long serialVersionUID = -6786914978162974627L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		search();
	    }
	} );

	final JToggleButton newButton = new JToggleButton( "Neu" );
	newButton.setName( BNEW );
	newButton.setMnemonic( 'N' );
	newButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		newPatient();
	    }
	} );
	newButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KEYNEW ), "newPatient" );
	newButton.getActionMap().put( "newPatient", new AbstractAction() {

	    private static final long serialVersionUID = -6786914978162974627L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		newPatient();
	    }
	} );

	final JToggleButton patientListButton = new JToggleButton( "Patientenliste" );
	patientListButton.setName( BPLIST );
	patientListButton.setMnemonic( 'P' );
	patientListButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		showPatientListPane();
	    }
	} );
	patientListButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KEYNEW ), "patientList" );
	patientListButton.getActionMap().put( "patientList", new AbstractAction() {

	    private static final long serialVersionUID = -4140114497197053021L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		showPatientListPane();
	    }
	} );

	final JToggleButton quitButton = new JToggleButton( "Beenden" );
	quitButton.setName( BQUIT );
	quitButton.setMnemonic( 'B' );
	quitButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		quit( true );
	    }
	} );
	quitButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KEYQUIT ), "quitApplication" );
	quitButton.getActionMap().put( "quitApplication", new AbstractAction() {

	    private static final long serialVersionUID = -6786914978162974627L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		quit( true );
	    }
	} );

	final JToggleButton detailsButton = new JToggleButton( "Details" );
	detailsButton.setName( BDETAILS );
	detailsButton.setMnemonic( 'e' );
	detailsButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		patientDetails();
	    }
	} );
	detailsButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KEYDETAILS ), "patientDetails" );
	detailsButton.getActionMap().put( "patientDetails", new AbstractAction() {

	    private static final long serialVersionUID = -6786914978162974627L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		patientDetails();
	    }
	} );

	final JToggleButton diagnosisButton = new JToggleButton( "Diagnose" );
	diagnosisButton.setName( BDIAGNOSIS );
	diagnosisButton.setMnemonic( 'D' );
	diagnosisButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		diagnosis();
	    }
	} );
	diagnosisButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KEYDIAGNOSIS ), "diagnose" );
	diagnosisButton.getActionMap().put( "diagnose", new AbstractAction() {

	    private static final long serialVersionUID = -6786914978162974627L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		diagnosis();
	    }
	} );

	final JToggleButton medicationButton = new JToggleButton( "Verordnung" );
	medicationButton.setName( BMEDICATION );
	medicationButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		applyAction( MEDICATION );
	    }
	} );
	/*
	 * medicationButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke(KEYMEDICATION), "verordnung");
	 * medicationButton.getActionMap().put("verordnung", new AbstractAction() { public void actionPerformed(ActionEvent e) { medication(); } });
	 */

	final JToggleButton billButton = new JToggleButton( "Rechnung" );
	billButton.setName( BBILL );
	billButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		bill();
	    }
	} );
	/*
	 * billButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke .getKeyStroke(KEYBILL), "rechnung");
	 * billButton.getActionMap().put("rechnung", new AbstractAction() { public void actionPerformed(ActionEvent e) { bill(); } });
	 */

	final JButton finishButton = new JButton( "Zurück" );
	finishButton.setName( BFINISH );
	finishButton.setMnemonic( 'z' );
	finishButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		finish();
	    }
	} );
	finishButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KEYFINISH ), "zurueck" );
	finishButton.getActionMap().put( "zurueck", new AbstractAction() {

	    private static final long serialVersionUID = -6786914978162974627L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		finish();
	    }
	} );

	final URL logoUrl = MediKnight.class.getClassLoader().getResource( "images/logo.gif" );
	final ImageIcon icon = new ImageIcon( Toolkit.getDefaultToolkit().createImage( logoUrl ) );

	final JButton aboutButton = new JButton( icon );
	aboutButton.setBorder( BorderFactory.createEmptyBorder() );
	aboutButton.setBackground( c2 );
	aboutButton.setFocusPainted( false );
	aboutButton.setContentAreaFilled( false );
	aboutButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		about();
	    }
	} );
	aboutButton.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KEYABOUT ), "about" );
	aboutButton.getActionMap().put( "about", new AbstractAction() {

	    private static final long serialVersionUID = -6786914978162974627L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		about();
	    }
	} );

	final JPanel logoPanel = new JPanel();
	logoPanel.setLayout( new BorderLayout() );
	logoPanel.setBackground( c2 );
	final JPanel dummyPanel = new JPanel();
	dummyPanel.setBackground( c2 );
	logoPanel.add( dummyPanel, BorderLayout.SOUTH );
	logoPanel.add( aboutButton, BorderLayout.CENTER );

	patientLabel = new JLabel( " " );
	navigator.add( patientLabel, "11,0,c" );
	navigator.add( searchButton, "11,0,c" );
	navigator.add( newButton, "11,0,c" );
	navigator.add( patientListButton, "11,0,c" );
	navigator.add( detailsButton, "11,0,c" );
	navigator.add( diagnosisButton, "11,0,c" );
	navigator.add( medicationButton, "11,0,c" );
	navigator.add( billButton, "11,0,c" );
	navigator.add( finishButton, "11,0,c" );
	navigator.add( quitButton, "11,0,c" );

	// create workspace component
	workspace = new JPanel( new BorderLayout() );
	// workspace.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	final JPanel buttonPanel = new JPanel( new BorderLayout() );

	buttonPanel.add( navigator, BorderLayout.CENTER );
	buttonPanel.add( logoPanel, BorderLayout.SOUTH );

	// layout components
	final Container pane = getContentPane();
	pane.setLayout( new GridBagLayout() );

	final JPanel filler = new JPanel();
	filler.setBackground( c1.brighter() );
	pane.add( filler, new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );

	pane.add( header, new GridBagConstraints( 1, 0, 1, 1, 1.0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );

	pane.add( workspace,
		new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 10, 10, 10, 10 ), 0, 0 ) );

	pane.add( buttonPanel,
		new GridBagConstraints( 0, 1, 1, 1, 0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
    }


    public void diagnosis() {
	setWaitCursor();
	tracer.trace( USER, "Schalter \"Diagnose\" aktiviert" );
	showDiagnosisPane();
	setDefaultCursor();
    }


    public void enableEditing( final boolean enable ) {
	// CAUTION:
	// This method may hang if two or more instances of the application
	// run on the same virtual machine.
	// That would be an AWT bug.
	grayedPane.setVisible( !enable );
    }


    public void finish() {
	applyAction( backAction );
    }


    public int getCurrency() {
	return euro ? CurrencyNumber.EUR : CurrencyNumber.DM;
    }


    public TagesDiagnose getCurrentDiagnosis() {
	return lockingInfo.getDiagnosis();
    }


    public Patient getCurrentPatient() {
	return lockingInfo.getPatient();
    }


    public Presenter getCurrentPresenter() {
	return currentPresenter;
    }


    public LockingInfo getLockingInfo() {
	return lockingInfo;
    }


    /**
     * Returns statical loaded bill items.
     * <p>
     * This method retrieve the bill items from the database on demand.
     *
     * @exception SQLException
     *                on db error
     */
    public RechnungsPosten[] getRechnungsPosten() throws SQLException {

	if( rechnungsPosten == null ) {
	    rechnungsPosten = RechnungsPosten.retrieve().toArray( new RechnungsPosten[0] );
	    Arrays.sort( rechnungsPosten );
	}
	return rechnungsPosten;
    }


    /**
     * Returns the user
     */
    public User getUser() {
	return user;
    }


    public boolean isEuro() {
	return euro;
    }


    public void letter() {

	final TagesDiagnose d = getCurrentDiagnosis();
	if( d != null ) {
	    try {
		final LetterModel model = new LetterModel( d.getRechnung() );
		setPane( new LetterPresenter( model ) );
		currentAction = LETTER;
	    } catch( final SQLException e ) {
		e.printStackTrace();
		/** @todo Exception reporting. */
	    }
	}
    }


    public void macro() {
	macro( null );
    }


    public void macro( final BillEntry[] entries ) {

	final TagesDiagnose d = getCurrentDiagnosis();
	if( d != null ) {
	    try {
		setHeaderPanel( "Rechnungsbausteine" );
		final MacroModel macroModel = new MacroModel( entries, d.getRechnung() );
		setPane( new MacroPresenter( macroModel ) );
		currentAction = MACRO;
	    } catch( final SQLException e ) {
		e.printStackTrace();
		/** @todo Exception reporting. */
	    }
	}
    }


    public void medication() {

	tracer.trace( USER, "Schalter \"Verordnung\" aktiviert" );

	final TagesDiagnose d = getCurrentDiagnosis();
	if( d != null ) {
	    showMedicationPane( d );
	}
    }


    public void newPatient() {
	setHeaderPanel( "Patient neu anlegen" );
	setPatientToNavigator( null );
	setVisibleNavigatorButton( BSEARCH, true, false );
	setVisibleNavigatorButton( BNEW, true, true );
	setVisibleNavigatorButton( BPLIST, true, false );
	setVisibleNavigatorButton( BQUIT, true, false );
	setVisibleNavigatorButton( BDETAILS, false, false );
	setVisibleNavigatorButton( BDIAGNOSIS, false, false );
	setVisibleNavigatorButton( BMEDICATION, false, false );
	setVisibleNavigatorButton( BBILL, false, false );
	setVisibleNavigatorButton( BFINISH, true, false );
	setVisibleNavigatorButton( BABOUT, false, false );

	setWaitCursor();
	tracer.trace( USER, "Schalter \"Neu\" aktiviert" );

	setTitle( "" );

	oldPatient = getCurrentPatient();
	setCurrentPatient( null );
	setCurrentDiagnosis( null );
	final PatientModel model = new PatientModel( new Patient() );
	model.setNewPatient( true );
	setPane( new PatientPresenter( model ) );
	currentAction = NEW_PATIENT;

	setDefaultCursor();
    }


    public void patientDetails() {
	setWaitCursor();
	tracer.trace( USER, "Schalter \"Details\" aktiviert" );
	showDetailsPane();
	setDefaultCursor();
    }


    public void quit( final boolean check ) {
	if( check ) {
	    final int action = JOptionPane.showConfirmDialog( this, "Alle Daten wurden gespeichert.\nMöchten Sie das Programm jetzt beenden?", NAME,
		    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );

	    if( action == JOptionPane.NO_OPTION ) {
		setVisibleNavigatorButton( BQUIT, true, false );
		return;
	    }
	}

	setCurrentPatient( null );

	commit();

	try {
	    tracer.trace( USER, "Schalter \"Quit\" aktiviert" );

	    Lock.releaseAll();

	} catch( final Exception x ) {
	    tracer.trace( TraceConstants.ERROR, x );
	} finally {
	    tracer.trace( INFO, "*** " + NAME + " Application ended ***" );
	    for( int i = 0; i < 5; i++ ) {
		tracer.trace( INFO, "*" );
	    }
	    System.exit( 0 );
	}
    }


    private void registerMappers() {
	// Wow, now that's ugly! It seems like with newer JDK releases,
	// static class initializers are not called anymore only by
	// mentioning the class alone. Therefore we have to create
	// dummy instances of the persistent classes.
	// Consider this a bad hack!
	new TagesDiagnose();
	new Rechnung();
	new Verordnung();
    }


    public void reload() {
	/**
	 * @todo: HACK! Find a better way to suppress committing.
	 */
	tracer.trace( DEBUG, "Reloading ..." );
	ignoreCommit = true;
	applyAction( currentAction );
	ignoreCommit = false;
    }


    /**
     * Save the current user's last window size
     */
    private void saveSize() {
	if( user != null ) {
	    try {
		final Rectangle r = getBounds();
		final int[] bounds = new int[] { r.x, r.y, r.width, r.height };
		UserProperty.save( user, "frame.bounds", MediknightUtilities.writeCSV( bounds ) );
	    } catch( final SQLException x ) {
		/** @todo: better exception handling */
	    }
	}
    }


    public void search() {
	commit();

	setHeaderPanel( "Patientensuche" );
	setVisibleNavigatorButton( BSEARCH, true, true );
	setVisibleNavigatorButton( BNEW, true, false );
	setVisibleNavigatorButton( BPLIST, true, false );
	setVisibleNavigatorButton( BQUIT, true, false );
	setVisibleNavigatorButton( BDETAILS, false, false );
	setVisibleNavigatorButton( BDIAGNOSIS, false, false );
	setVisibleNavigatorButton( BMEDICATION, false, false );
	setVisibleNavigatorButton( BBILL, false, false );
	setVisibleNavigatorButton( BFINISH, true, false );
	setVisibleNavigatorButton( BABOUT, true, false );

	try {
	    Lock.releaseAll();
	} catch( final Exception x ) {
	    tracer.trace( TraceConstants.ERROR, x );
	}

	setPatientToNavigator( null );
	setWindowTitle( true );
	setWaitCursor();
	tracer.trace( USER, "Schalter \"Suchen\" aktiviert" );
	setCurrentPatient( null );
	setPane( new SearchPresenter() );
	setDefaultCursor();

	currentAction = SEARCH;
    }


    public void selectPatient( final Patient patient ) throws SQLException {
	setCurrentPatient( patient );
	patient.adjustTagesDiagnosen();
	setCurrentDiagnosis( patient.getLetzteTagesDiagnose() );
	// setCurrentDiagnosis(null);
	diagnosis();
    }


    public void setCurrentDiagnosis( final TagesDiagnose diagnose ) {
	lockingInfo.setDiagnosis( diagnose );
    }


    public void setCurrentPatient( final Patient patient ) {
	lockingInfo.setPatient( patient );
    }


    public void setDefaultCursor() {
	setCursor( Cursor.getDefaultCursor() );
    }


    public void setHeaderPanel( final String text ) {
	title.setText( text );
    }


    /**
     * This method changes the current displayed data panel.
     */
    public void setPane( final Presenter presenter ) {
	if( tracer != null ) {
	    tracer.trace( DEBUG, "calling setPane(" + presenter.getClass().getName() + ")" );
	}

	commit();

	/** @todo This is pretty stupid, but should work for now. Improve! */
	if( currentPresenter != null ) {
	    if( currentPresenter.getClass() != presenter.getClass() ) {
		backAction = currentAction;
	    }
	    if( currentPresenter instanceof Observer ) {
		lockingInfo.deleteObserver( (Observer) currentPresenter );
	    }
	}
	if( presenter instanceof Observer ) {
	    lockingInfo.addObserver( (Observer) presenter );
	}
	currentPresenter = presenter;

	if( workspace.getComponentCount() > 0 ) {
	    final Component pane = workspace.getComponent( 0 );
	    workspace.remove( pane );
	}
	workspace.add( currentPresenter.createView() );
	workspace.revalidate();
	workspace.repaint();

	presenter.activate();

	enableEditing( true );
    }


    /**
     * Sets the navigation tooltip according to the given patient.
     */
    public void setPatientToNavigator( final Patient patient ) {
	if( patient != null ) {
	    final String anrede = patient.getAnrede();
	    final String firstName = patient.getVorname();
	    final String lastName = patient.getName();
	    String text = "<html><font face=tahoma size=2><b>" + anrede + (anrede.equals( "" ) ? "" : "<br>") + firstName
		    + (firstName.equals( "" ) ? "" : "<br>") + lastName + (lastName.equals( "" ) ? "" : "<br>");

	    if( patient.getGeburtsDatum() != null ) {
		text += MediknightUtilities.formatDate( patient.getGeburtsDatumAsDate() );
		if( patient.getAge() > 0 ) {
		    text += "<br>" + patient.getAge() + " Jahre";
		}
	    }

	    text += "</b></font></html>";
	    patientLabel.setText( text );

	    if( patient.hasBirthday() ) {
		patientLabel.setOpaque( true );
		patientLabel.setBackground( Color.red );
	    } else {
		patientLabel.setOpaque( false );
	    }
	} else {
	    patientLabel.setText( "" );
	    patientLabel.setToolTipText( "" );
	}
    }


    /**
     * This method controls the navigator panel. Buttons are identified by name.
     */
    public void setVisibleNavigatorButton( final String name, final boolean visible, final boolean selected ) {

	for( int i = 0; i < navigator.getComponentCount(); i++ ) {
	    final Component c = navigator.getComponent( i );

	    if( c.getName() != null && c.getName().equals( name ) ) {
		c.setVisible( visible );
		if( c instanceof JToggleButton ) {
		    ((JToggleButton) c).setSelected( selected );
		}
	    }
	    repaint();
	}
    }


    public void setWaitCursor() {
	setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
    }


    private void setWindowTitle() {
	if( getCurrentPatient() != null ) {
	    setTitle( getCurrentPatient().getFullname() + " - " + NAME );
	} else {
	    setTitle( NAME );
	}
    }


    private void setWindowTitle( final boolean check ) {
	if( check ) {
	    setTitle( NAME );
	} else {
	    setWindowTitle();
	}
    }


    /**
     * Sets the bill pane as the current pane. This methods sets the title too.
     */
    public void showBillPane() {
	final TagesDiagnose d = getCurrentDiagnosis();
	if( d != null ) {
	    setTitle( "Rechnung" );
	    setWindowTitle();
	    setHeaderPanel( "Rechnung (" + new SimpleDateFormat( "dd.MM.yyyy" ).format( d.getDatumAsDate() ) + ")" );

	    try {
		final BillModel model = new BillModel( d.getRechnung() );
		setPane( new BillPresenter( model ) );
		currentAction = BILL;
	    } catch( final SQLException e ) {
		e.printStackTrace();
		/** TODO Exception reporting. */
	    }
	}
    }


    public void showDetailsPane() {
	// Der folgende Guard wird benoetigt, da die momentane Behandlung von
	// "Zurueck"-Aktionen nicht wirklich gut ist.
	if( getCurrentPatient() == null ) {
	    return;
	}

	setTitle( "Patient" );
	setWindowTitle();

	setVisibleNavigatorButton( BSEARCH, true, false );
	setVisibleNavigatorButton( BNEW, true, false );
	setVisibleNavigatorButton( BPLIST, true, false );
	setVisibleNavigatorButton( BQUIT, true, false );
	setVisibleNavigatorButton( BDETAILS, true, true );
	setVisibleNavigatorButton( BDIAGNOSIS, true, false );
	setVisibleNavigatorButton( BMEDICATION, true, false );
	setVisibleNavigatorButton( BBILL, false, false );
	setVisibleNavigatorButton( BFINISH, true, false );
	setVisibleNavigatorButton( BABOUT, false, false );

	setTitle( "Patientendetails" );
	setHeaderPanel( "Patientendetails" );
	if( getCurrentDiagnosis() != null ) {
	    oldDiagnosis = getCurrentDiagnosis();
	}
	setCurrentDiagnosis( null );

	final PatientModel model = new PatientModel( getCurrentPatient() );

	setPane( new PatientPresenter( model ) );
	currentAction = DETAILS;
    }


    public void showDiagnosisPane() {
	setTitle( "Diagnose" );
	setWindowTitle();
	setHeaderPanel( "Diagnosen" );

	setVisibleNavigatorButton( BSEARCH, true, false );
	setVisibleNavigatorButton( BNEW, true, false );
	setVisibleNavigatorButton( BPLIST, true, false );
	setVisibleNavigatorButton( BQUIT, true, false );
	setVisibleNavigatorButton( BDETAILS, true, false );
	setVisibleNavigatorButton( BDIAGNOSIS, true, true );
	setVisibleNavigatorButton( BMEDICATION, true, false );
	setVisibleNavigatorButton( BBILL, false, false );
	setVisibleNavigatorButton( BFINISH, true, false );
	setVisibleNavigatorButton( BABOUT, false, false );

	final DiagnosisModel model = new DiagnosisModel();

	Patient patient = getCurrentPatient();
	if( patient == null && oldPatient != null ) {

	    patient = oldPatient;
	}
	if( patient != null ) {
	    setCurrentPatient( patient );
	    model.setPatient( patient );

	    setPatientToNavigator( patient );
	    navigator.revalidate();
	    setPane( new DiagnosisPresenter( model ) );
	    currentAction = DIAGNOSIS;
	} else {
	    search();
	}

    }


    public void showMedicationPane( final TagesDiagnose diagnose ) {
	setTitle( "Verordnung" );
	setWindowTitle();
	setHeaderPanel( "Verordnung (" + new SimpleDateFormat( "dd.MM.yyyy" ).format( diagnose.getDatumAsDate() ) + ")" );

	setVisibleNavigatorButton( BSEARCH, true, false );
	setVisibleNavigatorButton( BNEW, true, false );
	setVisibleNavigatorButton( BPLIST, true, false );
	setVisibleNavigatorButton( BQUIT, true, false );
	setVisibleNavigatorButton( BDETAILS, true, false );
	setVisibleNavigatorButton( BDIAGNOSIS, true, false );
	setVisibleNavigatorButton( BMEDICATION, true, true );
	setVisibleNavigatorButton( BBILL, false, false );
	setVisibleNavigatorButton( BFINISH, true, false );
	setVisibleNavigatorButton( BABOUT, false, false );

	final MedicationModel model = new MedicationModel( diagnose );
	setPane( new MedicationPresenter( model ) );
	currentAction = MEDICATION;
    }


    public void showPatientListPane() {
	setHeaderPanel( "Patientenliste ausgeben" );
	setPatientToNavigator( null );

	setVisibleNavigatorButton( BSEARCH, true, false );
	setVisibleNavigatorButton( BNEW, true, false );
	setVisibleNavigatorButton( BPLIST, true, true );
	setVisibleNavigatorButton( BQUIT, true, false );
	setVisibleNavigatorButton( BDETAILS, false, false );
	setVisibleNavigatorButton( BDIAGNOSIS, false, false );
	setVisibleNavigatorButton( BMEDICATION, false, false );
	setVisibleNavigatorButton( BBILL, false, false );
	setVisibleNavigatorButton( BFINISH, true, false );
	setVisibleNavigatorButton( BABOUT, false, false );

	setWaitCursor();

	setTitle( "" );

	oldPatient = getCurrentPatient();
	setCurrentPatient( null );
	setCurrentDiagnosis( null );

	setPane( new PatientListPresenter() );
	currentAction = PATIENTLIST;

	setDefaultCursor();
    }
}