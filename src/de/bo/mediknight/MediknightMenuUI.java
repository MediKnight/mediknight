/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.bo.mediknight;

import java.awt.Container;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;


/**
 * A new LAF for the <code>JMenuBar</code>. The menu bars gets a 3D button effect.
 * 
 * @see MediknightMenuBarUI
 *
 * @author sma@baltic-online.de
 */
public class MediknightMenuUI extends BasicMenuUI {

    /**
     * Constructs a new UI for menu bar buttons.
     */
    public static ComponentUI createUI( final JComponent x ) {
	return new MediknightMenuUI();
    }


    /**
     * Paints the component's background. Overwritten to make the component transparent so that the menu bar's background is visible.
     */
    @Override
    public void update( final Graphics g, final JComponent c ) {
	final Container parent = menuItem.getParent();
	if( parent != null && parent instanceof JMenuBar && !menuItem.isSelected() ) {
	    // menu items in the menubar will never be opaque unless they are
	    // selected
	    final boolean isMenuItemOpaque = menuItem.isOpaque();
	    menuItem.setOpaque( false );
	    super.update( g, c );
	    menuItem.setOpaque( isMenuItemOpaque );
	} else {
	    super.update( g, c );
	}
    }
}
