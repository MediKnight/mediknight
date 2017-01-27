package main.java.de.baltic_online.mediknight;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.baltic_online.swing.FlexGridConstraints;
import de.baltic_online.swing.FlexGridLayout;
import main.java.de.baltic_online.mediknight.domain.Patient;
import main.java.de.baltic_online.mediknight.widgets.JButton;
import main.java.de.baltic_online.mediknight.widgets.JList;
import main.java.de.baltic_online.mediknight.widgets.JPanel;
import main.java.de.baltic_online.mediknight.widgets.JScrollPane;


public class SearchPanel extends JPanel implements ChangeListener {

    private static final long serialVersionUID = 1L;


    public static void main( final String[] args ) {
	final JFrame f = new JFrame();
	f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	f.getContentPane().setLayout( new FlowLayout() );
	f.getContentPane().add( new SearchPanel() );
	f.setVisible( true );
	f.pack();
    }

    SearchPresenter					  presenter;
    GridBagLayout					  gridBagLayout1    = new GridBagLayout();
    JPanel						  searchPanel	    = new JPanel();
    FlexGridLayout					  flexGridLayout1   = new FlexGridLayout();
    javax.swing.JTextField				  searchTF	    = new javax.swing.JTextField();
    JButton						  searchBtn	    = new JButton();
    JPanel						  buttonPanel	    = new JPanel();
    GridLayout						  gridLayout1	    = new GridLayout();
    JPanel						  jPanel1	    = new JPanel();
    JPanel						  jPanel2	    = new JPanel();
    BorderLayout					  borderLayout1	    = new BorderLayout();
    JScrollPane						  patientSP	    = new JScrollPane();
    JList< Patient >					  patientList	    = new JList< Patient >();
    JPanel						  jPanel4	    = new JPanel();
    JScrollPane						  jScrollPane1	    = new JScrollPane();
    JList< Patient >					  recentPatientList = new JList< Patient >();
    BorderLayout					  borderLayout2	    = new BorderLayout();
    FlowLayout						  flowLayout2	    = new FlowLayout();
    JButton						  clearHistoryBtn   = new JButton();
    JPanel						  jPanel7	    = new JPanel();
    GridLayout						  gridLayout5	    = new GridLayout();
    JPanel						  jPanel6	    = new JPanel();
    GridLayout						  gridLayout2	    = new GridLayout();
    JLabel						  patientListLbl    = new JLabel();
    JPanel						  jPanel5	    = new JPanel();
    JPanel						  lastPatientPanel  = new JPanel();
    JLabel						  lastPatientLbl    = new JLabel();
    GridLayout						  gridLayout4	    = new GridLayout();
    Border						  border1;
    Border						  border2;
    BorderLayout					  borderLayout3	    = new BorderLayout();
    GridLayout						  gridLayout6	    = new GridLayout();
    main.java.de.baltic_online.mediknight.widgets.JButton selectBtn	    = new JButton();

    Border						  border3;


    public SearchPanel() {
	jbInit();
	boInit();
    }


