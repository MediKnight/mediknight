/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.AWTEventMulticaster;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.ListModel;

/**
 * This subclass of <code>JList</code> supports an ActionListener for double
 * click event, just like the old <code>java.awt.List</code> does.
 *
 * @autor sma@baltic-online.de
 * @author chs@baltic-online.de
 * @version 1.1
 */
public class JList extends javax.swing.JList {

    /**
     * Constructs a <code>JList</code> with an empty model.
     *
     * @since 1.0
     */
    public JList() {
        super();
    }

    /**
     * Constructs a <code>JList</code> that displays the elements in the
     * specified, non-null model.
     *
     * @param <code>dataModel</code> the data model for this list
     * @exception IllegalArgumentException if <code>dataModel</code> is
     * <code>null</code>
     *
     * @since 1.0
     */
    public JList(ListModel dataModel) throws IllegalArgumentException {
        super(dataModel);
    }

    /**
     * Constructs a <code>JList</code> that displays the elements in the
     * specified array.
     *
     * @param listData the aray of <code>Object</code>s to be loaded into the
     * data model.
     *
     * @since 1.0
     */
    public JList(Object[] listData) {
        super(listData);
    }

    /**
     * Constructs a <code>JList</code> that displays the elements in the
     * specified <code>Vector</code>.
     *
     * @param listData the <code>Vector</code> to be loaded into the data model.
     *
     * @since 1.0
     */
    public JList(Vector listData) {
        super(listData);
    }

    private ActionListener listeners;

    private static final MouseListener listener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                JList list = (JList)e.getSource();
                list.fireActionPerformed(new ActionEvent(list, 0, null));
            }
        }
    };

    /**
     * Ads the specified action listener to receive action events from this list.
     *
     * @param l the action listener to be added
     *
     * @since 1.0
     * @see java.awt.List#addActionListener
     */
    public void addActionListener(ActionListener l) {
        if (listeners == null)
            addMouseListener(listener);
        listeners = AWTEventMulticaster.add(listeners, l);
    }

    /**
     * Removes the specified action lisener so that it no longer receives
     * action events from this list.
     *
     * @param l the action listener to be removed
     *
     * @since 1.0
     * @see java.awt.List#removeActionListener
     */
    public void removeActionListener(ActionListener l) {
        listeners = AWTEventMulticaster.remove(listeners, l);
        if (listeners == null)
            removeMouseListener(listener);
    }

    /**
     * Callback method for action events happening that notifies listeners
     * of the event.
     *
     * @param e the ActionEvent having happened
     *
     * @since 1.0
     */
    protected void fireActionPerformed(ActionEvent e) {
        listeners.actionPerformed(e);
    }
}
