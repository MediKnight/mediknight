package main.java.de.baltic_online.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import main.java.de.baltic_online.mediknight.domain.VerordnungsPosten;
import main.java.de.baltic_online.mediknight.util.ErrorDisplay;
import main.java.de.baltic_online.mediknight.util.MediknightUtilities;
import de.baltic_online.swing.FlexGridConstraints;
import de.baltic_online.swing.FlexGridLayout;


public class MedicationSupportPanel extends JPanel implements ChangeListener, ListSelectionListener {

    private class AddDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	JPanel		    topPanel	 = new JPanel();

	JPanel		    downPanel	= new JPanel();

	JTextField		gruppeTF	 = new JTextField( 10 );

	JTextField		nummerTF	 = new JTextField( 10 );

	JTextField		nameTF	   = new JTextField( 20 );

	JScrollPane	       sp	       = new JScrollPane();

	JTextArea		 textTA	   = new JTextArea( 5, 20 );

	JButton		   cancelBtn	= new JButton( "Abbrechen" );

	JButton		   okBtn	    = new JButton( "Anlegen" );

	VerordnungsPosten	 vp;


	public AddDialog( final JFrame frame ) {
	    super( frame, "Verordnungsposten hinzufügen", true );
	    initializeComponents();
	    pack();
	    setResizable( false );

	    // setLocationRelativeTo(frame.getContentPane());
	    setVisible( true );

	}


	void initializeComponents() {
	    final Container pane = getContentPane();
	    pane.setLayout( new BorderLayout() );

	    final JPanel inputPanel = new JPanel();

	    final FlexGridLayout fgl = new FlexGridLayout( 4, 2 );
	    fgl.setHgap( 6 );
	    fgl.setVgap( 6 );

	    inputPanel.setLayout( fgl );

	    inputPanel.add( new JLabel( "Gruppe:" ), new FlexGridConstraints( FlexGridConstraints.PREFERRED, 0, FlexGridConstraints.W ) );
	    inputPanel.add( gruppeTF, new FlexGridConstraints( 0, 0, FlexGridConstraints.W ) );
	    inputPanel.add( new JLabel( "Nummer:" ), new FlexGridConstraints( FlexGridConstraints.PREFERRED, 0, FlexGridConstraints.W ) );
	    inputPanel.add( nummerTF, new FlexGridConstraints( 0, 0, FlexGridConstraints.W ) );
	    inputPanel.add( new JLabel( "Name:" ), new FlexGridConstraints( FlexGridConstraints.PREFERRED, 0, FlexGridConstraints.W ) );
	    inputPanel.add( nameTF, new FlexGridConstraints( 0, 0, FlexGridConstraints.W ) );
	    inputPanel.add( new JLabel( "Text:" ), new FlexGridConstraints( FlexGridConstraints.PREFERRED, 0, FlexGridConstraints.W ) );

	    sp.getViewport().add( textTA, null );
	    sp.setPreferredSize( textTA.getPreferredSize() );
	    sp.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	    sp.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	    inputPanel.add( sp, new FlexGridConstraints( FlexGridConstraints.FILL, FlexGridConstraints.FILL, FlexGridConstraints.W ) );

	    topPanel.add( inputPanel );
	    downPanel.setLayout( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
	    downPanel.add( okBtn );
	    downPanel.add( cancelBtn );

	    okBtn.setEnabled( false );

	    cancelBtn.addActionListener( new ActionListener() {

		@Override
		public void actionPerformed( final ActionEvent e ) {
		    dispose();
		}
	    } );

	    okBtn.addActionListener( new ActionListener() {

		@Override
		public void actionPerformed( final ActionEvent e ) {
		    saveEntry();

		}
	    } );

	    gruppeTF.addKeyListener( new KeyAdapter() {

		@Override
		public void keyReleased( final KeyEvent e ) {
		    if( gruppeTF.getText().length() < 1 || nummerTF.getText().length() < 1 ) {
			okBtn.setEnabled( false );
		    } else {
			okBtn.setEnabled( true );
			getRootPane().setDefaultButton( okBtn );
		    }
		}
	    } );

	    nummerTF.addKeyListener( new KeyAdapter() {

		@Override
		public void keyReleased( final KeyEvent e ) {
		    if( gruppeTF.getText().length() < 1 || nummerTF.getText().length() < 1 ) {
			okBtn.setEnabled( false );
		    } else {
			okBtn.setEnabled( true );
			getRootPane().setDefaultButton( okBtn );
		    }
		}
	    } );

	    pane.add( topPanel, BorderLayout.NORTH );
	    pane.add( downPanel, BorderLayout.SOUTH );

	    final Runnable runnable = new Runnable() {

		@Override
		public void run() {
		    gruppeTF.requestFocus();
		}
	    };
	    SwingUtilities.invokeLater( runnable );
	}


	private void saveEntry() {
	    vp = new VerordnungsPosten();
	    int gruppe;
	    int nummer;
	    try {

		final String g = gruppeTF.getText();
		final String n = nummerTF.getText();
		if( g.length() < 1 || n.length() < 1 ) {
		    return;
		}

		gruppe = Integer.parseInt( g );
		nummer = Integer.parseInt( n );
	    } catch( final NumberFormatException e ) {
		JOptionPane.showMessageDialog( this, "Gruppe und Nummer können nur als Zahl eingegeben werden!", "Fehlerhafte Eingabe...",
			JOptionPane.ERROR_MESSAGE );
		return;
	    }
	    final String name = nameTF.getText();
	    final String text = textTA.getText();

	    vp.setGruppe( gruppe );
	    vp.setNummer( nummer );
	    vp.setName( name );
	    vp.setText( text );
	    presenter.addItem( vp );
	    dispose();
	}
    }

