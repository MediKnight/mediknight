package de.bo.mediknight;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import de.bo.mediknight.widgets.*;

import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.*;


public class LetterPanel extends JPanel implements ChangeListener {

    private final String TEXT_GOA = "Für meine Bemühungen bei Ihnen erlaube ich mir laut GoÄ zu berechnen:";
    private final String TEXT_GEB = "Für meine Bemühungen bei Ihnen erlaube ich mir laut GebüH85 zu berechnen:";

    LetterPresenter presenter;

    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JScrollPane adressSP = new JScrollPane();
    JTextArea adressTA = new JTextArea();
    JLabel adressLbl = new JLabel();
    JLabel dateLbl = new JLabel();
    JPanel dummyPanel = new JPanel();
    JPanel dateComponentsPanel = new JPanel();
    JTextField dateTF = new JTextField();
    JButton dateBtn = new JButton();
    FlowLayout flowLayout1 = new FlowLayout();
    JLabel diagLbl = new JLabel();
    JScrollPane diagnosticSP = new JScrollPane();
    JTextArea diagnoseTA = new JTextArea();
    JLabel greetingsLbl = new JLabel();
    JScrollPane greetingsSP = new JScrollPane();
    JTextArea greetingsTA = new JTextArea();
    JLabel headerLbl = new JLabel();
    JPanel jPanel1 = new JPanel();
    JLabel copyCountLbl = new JLabel();
    FlowLayout flowLayout2 = new FlowLayout(FlowLayout.LEFT, 0, 0);
    JTextField countTF = new JTextField();
    JButton downBtn = new JButton();
    JButton upBtn = new JButton();
    JPanel jPanel2 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    GridLayout gridLayout1 = new GridLayout();
    JButton printBtn = new JButton();
    JPanel buttonPanel = new JPanel();
    JButton page1Btn = new JButton();
    JPanel jPanel3 = new JPanel();
    JUndoButton undoBtn = new JUndoButton();
    BorderLayout borderLayout2 = new BorderLayout();

