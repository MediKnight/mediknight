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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import de.baltic_online.borm.TraceConstants;
import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;
import main.java.de.baltic_online.mediknight.util.DateChooserTableCellEditor;
import main.java.de.baltic_online.mediknight.util.DateTableCellRenderer;
import main.java.de.baltic_online.mediknight.util.ErrorDisplay;
import main.java.de.baltic_online.mediknight.util.MediKnightTableModel;
import main.java.de.baltic_online.mediknight.widgets.JButton;
import main.java.de.baltic_online.mediknight.widgets.JPanel;
import main.java.de.baltic_online.mediknight.widgets.JScrollPane;
import main.java.de.baltic_online.mediknight.widgets.JTable;
import main.java.de.baltic_online.mediknight.widgets.JTextArea;
import main.java.de.baltic_online.mediknight.widgets.JUndoButton;


public class DiagnosisPanel extends main.java.de.baltic_online.mediknight.widgets.JPanel implements ChangeListener, FocusListener, ActionListener {

    private static final long serialVersionUID	 = 8967202476452623167L;

    DiagnosisPresenter	      presenter;

    JSplitPane		      jSplitPane1	 = new JSplitPane();
    JPanel		      jPanel1		 = new JPanel();
    BorderLayout	      borderLayout2	 = new BorderLayout();
    JPanel		      jPanel3		 = new JPanel();
    BorderLayout	      borderLayout4	 = new BorderLayout();
    JPanel		      pBottom		 = new JPanel();
    BorderLayout	      borderLayout3	 = new BorderLayout();
    JScrollPane		      jScrollPane1	 = new JScrollPane();
    JPanel		      jPanel4		 = new JPanel();
    BorderLayout	      borderLayout5	 = new BorderLayout();
    JLabel		      jLabel1		 = new JLabel();
    JLabel		      patientType	 = new JLabel();
    JTextArea		      firstDiagnosis	 = new JTextArea();
    JLabel		      jLabel3		 = new JLabel();
    JScrollPane		      entriesSP		 = new JScrollPane();
    JPanel		      entriesPanel	 = new JPanel();
    GridBagLayout	      gridBagLayout1	 = new GridBagLayout();
    JPanel		      jPanel2		 = new JPanel();
    BorderLayout	      borderLayout1	 = new BorderLayout();

    // DayDiagnosisEntryPanel currentPanel;
    BoxLayout		      boxLayout21	 = new BoxLayout( entriesPanel, BoxLayout.Y_AXIS );

    Component		      lastFocusComponent = null;
    JPanel		      jPanel5		 = new JPanel();
    JUndoButton		      undoBtn		 = new JUndoButton();
    BorderLayout	      borderLayout6	 = new BorderLayout();
    JButton		      printBtn		 = new JButton();

    JTable		      tbl_DayDiagnosis;
    JPanel		      pnl_MainDiag;
    JPanel		      pnl_DiagOptions;
    JButton		      btn_Verschreibung;
    JButton		      btn_Rechnung;


