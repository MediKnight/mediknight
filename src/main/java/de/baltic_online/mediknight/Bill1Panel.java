package main.java.de.baltic_online.mediknight;

//import de.baltic_online.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import main.java.de.baltic_online.mediknight.domain.Rechnung;
import main.java.de.baltic_online.mediknight.domain.RechnungsPosten;
import main.java.de.baltic_online.mediknight.util.CurrencyNumber;
import main.java.de.baltic_online.mediknight.util.ErrorDisplay;
import main.java.de.baltic_online.mediknight.util.MediknightUtilities;
import main.java.de.baltic_online.mediknight.widgets.JButton;
import main.java.de.baltic_online.mediknight.widgets.JPanel;
import main.java.de.baltic_online.mediknight.widgets.JRadioButton;
import main.java.de.baltic_online.mediknight.widgets.JScrollPane;
import main.java.de.baltic_online.mediknight.widgets.JTable;


public class Bill1Panel extends JPanel implements ChangeListener, ListSelectionListener {

    class BillTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3832967955412119635L;

	final String[]		  columnNames	   = { "GebüH", "GoÄ", "Spezifikation", "Einzelpreis", "Anzahl" };

	public boolean[]	  columnsVisible   = new boolean[5];


	public BillTableModel() {
	    columnsVisible[0] = true;
	    columnsVisible[1] = false;
	    columnsVisible[2] = true;
	    columnsVisible[3] = true;
	    columnsVisible[4] = true;
	}


	@Override
	public int getColumnCount() {
	    int n = 0;

	    for( final boolean element : columnsVisible ) {
		if( element ) {
		    n++;
		}
	    }
	    return n;
	}


	@Override
	public String getColumnName( final int col ) {
	    return columnNames[getNumber( col )];
	}


	/**
	 * This functiun converts a column number in the table to the right number of the datas.
	 */
	protected int getNumber( final int col ) {
	    int n = col; // right number to return
	    int i = 0;
	    do {
		if( !columnsVisible[i] ) {
		    n++;
		}
		i++;
	    } while( i < n );
	    // If we are on an invisible column,
	    // we have to go one step further
	    while( !columnsVisible[n] ) {
		n++;
	    }
	    return n;
	}


	@Override
	public int getRowCount() {
	    if( entries != null ) {
		return entries.length;
	    } else {
		return 0;
	    }
	}


	public BillEntry getRowObject( final int row ) {
	    return entries[row];
	}


	@Override
	public Object getValueAt( final int row, final int column ) {
	    final NumberFormat nf = MediknightUtilities.getNumberFormat();
	    final RechnungsPosten entry = entries[row].getItem();

	    switch( column ) {
		case 0:
		    if( gebuehBtn.isSelected() ) {
			return entry.getGebueH();
		    } else {
			return entry.getGOAE();
		    }
		case 1:
		    return entry.getText();
		case 2:
		    final int currency = entry.isEuro() ? CurrencyNumber.EUR : CurrencyNumber.DM;
		    return new CurrencyNumber( entry.getPreis(), currency ).toCurrency( MediKnight.getApplication().getCurrency() ).toString();
		case 3:
		    return nf.format( entries[row].getCount() );
	    }
	    return null;
	}


	@Override
	public boolean isCellEditable( final int row, final int col ) {
	    return col >= 1;
	}


