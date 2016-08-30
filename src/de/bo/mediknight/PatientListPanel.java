package de.bo.mediknight;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import de.baltic_online.borm.Tracer;
import de.bo.mediknight.domain.Patient;
import de.bo.mediknight.widgets.JButton;
import de.bo.mediknight.widgets.JPanel;
import de.bo.mediknight.widgets.JScrollPane;
import de.bo.mediknight.widgets.JTable;


public class PatientListPanel extends JPanel {

    private static final long	     serialVersionUID = 1L;

    private PatientListPresenter	  presenter;

    private JScrollPane		   tableScrollpane;

    private JTable			patientTable;

    private JButton		       printButton;

    private JButton		       selectAll;

    private JButton		       deselectAll;

    private JTextField		    filternameField;

    private JButton		       searchButton;

    private JButton		       exportButton;

    private LinkedList< Vector< Object >> patients;


    public PatientListPanel() {
	createUI();
	connectUI();

	getData();
	updateTable();

	setVisible( true );
    }


    public void connectUI() {
	printButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		printAction();
	    }
	} );

	deselectAll.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		for( int i = 0; i < patients.size(); i++ ) {
		    patients.get( i ).set( 0, Boolean.FALSE );
		}
		updateTable();
	    }
	} );

	selectAll.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		for( int i = 0; i < patients.size(); i++ ) {
		    patients.get( i ).set( 0, Boolean.TRUE );
		}
		updateTable();
	    }
	} );

	searchButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		getData();
		updateTable();
	    }
	} );

	filternameField.addFocusListener( new FocusAdapter() {

	    @Override
	    public void focusGained( final FocusEvent e ) {
		getRootPane().setDefaultButton( searchButton );
	    }
	} );

	exportButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile( new File( "patientenliste.pdf" ) );

		final int state = fileChooser.showSaveDialog( MainFrame.getApplication() );

		if( state == JFileChooser.APPROVE_OPTION ) {
		    exportAction( fileChooser.getSelectedFile() );
		}
	    }
	} );
    }


    public void createUI() {
	this.setSize( this.getMaximumSize() );
	setLayout( new BorderLayout() );

	patientTable = new JTable() {

	    private static final long serialVersionUID = 1L;


	    @Override
	    public boolean isCellEditable( final int row, final int col ) {
		if( col == 0 ) {
		    return true;
		} else {
		    return false;
		}
	    }
	};

	patientTable.setModel( new DefaultTableModel() {

	    private static final long serialVersionUID = 1L;


	    @Override
	    public Class< ? > getColumnClass( final int columnIndex ) {
		if( columnIndex == 0 ) {
		    return Boolean.class;
		} else {
		    return String.class;
		}
	    }


	    @Override
	    public boolean isCellEditable( final int row, final int col ) {
		if( col > 0 ) {
		    return false;
		} else {
		    return true;
		}
	    }
	} );

	patientTable.setAutoResizeMode( javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS );

	tableScrollpane = new JScrollPane( patientTable );
	tableScrollpane.setSize( tableScrollpane.getMaximumSize() );

	// Suchleiste bauen
	searchButton = new JButton( "Suchen" );
	filternameField = new JTextField( 30 );

	final JPanel searchPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
	searchPanel.add( new JLabel( "Ausgabe einschränken:" ) );
	searchPanel.add( filternameField );
	searchPanel.add( searchButton );

	selectAll = new JButton( "Alle auswählen" );
	deselectAll = new JButton( "Alle abwählen" );

	exportButton = new JButton( "PDF speichern" );
	printButton = new JButton( "Liste drucken" );

	final JPanel bottomButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
	bottomButtonPanel.add( exportButton );
	bottomButtonPanel.add( printButton );

	final JPanel topButtonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
	topButtonPanel.add( selectAll );
	topButtonPanel.add( deselectAll );

	final JPanel controlPanel = new JPanel( new GridLayout( 3, 1, 0, 0 ) );
	controlPanel.add( topButtonPanel, BorderLayout.CENTER );
	controlPanel.add( bottomButtonPanel, BorderLayout.SOUTH );

	add( searchPanel, BorderLayout.NORTH );
	add( tableScrollpane, BorderLayout.CENTER );
	add( controlPanel, BorderLayout.SOUTH );
    }


    @SuppressWarnings( "unchecked" )
    private void exportAction( final File file ) {
	presenter.getModel().exportPdf( ((DefaultTableModel) patientTable.getModel()).getDataVector(), file );
    }


    public void getData() {
	patients = new LinkedList< Vector< Object >>();

	List< Patient > patientData;
	try {
	    patientData = Patient.retrieve( filternameField.getText() );
	    Collections.sort( patientData );

	    for( int p = 0; p < patientData.size(); p++ ) {
		final Vector< Object > v = new Vector< Object >();

		final Patient patient = patientData.get( p );

		v.add( new Boolean( false ) );

		v.add( patient.getAnrede() );
		v.add( patient.getVorname() );
		v.add( patient.getName() );

		String address = patient.getAdresse1();
		if( !patient.getAdresse2().equals( "" ) ) {
		    if( !address.equals( "" ) ) {
			address += ", ";
		    }
		    address += patient.getAdresse2();
		}
		if( !patient.getAdresse3().equals( "" ) ) {
		    if( !address.equals( "" ) ) {
			address += ", ";
		    }
		    address += patient.getAdresse3();
		}
		v.add( address );

		v.add( patient.getTelefonPrivat() );
		v.add( patient.getTelefonArbeit() );
		v.add( patient.getHandy() );

		v.add( patient.getFax() );
		v.add( patient.getEmail() );

		patients.addLast( v );
	    }
	} catch( final SQLException e ) {
	    Tracer.getDefaultTracer().trace( e );
	}
    }


    @Override
    public Dimension getMinimumSize() {
	return this.getMaximumSize();
    }


    @Override
    public Dimension getPreferredSize() {
	return this.getMaximumSize();
    }


    @SuppressWarnings( "unchecked" )
    private void printAction() {
	presenter.getModel().printList( ((DefaultTableModel) patientTable.getModel()).getDataVector() );
    }


    public void setPresenter( final PatientListPresenter presenter ) {
	this.presenter = presenter;
    }


    public void updateTable() {
	MainFrame.getApplication().setWaitCursor();

	// Tabellenueberschriften
	final DefaultTableModel tableModel = new DefaultTableModel() {

	    private static final long serialVersionUID = 1L;


	    @Override
	    public Class< ? > getColumnClass( final int columnIndex ) {
		if( columnIndex == 0 ) {
		    return Boolean.class;
		} else {
		    return String.class;
		}
	    }
	};

	final String[] cols = new String[] { "Ausgewählt", "Anrede", "Vorname", "Name", "Adresse", "Telefon (Privat)", "Telefon (Arbeit)", "Handy", "Fax",
		"Email" };

	tableModel.setColumnIdentifiers( cols );

	for( int i = 0; i < patients.size(); i++ ) {
	    tableModel.addRow( patients.get( i ) );
	}

	patientTable.setModel( tableModel );
	MainFrame.getApplication().setDefaultCursor();
    }
}
