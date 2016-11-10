package main.java.de.baltic_online.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import main.java.de.baltic_online.mediknight.Commitable;
import main.java.de.baltic_online.mediknight.MediKnight;
import main.java.de.baltic_online.mediknight.MediknightTheme;
import main.java.de.baltic_online.mediknight.Presenter;
import main.java.de.baltic_online.mediknight.widgets.FlexGridLayout;
import main.java.de.baltic_online.mediknight.widgets.JGradientPanel;
import main.java.de.baltic_online.mediknight.widgets.JPanel;
import main.java.de.baltic_online.mediknight.widgets.LoginDialog;


public class MainTool extends JFrame {

    private static final long   serialVersionUID = 1L;

    private static JFrame       frame;
    private static final String BTN_PRINT	= "Drucktexte";
    private static final String BTN_DATA	 = "Rechnung";
    private static final String BTN_USER	 = "Anwender";
    private static final String BTN_MEDI	 = "Verordnung";
    private static final String BTN_QUIT	 = "Beenden";
    private static final String BTN_COLOR	= "Farbwerte";
    static Color		c1, c2;


    public static JFrame getFrame() {
	return frame;
    }


    public static void main( final String[] args ) {

	try {
	    MediKnight.initLocation();
	    MediKnight.initProperties();
	    MediKnight.initTracer();
	    MediKnight.initDB();
	    MediknightTheme.install( MediKnight.getProperties() );

	} catch( final Exception e ) {
	    e.printStackTrace();
	}

	final MainTool mt = new MainTool();
	try {
	    final LoginDialog ld = new LoginDialog( getFrame(), true );
	    ld.setVisible( true );
	    if( ld.getUser() == null ) {
		throw new IllegalAccessException( "Anwender hat sich nicht authentifiziert!" );
	    }
	} catch( final IllegalAccessException iax ) {
	    System.exit( 1 );
	}

	mt.setVisible( true );
    }

    JGradientPanel    buttonPanel;

    JToggleButton     printBtn      = new JToggleButton();
    JToggleButton     masterDataBtn = new JToggleButton();
    JToggleButton     userBtn       = new JToggleButton();
    JToggleButton     mediBtn       = new JToggleButton();
    JToggleButton     colorBtn      = new JToggleButton();
    JToggleButton     quitButton    = new JToggleButton();
    JPanel	    workspace     = new JPanel();

    JLabel	    title;

    private Presenter currentPresenter;


    public MainTool() {
	setTitle( "Mediknight - Administration" );
	setSize( 800, 600 );
	createUI();
	setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	frame = this;
    }


    private void colorSettings() {
	setVisibleNavigatorButton( BTN_DATA, true, false );
	setVisibleNavigatorButton( BTN_PRINT, true, false );
	setVisibleNavigatorButton( BTN_COLOR, true, true );
	setVisibleNavigatorButton( BTN_MEDI, true, false );
	setVisibleNavigatorButton( BTN_USER, true, false );
	setHeaderPanel( "Farbwerte" );
	setPane( new ColorPresenter() );
    }


