/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.baltic_online.mediknight;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BoundedRangeModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.plaf.metal.MetalScrollButton;


/**
 * A new LAF for the <code>JScrollBar</code> with a new button layout.
 *
 * @author sma@baltic-online.de
 */
public class MediknightScrollBarUI extends MetalScrollBarUI {

    /**
     * The class implements the scroll bar bumps with a transparent background. It's a simplified version of class <code>MetalBumps</code> (was wasn't
     * subclassable because of its access rights).
     */
    static class MediknightBumps implements Icon {

	private static final int IMAGE_SIZE = 64;
	protected int	    xBumps;
	protected int	    yBumps;
	protected Color	  topColor;
	protected Color	  shadowColor;

	protected Color	  backColor;
	protected BufferedImage  image;


	public MediknightBumps( final int width, final int height, final Color newTopColor, final Color newShadowColor, final Color newBackColor ) {
	    setBumpArea( width, height );
	    setBumpColors( newTopColor, newShadowColor, newBackColor );
	}


	protected void createImage() {
	    image = new BufferedImage( IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR );

	    final Graphics g = image.getGraphics();

	    g.setColor( topColor );
	    for( int x = 0; x < IMAGE_SIZE; x += 4 ) {
		for( int y = 0; y < IMAGE_SIZE; y += 4 ) {
		    g.drawLine( x, y, x, y );
		    g.drawLine( x + 2, y + 2, x + 2, y + 2 );
		}
	    }

	    g.setColor( shadowColor );
	    for( int x = 0; x < IMAGE_SIZE; x += 4 ) {
		for( int y = 0; y < IMAGE_SIZE; y += 4 ) {
		    g.drawLine( x + 1, y + 1, x + 1, y + 1 );
		    g.drawLine( x + 3, y + 3, x + 3, y + 3 );
		}
	    }
	    g.dispose();
	}


	@Override
	public int getIconHeight() {
	    return yBumps * 2;
	}


	@Override
	public int getIconWidth() {
	    return xBumps * 2;
	}


	@Override
	public void paintIcon( final Component c, final Graphics g, int x, int y ) {
	    if( image == null ) {
		createImage();
	    }

	    final int iconWidth = getIconWidth();
	    final int iconHeight = getIconHeight();

	    final int x2 = x + iconWidth;
	    final int y2 = y + iconHeight;

	    final int savex = x;
	    while( y < y2 ) {
		final int h = Math.min( y2 - y, IMAGE_SIZE );
		for( x = savex; x < x2; x += IMAGE_SIZE ) {
		    final int w = Math.min( x2 - x, IMAGE_SIZE );
		    g.drawImage( image, x, y, x + w, y + h, 0, 0, w, h, null );
		}
		y += IMAGE_SIZE;
	    }
	}


	public void setBumpArea( final int width, final int height ) {
	    xBumps = width / 2;
	    yBumps = height / 2;
	}


	public void setBumpColors( final Color newTopColor, final Color newShadowColor, final Color newBackColor ) {
	    if( newTopColor.equals( topColor ) && newShadowColor.equals( shadowColor ) && newBackColor.equals( backColor ) ) {
		return;
	    }
	    topColor = newTopColor;
	    shadowColor = newShadowColor;
	    backColor = newBackColor;
	    image = null;
	}
    }

    /**
     * Displays the arrow buttons. Adapted from <code>MetalScrollButton</code>. This had to be overwritten to correctly draw the button borders. Because all
     * method and fields were private, a lot of code had to be copied.
     */
    protected static class MediknightScrollButton extends MetalScrollButton {

	private static final long serialVersionUID = 1L;
	private static Color      shadowColor;
	private static Color      highlightColor;


	private static void drawDisabledBorder( final Graphics g, final int x, final int y, final int w, final int h ) {
	    g.translate( x, y );
	    g.setColor( MetalLookAndFeel.getControlShadow() );
	    g.drawRect( 0, 0, w - 1, h - 1 );
	}


	public MediknightScrollButton( final int direction, final int width ) {
	    super( direction, width, false );
	    shadowColor = UIManager.getColor( "ScrollBar.darkShadow" );
	    highlightColor = UIManager.getColor( "ScrollBar.highlight" );
	}


