package de.bo.mediknight.tools;

import java.util.*;
import java.util.List;
import java.text.*;
import java.io.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import de.bo.mediknight.*;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.borm.*;

public class LockRemover extends JFrame {
    JPanel mainPanel = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JRadioButton selectiveRB = new JRadioButton();
    JScrollPane listSP = new JScrollPane();
    JList lockList = new JList();
    JRadioButton everythingRB = new JRadioButton();
    Border border1;
    JPanel buttonPanel = new JPanel();
    JButton quitButton = new JButton();
    JButton commitButton = new JButton();
    JButton updateButton = new JButton();
    GridLayout gridLayout1 = new GridLayout();
    ButtonGroup group1 = new ButtonGroup();

    static Properties properties;
    List locks = new ArrayList();

    public LockRemover() throws Exception {
        super("Sperrungen aufheben");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initDB();
        jbInit();
        initUI();
        connectUI();
        updateEnablement();
    }

    List retrieveActiveLocks() throws SQLException {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        List results = new ArrayList();
        Query query = Datastore.current.getQuery(Lock.class);
        Iterator it = query.execute();
        while (it.hasNext()) {
            String description = "Stammdaten";
            Lock lock = (Lock) it.next();
            Patient patient = Patient.retrieve(lock.getPatientId());

            if (lock.getAspect() != null && lock.getAspect().length() > 0) {
                Query tdQuery = Datastore.current.getQuery(TagesDiagnose.class, "id = ?");
                tdQuery.bind(1, Integer.valueOf(lock.getAspect()));
                Iterator tdIterator = tdQuery.execute();
                if (tdIterator.hasNext()) {
                    TagesDiagnose diagnose = (TagesDiagnose) tdIterator.next();
                    description = "Tagesdiagnose vom " + df.format(diagnose.getDatum());
                } else {
                    description = "Gelöschte Tagesdiagnose";
                }
            }

            LockEntry entry =
                new LockEntry(lock, patient.getName(), patient.getVorname(), description);
            results.add(entry);
        }

        return results;
    }

    void initUI() throws SQLException {
        updateList();
        selectiveRB.setSelected(true);
    }

    void updateList() {
        try {
            locks = retrieveActiveLocks();
            lockList.setListData(locks.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void connectUI() {
        commitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commit();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateList();
            }
        });

        selectiveRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateEnablement();
            }
        });

        everythingRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateEnablement();
            }
        });

        lockList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateEnablement();
            }
        });
    }

    boolean confirmDeletion(Object[] entries) {
        String message = "Sollen die folgenden Sperren aufgehoben werden?\n";
        String[] options = new String[] { "OK", "Abbrechen" };

        for (int i = 0; i < entries.length; i++) {
            message += "    " + entries[i] + "\n";
        }

        int option = JOptionPane.showOptionDialog(this,
            message,
            "Aufhebung bestätigen",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]);
        return option == 0;
    }

    void commit() {
        Object[] entries = null;

        if (selectiveRB.isSelected()) {
            entries = lockList.getSelectedValues();
        } else {
            entries = locks.toArray(new LockEntry[0]);
        }

        if (confirmDeletion(entries)) {
            for (int i = 0; i < entries.length; i++) {
                try {
                    ((LockEntry) entries[i]).lock.setIdentity();
                    ((LockEntry) entries[i]).lock.delete();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            updateList();
        }
    }

    void updateEnablement() {
        commitButton.setEnabled(
            everythingRB.isSelected() ||
            (lockList.getSelectedValues() != null && lockList.getSelectedValues().length > 0));
        lockList.setEnabled(selectiveRB.isSelected());
    }

    void exit() {
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        properties = initProperties();
        MediknightTheme.install(properties);

        LockRemover frame = new LockRemover();
        frame.setSize(500, 300);
        frame.show();
    }

    public static Properties initProperties() throws IOException {
        InputStream is = null;

        try {
            is = new FileInputStream(new File(MainFrame.MEDIKNIGHT_PROPERTIES));
        } catch( FileNotFoundException e ) {
            is = MainFrame.class.getClassLoader().getResourceAsStream(MainFrame.PROPERTY_FILENAME);
        }

        Properties properties = new Properties();
        properties.load(is);
        is.close();

        return properties;
    }

    public static void initDB() throws Exception {
        String driverName = properties.getProperty("jdbc.driver.name");
        Class.forName(driverName).newInstance();

        String jdbcURL = properties.getProperty("jdbc.url.name");

        System.out.println("Connecting to "+jdbcURL);

        String user = properties.getProperty("jdbc.db.user");
        String passwd = properties.getProperty("jdbc.db.passwd");
        Datastore.current.connect(jdbcURL,user,passwd);
    }


    private void jbInit() {
        border1 = BorderFactory.createEmptyBorder(5,10,10,10);
        mainPanel.setLayout(gridBagLayout1);
        selectiveRB.setText("Einzelne Sperrungen aufheben");
        everythingRB.setText("Alle Sperrungen aufheben");
        mainPanel.setBorder(border1);
        quitButton.setText("Beenden");
        commitButton.setText("Aufheben");
        buttonPanel.setLayout(gridLayout1);
        gridLayout1.setHgap(5);
        updateButton.setText("Aktualisieren");
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(selectiveRB, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(listSP, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 17, 0, 0), 0, 0));
        mainPanel.add(everythingRB, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(buttonPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        buttonPanel.add(commitButton, null);
        buttonPanel.add(updateButton, null);
        buttonPanel.add(quitButton, null);
        listSP.getViewport().add(lockList, null);
        group1.add(selectiveRB);
        group1.add(everythingRB);
    }
}

class LockEntry {
    Lock lock;
    String name;
    String surname;
    String description;

    LockEntry(Lock lock, String name, String surname, String description) {
        this.lock = lock;
        this.name = name;
        this.surname = surname;
        this.description = description;
    }

    public String toString() {
        String s = name;
        if (surname != null && surname.length() > 0)
            s += ", " + surname;

        s += ": " + description;

        return s;
    }
}