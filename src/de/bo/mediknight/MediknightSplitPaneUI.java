/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.bo.mediknight;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalSplitPaneUI;


/**
 * A new LAF for the <code>JSplitPane</code>. The usual bumps are replaced with the same gradient which is also used for scroll bars. Because of too restrictive
 * access right, most stuff has been copied from <code>MetalSplitPaneUI</code> and <code>MetalSplitPaneDivider</code>.
 * 
 * @see MediknightScrollBarUI
 *
 * @author sma@baltic-online.de
 */
public class MediknightSplitPaneUI extends MetalSplitPaneUI {

    /**
     * This is a copy of the MetalSplitPaneDivider which couldn't be subclasses because of too restrict access rights.
     */
    class MediknightSplitPaneDivider extends BasicSplitPaneDivider {

	/**
	 * Copied from <code>MetalDividerLayout</code> and overwritten or layout buttons without border inserts.
	 */
	public class MediknightDividerLayout implements LayoutManager {

	    @Override
	    public void addLayoutComponent( final String string, final Component c ) {
	    }


	    @Override
	    public void layoutContainer( final Container c ) {
		final JButton leftButton = getLeftButtonFromSuper();
		final JButton rightButton = getRightButtonFromSuper();
		final JSplitPane splitPane = getSplitPaneFromSuper();
		final int orientation = getOrientationFromSuper();
		final int oneTouchSize = getOneTouchSizeFromSuper();
		final int oneTouchOffset = getOneTouchOffsetFromSuper();
		final Insets insets = getInsets();

		// This layout differs from the one used in
		// BasicSplitPaneDivider.
		// It does not center justify the oneTouchExpadable buttons.
		// This was necessary in order to meet the spec of the Metal
		// splitpane divider.
		if( leftButton != null && rightButton != null && c == MediknightSplitPaneDivider.this ) {
		    if( splitPane.isOneTouchExpandable() ) {
			if( orientation == JSplitPane.VERTICAL_SPLIT ) {
			    int extraY = insets != null ? insets.top : 0;
			    extraY += 2;
			    int blockSize = getDividerSize();

			    if( insets != null ) {
				blockSize -= insets.top + insets.bottom;
			    }
			    blockSize = Math.min( blockSize, oneTouchSize );
			    leftButton.setBounds( oneTouchOffset, extraY, blockSize * 2, blockSize );
			    rightButton.setBounds( oneTouchOffset + oneTouchSize * 2, extraY, blockSize * 2, blockSize );
			} else {
			    int blockSize = getDividerSize();
			    int extraX = insets != null ? insets.left : 0;
			    extraX += 2;

			    if( insets != null ) {
				blockSize -= insets.left + insets.right;
			    }
			    blockSize = Math.min( blockSize, oneTouchSize );
			    leftButton.setBounds( extraX, oneTouchOffset, blockSize, blockSize * 2 );
			    rightButton.setBounds( extraX, oneTouchOffset + oneTouchSize * 2, blockSize, blockSize * 2 );
			}
		    } else {
			leftButton.setBounds( -5, -5, 1, 1 );
			rightButton.setBounds( -5, -5, 1, 1 );
		    }
		}
	    }


	    @Override
	    public Dimension minimumLayoutSize( final Container c ) {
		return new Dimension( 0, 0 );
	    }


	    @Override
	    public Dimension preferredLayoutSize( final Container c ) {
		return new Dimension( 0, 0 );
	    }


	    @Override
	    public void removeLayoutComponent( final Component c ) {
	    }
	}

	private static final long serialVersionUID	      = 1L;
	private final Color       controlHightlightColor	= MetalLookAndFeel.getControl();

	private final Color       controlDarkShadowColor	= MetalLookAndFeel.getControlShadow();
	private final Color       primaryControlHighlightColor  = MetalLookAndFeel.getPrimaryControl();

	private final Color       primaryControlDarkShadowColor = MetalLookAndFeel.getPrimaryControlDarkShadow();


	public MediknightSplitPaneDivider( final BasicSplitPaneUI ui ) {
	    super( ui );
	    setLayout( new MediknightDividerLayout() );
	}