	@Override
	public void paint( final Graphics g ) {
	    final boolean isEnabled = getParent().isEnabled();
	    final Color arrowColor = isEnabled ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlDisabled();
	    final boolean isPressed = getModel().isPressed();
	    int w = getWidth();
	    int h = getHeight();
	    final int arrowHeight = (h + 1) / 4;

	    // Fill background
	    if( isPressed ) {
		g.setColor( MetalLookAndFeel.getControlShadow() );
		g.fillRect( 0, 0, w, h );
	    } else {
		MediknightButtonUI.paintGradient( (Graphics2D) g, getBackground(), w, h );
	    }

	    // Draw the arrow
	    g.setColor( arrowColor );
	    if( getDirection() == NORTH ) {
		final int startY = (h + 1 - arrowHeight) / 2;
		final int startX = w / 2;
		for( int line = 0; line < arrowHeight; line++ ) {
		    g.drawLine( startX - line, startY + line, startX + line + 1, startY + line );
		}
		w++;
	    } else if( getDirection() == SOUTH ) {
		final int startY = (h + 1 - arrowHeight) / 2 + arrowHeight - 1;
		final int startX = w / 2;
		for( int line = 0; line < arrowHeight; line++ ) {
		    g.drawLine( startX - line, startY - line, startX + line + 1, startY - line );
		}
		w++;
	    } else if( getDirection() == EAST ) {
		final int startX = (w + 1 - arrowHeight) / 2 + arrowHeight - 1;
		final int startY = h / 2;
		for( int line = 0; line < arrowHeight; line++ ) {
		    g.drawLine( startX - line, startY - line, startX - line, startY + line + 1 );
		}
		h++;
	    } else if( getDirection() == WEST ) {
		final int startX = (w + 1 - arrowHeight) / 2;
		final int startY = h / 2;
		for( int line = 0; line < arrowHeight; line++ ) {
		    g.drawLine( startX + line, startY - line, startX + line, startY + line + 1 );
		}
		h++;
	    }

	    // Draw the border
	    if( isEnabled ) {
		if( !isPressed ) {
		    g.setColor( highlightColor );
		    g.drawLine( 1, 1, w - 2, 1 );
		    g.drawLine( 1, 1, 1, h - 2 );
		}
		g.setColor( shadowColor );
		g.drawLine( 0, 0, w - 1, 0 );
		g.drawLine( 0, 0, 0, h - 1 );
		g.drawLine( 2, h - 1, w - 2, h - 1 );
		g.drawLine( w - 1, 2, w - 1, h - 2 );
	    } else {
		drawDisabledBorder( g, 0, 0, w, h );
	    }
	}
    }

    // need to copy these variables because they are private in the super class
    // private static Color thumbColor;
    private static Color thumbShadow;

    private static Color thumbHighlightColor;


    // private static MediknightBumps bumps;

    /**
     * Constructs a new UI class for scrollbars.
     */
    public static ComponentUI createUI( final JComponent c ) {
	return new MediknightScrollBarUI();
    }


    /** Overwritten because color information are private */
    @Override
    protected void configureScrollBarColors() {
	super.configureScrollBarColors();
	// thumbColor = UIManager.getColor("ScrollBar.thumb");
	thumbShadow = UIManager.getColor( "ScrollBar.thumbShadow" );
	thumbHighlightColor = UIManager.getColor( "ScrollBar.thumbHighlight" );
	// bumps = new MediknightBumps(10, 10, thumbHighlightColor, thumbColor,
	// null);
    }


    /**
     * Returns the view that represents the decrease view.
     * <p>
     * Overwritten to return my variant of a scrollbutton.
     * 
     * @see MediknightScrollButton
     */
    @Override
    protected JButton createDecreaseButton( final int orientation ) {
	decreaseButton = new MediknightScrollButton( orientation, scrollBarWidth );
	return decreaseButton;
    }