    public LetterPanel() {
        jbInit();
        boInit();

        printBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                presenter.printBill();
            }
        });
    }

    public void setPresenter( LetterPresenter presenter ) {
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

        String text;
        Rechnung rechnung = presenter.getModel().getRechnung();


        Patient patient = rechnung.getPatient();
        java.util.List tagesDiagnosen = presenter.getModel().getTagesDiagnosen();

        java.util.Date date = rechnung.getDatum();
        if ( date == null )
            date = rechnung.getDiagnose().getDatum();
        dateTF.setText(MediknightUtilities.formatDate(date));

        text = rechnung.getText();

        if (text == null) {
            text = "Diagnose: " + rechnung.getPatient().getErstDiagnose() + "\n\n";
            if ( rechnung.isGoae() )
                text = text + TEXT_GOA;
            else
                text = text + TEXT_GEB;

//            text = text + "\n\nSpezifikation:";
        }

        diagnoseTA.setText(text);

        if (rechnung.getAddress() == null || rechnung.getAddress().length() == 0)
            adressTA.setText(
                patient.getAnrede() + "\n" +
                (patient.getTitel().length() == 0 ? "" : patient.getTitel()  + " ") +
                (patient.getVorname().length() == 0 ? "" : patient.getVorname()) + " " +
                patient.getName() + "\n" +
                patient.getAdresse1() + "\n" +
                patient.getAdresse2() + "\n" +
                patient.getAdresse3() );
        else
            adressTA.setText(rechnung.getAddress());

        greetingsTA.setText(rechnung.getGreetings());
    }


    void boInit() {

        undoBtn.setName("letterUndoBtn");

        page1Btn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                presenter.showBill();
            }
        });

        dateBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
		java.util.Date date = MediknightUtilities.parseDate(dateTF.getText());
		if (date == null)
		    date = new java.util.Date();
                date = MediknightUtilities.showDateChooser( dateBtn.getParent(), date );
                if (date != null) {
                    dateTF.setText(MediknightUtilities.formatDate( date ));
                }
            }
        });

        upBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                int i;
                try {
                    i = Integer.parseInt(countTF.getText());
                } catch (NumberFormatException nfe) {
                    i = 3;
                }
                countTF.setText(Integer.toString(++i));
                downBtn.setEnabled(true);
            }
        });
        downBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                int i;
                try {
                    i = Integer.parseInt(countTF.getText());
                } catch (NumberFormatException nfe) {
                    i = 3;
                }
                if (i > 1)
                    countTF.setText(Integer.toString(--i));

                if (i == 1) downBtn.setEnabled(false);
            }
        });

    }

    public void setFocusOnAdressTA() {
	SwingUtilities.invokeLater( new Runnable() {
	    public void run() {
		adressTA.requestFocus();
		getRootPane().setDefaultButton( printBtn );
	    }
	});
    }


    public String getText() {
        return diagnoseTA.getText();
    }

    public String getGreetings() {
        return greetingsTA.getText();
    }

    public String getAdress() {
        return adressTA.getText();
    }

    public String getDate() {
        return dateTF.getText();
    }

    public int getCopyCount() {
        try {
            return Integer.parseInt( countTF.getText() );
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void jbInit() {
        this.setLayout(gridBagLayout1);
        adressTA.setColumns(25);
        adressTA.setRows(5);
        adressTA.setResponsibleUndoHandler("letterUndoBtn");
        adressSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        adressSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        adressLbl.setText("Adresse:");
        dateLbl.setToolTipText("");
        dateLbl.setText("Datum");
        dateBtn.setMargin(new Insets(0, 2, 0, 2));
        dateBtn.setText("...");
        dateTF.setColumns(10);
        dateTF.setResponsibleUndoHandler("letterUndoBtn");
        dateComponentsPanel.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        flowLayout1.setVgap(0);
        diagLbl.setText("Rechnungstext:");
        diagnosticSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        diagnosticSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        diagnoseTA.setRows(7);
        diagnoseTA.setResponsibleUndoHandler("letterUndoBtn");
        greetingsLbl.setText("Grußzeile:");
        greetingsSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        greetingsSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        greetingsSP.setOpaque(false);
        greetingsTA.setRows(4);
        greetingsTA.setResponsibleUndoHandler("letterUndoBtn");
        headerLbl.setText("Seite 2/2: Adresskopf und Grußzeile");
        this.setOpaque(false);
        dummyPanel.setOpaque(false);
        dateComponentsPanel.setOpaque(false);
        copyCountLbl.setText("Anzahl Kopien:");
        jPanel1.setOpaque(false);
        jPanel1.setLayout(flowLayout2);
        countTF.setSelectionEnd(3);
        countTF.setColumns(3);
        countTF.setOriginalText("3");
        countTF.setText("3");
        countTF.setResponsibleUndoHandler("letterUndoBtn");
        downBtn.setMargin(new Insets(0, 2, 0, 2));
        downBtn.setText("<");
        upBtn.setMargin(new Insets(0, 2, 0, 2));
        upBtn.setText(">");
        jPanel2.setOpaque(false);
        jPanel2.setLayout(borderLayout1);
        gridLayout1.setHgap(10);
        printBtn.setText("Drucken");
        buttonPanel.setLayout(gridLayout1);
        buttonPanel.setOpaque(false);
        page1Btn.setText("Seite 1");
        jPanel3.setOpaque(false);
        jPanel3.setLayout(borderLayout2);
        undoBtn.setText("Rückgängig");
        flowLayout2.setVgap(0);
        this.add(adressSP, new GridBagConstraints(0, 2, 1, 3, 0.6, 0.1
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(adressLbl, new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
        this.add(dateLbl, new GridBagConstraints(1, 3, 2, 1, 0.4, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 10, 0, 0), 0, 0));
        this.add(dummyPanel, new GridBagConstraints(1, 2, 2, 1, 0.4, 0.1
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(dateComponentsPanel, new GridBagConstraints(1, 4, 2, 1, 0.4, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 0), 0, 0));
        dateComponentsPanel.add(dateTF, null);
        dateComponentsPanel.add(dateBtn, null);
        this.add(diagLbl, new GridBagConstraints(0, 5, 3, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 0, 0));
        this.add(diagnosticSP, new GridBagConstraints(0, 6, 3, 1, 1.0, 0.6
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(greetingsLbl, new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 2, 0));
        this.add(greetingsSP, new GridBagConstraints(0, 8, 1, 1, 0.6, 0.2
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(headerLbl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(jPanel1, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jPanel1.add(copyCountLbl, null);
        jPanel1.add(countTF, null);
        jPanel1.add(downBtn, null);
        jPanel1.add(upBtn, null);
        this.add(jPanel2, new GridBagConstraints(0, 9, 4, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        jPanel2.add(buttonPanel, BorderLayout.EAST);
        buttonPanel.add(page1Btn, null);
        buttonPanel.add(printBtn, null);
        jPanel2.add(jPanel3, BorderLayout.WEST);
        jPanel3.add(undoBtn, BorderLayout.WEST);
        greetingsSP.getViewport().add(greetingsTA, null);
        diagnosticSP.getViewport().add(diagnoseTA, null);
        adressSP.getViewport().add(adressTA, null);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.getContentPane().add(new LetterPanel());
        f.show();
    }
}