/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.*;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * A simple tiny layout manager aligning components in a grid with the
 * following constraints
 * <pre>x, y, align</pre>
 * where x denots horizontal layout, y vertical layout and align the horizontal
 * alignment. All values are integers.  -1 means space filling, 0 means the
 * component's preferred size and a number larger then 0 is a logical size.
 * The alignment can be C, N, E, W, S, NW...
 *
 * @author sma@baltic-online.de
 * @version 1.1
 * @see LayoutManager2
 * @see LayoutManager
 */
public class FlexGridLayout implements LayoutManager2 {

    private int rows;
    private int cols;
    private int hgap;
    private int vgap;

    private Hashtable constraints = new Hashtable(10);

    // Constraint values used for alignment
    private static final int C = 0;
    private static final int N = 1;
    private static final int E = 2;
    private static final int W = 3;
    private static final int S = 4;
    private static final int NW = 5;
    private static final int NE = 6;
    private static final int SW = 7;
    private static final int SE = 8;

    /**
     * Create a new <code>FlexGridLayout</code> with one row and an arbitrary
     * number of columns, setting both <code>hgap</code> and <code>vgap</code> to 0.
     *
     * @since 1.0
     */
    public FlexGridLayout() {
        this(1, 0, 0, 0);
    }

    /**
     * Create a new <code>FlexGridLayout</code> with the specified number of
     * rows and columns, setting both <code>hgap</code> and <code>vgap</code>
     * to 0.
     *
     * @param rows the number of rows for the new <code>FlexGridLayout</code>
     * @param cols the number of columns for the new <code>FlexGridLayout</code>
     *
     * @since 1.0
     */
    public FlexGridLayout(int rows, int cols) {
        this(rows, cols, 0, 0);
    }

    /**
     * Create a new <code>FlexGridLayout</code> with the specified number of
     * rows and columns and the specified values for <code>hgap</code> and
     * <code>hgap</code>
     *
     * @param rows the number of rows for the new <code>FlexGridLayout</code>
     * @param cols the number of columns for the new <code>FlexGridLayout</code>
     * @param hgap the horizontal gap for the new <code>FlexGridLayout</code>
     * @param vgap the vertical gap for the new <code>FlexGridLayout</code>
     *
     * @since 1.0
     */
    public FlexGridLayout(int rows, int cols, int hgap, int vgap) {
        if ( rows == 0 && cols == 0 ) {
            throw new IllegalArgumentException("rows and cols cannot both be zero");
        }

        this.rows = rows;
        this.cols = cols;
        this.hgap = hgap;
        this.vgap = vgap;
    }

    /**
     * Adds the specified component to the layout, using the specified
     * constraint object. Ignored by this layout manager.
     *
     * @param name the component name
     * @param comp the component to be added
     *
     * @since 1.0
     * @see LayoutManager#addLayoutComponent
     */
    public void addLayoutComponent(String name, Component comp) {
        // ignored
    }

    /**
     * Remove the specified component from the layout.
     *
     * @param comp the component to be removed
     *
     * @since 1.0
     * @see LayoutManager#removeLayoutComponent
     */
    public void removeLayoutComponent(Component comp) {
        // for removed components, we can remove the associated constraint
        constraints.remove(comp);
    }

    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.
     *
     * @param comp the component to be added
     * @param constraint where/how the component is added to the layout.
     *
     * @since 1.0
     * @see LayoutManager2#addLayoutComponent
     */
    public void addLayoutComponent(Component comp, Object constraint) {
        // associate the constraint with specified component
        constraints.put(comp, new BLC((String)constraint));
    }

    /**
     * Invalidates the layout, indicating the if the layout manager has cached
     * information, it should be discarded.
     *
     * @param target the target container
     *
     * @since 1.0
     * @see LayoutManager2#invalidateLayout
     */
    public void invalidateLayout(Container target) {
        // ignored - we don't have any cache to clear
    }