    class MedicationTableModel extends AbstractTableModel {

	/**
         *
         */
	private static final long serialVersionUID = 1L;

	final String[]	    columnNames      = { "Gruppe", "Nummer", "Name" };

	VerordnungsPosten[]       items;


	public MedicationTableModel( final VerordnungsPosten[] items ) {
	    this.items = items;
	}


	@Override
	public int getColumnCount() {
	    return 3;
	}


	@Override
	public String getColumnName( final int col ) {
	    return columnNames[col];
	}


	@Override
	public int getRowCount() {
	    return items.length;
	}


	public VerordnungsPosten getRowObject( final int row ) {
	    return items[row];
	}


	@Override
	public Object getValueAt( final int row, final int column ) {
	    final VerordnungsPosten entry = items[row];

	    switch( column ) {
		case 0:
		    return new Integer( entry.getGruppe() );
		case 1:
		    return new Integer( entry.getNummer() );
		case 2:
		    return entry.getName();
	    }

	    return null;
	}


	@Override
	public boolean isCellEditable( final int row, final int col ) {
	    return true;
	}


	@Override
	public void setValueAt( final Object o, final int row, final int col ) {
	    final VerordnungsPosten p = items[row];
	    System.out.println( "Set Value:" );
	    switch( col ) {
		case 0:
		    p.setGruppe( Integer.parseInt( (String) o ) );
		    break;
		case 1:
		    p.setNummer( Integer.parseInt( (String) o ) );
		    System.out.println( (String) o );
		    break;
		case 2:
		    p.setName( (String) o );
		    break;

	    }

	    presenter.saveItem( p );
	}

    }

    private static final long  serialVersionUID    = 1L;

    // der index der verordnung, deren inhalt zur zeit im editor angezeigt wird.
    int			editIndex	   = -1;

    MedicationSupportPresenter presenter;

    BorderLayout	       borderLayout1       = new BorderLayout();

    JPanel		     topPanel	    = new JPanel();

    JScrollPane		itemTableSP	 = new JScrollPane();

    JTable		     itemTable	   = new JTable();

    JPanel		     downPanel	   = new JPanel();

    FlowLayout		 flowLayout1	 = new FlowLayout();

    JPanel		     buttonPanel	 = new JPanel();

    GridLayout		 gridLayout1	 = new GridLayout();

    JButton		    addBtn	      = new JButton();

    JButton		    deleteBtn	   = new JButton();

    JPanel		     centerPanel	 = new JPanel();

    JScrollPane		textAreaSP	  = new JScrollPane();

    JTextArea		  textTA	      = new JTextArea();

    BorderLayout	       borderLayout2       = new BorderLayout();

    BorderLayout	       borderLayout3       = new BorderLayout();

    JPanel		     centerPanelTopPanel = new JPanel();

    JLabel		     jLabel1	     = new JLabel();

    JPanel		     topPanelTopPanel    = new JPanel();

    FlowLayout		 flowLayout3	 = new FlowLayout();

    JLabel		     jLabel2	     = new JLabel();

    GridBagLayout	      gridBagLayout1      = new GridBagLayout();


    public MedicationSupportPanel() {
	jbInit();
    }


    public MedicationSupportPanel( final MedicationSupportPresenter presenter ) {
	jbInit();
	setPresenter( presenter );
    }


