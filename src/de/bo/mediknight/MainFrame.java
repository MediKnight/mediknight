package de.bo.mediknight;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import de.bo.mediknight.widgets.*;
import de.bo.mediknight.dialogs.DatabaseSelectionDialog;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.*;
import de.bo.mediknight.borm.*;

public class MainFrame extends JFrame implements TraceConstants {

    public static final int STANDALONE = 0;

    public static final int CLIENT = 1;

    public static final int SERVER = 2;

    public static final String BSEARCH = "search";

    public static final String BNEW = "new";

    public static final String BQUIT = "quit";

    public static final String BDETAILS = "details";

    public static final String BDIAGNOSIS = "diagnosis";

    public static final String BMEDICATION = "medication";

    public static final String BBILL = "bill";

    public static final String BFINISH = "finish";

    public static final String BABOUT = "about";

    // Constants describing possible actions, see backAction.
    public static final int SEARCH = 1;

    public static final int DIAGNOSIS = 2;

    public static final int MEDICATION = 3;

    public static final int BILL = 4;

    public static final int NEW_PATIENT = 5;

    public static final int DETAILS = 6;

    public static final int LETTER = 7;

    public static final int MACRO = 8;

    public static final int CREATE_MACRO = 9;

    /** @see javax.swing.KeyStroke#getKeyStroke(java.lang.String) */
    public static final String KEYSEARCH = "alt S";

    public static final String KEYNEW = "alt N";

    public static final String KEYQUIT = "alt Q";

    /** @todo think of a better short cut for details */
    public static final String KEYDETAILS = "alt E";

    public static final String KEYDIAGNOSIS = "alt D";

    //    public static final String KEYMEDICATION = "alt V";
    //    public static final String KEYBILL = "alt R";
    public static final String KEYFINISH = "alt Z";

    public static final String KEYABOUT = "alt A";

    // Resource of property file.
    public final static String MEDIKNIGHT_PROPERTIES = "mediknight.properties";

    public final static String PROPERTY_FILENAME = "de/bo/mediknight/resources/"
            + MEDIKNIGHT_PROPERTIES;

    // this application
    private static MainFrame application;

    /**
     * The name of the application.
     */
    public static String NAME;

    public static String VERSION;

    // the absolute location of the application
    private static File location;

    // the name of the this jar file (without location) if we are one.
    private static String jar;

    /**
     * "Tracing-Class".
     * <p>
     * Tracing-calls with this class reports user actions.
     */
    public final static String USER = "user";

    // user for recent patientlist
    private User user;

    // we use EUR instead of DM?
    private boolean euro;

    // this properties
    private static Properties properties;

    // Tracer
    private static Tracer tracer;

    // run mode, e.g. client, server, standalone
    private static int runMode;

    private int backAction;

    private int currentAction;

    private JPanel header;

    private JLabel subtitle;

    private JLabel title;

    private JLabel patientLabel;

    // panel container
    private JComponent workspace;

    // Statical List of master data
    private RechnungsPosten[] rechnungsPosten;

    //
    private TagesDiagnose oldDiagnosis = null;

    // gradient painted navigator panel
    private JGradientPanel navigator;

    // current used glasspane for presenting "disabled" mode
    private GrayedPane grayedPane;

    // Locking info
    private LockingInfo lockingInfo;

    private Presenter currentPresenter;

    private boolean ignoreCommit;

    private Patient oldPatient = null;

