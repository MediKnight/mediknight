package de.bo.mediknight;

import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import de.bo.swing.FlexGridConstraints;
import de.bo.swing.FlexGridLayout;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import de.bo.mediknight.widgets.*;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.MediknightUtilities;
import java.util.Date;

public class PatientPanel extends JPanel implements ChangeListener {
    PatientPresenter presenter;

    JPanel panel0 = new JPanel();
    GridLayout gridLayout1 = new GridLayout();
    JPanel jPanel1 = new JPanel();
    JLabel adress3Lbl = new JLabel();
    JComboBox anredeCB = new JComboBox();
    JLabel adress2Lbl = new JLabel();
    JTextField adress1TF = new JTextField();
    JLabel nachnameLbl = new JLabel();
    JTextField nachnameTF = new JTextField();
    JLabel vornameLbl = new JLabel();
    FlexGridLayout flexGridLayout1 = new FlexGridLayout();
    JTextField adress2TF = new JTextField();
    JTextField vornameTF = new JTextField();
    JLabel adress1Lbl = new JLabel();
    JLabel titelLbl = new JLabel();
    JTextField adress3TF = new JTextField();
    JLabel anredeLbl = new JLabel();
    JTextField titelTF = new JTextField();
    JLabel dummy2Lbl = new JLabel();
    JTextField telBerufTF = new JTextField();
    JLabel emailLbl = new JLabel();
    JTextField telPrivatTF = new JTextField();
    JLabel dummy1Lbl = new JLabel();
    JLabel handyLbl = new JLabel();
    JLabel faxLbl = new JLabel();
    FlexGridLayout flexGridLayout2 = new FlexGridLayout();
    JTextField handyTF = new JTextField();
    JTextField faxTF = new JTextField();
    JTextField emailTF = new JTextField();
    JLabel telBerufLbl = new JLabel();
    JLabel telPrivatLbl = new JLabel();
    JPanel jPanel4 = new JPanel();
    JLabel gebLbl = new JLabel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JPanel panel1 = new JPanel();
    ButtonGroup kkBtnGroup = new ButtonGroup();
    JRadioButton finanzamtPrivatBtn = new JRadioButton();
    JRadioButton privatBtn = new JRadioButton();
    JLabel kkLabel = new JLabel();
    JPanel panel3 = new JPanel();
    JScrollPane jScrollPane1 = new JScrollPane();
    JPanel panel2 = new JPanel();
    JLabel achtungLbl = new JLabel();
    GridLayout gridLayout2 = new GridLayout();
    JTextArea achtungTA = new JTextArea();
    GridLayout gridLayout3 = new GridLayout();
    JPanel panel4 = new JPanel();
    GridLayout gridLayout4 = new GridLayout();
    JLabel bemerkungsLbl = new JLabel();
    JTextArea bemerkungTA = new JTextArea();
    JScrollPane jScrollPane2 = new JScrollPane();
    JPanel panel5 = new JPanel();
    GridLayout gridLayout5 = new GridLayout();
    FlowLayout flowLayout1 = new FlowLayout();
    JPanel panel6 = new JPanel();
    JButton deleteBtn = new JButton();
    GridLayout gridLayout6 = new GridLayout();
    JButton saveBtn = new JButton();
    JPanel buttonPanel = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel10 = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    JUndoButton undoBtn = new JUndoButton();
    JPanel jPanel2 = new JPanel();
    JTextField gebTF = new JTextField();
    BorderLayout borderLayout3 = new BorderLayout();
    JButton dateBtn = new JButton();