    /**
     * Lays out the container in the specified panel.
     *
     * @param target the component which needs to be laid out
     *
     * @since 1.0
     * @see LayoutManager#layoutContainer
     */
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            int ncomponents = target.getComponentCount();
            if (ncomponents == 0)
                return;

            int widths[] = new int[ncomponents];
            int heights[] = new int[ncomponents];
            int nrows = rows;
            int ncols = cols;

            if (nrows > 0)
                ncols = (ncomponents + nrows - 1) / nrows;
            else
                nrows = (ncomponents + ncols - 1) / ncols;

            // first pass - get the size of non-spacefilling components
            for (int i = 0; i < ncomponents; i++) {
                Component c = target.getComponent(i);
                if (!c.isVisible())
                    continue;

                BLC blc = (BLC)constraints.get(c);
                if (blc == null || blc.x == 0)
                    widths[i] = c.getPreferredSize().width;
                else if (blc.x > 0)
                    widths[i] = blc.x * 10;
                else
                    widths[i] = -1;

                if (blc == null || blc.y == 0)
                    heights[i] = c.getPreferredSize().height;
                else if (blc.y > 0)
                    heights[i] = blc.y * 20;
                else
                    heights[i] = -1;
            }

            Insets insets = target.getInsets();
            Dimension dt = target.getSize();
            dt.width -= insets.left + insets.right + ((ncols - 1) * hgap);
            dt.height -= insets.top + insets.bottom + ((nrows - 1) * vgap);

            /* WARNING!!
             *
             * This code fixes a problem with unreported insets when a JPanel is
             * embedded within a JViewport (used by JScrollPane).  The JPanel
             * does NOT report the insets created by the JScrollPane and the
             * dimensions used are therefore always too large for the
             * display area.
             *
             */
            if (target.getParent() != null) {
                if (target.getParent().getClass().getName().equals("javax.swing.JViewport"))
                    dt.width -= 4;
            }


            int minColWidth[] = new int[ncols];
            int minRowHeight[] = new int[nrows];
            boolean colSpace[] = new boolean[ncols];
            boolean rowSpace[] = new boolean[nrows];

            // second pass - store some general inforamtion that
            // is used later to size the components.
            int index = 0;
            for (int row = 0; row < nrows; row++) {
                for (int column = 0; column < ncols; column++, index++) {
                    if (index >= ncomponents)
                        continue;

                    minColWidth[column] = Math.max(minColWidth[column], widths[index]);
                    minRowHeight[row] = Math.max(minRowHeight[row], heights[index]);

                    if (widths[index] < 0)
                        colSpace[column] = true;
                    if (heights[index] < 0)
                         rowSpace[row] = true;
                }
            }

            // third pass - allocate available width to components that
            // require some spacefilling.
            int noColFill = 0;
            for (int i = 0; i < ncols; i++) {
                if (colSpace[i] == true)
                    noColFill++;
            }

            if (noColFill > 0) {

                index = 0;
                for (int row = 0; row < nrows; row++) {
                    int nofill = 0;
                    int tempColFill = 0;
                    int usedWidth = 0;
                    for (int i = 0; i < ncols; i++) {
                        usedWidth += minColWidth[i];
                    }

                    int widthFill = (dt.width - usedWidth) / noColFill;

                    for (int column = 0; column < ncols; column++, index++) {
                        if(index >= ncomponents)
                            continue;

                        if (widths[index] < 0) {
                            if(minColWidth[column] > widthFill && tempColFill > 1) {
                                widths[index] = minColWidth[column];

                                tempColFill = noColFill - (++nofill);
                                if (tempColFill < 1)
                                    tempColFill = 1;

                                widthFill = (dt.width - usedWidth) / tempColFill;
                            } else {
                                widths[index] = minColWidth[column] + widthFill;
                                minColWidth[column] = widths[index];
                            }
                        }
                    }
                }
            }

