/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.bo.mediknight;

import java.awt.*;
import javax.swing.border.*;

/**
 * This kind of border should replace TitledBorder instances.  Instead of
 * ugly boxes, it just draws a nice separator line above the box.
 *
 * @author sma@baltic-online.de
 */
public class SeparatorBorder extends EtchedBorder {

    public static Border createBorder() {
        return new SeparatorBorder();
    }

    public static Border createBorder(String title) {
        return new TitledBorder(new SeparatorBorder(),
            title, TitledBorder.CENTER, TitledBorder.TOP);
    }

    /**
     * Constructs a new separator border line.
     */
    SeparatorBorder() {
        this(null);
    }

    /**
     * Construcs a new separator border line with the given title.
     */
    SeparatorBorder(String title) {
        super();
    }

    /**
     * Paints the border for the specified component with the specified
     * position and size.
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.translate(x, y);
        g.setColor(etchType == LOWERED? getShadowColor(c) : getHighlightColor(c));
        g.drawLine(0, 0, width-1, 0);
        g.setColor(etchType == LOWERED? getHighlightColor(c) : getShadowColor(c));
        g.drawLine(0, 1, width-1, 1);
        g.translate(-x, -y);
    }

    /**
     * Reinitializes the insets parameter with this Border's current Insets.
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.bottom = 0;
        insets.top = 2;

        return insets;
    }
}