    public PatientPanel() {
        jbInit();
        boInit();
        anredeCB.addItem("Herr");
        anredeCB.addItem("Frau");

        deleteBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int r = JOptionPane.showConfirmDialog( null,
                    "Patient " + presenter.getModel().getPatient() + " löschen ?","Patient löschen",
                    JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

                if ( r == JOptionPane.YES_OPTION )
                    presenter.deletePatient();
            }
        });

        saveBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                presenter.patientAnlegen();
            }
        });

        nachnameTF.addKeyListener(new KeyListener() {
            public void keyReleased( KeyEvent e) {
                if (vornameTF.getText().length() < 1 || nachnameTF.getText().length() < 1)
                    saveBtn.setEnabled( false );
                else {
		    if (presenter.getModel().isNewPatient()) {
			saveBtn.setEnabled( true );
			getRootPane().setDefaultButton( saveBtn );
		    }
                }
            }

            public void keyPressed( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e) {
            }
        });

        vornameTF.addKeyListener(new KeyListener() {
            public void keyReleased( KeyEvent e) {
                if (vornameTF.getText().length() < 1 || nachnameTF.getText().length() < 1)
                    saveBtn.setEnabled( false );
                else {
		    if (presenter.getModel().isNewPatient()) {
			saveBtn.setEnabled( true );
			getRootPane().setDefaultButton( saveBtn );
		    }
                }

            }

            public void keyPressed( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e) {
            }
        });
    }


    public void setPresenter( PatientPresenter presenter ) {
        if( this.presenter != null ) {
            this.presenter.getModel().removeChangeListener( this );
        }

        this.presenter = presenter;
        presenter.getModel().addChangeListener( this );
        update();
    }


    public void stateChanged( ChangeEvent e ) {
        update();
    }

    protected void update() {
        Patient patient = presenter.getModel().getPatient();
        if (patient.getAnrede() != null && patient.getAnrede().length() > 0)
            anredeCB.setSelectedItem(patient.getAnrede());
        else
            anredeCB.setSelectedIndex(0);
        achtungTA.setText(patient.getAchtung());
        adress1TF.setText(patient.getAdresse1());
        adress2TF.setText(patient.getAdresse2());
        adress3TF.setText(patient.getAdresse3());
        bemerkungTA.setText(patient.getBemerkung());
        emailTF.setText(patient.getEmail());
        faxTF.setText(patient.getFax());
        if (patient.getGeburtsDatum() != null)
            gebTF.setText(MediknightUtilities.formatDate(patient.getGeburtsDatum()));
        else
            gebTF.setText("");
        handyTF.setText(patient.getHandy());
        privatBtn.setSelected(patient.isPrivatPatient());
        telBerufTF.setText(patient.getTelefonArbeit());
        telPrivatTF.setText(patient.getTelefonPrivat());
        titelTF.setText(patient.getTitel());
        nachnameTF.setText(patient.getName());
        vornameTF.setText(patient.getVorname());

        buttonPanel.removeAll();
        if( presenter.getModel().isNewPatient() ) {
            saveBtn.setEnabled( false );
            buttonPanel.add( saveBtn );
        } else {
            buttonPanel.add( deleteBtn );

        }

        buttonPanel.revalidate();
    }


    public void getContent() {
        getContent(presenter.getModel().getPatient());
    }

    public void setFocusOnCombo() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		anredeCB.requestFocus();
		//getRootPane().setDefaultButton( deleteBtn );
            }
        });
    }

    public void getContent(Patient patient) {
        patient.setAnrede((String)anredeCB.getSelectedItem());
        patient.setAchtung(achtungTA.getText());
        patient.setAdresse1(adress1TF.getText());
        patient.setAdresse2(adress2TF.getText());
        patient.setAdresse3(adress3TF.getText());
        patient.setBemerkung(bemerkungTA.getText());
        patient.setEmail(emailTF.getText());
        patient.setFax(faxTF.getText());
        patient.setGeburtsDatum(MediknightUtilities.parseDate(gebTF.getText()));
        patient.setHandy(handyTF.getText());
        patient.setPrivatPatient(privatBtn.isSelected());
        patient.setTelefonArbeit(telBerufTF.getText());
        patient.setTelefonPrivat(telPrivatTF.getText());
        patient.setTitel(titelTF.getText());
        patient.setName(nachnameTF.getText());
        patient.setVorname(vornameTF.getText());
    }


    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.getContentPane().add(new PatientPanel());
        f.show();
    }

    public boolean isPatientOK() {
        return vornameTF.getText().length() > 0 && nachnameTF.getText().length() > 0;
    }

    private void boInit() {
        undoBtn.setName("patientPanelUndoBtn");
        undoBtn.setToolTipText("");
        undoBtn.setActionCommand("Rückgängig");
        undoBtn.setText("Rückgängig");

        dateBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
		Date date = MediknightUtilities.parseDate(gebTF.getText());
		if (date == null)
		    date = new Date();

                date = MediknightUtilities.showDateChooser( dateBtn.getParent(), date );
                if (date != null) {
                    gebTF.setText(MediknightUtilities.formatDate( date ));
                }
            }
        });
    }

    private void jbInit() {
        this.setLayout(gridBagLayout1);
        panel0.setLayout(gridLayout1);
        jPanel1.setLayout(flexGridLayout1);
        nachnameLbl.setText("Nachname:");
        vornameLbl.setText("Vorname:");
        flexGridLayout1.setRows(7);
        flexGridLayout1.setColumns(2);
        flexGridLayout1.setHgap(5);
        flexGridLayout1.setVgap(5);
        adress1Lbl.setText("Adresse:");
        titelLbl.setText("Titel:");
        anredeLbl.setText("Anrede:");
        gridLayout1.setColumns(2);
        gridLayout1.setHgap(10);
        gridLayout1.setVgap(10);
        dummy2Lbl.setText(" ");
        emailLbl.setText("E-Mail:");
        handyLbl.setText("Handy:");
        faxLbl.setText("Fax:");
        flexGridLayout2.setRows(7);
        flexGridLayout2.setColumns(2);
        flexGridLayout2.setHgap(5);
        flexGridLayout2.setVgap(5);
        telBerufLbl.setText("Telefon (beruflich):");
        telPrivatLbl.setText("Telefon (privat):");
        jPanel4.setLayout(flexGridLayout2);
        gebLbl.setText("Geburtstag:");
        finanzamtPrivatBtn.setSelected(true);
        finanzamtPrivatBtn.setResponsibleUndoHandler("patientPanelUndoBtn");
        finanzamtPrivatBtn.setOpaque(false);
        finanzamtPrivatBtn.setText("Finanzamt");
        privatBtn.setOpaque(false);
        privatBtn.setText("privat");
        privatBtn.setResponsibleUndoHandler("patientPanelUndoBtn");
        kkLabel.setText("Krankenkasse: ");
        achtungLbl.setText("Achtung:");
        panel2.setLayout(gridLayout2);
        achtungTA.setRows(6);
        achtungTA.setResponsibleUndoHandler("patientPanelUndoBtn");
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel3.setLayout(gridLayout3);
        panel4.setLayout(gridLayout4);
        bemerkungsLbl.setText("Bemerkung:");
        bemerkungTA.setRows(5);
        bemerkungTA.setToolTipText("");
        bemerkungTA.setResponsibleUndoHandler("patientPanelUndoBtn");
        panel5.setLayout(gridLayout5);
        jScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.setOpaque(false);
        this.setResponsibleUndoHandler("");
        panel0.setOpaque(false);
        jPanel1.setOpaque(false);
        jPanel4.setOpaque(false);
        panel1.setOpaque(false);
        panel1.setLayout(flowLayout1);
        panel2.setOpaque(false);
        panel4.setOpaque(false);
        panel5.setOpaque(false);
        anredeCB.setOpaque(false);
        anredeCB.setEditable(true);
        anredeCB.setResponsibleUndoHandler("patientPanelUndoBtn");
        flowLayout1.setHgap(0);
        flowLayout1.setVgap(0);
        deleteBtn.setText("Löschen");
        saveBtn.setText("Patient anlegen");
        buttonPanel.setLayout(gridLayout6);
        buttonPanel.setOpaque(false);
        panel6.setOpaque(false);
        panel6.setLayout(borderLayout1);
        jPanel10.setOpaque(false);
        jPanel10.setLayout(borderLayout2);
        titelTF.setResponsibleUndoHandler("patientPanelUndoBtn");
        vornameTF.setResponsibleUndoHandler("patientPanelUndoBtn");
        nachnameTF.setResponsibleUndoHandler("patientPanelUndoBtn");
        adress1TF.setResponsibleUndoHandler("patientPanelUndoBtn");
        adress2TF.setResponsibleUndoHandler("patientPanelUndoBtn");
        adress3TF.setResponsibleUndoHandler("patientPanelUndoBtn");
        telPrivatTF.setResponsibleUndoHandler("patientPanelUndoBtn");
        telBerufTF.setResponsibleUndoHandler("patientPanelUndoBtn");
        faxTF.setResponsibleUndoHandler("patientPanelUndoBtn");
        handyTF.setResponsibleUndoHandler("patientPanelUndoBtn");
        emailTF.setResponsibleUndoHandler("patientPanelUndoBtn");
        gebTF.setResponsibleUndoHandler("patientPanelUndoBtn");
        jPanel2.setOpaque(false);
        jPanel2.setLayout(borderLayout3);
        dateBtn.setMargin(new Insets(0, 2, 0, 2));
        dateBtn.setText("...");
        this.add(panel0, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        panel0.add(jPanel1, null);
        jPanel1.add(anredeLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel1.add(anredeCB, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel1.add(titelLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel1.add(titelTF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel1.add(vornameLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel1.add(vornameTF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel1.add(nachnameLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel1.add(nachnameTF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel1.add(adress1Lbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel1.add(adress1TF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel1.add(adress2Lbl, new FlexGridConstraints(0, 0, FlexGridConstraints.C));
        jPanel1.add(adress2TF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel1.add(adress3Lbl, new FlexGridConstraints(0, 0, FlexGridConstraints.C));
        jPanel1.add(adress3TF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        panel0.add(jPanel4, null);
        jPanel4.add(telPrivatLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel4.add(telPrivatTF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel4.add(telBerufLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel4.add(telBerufTF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel4.add(faxLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel4.add(faxTF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel4.add(handyLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel4.add(handyTF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel4.add(emailLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel4.add(emailTF, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel4.add(dummy1Lbl, new FlexGridConstraints(0, 0, FlexGridConstraints.C));
        jPanel4.add(dummy2Lbl, new FlexGridConstraints(0, 0, FlexGridConstraints.C));
        jPanel4.add(gebLbl, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
        jPanel4.add(jPanel2, new FlexGridConstraints(-1, 0, FlexGridConstraints.C));
        jPanel2.add(gebTF, BorderLayout.CENTER);
        jPanel2.add(dateBtn, BorderLayout.EAST);
        this.add(panel1, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
        panel1.add(kkLabel, null);
        panel1.add(finanzamtPrivatBtn, null);
        panel1.add(privatBtn, null);
        this.add(panel3, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        panel3.add(jScrollPane1, null);
        jScrollPane1.getViewport().add(achtungTA, null);
        this.add(panel2, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
        panel2.add(achtungLbl, null);
        this.add(panel4, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
        panel4.add(bemerkungsLbl, null);
        this.add(panel5, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        panel5.add(jScrollPane2, null);
        this.add(panel6, new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        panel6.add(buttonPanel, BorderLayout.EAST);
        buttonPanel.add(saveBtn, null);
        buttonPanel.add(deleteBtn, null);
        panel6.add(jPanel10, BorderLayout.WEST);
        jPanel10.add(undoBtn, BorderLayout.EAST);
        jScrollPane2.getViewport().add(bemerkungTA, null);
        kkBtnGroup.add(finanzamtPrivatBtn);
        kkBtnGroup.add(privatBtn);
    }
}
