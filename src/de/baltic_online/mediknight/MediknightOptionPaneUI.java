/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.baltic_online.mediknight;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;


/**
 * A new LAF for the <code>JOptionPane</code> which right-aligns buttons. It also makes sure that button have a minimum width of at least 80 pixels.
 *
 * @author sma@baltic-online.de
 */
public class MediknightOptionPaneUI extends BasicOptionPaneUI {

    /**
     * Overwritten to right-align buttons and apply a minimum width.
     */
    protected static class MyButtonAreaLayout extends ButtonAreaLayout {

	private static final int MINWIDTH = 80;


	MyButtonAreaLayout( final boolean syncAllWidths, final int padding ) {
	    super( syncAllWidths, padding );
	}


	/* Overwritten to right-align buttons with MINSIZE respected */
	@Override
	public void layoutContainer( final Container container ) {
	    final Component[] children = container.getComponents();
	    if( children == null ) {
		return;
	    }
	    int numChildren = children.length;
	    if( syncAllWidths ) {
		int width = MINWIDTH;
		int height = 0;
		for( int i = 0; i < numChildren; i++ ) {
		    final Dimension d = children[i].getPreferredSize();
		    if( d.width > width ) {
			width = d.width;
		    }
		    if( d.height > height ) {
			height = d.height;
		    }
		}
		final Insets insets = container.getInsets();
		int xLocation = container.getWidth() + padding - insets.right;
		final int yLocation = insets.top;
		while( --numChildren >= 0 ) {
		    xLocation -= width + padding;
		    children[numChildren].setBounds( xLocation, yLocation, width, height );
		}
	    } else {
		super.layoutContainer( container );
	    }
	}


	/* Overwritten to add MINSIZE to algorithm */
	@Override
	public Dimension minimumLayoutSize( final Container c ) {
	    if( c != null ) {
		final Component[] children = c.getComponents();
		if( children != null && children.length > 0 ) {
		    final int numChildren = children.length;
		    final Insets insets = c.getInsets();
		    if( syncAllWidths ) {
			int width = MINWIDTH;
			int height = 0;
			for( int i = 0; i < numChildren; i++ ) {
			    final Dimension d = children[i].getPreferredSize();
			    if( d.width > width ) {
				width = d.width;
			    }
			    if( d.height > height ) {
				height = d.height;
			    }
			}
			width = (width + padding) * numChildren - padding;
			width += insets.left + insets.right;
			height += insets.top + insets.bottom;
			return new Dimension( width, height );
		    }
		}
	    }
	    return super.minimumLayoutSize( c );
	}
    }


    /**
     * Constructs a new UI class for option panes.
     */
    public static ComponentUI createUI( final JComponent x ) {
	return new MediknightOptionPaneUI();
    }


    /**
     * Creates and returns a Container containing the buttons. The buttons are created by calling <code>getButtons</code>.
     * <p>
     * Overwritten to use my own layout manager which <b>must</b> unfortunately be a subclass of <code>ButtonAreaLayout</code> if you don't want to also change
     * <code>addButtonComponents</code>.
     */
    @Override
    protected Container createButtonArea() {
	final JPanel bottom = new JPanel();
	bottom.setBorder( UIManager.getBorder( "OptionPane.buttonAreaBorder" ) );
	bottom.setLayout( new MyButtonAreaLayout( true, 6 ) );
	addButtonComponents( bottom, getButtons(), getInitialValueIndex() );
	return bottom;
    }
}