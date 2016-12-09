package main.java.de.baltic_online.mediknight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.baltic_online.borm.TraceConstants;
import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;
import main.java.de.baltic_online.mediknight.util.ErrorDisplay;
import main.java.de.baltic_online.mediknight.util.MediKnightDayDiagnosisListModel;
import main.java.de.baltic_online.mediknight.widgets.JButton;
import main.java.de.baltic_online.mediknight.widgets.JPanel;
import main.java.de.baltic_online.mediknight.widgets.JScrollPane;
import main.java.de.baltic_online.mediknight.widgets.JTextArea;
import main.java.de.baltic_online.mediknight.widgets.JUndoButton;


public class DiagnosisPanel extends main.java.de.baltic_online.mediknight.widgets.JPanel implements ChangeListener, FocusListener, ActionListener {

    private static final long serialVersionUID		= 8967202476452623167L;

    DiagnosisPresenter	      presenter;

    final JSplitPane	      jSplitPane1;
    final JPanel	      jPanel1;
    final BorderLayout	      borderLayout2;
    final JPanel	      jPanel3;
    final BorderLayout	      borderLayout4;
    final JPanel	      pBottom;
    final BorderLayout	      borderLayout3;
    final JScrollPane	      jScrollPane1;
    final JPanel	      jPanel4;
    final BorderLayout	      borderLayout5;
    final JLabel	      jLabel1;
    final JLabel	      patientType;
    final JTextArea	      firstDiagnosis;
    final JLabel	      jLabel3;
    final JScrollPane	      sp_Entries;
    final JScrollPane	      sp_DayDiagnosis;
    final JPanel	      entriesPanel;
    final GridBagLayout	      gridBagLayout1;
    final JPanel	      jPanel2;
    final BorderLayout	      borderLayout1;
    final BoxLayout	      boxLayout21;

    Component		      lastFocusComponent;
    final JPanel	      jPanel5;
    final JUndoButton	      undoBtn;
    final BorderLayout	      borderLayout6;
    final JButton	      printBtn;

    final JPanel	      pnl_DayDiagnosisList;
    final JPanel	      pnl_DayDiagnosis;
    final JLabel	      lbl_DayDiagnosisList;
    final JLabel	      lbl_DayDiagnosis;
    final JList< String >     lst_DayDiagnosisList;
    final JTextArea	      ta_DayDiagnosis;
    List< TagesDiagnose >     tagesDiagnosen;

    final JPanel	      pnl_MainDiag;
    final JPanel	      pnl_DiagOptions;
    final JButton	      btn_Verschreibung;
    final JButton	      btn_Rechnung;

    private int		      selectedDayDiagnosisIndex	= -1;


    public DiagnosisPanel() {
	jSplitPane1 = new JSplitPane();
	jPanel1 = new JPanel();
	borderLayout2 = new BorderLayout();
	jPanel3 = new JPanel();
	borderLayout4 = new BorderLayout();
	pBottom = new JPanel();
	borderLayout3 = new BorderLayout();
	jScrollPane1 = new JScrollPane();
	jPanel4 = new JPanel();
	borderLayout5 = new BorderLayout();
	jLabel1 = new JLabel();
	patientType = new JLabel();
	firstDiagnosis = new JTextArea();
	jLabel3 = new JLabel();
	sp_Entries = new JScrollPane();
	sp_DayDiagnosis = new JScrollPane();
	entriesPanel = new JPanel();
	gridBagLayout1 = new GridBagLayout();
	jPanel2 = new JPanel();
	borderLayout1 = new BorderLayout();
	boxLayout21 = new BoxLayout( entriesPanel, BoxLayout.Y_AXIS );

	lastFocusComponent = null;
	jPanel5 = new JPanel();
	undoBtn = new JUndoButton();
	borderLayout6 = new BorderLayout();
	printBtn = new JButton();

	final BorderLayout lyt_MainLayout = new BorderLayout();
	final BorderLayout lyt_DayDiagnosisList = new BorderLayout();
	final BorderLayout lyt_DayDiagnosis = new BorderLayout();
	lyt_MainLayout.setHgap( MediKnight.LAYOUT_MIDDLE_SPACER );
	lyt_DayDiagnosisList.setVgap( MediKnight.LAYOUT_SMALL_SPACER );
	lyt_DayDiagnosis.setVgap( MediKnight.LAYOUT_SMALL_SPACER );

	pnl_DayDiagnosisList = new JPanel( lyt_DayDiagnosisList );
	pnl_DayDiagnosis = new JPanel( lyt_DayDiagnosis );
	lbl_DayDiagnosisList = new JLabel( "Datum:" );
	lbl_DayDiagnosis = new JLabel( "Diagnosetext:" );
	lst_DayDiagnosisList = new JList<>();

	ta_DayDiagnosis = new JTextArea( "" );
	pnl_DiagOptions = new JPanel();
	btn_Rechnung = new JButton( "Rechnung" );
	btn_Verschreibung = new JButton( "Verschreibung" );

	pnl_MainDiag = new JPanel( lyt_MainLayout );

	jbInit();
	addListeners();
    }