    public DiagnosisPanel() {
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
	    MainFrame.getTracer().trace( TraceConstants.DEBUG, "Button for diagnosis " + td );
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


    public void activate() { // TODO: What's this good for?
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
		    MainFrame.getTracer().trace( TraceConstants.DEBUG, "Button for medication on diagnosis " + selectedDiagnosis );
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
		    MainFrame.getTracer().trace( TraceConstants.DEBUG, "Button for bill on diagnosis " + selectedDiagnosis );
		    presenter.setSelectedDiagnose( selectedDiagnosis );
		    presenter.showBill();
		}
	    }

	} );
	
	entriesSP.addMouseListener( new MouseAdapter(){
	    public void mouseClicked( final MouseEvent evt ) {
		int tableRow = tbl_DayDiagnosis.rowAtPoint( evt.getPoint() );
		System.out.println(  ":>>>>>>" );
		if( tableRow == -1 ) {
		    if( tbl_DayDiagnosis.isEditing() ){
			tbl_DayDiagnosis.getCellEditor().stopCellEditing();
		    }
		    tbl_DayDiagnosis.clearSelection();
		    System.out.println(  ": CLEARED" );
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
		// Muss das sein? Ich vermute mal nicht und ausserdem stoert es
		// die Funktionalitaet der Knoepfe Verordnung und Diagnose.
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


    /**
     * Return selected day diagnosis or null if none has been selected.
     * 
     * @return
     */
    private TagesDiagnose getSelectedDayDiagnosis() {
	final int selectedRow = tbl_DayDiagnosis.getSelectedRow();

	if( selectedRow > -1 ) {
	    final int modelRowIndex = tbl_DayDiagnosis.convertRowIndexToModel( selectedRow );

	    return ((MediKnightTableModel) tbl_DayDiagnosis.getModel()).getRowObject( modelRowIndex );
	} else {
	    return null;
	}
    }


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
	entriesSP.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	entriesSP.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	entriesSP.setOpaque( false );
	entriesSP.setToolTipText( "" );
	entriesSP.setResponsibleUndoHandler( "" );
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
	// entriesSP.getViewport().add(entriesPanel, null); // TODO Cleanup ?
	jScrollPane1.getViewport().add( firstDiagnosis, null );
	jSplitPane1.setDividerLocation( 120 );

	tbl_DayDiagnosis = new JTable();
	tbl_DayDiagnosis.getTableHeader().setReorderingAllowed( false );
	tbl_DayDiagnosis.setAutoResizeMode( JTable.AUTO_RESIZE_LAST_COLUMN );
	tbl_DayDiagnosis.setDefaultRenderer( Date.class, new DateTableCellRenderer( tbl_DayDiagnosis ) );
	tbl_DayDiagnosis.setDefaultEditor( Date.class, new DateChooserTableCellEditor( tbl_DayDiagnosis ) );
	tbl_DayDiagnosis.setAutoCreateRowSorter( true );	

	entriesSP.getViewport().add( tbl_DayDiagnosis, null );
	pnl_DiagOptions = new JPanel();
	pnl_DiagOptions.setLayout( new FlowLayout( FlowLayout.TRAILING ) );
	btn_Rechnung = new JButton( "Rechnung" );
	btn_Verschreibung = new JButton( "Verschreibung" );
	pnl_DiagOptions.add( btn_Rechnung );
	pnl_DiagOptions.add( btn_Verschreibung );

	pnl_MainDiag = new JPanel( new BorderLayout() );
	pnl_MainDiag.add( pnl_DiagOptions, BorderLayout.NORTH );
	pnl_MainDiag.add( entriesSP, BorderLayout.CENTER );

	pBottom.add( pnl_MainDiag, BorderLayout.CENTER );
    }


    /**
     * Adjust the day diagnosis table's column width such, that date column is just as large as needed and the text column uses the remaining space.
     */
    private void setDayDiagnosisTableColumnWidths() {
	final TableColumnModel datasetColumnModel = tbl_DayDiagnosis.getColumnModel();

	for( int column = 0; column < datasetColumnModel.getColumnCount() - 1; ++column ) {
	    final TableColumn currentColumn = datasetColumnModel.getColumn( column );
	    final int minColumnWidth = currentColumn.getMinWidth();
	    final int maxColumnWidth = currentColumn.getMaxWidth();
	    int newWidth = 0;

	    for( int row = 0; row < tbl_DayDiagnosis.getRowCount(); ++row ) {
		final TableCellRenderer renderer = tbl_DayDiagnosis.getCellRenderer( row, column );
		final Component comp = tbl_DayDiagnosis.prepareRenderer( renderer, row, column );
		final int preferredWidth = (int) (comp.getPreferredSize().getWidth() + tbl_DayDiagnosis.getIntercellSpacing().getWidth());

		newWidth = Integer.max( newWidth, Integer.max( preferredWidth, minColumnWidth ) );
		if( newWidth >= maxColumnWidth ) { 
		    newWidth = maxColumnWidth;
		}
	    }

	    currentColumn.setMinWidth( newWidth );
	    currentColumn.setMaxWidth( newWidth );
	    currentColumn.setPreferredWidth( newWidth );
	 
	}
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

	List< TagesDiagnose > tagesDiagnosen;
	try {
	    tagesDiagnosen = model.getTagesDiagnosen();
	} catch( final SQLException e ) {
	    new ErrorDisplay( e, "Fehler beim Einlesen der Tagesdiagnosen!", "Tagesdiagnosen laden...", this );
	    tagesDiagnosen = null;
	}

	tbl_DayDiagnosis.setModel( new MediKnightTableModel( tagesDiagnosen ) );
	setDayDiagnosisTableColumnWidths();
    }
}
