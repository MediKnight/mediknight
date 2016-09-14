//package main.java.de.baltic_online.mediknight;
//
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.Insets;
//import java.awt.event.*;
//import java.util.Date;
//
//import javax.swing.*;
//
//import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;
//import main.java.de.baltic_online.mediknight.util.MediknightUtilities;
//import main.java.de.baltic_online.mediknight.widgets.*;
//import main.java.de.baltic_online.mediknight.widgets.JPanel;
//import main.java.de.baltic_online.mediknight.widgets.JButton;
//import main.java.de.baltic_online.mediknight.widgets.JTextArea;
//import main.java.de.baltic_online.mediknight.widgets.JTextField;
//
//public class DayDiagnosisEntryPanel extends JPanel implements FocusListener {
//    /**
//	 * 
//	 */
//	private static final long serialVersionUID = -5533264757066868213L;
//	JPanel dayPanel = new JPanel();
//    FlowLayout flowLayout1 = new FlowLayout();
//    JButton billBtn = new JButton();
//    JButton medicationBtn = new JButton();
//    JTextField dateField = new JTextField();
//    JButton dateBtn = new JButton();
//    JTextArea descriptionTA = new JTextArea();
///*    public Component[] getComponents() {
//        return new Component[] {this};//{descriptionTA, dateField};
//    }*/
//    //VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
//    BoxLayout boxLayout1 = new BoxLayout(this,BoxLayout.Y_AXIS);
//
//    TagesDiagnose diagnose;
//    DiagnosisPresenter presenter;
//
//    public static final String KEYMEDICATION    = "alt V";
//    public static final String KEYBILL          = "alt R";
//
//    public DayDiagnosisEntryPanel(Date date,String description) {
//        jbInit();
//        setOpaque(true);
//        setBackground(Color.white);
//
//        set(date,description);
//
//        dateBtn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e ) {
//                Date date = MediknightUtilities.parseDate(dateField.getText());
//                if (date == null)
//                    date = new Date();
//                date = MediknightUtilities.showDateChooser(dateBtn.getParent(),date);
//                if (date != null) {
//                    dateField.setText(MediknightUtilities.formatDate(date));
//                    try {
//                        diagnose.setDatum( new java.sql.Date( date.getTime() ));
//                        diagnose.save();
//                    } catch (java.sql.SQLException sqle) {
//                    }
//                }
//            }
//        });
//
//        boInit();
//    }
//
//    public DayDiagnosisEntryPanel(TagesDiagnose diagnose, DiagnosisPresenter presenter) {
//        this(new java.util.Date(diagnose.getDatum().getTime()),diagnose.getText());
//        this.diagnose = diagnose;
//        this.presenter = presenter;
//    }
//
//    public void set(Date date,String description) {
//        dateField.setText(date == null ? "" : MediknightUtilities.formatDate(date));
//        descriptionTA.setOriginalText(description);
//        descriptionTA.setText(description);
//    }
//
//    public TagesDiagnose getDiagnose() {
//        setContent();
//        return diagnose;
//    }
//
//    public void addDescriptionFocusListener( FocusListener l ) {
//        descriptionTA.addFocusListener(l);
//    }
//
//    public void removeDescriptionFocusListener( FocusListener l ) {
//        descriptionTA.removeFocusListener(l);
//    }
//
//    public void addActionListener(ActionListener l) {
//        medicationBtn.addActionListener(l);
//        billBtn.addActionListener(l);
//    }
//
//    public void removeActionListener(ActionListener l) {
//        medicationBtn.removeActionListener(l);
//        billBtn.removeActionListener(l);
//    }
//
//    private void setContent() {
//        diagnose.setDatum(MediknightUtilities.parseDate(dateField.getText()));
//        diagnose.setText(descriptionTA.getText().trim());
//    }
//
//    private void boInit() {
//        // initialize keyboard shortcuts for buttons
//        billBtn.getActionMap().put("rechnung", new AbstractAction() {
//			private static final long serialVersionUID = -6666575964516937837L;
//
//			public void actionPerformed(ActionEvent e) {
//                billBtn.doClick();
//            }
//        });
//        medicationBtn.getActionMap().put("verordnung", new AbstractAction() {
// 			private static final long serialVersionUID = 9131246829561031709L;
//
//			public void actionPerformed(ActionEvent e) {
//                medicationBtn.doClick();
//            }
//        });
//        descriptionTA.addFocusListener(this);
//
//        dateField.addFocusListener( new FocusAdapter() {
//            public void focusLost( FocusEvent e) {
//                try {
//                    diagnose.setDatum( MediknightUtilities.parseDate( dateField.getText() ));
//                    diagnose.save();
//                } catch (java.sql.SQLException sqle) {
//                }
//            }
//        });
////	dateField.setPreferredSize( new Dimension( dateField.getPreferredSize().width + 6, dateField.getPreferredSize().height ));
//    }
//
//    public void focusGained(FocusEvent e) {
//        // activate keyboard shortcuts for buttons
//        billBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KEYBILL), "rechnung");
//        medicationBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KEYMEDICATION), "verordnung");
//        setBackground( javax.swing.UIManager.getColor( "Panel.background" ) );
//        if (countRows( descriptionTA.getText() ) < 5)
//            descriptionTA.setRows( 5 );
//        updateUI();
//    }
//
//    public void focusLost(FocusEvent e) {
////        try {
//            diagnose.setText( descriptionTA.getText() );
//            //diagnose.save();
//            presenter.saveTagesdiagnose( diagnose );
//  /*      } catch (java.sql.SQLException sqle) {
//            sqle.printStackTrace();
//        }*/
//        billBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(KeyStroke.getKeyStroke(KEYBILL));
//        medicationBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(KeyStroke.getKeyStroke(KEYMEDICATION));
//        setBackground( javax.swing.UIManager.getColor( "TextArea.background" ) );
//
//        if (countRows( descriptionTA.getText() ) > 5) {
//            ((JTextArea) e.getSource()).setRows( 5 );
//        } else {
//            ((JTextArea) e.getSource()).setRows( countRows(descriptionTA.getText()) );
//            ((JTextArea) e.getSource()).getName();
//        }
//    }
//
//    public void requestFocusForDescriptionTA() {
//        descriptionTA.requestFocus();
//    }
//
//    public int countRows(String s) {
//        int row = 1;
//        for (int i = 0; i < s.length(); i++) {
//            if (((int)s.charAt( i )) == 10)
//                row++;
//        }
//        return row;
//    }
//
//    private void jbInit() {
//        this.setLayout(boxLayout1);
//        dayPanel.setLayout(flowLayout1);
//        flowLayout1.setAlignment(FlowLayout.LEFT);
//        flowLayout1.setHgap(4);
//        flowLayout1.setVgap(0);
//        billBtn.setToolTipText("Rechnung schreiben");
//        billBtn.setHorizontalTextPosition(SwingConstants.CENTER);
//        billBtn.setMargin(new Insets(0, 2, 0, 2));
//        billBtn.setText("R");
//        medicationBtn.setToolTipText("Verordnung schreiben");
//        medicationBtn.setHorizontalTextPosition(SwingConstants.CENTER);
//        medicationBtn.setMargin(new Insets(0, 2, 0, 2));
//        medicationBtn.setText("V");
//        dateField.setBorder(BorderFactory.createLineBorder(Color.black));
//        dateField.setColumns( 8 );
//        dateField.setHorizontalAlignment(SwingConstants.CENTER);
//        dateField.setResponsibleUndoHandler( "diagnosisUndoBtn" );
//        dateBtn.setPreferredSize(new Dimension(15, 20));
//        dateBtn.setHorizontalTextPosition(SwingConstants.CENTER);
//        dateBtn.setMargin(new Insets(0, 2, 0, 2));
//        dateBtn.setText("...");
//        descriptionTA.setWrapStyleWord(true);
//        descriptionTA.setLineWrap(true);
//        descriptionTA.setBorder(new UnderlineableBorder(descriptionTA.getSelectionColor()));
//        descriptionTA.setAlignmentY((float) 0.0);
//        descriptionTA.setAlignmentX((float) 0.0);
//        descriptionTA.setResponsibleUndoHandler("diagnosisUndoBtn");
//        dayPanel.setOpaque(false);
//        dayPanel.setForeground(Color.blue);
//        dayPanel.setAlignmentX((float) 0.0);
//        dayPanel.setAlignmentY((float) 0.0);
//        dayPanel.setBorder(new MediBorder());
//        this.setAlignmentX((float) 0.0);
//        this.setAlignmentY((float) 0.0);
//        this.setOpaque(false);
//        this.add(dayPanel, null);
//        dayPanel.add(billBtn, null);
//        dayPanel.add(medicationBtn, null);
//        dayPanel.add(dateField, null);
//        dayPanel.add(dateBtn, null);
//        this.add(descriptionTA, null);
//    }
//}
