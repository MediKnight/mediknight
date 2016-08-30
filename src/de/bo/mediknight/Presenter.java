package de.bo.mediknight;

import java.awt.Component;


public interface Presenter {

    void activate();


    Component createView();
}