package de.bo.mediknight.tools;

import de.bo.mediknight.*;
import java.awt.Component;

// for main-method
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import de.bo.mediknight.domain.*;
import java.util.*;

public class MedicationSupportPresenter implements Presenter, Commitable {

    MedicationSupportPanel view;
    MedicationSupportModel model;

    public MedicationSupportPresenter() {
    }

    public MedicationSupportPresenter( MedicationSupportModel model ) {
        this.model = model;
    }

    public void commit() {
        view.saveText();
    }

    public Component getResponsibleComponent() {
        return null;
    }

    public void reload(Component component,KnightObject knightObject) {
    }

    public void activate() {}

    public Component createView() {
        view = new MedicationSupportPanel();
        view.setPresenter( this );

        return view;
    }

    public MedicationSupportModel getModel() {
        return model;
    }

    public void addItem( VerordnungsPosten posten ) {

        try {
            posten.save();
        } catch (java.sql.SQLException e ) {
            e.printStackTrace();
        }
        view.update();
    }

    public void deleteItem( int[] rows ) {
        try {
            for (int i = 0; i < rows.length; i++) {
                model.delete( rows[i] );
            }
        } catch (java.sql.SQLException e ) {
            e.printStackTrace();
        }
        view.update();
    }

    public void saveItem( VerordnungsPosten p ) {
        try {
            p.save();
        } catch (java.sql.SQLException e ) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            MainFrame.initProperties();
            MainFrame.initTracer();
            MainFrame.initDB();
        } catch (Exception e) {
            e.printStackTrace();
        }


        JFrame frame = new JFrame();
        frame.getContentPane().setLayout( new BorderLayout());
        MedicationSupportModel model = new MedicationSupportModel(  );
        MedicationSupportPresenter presenter = new MedicationSupportPresenter( model );

        frame.getContentPane().add(presenter.createView(), BorderLayout.CENTER );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.show();
        frame.pack();

    }
}