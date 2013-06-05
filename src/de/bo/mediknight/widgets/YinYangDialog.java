/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class implements a "please wait I'm busy" style of progress dialog
 * showing a turning yin yang symbol and a variable status text.
 * <p>Here's an example
 * <pre>public static void main(String[] s) {
 *       YinYangDialog d = new YinYangDialog(null, "I'm busy");
 *       d.setStatusText("Printing file\n\"abc.txt\"");
 *       d.run(new Runnable() {
 *           public void run() {
 *               System.out.println("Do something");
 *               try {
 *                   Thread.sleep(4000);
 *               } catch (InterruptedException e) {
 *               }
 *               System.out.println("Done");
 *           }
 *       });
 *   }</pre>
 *
 * @author sma@baltic-online.de
 * @version 1.0
 */
public class YinYangDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private Thread yinYangThread = null ;

    /**
     * Constructs the dialog.
     *
     * @param owner the dialog's owner
     * @param title the dialog's title
     *
     * @since 1.0
     */
    public YinYangDialog(Frame owner, String title) {
        super(owner, title, true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        javax.swing.JPanel p = (javax.swing.JPanel)getContentPane();
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(createYinYangIcon(), BorderLayout.CENTER);
        p.add(createStatusText(), BorderLayout.SOUTH);
    }

    /**
     * Sets the status text which is shown below the yin yang symbol.
     *
     * @param s the status string
     *
     * @since 1.0
     */
    public void setStatusText(String s) {
        int i = s.indexOf('\n');
        if (i != -1) {
            s = "<html><center>" + s;
            while ((i = s.indexOf('\n')) != -1)
                s = s.substring(0, i) + "<br>" + s.substring(i + 1);
        }
        statusLabel.setText(s);
    }

    /**
     * Shows the dialog after packing in a way to minimize screen flicker and
     * starts the animation timer.  You need to call this method to resize the
     * dialog after changing the status text.
     *
     * @param b tells whether the dialog shall be shown or hidden
     *
     * @since 1.0
     */
    public void setVisible(boolean b) {
        if (b) {
            super.setVisible(false);
            pack();
            centerDialog();
            timer.start();
        }
        else
            timer.stop();
        super.setVisible(b);
    }

    /**
     * Centers the dialog over its parent or in the screen if the dialog has no
     * parent.
     *
     * @since 1.0
     */
    private void centerDialog() {
        Container parent = getParent();
        Dimension d = parent.getSize();

        // workaround: if dialog has no owner, swings returns an invisible
        // frame located at 0,0 with dimension 0,0.  We think this is an error
        // as the dimension should be the screen size instead.
        if (d.width == 0 || d.height == 0)
            d = getToolkit().getScreenSize();

        setLocation(parent.getX() + (d.width - getWidth()) / 2,
                    parent.getY() + (d.height - getHeight()) / 2);
    }

    /**
     * Runs a <code>Runnable</code> method while showing the dialog.  Currently,
     * you cannot abort this operation so it is important that it will terminate.
     *
     * @param worker the operation to run while showing this dialog
     *
     * @since 1.0
     */
    public void run(final Runnable worker) {
        yinYangThread = new Thread(new Runnable() {
            public void run() {
                try {
                    worker.run();
                } finally {
                    setVisible(false);
                }
            }
        });
        yinYangThread.start();
        setVisible(true);
    }

    /**
     * Animation timer for yin yang symbol.
     *
     * @since 1.0
     */
    private Timer timer = new Timer(100, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            iconLabel.repaint();
        }
    });

    /**
     * Creates the yin yang symbol component.
     *
     * @since 1.0
     */
    private JLabel createYinYangIcon() {
        iconLabel = new JLabel(new YinYangIcon());
        return iconLabel;
    }

    /**
     * Creates the status text component.
     *
     * @since 1.0
     */
    private JLabel createStatusText() {
        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setForeground(Color.black);
        return statusLabel;
    }

    private JLabel iconLabel;
    private JLabel statusLabel;

    /**
     * This class implements a yin and yang symbol as an icon.
     * Each time the icon is drawn it gets rotated a bit.
     *
     * @since 1.0
     * @version 1.0
     */
    static class YinYangIcon implements Icon {

        private int size;
        private int rotate;
        private Color yinColor;
        private Color yangColor;

        /**
         * Create a new <code>YinYangIcon</code> using the default size and
         * colors.
         *
         * @since 1.0
         */
        public YinYangIcon() {
            this(80, Color.white, Color.black);
        }

        /**
         * Create a new <code>YinYangIcon</code> with the specified size and
         * colors.
         *
         * @param size the size of the <code>YinYangIcon</code>
         * @param yinColor the color to draw the Yin part in
         * @param yangColor the color to draw the Yang part in
         *
         * @since 1.0
         */
        public YinYangIcon(int size, Color yinColor, Color yangColor) {
            this.size = size;
            this.yinColor = yinColor;
            this.yangColor = yangColor;
        }

        /**
         * Return the width of the receiving </code>YinYangIcon</code>.
         *
         * @since 1.0
         */
        public int getIconWidth() {
            return size;
        }

        /**
         * Return the height of the receiving </code>YinYangIcon</code>.
         *
         * @since 1.0
         */
        public int getIconHeight() {
            return size;
        }

        /**
         * Paints the <code>YinYangIcon</code> on the specified graphics context
         * using the specified parent object and the specified x and y position
         *
         * @param c the parent widget of the <code>YinYangIcon</code>
         * @param g the <code>Graphics</code> object to draw upon
         * @param x the x position of the icon
         * @param y the y position of the icon
         *
         * @since 1.0
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D)g;
            int sz = size / 2;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x + sz, y + sz);
            g2.rotate(Math.toRadians((rotate += 36) % 360));
            g2.setColor(yinColor);
            g2.fillArc(-sz, -sz, size, size, 0, 180);
            g2.setColor(yangColor);
            g2.fillArc(-sz, -sz, size, size, 0, -180);
            g2.fillArc(-sz, sz / -2 + 1 /*rounding error*/, sz, sz, 0, 180);
            g2.setColor(yinColor);
            g2.fillArc(0, sz / -2 - 1 /*rounding error*/, sz, sz, 0, -180);
            sz /= 4;
            g2.fillOval(sz * -5 / 2, sz / -2, sz, sz);
            g2.setColor(yangColor);
            g2.fillOval(sz * 3 / 2, sz / -2, sz, sz);
        }
    }
}