            // fourth pass - allocate available height to components that
            // require some spacefilling.
            int noRowFill = 0;
            for (int i = 0; i < nrows; i++) {
                if (rowSpace[i] == true)
                    noRowFill++;
            }

            if (noRowFill > 0) {
                int minUsedHeight = 0;
                for (int i = 0; i < nrows; i++) {
                    minUsedHeight += minRowHeight[i];
                }

                int heightFill = (dt.height - minUsedHeight) / noRowFill;

                index = 0;
                for (int row = 0; row < nrows; row++) {

                    int startHeight = minRowHeight[row];
                    for (int column = 0; column < ncols; column++, index++) {
                        if(index >= ncomponents)
                            continue;

                        if(heights[index] < 0)  {
                            heights[index] = heightFill + startHeight;
                            minRowHeight[row] = heights[index];
                        }
                    }
                }
            }

            // fifth pass - now layout the components in the correct size
            // grids - these are the max for that row and that column.
            index = 0;
            int y = insets.top;
            for (int row = 0, x = insets.left; row < nrows; row++) {
                for (int column = 0; column < ncols; x += minColWidth[column] + vgap, column++, index++) {
                    if (index >= ncomponents)
                        continue;

                    Component c = target.getComponent(index);
                    if (!c.isVisible())
                        continue;

                    BLC blc = (BLC)constraints.get(c);
                    int X = x;
                    int Y = y;
                    switch (blc.align) {
                    case C:
                        X = x + (minColWidth[column] - widths[index]) / 2;
                        Y = y + (minRowHeight[row] - heights[index]) / 2;
                        break;
                    case N:
                        X = x + (minColWidth[column] - widths[index]) / 2;
                        Y = y;
                        break;
                    case E:
                        X = x + minColWidth[column] - widths[index];
                        Y = y + (minRowHeight[row] - heights[index]) / 2;
                        break;
                    case W:
                        X = x;
                        Y = y + (minRowHeight[row] - heights[index]) / 2;
                        break;
                    case S:
                        X = x + (minColWidth[column] - widths[index]) / 2;
                        Y = y + (minRowHeight[row] - heights[index]);
                        break;
                    case NW:
                        X = x;
                        Y = y;
                        break;
                    case NE:
                        X = x + minColWidth[column] - widths[index];
                        Y = y;
                        break;
                    case SW:
                        X = x;
                        Y = y + (minRowHeight[row] - heights[index]);
                        break;
                    case SE:
                        X = x + minColWidth[column] - widths[index];
                        Y = y + (minRowHeight[row] - heights[index]);
                        break;
                    default:
                        X = x;
                        Y = y;
                    }
                    c.setBounds(X, Y, widths[index], heights[index]);
                }
                x = insets.left;
                y += minRowHeight[row] + hgap;
            }
        }
    }

    /**
     * Calculates the minimum size dimensions for the specified  panel given
     * the components in the specified parent container.
     *
     * @param target the component to be laid out
     *
     * @since 1.0
     * @see LayoutManager#minimumLayoutSize
     */
    public Dimension minimumLayoutSize(Container target) {
        return preferredLayoutSize(target); // shortcut
    }

    /**
     * Calculates the preferred size dimensions for the specified panel given
     * the components in the specified parent container.
     *
     * @param target the component to be laid out
     *
     * @since 1.0
     * @see LayoutManager#preferredLayoutSize
     */
    public Dimension preferredLayoutSize(Container target) {
        Dimension d = new Dimension();

        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int ncomponents = target.getComponentCount();
            int nrows = rows;
            int ncols = cols;

            if (nrows > 0)
                ncols = (ncomponents + nrows - 1) / nrows;
            else
                nrows = (ncomponents + ncols - 1) / ncols;

            int minColWidth[] = new int[ncols];
            int minRowHeight[] = new int[nrows];
            int index = 0;

            for (int row = 0; row < nrows; row++) {
                for (int column = 0; column < ncols; column++, index++) {
                    if(index >= ncomponents)
                        continue;

                    Component c = target.getComponent(index);
                    if (!c.isVisible())
                        continue;

                    BLC blc = (BLC)constraints.get(c);

                    if (blc == null || blc.x <= 0)
                        d.width = Math.max(minColWidth[column], c.getPreferredSize().width);
                    else
                        d.width = Math.max(minColWidth[column], blc.x * 10);

                    if (blc == null || blc.y <= 0)
                        d.height = Math.max(minRowHeight[row], c.getPreferredSize().height);
                    else
                        d.height = Math.max(minRowHeight[row], blc.y * 20);

                    minColWidth[column] = Math.max(minColWidth[column], d.width);
                    minRowHeight[row] = Math.max(minRowHeight[row], d.height);

                }
            }

            int totalWidth = 0;
            for (int i = 0; i < ncols; i++)
                totalWidth += minColWidth[i];

            int totalHeight = 0;
            for (int i = 0; i < nrows; i++)
                totalHeight += minRowHeight[i];

            d.width = insets.left + insets.right + totalWidth + (ncols-1)*hgap;
            d.height = insets.top + insets.bottom + totalHeight + (nrows-1)*vgap;

            /* WARNING!!
             *
             * This code fixes a problem with unreported insets when a JPanel is
             * embedded within a JViewport (used by JScrollPane).  The JPanel
             * does NOT report the insets created by the JScrollPane and the
             * dimensions used are therefore always too large for the
             * display area.
             *
             */
            if (target.getParent() != null) {
                if (target.getParent().getClass().getName().equals("javax.swing.JViewport"))
                    d.width += 4;
            }
        }

        return d;
    }

    /**
     * Returns the maximum size of this component.
     *
     * @param target the component to be laid out
     *
     * @since 1.0
     * @see LayoutManager2#maximumLayoutSize
     */
    public Dimension maximumLayoutSize(Container target) {
        // shortcut
        return preferredLayoutSize(target);
    }

    /**
     * Returns the alignment along the x axis.
     *
     * @param target the component to be laid out
     *
     * @since 1.0
     * @see LayoutManager2#getLayoutAlignmentX
     */
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.
     *
     * @param target the component to be laid out
     *
     * @since 1.0
     * @see LayoutManager2#getLayoutAlignmentY
     */
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    /**
     * Constraint class for FlexGridLayout. BLC seems to stand for "... layout
     * constraint", but I have no idea what the B means. Maybe "Baltic Online",
     * but I doubt that.
     *
     * @author unknown (probably sml@baltic-online.de or sma@baltic-online.de)
     * @version 1.0
     * @see FlexGridLayout
     */
    private static class BLC {
        int x;
        int y;
        int align = -99;

        /**
         * Construct a new BLC object based on the specified constraint string
         *
         * @param constraint a string specifying the constraints
         *
         * @since 1.0
         */
        BLC(String constraint) {
            StringTokenizer st = new StringTokenizer(constraint, ",");
            if (st.hasMoreTokens()) {
                x = Integer.parseInt(st.nextToken());
                if (st.hasMoreTokens()) {
                    y = Integer.parseInt(st.nextToken());
                    if (st.hasMoreTokens())
                        align = decodeAlign(st.nextToken());
                }
            }
        }

        /**
         * Decode an alignment string.
         *
         * @param the string containing the alignment specification
         * @exception IllegalArgumentException if the string does not specify
         * a valid alignment
         *
         * @since 1.0
         */
        private int decodeAlign(String s) {
            s = s.toLowerCase();
            if (s.equals("c"))
                return C;
            if (s.equals("n"))
                return N;
            if (s.equals("w"))
                return W;
            if (s.equals("e"))
                return E;
            if (s.equals("s"))
                return S;
            if (s.equals("nw"))
                return NW;
            if (s.equals("ne"))
                return NE;
            if (s.equals("sw"))
                return SW;
            if (s.equals("se"))
                return SE;

            throw new IllegalArgumentException("unknown alignment " + s);
        }
    }
}
