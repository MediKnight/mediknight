package main.java.de.baltic_online.mediknight;

import java.awt.Component;

import main.java.de.baltic_online.mediknight.domain.KnightObject;


public interface Commitable {

    void commit();


    Component getResponsibleComponent();


    void reload( Component component, KnightObject knightObject );
}