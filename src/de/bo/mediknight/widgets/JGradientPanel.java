/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.*;

/**
 * This class implements a <code>JPanel</code> with a gradient fill background.
 *
 * @author sma@baltic-online.de
 * @author chs@baltic-online.de
 * @version 1.1
 */
public class JGradientPanel extends JPanel {

    // the JGradientPanel's extent
    private float extent = 0.5f;

    private Color gradientColor;

    /**
     * Constructs a new <code>JGradientPanel</code>.
     *
     * @since 1.0
     */
    public JGradientPanel() {
        super();
    }

    /**
     * Constructs a new <code>JGradientPanel</code>.
     *
     * @param layout the layout manager to use
     *
     * @since 1.0
     */
    public JGradientPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * Constructs a new <code>JGradientPanel</code>.
     *
     * @param layout the layout manager to use
     * @param extent the extent of the gradient part of the panel
     *
     * @throws <code>IllegalArgumentException</code> iff extent is not in
     * [0.0, 1.0]
     *
     * @since 1.1
     */
    public JGradientPanel(LayoutManager layout, float extent) throws IllegalArgumentException {
        super(layout);
        if ( extent > 1.0 || extent < 0.0 ) {
            throw new IllegalArgumentException("extent must be in the range of 0.0 to 1.0");
        }
        this.extent = extent;
    }

    /**
     * Constructs a new <code>JGradientPanel</code>.
     *
     * @param extent the extent of the gradient part of the panel
     *
     * @throws <code>IllegalArgumentException</code> iff extent is not in
     * [0.0, 1.0]
     *
     * @since 1.1
     */
    public JGradientPanel(float extent) throws IllegalArgumentException {
        super();
        if ( extent > 1.0 || extent < 0.0 ) {
            throw new IllegalArgumentException("extent must be in the range of 0.0 to 1.0");
        }
        this.extent = extent;
    }

    /**
     * Sets the second gradient color.  The panel with show a gradient fill
     * between its background color and this color.
     *
     * @param c the gradient fill color
     *
     * @since 1.0
     */
    public void setGradientColor(Color c) {
        gradientColor = c;
        repaint();
    }

    /**
     * Returns the gradient fill color.
     *
     * @since 1.0
     */
    public Color getGradientColor() {
        return gradientColor;
    }

    /**
     * Set the gradient's extent (0.0 to 1.0).
     *
     * @param extent the extent of the gradient part of the panel
     *
     * @throws <code>IllegalArgumentException</code> iff extent is not in
     * [0.0, 1.0]
     *
     * @since 1.1
     */
    public void setExtent(float extent) throws IllegalArgumentException {
        if (extent > 1.0 || extent < 0.0) {
            throw new IllegalArgumentException("extent must be in the range of 0.0 to 1.0");
        }
        this.extent = extent;
    }

    /**
     * Return the GradientPanel's extent.
     *
     * @since 1.1
     */
    public float getExtent() {
        return extent;
    }

    /**
     * Paints the component.  Currently, only a vertical fill for the upper
     * half of the component is supported.
     *
     * @param g the <code>Graphics</code> object upon which to paint
     *
     * @since 1.0
     */
    protected void paintComponent(Graphics g) {
        if (gradientColor == null) {
            super.paintComponent(g);
            return;
        }
        if (isOpaque()) {
            Graphics2D g2 = (Graphics2D)g;
            Paint gradientPaint =  new GradientPaint(
                0f, 0f, getGradientColor(),
                0f, getHeight() * getExtent(), getBackground()
            );
            g2.setPaint(gradientPaint);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
