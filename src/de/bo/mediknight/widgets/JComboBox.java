/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

/**
 * A specialized <code>JComboBox</code> for Mediknight that implements the
 * <code>Mutable</code> interface.
 *
 * @author chs@baltic-online.de
 * @author es@baltic-online.de
 * @version 1.5
 *
 */
public class JComboBox extends javax.swing.JComboBox implements Mutable {

    private UndoHandler undoHandler = null;
    private String undoHandlerName = null;

    /**
     * the original value of the <code>JComboBox</code>. When set to null,
     * a subsequent call to <code>setItem</code> will set this attribute as
     * well.
     */
    protected Object originalItem = null;

    /**
     * a list of those interested in receiving <code>MutableChangeEvent</code>s
     * from this instance.
     */
    EventListenerList listenerList = new EventListenerList();

    /**
     * Creates a <code>JComboBox</code> with a default data model.
     *
     * @since 1.0
     */
    public JComboBox() {
        super();
        initialize();
    }

    /**
     * Creates a <code>JComboBox</code> that takes its items from an existing
     * <code>ComboBoxModel</code>.
     *
     * @param aModel the <code>ComboBoxModel</code> to use
     *
     * @since 1.0
     */
    public JComboBox(ComboBoxModel aModel) {
        super(aModel);
        initialize();
    }

    /**
     * Crates a <code>JComboBox</code> that contains the elements in the
     * specified array.
     *
     * @param items the array to use
     *
     * @since 1.0
     */
    public JComboBox(Object[] items) {
        super(items);
        initialize();
    }

    /**
     * Creates a <code>JComboBox/code> that contains the elements in the
     * specified Vector.
     *
     * @param items the vector to use
     *
     * @since 1.1
     */
    public JComboBox(Vector items) {
        super(items);
        initialize();
    }

    /**
     * Set the item that should be edited.
     *
     * @param item the item that should be edited
     *
     * @since 1.0
     * @see #originalItem
     */
    public void setSelectedItem(Object item) {
        super.setSelectedItem(item);
        if(originalItem == null)
            setOriginalItem(item);
        else
            if(isChanged())
                fireMutableChanged();
    }

    /**
     * Selects the item at index <code>anIndex</code>.
     *
     * @param anIndex an integer specifying the list item to select,
     *			where 0 specifies
     *                	the first item in the list
     * @exception IllegalArgumentException if <code>anIndex</code> < -1 or
     *			<code>anIndex</code> is greater than or equal to size
     */
    public void setSelectedIndex(int anIndex) {
        setSelectedItem(getItemAt(anIndex));
    }

    /**
     * Return the edited item
     *
     * @since 1.0
     */
    public Object getItem() {
        return getItemAt(getSelectedIndex());
    }

    /**
     * Additional initialization. Subclasses that override this method should
     * always call super.initialize().
     *
     * @since 1.0
     */
    protected void initialize() {
        setBorder(new UnderlineableBorder(getBorder(), getForeground()));
        getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateBorder();
                fireMutableChanged();
            }
        });
        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateBorder();
                fireMutableChanged();
            }
        });

    }

    /**
     * Set the original item
     *
     * @param item the new original item
     *
     * @since 1.0
     */
    public void setOriginalItem (Object item) {
        originalItem = item;
    }

    /**
     * Return the original item
     *
     * @since 1.0
     */
    public Object getOriginalItem() {
        return originalItem;
    }

    /**
     * update the <code>JComboBox</code>'s border, if it is an instance of
     * <code>UnderlineableBorder</code>
     *
     * @see UnderlineableBorder
     * @since 1.0
     */
    public void updateBorder() {
        Border border = getBorder();
        if(border instanceof UnderlineableBorder)
            ((UnderlineableBorder) border).setUnderlined
                ((getOriginalItem() == null) ? false : isChanged());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repaint();
            }
        });
    }

    // Implementation of Mutable

    /**
     * return whether the <code>JComboBox</code>'s value has been changed.
     *
     * @since 1.0
     */
    public boolean isChanged() {
        return((getOriginalItem() == null) ? false : !getOriginalItem().equals(getItem()));
    }

    /**
     * Revert the item being edited to the original value
     *
     * @since 1.0
     */
    public void revert() {
        setSelectedItem(getOriginalItem());
        fireMutableChanged();
    }

    /**
     * Forget the original value. The next call to <code>SetItem</code>
     * will set the original value as well as the current one.
     *
     * @see #originalItem
     * @since 1.0
     */
    public void forgetOriginalValue() {
        setOriginalItem(null);
        updateBorder();
    }

    /**
     * Register an object to receive <code>MutableChangeEvent</code>s from us
     * in the future.
     *
     * @since 1.2
     */
    public void addMutableChangeListener(MutableChangeListener l) {
       listenerList.add(MutableChangeListener.class, l);
    }

    /**
     * Deregister an object to receive <code>MutableChangeEvent</code>s from us
     * in the future.
     *
     * @since 1.2
     */
    public void removeMutableChangeListener(MutableChangeListener l) {
        listenerList.remove(MutableChangeListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * <p>
     * This code was copied from the documentation for the class
     * <code>EventListenerList</code>.
     *
     * @since 1.2
     */
    public void fireMutableChanged() {
        if(undoHandler == null) {
            undoHandler = UndoUtilities.findUndoHandler(this);
            addMutableChangeListener(undoHandler);
        }
        MutableChangeEvent fooEvent = new MutableChangeEvent(this, isChanged());
        UndoUtilities.dispatchMutableChangeEvent(fooEvent, listenerList);
    }

    /**
     * Return the name of the <code>UndoHandler</code> object responsible for
     * handling Undo for this widget.
     *
     * @since 1.3
     */
    public String getResponsibleUndoHandler() {
        return undoHandlerName;
    }

    /**
     * Set the name of the <code>UndoHandler</code> widget responsible for
     * handling Undo for this widget.
     *
     * @param s the name of the new <code>UndoHandler</code>
     *
     * @since 1.4
     */
    public void setResponsibleUndoHandler(String s) {
        if(!isChanged()) {
            if(undoHandler != null)
                removeMutableChangeListener(undoHandler);

            undoHandlerName = s;
            undoHandler = null;
        }
    }

    /**
     * Commit changes made.
     *
     * @since 1.5
     */
    public void commit() {
        setSelectedItem(getOriginalItem());
    }

}