	/**
	 * Creates and return an instance of JButton that can be used to collapse the left component in the metal split pane.
	 */
	@Override
	protected JButton createLeftOneTouchButton() {
	    final JButton b = new JButton() {

		private static final long serialVersionUID = 1L;
		// Sprite buffer for the arrow image of the left button
		int[][]		   buffer	   = { { 0, 0, 0, 2, 2, 0, 0, 0, 0 }, { 0, 0, 2, 1, 1, 1, 0, 0, 0 }, { 0, 2, 1, 1, 1, 1, 1, 0, 0 },
								   { 2, 1, 1, 1, 1, 1, 1, 1, 0 }, { 0, 3, 3, 3, 3, 3, 3, 3, 3 } };


		// Don't want the button to participate in focus traversable.
		@Override
		public boolean isFocusable() {
		    return false;
		}


		@Override
		public void paint( final Graphics g ) {
		    final JSplitPane splitPane = getSplitPaneFromSuper();
		    if( splitPane != null ) {
			final int oneTouchSize = getOneTouchSizeFromSuper();
			final int orientation = getOrientationFromSuper();
			final int blockSize = Math.min( getDividerSize(), oneTouchSize );

			// Initialize the color array
			final Color[] colors = { this.getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(),
				MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight() };

			// ... then draw the arrow.
			if( getModel().isPressed() ) {
			    // Adjust color mapping for pressed button state
			    colors[1] = colors[2];
			}
			if( orientation == JSplitPane.VERTICAL_SPLIT ) {
			    // Draw the image for a vertical split
			    for( int i = 1; i <= buffer[0].length; i++ ) {
				for( int j = 1; j < blockSize; j++ ) {
				    if( buffer[j - 1][i - 1] == 0 ) {
					continue;
				    } else {
					g.setColor( colors[buffer[j - 1][i - 1]] );
				    }
				    g.drawLine( i, j, i, j );
				}
			    }
			} else {
			    // Draw the image for a horizontal split
			    // by simply swaping the i and j axis.
			    // Except the drawLine() call this code is
			    // identical to the code block above. This was done
			    // in order to remove the additional orientation
			    // check for each pixel.
			    for( int i = 1; i <= buffer[0].length; i++ ) {
				for( int j = 1; j < blockSize; j++ ) {
				    if( buffer[j - 1][i - 1] == 0 ) {
					// Nothing needs
					// to be drawn
					continue;
				    } else {
					// Set the color from the
					// color map
					g.setColor( colors[buffer[j - 1][i - 1]] );
				    }
				    // Draw a pixel
				    g.drawLine( j, i, j, i );
				}
			    }
			}
		    }
		}


		@Override
		public void setBorder( final Border b ) {
		}
	    };
	    b.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
	    b.setFocusPainted( false );
	    b.setBorderPainted( false );
	    b.setOpaque( false );
	    return b;
	}


	/**
	 * Creates and return an instance of JButton that can be used to collapse the right component in the metal split pane.
	 */
	@Override
	protected JButton createRightOneTouchButton() {
	    final JButton b = new JButton() {

		private static final long serialVersionUID = 1L;
		// Sprite buffer for the arrow image of the right button
		int[][]		   buffer	   = { { 2, 2, 2, 2, 2, 2, 2, 2 }, { 0, 1, 1, 1, 1, 1, 1, 3 }, { 0, 0, 1, 1, 1, 1, 3, 0 },
								   { 0, 0, 0, 1, 1, 3, 0, 0 }, { 0, 0, 0, 0, 3, 0, 0, 0 } };


		// Don't want the button to participate in focus traversable.
		@Override
		public boolean isFocusable() {
		    return false;
		}


		@Override
		public void paint( final Graphics g ) {
		    final JSplitPane splitPane = getSplitPaneFromSuper();
		    if( splitPane != null ) {
			final int oneTouchSize = getOneTouchSizeFromSuper();
			final int orientation = getOrientationFromSuper();
			final int blockSize = Math.min( getDividerSize(), oneTouchSize );

			// Initialize the color array
			final Color[] colors = { this.getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(),
				MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight() };

			// ... then draw the arrow.
			if( getModel().isPressed() ) {
			    // Adjust color mapping for pressed button state
			    colors[1] = colors[2];
			}
			if( orientation == JSplitPane.VERTICAL_SPLIT ) {
			    // Draw the image for a vertical split
			    for( int i = 1; i <= buffer[0].length; i++ ) {
				for( int j = 1; j < blockSize; j++ ) {
				    if( buffer[j - 1][i - 1] == 0 ) {
					continue;
				    } else {
					g.setColor( colors[buffer[j - 1][i - 1]] );
				    }
				    g.drawLine( i, j, i, j );
				}
			    }
			} else {
			    // Draw the image for a horizontal split
			    // by simply swaping the i and j axis.
			    // Except the drawLine() call this code is
			    // identical to the code block above. This was done
			    // in order to remove the additional orientation
			    // check for each pixel.
			    for( int i = 1; i <= buffer[0].length; i++ ) {
				for( int j = 1; j < blockSize; j++ ) {
				    if( buffer[j - 1][i - 1] == 0 ) {
					// Nothing needs
					// to be drawn
					continue;
				    } else {
					// Set the color from the
					// color map
					g.setColor( colors[buffer[j - 1][i - 1]] );
				    }
				    // Draw a pixel
				    g.drawLine( j, i, j, i );
				}
			    }
			}
		    }
		}


		@Override
		public void setBorder( final Border border ) {
		}
	    };
	    b.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
	    b.setFocusPainted( false );
	    b.setBorderPainted( false );
	    b.setOpaque( false );
	    return b;
	}


