package de.bo.mediknight;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class CreateMacroPanel extends JPanel implements ChangeListener {
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel nameLabel = new JLabel();
    JTextField nameTF = new JTextField();
    JPanel jPanel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel buttonPanel = new JPanel();
    JButton createBtn = new JButton();
    GridLayout gridLayout1 = new GridLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JList macroList = new JList();

    CreateMacroPresenter presenter;

    public CreateMacroPanel() {
        jbInit();
        boInit();
    }


    public void setMacroName( String name ) {
        nameTF.setText( name );
    }

    public String getMacroName() {
        return nameTF.getText().trim();
    }

    public Object getSelectedMacro() {
        return macroList.getSelectedValue();
    }

    public void setPresenter( CreateMacroPresenter presenter ) {
        if( this.presenter != null ) {
            this.presenter.getModel().removeChangeListener( this );
        }

        this.presenter = presenter;
        presenter.getModel().addChangeListener( this );
        update();
    }


    public void update() {
        macroList.setListData( presenter.getModel().getComponentList().toArray() );
    }

    public void stateChanged( ChangeEvent e ) {
        update();
    }

    public void activate() {
        getRootPane().setDefaultButton( createBtn );
        nameTF.requestFocus();
    }

    private void boInit() {
        createBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                presenter.createMacro();
            }
        });

        nameTF.getDocument().addDocumentListener( new DocumentListener() {
            public void insertUpdate( DocumentEvent e ) { enableButton(); }
            public void removeUpdate( DocumentEvent e ) { enableButton(); }
            public void changedUpdate( DocumentEvent e ) { enableButton(); }
            void enableButton() {
                createBtn.setEnabled( nameTF.getText().trim().length() > 0 );
            }
        });

        macroList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                presenter.macroSelected();
            }
        });
    }

    private void jbInit() {
        this.setLayout(gridBagLayout1);
        nameLabel.setText("Name des Bausteins:");
        jPanel1.setLayout(borderLayout1);
        createBtn.setText("Anlegen");
        createBtn.setEnabled( false );
        buttonPanel.setLayout(gridLayout1);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(nameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
        this.add(nameTF, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(jPanel1, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(12, 0, 0, 0), 0, 0));
        jPanel1.add(buttonPanel, BorderLayout.EAST);
        buttonPanel.add(createBtn, null);
        this.add(jScrollPane1, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 12, 0), 0, 0));
        jScrollPane1.getViewport().add(macroList, null);
    }
}