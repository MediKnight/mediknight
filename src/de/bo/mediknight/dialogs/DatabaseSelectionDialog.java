package de.bo.mediknight.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;

import de.bo.mediknight.MainFrame;
import de.bo.mediknight.MainFrame.Datasource;
import de.bo.mediknight.widgets.JButton;
import de.bo.mediknight.widgets.JComboBox;
import de.bo.mediknight.widgets.JPanel;
import de.bo.mediknight.widgets.JTextArea;

public class DatabaseSelectionDialog extends JDialog {

    MainFrame.Datasource[] sources;

    JTextArea messageTA;

    JComboBox databaseCB;

    JButton connectButton;

    JButton quitButton;

    boolean cancelled = true;

    public DatabaseSelectionDialog(Datasource[] sources) {
        super();
        this.sources = sources;
        setTitle(MainFrame.getProperties().getProperty("name")
                + " - Datenbank wählen");
        setModal(true);
        createUI();
        connectUI();
        configureUI();
        
        pack();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Datasource getSelectedSource() {
        return (Datasource) databaseCB.getSelectedItem();
    }

    private void connect() {
        cancelled = false;
        dispose();
    }

    private void cancel() {
        cancelled = true;
        dispose();
    }

    private void configureUI() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();

        for (int i = 0; i < sources.length; i++) {
            model.addElement(sources[i]);
        }
        databaseCB.setModel(model);
        databaseCB.setSelectedIndex(1);
    }

    private void createUI() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel helperPanel = new JPanel(new GridBagLayout());

        messageTA = new JTextArea();
        messageTA.setEditable(false);
        messageTA.setWrapStyleWord(true);
        messageTA.setLineWrap(true);
        messageTA.setOpaque(false);
        messageTA.setSize(new Dimension(400, 1));

        databaseCB = new JComboBox();
        connectButton = new JButton("Verbinden");
        quitButton = new JButton("Beenden");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 5, 0));
        buttonPanel.add(connectButton);
        buttonPanel.add(quitButton);

        helperPanel
                .add(databaseCB, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(6, 12, 24, 12), 0, 0));

        helperPanel.add(buttonPanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
                0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        contentPanel.add(messageTA, BorderLayout.NORTH);
        contentPanel.add(helperPanel, BorderLayout.CENTER);

        setContentPane(contentPanel);
    }

    private void connectUI() {
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });

    }

    public void showNoticeInitialDatabaseNotAvailable(Datasource source) {
        messageTA.setText("Die als Standard konfigurierte Datenbank '" + source
                + "' konnte nicht "
                + "erreicht werden. Bitte wählen Sie eine Datenbank mit einer "
                + "aktuellen Sicherung der Hauptdatenbank aus.\n\n"
                + "Bitte beachten Sie, dass es zu Datenverlusten kommen kann, "
                + "falls die Daten der gewählten Datenbank nicht hinreichend "
                + "aktuell sind.");
        pack();
    }

    public void showNoticeDatabaseNotAvailable(Datasource source) {
        messageTA.setText("Die Datenbank '" + source + "' ist nicht "
                + "erreichbar. Bitte wählen Sie eine Datenbank mit einer "
                + "aktuellen Sicherung der Hauptdatenbank aus.\n\n"
                + "Bitte beachten Sie, dass es zu Datenverlusten kommen kann, "
                + "falls die Daten der gewählten Datenbank nicht hinreichend "
                + "aktuell sind.");
        pack();
    }

    public void selectSource(Datasource source) {
        databaseCB.setSelectedItem(source);
    }
}