    /**
     * Hier wird das Event für die 'R' + 'V' - Buttons ausgeführt Um eine Diagnose für Verordnung bzw. Rechnung zu setzen, ist es im Moment nötig, sich den Opa
     * des gedrückten Knopfes zu holen.
     */
    @Override
    public void actionPerformed( final ActionEvent e ) {
	final JButton btn = (JButton) e.getSource();
	final Component c = ((Component) ((Component) e.getSource()).getParent()).getParent();

	int i = 0;
	for( i = 0; i < entriesPanel.getComponentCount(); i++ ) {
	    if( entriesPanel.getComponent( i ) == c ) {
		break;
	    }
	}
	try {
	    final TagesDiagnose td = presenter.getModel().getTagesDiagnosen().get( i );
	    MediKnight.getTracer().trace( TraceConstants.DEBUG, "Button for diagnosis " + td );
	    presenter.setSelectedDiagnose( td );
	} catch( final SQLException ex ) {
	    new ErrorDisplay( ex, "Fehler beim Einlesen der Tagesdiagnosen!", "Einlesen der Tagesdiagnose...", this );
	}

	if( btn.getText().equals( "R" ) ) {
	    presenter.showBill();
	} else if( btn.getText().equals( "V" ) ) {
	    presenter.showMedication();
	    /** @todo Date actionlistener **/
	}
    }


