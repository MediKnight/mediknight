package main.java.de.baltic_online.mediknight.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;

import main.java.de.baltic_online.mediknight.MediKnight;
import main.java.de.baltic_online.mediknight.MediKnight.Datasource;
import main.java.de.baltic_online.mediknight.widgets.JButton;
import main.java.de.baltic_online.mediknight.widgets.JComboBox;
import main.java.de.baltic_online.mediknight.widgets.JPanel;
import main.java.de.baltic_online.mediknight.widgets.JTextArea;


public class DatabaseSelectionDialog extends JDialog {

    private static final long	       serialVersionUID	= 1L;

    MediKnight.Datasource[]	       sources;

    JTextArea			       messageTA;

    JComboBox< MediKnight.Datasource > databaseCB;

    JButton			       connectButton;

    JButton			       quitButton;

    boolean			       cancelled	= true;


    public DatabaseSelectionDialog( final Datasource[] sources ) {
	super();
	this.sources = sources;
	setTitle( MediKnight.getProperties().getProperty( "name" ) + " - Datenbank wählen" );
	setModal( true );
	createUI();
	connectUI();
	configureUI();

	pack();
    }


    private void cancel() {
	cancelled = true;
	dispose();
    }


    private void configureUI() {
	final DefaultComboBoxModel< Datasource > model = new DefaultComboBoxModel< Datasource >();

	for( final Datasource source : sources ) {
	    model.addElement( source );
	}
	databaseCB.setModel( model );
	databaseCB.setSelectedIndex( 1 );
    }


    private void connect() {
	cancelled = false;
	dispose();
    }


    private void connectUI() {
	quitButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		cancel();
	    }
	} );

	connectButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		connect();
	    }
	} );

    }


    private void createUI() {
	final JPanel contentPanel = new JPanel( new BorderLayout() );
	contentPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
	final JPanel helperPanel = new JPanel( new GridBagLayout() );

	messageTA = new JTextArea();
	messageTA.setEditable( false );
	messageTA.setWrapStyleWord( true );
	messageTA.setLineWrap( true );
	messageTA.setOpaque( false );
	messageTA.setSize( new Dimension( 400, 1 ) );

	databaseCB = new JComboBox< MediKnight.Datasource >();
	connectButton = new JButton( "Verbinden" );
	quitButton = new JButton( "Beenden" );

	final JPanel buttonPanel = new JPanel( new GridLayout( 1, 0, 5, 0 ) );
	buttonPanel.add( connectButton );
	buttonPanel.add( quitButton );

	helperPanel.add( databaseCB, new GridBagConstraints( 0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
		new Insets( 6, 12, 24, 12 ), 0, 0 ) );

	helperPanel.add( buttonPanel,
		new GridBagConstraints( 0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );

	contentPanel.add( messageTA, BorderLayout.NORTH );
	contentPanel.add( helperPanel, BorderLayout.CENTER );

	setContentPane( contentPanel );
    }


    public Datasource getSelectedSource() {
	return (Datasource) databaseCB.getSelectedItem();
    }


    public boolean isCancelled() {
	return cancelled;
    }


    public void selectSource( final Datasource source ) {
	databaseCB.setSelectedItem( source );
    }


    public void showNoticeDatabaseNotAvailable( final Datasource source ) {
	messageTA.setText( "Die Datenbank '" + source + "' ist nicht " + "erreichbar. Bitte wählen Sie eine Datenbank mit einer "
		+ "aktuellen Sicherung der Hauptdatenbank aus.\n\n" + "Bitte beachten Sie, dass es zu Datenverlusten kommen kann, "
		+ "falls die Daten der gewählten Datenbank nicht hinreichend " + "aktuell sind." );
	pack();
    }


    public void showNoticeInitialDatabaseNotAvailable( final Datasource source ) {
	messageTA.setText(
		"Die als Standard konfigurierte Datenbank '" + source + "' konnte nicht " + "erreicht werden. Bitte wählen Sie eine Datenbank mit einer "
			+ "aktuellen Sicherung der Hauptdatenbank aus.\n\n" + "Bitte beachten Sie, dass es zu Datenverlusten kommen kann, "
			+ "falls die Daten der gewählten Datenbank nicht hinreichend " + "aktuell sind." );
	pack();
    }
}