/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.bo.mediknight;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import java.awt.image.BufferedImage;

/**
 * A new LAF for the <code>JScrollBar</code> with a new button layout.
 *
 * @author sma@baltic-online.de
 */
public class MediknightScrollBarUI extends MetalScrollBarUI {

    /**
     * Constructs a new UI class for scrollbars.
     */
    public static ComponentUI createUI(JComponent c) {
        return new MediknightScrollBarUI();
    }

    /**
     * Layouts a vertical scrollbar so that both buttons are to the bottom.
     * Copied from <code>BasicScrollBarUI</code> and changed accordingly.
     */
    protected void layoutVScrollbar(JScrollBar sb) {
        Dimension sbSize = sb.getSize();
        Insets sbInsets = sb.getInsets();

        /* Width and left edge of the buttons and thumb.
         */
        int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
        int itemX = sbInsets.left;

        /* Nominal locations of the buttons, assuming their preferred
         * size will fit.
         */
        int incrButtonH = incrButton.getPreferredSize().height;
        int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);

        int decrButtonH = decrButton.getPreferredSize().height;
        int decrButtonY = incrButtonY - decrButtonH + 1;

        /* The thumb must fit within the height left over after we
         * subtract the preferredSize of the buttons and the insets.
         */
        int sbInsetsH = sbInsets.top + sbInsets.bottom;
        int sbButtonsH = decrButtonH + incrButtonH;
        float trackH = sbSize.height - (sbInsetsH + sbButtonsH);

        /* Compute the height and origin of the thumb.   The case
         * where the thumb is at the bottom edge is handled specially
         * to avoid numerical problems in computing thumbY.  Enforce
         * the thumbs min/max dimensions.  If the thumb doesn't
         * fit in the track (trackH) we'll hide it later.
         */
        float min = sb.getMinimum();
        float extent = sb.getVisibleAmount();
        float range = sb.getMaximum() - min;
        float value = sb.getValue();
        int thumbH = (range <= 0)
            ? getMaximumThumbSize().height : (int)(trackH * (extent / range));
        thumbH = Math.max(thumbH, getMinimumThumbSize().height);
        thumbH = Math.min(thumbH, getMaximumThumbSize().height);

        int thumbY = decrButtonY - thumbH;
        if (sb.getValue() < (sb.getMaximum() - sb.getVisibleAmount())) {
            float thumbRange = trackH - thumbH;
            thumbY = (int)(0.5f + (thumbRange * ((value - min) / (range - extent))));
            thumbY += sbInsets.top;
        }

        /* If the buttons don't fit, allocate half of the available
         * space to each and move the lower one (incrButton) down.
         */
        int sbAvailButtonH = (sbSize.height - sbInsetsH);
        if (sbAvailButtonH < sbButtonsH) {
            incrButtonH = decrButtonH = sbAvailButtonH / 2;
            incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
        }

        decrButton.setBounds(itemX, decrButtonY, itemW, decrButtonH);
        incrButton.setBounds(itemX, incrButtonY, itemW, incrButtonH);

        /* Update the trackRect field.
         */
        int itrackY = sbInsets.top;
        int itrackH = decrButtonY - itrackY;
        trackRect.setBounds(itemX, itrackY, itemW, itrackH);

