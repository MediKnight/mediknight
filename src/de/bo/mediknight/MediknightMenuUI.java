/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.bo.mediknight;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

/**
 * A new LAF for the <code>JMenuBar</code>. The menu bars gets a 3D button
 * effect.
 * @see MediknightMenuBarUI
 *
 * @author sma@baltic-online.de
 */
public class MediknightMenuUI extends BasicMenuUI {

    /**
     * Constructs a new UI for menu bar buttons.
     */
    public static ComponentUI createUI(JComponent x) {
        return new MediknightMenuUI();
    }

    /**
     * Paints the component's background.  Overwritten to make the component
     * transparent so that the menu bar's background is visible.
     */
    public void update(Graphics g, JComponent c) {
        Container parent = menuItem.getParent();
        if (parent != null && parent instanceof JMenuBar && !menuItem.isSelected()) {
            // menu items in the menubar will never be opaque unless they are selected
            boolean isMenuItemOpaque = menuItem.isOpaque();
            menuItem.setOpaque(false);
            super.update(g, c);
            menuItem.setOpaque(isMenuItemOpaque);
        } else {
            super.update(g, c);
        }
    }
}
