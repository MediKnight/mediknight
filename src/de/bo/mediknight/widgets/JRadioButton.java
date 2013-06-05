/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.util.Enumeration;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.EventListenerList;

/**
 * This subclass of <code>javax.swing.JRadioButton</code> implements the
 * <code>Mutable</code> interface.
 *
 * @author es@baltic-online.de
 * @author chs@baltic-online.de
 * @version 1.5
 * @see javax.swing.JRadioButton
 */
public class JRadioButton extends javax.swing.JRadioButton implements Mutable {

    private static final long serialVersionUID = 1L;
    
    private UndoHandler undoHandler = null;
    private String undoHandlerName = null;

    protected ButtonModel originallySelectedButton = null;

    /**
     * The original value of this <code>JRadioButton</code>
     */
    protected Boolean originalValue = null;

    /**
     * a list of those interested in receiving <code>MutableChangeEvent</code>s
     * from this instance.
     */
    EventListenerList listenerList = new EventListenerList();

    /**
     * Creates an initially unselected radio button with no set text.
     *
     * @since 1.1
     */
    public JRadioButton() {
        super();
        initialize();
    }

    /**
     * Creates an initially unselected radio button with the specified image
     * but no text.
     *
     * @param icon the image that the button should display.
     *
     * @since 1.1
     */
    public JRadioButton(Icon icon) {
        super(icon);
        initialize();
    }

    /**
     * Creates an <code>JRadioButton</code> where properties are taken from the
     * <code>Action</code> supplied.
     *
     * @param a the <code>Action</code> specifying the properties to be used
     * for the <code>JRadioButton</code>.
     *
     * @since 1.1
     */
    public JRadioButton(Action a) {
        super(a);
        initialize();
    }

    /**
     * Creates a <code>JRadioButton</code> with the specified image and
     * selection state, but no text.
     *
     * @param icon the image the the button should display.
     * @param selected if true, the button is initially seleced; otherwise, the
     * button is initially unselected.
     *
     * @since 1.1
     */
    public JRadioButton(Icon icon, boolean selected) {
        super(icon, selected);
        initialize();
    }

    /**
     * Creates a <code>JRadioButton</code> with the specified texst and
     * selection state.
     *
     * @param text the string displayed on the radio button
     * @param selected if true, the button is initially selected; otherwise,
     * the button is initially unselected
     *
     * @since 1.1
     */
    public JRadioButton(String text, boolean selected) {
        super(text, selected);
        initialize();
    }

    /**
     * Creates a <code>JRadioButton</code> that has the specified text and
     * image, and that is initially unselected.
     *
     * @param text the string displayed on the radio button
     * @param icon the image that thebutton should display.
     *
     * @since 1.1
     */
    public JRadioButton(String text, Icon icon) {
        super(text, icon);
        initialize();
    }

    /**
     * Creates a <code>JRadioButton</code> that has the specified text, image,
     * and selection state.
     *
     * @param text the string displayed on the radio button
     * @param icon the image that the button should display
     * @param selected if true, the button is initially selected, otherwise,
     * the button is initially unselected.
     *
     * @since 1.1
     */
    public JRadioButton(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
        initialize();
    }

    /**
     * Creates an unselected radio button with the specified text.
     *
     * @param text the string displayed on the radio button
     *
     * @since 1.0
     */
    public JRadioButton(String text) {
        super(text);
        initialize();
    }

    /**
     * Initialize the receiving <code>JRadioButton</code>.
     *
     * @since 1.1
     */
    protected void initialize() {
        this.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent ae) {
                setOriginalValue();
                updateBorder();
                fireMutableChanged();
            }
        });
        if(!(this.getModel() instanceof DefaultButtonModel))
            throw new javax.swing.undo.CannotUndoException();

    }

    public void setOriginalValue() {
        setOriginalValue(isSelected());
    }

    /**
     * Set the original value
     *
     * @param originalValue the new original value for the
     * <code>JRadioButton</code>
     *
     * @since 1.0
     */
    public void setOriginalValue(boolean originalValue) {
        setOriginalValue(new Boolean(originalValue));
    }

    /**
     * Set the original value
     *
     * @param originalValue the new originalValue for the
     * <code>JRadioButton</code>
     *
     * @since 1.0
     */
    public void setOriginalValue(Boolean originalValue) {
        if(this.originalValue == null)
            this.originalValue = originalValue;

        if(this.originallySelectedButton == null) {
            ButtonModel b = this.getModel();
            if(b instanceof DefaultButtonModel) {
                ButtonGroup bg = ((DefaultButtonModel)b).getGroup();
                if ( bg != null )
                    /** @todo: Fix NullPointerException, cause this state must not be happen */
                    originallySelectedButton = bg.getSelection();
            }
            else
                throw new javax.swing.undo.CannotUndoException();
        }
    }

    /**
     * Commit changes made.
     *
     * @since 1.5
     *
     * @todo test this method!
     */
    public void commit() {
        if(!isChanged())
            return;

        if(originalValue != null) {
            if(originallySelectedButton != null) {
                Enumeration<AbstractButton> buttons = ((DefaultButtonModel) this.getModel()).getGroup().getElements();
                while(buttons.hasMoreElements())
                    ((JRadioButton) buttons.nextElement()).commit();
                SwingUtilities.getRoot(this).repaint();
            }
        }
        fireMutableChanged();
        updateBorder();
    }

    /**
     * Return the original value
     *
     * @since 1.0
     */
    public Boolean getOriginalValue() {
        return originalValue;
    }

    // Implementation of <code>Mutable</code>

    /**
     * Forget the original value. The next call to <code>setSelected()</code>
     * will set the original value as well.
     *
     * @since 1.0
     */
    public void forgetOriginalValue() {
        setOriginalValue(null);
        updateBorder();
    }

    /**
     * Return whether the state of this <code>JRadioButton</code> has changed.
     *
     * @since 1.0
     */
    public boolean isChanged() {
        return (originalValue == null) ? false :
            (originalValue.booleanValue() != isSelected());
    }

    /**
     * Revert the <code>JRadioButton</code> to its original value.
     *
     * @since 1.0
     */
    public void revert() {
        if(originalValue != null) {
            if(originallySelectedButton != null) {
                ((DefaultButtonModel) this.getModel()).getGroup().setSelected(originallySelectedButton, true);
                SwingUtilities.getRoot(this).repaint();
            }
        }
        fireMutableChanged();
        updateBorder();
    }

    /**
     * Sets the state of the button. Also sets the original value if necessary.
     *
     * @param selected true if the button is selected, otherwise false
     *
     * @since 1.0
     */
    public void setSelected(boolean selected) {
        setOriginalValue(selected);
        super.setSelected(selected);
//        fireMutableChanged();
        updateBorder();
    }

    /**
     * update the <code>JComboBox</code>'s border, if it is an instance of
     * <code>UnderlineableBorder</code>
     *
     * @see UnderlineableBorder
     * @since 1.0
     */
    protected void updateBorder() {
        Container c = getParent();
        if(c instanceof JPanel) {
            ((JPanel) c).updateBorder();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repaint();
            }
        });
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
     * notification on this event type.
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


}