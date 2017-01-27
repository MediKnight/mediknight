package main.java.de.baltic_online.mediknight;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import main.java.de.baltic_online.mediknight.domain.RechnungsGruppe;
import main.java.de.baltic_online.mediknight.domain.RechnungsPosten;
import main.java.de.baltic_online.mediknight.util.CurrencyNumber;
import main.java.de.baltic_online.mediknight.util.MediknightUtilities;
import main.java.de.baltic_online.mediknight.widgets.JButton;
import main.java.de.baltic_online.mediknight.widgets.JList;
import main.java.de.baltic_online.mediknight.widgets.JScrollPane;


public class MacroPanel extends JPanel implements ChangeListener, ListSelectionListener {

    class MacroTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -974915365204385546L;

	BillEntry[]		  entries;

	final String[]		  columnNames	   = { "GebüH", "GoÄ", "Spezifikation", "Einzelpreis", "Anzahl" };


	public MacroTableModel() {
	    entries = new BillEntry[0];
	}


	public MacroTableModel( final RechnungsGruppe macro ) {
	    entries = BillEntry.loadEntries( macro );
	}


	@Override
	public int getColumnCount() {
	    return 5;
	}


	@Override
	public String getColumnName( final int col ) {
	    return columnNames[col];
	}


	@Override
	public int getRowCount() {
	    return entries.length;
	}