    private void boInit() {
	MedicationTableModel model;
	try {
	    model = new MedicationTableModel( presenter.getModel().getVerordnungsposten() );
	} catch( final java.sql.SQLException e ) {
	    new ErrorDisplay( e, "Fehler beim Einlesen der Rechnungsposten!", "Fehler!", this );
	    model = new MedicationTableModel( new VerordnungsPosten[0] );
	    e.printStackTrace();
	} catch( final NullPointerException ex ) {
	    model = new MedicationTableModel( new VerordnungsPosten[0] );
	    ex.printStackTrace();
	}
	initTable( model );

	addBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		new AddDialog( new JFrame() );
	    }
	} );

	deleteBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		deleteEntries();
	    }
	} );

	textTA.addFocusListener( new FocusListener() {

	    @Override
	    public void focusGained( final FocusEvent e ) {
	    }


	    @Override
	    public void focusLost( final FocusEvent e ) {
		saveText();
	    }
	} );

	deleteBtn.setEnabled( false );
	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		getRootPane().setDefaultButton( addBtn );
	    }
	} );
    }


    private void deleteEntries() {
	if( itemTable.getSelectedRowCount() > 0 ) {

	    final int r = JOptionPane.showConfirmDialog( getParent(), itemTable.getSelectedRowCount() > 1 ? itemTable.getSelectedRowCount()
		    + " Positionen wirklich löschen ?" : itemTable.getSelectedRowCount() + " Position wirklich löschen ?", "Stammdaten löschen",
		    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );

	    if( r == JOptionPane.YES_OPTION ) {
		presenter.deleteItem( itemTable.getSelectedRows() );
	    }
	}
    }


    private void initTable( final MedicationTableModel model ) {
	itemTable.setModel( model );
	itemTable.getSelectionModel().addListSelectionListener( this );
	itemTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

	itemTable.getColumnModel().getColumn( 2 ).setPreferredWidth( itemTable.getPreferredSize().width );

	TableColumn column = itemTable.getColumnModel().getColumn( 0 );
	column.setCellRenderer( MediknightUtilities.getTCRRight() );
	column = itemTable.getColumnModel().getColumn( 1 );
	column.setCellRenderer( MediknightUtilities.getTCRRight() );

    }


    private void jbInit() {
	this.setLayout( borderLayout1 );
	itemTableSP.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	itemTableSP.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	downPanel.setLayout( flowLayout1 );
	buttonPanel.setLayout( gridLayout1 );
	addBtn.setText( "Neu" );
	flowLayout1.setAlignment( 2 );
	flowLayout1.setHgap( 0 );
	deleteBtn.setText( "löschen" );
	gridLayout1.setHgap( 5 );
	topPanel.setLayout( borderLayout2 );
	centerPanel.setLayout( borderLayout3 );
	textAreaSP.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	textAreaSP.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	textAreaSP.setRequestFocusEnabled( false );
	jLabel1.setText( "Verordnungstext:" );
	centerPanelTopPanel.setLayout( gridBagLayout1 );
	textTA.setRows( 8 );
	topPanelTopPanel.setLayout( flowLayout3 );
	flowLayout3.setAlignment( 0 );
	flowLayout3.setHgap( 0 );
	flowLayout3.setVgap( 0 );
	jLabel2.setText( "Verordnungsstammdaten:" );
	this.setOpaque( false );
	topPanel.setOpaque( false );
	topPanel.setPreferredSize( new Dimension( 470, 200 ) );
	topPanelTopPanel.setOpaque( false );
	downPanel.setOpaque( false );
	buttonPanel.setOpaque( false );
	centerPanel.setOpaque( false );
	centerPanelTopPanel.setOpaque( false );
	this.add( topPanel, BorderLayout.NORTH );
	topPanel.add( itemTableSP, BorderLayout.CENTER );
	topPanel.add( topPanelTopPanel, BorderLayout.NORTH );
	topPanelTopPanel.add( jLabel2, null );
	this.add( downPanel, BorderLayout.SOUTH );
	downPanel.add( buttonPanel, null );
	buttonPanel.add( deleteBtn, null );
	buttonPanel.add( addBtn, null );
	this.add( centerPanel, BorderLayout.CENTER );
	centerPanel.add( textAreaSP, BorderLayout.CENTER );
	textAreaSP.getViewport().add( textTA, null );
	centerPanel.add( centerPanelTopPanel, BorderLayout.NORTH );
	centerPanelTopPanel.add( jLabel1, new GridBagConstraints( 0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 5,
		0, 0, 0 ), 0, 0 ) );
	itemTableSP.getViewport().add( itemTable, null );
    }


    public void saveText() {
	System.err.println( "saveText: " + editIndex );
	if( editIndex != -1 ) {
	    final VerordnungsPosten p = ((MedicationTableModel) itemTable.getModel()).getRowObject( editIndex );
	    p.setText( textTA.getText() );
	    presenter.saveItem( p );
	}
    }


    public void setPresenter( final MedicationSupportPresenter presenter ) {
	this.presenter = presenter;
	boInit();
    }


    @Override
    public void stateChanged( final ChangeEvent e ) {
	update();
    }


    protected void update() {
	try {
	    initTable( new MedicationTableModel( presenter.getModel().getVerordnungsposten() ) );
	} catch( final java.sql.SQLException e ) {
	    new ErrorDisplay( e, "Fehler beim Einlesen der Verordnungssposten!", "Fehler!", this );
	    e.printStackTrace();
	} catch( final NullPointerException ex ) {
	    ex.printStackTrace();
	}
    }


    @Override
    public void valueChanged( final ListSelectionEvent e ) {
	saveText();

	if( itemTable.getSelectedRow() != -1 ) {
	    textTA.setText( ((MedicationTableModel) itemTable.getModel()).getRowObject( itemTable.getSelectedRow() ).getText() );
	    deleteBtn.setEnabled( true );
	    editIndex = itemTable.getSelectedRow();
	} else {
	    textTA.setText( "" );
	    deleteBtn.setEnabled( false );
	    editIndex = -1;
	}
    }
}