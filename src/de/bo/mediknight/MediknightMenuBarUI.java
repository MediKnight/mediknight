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
 * @see MediknightMenuUI
 *
 * @author sma@baltic-online.de
 */
public class MediknightMenuBarUI extends BasicMenuBarUI {

    /**
     * Constructs a new UI class for menu bars.
     */
    public static ComponentUI createUI(JComponent c) {
        return new MediknightMenuBarUI();
    }

    /**
     * Paints the component's background.
     */
    public void paint(Graphics g, JComponent c) {
        MediknightButtonUI.paintGradient((Graphics2D)g,
            c.getBackground(),
            c.getWidth(),
            c.getHeight());
    }
}