    /**
     * Returns the view that represents the increase view.
     * <p>
     * Overwritten to return my variant of a scrollbutton.
     * 
     * @see MediknightScrollButton
     */
    @Override
    protected JButton createIncreaseButton( final int orientation ) {
	increaseButton = new MediknightScrollButton( orientation, scrollBarWidth );
	return increaseButton;
    }


    /**
     * Returns TrackListener that controls thumb dragging. Copied from <code>BasicScrollBarUI</code> and adapted to new button layout.
     */
    @Override
    protected TrackListener createTrackListener() {
	return new TrackListener() {

	    @Override
	    public void mouseDragged( final MouseEvent e ) {
		if( !scrollbar.isEnabled() || !isDragging ) {
		    return;
		}
		final BoundedRangeModel model = scrollbar.getModel();
		final Rectangle thumbR = getThumbBounds();
		int thumbMin, thumbMax, thumbPos;
		if( scrollbar.getOrientation() == Adjustable.VERTICAL ) {
		    thumbMin = scrollbar.getInsets().top;
		    thumbMax = decrButton.getY() - getThumbBounds().height;
		    thumbPos = Math.min( thumbMax, Math.max( thumbMin, e.getY() - offset ) );
		    setThumbBounds( thumbR.x, thumbPos, thumbR.width, thumbR.height );
		} else {
		    thumbMin = scrollbar.getInsets().left;
		    thumbMax = decrButton.getX() - getThumbBounds().width;
		    thumbPos = Math.min( thumbMax, Math.max( thumbMin, e.getX() - offset ) );
		    setThumbBounds( thumbPos, thumbR.y, thumbR.width, thumbR.height );
		}

		/*
		 * Set the scrollbars value. If the thumb has reached the end of the scrollbar, then just set the value to its maximum. Otherwise compute the
		 * value as accurately as possible.
		 */
		if( thumbPos == thumbMax ) {
		    scrollbar.setValue( model.getMaximum() - model.getExtent() );
		} else {
		    final float valueMax = model.getMaximum() - model.getExtent();
		    final float valueRange = valueMax - model.getMinimum();
		    final float thumbValue = thumbPos - thumbMin;
		    final float thumbRange = thumbMax - thumbMin;
		    final int value = (int) (0.5 + thumbValue / thumbRange * valueRange);
		    scrollbar.setValue( value + model.getMinimum() );
		}
	    }
	};
    }


    /**
     * Layouts a horizontal scrollbar so that both buttons are to the right. Copied from <code>BasicScrollBarUI</code> and changed accordingly.
     */
    @Override
    protected void layoutHScrollbar( final JScrollBar sb ) {
	final Dimension sbSize = sb.getSize();
	final Insets sbInsets = sb.getInsets();

	/*
	 * Height and top edge of the buttons and thumb.
	 */
	final int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);
	final int itemY = sbInsets.top;

	/*
	 * Nominal locations of the buttons, assuming their preferred size will fit.
	 */
	int incrButtonW = incrButton.getPreferredSize().width;
	int incrButtonX = sbSize.width - (sbInsets.right + incrButtonW);

	int decrButtonW = decrButton.getPreferredSize().width;
	final int decrButtonX = incrButtonX - decrButtonW + 1;

	/*
	 * The thumb must fit within the width left over after we subtract the preferredSize of the buttons and the insets.
	 */
	final int sbInsetsW = sbInsets.left + sbInsets.right;
	final int sbButtonsW = decrButtonW + incrButtonW;
	final float trackW = sbSize.width - (sbInsetsW + sbButtonsW);

	/*
	 * Compute the width and origin of the thumb. Enforce the thumbs min/max dimensions. The case where the thumb is at the right edge is handled specially
	 * to avoid numerical problems in computing thumbX. If the thumb doesn't fit in the track (trackH) we'll hide it later.
	 */
	final float min = sb.getMinimum();
	final float extent = sb.getVisibleAmount();
	final float range = sb.getMaximum() - min;
	final float value = sb.getValue();

	int thumbW = range <= 0 ? getMaximumThumbSize().width : (int) (trackW * (extent / range));
	thumbW = Math.max( thumbW, getMinimumThumbSize().width );
	thumbW = Math.min( thumbW, getMaximumThumbSize().width );

