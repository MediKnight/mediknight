/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MediBorder extends AbstractBorder {

    public Insets getBorderInsets(Component c) {
        return new Insets(6,0,6,0);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Insets insets = getBorderInsets(c);

        g.setColor(UIManager.getColor("effect"));
        g.translate(x, y);
        g.fillRect(0,height-insets.bottom+2,width,2);
        g.fillRect(0,height-insets.bottom+2,width,2);
    }
}