    private void boInit() {
	for( int i = 'A'; i <= 'Z'; i++ ) {
	    final char c = (char) i;

	    final JButton b = new JButton( new Character( c ).toString() );
	    b.addActionListener( new ActionListener() {

		@Override
		public void actionPerformed( final ActionEvent e ) {
		    presenter.indexButtonPressed( (JButton) e.getSource() );
		}
	    } );
	    b.setMargin( new Insets( 0, 2, 0, 2 ) );
	    b.setRequestFocusEnabled( false );
	    // b.setFocusable(false);
	    buttonPanel.add( b );
	}

	patientList.addListSelectionListener( new ListSelectionListener() {

	    @Override
	    public void valueChanged( final ListSelectionEvent e ) {
		presenter.clearSelection( recentPatientList );
		selectButtonEnabled();
	    }
	} );

	recentPatientList.addListSelectionListener( new ListSelectionListener() {

	    @Override
	    public void valueChanged( final ListSelectionEvent e ) {
		presenter.clearSelection( patientList );
		selectButtonEnabled();
	    }
	} );

	clearHistoryBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.clearHistory();
		clearHistoryBtn.setEnabled( false );
	    }
	} );

	searchBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.searchFor( searchTF.getText() );
	    }
	} );

	selectBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		new Thread() {

		    @Override
		    public void run() {
			presenter.patientSelected();
		    }
		}.start();
	    }
	} );

	final MouseAdapter listAdapter = new MouseAdapter() {

	    @Override
	    public void mousePressed( final MouseEvent e ) {
		if( e.getClickCount() == 2 ) {
		    presenter.patientSelected();
		}
	    }
	};

	patientList.addMouseListener( listAdapter );
	recentPatientList.addMouseListener( listAdapter );

	final KeyAdapter keyAdapter = new KeyAdapter() {

	    @Override
	    public void keyPressed( final KeyEvent e ) {
		if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
		    presenter.patientSelected();
		}
	    }
	};

	patientList.addKeyListener( keyAdapter );
	recentPatientList.addKeyListener( keyAdapter );

	searchTF.addKeyListener( new KeyListener() {

	    @Override
	    public void keyPressed( final KeyEvent e ) {
		if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
		    presenter.searchFor( searchTF.getText() );
		}
	    }


	    @Override
	    public void keyReleased( final KeyEvent e ) {
	    }


	    @Override
	    public void keyTyped( final KeyEvent e ) {
	    }
	} );

	searchTF.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.searchFor( searchTF.getText() );
	    }
	} );

	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		searchTF.requestFocus();
	    }
	} );

	if( recentPatientList.getComponentCount() < 1 ) {
	    clearHistoryBtn.setEnabled( false );
	}

	selectButtonEnabled();

	searchTF.addFocusListener( new FocusAdapter() {

	    @Override
	    public void focusGained( final FocusEvent e ) {
		getRootPane().setDefaultButton( searchBtn );
	    }
	} );

	final FocusListener l = new FocusAdapter() {

	    @Override
	    public void focusGained( final FocusEvent e ) {
		getRootPane().setDefaultButton( selectBtn );
	    }
	};

	recentPatientList.addFocusListener( l );
	patientList.addFocusListener( l );
    }


    public Object getSelection() {
	if( patientList.getSelectedValue() != null ) {
	    return patientList.getSelectedValue();
	} else {
	    return recentPatientList.getSelectedValue();
	}
    }


    private void jbInit() {
	border1 = BorderFactory.createEmptyBorder( 5, 0, 0, 0 );
	border2 = BorderFactory.createEmptyBorder( 5, 0, 0, 0 );
	border3 = BorderFactory.createEmptyBorder( 12, 0, 0, 0 );
	this.setLayout( gridBagLayout1 );
	searchPanel.setLayout( flexGridLayout1 );
	searchBtn.setText( "Suchen" );
	flexGridLayout1.setHgap( 5 );
	buttonPanel.setLayout( gridLayout1 );
	jPanel1.setLayout( borderLayout1 );
	jPanel2.setLayout( borderLayout3 );
	// patientSP.setFocusable(false);
	patientSP.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	patientSP.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	jPanel4.setLayout( borderLayout2 );
	jScrollPane1.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	jScrollPane1.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	recentPatientList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
	recentPatientList.setVisibleRowCount( 10 );
	gridLayout1.setVgap( 5 );
	flowLayout2.setAlignment( 0 );
	flowLayout2.setHgap( 0 );
	flowLayout2.setVgap( 0 );
	clearHistoryBtn.setText( "Liste zurücksetzen" );
	jPanel7.setLayout( gridLayout5 );
	jPanel6.setLayout( flowLayout2 );
	searchPanel.setOpaque( false );
	buttonPanel.setOpaque( false );
	jPanel1.setOpaque( false );
	jPanel2.setBorder( border3 );
	jPanel2.setOpaque( false );
	jPanel4.setOpaque( false );
	jPanel6.setOpaque( false );
	this.setOpaque( false );
	this.setRequestFocusEnabled( false );
	patientListLbl.setBorder( border1 );
	patientListLbl.setText( "Gefundene Patienten:" );
	jPanel5.setLayout( gridLayout2 );
	jPanel5.setOpaque( false );
	lastPatientPanel.setLayout( gridLayout4 );
	lastPatientPanel.setOpaque( false );
	lastPatientLbl.setBorder( border2 );
	lastPatientLbl.setText( "Zuletzt behandelte Patienten:" );
	gridLayout6.setHgap( 5 );
	selectBtn.setText( "Auswählen" );
	this.add( searchPanel,
		new GridBagConstraints( 0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	searchPanel.add( searchTF, new FlexGridConstraints( -1, 0, FlexGridConstraints.C ) );
	searchPanel.add( searchBtn, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	this.add( buttonPanel,
		new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 5, 0, 0, 0 ), 0, 0 ) );
	this.add( jPanel1, new GridBagConstraints( 0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	jPanel1.add( patientSP, BorderLayout.CENTER );
	jPanel1.add( jPanel5, BorderLayout.NORTH );
	jPanel5.add( patientListLbl, null );
	patientSP.getViewport().add( patientList, null );
	this.add( jPanel2,
		new GridBagConstraints( 0, 6, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	jPanel2.add( clearHistoryBtn, BorderLayout.WEST );
	jPanel2.add( selectBtn, BorderLayout.EAST );
	this.add( jPanel4, new GridBagConstraints( 0, 4, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	jPanel4.add( jScrollPane1, BorderLayout.CENTER );
	jPanel4.add( jPanel6, BorderLayout.SOUTH );
	jPanel6.add( jPanel7, null );
	jPanel4.add( lastPatientPanel, BorderLayout.NORTH );
	lastPatientPanel.add( lastPatientLbl, null );
	// jScrollPane1.setFocusable(false);
	jScrollPane1.getViewport().add( recentPatientList, null );
    }


    private void selectButtonEnabled() {
	if( recentPatientList.getSelectedIndex() == -1 && patientList.getSelectedIndex() == -1 ) {
	    selectBtn.setEnabled( false );
	} else {
	    selectBtn.setEnabled( true );
	}
    }


    public void setFocusOnPatientList() {
	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		patientList.setSelectedIndex( 0 );
		patientList.requestFocus();
	    }
	} );
    }


    public void setFocusOnSearchTF() {
	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		searchTF.requestFocus();
	    }
	} );
    }


    public void setPresenter( final SearchPresenter presenter ) {
	if( this.presenter != null ) {
	    this.presenter.getModel().removeChangeListener( this );
	}

	this.presenter = presenter;
	presenter.getModel().addChangeListener( this );

	recentPatientList.setListData( presenter.getModel().getRecentPatientsList().toArray( new Patient[0] ) );
	if( presenter.getModel().getRecentPatientsList().size() < 1 ) {
	    clearHistoryBtn.setEnabled( false );
	}
    }


    public void setSelectedTF() {
	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		Toolkit.getDefaultToolkit().beep();
		searchTF.selectAll();
	    }
	} );

    }


    @Override
    public void stateChanged( final ChangeEvent e ) {
	patientList.setListData( presenter.getModel().getFoundPatients().toArray( new Patient[0] ) );
	recentPatientList.setListData( presenter.getModel().getRecentPatientsList().toArray( new Patient[0] ) );
    }

    // class SearchFocusPolicy extends FocusTraversalPolicy {
    // /**
    // * @see java.awt.FocusTraversalPolicy#getComponentAfter(Container,
    // Component)
    // */
    // public Component getComponentAfter(
    // Container focusCycleRoot,
    // Component aComponent) {
    // return null;
    // }
    //
    // /**
    // * @see java.awt.FocusTraversalPolicy#getComponentBefore(Container,
    // Component)
    // */
    // public Component getComponentBefore(
    // Container focusCycleRoot,
    // Component aComponent) {
    // return null;
    // }
    //
    // /**
    // * @see java.awt.FocusTraversalPolicy#getDefaultComponent(Container)
    // */
    // public Component getDefaultComponent(Container focusCycleRoot) {
    // return searchTF;
    // }
    //
    // /**
    // * @see java.awt.FocusTraversalPolicy#getFirstComponent(Container)
    // */
    // public Component getFirstComponent(Container focusCycleRoot) {
    // return searchTF;
    // }
    //
    // /**
    // * @see java.awt.FocusTraversalPolicy#getLastComponent(Container)
    // */
    // public Component getLastComponent(Container focusCycleRoot) {
    // return recentPatientList;
    // }
    // }
}