    public void activate() {
	// /** @todo I have to admit that this is totally braindead, but for the
	// moment it works. */
	//
	// if( presenter.getModel().getPatient().getErstDiagnose() != null &&
	// presenter.getModel().getPatient().getErstDiagnose().length() > 0 ) {
	// SwingUtilities.invokeLater( new Runnable() {
	// public void run() {
	// entriesPanel.scrollRectToVisible( currentPanel.getBounds() );
	// currentPanel.requestFocusForDescriptionTA();
	// }
	// } );
	// } else {
	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		firstDiagnosis.requestFocus();
	    }
	} );
	// }
    }


    private void addListeners() {
	btn_Verschreibung.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		final TagesDiagnose selectedDiagnosis = getSelectedDayDiagnosis();

		if( selectedDiagnosis != null ) {
		    MediKnight.getTracer().trace( TraceConstants.DEBUG, "Button for medication on diagnosis " + selectedDiagnosis );
		    presenter.setSelectedDiagnose( selectedDiagnosis );
		    presenter.showMedication();
		}
	    }

	} );

	btn_Rechnung.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		final TagesDiagnose selectedDiagnosis = getSelectedDayDiagnosis();

		if( selectedDiagnosis != null ) {
		    MediKnight.getTracer().trace( TraceConstants.DEBUG, "Button for bill on diagnosis " + selectedDiagnosis );
		    presenter.setSelectedDiagnose( selectedDiagnosis );
		    presenter.showBill();
		}
	    }

	} );

	lst_DayDiagnosisList.addListSelectionListener( new ListSelectionListener() {

	    @Override
	    public void valueChanged( ListSelectionEvent e ) {
		SwingUtilities.invokeLater( new Runnable() {

		    public void run() {
			final TagesDiagnose dayDiagnosis = getSelectedDayDiagnosis();

			if( dayDiagnosis != null ) {
			    ta_DayDiagnosis.setText( dayDiagnosis.getText() );
			}

			selectedDayDiagnosisIndex = lst_DayDiagnosisList.getSelectedIndex();
		    }
		} );

	    }
	} );

	ta_DayDiagnosis.addFocusListener( new FocusAdapter() {

	    public void focusLost( final FocusEvent evt ) {
		final int currentIndex = lst_DayDiagnosisList.getSelectedIndex();

		if( currentIndex > -1 ) {
		    saveDayDiagnosis( selectedDayDiagnosisIndex );
		}
	    }
	} );

    }


    private void boInit() {

	if( presenter.getModel().getPatient().isPrivatPatient() ) {
	    patientType.setText( "Privatpatient" );
	} else {
	    patientType.setText( "Kassenpatient" );
	}

	firstDiagnosis.addFocusListener( new FocusListener() {

	    @Override
	    public void focusGained( final FocusEvent e ) {
		// Muss das sein? Ich vermute mal nicht und ausserdem stoert es die Funktionalitaet der Knoepfe Verordnung und Diagnose.
		// presenter.setSelectedDiagnose(null);
		lastFocusComponent = firstDiagnosis;
	    }


	    @Override
	    public void focusLost( final FocusEvent e ) {
	    }
	} );

	printBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.printDiagnosis();
	    }
	} );

	btn_Rechnung.getActionMap().put( "rechnung", new AbstractAction() {

	    private static final long serialVersionUID = -6666575964516937837L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		btn_Rechnung.doClick();
	    }
	} );

	btn_Verschreibung.getActionMap().put( "verordnung", new AbstractAction() {

	    private static final long serialVersionUID = 9131246829561031709L;


	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		btn_Verschreibung.doClick();
	    }
	} );

	undoBtn.setName( "diagnosisUndoBtn" );
	undoBtn.setToolTipText( "" );
	undoBtn.setActionCommand( "Rückgängig" );
	undoBtn.setText( "Rückgängig" );
    }


    @Override
    public void focusGained( final FocusEvent e ) {
	final Component c = ((Component) e.getSource()).getParent();

	lastFocusComponent = c;

	int i = 0;
	for( i = 0; i < entriesPanel.getComponentCount(); i++ ) {
	    if( entriesPanel.getComponent( i ) == c ) {
		break;
	    }
	}

	try {
	    presenter.setSelectedDiagnose( presenter.getModel().getTagesDiagnosen().get( i ) );
	} catch( final SQLException ex ) {
	    new ErrorDisplay( ex, "Fehler beim Einlesen der Tagesdiagnosen!", "Einlesen der Tagesdiagnose...", this );
	}
    }


    @Override
    public void focusLost( final FocusEvent e ) {

    }


    public String getFirstDiagnose() {
	return firstDiagnosis.getText();
    }


    public Component getLastFocusComponent() {
	return lastFocusComponent;
    }


    // /**
    // * Return selected day diagnosis or null if none has been selected.
    // *
    // * @return
    // */
    // private TagesDiagnose getSelectedDayDiagnosis() {
    // if( !lst_DayDiagnosisList.isSelectionEmpty() ) {
    //
    // final LocalDate selectedDate = LocalDate.parse( lst_DayDiagnosisList.getSelectedValue(), DateTimeFormatter.ofLocalizedDate( FormatStyle.MEDIUM ) );
    // for( TagesDiagnose elem : tagesDiagnosen ) {
    // if( elem.getDatum().equals( selectedDate ) ) {
    // return elem;
    // }
    // }
    // }
    //
    // return null;
    // }

    private void jbInit() {
	this.setLayout( gridBagLayout1 );
	jSplitPane1.setOrientation( JSplitPane.VERTICAL_SPLIT );
	jSplitPane1.setOpaque( false );
	jSplitPane1.setOneTouchExpandable( true );
	jPanel1.setLayout( borderLayout2 );
	jPanel1.setOpaque( false );
	jPanel3.setLayout( borderLayout4 );
	pBottom.setLayout( borderLayout3 );
	jPanel4.setLayout( borderLayout5 );
	jLabel1.setForeground( Color.black );
	jLabel1.setText( "Dauerdiagnose                        " );
	patientType.setForeground( Color.black );
	patientType.setText( "            privat" );
	jScrollPane1.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	jScrollPane1.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	jScrollPane1.setOpaque( false );
	jPanel4.setOpaque( false );
	pBottom.setOpaque( false );
	jLabel3.setForeground( Color.black );
	jLabel3.setText( "Tagesdiagnosen" );
	this.setBorder( BorderFactory.createEmptyBorder() );
	sp_Entries.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	sp_Entries.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	sp_Entries.setOpaque( false );
	sp_Entries.setToolTipText( "" );
	sp_Entries.setResponsibleUndoHandler( "" );
	jPanel3.setOpaque( false );
	patientType.setForeground( Color.black );
	patientType.setText( "privat" );
	jPanel2.setLayout( borderLayout1 );
	borderLayout1.setHgap( 36 );
	// boxLayout21.setAxis(BoxLayout.Y_AXIS);
	firstDiagnosis.setResponsibleUndoHandler( "diagnosisUndoBtn" );
	jPanel2.setOpaque( false );
	jPanel5.setLayout( borderLayout6 );
	printBtn.setText( "Diagnosen drucken" );
	this.add( jSplitPane1,
		new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 1, 1, 1 ), 0, 0 ) );
	jSplitPane1.add( jPanel1, JSplitPane.TOP );
	jPanel1.add( jPanel3, BorderLayout.NORTH );
	jPanel3.add( jPanel4, BorderLayout.WEST );
	jPanel4.add( patientType, BorderLayout.EAST );
	jPanel4.add( jLabel1, BorderLayout.WEST );
	jPanel4.add( jPanel2, BorderLayout.EAST );
	// jPanel2.add(patientType, BorderLayout.EAST);
	jPanel1.add( jScrollPane1, BorderLayout.CENTER );
	jSplitPane1.add( pBottom, JSplitPane.BOTTOM );
	pBottom.add( jLabel3, BorderLayout.NORTH );

	this.add( jPanel5,
		new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 10, 0, 0, 0 ), 0, 0 ) );
	jPanel5.add( undoBtn, BorderLayout.WEST );
	jPanel5.add( printBtn, BorderLayout.EAST );
	jScrollPane1.getViewport().add( firstDiagnosis, null );
	jSplitPane1.setDividerLocation( 120 );

	final int dateItemLength = lst_DayDiagnosisList.getFontMetrics( lst_DayDiagnosisList.getFont() )
		.stringWidth( LocalDate.now().format( DateTimeFormatter.ofLocalizedDate( FormatStyle.MEDIUM ) ) );

	lst_DayDiagnosisList.setFixedCellWidth( dateItemLength + 20 );
	final DefaultListCellRenderer tmpRenderer = (DefaultListCellRenderer) lst_DayDiagnosisList.getCellRenderer();
	tmpRenderer.setHorizontalAlignment( SwingConstants.CENTER );

	ta_DayDiagnosis.setLineWrap( true );

	sp_Entries.getViewport().add( lst_DayDiagnosisList, null );
	sp_DayDiagnosis.getViewport().add( ta_DayDiagnosis, null );
	pnl_DayDiagnosisList.add( lbl_DayDiagnosisList, BorderLayout.NORTH );
	pnl_DayDiagnosisList.add( sp_Entries, BorderLayout.CENTER );
	pnl_DayDiagnosis.add( lbl_DayDiagnosis, BorderLayout.NORTH );
	pnl_DayDiagnosis.add( sp_DayDiagnosis, BorderLayout.CENTER );

	pnl_DiagOptions.setLayout( new FlowLayout( FlowLayout.TRAILING ) );

	pnl_DiagOptions.add( btn_Rechnung );
	pnl_DiagOptions.add( btn_Verschreibung );

	pnl_MainDiag.add( pnl_DiagOptions, BorderLayout.NORTH );
	pnl_MainDiag.add( pnl_DayDiagnosisList, BorderLayout.WEST );
	pnl_MainDiag.add( pnl_DayDiagnosis, BorderLayout.CENTER );

	pBottom.add( pnl_MainDiag, BorderLayout.CENTER );
    }


    public void setFirstDiagnosis( final String text ) {
	firstDiagnosis.setText( text );
	firstDiagnosis.setOriginalText( text );
    }


    public void setPresenter( final DiagnosisPresenter presenter ) {
	if( this.presenter != null ) {
	    this.presenter.getModel().removeChangeListener( this );
	}

	this.presenter = presenter;
	presenter.getModel().addChangeListener( this );
	boInit();
	update();

    }


    @Override
    public void stateChanged( final ChangeEvent e ) {
	update();
    }


    protected void update() {
	final DiagnosisModel model = presenter.getModel();

	setFirstDiagnosis( model.getPatient().getErstDiagnose() );
	LocalDate date = model.getPatient().getGeburtsDatum();
	if( date == null ) {
	    date = LocalDate.now();
	}

	patientType.setText( model.getPatient().isPrivatPatient() ? "privat" : "Kasse" );
	try {
	    tagesDiagnosen = model.getTagesDiagnosen();
	} catch( final SQLException e ) {
	    new ErrorDisplay( e, "Fehler beim Einlesen der Tagesdiagnosen!", "Tagesdiagnosen laden...", this );
	    tagesDiagnosen = null;
	}

	lst_DayDiagnosisList.setModel( new MediKnightDayDiagnosisListModel( tagesDiagnosen ) );
	lst_DayDiagnosisList.setSelectedIndex( 0 );
	selectedDayDiagnosisIndex = lst_DayDiagnosisList.getSelectedIndex();
    }


    private TagesDiagnose getSelectedDayDiagnosis() {
	return ((MediKnightDayDiagnosisListModel) lst_DayDiagnosisList.getModel()).getObjectAt( lst_DayDiagnosisList.getSelectedIndex() );
    }


    private TagesDiagnose getPreviousDayDiagnosis() {
	return ((MediKnightDayDiagnosisListModel) lst_DayDiagnosisList.getModel()).getObjectAt( selectedDayDiagnosisIndex );
    }


    private void saveDayDiagnosis( final int index ) {
	final TagesDiagnose dayDiagnosis = ((MediKnightDayDiagnosisListModel) lst_DayDiagnosisList.getModel()).getObjectAt( index );

	if( dayDiagnosis != null ) {
	    dayDiagnosis.setText( ta_DayDiagnosis.getText() );
	    try {
		dayDiagnosis.save();
	    } catch( final SQLException e ) {
		e.printStackTrace();
	    }
	}
    }
}
