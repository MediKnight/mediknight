/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.bo.mediknight;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;


/**
 * A new LAF for the <code>JSplitPane</code>.  The usual bumps are replaced
 * with the same gradient which is also used for scroll bars.  Because of
 * too restrictive access right, most stuff has been copied from
 * <code>MetalSplitPaneUI</code> and <code>MetalSplitPaneDivider</code>.
 * @see MediknightScrollBarUI
 *
 * @author sma@baltic-online.de
 */
public class MediknightSplitPaneUI extends MetalSplitPaneUI {

    /**
      * Constructs a new UI for the split panes.
      */
    public static ComponentUI createUI(JComponent x) {
	return new MediknightSplitPaneUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        c.setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    /**
      * Creates the default divider.
      */
    public BasicSplitPaneDivider createDefaultDivider() {
	return new MediknightSplitPaneDivider(this);
    }


    /**
     * This is a copy of the MetalSplitPaneDivider which couldn't be
     * subclasses because of too restrict access rights.
     */
    class MediknightSplitPaneDivider extends BasicSplitPaneDivider {

        private Color controlHightlightColor = MetalLookAndFeel.getControl();
        private Color controlDarkShadowColor = MetalLookAndFeel.getControlShadow();

        private Color primaryControlHighlightColor = MetalLookAndFeel.getPrimaryControl();
        private Color primaryControlDarkShadowColor = MetalLookAndFeel.getPrimaryControlDarkShadow();

        public MediknightSplitPaneDivider(BasicSplitPaneUI ui) {
            super(ui);
            setLayout(new MediknightDividerLayout());
        }

        public void paint(Graphics g) {
            Color c1;
            Color c2;
            if (splitPane.hasFocus()) {
                c1 = primaryControlHighlightColor;
                c2 = primaryControlDarkShadowColor;
            }
            else {
                c1 = controlHightlightColor;
                c2 = controlDarkShadowColor;
            }
            Dimension  size = getSize();
            Paint p = (orientation == JSplitPane.VERTICAL_SPLIT)
                ? new GradientPaint(0f, -2f, c1, 0f, size.height, c2)
                : new GradientPaint(-2f, 0f, c1, size.width, 0f, c2);
            ((Graphics2D)g).setPaint(p);

            Rectangle clip = g.getClipBounds();
            g.fillRect(clip.x, clip.y, clip.width, clip.height);

            /*if (orientation == JSplitPane.VERTICAL_SPLIT) {
                g.setColor(MetalLookAndFeel.getControlHighlight());
                g.drawLine(clip.x, 0, clip.x + clip.width, 0);
                g.setColor(MetalLookAndFeel.getControlDarkShadow());
                g.drawLine(clip.x, size.height - 1, clip.x + clip.width, size.height - 1);
            }
            else {
                g.setColor(MetalLookAndFeel.getControlHighlight());
                g.drawLine(0, clip.y, 0, clip.y + clip.height);
                g.setColor(MetalLookAndFeel.getControlDarkShadow());
                g.drawLine(size.width - 1, clip.y, size.width - 1, clip.y + clip.height);
            }*/
            super.paint(g);
        }

        public void setBorder(Border b) {
        }

        /**
         * Creates and return an instance of JButton that can be used to
         * collapse the left component in the metal split pane.
         */
        protected JButton createLeftOneTouchButton() {
            JButton b = new JButton() {
                // Sprite buffer for the arrow image of the left button
                int[][]     buffer = {{0, 0, 0, 2, 2, 0, 0, 0, 0},
                                      {0, 0, 2, 1, 1, 1, 0, 0, 0},
                                      {0, 2, 1, 1, 1, 1, 1, 0, 0},
                                      {2, 1, 1, 1, 1, 1, 1, 1, 0},
                                      {0, 3, 3, 3, 3, 3, 3, 3, 3}};

                public void setBorder(Border b) {
                }

                public void paint(Graphics g) {
                    JSplitPane splitPane = getSplitPaneFromSuper();
                    if(splitPane != null) {
                        int         oneTouchSize = getOneTouchSizeFromSuper();
                        int         orientation = getOrientationFromSuper();
                        int         blockSize = Math.min(getDividerSize(),
                                                         oneTouchSize);

                        // Initialize the color array
                        Color[]     colors = {
                                this.getBackground(),
                                MetalLookAndFeel.getPrimaryControlDarkShadow(),
                                MetalLookAndFeel.getPrimaryControlInfo(),
                                MetalLookAndFeel.getPrimaryControlHighlight()};

                        // ... then draw the arrow.
                        if (getModel().isPressed()) {
                                // Adjust color mapping for pressed button state
                                colors[1] = colors[2];
                        }
                        if(orientation == JSplitPane.VERTICAL_SPLIT) {
                                // Draw the image for a vertical split
                                for (int i=1; i<=buffer[0].length; i++) {
                                        for (int j=1; j<blockSize; j++) {
                                                if (buffer[j-1][i-1] == 0) {
                                                        continue;
                                                }
                                                else {
                                                    g.setColor(
                                                        colors[buffer[j-1][i-1]]);
                                                }
                                                g.drawLine(i, j, i, j);
                                        }
                                }
                        }
                        else {
                                // Draw the image for a horizontal split
                                // by simply swaping the i and j axis.
                                // Except the drawLine() call this code is
                                // identical to the code block above. This was done
                                // in order to remove the additional orientation
                                // check for each pixel.
                                for (int i=1; i<=buffer[0].length; i++) {
                                        for (int j=1; j<blockSize; j++) {
                                                if (buffer[j-1][i-1] == 0) {
                                                        // Nothing needs
                                                        // to be drawn
                                                        continue;
                                                }
                                                else {
                                                        // Set the color from the
                                                        // color map
                                                        g.setColor(
                                                        colors[buffer[j-1][i-1]]);
                                                }
                                                // Draw a pixel
                                                g.drawLine(j, i, j, i);
                                        }
                                }
                        }
                    }
                }

                // Don't want the button to participate in focus traversable.
                public boolean isFocusable() {
                    return false;
                }
            };
            b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setOpaque(false);
            return b;
        }

        /**
         * Creates and return an instance of JButton that can be used to
         * collapse the right component in the metal split pane.
         */
        protected JButton createRightOneTouchButton() {
            JButton b = new JButton() {
                // Sprite buffer for the arrow image of the right button
                int[][]     buffer = {{2, 2, 2, 2, 2, 2, 2, 2},
                                      {0, 1, 1, 1, 1, 1, 1, 3},
                                      {0, 0, 1, 1, 1, 1, 3, 0},
                                      {0, 0, 0, 1, 1, 3, 0, 0},
                                      {0, 0, 0, 0, 3, 0, 0, 0}};

                public void setBorder(Border border) {
                }

                public void paint(Graphics g) {
                    JSplitPane splitPane = getSplitPaneFromSuper();
                    if(splitPane != null) {
                        int         oneTouchSize = getOneTouchSizeFromSuper();
                        int         orientation = getOrientationFromSuper();
                        int         blockSize = Math.min(getDividerSize(),
                                                         oneTouchSize);

                        // Initialize the color array
                        Color[]     colors = {
                                this.getBackground(),
                                MetalLookAndFeel.getPrimaryControlDarkShadow(),
                                MetalLookAndFeel.getPrimaryControlInfo(),
                                MetalLookAndFeel.getPrimaryControlHighlight()};

                        // ... then draw the arrow.
                        if (getModel().isPressed()) {
                                // Adjust color mapping for pressed button state
                                colors[1] = colors[2];
                        }
                        if(orientation == JSplitPane.VERTICAL_SPLIT) {
                                // Draw the image for a vertical split
                                for (int i=1; i<=buffer[0].length; i++) {
                                        for (int j=1; j<blockSize; j++) {
                                                if (buffer[j-1][i-1] == 0) {
                                                        continue;
                                                }
                                                else {
                                                    g.setColor(
                                                        colors[buffer[j-1][i-1]]);
                                                }
                                                g.drawLine(i, j, i, j);
                                        }
                                }
                        }
                        else {
                                // Draw the image for a horizontal split
                                // by simply swaping the i and j axis.
                                // Except the drawLine() call this code is
                                // identical to the code block above. This was done
                                // in order to remove the additional orientation
                                // check for each pixel.
                                for (int i=1; i<=buffer[0].length; i++) {
                                        for (int j=1; j<blockSize; j++) {
                                                if (buffer[j-1][i-1] == 0) {
                                                        // Nothing needs
                                                        // to be drawn
                                                        continue;
                                                }
                                                else {
                                                        // Set the color from the
                                                        // color map
                                                        g.setColor(
                                                        colors[buffer[j-1][i-1]]);
                                                }
                                                // Draw a pixel
                                                g.drawLine(j, i, j, i);
                                        }
                                }
                        }
                    }
                }

                // Don't want the button to participate in focus traversable.
                public boolean isFocusable() {
                    return false;
                }
            };
            b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setOpaque(false);
            return b;
        }

        /**
         * Copied from <code>MetalDividerLayout</code> and overwritten or
         * layout buttons without border inserts.
         */
        public class MediknightDividerLayout implements LayoutManager {
            public void layoutContainer(Container c) {
                JButton     leftButton = getLeftButtonFromSuper();
                JButton     rightButton = getRightButtonFromSuper();
                JSplitPane  splitPane = getSplitPaneFromSuper();
                int         orientation = getOrientationFromSuper();
                int         oneTouchSize = getOneTouchSizeFromSuper();
                int         oneTouchOffset = getOneTouchOffsetFromSuper();
                Insets      insets = getInsets();

                // This layout differs from the one used in BasicSplitPaneDivider.
                // It does not center justify the oneTouchExpadable buttons.
                // This was necessary in order to meet the spec of the Metal
                // splitpane divider.
                if (leftButton != null && rightButton != null &&
                    c == MediknightSplitPaneDivider.this) {
                    if (splitPane.isOneTouchExpandable()) {
                        if (orientation == JSplitPane.VERTICAL_SPLIT) {
                            int extraY = (insets != null) ? insets.top : 0;
                            extraY += 2;
                            int blockSize = getDividerSize();

                            if (insets != null) {
                                blockSize -= (insets.top + insets.bottom);
                            }
                            blockSize = Math.min(blockSize, oneTouchSize);
                            leftButton.setBounds(oneTouchOffset, extraY,
                                                 blockSize * 2, blockSize);
                            rightButton.setBounds(oneTouchOffset +
                                                  oneTouchSize * 2, extraY,
                                                  blockSize * 2, blockSize);
                        }
                        else {
                            int blockSize = getDividerSize();
                            int extraX = (insets != null) ? insets.left : 0;
                            extraX += 2;

                            if (insets != null) {
                                blockSize -= (insets.left + insets.right);
                            }
                            blockSize = Math.min(blockSize, oneTouchSize);
                            leftButton.setBounds(extraX, oneTouchOffset,
                                                 blockSize, blockSize * 2);
                            rightButton.setBounds(extraX, oneTouchOffset +
                                                  oneTouchSize * 2, blockSize,
                                                  blockSize * 2);
                        }
                    }
                    else {
                        leftButton.setBounds(-5, -5, 1, 1);
                        rightButton.setBounds(-5, -5, 1, 1);
                    }
                }
            }

            public Dimension minimumLayoutSize(Container c) {
                return new Dimension(0,0);
            }

            public Dimension preferredLayoutSize(Container c) {
                return new Dimension(0, 0);
            }

            public void removeLayoutComponent(Component c) {}

            public void addLayoutComponent(String string, Component c) {}
        }

        /*
         * The following methods only exist in order to be able to access protected
         * members in the superclass, because these are otherwise not available
         * in any inner class.
         */

        int getOneTouchSizeFromSuper() {
            return super.ONE_TOUCH_SIZE;
        }

        int getOneTouchOffsetFromSuper() {
            return super.ONE_TOUCH_OFFSET;
        }

        int getOrientationFromSuper() {
            return super.orientation;
        }

        JSplitPane getSplitPaneFromSuper() {
            return super.splitPane;
        }

        JButton getLeftButtonFromSuper() {
            return super.leftButton;
        }

        JButton getRightButtonFromSuper() {
            return super.rightButton;
        }
    }
}