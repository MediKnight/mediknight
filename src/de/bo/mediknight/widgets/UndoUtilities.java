/*
 * @(#)$Id$
 *
 * (C)2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.util.Vector;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * <code>UndoUtilities</code> is a class collecting a number of (static)
 * utility methods related to the Undo stuff, similar to
 * <code>SwingUtilities</code>.
 *
 * @author chs@baltic-online.de
 *
 * @version 1.2
 */
public class UndoUtilities {

    /**
     * The default constructor for this class, which only assures that it
     * cannot be instantiated by throwing an exception stating the obvious.
     *
     * @since 1.0
     */
    public UndoUtilities() throws InstantiationException {
        throw new InstantiationException("UndoUtilities cannot be instantiated");
    }


    /**
     * Determines whether a given <code>Object</code> is a container or not.
     * NOTE: This does not necessarily have anything to with being an instance
     * of <code>Container</code>! More precisely, being an instance of
     * <code>Container</code> is a necessary but not sufficient prerequisite.
     *
     * @param c the <code>Object</code> to check
     *
     * @returns true iff the given <code>Component</code> is a container
     *
     * @since 1.0
     */
    public static boolean isContainer(Object c) {
        // if it's not even an awt container, it's not a container at all.
        if(!(c instanceof Container))
            return false;

        // if it's not an instance of Mutable, it can't handle revert(), so
        // it's a container. :)
        if(!(c instanceof Mutable))
            return true;

        // if it's an instance of a class that's typically used only to contain
        // other widgets and has no canonical state in the sense of the Mutable
        // interface, it is a container.
        if( (c instanceof JPanel) ||
            (c instanceof JScrollPane) ||
            (c instanceof JSplitPane) ||
            (c instanceof JViewport) ||
            (c instanceof JInternalFrame) ||
            (c instanceof JLayeredPane) ||
            (c instanceof JMenuBar) ||
            (c instanceof JPanel) ||
            (c instanceof JPopupMenu) ||
            (c instanceof JRootPane) ||
            (c instanceof JTabbedPane) ||
            (c instanceof JTable))
                return true;

        // if none of the above applies, it is not a container.
        return false;
    }

    /**
     * Find the <code>UndoHandler</code> responsible for this widget. The
     * search is done as follows:
     *
     * <ol>
     * <li>Request the name of the responsible <code>UndoHandler</code> from
     * the <code>Mutable</code>
     * <li>Try to find a widget of that name in the parent container of the
     * <code>Mutable</code>
     * <li>If no such widget is found, get the root component for the
     * <code>Mutable</code> object and perform a DFS search (well, some kind of
     * search at least) on the component tree.
     *
     * @param c the <code>Mutable</code> to search the <code>UndoHandler</code>
     * for
     * @returns the responsible <code>UndoHandler</code>, or <code>null</code>
     * if none is found
     *
     * @since 1.1
     * @see Mutable
     * @see UndoHandler
     */
    public static UndoHandler findUndoHandler(Mutable c) {
        String undoHandlerName = c.getResponsibleUndoHandler();
        Container parent = ((Component) c).getParent();

        if(containsComponent(parent, undoHandlerName))
            return (UndoHandler) getComponent(parent, undoHandlerName);

        Component rootComponent = SwingUtilities.getRoot((Component) c);
        if(rootComponent instanceof Container)
            return (UndoHandler) searchForComponent((Container) rootComponent, undoHandlerName);
        else
            if(rootComponent instanceof UndoHandler)
                return (UndoHandler) rootComponent;
            else
                return null;
    }

    /**
     * Returns all Mutable Components of the given container
     * and all sub containers.
     */
    public static Mutable[] getMutables(Container c) {
        Vector v = new Vector(20,10);
        addMutables(v,c);
        return (Mutable[])v.toArray(new Mutable[v.size()]);
    }

