package de.bo.mediknight.tools;

import de.bo.mediknight.*;
import java.awt.Component;


// for main-method
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import de.bo.mediknight.domain.*;
import java.util.*;


public class MasterDataSupportPresenter implements Presenter, Commitable {
    MasterDataSupportModel model;
    MasterDataSupportPanel view;

    public MasterDataSupportPresenter( MasterDataSupportModel model ) {
        this.model = model;
    }

    public MasterDataSupportModel getModel() {
        return model;
    }

    public void activate() {}

    public Component createView() {
        view = new MasterDataSupportPanel(model.getRechnungsPosten());
        view.setPresenter( this );

        return view;
    }

    public void commit() {
    }

    public void reload(Component component,KnightObject knightObject) {
    }

    public Component getResponsibleComponent() {
        return null;
    }

    public static void main(String[] args) {
        try {
            MainFrame.initProperties();
            MainFrame.initTracer();
            MainFrame.initDB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RechnungsPosten[] posten = null;
        try {
            if ( posten == null ) {
                posten = (RechnungsPosten[])
                    RechnungsPosten.retrieve().toArray(new RechnungsPosten[0]);
                Arrays.sort(posten);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout( new BorderLayout());
        MasterDataSupportModel model = new MasterDataSupportModel( posten );
        MasterDataSupportPresenter presenter = new MasterDataSupportPresenter( model );
        frame.getContentPane().add(presenter.createView(), BorderLayout.CENTER );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.show();
        frame.pack();

    }

}