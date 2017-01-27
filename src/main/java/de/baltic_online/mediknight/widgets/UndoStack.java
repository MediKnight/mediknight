/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.widgets;

import java.util.EmptyStackException;
import java.util.Stack;


/**
 * UndoStack - an extension of the <code>java.util.Stack<code> class that can
 * be used to implement a stack of "undoable" widgets in our own
 * <code>JPanel</code> (and probably for other things as well).
 *
 * @author chs@baltic-online.de
 *
 * @version 1.3
 * @see UndoBackend
 * @see java.util.Stack
 * @see main.java.de.baltic_online.mediknight.widgets.JPanel
 */
public class UndoStack< E > extends Stack< E > implements UndoBackend< E > {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


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
    // by our superclass, so we don't need to define them here.

    /**
     * Return the topmost undo candidate (i.e., the one most recently added). Required by the <code>UndoBackend</code> interface.
     *
     * @since 1.1
     */
    @Override
    public E getNext() {
	return pop();
    }


    /**
     * Return the number of current undo candidates. Required by the <code>UndoBackend</code> interface.
     *
     * @since 1.1
     */
    @Override
    public int getSize() {
	return size();
    }


    /**
     * Return whether there are any candidates for undo left. Required by the <code>UndoBackend</code> interface.
     *
     * @since 1.1
     */
    @Override
    public boolean isEmpty() {
	return empty();
    }


    /**
     * Move an element in the stack to the top, inserting it if necessary, and return whether or not the object was already present in the stack.
     *
     * @param o
     *            the object to be moved.
     * @return true iff the object to be moved was already present in the stack
     *
     * @since 1.0
     */
    @Override
    public synchronized boolean toTop( final E o ) {
	try {
	    // shortcut: if the object is already on top of the stack, there's
	    // no
	    // need to do anything besides return true.
	    if( peek().equals( o ) ) {
		return true;
	    }
	} catch( final EmptyStackException e ) {
	} catch( final NullPointerException e ) {
	}

	// remember whether the object already was contained in the UndoStack.
	final boolean contained = contains( o );

	// remove the object if necessary and push it to the top of the stack.
	if( contained ) {
	    remove( o );
	}
	push( o );

	// return whether the object was already contained in the stack.
	return contained;
    }
}