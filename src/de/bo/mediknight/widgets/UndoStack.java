/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.util.*;

/**
 * UndoStack - an extension of the <code>java.util.Stack<code> class that can
 * be used to implement a stack of "undoable" widgets in our own
 * <code>JPanel</code> (and probably for other things as well).
 *
 * @author chs@baltic-online.de
 *
 * @version 1.2
 * @see UndoBackend
 * @see java.util.Stack
 * @see de.bo.mediknight.widgets.JPanel
 */
public class UndoStack extends Stack implements UndoBackend {

    /**
     * Creates a new instance of <code>UndoStack</code>.
     *
     * @since 1.0
     */
    public UndoStack() {
        super();
    }

    // --- Implementation of UndoBackend

    // NOTE: peek() and remove(Object) are already properly implemented
    //       by our superclass, so we don't need to define them here.

    /**
     * Return the topmost undo candidate (i.e., the one most recently added).
     * Required by the <code>UndoBackend</code> interface.
     *
     * @since 1.1
     */
    public Object getNext() {
        return pop();
    }

    /**
     * Return the number of current undo candidates. Required by the
     * <code>UndoBackend</code> interface.
     *
     * @since 1.1
     */
    public int getSize() {
        return size();
    }

    /**
     * Return whether there are any candidates for undo left. Required by the
     * <code>UndoBackend</code> interface.
     *
     * @since 1.1
     */
    public boolean isEmpty() {
        return empty();
    }

    /**
     * Move an element in the stack to the top, inserting it if necessary, and
     * return whether or not the object was already present in the stack.
     *
     * @param o the object to be moved.
     * @return true iff the object to be moved was already present in the stack
     *
     * @since 1.0
     */
    public synchronized boolean toTop(Object o) {
        try {
            // shortcut: if the object is already on top of the stack, there's no
            // need to do anything besides return true.
            if(peek().equals(o)) return true;
        } catch (EmptyStackException e) {
        } catch (NullPointerException e) {
        }

        // remember whether the object already was contained in the UndoStack.
        boolean contained = contains(o);

        // remove the object if necessary and push it to the top of the stack.
        if(contained) remove(o);
        push(o);

        // return whether the object was already contained in the stack.
        return contained;
    }
}