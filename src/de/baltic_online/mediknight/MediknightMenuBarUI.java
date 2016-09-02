/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.baltic_online.mediknight;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;


/**
 * A new LAF for the <code>JMenuBar</code>. The menu bars gets a 3D button effect.
 * 
 * @see MediknightMenuUI
 *
 * @author sma@baltic-online.de
 */
public class MediknightMenuBarUI extends BasicMenuBarUI {

    /**
     * Constructs a new UI class for menu bars.
     */
    public static ComponentUI createUI( final JComponent c ) {
	return new MediknightMenuBarUI();
    }


    /**
     * Paints the component's background.
     */
    @Override
    public void paint( final Graphics g, final JComponent c ) {
	MediknightButtonUI.paintGradient( (Graphics2D) g, c.getBackground(), c.getWidth(), c.getHeight() );
    }
}