	@Override
	public void setValueAt( final Object o, final int row, final int col ) {

	    final NumberFormat nf = MediknightUtilities.getNumberFormat();
	    final RechnungsPosten rp = entries[row].getItem();
	    final boolean useEuro = MediKnight.getApplication().isEuro();

	    if( col == 1 ) {
		rp.setText( (String) o );
	    }
	    if( col == 2 ) {
		try {
		    final int defaultCurrency = useEuro ? CurrencyNumber.EUR : CurrencyNumber.DM;
		    final CurrencyNumber cn = CurrencyNumber.parse( o.toString(), defaultCurrency );
		    rp.setPreis( cn.doubleValue() );
		    rp.setEuro( cn.getCurrency() == CurrencyNumber.EUR );
		} catch( final IllegalArgumentException x ) {
		    x.printStackTrace();
		    rp.setPreis( 0.0 );
		    rp.setEuro( useEuro );
		}
	    }
	    if( col == 3 ) {
		try {
		    entries[row].setCount( nf.parse( o.toString() ).doubleValue() );
		} catch( final ParseException e ) {
		    e.printStackTrace();
		    /** TODO Exception */
		    entries[row].setCount( 1.0 );
		}
	    }
	    BillEntry.saveEntries( rechnung, entries );
	}
    }

    class ItemTableModel extends AbstractTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 3575803786544655240L;

	final String[]		  columnNames	   = { "GebüH", "GoÄ", "Spezifikation", "Einzelpreis" };

	RechnungsPosten[]	  items;


	public ItemTableModel( final RechnungsPosten[] items ) {
	    this.items = items;
	}


	@Override
	public int getColumnCount() {
	    return 4;
	}


	@Override
	public String getColumnName( final int col ) {
	    return columnNames[col];
	}


	@Override
	public int getRowCount() {
	    return items.length;
	}


	@Override
	public Object getValueAt( final int row, final int column ) {
	    final RechnungsPosten entry = items[row];

	    switch( column ) {
		case 0:
		    return entry.getGebueH();
		case 1:
		    return entry.getGOAE();
		case 2:
		    return entry.getText();
		case 3:
		    CurrencyNumber price = new CurrencyNumber( entry.getPreis(), CurrencyNumber.DM );

		    if( MediKnight.getApplication().isEuro() ) {
			price = price.toEuro();
		    }
		    return price.toString();
	    }

	    return null;
	}
    }

    private static final long serialVersionUID	 = 8050367950483181498L;

    public static final int   ITEM_PRICE_COLUMN	 = 2;
    public static final int   ITEM_AMOUNT_COLUMN = 3;
    BillPresenter	      presenter;
    Rechnung		      rechnung;

    BillEntry[]		      entries;
    final BillTableModel      billModel;
    final GridBagLayout	      gridBagLayout1;
    final JPanel	      headerPanel;
    final JLabel	      headerLbl;
    final FlowLayout	      flowLayout1;
    final JPanel	      splitPanePanel;
    final JPanel	      footerPanel;
    final JSplitPane	      mainSP;
    final GridBagLayout	      gridBagLayout2;
    final JPanel	      spTopPanel;
    final BorderLayout	      borderLayout1;
    final JPanel	      spTopBtnPanel;
    final JButton	      addBtn;
    final FlowLayout	      flowLayout3;
    final JPanel	      spBottomPanel;
    final JScrollPane	      billTableSP;
    final BorderLayout	      borderLayout2;
    final JPanel	      spBottomSouthPanel;
    final JButton	      deleteBtn;
    final JButton	      createBtn;
    final JScrollPane	      entriesTableSP;
    final JTable	      entriesTable;
    final JTable	      billTable;
    final BorderLayout	      borderLayout3;
    final JLabel	      sumLbl;
    final JPanel	      southPanel;
    final JPanel	      dummyPanel;
    final JButton	      macroBtn;
    final JButton	      page2Btn;
    final JPanel	      buttonPanel;
    final GridLayout	      gridLayout2;
    final FlowLayout	      flowLayout2;
    final JPanel	      spBottomSouthBtnPanel;
    final BorderLayout	      borderLayout4;
    final JPanel	      spBottomSouthFeePanel;
    final FlowLayout	      flowLayout4;
    final JRadioButton	      gebuehBtn;
    final JRadioButton	      goaeBtn;
    final ButtonGroup	      gebBg;
    final FlowLayout	      flowLayout5;
    Component	      component1;


    public Bill1Panel( final Rechnung rechnung ) {
	this.rechnung = rechnung;
	billModel = new BillTableModel();
	gridBagLayout1 = new GridBagLayout();
	headerPanel = new JPanel();
	headerLbl = new JLabel();
	flowLayout1 = new FlowLayout();
	splitPanePanel = new JPanel();
	footerPanel = new JPanel();
	mainSP = new JSplitPane();
	gridBagLayout2 = new GridBagLayout();
	spTopPanel = new JPanel();
	borderLayout1 = new BorderLayout();
	spTopBtnPanel = new JPanel();
	addBtn = new JButton();
	flowLayout3 = new FlowLayout();
	spBottomPanel = new JPanel();
	billTableSP = new JScrollPane();
	borderLayout2 = new BorderLayout();
	spBottomSouthPanel = new JPanel();
	deleteBtn = new JButton();
	createBtn = new JButton();
	entriesTableSP = new JScrollPane();
	entriesTable = new JTable();
	billTable = new JTable();
	borderLayout3 = new BorderLayout();
	sumLbl = new JLabel();
	southPanel = new JPanel();
	dummyPanel = new JPanel();
	macroBtn = new JButton();
	page2Btn = new JButton();
	buttonPanel = new JPanel();
	gridLayout2 = new GridLayout();
	flowLayout2 = new FlowLayout();
	spBottomSouthBtnPanel = new JPanel();
	borderLayout4 = new BorderLayout();
	spBottomSouthFeePanel = new JPanel();
	flowLayout4 = new FlowLayout();
	gebuehBtn = new JRadioButton();
	goaeBtn = new JRadioButton();
	gebBg = new ButtonGroup();
	flowLayout5 = new FlowLayout();

	jbInit();

	// korrigiert Fehler beim Ausdehnen der SplitPane, da Scroller
	// überdimensional groß
	mainSP.updateUI();
	boInit();

    }


    public void activate() {
	getRootPane().setDefaultButton( page2Btn );
    }


    void boInit() {
	entriesTable.setShowHorizontalLines( false );

	entriesTable.addMouseListener( new MouseAdapter() {

	    @Override
	    public void mousePressed( final MouseEvent e ) {
		if( e.getClickCount() == 2 ) {
		    presenter.addItem();
		}
	    }
	} );

	page2Btn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.showLetter();
	    }
	} );

	addBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.addItem();
	    }
	} );

	deleteBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.deleteEntry();
	    }
	} );

	createBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.createMacro();
	    }
	} );

	macroBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.showMacro();
	    }
	} );

	if( rechnung.isGoae() ) {
	    goaeBtn.setSelected( true );
	} else {
	    gebuehBtn.setSelected( true );
	}

    }


    private void calculateTotal() {
	final CurrencyNumber sum = new CurrencyNumber( 0.0, CurrencyNumber.EUR );
	for( final BillEntry entrie : entries ) {
	    final RechnungsPosten rp = entrie.getItem();
	    final int currency = rp.isEuro() ? CurrencyNumber.EUR : CurrencyNumber.DM;
	    /*
	     * CurrencyNumber cn = new CurrencyNumber(rp.getPreis()*entries[i].getCount (),currency).toEuro();
	     */

	    final CurrencyNumber cn = new CurrencyNumber( rp.getPreis() * entrie.getCount(), currency ).toCurrency( MediKnight.getApplication().getCurrency() )
		    .round( 2 );

	    sum.add( cn );
	}

	sumLbl.setText( "Gesamtsumme: " + sum );
    }


    JTable getBillTable() {
	return billTable;
    }


    public int getSelectedBillRow() {
	return billTable.getSelectedRow();
    }


    public int[] getSelectedBillRows() {
	return billTable.getSelectedRows();
    }


    public RechnungsPosten getSelectedItem() {
	try {
	    if( entriesTable.getSelectedRowCount() > 0 ) {
		return presenter.getModel().getRechnungsPosten()[entriesTable.getSelectedRow()];
	    } else {
		return null;
	    }
	} catch( final java.sql.SQLException e ) {
	    new ErrorDisplay( e, "Fehler beim Kopieren der Stammdaten!", "Fehler!", this );
	    return null;
	}

    }


    public BillEntry[] getSelectedItems() {
	final int[] rows = billTable.getSelectedRows();
	final BillEntry[] posten = new BillEntry[rows.length];
	for( int i = 0; i < rows.length; i++ ) {
	    posten[i] = ((BillTableModel) billTable.getModel()).getRowObject( rows[i] );
	}
	return posten;
    }


    public RechnungsPosten[] getSelectedRechnungsPostenItems() {
	RechnungsPosten[] posten;
	try {
	    final int[] rows = entriesTable.getSelectedRows();
	    posten = new RechnungsPosten[rows.length];
	    for( int i = 0; i < rows.length; ++i ) {
		posten[i] = presenter.getModel().getRechnungsPosten()[rows[i]];
	    }
	    return posten;
	} catch( final java.sql.SQLException e ) {
	    new ErrorDisplay( e, "Fehler beim Einlesen der Rechnungsposten!", "Fehler!", this );
	    return posten = null;
	}

    }


    public boolean isGOAE() {
	return goaeBtn.isSelected();
    }


    private void jbInit() {
	component1 = Box.createHorizontalStrut( 5 );
	this.setLayout( gridBagLayout1 );
	headerLbl.setText( "Seite 1/2: Rechnungsposten" );
	headerPanel.setLayout( flowLayout1 );
	flowLayout1.setAlignment( FlowLayout.LEFT );
	flowLayout1.setHgap( 0 );
	footerPanel.setLayout( borderLayout3 );
	splitPanePanel.setLayout( gridBagLayout2 );
	mainSP.setOrientation( JSplitPane.VERTICAL_SPLIT );
	mainSP.setMinimumSize( new Dimension( 218, 20 ) );
	mainSP.setOpaque( false );
	mainSP.setOneTouchExpandable( true );
	spTopPanel.setLayout( borderLayout1 );
	spTopPanel.setMinimumSize( new Dimension( 100, 100 ) );
	spTopPanel.setOpaque( false );
	addBtn.setEnabled( false );
	addBtn.setHorizontalAlignment( SwingConstants.RIGHT );
	addBtn.setText( "Hinzuf\u00FCgen" );
	spTopBtnPanel.setLayout( flowLayout3 );
	flowLayout3.setAlignment( FlowLayout.RIGHT );
	flowLayout3.setHgap( 0 );
	billTableSP.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	billTableSP.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	billTableSP.setOpaque( false );
	spBottomPanel.setLayout( borderLayout2 );
	deleteBtn.setEnabled( false );
	deleteBtn.setText( "Entfernen" );
	createBtn.setText( "Als Baustein..." );
	createBtn.setEnabled( false );
	spBottomSouthPanel.setLayout( borderLayout4 );
	entriesTableSP.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	entriesTableSP.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	sumLbl.setFont( new java.awt.Font( "Dialog", 1, 18 ) );
	sumLbl.setHorizontalAlignment( SwingConstants.CENTER );
	sumLbl.setText( "<summe>" );
	southPanel.setLayout( flowLayout2 );
	macroBtn.setText( "Baustein" );
	page2Btn.setText( "Seite 2 & Drucken" );
	buttonPanel.setLayout( gridLayout2 );
	flowLayout2.setAlignment( FlowLayout.RIGHT );
	flowLayout2.setHgap( 0 );
	flowLayout2.setVgap( 0 );
	gridLayout2.setHgap( 10 );
	spBottomSouthBtnPanel.setLayout( flowLayout5 );
	spBottomSouthFeePanel.setLayout( flowLayout4 );
	gebuehBtn.setOpaque( false );
	gebuehBtn.setText( "GebüH" );
	gebuehBtn.setActionCommand( "gebuehBtn" );
	goaeBtn.setOpaque( false );
	goaeBtn.setText( "GoÄ" );
	goaeBtn.setActionCommand( "goaeBtn" );
	this.setOpaque( false );
	headerPanel.setOpaque( false );
	splitPanePanel.setOpaque( false );
	spTopBtnPanel.setOpaque( false );
	spBottomPanel.setOpaque( false );
	spBottomSouthPanel.setOpaque( false );
	spBottomSouthBtnPanel.setOpaque( false );
	spBottomSouthFeePanel.setOpaque( false );
	footerPanel.setOpaque( false );
	southPanel.setOpaque( false );
	buttonPanel.setOpaque( false );
	dummyPanel.setOpaque( false );
	billTable.setOpaque( false );
	flowLayout5.setHgap( 0 );
	this.add( headerPanel,
		new GridBagConstraints( 0, 0, 3, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	headerPanel.add( headerLbl, null );
	this.add( splitPanePanel,
		new GridBagConstraints( 0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	splitPanePanel.add( mainSP,
		new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	mainSP.add( spTopPanel, JSplitPane.TOP );
	spTopPanel.add( spTopBtnPanel, BorderLayout.SOUTH );
	spTopBtnPanel.add( addBtn, null );
	spTopPanel.add( entriesTableSP, BorderLayout.CENTER );
	mainSP.add( spBottomPanel, JSplitPane.BOTTOM );
	spBottomPanel.add( billTableSP, BorderLayout.CENTER );
	spBottomPanel.add( spBottomSouthPanel, BorderLayout.SOUTH );
	spBottomSouthPanel.add( spBottomSouthBtnPanel, BorderLayout.EAST );
	spBottomSouthBtnPanel.add( createBtn, null );
	spBottomSouthBtnPanel.add( component1, null );
	spBottomSouthBtnPanel.add( deleteBtn, null );
	spBottomSouthPanel.add( spBottomSouthFeePanel, BorderLayout.WEST );
	spBottomSouthFeePanel.add( gebuehBtn, null );
	spBottomSouthFeePanel.add( goaeBtn, null );
	billTableSP.getViewport().add( billTable, null );
	entriesTableSP.getViewport().add( entriesTable, null );
	this.add( footerPanel,
		new GridBagConstraints( 1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	footerPanel.add( sumLbl, BorderLayout.WEST );
	footerPanel.add( southPanel, BorderLayout.SOUTH );
	southPanel.add( buttonPanel, null );
	buttonPanel.add( macroBtn, null );
	buttonPanel.add( page2Btn, null );
	footerPanel.add( dummyPanel, BorderLayout.CENTER );
	gebBg.add( gebuehBtn );
	gebBg.add( goaeBtn );
	mainSP.setDividerLocation( 200 );

	billTable.setModel( billModel );
	billTable.setShowGrid( true );
	billTable.setGridColor( getForeground() );
	entriesTable.setShowGrid( true );
	entriesTable.setGridColor( getForeground() );
    }


    private void setColumnView() {
	// billModel = new BillTableModel( );
	if( gebuehBtn.isSelected() ) {
	    billModel.columnsVisible[0] = true;
	    billModel.columnsVisible[1] = false;
	} else {
	    billModel.columnsVisible[0] = false;
	    billModel.columnsVisible[1] = true;
	}
	// billTable.setModel( billModel );
	billModel.fireTableDataChanged();
    }


    public void setPresenter( final BillPresenter presenter ) {
	if( this.presenter != null ) {
	    this.presenter.getModel().removeChangeListener( this );
	}

	this.presenter = presenter;
	presenter.getModel().addChangeListener( this );
	update();
    }


    @Override
    public void stateChanged( final ChangeEvent e ) {
	update();
    }


    protected void update() {

	entries = BillEntry.loadEntries( presenter.getModel().getRechnung() );
	setColumnView();

	billTable.getSelectionModel().addListSelectionListener( this );

	ItemTableModel itemModel;
	try {
	    itemModel = new ItemTableModel( presenter.getModel().getRechnungsPosten() );
	} catch( final java.sql.SQLException e ) {
	    new ErrorDisplay( e, "Fehler beim Einlesen der Rechnungsposten!", "Fehler!", this );
	    itemModel = new ItemTableModel( new RechnungsPosten[0] );
	}

	entriesTable.setModel( itemModel );
	entriesTable.getSelectionModel().addListSelectionListener( this );

	TableColumn column = entriesTable.getColumnModel().getColumn( 3 );
	column.setCellRenderer( MediknightUtilities.getTCRRight() );

	column = billTable.getColumnModel().getColumn( ITEM_PRICE_COLUMN );
	column.setCellRenderer( MediknightUtilities.getTCRRight() );

	column = billTable.getColumnModel().getColumn( ITEM_AMOUNT_COLUMN );
	column.setCellRenderer( MediknightUtilities.getTCRRight() );

	gebuehBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		// update();
		setColumnView();
	    }
	} );

	goaeBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		// update();
		setColumnView();
	    }
	} );

	calculateTotal();
	// sumLbl.setText( presenter.getModel().getRechnung());
    }


    /**
     * Called when the entriesTable's selection changes
     */
    @Override
    public void valueChanged( final ListSelectionEvent e ) {
	if( entriesTable.getSelectedRow() == -1 ) { // no row is selected
	    addBtn.setEnabled( false );
	} else {
	    addBtn.setEnabled( true );
	}

	if( billTable.getSelectedRow() == -1 ) { // no row is selected
	    deleteBtn.setEnabled( false );
	    createBtn.setEnabled( false );
	} else {
	    deleteBtn.setEnabled( true );
	    createBtn.setEnabled( true );
	}
    }
}
