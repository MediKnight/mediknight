package de.bo.mediknight;

import java.awt.*;

public interface Presenter {
    Component createView();
    void activate();
}