package de.bo.mediknight;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.bo.mediknight.domain.TagesDiagnose;
import de.bo.mediknight.util.ErrorDisplay;
import de.bo.mediknight.widgets.*;
import de.bo.mediknight.widgets.JPanel;
import de.bo.mediknight.widgets.JScrollPane;
import de.bo.mediknight.widgets.JButton;
import de.bo.mediknight.widgets.JTextArea;


public class DiagnosisPanel extends de.bo.mediknight.widgets.JPanel implements ChangeListener, FocusListener, ActionListener  {
	private static final long serialVersionUID = 8967202476452623167L;

	DiagnosisPresenter presenter;

    JSplitPane jSplitPane1 = new JSplitPane();
    JPanel jPanel1 = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    JPanel jPanel3 = new JPanel();
    BorderLayout borderLayout4 = new BorderLayout();
    JPanel pBottom = new JPanel();
    BorderLayout borderLayout3 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JPanel jPanel4 = new JPanel();
    BorderLayout borderLayout5 = new BorderLayout();
    JLabel jLabel1 = new JLabel();
    JLabel patientType = new JLabel();
    JTextArea firstDiagnosis = new JTextArea();
    JLabel jLabel3 = new JLabel();
    JScrollPane entriesSP = new JScrollPane();
    JPanel entriesPanel = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JPanel jPanel2 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();

    DayDiagnosisEntryPanel currentPanel;
    BoxLayout boxLayout21 = new BoxLayout(entriesPanel,BoxLayout.Y_AXIS);

    Component lastFocusComponent = null;
    JPanel jPanel5 = new JPanel();
    JUndoButton undoBtn = new JUndoButton();
    BorderLayout borderLayout6 = new BorderLayout();
    JButton printBtn = new JButton();

    public DiagnosisPanel() {
        jbInit();
    }


    public void setPresenter( DiagnosisPresenter presenter ) {
        if( this.presenter != null ) {
            this.presenter.getModel().removeChangeListener( this );
        }

        this.presenter = presenter;
        presenter.getModel().addChangeListener( this );
        boInit();
        update();

    }

    public TagesDiagnose[] getDiagnosen() {
        TagesDiagnose[] diagnosen = new TagesDiagnose[entriesPanel.getComponentCount()];
        for (int i = 0; i < entriesPanel.getComponentCount(); i++) {
            TagesDiagnose diagnose = ((DayDiagnosisEntryPanel) entriesPanel.getComponent(i)).getDiagnose();
            diagnosen[i] = diagnose;
        }
        return diagnosen;
    }

    public String getFirstDiagnose() {
        return firstDiagnosis.getText();
    }

    public void setFirstDiagnosis(String text) {
        firstDiagnosis.setText(text);
        firstDiagnosis.setOriginalText(text);
    }

    public void stateChanged( ChangeEvent e ) {
        update();
    }

    public Component getLastFocusComponent() {
        return lastFocusComponent;
    }

    protected void update() {
        DiagnosisModel model = presenter.getModel();
        setFirstDiagnosis(model.getPatient().getErstDiagnose());

        Date date = model.getPatient().getGeburtsDatum();
        if ( date == null )
            date = new java.util.Date();
        patientType.setText( model.getPatient().isPrivatPatient() ? "privat" : "Kasse" );

        for( int i = 0; i < entriesPanel.getComponentCount(); i++) {
            ((DayDiagnosisEntryPanel) entriesPanel.getComponent(i)).removeDescriptionFocusListener( this );
            ((DayDiagnosisEntryPanel) entriesPanel.getComponent(i)).removeActionListener( this );
        }

        entriesSP.getViewport().remove( entriesPanel );

        entriesPanel.removeAll();
        List<TagesDiagnose> tagesDiagnosen;
        try {
            tagesDiagnosen = model.getTagesDiagnosen();
        } catch( SQLException e ) {
            new ErrorDisplay (e, "Fehler beim Einlesen der Tagesdiagnosen!", "Tagesdiagnosen laden...", this);
            tagesDiagnosen = null;
        }

        currentPanel = null;

        for( int i = 0; i < tagesDiagnosen.size(); i++ ) {
            TagesDiagnose diagnose = (TagesDiagnose) tagesDiagnosen.get( i );
            final DayDiagnosisEntryPanel entry = new DayDiagnosisEntryPanel( diagnose, presenter );
            entry.addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent e ) {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            entriesPanel.scrollRectToVisible( entry.getBounds() );
                            entry.requestFocusForDescriptionTA();
                        }
                    } );
                }
            });

            if (i == 0) {
                currentPanel = entry;
            }

            entry.addDescriptionFocusListener( this );
            entry.addActionListener( this );
            entry.setResponsibleUndoHandler( "diagnosisUndoBtn" );
