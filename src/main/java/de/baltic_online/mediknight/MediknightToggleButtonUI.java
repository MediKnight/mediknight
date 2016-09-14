/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package main.java.de.baltic_online.mediknight;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;


/**
 * A new LAF for the <code>JToggleButton</code> which uses a gradient to paint the button's beackground.
 *
 * @author sma@baltic-online.de
 */
public class MediknightToggleButtonUI extends MetalToggleButtonUI {

    // optimization as in the MetalButtonUI class
    private static final ComponentUI buttonUI = new MediknightToggleButtonUI();


    /**
     * Constructs a new UI class for toggle buttons.
     */
    public static ComponentUI createUI( final JComponent c ) {
	return buttonUI;
    }


    /**
     * Setup the component for the look. Lie to the button that is isn't opaque.
     */
    @Override
    public void installUI( final JComponent c ) {
	super.installUI( c );
	c.setOpaque( false );
    }


    /**
     * Paint the component. If opaque, fill the component's background with a gradient.
     */
    @Override
    public void paint( final Graphics g, final JComponent c ) {
	if( ((AbstractButton) c).isContentAreaFilled() ) {
	    MediknightButtonUI.paintGradient( (Graphics2D) g, c.getBackground(), c.getWidth(), c.getHeight() );
	}
	super.paint( g, c );
    }


    /**
     * Paint the component in a pressed state. If opaque, fill the component's background with a gradient.
     */
    @Override
    protected void paintButtonPressed( final Graphics g, final AbstractButton b ) {
	if( b.isContentAreaFilled() ) {
	    if( b.getModel().isPressed() ) {
		super.paintButtonPressed( g, b );
	    } else {
		MediknightButtonUI.paintGradient( (Graphics2D) g, UIManager.getColor( "ToggleButton.selectedBackground" ), b.getWidth(), b.getHeight() );
	    }
	}
    }


    protected void paintFocusX( final Graphics g, final AbstractButton b, final Rectangle viewRect, final Rectangle textRect, final Rectangle iconRect ) {
	final ButtonModel model = b.getModel();
	if( model.isSelected() && !model.isPressed() ) {
	    final Color oldColor = focusColor;
	    focusColor = selectColor;
	    super.paintFocus( g, b, viewRect, textRect, iconRect );
	    focusColor = oldColor;
	} else {
	    super.paintFocus( g, b, viewRect, textRect, iconRect );
	}
    }


    // WARNING: MetalToggleButtonUI incorrectly defines this method as
    // JComponent
    // instead of AbstractButton so that method is never called which is
    // obviously a bug in upto at least JDK 1.3. This is the correct method
    // which is actually called by BasicToggleButtonUI.paint().
    @Override
    protected void paintText( final Graphics g, final AbstractButton b, final Rectangle textRect, final String text ) {
	super.paintText( g, (JComponent) b, textRect, text );
    }
}