	@Override
	public Object getValueAt( final int row, final int column ) {
	    final java.text.NumberFormat nf = MediknightUtilities.getNumberFormat();
	    final RechnungsPosten entry = entries[row].getItem();

	    switch( column ) {
		case 0:
		    return entry.getGebueH();
		case 1:
		    return entry.getGOAE();
		case 2:
		    return entry.getText();
		case 3:
		    return new CurrencyNumber( entry.getPreis(), CurrencyNumber.DM ).toCurrency( MediKnight.getApplication().getCurrency() ).toString();
		case 4:
		    // try {
		    // return
		    // MediknightUtilities.getNumberFormat().parse(String.valueOf(
		    // entries[row].getCount()));
		    // return ((NewAppWindow)
		    // AppWindow.getApplication()).getNumberFormat().parse(String.valueOf(
		    // entries[row].getCount()));
		    // } catch (java.text.ParseException e) {
		    // return new String("0"); /** @todo Better exception */
		    // }
		    return nf.format( entries[row].getCount() );
		// return );
	    }

	    return null;
	}
    }

    private static final long serialVersionUID = -6057708429859738892L;


    public static void main( final String[] args ) {
	final JFrame f = new JFrame();
	f.getContentPane().setLayout( new FlowLayout() );
	// f.getContentPane().add(new MacroPanel());
	f.setVisible( true );
	f.pack();
    }

    MacroPresenter	     presenter;

    BorderLayout	     borderLayout1 = new BorderLayout();

    JSplitPane		     jSplitPane1   = new JSplitPane();

    JScrollPane		     macroSP	   = new JScrollPane();

    JList< RechnungsGruppe > keyList	   = new JList< RechnungsGruppe >();

    JScrollPane		     itemTableSP   = new JScrollPane();

    JTable		     itemTable	   = new JTable();

    JPanel		     jPanel1	   = new JPanel();

    BorderLayout	     borderLayout2 = new BorderLayout();

    GridLayout		     gridLayout2   = new GridLayout();

    JPanel		     jPanel2	   = new JPanel();

    JButton		     applyBtn	   = new JButton();

    FlowLayout		     flowLayout1   = new FlowLayout();

    JPanel		     buttonPanel   = new JPanel();

    JPanel		     jPanel3	   = new JPanel();

    JButton		     deleteBtn	   = new JButton();

    FlowLayout		     flowLayout2   = new FlowLayout();


    public MacroPanel() {
	jbInit();
	boInit();
    }


    void boInit() {
	/* createBtn.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent e ) { presenter.createMacro(); } }); */

	deleteBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.deleteMacro();
	    }
	} );
	deleteBtn.setPreferredSize( applyBtn.getPreferredSize() );

	applyBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.addMacro();
	    }
	} );

	keyList.addListSelectionListener( this );

    }


    public RechnungsGruppe getSelectedRechnungsGruppe() {
	return keyList.getSelectedValue();
    }


    private void jbInit() {
	this.setLayout( borderLayout1 );
	itemTableSP.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	jSplitPane1.setOneTouchExpandable( true );
	keyList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
	this.setOpaque( false );
	jPanel1.setLayout( borderLayout2 );
	jPanel1.setOpaque( false );
	gridLayout2.setHgap( 10 );
	jPanel2.setOpaque( false );
	jPanel2.setLayout( gridLayout2 );
	applyBtn.setText( "übernehmen" );
	flowLayout1.setAlignment( FlowLayout.RIGHT );
	flowLayout1.setHgap( 0 );
	buttonPanel.setLayout( flowLayout1 );
	buttonPanel.setOpaque( false );
	jPanel3.setOpaque( false );
	jPanel3.setLayout( flowLayout2 );
	deleteBtn.setText( "löschen" );
	flowLayout2.setAlignment( FlowLayout.LEFT );
	flowLayout2.setHgap( 0 );
	macroSP.getViewport().add( keyList, null );
	this.add( jSplitPane1, BorderLayout.CENTER );
	jSplitPane1.add( macroSP, JSplitPane.LEFT );
	jSplitPane1.add( itemTableSP, JSplitPane.RIGHT );
	this.add( jPanel1, BorderLayout.SOUTH );
	jPanel1.add( buttonPanel, BorderLayout.EAST );
	buttonPanel.add( jPanel2, null );
	jPanel2.add( applyBtn, null );
	jPanel1.add( jPanel3, BorderLayout.WEST );
	jPanel3.add( deleteBtn, null );
	itemTableSP.getViewport().add( itemTable, null );
	jSplitPane1.setDividerLocation( 115 );
    }


    public void setFocusOnList() {
	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		if( !presenter.getModel().getComponentList().isEmpty() ) {
		    keyList.setSelectedIndex( 0 );
		}
		getRootPane().setDefaultButton( applyBtn );
	    }
	} );
    }


    public void setPresenter( final MacroPresenter presenter ) {
	if( this.presenter != null ) {
	    this.presenter.getModel().removeChangeListener( this );
	}

	this.presenter = presenter;
	presenter.getModel().addChangeListener( this );
	update();
    }


    private void sizingTable() {
	TableColumn column = itemTable.getColumnModel().getColumn( 3 );
	column.setCellRenderer( MediknightUtilities.getTCRRight() );

	column = itemTable.getColumnModel().getColumn( 4 );
	column.setCellRenderer( MediknightUtilities.getTCRRight() );

	itemTable.getColumnModel().getColumn( 2 ).setPreferredWidth( itemTable.getPreferredSize().width );
    }


    @Override
    public void stateChanged( final ChangeEvent e ) {
	update();
    }


    protected void update() {
	keyList.setListData( presenter.getModel().getComponentList().toArray( new RechnungsGruppe[0] ) );
	itemTable.setModel( new MacroTableModel() );
	deleteBtn.setEnabled( false );
	applyBtn.setEnabled( false );
	if( !presenter.getModel().hasContent() ) {
	    // createBtn.setEnabled(false);
	    sizingTable();
	}
    }


    @Override
    public void valueChanged( final ListSelectionEvent e ) {
	if( e.getValueIsAdjusting() ) {
	    return;
	}

	if( getSelectedRechnungsGruppe() == null ) {
	    itemTable.setModel( new MacroTableModel() );
	    deleteBtn.setEnabled( false );
	    applyBtn.setEnabled( false );
	} else {
	    final MacroTableModel model = new MacroTableModel( getSelectedRechnungsGruppe() );
	    itemTable.setModel( model );
	    deleteBtn.setEnabled( true );
	    applyBtn.setEnabled( true );
	}
	sizingTable();
    }
}