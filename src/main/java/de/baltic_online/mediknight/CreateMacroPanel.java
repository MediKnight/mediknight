package main.java.de.baltic_online.mediknight;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.java.de.baltic_online.mediknight.domain.KnightObject;


public class CreateMacroPanel extends JPanel implements ChangeListener {

    /**
     *
     */
    private static final long serialVersionUID = 4555675996654488301L;
    GridBagLayout	      gridBagLayout1   = new GridBagLayout();
    JLabel		      nameLabel	       = new JLabel();
    JTextField		      nameTF	       = new JTextField();
    JPanel		      jPanel1	       = new JPanel();
    BorderLayout	      borderLayout1    = new BorderLayout();
    JPanel		      buttonPanel      = new JPanel();
    JButton		      createBtn	       = new JButton();
    GridLayout		      gridLayout1      = new GridLayout();
    JScrollPane		      jScrollPane1     = new JScrollPane();
    JList< KnightObject >     macroList	       = new JList< KnightObject >();

    CreateMacroPresenter      presenter;


    public CreateMacroPanel() {
	jbInit();
	boInit();
    }


    public void activate() {
	getRootPane().setDefaultButton( createBtn );
	nameTF.requestFocus();
    }


    private void boInit() {
	createBtn.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		presenter.createMacro();
	    }
	} );

	nameTF.getDocument().addDocumentListener( new DocumentListener() {

	    @Override
	    public void changedUpdate( final DocumentEvent e ) {
		enableButton();
	    }


	    void enableButton() {
		createBtn.setEnabled( nameTF.getText().trim().length() > 0 );
	    }


	    @Override
	    public void insertUpdate( final DocumentEvent e ) {
		enableButton();
	    }


	    @Override
	    public void removeUpdate( final DocumentEvent e ) {
		enableButton();
	    }
	} );

	macroList.addListSelectionListener( new ListSelectionListener() {

	    @Override
	    public void valueChanged( final ListSelectionEvent e ) {
		presenter.macroSelected();
	    }
	} );
    }


    public String getMacroName() {
	return nameTF.getText().trim();
    }


    public Object getSelectedMacro() {
	return macroList.getSelectedValue();
    }


    private void jbInit() {
	this.setLayout( gridBagLayout1 );
	nameLabel.setText( "Name des Bausteins:" );
	jPanel1.setLayout( borderLayout1 );
	createBtn.setText( "Anlegen" );
	createBtn.setEnabled( false );
	buttonPanel.setLayout( gridLayout1 );
	jScrollPane1.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
	this.add( nameLabel,
		new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 6 ), 0, 0 ) );
	this.add( nameTF,
		new GridBagConstraints( 1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
	this.add( jPanel1, new GridBagConstraints( 0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 12, 0, 0, 0 ), 0, 0 ) );
	jPanel1.add( buttonPanel, BorderLayout.EAST );
	buttonPanel.add( createBtn, null );
	this.add( jScrollPane1,
		new GridBagConstraints( 0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 12, 0 ), 0, 0 ) );
	jScrollPane1.getViewport().add( macroList, null );
    }


    public void setMacroName( final String name ) {
	nameTF.setText( name );
    }


    public void setPresenter( final CreateMacroPresenter presenter ) {
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


    public void update() {
	macroList.setListData( presenter.getModel().getComponentList().toArray( new KnightObject[1] ) );
    }
}