	JButton getLeftButtonFromSuper() {
	    return super.leftButton;
	}


	int getOneTouchOffsetFromSuper() {
	    return BasicSplitPaneDivider.ONE_TOUCH_OFFSET;
	}


	/*
	 * The following methods only exist in order to be able to access protected members in the superclass, because these are otherwise not available in any
	 * inner class.
	 */

	int getOneTouchSizeFromSuper() {
	    return BasicSplitPaneDivider.ONE_TOUCH_SIZE;
	}


	int getOrientationFromSuper() {
	    return super.orientation;
	}


	JButton getRightButtonFromSuper() {
	    return super.rightButton;
	}


	JSplitPane getSplitPaneFromSuper() {
	    return super.splitPane;
	}


	@Override
	public void paint( final Graphics g ) {
	    Color c1;
	    Color c2;
	    if( splitPane.hasFocus() ) {
		c1 = primaryControlHighlightColor;
		c2 = primaryControlDarkShadowColor;
	    } else {
		c1 = controlHightlightColor;
		c2 = controlDarkShadowColor;
	    }
	    final Dimension size = getSize();
	    final Paint p = orientation == JSplitPane.VERTICAL_SPLIT ? new GradientPaint( 0f, -2f, c1, 0f, size.height, c2 ) : new GradientPaint( -2f, 0f, c1,
		    size.width, 0f, c2 );
	    ((Graphics2D) g).setPaint( p );

	    final Rectangle clip = g.getClipBounds();
	    g.fillRect( clip.x, clip.y, clip.width, clip.height );

	    /*
	     * if (orientation == JSplitPane.VERTICAL_SPLIT) { g.setColor(MetalLookAndFeel.getControlHighlight()); g.drawLine(clip.x, 0, clip.x + clip.width,
	     * 0); g.setColor(MetalLookAndFeel.getControlDarkShadow()); g.drawLine(clip.x, size.height - 1, clip.x + clip.width, size.height - 1); } else {
	     * g.setColor(MetalLookAndFeel.getControlHighlight()); g.drawLine(0, clip.y, 0, clip.y + clip.height);
	     * g.setColor(MetalLookAndFeel.getControlDarkShadow()); g.drawLine(size.width - 1, clip.y, size.width - 1, clip.y + clip.height); }
	     */
	    super.paint( g );
	}


	@Override
	public void setBorder( final Border b ) {
	}
    }


    /**
     * Constructs a new UI for the split panes.
     */
    public static ComponentUI createUI( final JComponent x ) {
	return new MediknightSplitPaneUI();
    }


    /**
     * Creates the default divider.
     */
    @Override
    public BasicSplitPaneDivider createDefaultDivider() {
	return new MediknightSplitPaneDivider( this );
    }


    @Override
    public void installUI( final JComponent c ) {
	super.installUI( c );
	c.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    }
}