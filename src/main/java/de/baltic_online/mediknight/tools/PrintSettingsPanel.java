package main.java.de.baltic_online.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class PrintSettingsPanel extends JPanel implements ChangeListener {

    private static final long serialVersionUID	   = 1L;
    private static String     PRINT_SENDER	   = "print.sender";
    private static String     PRINT_LOGO	   = "print.logo";
    private static String     PRINT_FONT	   = "print.font";
    private static String     PRINT_BILL_FINAL	   = "print.bill.final";
    private static String     PRINT_MEDI_FINAL	   = "print.medication.final";

    PrintSettingsPresenter    presenter;

    JPanel		      logoPanel		   = new JPanel();
    GridBagLayout	      gridBagLayout1	   = new GridBagLayout();
    JTextArea		      logoTextArea	   = new JTextArea();
    BorderLayout	      borderLayout1	   = new BorderLayout();
    JLabel		      logoLabel		   = new JLabel();
    JTextField		      senderTextField	   = new JTextField();
    JLabel		      senderLabel	   = new JLabel();
    JPanel		      dummyPanel1	   = new JPanel();
    JPanel		      dummyPanel2	   = new JPanel();
    JTabbedPane		      specialContentPane   = new JTabbedPane();
    JPanel		      medicationPanel	   = new JPanel();
    BorderLayout	      borderLayout2	   = new BorderLayout();
    JPanel		      billPanel		   = new JPanel();
    GridBagLayout	      gridBagLayout2	   = new GridBagLayout();
    JScrollPane		      billFinalContentPane = new JScrollPane();
    JTextArea		      billFinalTextArea	   = new JTextArea();
    JPanel		      dummyPanel3	   = new JPanel();
    JPanel		      buttonPanel	   = new JPanel();
    JPanel		      buttonHelpPanel	   = new JPanel();
    FlowLayout		      flowLayout1	   = new FlowLayout();
    GridLayout		      gridLayout1	   = new GridLayout();
    JPanel		      dummyPanel	   = new JPanel();
    JPanel		      jPanel2		   = new JPanel();
    JLabel		      fontLabel		   = new JLabel();
    JComboBox< String >	      fontComboBox	   = new JComboBox< String >();
    FlowLayout		      flowLayout2	   = new FlowLayout();
    JScrollPane		      medicationFinalSP	   = new JScrollPane();
    JTextArea		      medicationFinalTA	   = new JTextArea();


    public PrintSettingsPanel( final PrintSettingsPresenter presenter ) {
	this.presenter = presenter;
	jbInit();
	boInit();
    }


    private void boInit() {
	fontComboBox.addItem( "Arial" );
	fontComboBox.addItem( "Times" );

	update();

	logoTextArea.addFocusListener( new FocusListener() {

	    @Override
	    public void focusGained( final FocusEvent e ) {
	    }


	    @Override
	    public void focusLost( final FocusEvent e ) {
		saveEntries();
	    }
	} );

	senderTextField.addFocusListener( new FocusListener() {

	    @Override
	    public void focusGained( final FocusEvent e ) {
	    }


	    @Override
	    public void focusLost( final FocusEvent e ) {
		saveEntries();
	    }
	} );

	billFinalTextArea.addFocusListener( new FocusListener() {

	    @Override
	    public void focusGained( final FocusEvent e ) {
	    }


	    @Override
	    public void focusLost( final FocusEvent e ) {
		saveEntries();
	    }
	} );

	medicationFinalTA.addFocusListener( new FocusListener() {

	    @Override
	    public void focusGained( final FocusEvent e ) {
	    }


	    @Override
	    public void focusLost( final FocusEvent e ) {
		saveEntries();
	    }
	} );

	fontComboBox.addItemListener( new ItemListener() {

	    @Override
	    public void itemStateChanged( final ItemEvent e ) {
		saveEntries();
		update();
	    }
	} );
    }


    public String getBillFinal() {
	return billFinalTextArea.getText();
    }


    public String getComboFonts() {
	return fontComboBox.getSelectedItem().toString();
    }


    public String getLogo() {
	return logoTextArea.getText();
    }


    public String getMedicationFinal() {
	return medicationFinalTA.getText();
    }


    public String getSender() {
	return senderTextField.getText();
    }


    private void jbInit() {
	this.setLayout( gridBagLayout1 );
	logoTextArea.setColumns( 35 );
	logoTextArea.setRows( 9 );
	logoTextArea.setBorder( BorderFactory.createLineBorder( Color.black ) );
	logoPanel.setLayout( borderLayout1 );
	logoLabel.setText( "Logoinhalt:" );
	senderLabel.setText( "Absenderzeile:" );
	medicationPanel.setLayout( borderLayout2 );
	billPanel.setLayout( gridBagLayout2 );
	billFinalContentPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	billFinalContentPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	billFinalTextArea.setColumns( 40 );
	billFinalTextArea.setRows( 5 );
	buttonPanel.setLayout( flowLayout1 );
	flowLayout1.setAlignment( FlowLayout.RIGHT );
	flowLayout1.setHgap( 0 );
	flowLayout1.setVgap( 0 );
	buttonHelpPanel.setLayout( gridLayout1 );
	gridLayout1.setHgap( 5 );
	gridLayout1.setVgap( 5 );
	fontLabel.setText( "Schriftart:" );
	jPanel2.setLayout( flowLayout2 );
	flowLayout2.setAlignment( FlowLayout.LEFT );
	flowLayout2.setHgap( 0 );
	flowLayout2.setVgap( 0 );
	medicationFinalSP.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	medicationFinalSP.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	this.setOpaque( false );
	logoPanel.setOpaque( false );
	dummyPanel2.setOpaque( false );
	dummyPanel1.setOpaque( false );
	dummyPanel.setOpaque( false );
	dummyPanel.setToolTipText( "" );
	dummyPanel3.setOpaque( false );
	this.add( logoPanel,
		new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	logoPanel.add( logoTextArea, BorderLayout.WEST );
	this.add( senderTextField,
		new GridBagConstraints( 1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	this.add( senderLabel,
		new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 5 ), 0, 0 ) );
	this.add( dummyPanel1,
		new GridBagConstraints( 0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	this.add( dummyPanel2,
		new GridBagConstraints( 0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	this.add( specialContentPane,
		new GridBagConstraints( 0, 6, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	specialContentPane.add( medicationPanel, "Verordnung" );
	medicationPanel.add( medicationFinalSP, BorderLayout.CENTER );
	medicationFinalSP.getViewport().add( medicationFinalTA, null );
	specialContentPane.add( billPanel, "Rechnung" );
	billPanel.add( billFinalContentPane,
		new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	this.add( dummyPanel3,
		new GridBagConstraints( 0, 7, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	this.add( buttonPanel,
		new GridBagConstraints( 0, 8, 4, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 0, 0, 5, 0 ), 0, 0 ) );
	buttonPanel.add( buttonHelpPanel, null );
	this.add( dummyPanel,
		new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	this.add( jPanel2, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	jPanel2.add( fontComboBox, null );
	this.add( logoLabel,
		new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 5 ), 0, 0 ) );
	this.add( fontLabel, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 5 ), 0, 0 ) );
	billFinalContentPane.getViewport().add( billFinalTextArea, null );
    }


    public void saveEntries() {
	presenter.getModel().alterMap( PRINT_FONT, (String) fontComboBox.getSelectedItem() );
	presenter.getModel().alterMap( PRINT_BILL_FINAL, billFinalTextArea.getText() );
	presenter.getModel().alterMap( PRINT_LOGO, logoTextArea.getText() );
	presenter.getModel().alterMap( PRINT_SENDER, senderTextField.getText() );
	presenter.getModel().alterMap( PRINT_MEDI_FINAL, medicationFinalTA.getText() );
    }


    public void setBillFinal( final String billFinal ) {
	billFinalTextArea.setText( billFinal );
    }


    public void setComboFonts( final String font ) {
	if( fontComboBox.getSelectedItem() != font ) {
	    fontComboBox.setSelectedItem( font );
	}
    }


    private void setFonts( final String font ) {
	String fontname = "Dialog";
	if( font.equals( "Times" ) ) {
	    fontname = "Times New Roman";
	} else if( font.equals( "Arial" ) ) {
	    fontname = "Arial";
	}

	billFinalTextArea.setFont( new Font( fontname, 0, 12 ) );
	logoTextArea.setFont( new Font( fontname, 0, 12 ) );
	medicationFinalTA.setFont( new Font( fontname, 0, 12 ) );
	senderTextField.setFont( new Font( fontname, 0, 12 ) );
    }


    public void setLogo( final String logo ) {
	logoTextArea.setText( logo );
    }


    public void setMedicationFinal( final String medi ) {
	medicationFinalTA.setText( medi );
    }


    public void setSender( final String sender ) {
	senderTextField.setText( sender );
    }


    @Override
    public void stateChanged( final ChangeEvent e ) {
	update();
    }


    private void update() {
	final Map< String, String > map = presenter.getModel().getMap();
	setFonts( map.get( PRINT_FONT ).toString() );
	setLogo( map.get( PRINT_LOGO ).toString() );
	setSender( map.get( PRINT_SENDER ).toString() );
	setComboFonts( map.get( PRINT_FONT ).toString() );
	setBillFinal( map.get( PRINT_BILL_FINAL ).toString() );
	setMedicationFinal( map.get( PRINT_MEDI_FINAL ).toString() );

    }

}