	int thumbX = decrButtonX - thumbW;
	if( sb.getValue() < sb.getMaximum() - sb.getVisibleAmount() ) {
	    final float thumbRange = trackW - thumbW;
	    thumbX = (int) (0.5f + thumbRange * ((value - min) / (range - extent)));
	    thumbX += sbInsets.left;
	}

	/*
	 * If the buttons don't fit, allocate half of the available space to each and move the right one (incrButton) over.
	 */
	final int sbAvailButtonW = sbSize.width - sbInsetsW;
	if( sbAvailButtonW < sbButtonsW ) {
	    incrButtonW = decrButtonW = sbAvailButtonW / 2;
	    incrButtonX = sbSize.width - (sbInsets.right + incrButtonW);
	}

	decrButton.setBounds( decrButtonX, itemY, decrButtonW, itemH );
	incrButton.setBounds( incrButtonX, itemY, incrButtonW, itemH );

	/*
	 * Update the trackRect field.
	 */
	final int itrackX = sbInsets.left;
	final int itrackW = decrButtonX - itrackX;
	trackRect.setBounds( itrackX, itemY, itrackW, itemH );

	/*
	 * Make sure the thumb fits between the buttons. Note that setting the thumbs bounds causes a repaint.
	 */
	if( thumbW >= (int) trackW ) {
	    setThumbBounds( -2, -2, 0, 0 );
	} else {
	    if( thumbX + thumbW > decrButtonX ) {
		thumbX = decrButtonX - thumbW;
	    }
	    if( thumbX < sbInsets.left ) {
		thumbX = sbInsets.left;
	    }
	    setThumbBounds( thumbX, itemY, thumbW, itemH );
	}
    }


    /**
     * Layouts a vertical scrollbar so that both buttons are to the bottom. Copied from <code>BasicScrollBarUI</code> and changed accordingly.
     */
    @Override
    protected void layoutVScrollbar( final JScrollBar sb ) {
	final Dimension sbSize = sb.getSize();
	final Insets sbInsets = sb.getInsets();

	/*
	 * Width and left edge of the buttons and thumb.
	 */
	final int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
	final int itemX = sbInsets.left;

	/*
	 * Nominal locations of the buttons, assuming their preferred size will fit.
	 */
	int incrButtonH = incrButton.getPreferredSize().height;
	int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);

	int decrButtonH = decrButton.getPreferredSize().height;
	final int decrButtonY = incrButtonY - decrButtonH + 1;

	/*
	 * The thumb must fit within the height left over after we subtract the preferredSize of the buttons and the insets.
	 */
	final int sbInsetsH = sbInsets.top + sbInsets.bottom;
	final int sbButtonsH = decrButtonH + incrButtonH;
	final float trackH = sbSize.height - (sbInsetsH + sbButtonsH);

	/*
	 * Compute the height and origin of the thumb. The case where the thumb is at the bottom edge is handled specially to avoid numerical problems in
	 * computing thumbY. Enforce the thumbs min/max dimensions. If the thumb doesn't fit in the track (trackH) we'll hide it later.
	 */
	final float min = sb.getMinimum();
	final float extent = sb.getVisibleAmount();
	final float range = sb.getMaximum() - min;
	final float value = sb.getValue();
	int thumbH = range <= 0 ? getMaximumThumbSize().height : (int) (trackH * (extent / range));
	thumbH = Math.max( thumbH, getMinimumThumbSize().height );
	thumbH = Math.min( thumbH, getMaximumThumbSize().height );

	int thumbY = decrButtonY - thumbH;
	if( sb.getValue() < sb.getMaximum() - sb.getVisibleAmount() ) {
	    final float thumbRange = trackH - thumbH;
	    thumbY = (int) (0.5f + thumbRange * ((value - min) / (range - extent)));
	    thumbY += sbInsets.top;
	}

	/*
	 * If the buttons don't fit, allocate half of the available space to each and move the lower one (incrButton) down.
	 */
	final int sbAvailButtonH = sbSize.height - sbInsetsH;
	if( sbAvailButtonH < sbButtonsH ) {
	    incrButtonH = decrButtonH = sbAvailButtonH / 2;
	    incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
	}

	decrButton.setBounds( itemX, decrButtonY, itemW, decrButtonH );
	incrButton.setBounds( itemX, incrButtonY, itemW, incrButtonH );

	/*
	 * Update the trackRect field.
	 */
	final int itrackY = sbInsets.top;
	final int itrackH = decrButtonY - itrackY;
	trackRect.setBounds( itemX, itrackY, itemW, itrackH );

	/*
	 * If the thumb isn't going to fit, zero it's bounds. Otherwise make sure it fits between the buttons. Note that setting the thumbs bounds will cause a
	 * repaint.
	 */
	if( thumbH >= (int) trackH ) {
	    setThumbBounds( -2, -2, 0, 0 );
	} else {
	    if( thumbY + thumbH > decrButtonY ) {
		thumbY = decrButtonY - thumbH;
	    }
	    if( thumbY < sbInsets.top ) {
		thumbY = sbInsets.top;
	    }
	    setThumbBounds( itemX, thumbY, itemW, thumbH );
	}
    }


    /**
     * Paints the scroll bar thumb with a nice gradient. Mostly copied but added a gradient paint. Currently, it doesn't paint bumps. The code is still there,
     * but commented out.
     */
    @Override
    protected void paintThumb( final Graphics g, final JComponent c, final Rectangle thumbBounds ) {
	if( !c.isEnabled() ) {
	    return;
	}

	final boolean leftToRight = true; // MetalUtils.isLeftToRight(c);

	g.translate( thumbBounds.x, thumbBounds.y );

	if( scrollbar.getOrientation() == Adjustable.VERTICAL ) {
	    if( !isFreeStanding ) {
		if( !leftToRight ) {
		    thumbBounds.width += 1;
		    g.translate( -1, 0 );
		} else {
		    thumbBounds.width += 2;
		}
	    }

	    ((Graphics2D) g).setPaint( new GradientPaint( -3f, 0f, thumbHighlightColor, thumbBounds.width + 2, 0f, thumbShadow, false ) );
	    g.fillRect( 0, 0, thumbBounds.width - 2, thumbBounds.height - 1 );

	    g.setColor( thumbShadow );
	    g.drawRect( 0, 0, thumbBounds.width - 2, thumbBounds.height - 1 );

	    g.setColor( thumbHighlightColor );
	    g.drawLine( 1, 1, thumbBounds.width - 3, 1 );
	    g.drawLine( 1, 1, 1, thumbBounds.height - 2 );

	    // bumps.setBumpArea(thumbBounds.width - 6, thumbBounds.height - 7);
	    // bumps.paintIcon(c, g, 3, 4);

	    if( !isFreeStanding ) {
		if( !leftToRight ) {
		    thumbBounds.width -= 1;
		    g.translate( 1, 0 );
		} else {
		    thumbBounds.width -= 2;
		}
	    }
	} else { // HORIZONTAL
	    if( !isFreeStanding ) {
		thumbBounds.height += 2;
	    }

	    ((Graphics2D) g).setPaint( new GradientPaint( 0f, -3f, thumbHighlightColor, 0f, thumbBounds.height + 1, thumbShadow, false ) );
	    g.fillRect( 0, 0, thumbBounds.width - 1, thumbBounds.height - 2 );

	    g.setColor( thumbShadow );
	    g.drawRect( 0, 0, thumbBounds.width - 1, thumbBounds.height - 2 );

	    g.setColor( thumbHighlightColor );
	    g.drawLine( 1, 1, thumbBounds.width - 3, 1 );
	    g.drawLine( 1, 1, 1, thumbBounds.height - 3 );

	    // bumps.setBumpArea(thumbBounds.width - 7, thumbBounds.height - 6);
	    // bumps.paintIcon(c, g, 4, 3);

	    if( !isFreeStanding ) {
		thumbBounds.height -= 2;
	    }
	}

	g.translate( -thumbBounds.x, -thumbBounds.y );
    }
}