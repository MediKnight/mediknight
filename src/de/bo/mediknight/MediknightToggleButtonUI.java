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
 * A new LAF for the <code>JToggleButton</code> which uses a gradient to paint
 * the button's beackground.
 *
 * @author sma@baltic-online.de
 */
public class MediknightToggleButtonUI extends MetalToggleButtonUI {

    /**
     * Constructs a new UI class for toggle buttons.
     */
    public static ComponentUI createUI(JComponent c) {
        return buttonUI;
    }

    /**
     * Setup the component for the look. Lie to the button that is isn't opaque.
     */
    public void installUI(JComponent c) {
        super.installUI(c);
        c.setOpaque(false);
    }

    /**
     * Paint the component.  If opaque, fill the component's background with
     * a gradient.
     */
    public void paint(Graphics g, JComponent c) {
        if (((AbstractButton)c).isContentAreaFilled()) {
            MediknightButtonUI.paintGradient((Graphics2D)g,
                c.getBackground(),
                c.getWidth(),
                c.getHeight());
        }
        super.paint(g, c);
    }

    /**
     * Paint the component in a pressed state.  If opaque, fill the component's
     * background with a gradient.
     */
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if (b.isContentAreaFilled()) {
            if (b.getModel().isPressed()) {
                super.paintButtonPressed(g, b);
            } else {
                MediknightButtonUI.paintGradient((Graphics2D)g,
                    UIManager.getColor("ToggleButton.selectedBackground"),
                    b.getWidth(),
                    b.getHeight());
            }
	}
    }

    protected void paintFocusX(Graphics g, AbstractButton b,
			      Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        ButtonModel model = b.getModel();
        if (model.isSelected() && !model.isPressed()) {
            Color oldColor = focusColor;
            focusColor = selectColor;
            super.paintFocus(g, b, viewRect, textRect, iconRect);
            focusColor = oldColor;
        } else
            super.paintFocus(g, b, viewRect, textRect, iconRect);
    }

    // WARNING: MetalToggleButtonUI incorrectly defines this method as JComponent
    //  instead of AbstractButton so that method is never called which is
    //  obviously a bug in upto at least JDK 1.3.  This is the correct method
    //  which is actually called by BasicToggleButtonUI.paint().
    protected void paintText(Graphics g, AbstractButton b,
			     Rectangle textRect, String text) {
        ButtonModel model = b.getModel();
        if (false && model.isEnabled() && model.isSelected() && !model.isPressed()) {
            g.setColor(UIManager.getColor(getPropertyPrefix() + "selectionForeground"));
            BasicGraphicsUtils.drawString(g, text, model.getMnemonic(), textRect.x, textRect.y + g.getFontMetrics().getAscent());
        } else
            super.paintText(g, (JComponent)b, textRect, text);
    }

    // optimization as in the MetalButtonUI class
    private static final ComponentUI buttonUI = new MediknightToggleButtonUI();
}