        /* If the thumb isn't going to fit, zero it's bounds.  Otherwise
         * make sure it fits between the buttons.  Note that setting the
         * thumbs bounds will cause a repaint.
         */
        if(thumbH >= (int)trackH)	{
            setThumbBounds(-2, -2, 0, 0);
        }
        else {
            if ((thumbY + thumbH) > decrButtonY) {
                thumbY = decrButtonY - thumbH;
            }
            if (thumbY  < sbInsets.top) {
                thumbY = sbInsets.top;
            }
            setThumbBounds(itemX, thumbY, itemW, thumbH);
        }
    }

    /**
     * Layouts a horizontal scrollbar so that both buttons are to the right.
     * Copied from <code>BasicScrollBarUI</code> and changed accordingly.
     */
    protected void layoutHScrollbar(JScrollBar sb) {
        Dimension sbSize = sb.getSize();
        Insets sbInsets = sb.getInsets();

        /* Height and top edge of the buttons and thumb.
         */
        int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);
        int itemY = sbInsets.top;

        /* Nominal locations of the buttons, assuming their preferred
         * size will fit.
         */
        int incrButtonW = incrButton.getPreferredSize().width;
        int incrButtonX = sbSize.width - (sbInsets.right + incrButtonW);

        int decrButtonW = decrButton.getPreferredSize().width;
        int decrButtonX = incrButtonX - decrButtonW + 1;

        /* The thumb must fit within the width left over after we
         * subtract the preferredSize of the buttons and the insets.
         */
        int sbInsetsW = sbInsets.left + sbInsets.right;
        int sbButtonsW = decrButtonW + incrButtonW;
        float trackW = sbSize.width - (sbInsetsW + sbButtonsW);

        /* Compute the width and origin of the thumb.  Enforce
         * the thumbs min/max dimensions.  The case where the thumb
         * is at the right edge is handled specially to avoid numerical
         * problems in computing thumbX.  If the thumb doesn't
         * fit in the track (trackH) we'll hide it later.
         */
        float min = sb.getMinimum();
        float extent = sb.getVisibleAmount();
        float range = sb.getMaximum() - min;
        float value = sb.getValue();

        int thumbW = (range <= 0)
	        ? getMaximumThumbSize().width : (int)(trackW * (extent / range));
        thumbW = Math.max(thumbW, getMinimumThumbSize().width);
        thumbW = Math.min(thumbW, getMaximumThumbSize().width);

        int thumbX = decrButtonX - thumbW;
        if (sb.getValue() < (sb.getMaximum() - sb.getVisibleAmount())) {
            float thumbRange = trackW - thumbW;
            thumbX = (int)(0.5f + (thumbRange * ((value - min) / (range - extent))));
            thumbX += sbInsets.left;
        }

        /* If the buttons don't fit, allocate half of the available
         * space to each and move the right one (incrButton) over.
         */
        int sbAvailButtonW = (sbSize.width - sbInsetsW);
        if (sbAvailButtonW < sbButtonsW) {
            incrButtonW = decrButtonW = sbAvailButtonW / 2;
            incrButtonX = sbSize.width - (sbInsets.right + incrButtonW);
        }

        decrButton.setBounds(decrButtonX, itemY, decrButtonW, itemH);
        incrButton.setBounds(incrButtonX, itemY, incrButtonW, itemH);

    	/* Update the trackRect field.
    	 */
    	int itrackX = sbInsets.left;
    	int itrackW = decrButtonX - itrackX;
    	trackRect.setBounds(itrackX, itemY, itrackW, itemH);

        /* Make sure the thumb fits between the buttons.  Note
         * that setting the thumbs bounds causes a repaint.
         */
        if (thumbW >= (int)trackW) {
            setThumbBounds(-2, -2, 0, 0);
        }
        else {
            if (thumbX + thumbW > decrButtonX) {
                thumbX = decrButtonX - thumbW;
            }
            if (thumbX  < sbInsets.left) {
                thumbX = sbInsets.left;
            }
            setThumbBounds(thumbX, itemY, thumbW, itemH);
        }
    }

    /**
     * Returns TrackListener that controls thumb dragging. Copied from
     * <code>BasicScrollBarUI</code> and adapted to new button layout.
     */
    protected TrackListener createTrackListener(){
        return new TrackListener() {
            public void mouseDragged(MouseEvent e) {
                if (!scrollbar.isEnabled() || !isDragging)
                    return;
                BoundedRangeModel model = scrollbar.getModel();
                Rectangle thumbR = getThumbBounds();
                float trackLength;
                int thumbMin, thumbMax, thumbPos;
                if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                    thumbMin = scrollbar.getInsets().top;
                    thumbMax = decrButton.getY() - getThumbBounds().height;
                    thumbPos = Math.min(thumbMax, Math.max(thumbMin, (e.getY() - offset)));
                    setThumbBounds(thumbR.x, thumbPos, thumbR.width, thumbR.height);
                    trackLength = getTrackBounds().height;
                }
                else {
                    thumbMin = scrollbar.getInsets().left;
                    thumbMax = decrButton.getX() - getThumbBounds().width;
                    thumbPos = Math.min(thumbMax, Math.max(thumbMin, (e.getX() - offset)));
                    setThumbBounds(thumbPos, thumbR.y, thumbR.width, thumbR.height);
                    trackLength = getTrackBounds().width;
                }

                /* Set the scrollbars value.  If the thumb has reached the end of
                 * the scrollbar, then just set the value to its maximum.  Otherwise
                 * compute the value as accurately as possible.
                 */
                if (thumbPos == thumbMax) {
                    scrollbar.setValue(model.getMaximum() - model.getExtent());
                }
                else {
                    float valueMax = model.getMaximum() - model.getExtent();
                    float valueRange = valueMax - model.getMinimum();
                    float thumbValue = thumbPos - thumbMin;
                    float thumbRange = thumbMax - thumbMin;
                    int value = (int)(0.5 + ((thumbValue / thumbRange) * valueRange));
                    scrollbar.setValue(value + model.getMinimum());
                }
            }
        };
    }

    /**
     * Returns the view that represents the decrease view.
     * <p>Overwritten to return my variant of a scrollbutton.
     * @see MediknightScrollButton
     */
    protected JButton createDecreaseButton(int orientation) {
        decreaseButton = new MediknightScrollButton(orientation, scrollBarWidth);
        return decreaseButton;
    }

    /**
     * Returns the view that represents the increase view.
     * <p>Overwritten to return my variant of a scrollbutton.
     * @see MediknightScrollButton
     */
    protected JButton createIncreaseButton(int orientation) {
        increaseButton =  new MediknightScrollButton(orientation, scrollBarWidth);
        return increaseButton;
    }

    /**
     * Displays the arrow buttons. Adapted from <code>MetalScrollButton</code>.
     * This had to be overwritten to correctly draw the button borders. Because
     * all method and fields were private, a lot of code had to be copied.
     */
    protected static class MediknightScrollButton extends MetalScrollButton {

        private static Color shadowColor;
        private static Color highlightColor;

        public MediknightScrollButton(int direction, int width) {
            super(direction, width, false);
            shadowColor = UIManager.getColor("ScrollBar.darkShadow");
            highlightColor = UIManager.getColor("ScrollBar.highlight");
        }

        public void paint(Graphics g) {
            boolean isEnabled = getParent().isEnabled();
            Color arrowColor = isEnabled ?
                MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlDisabled();
            boolean isPressed = getModel().isPressed();
            int w = getWidth();
            int h = getHeight();
            int arrowHeight = (h + 1) / 4;
            int arrowWidth = (h + 1) / 2;

            // Fill background
            if (isPressed) {
                g.setColor(MetalLookAndFeel.getControlShadow());
                g.fillRect(0, 0, w, h);
            } else {
                MediknightButtonUI.paintGradient((Graphics2D)g,
                    getBackground(),
                    w,
                    h);
            }

            // Draw the arrow
            g.setColor(arrowColor);
            if (getDirection() == NORTH) {
                int startY = ((h + 1) - arrowHeight) / 2;
                int startX = w / 2;
                for (int line = 0; line < arrowHeight; line++) {
                    g.drawLine(startX-line, startY+line, startX +line+1, startY+line);
                }
                w++;
            }
            else if (getDirection() == SOUTH) {
                int startY = (((h+1) - arrowHeight) / 2)+ arrowHeight-1;
                int startX = (w / 2);
                for (int line = 0; line < arrowHeight; line++) {
                    g.drawLine( startX-line, startY-line, startX +line+1, startY-line);
                }
                w++;
            }
            else if (getDirection() == EAST) {
                int startX = (((w+1) - arrowHeight) / 2) + arrowHeight-1;
                int startY = (h / 2);
                for (int line = 0; line < arrowHeight; line++) {
                    g.drawLine( startX-line, startY-line, startX -line, startY+line+1);
                }
                h++;
            }
            else if (getDirection() == WEST) {
                int startX = (((w+1) - arrowHeight) / 2);
                int startY = (h / 2);
                for (int line = 0; line < arrowHeight; line++) {
                    g.drawLine( startX+line, startY-line, startX +line, startY+line+1);
                }
                h++;
            }

            // Draw the border
            if (isEnabled) {
                if (!isPressed) {
                    g.setColor(highlightColor);
                    g.drawLine(1, 1, w - 2, 1);
                    g.drawLine(1, 1, 1, h - 2);
                }
                g.setColor(shadowColor);
                g.drawLine(0, 0, w - 1, 0);
                g.drawLine(0, 0, 0, h - 1);
                g.drawLine(2, h - 1, w - 2, h - 1);
                g.drawLine(w - 1, 2, w - 1, h - 2);
            }
            else
                drawDisabledBorder(g, 0, 0, w, h);
        }

        private static void drawDisabledBorder(Graphics g, int x, int y, int w, int h) {
            g.translate(x, y);
            g.setColor(MetalLookAndFeel.getControlShadow());
            g.drawRect(0, 0, w - 1, h - 1);
        }
    }

    // need to copy these variables because they are private in the super class
    private static Color thumbColor;
    private static Color thumbShadow;
    private static Color thumbHighlightColor;
    private static MediknightBumps bumps;

    /** Overwritten because color information are private */
    protected void configureScrollBarColors() {
        super.configureScrollBarColors();
        thumbColor = UIManager.getColor("ScrollBar.thumb");
        thumbShadow = UIManager.getColor("ScrollBar.thumbShadow");
        thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
        bumps = new MediknightBumps(10, 10, thumbHighlightColor, thumbColor, null);
    }

    /**
     * Paints the scroll bar thumb with a nice gradient.  Mostly copied but
     * added a gradient paint.  Currently, it doesn't paint bumps.  The code
     * is still there, but commented out.
     */
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!c.isEnabled()) {
	    return;
	}

        boolean leftToRight = true; //MetalUtils.isLeftToRight(c);

        g.translate(thumbBounds.x, thumbBounds.y);

	if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
	    if (!isFreeStanding) {
                if (!leftToRight) {
                    thumbBounds.width += 1;
                    g.translate(-1, 0);
		} else {
                    thumbBounds.width += 2;
                }
	    }

            ((Graphics2D)g).setPaint(new GradientPaint(
                -3f, 0f, thumbHighlightColor,
                (float)(thumbBounds.width + 2), 0f, thumbShadow,
                false));
	    g.fillRect(0, 0, thumbBounds.width - 2, thumbBounds.height - 1);

	    g.setColor(thumbShadow );
	    g.drawRect(0, 0, thumbBounds.width - 2, thumbBounds.height - 1);

	    g.setColor(thumbHighlightColor );
	    g.drawLine(1, 1, thumbBounds.width - 3, 1);
	    g.drawLine(1, 1, 1, thumbBounds.height - 2);

	    //bumps.setBumpArea(thumbBounds.width - 6, thumbBounds.height - 7);
	    //bumps.paintIcon(c, g, 3, 4);

	    if (!isFreeStanding ) {
                if (!leftToRight ) {
                    thumbBounds.width -= 1;
                    g.translate(1, 0);
		} else {
                    thumbBounds.width -= 2;
                }
	    }
	}
	else {  // HORIZONTAL
	    if (!isFreeStanding) {
	        thumbBounds.height += 2;
	    }

            ((Graphics2D)g).setPaint(new GradientPaint(
                0f, -3f, thumbHighlightColor,
                0f, (float)(thumbBounds.height + 1), thumbShadow,
                false));
	    g.fillRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 2);

	    g.setColor(thumbShadow);
	    g.drawRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 2);

	    g.setColor(thumbHighlightColor);
	    g.drawLine(1, 1, thumbBounds.width - 3, 1);
	    g.drawLine(1, 1, 1, thumbBounds.height - 3);

	    //bumps.setBumpArea(thumbBounds.width - 7, thumbBounds.height - 6);
	    //bumps.paintIcon(c, g, 4, 3);

	    if (!isFreeStanding) {
	        thumbBounds.height -= 2;
	    }
	}

        g.translate(-thumbBounds.x, -thumbBounds.y);
    }


    /**
     * The class implements the scroll bar bumps with a transparent background.
     * It's a simplified version of class <code>MetalBumps</code> (was wasn't
     * subclassable because of its access rights).
     */
    static class MediknightBumps implements Icon {

        protected int xBumps;
        protected int yBumps;
        protected Color topColor;
        protected Color shadowColor;
        protected Color backColor;

        private static final int IMAGE_SIZE = 64;
        protected BufferedImage image;

        public MediknightBumps(int width, int height,
                Color newTopColor, Color newShadowColor, Color newBackColor) {
            setBumpArea(width, height);
            setBumpColors(newTopColor, newShadowColor, newBackColor);
        }

        public void setBumpArea(int width, int height) {
            xBumps = width / 2;
            yBumps = height / 2;
        }

        public void setBumpColors(Color newTopColor, Color newShadowColor, Color newBackColor) {
            if (newTopColor.equals(topColor) &&
                newShadowColor.equals(shadowColor) &&
                newBackColor.equals(backColor)) return;
            topColor = newTopColor;
            shadowColor = newShadowColor;
            backColor = newBackColor;
            image = null;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (image == null) createImage();

            int iconWidth = getIconWidth();
            int iconHeight = getIconHeight();

            int x2 = x + iconWidth;
            int y2 = y + iconHeight;

            int savex = x;
            while (y < y2) {
                int h = Math.min(y2 - y, IMAGE_SIZE);
                for (x = savex; x < x2; x += IMAGE_SIZE) {
                    int w = Math.min(x2 - x, IMAGE_SIZE);
                    g.drawImage(image,
                                x, y, x+w, y+h,
                                0, 0, w, h,
                                null);
                }
                y += IMAGE_SIZE;
            }
        }

        public int getIconWidth() {
            return xBumps * 2;
        }

        public int getIconHeight() {
            return yBumps * 2;
        }

        protected void createImage() {
            image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_4BYTE_ABGR);

            Graphics g = image.getGraphics();

            g.setColor(topColor);
            for (int x = 0; x < IMAGE_SIZE; x+=4) {
                for (int y = 0; y < IMAGE_SIZE; y+=4) {
                    g.drawLine(x, y, x, y);
                    g.drawLine(x+2, y+2, x+2, y+2);
                }
            }

            g.setColor(shadowColor);
            for (int x = 0; x < IMAGE_SIZE; x+=4) {
                for (int y = 0; y < IMAGE_SIZE; y+=4) {
                    g.drawLine(x+1, y+1, x+1, y+1);
                    g.drawLine(x+3, y+3, x+3, y+3);
                }
            }
            g.dispose();
        }
    }
}