package de.bo.mediknight;

import java.awt.Component;

import de.bo.mediknight.domain.KnightObject;

public interface Commitable {
    void commit();
    Component getResponsibleComponent();
    void reload(Component component,KnightObject knightObject);
}