    private static void addMutables(Vector v,Container container) {
        Component[] comp = container.getComponents();
        for ( int i=0; i<comp.length; i++ ) {
            Component c = comp[i];
            if ( c instanceof Mutable ) {
                v.add(c);
            }
            if ( c instanceof Container ) {
                addMutables(v,(Container)c);
            }
        }
    }

    /**
     * Check whether a given container has a (direct) child widget of a given
     * name. If name is <code>null</code>, or the empty string,
     * <code>false</code> is returned without any further operation.
     *
     * @param c the container to inspect
     * @param name the name of the desired child widget
     *
     * @since 1.1
     */
    public static boolean containsComponent(Container c, String name) {
        // short-cut
        if((name == null) || ("".equals(name)))
            return false;

        // try to find the component by its name
        //System.out.print("Before call getComponents() "+c+" ...");
        Component[] components = c.getComponents();
        //System.out.println("... after call getComponents() ");
        for(int i = 0; i < components.length; i++)
            if(name.equals(components[i].getName()))
                return true;

        // nothing found, so there is no child of the given name
        return false;
    }

    /**
     * Return the (direct) child widget of the given name of the given
     * container, provided there is such a widget. NOTE: the semantics of this
     * method are not well-defined if the container contains more than one
     * child widget of the same name! In general, try to ALWAYS use
     * non-ambigous names. Similarly to <code>containsComponent</code> above,
     * return <code>null</code> if name is <code>null</code> or the empty
     * string, without any further operation.
     *
     * @param c the container to inspect
     * @param name the name of the desired child widget
     * @returns the child widget, or null if none is found.
     */
    public static Component getComponent(Container c, String name) {
        // short-cut
        if((name == null) || ("".equals(name)))
            return null;

        // try to find the component by its name. no call to containsComponent
        // is made to avoid searching the components twice.
        Component[] components = c.getComponents();
        for(int i = 0; i < components.length; i++)
            if(name.equals(components[i].getName()))
                return components[i];

        // nothing found, so there is no child of the given name
        return null;
    }

    /**
     * Search throught the tree of child components of a given
     * <code>Container</code> to see if it contains a child of a specified
     * name. NOTE: the same caution as for the semantics of
     * <code>getComponent(Container, String)</code> above applies! Also, just
     * like there, <code>null</code> is immediately returned if name is
     * <code>null</code> or the empty string.
     *
     * @param c the container through the component tree of which to search
     * @param name the component name to search for
     *
     * @since 1.1
     * @see Container
     */
    public static Component searchForComponent(Container c, String name) {
        // short-cut
        if((name == null) || ("".equals(name)))
            return null;

        // if we have a child of the specified name, return that and we're done.
        Component candidate = getComponent(c, name);
        if(candidate != null)
            return candidate;

        // otherwise, iterate through all components and recursively check all
        // instances of Container to see if they contain an appropriate child.
        // if so, return that one and we're done.
        Component[] components = c.getComponents();
        for(int i = 0; i < components.length; i++)
            if(components[i] instanceof Container) {
                Component can = searchForComponent((Container) components[i], name);
                if(can != null)
                    return can;
            }

        // if no child of the specified name was found at all, return null and
        // we're done.
        return null;
    }

    /**
     * dispatch a <code>MutableChangeEvent</code> to a given set of listeners.
     * Note: this code is based on the fireFooXXX() method in the example given
     * in the documentation for the <code>EventListenerList</code> class. This
     * method used to be in its own class
     * (<code>MutableChangeEventMultiCaster</code>), but the advent of the
     * <code>UndoUtilities</code> class has changed things. Ah, well.
     *
     * @param e the <code>MutableChangeEvent</code> to dispatch
     * @param listenerList the list of listeners intended to receive this event.
     *
     * @since 1.2
     * @see javax.swing.event.EventListenerList
     */
    public static void dispatchMutableChangeEvent(MutableChangeEvent e, EventListenerList listenerList) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i >= 0; i -= 2)
            if (listeners[i] == MutableChangeListener.class)
                ((MutableChangeListener) listeners[i + 1]).mutableStateChanged(e);
    }
}