    private JGradientPanel createButtonPanel() {
	buttonPanel = new JGradientPanel();
	buttonPanel.setLayout( new FlexGridLayout( 0, 1, 5, 5 ) );
	buttonPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
	buttonPanel.setGradientColor( c1 );
	buttonPanel.setBackground( c2 );

	printBtn.setText( BTN_PRINT );
	masterDataBtn.setText( BTN_DATA );
	userBtn.setText( BTN_USER );
	mediBtn.setText( BTN_MEDI );
	quitButton.setText( BTN_QUIT );
	colorBtn.setText( BTN_COLOR );

	printBtn.setName( BTN_PRINT );
	masterDataBtn.setName( BTN_DATA );
	userBtn.setName( BTN_USER );
	mediBtn.setName( BTN_MEDI );
	quitButton.setName( BTN_QUIT );
	colorBtn.setName( BTN_COLOR );

	printBtn.setMargin( new Insets( 2, 0, 2, 0 ) );
	masterDataBtn.setMargin( new Insets( 2, 0, 2, 0 ) );
	mediBtn.setMargin( new Insets( 2, 0, 2, 0 ) );
	userBtn.setMargin( new Insets( 2, 0, 2, 0 ) );
	quitButton.setMargin( new Insets( 2, 0, 2, 0 ) );
	colorBtn.setMargin( new Insets( 2, 0, 2, 0 ) );

	mediBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		medicationDataSettings();
	    }
	} );

	printBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		printSettings();
	    }
	} );

	colorBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		colorSettings();
	    }
	} );

	masterDataBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		masterDataSettings();
	    }
	} );

	userBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		userSettings();
	    }
	} );

	quitButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		quit();
	    }
	} );

	buttonPanel.add( printBtn, "10,0,c" );
	buttonPanel.add( masterDataBtn, "10,0,c" );
	buttonPanel.add( mediBtn, "10,0,c" );
	buttonPanel.add( userBtn, "10,0,c" );
	buttonPanel.add( colorBtn, "10,0,c" );
	buttonPanel.add( quitButton, "10,0,c" );

	return buttonPanel;
    }


    private void createUI() {
	c1 = UIManager.getColor( "effect" );
	if( c1 == null ) {
	    c1 = Color.black;
	}
	c2 = Color.white;

	setBackground( c2 );

	final Container pane = getContentPane();
	pane.setLayout( new GridBagLayout() );

	title = new JLabel( "Mediknight - Administration" );
	title.setBackground( c1 );
	title.setForeground( c2 );
	title.setOpaque( true );
	title.setFont( title.getFont().deriveFont( 20.0f ) );

	final JPanel filler = new JPanel();
	filler.setBackground( c1 );

	workspace = new JPanel( new BorderLayout() );
	workspace.setBackground( c2 );
	workspace.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

	final GridBagConstraints c = new GridBagConstraints();
	c.gridx = c.gridy = c.gridwidth = c.gridheight;
	c.fill = GridBagConstraints.BOTH;
	pane.add( filler, c );
	c.gridx++;
	pane.add( title, c );
	c.gridy++;
	c.weightx = c.weighty = 1d;
	pane.add( workspace, c );
	c.gridx--;
	c.weightx = 0d;
	c.fill = GridBagConstraints.VERTICAL;
	c.anchor = GridBagConstraints.NORTH;
	// pane.add(navigator, c);
	pane.add( createButtonPanel(), c );

    }


    private void masterDataSettings() {
	setVisibleNavigatorButton( BTN_DATA, true, true );
	setVisibleNavigatorButton( BTN_COLOR, true, false );
	setVisibleNavigatorButton( BTN_PRINT, true, false );
	setVisibleNavigatorButton( BTN_MEDI, true, false );
	setVisibleNavigatorButton( BTN_USER, true, false );
	setHeaderPanel( "Stammdatenpflege" );
	final MasterDataSupportModel model = new MasterDataSupportModel();
	setPane( new MasterDataSupportPresenter( model ) );
    }


    private void medicationDataSettings() {
	setVisibleNavigatorButton( BTN_DATA, true, false );
	setVisibleNavigatorButton( BTN_PRINT, true, false );
	setVisibleNavigatorButton( BTN_COLOR, true, false );
	setVisibleNavigatorButton( BTN_USER, true, false );
	setVisibleNavigatorButton( BTN_MEDI, true, true );
	setHeaderPanel( "Verodnungsstammdaten" );
	final MedicationSupportModel model = new MedicationSupportModel();
	setPane( new MedicationSupportPresenter( model ) );
    }


    private void printSettings() {
	setVisibleNavigatorButton( BTN_DATA, true, false );
	setVisibleNavigatorButton( BTN_PRINT, true, true );
	setVisibleNavigatorButton( BTN_COLOR, true, false );
	setVisibleNavigatorButton( BTN_MEDI, true, false );
	setVisibleNavigatorButton( BTN_USER, true, false );
	setHeaderPanel( "Druckbausteine" );
	final PrintSettingsModel model = new PrintSettingsModel();
	setPane( new PrintSettingsPresenter( model ) );
    }


    public void quit() {
	final int action = JOptionPane.showConfirmDialog( this, "Alle Daten wurden gespeichert.\nMöchten sie das Programm jetzt beenden?", "Mediknight",
		JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );

	if( action == JOptionPane.NO_OPTION ) {
	    setVisibleNavigatorButton( BTN_QUIT, true, false );
	    return;
	}

	if( currentPresenter instanceof Commitable ) {
	    ((Commitable) currentPresenter).commit();
	}

	System.exit( 0 );
    }


    private void setHeaderPanel( final String title ) {
	this.title.setText( title );
    }


    public void setPane( final Presenter presenter ) {
	/*
	 * if ( tracer != null ) tracer.trace(DEBUG,"calling setPane("+presenter. getClass().getName()+")");
	 */

	if( currentPresenter instanceof Commitable ) {
	    ((Commitable) currentPresenter).commit();
	}
	currentPresenter = presenter;
	/** @todo This is pretty stupid, but should work for now. Improve! */
	/*
	 * if( currentPresenter != null && currentPresenter.getClass() != presenter.getClass() ) { backAction = currentAction; }
	 */

	if( workspace.getComponentCount() > 0 ) {
	    final Component pane = workspace.getComponent( 0 );
	    workspace.remove( pane );
	}
	workspace.add( presenter.createView() );
	workspace.revalidate();
	workspace.repaint();

	// enableEditing(true);
    }


    public void setVisibleNavigatorButton( final String name, final boolean visible, final boolean selected ) {

	for( int i = 0; i < buttonPanel.getComponentCount(); i++ ) {
	    final Component c = buttonPanel.getComponent( i );

	    if( c.getName() != null && c.getName().equals( name ) ) {
		c.setVisible( visible );
		if( c instanceof JToggleButton ) {
		    ((JToggleButton) c).setSelected( selected );
		}
	    }
	    repaint();
	}
    }


    private void userSettings() {
	setVisibleNavigatorButton( BTN_DATA, true, false );
	setVisibleNavigatorButton( BTN_COLOR, true, false );
	setVisibleNavigatorButton( BTN_PRINT, true, false );
	setVisibleNavigatorButton( BTN_USER, true, true );
	setVisibleNavigatorButton( BTN_MEDI, true, false );
	setHeaderPanel( "Anwenderverwaltung" );
	final UserAdministrationModel model = new UserAdministrationModel();
	setPane( new UserAdministrationPresenter( model ) );
    }

}