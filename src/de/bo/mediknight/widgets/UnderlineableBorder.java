/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * An implementation of the <code>Border</code> interface that adds the ability
 * to become underlined to any other border.
 *
 * @author chs@baltic-online.de
 * @author es@baltic-online.de
 * @version 1.0
 * @see Border
 */
public class UnderlineableBorder implements Border {

    /**
     * The original border that should be used as an actual border of the
     * component (underlined, if necessary).
     */
    protected Border originalBorder;

    /**
     * True iff the border should currently be underlined.
     */
    protected boolean underlined = false;

    /**
     * The color to underline the original border in (if at all).
     */
    protected Color underlineColor;

    /**
     * The default border to be used as original border if no other border is
     * requested by the caller.
     */
    protected static Border defaultBorder = BorderFactory.createEmptyBorder();

    /**
     * Create an instance of <code>UnderlineableBorder</code> using the
     * specified original border and underline color, originally underlined
     * iff <code>underlined</code> is <code>true</code>.
     *
     * @param originalBorder the border to use as original border
     * @param underlined whether the border should be initially underlined
     * @param underlineColor the color the border should be underlined in
     *
     * @since 1.0
     */
    public UnderlineableBorder(Border originalBorder, boolean underlined, Color underlineColor) {
        super();
        if(originalBorder == null)
            originalBorder = defaultBorder;
        setOriginalBorder(originalBorder);
        setUnderlined(underlined);
        setUnderlineColor(underlineColor);
    }

    /**
     * Create an instance of <code>UnderlineableBorder</code> using the
     * specified original border, originally underlined iff
     * <code>underlined</code> is <code>true</code>.
     *
     * @param originalBorder the border to use as original border
     * @param underlined whether the border should be initially underlined
     *
     * @since 1.0
     */
    public UnderlineableBorder(Border originalBorder, boolean underlined) {
        this(originalBorder, underlined, Color.red);
    }

    /**
     * Create an instance of <code>UnderlineableBorder</code> using the
     * specified original border and underline color.
     *
     * @param originalBorder the border to use as original border
     * @param underlineColor the color the border should be underlined in
     *
     * @since 1.0
     */
    public UnderlineableBorder(Border originalBorder, Color underlineColor) {
        this(originalBorder, false, underlineColor);
    }

    /**
     * Create an instance of <code>UnderlineableBorder</code> using the
     * specified original border.
     *
     * @param originalBorder the border to use as original border
     *
     * @since 1.0
     */
    public UnderlineableBorder(Border originalBorder) {
        this(originalBorder, false);
    }

    /**
     * Create an instance of <code>UnderlineableBorder</code> using the
     * specified underline color, originally underlined iff
     * <code>underlined</code> is <code>true</code>.
     *
     * @param underlined whether the border should be initially underlined
     * @param underlineColor the color the border should be underlined in
     *
     * @since 1.0
     */
    public UnderlineableBorder(boolean underlined, Color underlineColor) {
        this(defaultBorder, underlined, underlineColor);
    }

    /**
     * Create an instance of <code>UnderlineableBorder</code> using the
     * specified underline color.
     *
     * @param underlineColor the color the border should be underlined in
     *
     * @since 1.0
     */
    public UnderlineableBorder(Color underlineColor) {
        this(defaultBorder, false, underlineColor);
    }

    /**
     * Create an instance of <code>UnderlineableBorder</code>, originally
     * underlined iff <code>underlined</code> is <code>true</code>.
     *
     * @param underlined whether the border should be initially underlined
     *
     * @since 1.0
     */
    public UnderlineableBorder(boolean underlined) {
        this(defaultBorder, underlined, Color.red);
    }

    /**
     * Create an instance of <code>UnderlineableBorder</code> using default
     * values.
     *
     * @since 1.0
     */
    public UnderlineableBorder() {
        this(false);
    }

    /**
     * Return whether the border is currently underlined
     *
     * @since 1.0
     */
    public boolean isUnderlined() {
        return underlined;
    }

    /**
     * Set whether the border should be underlined
     *
     * @param underlined true iff the border should be underlined
     *
     * @since 1.0
     */
    public void setUnderlined(boolean underlined) {
        this.underlined = underlined;
    }

    /**
     * Set the original border to be used
     *
     * @param originalBorder the border to use as original border from now on
     *
     * @since 1.0
     */
    public void setOriginalBorder(Border originalBorder) {
        this.originalBorder = originalBorder;
    }

    /**
     * Return the current original border being used
     *
     * @since 1.0
     */
    public Border getOriginalBorder() {
        return originalBorder;
    }

    /**
     * Set the color the border should be underlined in
     *
     * @param underlineColor the color the border should be underlined in
     *
     * @since 1.0
     */
    public void setUnderlineColor(Color underlineColor) {
        this.underlineColor = underlineColor;
    }

    /**
     * Return the color the border will currently be underlined in
     *
     * @since 1.0
     */
    public Color getUnderlineColor() {
        return underlineColor;
    }

    /* Implementation of <code>Border</code>. */

    /**
     * Return the insets of the border.
     *
     * @param c the component for which this border insets value applies
     *
     * @since 1.0
     */
    public Insets getBorderInsets(Component c) {
        Insets i = originalBorder.getBorderInsets(c);
        if (c instanceof javax.swing.JTextArea)
            return new Insets(i.top, i.left + 4, i.bottom, i.right);
        return i;
    }

    /**
     * Return whether or not the border is opaque.
     *
     * @since 1.0
     */
    public boolean isBorderOpaque() {
        return originalBorder.isBorderOpaque();
    }

    /**
     * Paint the border for the specified component with the specified position
     * and size.
     *
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     *
     * @since 1.0
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        originalBorder.paintBorder(c, g, x, y, width, height);
        if (underlined) {
            g.setColor(underlineColor);
            if (c instanceof javax.swing.JTextArea) {
                g.fillRect(x, y, 2, height);
            }
            else {
                g.fillRect(x, y + height - 3, width, 2);
            }
        }
    }
}
