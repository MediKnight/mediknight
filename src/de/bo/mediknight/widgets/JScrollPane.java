/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.Component;

import javax.swing.event.EventListenerList;

/**
 * This subclass of <code>javax.swing.JScrollPane</code> implements the
 * <code>Mutable</code> interface, delegating calls to the methods specified
 * there to its child widget (if able to).
 *
 * @author es@baltic-online.de
 * @author chs@baltic-online.de
 * @version 1.5
 * @see Mutable
 * @see javax.swing.JScrollPane
 */
public class JScrollPane extends javax.swing.JScrollPane
                         implements Mutable, MutableChangeListener {

    /**
     * true iff the component being displayed by this <code>JScrollPane</code>
     * implements <code>Mutable</code>
     */
    private boolean myComponentIsMutable = false;

    /**
     * the component being handled by this <code>JScrollPane</code>.
     */
    private java.awt.Component myComponent = null;

    /**
     * a list of those interested in receiving <code>MutableChangeEvent</code>s
     * from this instance.
     */
    EventListenerList listenerList = new EventListenerList();

    /**
     * Register whether the child widget managed by this
     * <code>JScrollPane</code> implements the <code>Mutable</code> interface.
     *
     * @param c the component to be registered (maybe).
     *
     * @since 1.0
     */
    private void register(Component c) {
        // if we already have a component that is Mutable, unregister ourselves
        // as a listener for MutableChangeEvents from that one.
        if((myComponent != null) && myComponentIsMutable)
            ((Mutable) myComponent).removeMutableChangeListener(this);

        // save the new component and whether it is Mutable for later use.
        myComponentIsMutable = (c instanceof Mutable);
        myComponent = c;

        // If the component is Mutable, register ourselves as a listener
        // for MutableChangeEvents.
        if(myComponentIsMutable)
            ((Mutable) myComponent).addMutableChangeListener(this);

    }

    /**
     * Creates a viewport if necessary and then sets its view. Applications
     * that don't provide the view directly to the <code>JScrollPane</code>
     * constructor should use this method to specify the scrollable child
     * that's going to be displayed in the scrollpane. Applications should *NOT*
     * add children directly to the scrollpane!
     *
     * @param view the component to add to the viewport
     *
     * @since 1.2
     */
    public void setViewportView(Component view) {
        super.setViewportView(view);
        register(view);
    }

    /**
     * Creates an empty (no viewport view) <code>JScrollPane</code> where both
     * horizontal and vertical scrollbars appear when needed.
     *
     * @since 1.0
     * @see javax.swing.JScrollPane#JScrollPane()
     */
    public JScrollPane() {
        super();
    }

    /**
     * Creates a <code>JScrollPane</code> that displays the contents of the
     * specified component, where both horizontal and vertical scrollbars
     * appear whenever the component's contents are larger than the view.
     *
     * @param c the component to display in the scrollpane's viewport
     *
     * @since 1.0
     * @see javax.swing.JScrollPane#JScrollPane(java.awt.Component)
     */
    public JScrollPane(Component c) {
        super(c);
        register(c);
    }

    /**
     * Creates a <code>JScrollPane</code> that displays the view compoment in a
     * viewport whose view position can be controlled with a pair of
     * scrollbars.
     *
     * @param c the component to display in the scrollpane's viewport
     * @param v an integer that specifies the vertical scrollbar policy
     * @param h an integer that specifies the horizontal scrollbar policy
     *
     * @since 1.0
     * @see javax.swing.JScrollPane#JScrollPane(java.awt.Component, int, int)
     */
    public JScrollPane(Component c, int v, int h) {
        super(c, v, h);
        register(c);
    }

    /**
     * Creates an empty (no viewport view) <code>JScrollPane</code> with
     * specified scrollbar policies.
     *
     * @param v an integer that specifies the vertical scrollbar policy
     * @param h an integer that specifies the horizontal scrollbar policy
     *
     * @since 1.0
     * @see javax.swing.JScrollPane#JScrollPane(int, int)
     */
    public JScrollPane(int v, int h) {
        super(v, h);
    }

    /**
     * Adds the specified compoment to this <code>JScrollPane</code> at the
     * specified index using the specified constraints.
     *
     * @param comp the component to be added.
     * @param constraints an object expressing layout constraints for this
     * component
     * @param index the position in the container's list at which to insert the
     * component, where -1 means insert at the end
     *
     * @since 1.1
     * @see java.awt.Container(java.awt.Copmonent, java.lang.Object, int)
     */
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        register(comp);
    }

    // Implementation of Mutable

    /**
     * return whether the receiver's value has been changed.
     *
     * @since 1.0
     */
    public boolean isChanged() {
        if(myComponentIsMutable)
            return ((Mutable) myComponent).isChanged();
        else
            return false;
    }

    /**
     * Revert the receiver to its original value.
     *
     * @since 1.0
     */
    public void revert() {
        if(myComponentIsMutable) {
            ((Mutable) myComponent).revert();
            fireMutableChanged();
        }
    }

    /**
     * Commit changes made.
     *
     * @since 1.5
     */
    public void commit() {
        if(myComponentIsMutable) {
            ((Mutable) myComponent).commit();
            fireMutableChanged();
        }
    }

    /**
     * Forget the original value.
     *
     * @since 1.0
     */
    public void forgetOriginalValue() {
        if(myComponentIsMutable)
            ((Mutable) myComponent).forgetOriginalValue();
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
        MutableChangeEvent fooEvent = new MutableChangeEvent(this, isChanged());
        UndoUtilities.dispatchMutableChangeEvent(fooEvent, listenerList);
    }

    /**
     * Invoked whenever a <code>MutableChangeEvent</code> occurs.
     *
     * @param e the <code>MutableChangeEvent</code> having led to the invocation
     * of this method
     *
     * @since 1.2
     */
    public void mutableStateChanged(MutableChangeEvent e) {
        // if the source of the event was my child component, I have changed
        // my canonical state myself, so an appropriate event is fired.
        if(e.getSource().equals(myComponent))
            fireMutableChanged();
    }

    /**
     * Return the name of the <code>UndoHandler</code> widget responsible for
     * handling Undo for this widget.
     *
     * @since 1.3
     */
    public String getResponsibleUndoHandler() {
        if(myComponentIsMutable)
            return ((Mutable) myComponent).getResponsibleUndoHandler();
        else
            return null;
    }

    /**
     * Set the name of the <code>UndoHandler</code> widget responsible for
     * handling Undo for this widget.
     *
     * @param s the name of the new <code>UndoHandler</code>
     *
     * @since 1.3
     */
    public void setResponsibleUndoHandler(String s) {
        if(myComponentIsMutable)
            ((Mutable) myComponent).setResponsibleUndoHandler(s);
    }

}