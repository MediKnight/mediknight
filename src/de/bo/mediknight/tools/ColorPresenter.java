package de.bo.mediknight.tools;

import de.bo.mediknight.*;
import de.bo.mediknight.domain.*;
import java.awt.Component;
import java.util.HashMap;

import javax.swing.*;
import java.awt.*;

public class ColorPresenter implements Presenter, Commitable {

    ColorModel model;
    ColorPanel view;

    public ColorPresenter() {
        model = new ColorModel();
    }

    public void commit() {
        try {
            model.saveProperties(view.getProperties());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void activate() {
    }

    public void reload(Component component,KnightObject knightObject) {
    }

    public Component getResponsibleComponent() {
        return null;
    }

    public Component createView() {
        view =  new ColorPanel(model.getProperties());
        return view;
    }

    public void saveProperties(HashMap<String, Object> map) {
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
        ColorModel model = new ColorModel();

        JFrame frame = new JFrame();
        frame.getContentPane().setLayout( new BorderLayout());
        ColorPresenter presenter = new ColorPresenter(  );
        frame.getContentPane().add(presenter.createView(), BorderLayout.CENTER );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible(true);
        frame.pack();
    }
}