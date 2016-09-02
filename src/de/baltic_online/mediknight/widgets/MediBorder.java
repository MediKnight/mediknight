/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.widgets;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;


public class MediBorder extends AbstractBorder {

    private static final long serialVersionUID = 1L;


    @Override
    public Insets getBorderInsets( final Component c ) {
	return new Insets( 6, 0, 6, 0 );
    }


    @Override
    public boolean isBorderOpaque() {
	return true;
    }


    @Override
    public void paintBorder( final Component c, final Graphics g, final int x, final int y, final int width, final int height ) {
	final Insets insets = getBorderInsets( c );

	g.setColor( UIManager.getColor( "effect" ) );
	g.translate( x, y );
	g.fillRect( 0, height - insets.bottom + 2, width, 2 );
	g.fillRect( 0, height - insets.bottom + 2, width, 2 );
    }
}