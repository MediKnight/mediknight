package de.bo.mediknight.tools;

import de.bo.mediknight.*;
import java.awt.Component;
import java.awt.*;
import javax.swing.*;

import de.bo.mediknight.domain.KnightObject;

public class UserAdministrationPresenter implements Presenter, Commitable {

    UserAdministrationModel model;
    UserAdministrationPanel view;


    public UserAdministrationPresenter() {

    }

    public UserAdministrationPresenter( UserAdministrationModel model ) {
        this.model = model;
    }

    public UserAdministrationModel getModel() {
        return model;
    }

    public void activate() {}

    public Component createView() {
        view = new UserAdministrationPanel();
        view.setPresenter( this );

        return view;
    }

    public void commit() {
        view.save();
    }

    public Component getResponsibleComponent() {
        return null;
    }

    public void reload(Component component,KnightObject knightObject) {
    }

    public static void main(String[] args) {
        try {
            MainFrame.initProperties();
            MainFrame.initTracer();
            MainFrame.initDB();
   //         MediknightTheme.install();

        } catch (Exception e) {
            e.printStackTrace();
        }
        UserAdministrationModel model = new UserAdministrationModel();

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout( new BorderLayout());
        UserAdministrationPresenter presenter = new UserAdministrationPresenter( model );
        frame.getContentPane().add(presenter.createView(), BorderLayout.CENTER );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.show();
        frame.pack();
    }

}