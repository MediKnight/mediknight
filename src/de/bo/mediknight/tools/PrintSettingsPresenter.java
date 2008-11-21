package de.bo.mediknight.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import de.bo.mediknight.*;


import de.bo.mediknight.domain.KnightObject;
import de.bo.mediknight.MainFrame;

/**
 * Hier sind alle Eigenschaften des Programms gespeichert. Zum Beispiel
 * welche Daten das Logo enthält.
 * 
 */
public class PrintSettingsPresenter implements Presenter, Commitable {

    PrintSettingsModel model;
    PrintSettingsPanel view;

    public PrintSettingsPresenter(PrintSettingsModel model) {
        this.model = model;
    }

    public PrintSettingsModel getModel() {
        return model;
    }

    public void activate() {}

    public Component createView() {
        view = new PrintSettingsPanel( this );

        return view;
    }

    public static Map getSettings() {
        PrintSettingsModel model = new PrintSettingsModel();
        return model.getMap();
    }

    public void commit() {
        view.saveEntries();
    }

    public Component getResponsibleComponent() {
        /** @todo: implement this! */
        return null;
    }

    public void reload(Component component,KnightObject knightObject) {
    }

    public static void main(String[] args) throws Exception {

        try {
            MainFrame.initProperties();
            MainFrame.initTracer();
            MainFrame.initDB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintSettingsModel model = new PrintSettingsModel( );
        PrintSettingsPresenter presenter = new PrintSettingsPresenter( model );

        JFrame f = new JFrame("PrintSettings Test");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(presenter.createView(), BorderLayout.CENTER);
        f.pack();
        f.setVisible(true);
    }
}