//Test
            new LockingListener(presenter).applyTo(UndoUtilities.getMutables(entry));
//End

            entriesPanel.add( entry  );
        }

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                entriesSP.getViewport().add( entriesPanel );
            }
        } );
    }

    public void activate() {
        /** @todo I have to admit that this is totally braindead, but for the
            moment it works. */
        if( presenter.getModel().getPatient().getErstDiagnose() != null &&
            presenter.getModel().getPatient().getErstDiagnose().length() > 0 ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    entriesPanel.scrollRectToVisible( currentPanel.getBounds() );
                    currentPanel.requestFocusForDescriptionTA();
                }
            } );
        } else {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    firstDiagnosis.requestFocus();
                }
            } );
        }
    }


    /**
     * Hier wird das Event für die 'R' + 'V' - Buttons ausgeführt
     * Um eine Diagnose für Verordnung bzw. Rechnung zu setzen, ist
     * es im Moment nötig, sich den Opa des gedrückten Knopfes zu
     * holen.
     */
    public void actionPerformed( ActionEvent e ) {
        JButton btn = (JButton) e.getSource();
        Component c = ((Component)(((Component) e.getSource()).getParent())).getParent();

        int i = 0;
        for( i = 0; i < entriesPanel.getComponentCount(); i++ ) {
            if( entriesPanel.getComponent( i ) == c )
                break;
        }
        try {
            TagesDiagnose td = (TagesDiagnose)presenter.getModel().getTagesDiagnosen().get(i);
            MainFrame.getTracer().trace(MainFrame.DEBUG,"Button for diagnosis "+td);
            presenter.setSelectedDiagnose(td);
        } catch (SQLException ex) {
            new ErrorDisplay(ex, "Fehler beim Einlesen der Tagesdiagnosen!", "Einlesen der Tagesdiagnose...", this);
        }

        if (btn.getText().equals("R"))
            presenter.showBill();
        else if (btn.getText().equals("V"))
            presenter.showMedication();
        /** @todo Date actionlistener **/
    }


    public void focusGained( FocusEvent e ) {
        Component c = ((Component) e.getSource()).getParent();

        lastFocusComponent = c;

        int i = 0;
        for( i = 0; i < entriesPanel.getComponentCount(); i++ ) {
            if( entriesPanel.getComponent( i ) == c )
                break;
        }

        try {
            presenter.setSelectedDiagnose( (TagesDiagnose) presenter.getModel().getTagesDiagnosen().get( i ) );
        } catch (SQLException ex) {
            new ErrorDisplay(ex, "Fehler beim Einlesen der Tagesdiagnosen!", "Einlesen der Tagesdiagnose...", this);
        }
    }


    public void focusLost( FocusEvent e ) {

    }

    private void boInit() {

        if (presenter.getModel().getPatient().isPrivatPatient())
            patientType.setText("Privatpatient");
        else
            patientType.setText("Kassenpatient");

        firstDiagnosis.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                // Muss das sein? Ich vermute mal nicht und ausserdem stoert
                // es die Funktionalitaet der Knoepfe Verordnung und Diagnose.
                // presenter.setSelectedDiagnose(null);
                lastFocusComponent = firstDiagnosis;
            }
            public void focusLost(FocusEvent e) {}
        });

        printBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                presenter.printDiagnosis();
            }
        });

        undoBtn.setName("diagnosisUndoBtn");
        undoBtn.setToolTipText("");
        undoBtn.setActionCommand("Rückgängig");
        undoBtn.setText("Rückgängig");
    }


    private void jbInit() {
        this.setLayout(gridBagLayout1);
        jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOpaque(false);
        jSplitPane1.setOneTouchExpandable(true);
        jPanel1.setLayout(borderLayout2);
        jPanel1.setOpaque(false);
        jPanel3.setLayout(borderLayout4);
        pBottom.setLayout(borderLayout3);
        jPanel4.setLayout(borderLayout5);
        jLabel1.setForeground(Color.black);
        jLabel1.setText("Dauerdiagnose                        ");
        patientType.setForeground(Color.black);
        patientType.setText("            privat");
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setOpaque(false);
        jPanel4.setOpaque(false);
        pBottom.setOpaque(false);
        jLabel3.setForeground(Color.black);
        jLabel3.setText("Tagesdiagnosen");
        this.setBorder(BorderFactory.createEmptyBorder());
        entriesSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        entriesSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        entriesSP.setOpaque(false);
        entriesSP.setToolTipText("");
        entriesSP.setResponsibleUndoHandler("");
        entriesPanel.setLayout(boxLayout21);
        entriesPanel.setBackground(Color.white);
        entriesPanel.setResponsibleUndoHandler("diagnosisUndoBtn");
        jPanel3.setOpaque(false);
        patientType.setForeground(Color.black);
        patientType.setText("privat");
        jPanel2.setLayout(borderLayout1);
        borderLayout1.setHgap(36);
        //boxLayout21.setAxis(BoxLayout.Y_AXIS);
        firstDiagnosis.setResponsibleUndoHandler("diagnosisUndoBtn");
        jPanel2.setOpaque(false);
        jPanel5.setLayout(borderLayout6);
        printBtn.setText("Diagnosen drucken");
        this.add(jSplitPane1, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 1, 1, 1), 0, 0));
        jSplitPane1.add(jPanel1, JSplitPane.TOP);
        jPanel1.add(jPanel3, BorderLayout.NORTH);
        jPanel3.add(jPanel4, BorderLayout.WEST);
        jPanel4.add(patientType, BorderLayout.EAST);
        jPanel4.add(jLabel1, BorderLayout.WEST);
        jPanel4.add(jPanel2, BorderLayout.EAST);
        jPanel2.add(patientType, BorderLayout.EAST);
        jPanel1.add(jScrollPane1, BorderLayout.CENTER);
        jSplitPane1.add(pBottom, JSplitPane.BOTTOM);
        pBottom.add(jLabel3, BorderLayout.NORTH);
        pBottom.add(entriesSP, BorderLayout.CENTER);
        this.add(jPanel5, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        jPanel5.add(undoBtn, BorderLayout.WEST);
        jPanel5.add(printBtn, BorderLayout.EAST);
        //entriesSP.getViewport().add(entriesPanel, null);
        jScrollPane1.getViewport().add(firstDiagnosis, null);
        jSplitPane1.setDividerLocation(120);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        DiagnosisPanel dp = new DiagnosisPanel();
       // dp.entriesPanel.add(new DayDiagnosisEntryPanel( new Date(), "Ich bin eine Diagnose.", presenter ));
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DAY_OF_MONTH, -1);
       // dp.entriesPanel.add( new DayDiagnosisEntryPanel( calendar.getTime(), "Ich bin zwei Diagnosen.", presenter ));
        f.getContentPane().add(dp);
        f.pack();
        f.setVisible(true);
    }
}
