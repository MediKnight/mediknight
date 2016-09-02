package de.baltic_online.mediknight;

import java.awt.Component;


public interface Presenter {

    void activate();


    Component createView();
}