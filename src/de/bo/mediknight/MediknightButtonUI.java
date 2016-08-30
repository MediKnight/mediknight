/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.bo.mediknight;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.text.View;


/**
 * A new LAF for the <code>JButton</code> which uses a gradient to paint the button's beackground and marks default buttons with a CR icon.
 *
 * @author sma@baltic-online.de
 */
public class MediknightButtonUI extends MetalButtonUI implements SwingConstants {

    protected static Color	   defaultBackgroundColor;
    protected static Icon	    defaultDefaultIcon;

    // referenced in paint(), these are private to MetalButtonUI
    private static Rectangle	 viewRect = new Rectangle();

    private static Rectangle	 textRect = new Rectangle();

    private static Rectangle	 iconRect = new Rectangle();

    // optimization as in the MetalButtonUI class
    private static final ComponentUI buttonUI = new MediknightButtonUI();


    /**
     * Returns a brighter color. Same algorithms as <code>Color.brighter()</code> but with a variable factor.
     */
    static final Color brighter( final Color color, final double factor ) {
	int r = color.getRed();
	int g = color.getGreen();
	int b = color.getBlue();

	final int i = (int) (1.0 / (1.0 - factor));
	if( r == 0 && g == 0 && b == 0 ) {
	    return new Color( i, i, i );
	}
	if( r > 0 && r < i ) {
	    r = i;
	}
	if( g > 0 && g < i ) {
	    g = i;
	}
	if( b > 0 && b < i ) {
	    b = i;
	}

	return new Color( Math.min( (int) (r / factor), 255 ), Math.min( (int) (g / factor), 255 ), Math.min( (int) (b / factor), 255 ) );
    }


    /**
     * Constructs a new UI class for push buttons.
     */
    public static ComponentUI createUI( final JComponent c ) {
	return buttonUI;
    }


    /**
     * Returns a darker color. Same algorithms as <code>Color.darker()</code> but with a variable factor.
     */
    static final Color darker( final Color color, final double factor ) {
	return new Color( Math.max( (int) (color.getRed() * factor), 0 ), Math.max( (int) (color.getGreen() * factor), 0 ), Math.max(
		(int) (color.getBlue() * factor), 0 ) );
    }


    /**
     * Paints the gradient.
     * 
     * @param g
     *            the <code>Graphcis</code> object to paint to
     * @param color
     *            base color, gradient goes from brighter() to darker().
     * @param w
     *            the component's width
     * @param h
     *            the component's height
     */
    public static void paintGradient( final Graphics2D g, final Color color, final int w, final int h ) {
	g.setPaint( new GradientPaint( 0f, 0f, brighter( color, 0.8 ), 0f, h * 0.4f, color, false ) );
	g.fillRect( 0, 0, w, h / 2 );
	g.setPaint( new GradientPaint( 0f, h * 0.6f, color, 0f, h, darker( color, 0.8 ), false ) );
	g.fillRect( 0, h / 2, w, h / 2 );
    }


    // helper methods

    @Override
    public void installDefaults( final AbstractButton b ) {
	super.installDefaults( b );
	if( defaultBackgroundColor == null ) {
	    defaultBackgroundColor = UIManager.getColor( "Button.defaultBackground" );
	    defaultDefaultIcon = new ImageIcon( getClass().getResource( "cr.gif" ) );
	}
    }


    /**
     * Paint the component. If opaque, fill the component's background with a gradient. The default button is additionally marked with a CR-icon. This method is
     * postly copied from <code>MetalButtonUI</code> with some changes to support the CR-icon.
     */
    @Override
    public void paint( final Graphics g, final JComponent c ) {
	final AbstractButton b = (AbstractButton) c;
	final ButtonModel model = b.getModel();

	final FontMetrics fm = g.getFontMetrics();

	final Insets i = c.getInsets();

	viewRect.x = i.left;
	viewRect.y = i.top;
	viewRect.width = b.getWidth() - (i.right + viewRect.x);
	viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

	textRect.x = textRect.y = textRect.width = textRect.height = 0;
	iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

	// if an default button has no icon, fill in the CR icon
	Icon icon = b.getIcon();
	boolean hasDefaultIcon;
	if( icon == null && ((JButton) b).isDefaultButton() ) {
	    hasDefaultIcon = true;
	    icon = defaultDefaultIcon;
	    viewRect.x -= 8;
	    viewRect.width += 16;
	} else {
	    hasDefaultIcon = false;
	}

	final Font f = c.getFont();
	g.setFont( f );

	// layout the text and icon
	final String text = SwingUtilities.layoutCompoundLabel( c, fm, b.getText(), icon, b.getVerticalAlignment(), b.getHorizontalAlignment(),
		hasDefaultIcon ? CENTER : b.getVerticalTextPosition(), hasDefaultIcon ? LEADING : b.getHorizontalTextPosition(), viewRect, iconRect, textRect,
		b.getText() == null ? 0 : hasDefaultIcon ? 2 : defaultTextIconGap );

	clearTextShiftOffset();

	// paint gradient
	if( b.isContentAreaFilled() ) {
	    final Color color = ((JButton) b).isDefaultButton() ? defaultBackgroundColor : c.getBackground();
	    paintGradient( (Graphics2D) g, color, c.getWidth(), c.getHeight() );
	}

	// perform UI specific press action, e.g. Windows L&F shifts text
	if( model.isArmed() && model.isPressed() ) {
	    paintButtonPressed( g, b );
	}

	// Paint the Icon
	if( icon != null ) {
	    if( hasDefaultIcon ) {
		icon.paintIcon( c, g, iconRect.x, iconRect.y );
	    } else {
		paintIcon( g, c, iconRect );
	    }
	}

	if( text != null && !text.equals( "" ) ) {
	    final View v = (View) c.getClientProperty( BasicHTML.propertyKey );
	    if( v != null ) {
		v.paint( g, textRect );
	    } else {
		paintText( g, c, textRect, text );
	    }
	}

	if( b.isFocusPainted() && b.hasFocus() ) {
	    // paint UI specific focus
	    if( hasDefaultIcon ) {
		textRect.width += 16;
	    }
	    paintFocus( g, b, viewRect, textRect, iconRect );
	}
    }


    @Override
    public void uninstallDefaults( final AbstractButton b ) {
	super.uninstallDefaults( b );
	defaultBackgroundColor = null;
    }
}