/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.bo.mediknight;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

/**
 * A new LAF for the <code>JOptionPane</code> which right-aligns buttons. It
 * also makes sure that button have a minimum width of at least 80 pixels.
 *
 * @author sma@baltic-online.de
 */
public class MediknightOptionPaneUI extends BasicOptionPaneUI {

    /**
     * Constructs a new UI class for option panes.
     */
    public static ComponentUI createUI(JComponent x) {
	return new MediknightOptionPaneUI();
    }

    /**
     * Creates and returns a Container containing the buttons. The buttons
     * are created by calling <code>getButtons</code>.
     * <p>Overwritten to use my own layout manager which <b>must</b>
     * unfortunately be a subclass of <code>ButtonAreaLayout</code> if you don't
     * want to also change <code>addButtonComponents</code>.
     */
    protected Container createButtonArea() {
        JPanel bottom = new JPanel();
        bottom.setBorder(UIManager.getBorder("OptionPane.buttonAreaBorder"));
	bottom.setLayout(new MyButtonAreaLayout(true, 6));
	addButtonComponents(bottom, getButtons(), getInitialValueIndex());
	return bottom;
    }

    /**
     * Overwritten to right-align buttons and apply a minimum width.
     */
    protected static class MyButtonAreaLayout extends ButtonAreaLayout {
        MyButtonAreaLayout(boolean syncAllWidths, int padding) {
            super(syncAllWidths, padding);
        }

        private static final int MINWIDTH = 80;

        /* Overwritten to right-align buttons with MINSIZE respected */
        public void layoutContainer(Container container) {
            Component[] children = container.getComponents();
            if (children == null)
                return;
            int numChildren = children.length;
            if (syncAllWidths) {
                int width = MINWIDTH;
                int height = 0;
                for (int i = 0; i < numChildren; i++) {
                    Dimension d = children[i].getPreferredSize();
                    if (d.width > width)
                        width = d.width;
                    if (d.height > height)
                        height = d.height;
                }
                Insets insets = container.getInsets();
                int xLocation = container.getWidth() + padding - insets.right;
                int yLocation = insets.top;
                while (--numChildren >= 0) {
                    xLocation -= width + padding;
                    children[numChildren].setBounds(xLocation, yLocation, width, height);
                }
            }
            else super.layoutContainer(container);
        }

        /* Overwritten to add MINSIZE to algorithm */
        public Dimension minimumLayoutSize(Container c) {
            if (c != null) {
                Component[] children = c.getComponents();
                if (children != null && children.length > 0) {
                    int numChildren = children.length;
                    Insets insets = c.getInsets();
                    if (syncAllWidths) {
                        int width = MINWIDTH;
                        int height = 0;
                        for (int i = 0; i < numChildren; i++) {
                            Dimension d = children[i].getPreferredSize();
                            if (d.width > width)
                                width = d.width;
                            if (d.height > height)
                                height = d.height;
                        }
                        width = (width + padding) * numChildren - padding;
                        width += insets.left + insets.right;
                        height += insets.top + insets.bottom;
                        return new Dimension(width, height);
                    }
                }
            }
            return super.minimumLayoutSize(c);
        }
    }
}