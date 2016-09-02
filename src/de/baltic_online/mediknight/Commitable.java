package de.baltic_online.mediknight;

import java.awt.Component;

import de.baltic_online.mediknight.domain.KnightObject;


public interface Commitable {

    void commit();


    Component getResponsibleComponent();


    void reload( Component component, KnightObject knightObject );
}