    private MainFrame() {
        registerMappers();
        application = this;

        backAction = currentAction = SEARCH;
        ignoreCommit = false;
        euro = false;

        lockingInfo = new LockingInfo();

        // default locale for this application
        setLocale(Locale.GERMAN);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                quit(true);
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                saveSize();
            }
        });

        getContentPane().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                saveSize();
            }
        });

        createUI();
    }

    private void registerMappers() {
        // Wow, now that's ugly! It seems like with newer JDK releases,
        // static class initializers are not called anymore only by
        // mentioning the class alone. Therefore we have to create
        // dummy instances of the persistent classes.
        // Consider this a bad hack!
        new TagesDiagnose();
        new Rechnung();
        new Verordnung();
    }

    private void createUI() {
        grayedPane = new GrayedPane();
        setGlassPane(grayedPane);

        Color c1 = UIManager.getColor("effect");
        if (c1 == null)
            c1 = Color.black;
        Color c2 = Color.white;

        setSize(800, 600);

        // create title component
        header = new JPanel(new GridBagLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.setBackground(c1);
        title = new JLabel("Willkommen zu " + NAME);
        title.setForeground(c2);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24.0f));
        subtitle = new JLabel(NAME + " " + VERSION
                + " - Abrechnungsprogramm für Heilpraktiker");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.BOLD));
        header.add(title, new GridBagConstraints(0, 1, 1, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
                        0, 0, 0), 0, 0));
        header.add(subtitle, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        // create navigator component
        navigator = new JGradientPanel();
        navigator.setGradientColor(c1);
        navigator.setBackground(c2);
        navigator.setLayout(new FlexGridLayout(0, 1, 5, 5));
        navigator.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToggleButton searchButton = new JToggleButton("Suchen");
        searchButton.setName(BSEARCH);
        searchButton.setMnemonic('S');
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        searchButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KEYSEARCH), "search");
        searchButton.getActionMap().put("search", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });

        JToggleButton newButton = new JToggleButton("Neu");
        newButton.setName(BNEW);
        newButton.setMnemonic('N');
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newPatient();
            }
        });
        newButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KEYNEW), "newPatient");
        newButton.getActionMap().put("newPatient", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                newPatient();
            }
        });

        JToggleButton quitButton = new JToggleButton("Beenden");
        quitButton.setName(BQUIT);
        quitButton.setMnemonic('B');
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quit(true);
            }
        });
        quitButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KEYQUIT), "quitApplication");
        quitButton.getActionMap().put("quitApplication", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                quit(true);
            }
        });

        JToggleButton detailsButton = new JToggleButton("Details");
        detailsButton.setName(BDETAILS);
        detailsButton.setMnemonic('e');
        detailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                patientDetails();
            }
        });
        detailsButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KEYDETAILS), "patientDetails");
        detailsButton.getActionMap().put("patientDetails",
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        patientDetails();
                    }
                });

        JToggleButton diagnosisButton = new JToggleButton("Diagnose");
        diagnosisButton.setName(BDIAGNOSIS);
        diagnosisButton.setMnemonic('D');
        diagnosisButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                diagnosis();
            }
        });
        diagnosisButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KEYDIAGNOSIS), "diagnose");
        diagnosisButton.getActionMap().put("diagnose", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                diagnosis();
            }
        });

        JToggleButton medicationButton = new JToggleButton("Verordnung");
        medicationButton.setName(BMEDICATION);
        medicationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyAction(MEDICATION);
            }
        });
        /*
         * medicationButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KEYMEDICATION),
         * "verordnung"); medicationButton.getActionMap().put("verordnung", new
         * AbstractAction() { public void actionPerformed(ActionEvent e) {
         * medication(); } });
         */

        JToggleButton billButton = new JToggleButton("Rechnung");
        billButton.setName(BBILL);
        billButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bill();
            }
        });
        /*
         * billButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KEYBILL),
         * "rechnung"); billButton.getActionMap().put("rechnung", new
         * AbstractAction() { public void actionPerformed(ActionEvent e) {
         * bill(); } });
         */

        JButton finishButton = new JButton("Zurück");
        finishButton.setName(BFINISH);
        finishButton.setMnemonic('z');
        finishButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                finish();
            }
        });
        finishButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KEYFINISH), "zurueck");
        finishButton.getActionMap().put("zurueck", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                finish();
            }
        });

        ImageIcon icon;

        URL url = MainFrame.class.getClassLoader().getResource(
                "de/bo/mediknight/resources/logo.gif");
        icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));

        JPanel aboutPanel = new JPanel();
        JButton aboutButton = new JButton(icon);
        aboutButton.setBorder(BorderFactory.createEmptyBorder());
        aboutButton.setBackground(c2);
        aboutButton.setFocusPainted(false);
        aboutButton.setContentAreaFilled(false);
        aboutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                about();
            }
        });
        aboutButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KEYABOUT), "about");
        aboutButton.getActionMap().put("about", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                about();
            }
        });

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBackground(c2);
        JPanel dummyPanel = new JPanel();
        dummyPanel.setBackground(c2);
        logoPanel.add(dummyPanel, BorderLayout.SOUTH);
        logoPanel.add(aboutButton, BorderLayout.CENTER);

        patientLabel = new JLabel(" ");
        navigator.add(patientLabel, "11,0,c");
        navigator.add(searchButton, "11,0,c");
        navigator.add(newButton, "11,0,c");
        navigator.add(detailsButton, "11,0,c");
        navigator.add(diagnosisButton, "11,0,c");
        navigator.add(medicationButton, "11,0,c");
        navigator.add(billButton, "11,0,c");
        navigator.add(finishButton, "11,0,c");
        navigator.add(quitButton, "11,0,c");

        // create workspace component
        workspace = new JPanel(new BorderLayout());
        //workspace.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel buttonPanel = new JPanel(new BorderLayout());

        buttonPanel.add(navigator, BorderLayout.CENTER);
        buttonPanel.add(logoPanel, BorderLayout.SOUTH);

        // layout components
        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());

        JPanel filler = new JPanel();
        filler.setBackground(c1.brighter());
        pane.add(filler, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));

        pane.add(header, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));

        pane.add(workspace, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        10, 10, 10, 10), 0, 0));

        pane.add(buttonPanel, new GridBagConstraints(0, 1, 1, 1, 0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    public void patientDetails() {
        setWaitCursor();
        tracer.trace(USER, "Schalter \"Details\" aktiviert");
        showDetailsPane();
        setDefaultCursor();
    }

    public void diagnosis() {
        setWaitCursor();
        tracer.trace(USER, "Schalter \"Diagnose\" aktiviert");
        showDiagnosisPane();
        setDefaultCursor();
    }

    public void medication() {

        tracer.trace(USER, "Schalter \"Verordnung\" aktiviert");

        TagesDiagnose d = getCurrentDiagnosis();
        if (d != null) {
            showMedicationPane(d);
        }
    }

    public void bill() {
        setWaitCursor();
        tracer.trace(USER, "Schalter \"Rechnung\" aktiviert");

        showBillPane();
    }

    public void letter() {

        TagesDiagnose d = getCurrentDiagnosis();
        if (d != null) {
            try {
                LetterModel model = new LetterModel(d.getRechnung());
                setPane(new LetterPresenter(model));
                currentAction = LETTER;
            } catch (SQLException e) {
                e.printStackTrace();
                /** @todo Exception reporting. */
            }
        }
    }

    public void macro(BillEntry[] entries) {

        TagesDiagnose d = getCurrentDiagnosis();
        if (d != null) {
            try {
                setHeaderPanel("Rechnungsbausteine");
                MacroModel macroModel = new MacroModel(entries, d.getRechnung());
                setPane(new MacroPresenter(macroModel));
                currentAction = MACRO;
            } catch (SQLException e) {
                e.printStackTrace();
                /** @todo Exception reporting. */
            }
        }
    }

    public void macro() {
        macro(null);
    }

    public void createMacro(BillEntry[] entries) {
        setHeaderPanel("Baustein erstellen");
        CreateMacroModel model = new CreateMacroModel(entries);
        setPane(new CreateMacroPresenter(model));
        currentAction = CREATE_MACRO;
    }

    public void search() {
        commit();

        setHeaderPanel("Patientensuche");
        setVisibleNavigatorButton(BSEARCH, true, true);
        setVisibleNavigatorButton(BNEW, true, false);
        setVisibleNavigatorButton(BQUIT, true, false);
        setVisibleNavigatorButton(BDETAILS, false, false);
        setVisibleNavigatorButton(BDIAGNOSIS, false, false);
        setVisibleNavigatorButton(BMEDICATION, false, false);
        setVisibleNavigatorButton(BBILL, false, false);
        setVisibleNavigatorButton(BFINISH, true, false);
        setVisibleNavigatorButton(BABOUT, true, false);

        try {
            Lock.releaseAll();
        } catch (Exception x) {
            tracer.trace(ERROR_T, x);
        }

        setPatientToNavigator(null);
        setWindowTitle(true);
        setWaitCursor();
        tracer.trace(USER, "Schalter \"Suchen\" aktiviert");
        setCurrentPatient(null);
        setPane(new SearchPresenter());
        setDefaultCursor();

        currentAction = SEARCH;
    }

    /**
     * Sets the bill pane as the current pane. This methods sets the title too.
     */
    public void showBillPane() {
        TagesDiagnose d = getCurrentDiagnosis();
        if (d != null) {
            setTitle("Rechnung");
            setWindowTitle();
            setHeaderPanel("Rechnung ("
                    + new SimpleDateFormat("dd.MM.yyyy").format(d.getDatum())
                    + ")");

            try {
                BillModel model = new BillModel(d.getRechnung());
                setPane(new BillPresenter(model));
                currentAction = BILL;
            } catch (SQLException e) {
                e.printStackTrace();
                /** @todo Exception reporting. */
            }
        }
    }

    public void newPatient() {
        setHeaderPanel("Patient neu anlegen");
        setPatientToNavigator(null);
        setVisibleNavigatorButton(BSEARCH, true, false);
        setVisibleNavigatorButton(BNEW, true, true);
        setVisibleNavigatorButton(BQUIT, true, false);
        setVisibleNavigatorButton(BDETAILS, false, false);
        setVisibleNavigatorButton(BDIAGNOSIS, false, false);
        setVisibleNavigatorButton(BMEDICATION, false, false);
        setVisibleNavigatorButton(BBILL, false, false);
        setVisibleNavigatorButton(BFINISH, true, false);
        setVisibleNavigatorButton(BABOUT, false, false);

        setWaitCursor();
        tracer.trace(USER, "Schalter \"Neu\" aktiviert");

        setTitle("");

        oldPatient = getCurrentPatient();
        setCurrentPatient(null);
        setCurrentDiagnosis(null);
        PatientModel model = new PatientModel(new Patient());
        model.setNewPatient(true);
        setPane(new PatientPresenter(model));
        currentAction = NEW_PATIENT;

        setDefaultCursor();
    }

    public void showDetailsPane() {
        // Der folgende Guard wird benoetigt, da die momentane Behandlung von
        // "Zurueck"-Aktionen nicht wirklich gut ist.
        if (getCurrentPatient() == null)
            return;

        setTitle("Patient");
        setWindowTitle();

        setVisibleNavigatorButton(BSEARCH, true, false);
        setVisibleNavigatorButton(BNEW, true, false);
        setVisibleNavigatorButton(BQUIT, true, false);
        setVisibleNavigatorButton(BDETAILS, true, true);
        setVisibleNavigatorButton(BDIAGNOSIS, true, false);
        setVisibleNavigatorButton(BMEDICATION, true, false);
        setVisibleNavigatorButton(BBILL, false, false);
        setVisibleNavigatorButton(BFINISH, true, false);
        setVisibleNavigatorButton(BABOUT, false, false);

        setTitle("Patientendetails");
        setHeaderPanel("Patientendetails");
        if (getCurrentDiagnosis() != null)
            oldDiagnosis = getCurrentDiagnosis();
        setCurrentDiagnosis(null);

        PatientModel model = new PatientModel(getCurrentPatient());
        setPane(new PatientPresenter(model));
        currentAction = DETAILS;
    }

    public void showDiagnosisPane() {
        setTitle("Diagnose");
        setWindowTitle();
        setHeaderPanel("Diagnosen");

        setVisibleNavigatorButton(BSEARCH, true, false);
        setVisibleNavigatorButton(BNEW, true, false);
        setVisibleNavigatorButton(BQUIT, true, false);
        setVisibleNavigatorButton(BDETAILS, true, false);
        setVisibleNavigatorButton(BDIAGNOSIS, true, true);
        setVisibleNavigatorButton(BMEDICATION, true, false);
        setVisibleNavigatorButton(BBILL, false, false);
        setVisibleNavigatorButton(BFINISH, true, false);
        setVisibleNavigatorButton(BABOUT, false, false);

        DiagnosisModel model = new DiagnosisModel();

        Patient patient = getCurrentPatient();
        if (patient == null && oldPatient != null) {

            patient = oldPatient;
        }
        if (patient != null) {
            setCurrentPatient(patient);
            model.setPatient(patient);

            setPatientToNavigator(patient);
            navigator.revalidate();
            setPane(new DiagnosisPresenter(model));
            currentAction = DIAGNOSIS;
        } else
            search();

    }

    public void showMedicationPane(TagesDiagnose diagnose) {
        setTitle("Verordnung");
        setWindowTitle();
        setHeaderPanel("Verordnung ("
                + new SimpleDateFormat("dd.MM.yyyy")
                        .format(diagnose.getDatum()) + ")");

        setVisibleNavigatorButton(BSEARCH, true, false);
        setVisibleNavigatorButton(BNEW, true, false);
        setVisibleNavigatorButton(BQUIT, true, false);
        setVisibleNavigatorButton(BDETAILS, true, false);
        setVisibleNavigatorButton(BDIAGNOSIS, true, false);
        setVisibleNavigatorButton(BMEDICATION, true, true);
        setVisibleNavigatorButton(BBILL, false, false);
        setVisibleNavigatorButton(BFINISH, true, false);
        setVisibleNavigatorButton(BABOUT, false, false);

        MedicationModel model = new MedicationModel(diagnose);
        setPane(new MedicationPresenter(model));
        currentAction = MEDICATION;
    }

    private void about() {

        String dialogOne = NAME + " " + VERSION;
        String dialogTwo = getProperties().getProperty("dialog.two");
        String dialogThree = getProperties().getProperty("dialog.three");
        String attention = getProperties().getProperty("attention");

        Object[] msg = new Object[1];

        msg[0] = new JLabel("<html><font face=dialog>" + "<font size=5><b>"
                + dialogOne + "</b></font>" + "<p><b>" + dialogTwo + "</b></p"
                + "<p>" + dialogThree + "</p>" + "<p>&#160;</p>"
                + "<p><font size=1>" + attention + "</font></p>");

        String[] bt = { "OK" };
        JOptionPane.showOptionDialog(this, msg, "Info",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, bt, bt[0]);
    }

    public void setHeaderPanel(String text) {
        title.setText(text);
    }

    public void reload() {
        /**
         * @todo: HACK! Find a better way to suppress committing.
         */
        tracer.trace(DEBUG, "Reloading ...");
        ignoreCommit = true;
        applyAction(currentAction);
        ignoreCommit = false;
    }

    public void finish() {
        applyAction(backAction);
    }

    public void applyAction(int action) {
        switch (action) {
        case SEARCH:
            search();
            break;

        case DIAGNOSIS:
            showDiagnosisPane();
            break;

        case MEDICATION:
            if (getCurrentDiagnosis() == null)
                setCurrentDiagnosis(oldDiagnosis);
            medication();
            break;

        case DETAILS:
            showDetailsPane();
            break;

        case NEW_PATIENT:
            newPatient();
            break;

        case BILL:
            if (getCurrentDiagnosis() == null)
                setCurrentDiagnosis(oldDiagnosis);
            bill();
            break;

        case LETTER:
            if (getCurrentDiagnosis() == null)
                setCurrentDiagnosis(oldDiagnosis);
            letter();
            break;

        case MACRO:
            if (getCurrentDiagnosis() == null)
                setCurrentDiagnosis(oldDiagnosis);
            macro();
            break;

        case CREATE_MACRO:
            /**
             * @todo At the moment it is not possible to return to the create
             *       macro function since the information about selected bill
             *       entries
             */
            bill();
            break;

        default:
            search();
            break;
        }
    }

    public void quit(boolean check) {
        if (check) {
            int action = JOptionPane
                    .showConfirmDialog(
                            this,
                            "Alle Daten wurden gespeichert.\nMöchten Sie das Programm jetzt beenden?",
                            NAME, JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

            if (action == JOptionPane.NO_OPTION) {
                setVisibleNavigatorButton(BQUIT, true, false);
                return;
            }
        }

        setCurrentPatient(null);

        commit();

        try {
            tracer.trace(USER, "Schalter \"Quit\" aktiviert");

            Lock.releaseAll();

        } catch (Exception x) {
            tracer.trace(ERROR_T, x);
        } finally {
            tracer.trace(INFO, "*** " + NAME + " Application ended ***");
            for (int i = 0; i < 5; i++)
                tracer.trace(INFO, "*");
            System.exit(0);
        }
    }

    /**
     * This method controls the navigator panel. Buttons are identified by name.
     */
    public void setVisibleNavigatorButton(String name, boolean visible,
            boolean selected) {

        for (int i = 0; i < navigator.getComponentCount(); i++) {
            Component c = navigator.getComponent(i);

            if (c.getName() != null && c.getName().equals(name)) {
                c.setVisible(visible);
                if (c instanceof JToggleButton) {
                    ((JToggleButton) c).setSelected(selected);
                }
            }
            repaint();
        }
    }

    /**
     * This method changes the current displayed data panel.
     */
    public void setPane(Presenter presenter) {
        if (tracer != null)
            tracer.trace(DEBUG, "calling setPane("
                    + presenter.getClass().getName() + ")");

        commit();

        /** @todo This is pretty stupid, but should work for now. Improve! */
        if (currentPresenter != null) {
            if (currentPresenter.getClass() != presenter.getClass()) {
                backAction = currentAction;
            }
            if (currentPresenter instanceof Observer) {
                lockingInfo.deleteObserver((Observer) currentPresenter);
            }
        }
        if (presenter instanceof Observer) {
            lockingInfo.addObserver((Observer) presenter);
        }
        currentPresenter = presenter;

        if (workspace.getComponentCount() > 0) {
            Component pane = workspace.getComponent(0);
            workspace.remove(pane);
        }
        workspace.add(currentPresenter.createView());
        workspace.revalidate();
        workspace.repaint();

        presenter.activate();

        enableEditing(true);
    }

    public void setDefaultCursor() {
        setCursor(Cursor.getDefaultCursor());
    }

    public void setWaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void setWindowTitle() {
        if (getCurrentPatient() != null)
            setTitle(getCurrentPatient().getFullname() + " - " + NAME);
        else
            setTitle(NAME);
    }

    private void setWindowTitle(boolean check) {
        if (check)
            setTitle(NAME);
        else
            setWindowTitle();
    }

    public void enableEditing(boolean enable) {
        // CAUTION:
        // This method may hang if two or more instances of the application
        // run on the same virtual machine.
        // That would be an AWT bug.
        grayedPane.setVisible(!enable);
    }

    public LockingInfo getLockingInfo() {
        return lockingInfo;
    }

    public Patient getCurrentPatient() {
        return lockingInfo.getPatient();
    }

    public void setCurrentPatient(Patient patient) {
        lockingInfo.setPatient(patient);
    }

    public TagesDiagnose getCurrentDiagnosis() {
        return lockingInfo.getDiagnosis();
    }

    public void setCurrentDiagnosis(TagesDiagnose diagnose) {
        lockingInfo.setDiagnosis(diagnose);
    }

    public Presenter getCurrentPresenter() {
        return currentPresenter;
    }

    /**
     * Returns statical loaded bill items.
     * <p>
     * This method retrieve the bill items from the database on demand.
     * 
     * @exception SQLException
     *                on db error
     */
    public RechnungsPosten[] getRechnungsPosten() throws SQLException {

        if (rechnungsPosten == null) {
            rechnungsPosten = (RechnungsPosten[]) RechnungsPosten.retrieve()
                    .toArray(new RechnungsPosten[0]);
            Arrays.sort(rechnungsPosten);
        }
        return rechnungsPosten;
    }

    /**
     * Sets the navigation tooltip according to the given patient.
     */
    public void setPatientToNavigator(Patient patient) {
        if (patient != null)
            if (patient != null) {
                String anrede = patient.getAnrede();
                String firstName = patient.getVorname();
                String lastName = patient.getName();
                String text = "<html><font face=tahoma size=2><b>" + anrede
                        + ((anrede.equals("")) ? "" : "<br>") + firstName
                        + ((firstName.equals("")) ? "" : "<br>") + lastName
                        + ((lastName.equals("")) ? "" : "<br>");

                if (patient.getGeburtsDatum() != null) {
                    text += MediknightUtilities.formatDate(patient
                            .getGeburtsDatum());
                    if (patient.getAge() > 0) {
                        text += "<br>" + patient.getAge() + " Jahre";
                    }
                }

                text += "</b></font></html>";
                patientLabel.setText(text);

                if (patient.hasBirthday()) {
                    patientLabel.setOpaque(true);
                    patientLabel.setBackground(Color.red);
                } else {
                    patientLabel.setOpaque(false);
                }
            } else {
                patientLabel.setText("");
                patientLabel.setToolTipText("");
            }
    }

    public void selectPatient(Patient patient) throws SQLException {
        setCurrentPatient(patient);
        patient.adjustTagesDiagnosen();
        setCurrentDiagnosis(patient.getLetzteTagesDiagnose());
        //setCurrentDiagnosis(null);
        diagnosis();
    }

    public static void initDB() {
        Datasource[] sources = collectDatasources();
        boolean initialConnectError = true;

        if (sources.length == 0) {
            System.exit(1);
        } else {
            try {
                connectDB(sources[0]);
                initialConnectError = false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (initialConnectError) {
                boolean connected = false;
                Datasource lastSource = null;

                while (!connected) {
                    DatabaseSelectionDialog dialog = new DatabaseSelectionDialog(
                            sources);
                    if (initialConnectError) {
                        initialConnectError = false;
                        dialog
                                .showNoticeInitialDatabaseNotAvailable(sources[0]);
                    } else {
                        dialog.showNoticeDatabaseNotAvailable(lastSource);
                        dialog.selectSource(lastSource);
                    }
                    dialog.show();

                    if (dialog.isCancelled()) {
                        System.exit(0);
                    } else {
                        lastSource = dialog.getSelectedSource();
                        try {
                            connectDB(lastSource);
                            connected = true;
                        } catch (ClassNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static void connectDB(Datasource source)
            throws ClassNotFoundException, SQLException {
        Class.forName(source.driver);
        Datastore.current.connect(source.url, source.user, source.password);
    }

    public static Datasource[] collectDatasources() {
        int i = 1;
        List sources = new ArrayList();

        while (properties.containsKey("dbserver." + i + ".description")) {
            String prefix = "dbserver." + i + ".";
            Datasource source = new MainFrame.Datasource(properties
                    .getProperty(prefix + "description"), properties
                    .getProperty(prefix + "driver"), properties
                    .getProperty(prefix + "url"), properties.getProperty(prefix
                    + "user"), properties.getProperty(prefix + "password"));
            sources.add(source);
            i++;
        }

        return (Datasource[]) sources.toArray(new Datasource[0]);
    }

    public static void storeProperties(Properties prop)
            throws FileNotFoundException, IOException {
        properties = prop;
        properties.store(new FileOutputStream(MEDIKNIGHT_PROPERTIES), "");
    }

    public static void initProperties() throws IOException {
        InputStream is = null;

        try {
            is = new FileInputStream(new File(MEDIKNIGHT_PROPERTIES));
        } catch (FileNotFoundException e) {
            is = MainFrame.class.getClassLoader().getResourceAsStream(
                    PROPERTY_FILENAME);
        }

        properties = new Properties();
        properties.load(is);
        is.close();
        is = null;

        // Merge the user preferences in.
        Properties userPreferences = new Properties();

        try {
            is = new FileInputStream(new File(System.getProperty("user.home"),
                    ".mediknight.properties"));
            userPreferences.load(is);
            Iterator it = userPreferences.keySet().iterator();

            while (it.hasNext()) {
                Object key = it.next();
                properties.put(key, userPreferences.get(key));
            }
        } catch (FileNotFoundException e) {
            System.err.println("No user preferences.");
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static void initTracer() {
        String[] tcs = { DEBUG, ERROR_T, INFO, WARNING, USER, KnightObject.DATA };
        for (int i = 0; i < tcs.length; i++)
            Tracer.addTraceClass(tcs[i]);

        String logPath = properties.getProperty("log.path");
        if (logPath == null || logPath.length() == 0) {
            // No explizit Tracer given, trace out to the console
            tracer = new Tracer(System.out);
        } else {
            // Find the right place for create logfiles.
            // First replace '.' with '/' or '\\'
            StringBuffer sb = new StringBuffer();
            int l = logPath.length();
            for (int i = 0; i < l; i++) {
                char c = logPath.charAt(i);
                sb.append((c == '.') ? File.separatorChar : c);
            }
            logPath = sb.toString();
            String home = System.getProperty("user.home");
            if (home == null || home.length() == 0) {
                home = location.getPath();
            }
            logPath = home + File.separator + logPath;

            //System.out.println("Logfile Basepath: "+logPath);

            String logMaxLength = properties.getProperty("log.maxlength");
            String logMaxCycle = properties.getProperty("log.maxcycle");
            LogWriter writer = new LogWriter(logPath, Long
                    .parseLong(logMaxLength), Integer.parseInt(logMaxCycle));
            tracer = new Tracer(writer);
        }
        KnightObject.setTracer(tracer);

        String traceClasses = properties.getProperty("trace.classes");
        StringTokenizer st = new StringTokenizer(traceClasses, ",");
        while (st.hasMoreTokens())
            tracer.enable(st.nextToken());
    }

    public static Tracer getTracer() {
        return tracer;
    }

    /**
     * Returns application properties.
     */
    public static Properties getProperties() {
        return properties;
    }

    private void applyProperties() {
        String s = properties.getProperty("currency");
        euro = s != null && s.toLowerCase().startsWith("eur");
    }

    /**
     * Returns the user
     */
    public User getUser() {
        return user;
    }

    public boolean isEuro() {
        return euro;
    }

    public int getCurrency() {
        return euro ? CurrencyNumber.EUR : CurrencyNumber.DM;
    }

    public void commit() {
        if (!ignoreCommit && currentPresenter instanceof Commitable) {
            MainFrame app = getApplication();
            LockingInfo li = app.getLockingInfo();
            Patient patient = li.getPatient();
            Lock lock = null;
            try {
                if (patient != null) {
                    lock = patient.acquireLock(li.getAspect(),
                            LockingListener.LOCK_TIMEOUT);
                    if (lock != null) {
                        tracer
                                .trace(DEBUG, "Committing on "
                                        + currentPresenter);
                        ((Commitable) currentPresenter).commit();
                    } else {
                        return;
                    }
                }
            } catch (SQLException sqle) {
            } finally {
                try {
                    lock.release();
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * Returns the application itself.
     */
    public static MainFrame getApplication() {
        return application;
    }

    private void saveSize() {
        try {
            Rectangle r = getBounds();
            int[] bounds = new int[] { r.x, r.y, r.width, r.height };
            UserProperty.save(user, "frame.bounds", MediknightUtilities
                    .writeCSV(bounds));
        } catch (SQLException x) {
            /** @todo: better exception handling */
        }
    }

    public static File getAppLocation() {
        return location;
    }

    //
    // Determines the absolute location of this application.
    //
    public static void initLocation() {
        URL url = MainFrame.class.getClassLoader().getResource(".");
        if (url != null) {
            // We run as an ordinary class collection ...
            location = new File(url.getPath());
        } else {
            // We run as a jar ...
            try {
                String ud = System.getProperty("user.dir");
                String cp = System.getProperty("java.class.path");
                int spos = cp.lastIndexOf(File.separator);
                if (spos > 0) {
                    // Called outside from jar dir
                    location = new File(ud + File.separator
                            + cp.substring(0, spos)).getCanonicalFile();
                    jar = cp.substring(spos + 1);
                } else if (spos == 0) {
                    // Called from root dir
                    location = new File(File.separator);
                    jar = cp.substring(1);
                } else {
                    // Called inside from jar dir
                    location = new File(ud);
                    jar = cp;
                }
            } catch (IOException iox) {
                System.err
                        .println("Could not determine location of the application.");
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
    	SplashWindow splash = null;

        initLocation();
        try {
            initProperties();
            MediknightTheme.install(properties);
            splash = new SplashWindow("de/bo/mediknight/resources/practic-splash.png");

            // determine application name and version
            NAME = getProperties().getProperty("name", "Mediknight");
            VERSION = getProperties().getProperty("version", "2.2");

            // Global variable 'application' set by the constructor
            new MainFrame();

            application.applyProperties();

            initTracer();

            // DB-Stuff
            String s = properties.getProperty("borm.debug");
            ObjectMapper.debug = Boolean.valueOf(s).booleanValue();

            initDB();

            if (splash != null)
                splash.dispose();

            // Login
            LoginDialog ld = new LoginDialog(application);
            ld.setTitle(NAME + " Anmeldung");
            ld.show();
            if (ld.getUser() == null)
                throw new IllegalAccessException(
                        "Anwender hat sich nicht authentifiziert!");

            application.user = ld.getUser();
            Map map = UserProperty.retrieveUserInformation(application.user);
            String bs = (String) map.get("frame.bounds");
            if (bs != null) {
                int[] bounds = MediknightUtilities.readCSV(bs);
                application.setBounds(bounds[0], bounds[1], bounds[2],
                        bounds[3]);
            }

            application.setVisible(true);

            tracer.trace(INFO, "*** " + NAME + " Application started ***");

            // Start with the SearchPanel
            application.search();
        } catch (FileNotFoundException fnfx) {
            System.err.println("No properties found");
            System.exit(1);
        } catch (IllegalAccessException iax) {
            tracer.trace(ERROR_T, iax);
            System.exit(1);
        } catch (RuntimeException rtx) {
            rtx.printStackTrace();
            System.err.println("Property error");
            System.exit(1);
        } catch (Exception x) {
            x.printStackTrace();
            tracer.trace(ERROR_T, x);
        }
    }

    public static class Datasource {

        public String description;

        public String driver;

        public String url;

        public String user;

        public String password;

        public Datasource(String description, String driver, String url,
                String user, String password) {
            this.description = description;
            this.driver = driver;
            this.url = url;
            this.user = user;
            this.password = password;
        }

        public String toString() {
            return description;
        }
    }

    private class GrayedPane extends JComponent {
        private Composite composite;

        private Image bufferImage;

        private AWTEventListener eventListener;

        public GrayedPane() {
            super();

            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    0.7f);
            bufferImage = null;

            MouseInputAdapter mia = new MyMouseInputAdapter(this);
            addMouseListener(mia);
            addMouseMotionListener(mia);

            eventListener = new AWTEventListener() {
                public void eventDispatched(AWTEvent e) {
                    if (e instanceof KeyEvent
                            && e.getSource() instanceof Mutable) {
                        ((KeyEvent) e).consume();
                    }
                }
            };
        }

        public void setVisible(boolean visible) {
            if (visible) {
                Toolkit.getDefaultToolkit().addAWTEventListener(eventListener,
                        AWTEvent.KEY_EVENT_MASK);
            } else {
                Toolkit.getDefaultToolkit().removeAWTEventListener(
                        eventListener);
            }
            super.setVisible(visible);
        }

        public void paint(Graphics g) {
            Rectangle r = workspace.getBounds();
            if (bufferImage == null || bufferImage.getHeight(this) != r.height
                    || bufferImage.getWidth(this) != r.width) {

                rebuildBufferImage(r.width, r.height);
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(composite);
            g2.drawImage(bufferImage, r.x, r.y, this);
        }

        private void rebuildBufferImage(int w, int h) {
            bufferImage = createImage(w, h);
            Graphics g = bufferImage.getGraphics();
            //Color color = UIManager.getColor("effect").brighter();
            Color color = UIManager.getColor("effect");
            g.setColor(color);
            g.fillRect(0, 0, w, h);

            g.setColor(color.brighter().brighter());
            Font font = new Font("sanserif", Font.BOLD, 36);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics(font);
            String[] text = { "Bearbeitung nicht möglich!", "",
                    "Die Daten werden von einem", "anderen Benutzer geändert!" };
            for (int i = 0; i < text.length; i++) {
                int th = text.length * (fm.getHeight() + 8);
                int y = (h - th) / 2 + ((fm.getHeight() + 8) * i);
                int x = (w - fm.stringWidth(text[i])) / 2;
                g.drawString(text[i], x, y);
            }
        }
    }

    /**
     * This universal Mouse(Motion)Listener eats all MouseEvents expect those
     * are not under the "blue panel".
     */
    private class MyMouseInputAdapter extends MouseInputAdapter {
        private Component glasspane;

        public void mouseMoved(MouseEvent e) {
            Point p = e.getPoint();
            Point pc = SwingUtilities.convertPoint(glasspane, p, workspace);
            glasspane.setCursor(Cursor
                    .getPredefinedCursor((pc.x < 0) ? Cursor.DEFAULT_CURSOR
                            : Cursor.WAIT_CURSOR));
        }

        public MyMouseInputAdapter(Component glasspane) {
            this.glasspane = glasspane;
        }

        public void mouseDragged(MouseEvent e) {
            redispatchMouseEvent(e);
        }

        public void mouseClicked(MouseEvent e) {
            redispatchMouseEvent(e);
        }

        public void mouseEntered(MouseEvent e) {
            redispatchMouseEvent(e);
        }

        public void mouseExited(MouseEvent e) {
            redispatchMouseEvent(e);
        }

        public void mousePressed(MouseEvent e) {
            redispatchMouseEvent(e);
        }

        public void mouseReleased(MouseEvent e) {
            redispatchMouseEvent(e);
        }

        private void redispatchMouseEvent(MouseEvent e) {
            // Mouse position relativ to the application content (e.g. the
            // glass pane)
            Point p = e.getPoint();
            // Mouse position relativ to the workspace component
            Point pc = SwingUtilities.convertPoint(glasspane, p, workspace);

            if (pc.x < 0) { // we are in navigator, do not eat!
                // Mouse position relativ to the navigator component
                Point pn = SwingUtilities.convertPoint(glasspane, p, navigator);
                // Gets component
                Component comp = SwingUtilities.getDeepestComponentAt(
                        navigator, pn.x, pn.y);
                if (comp != null) { // redispatch
                    // Mouse position relativ to the retrieved component
                    pc = SwingUtilities.convertPoint(glasspane, p, comp);
                    // do event
                    comp.dispatchEvent(new MouseEvent(comp, e.getID(), e
                            .getWhen(), e.getModifiers(), pc.x, pc.y, e
                            .getClickCount(), e.isPopupTrigger()));
                }
            }
        }
    }
}