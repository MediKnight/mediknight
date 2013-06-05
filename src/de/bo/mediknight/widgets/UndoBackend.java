/*
 * @(#)$Id$
 *
 * (C)2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.util.Iterator;

/**
 * <code>UndoBackend</code> is an interface that needs to be implemented by
 * classes that wish to function as the 'backend' for an
 * <code>UndoManager</code>; that is, classes that are responsible for the
 * actual storing / retrieval of possible Undo candidates. The
 * <code>UndoStack</code> class is a sample implementation of this interface.
 *
 * @author chs@baltic-online.de
 *
 * @version 1.1
 */
public interface UndoBackend<E> {

    /**
     * Return the next Undo candidate without actually removing it.
     *
     * @since 1.0
     */
    public E peek();

    /**
     * Return the next Undo candidate and remove it.
     *
     * @since 1.0
     */
    public E getNext();

    /**
     * Remove the specified Undo candidate.
     *
     * @since 1.0
     */
    public boolean remove(E o);

    /**
     * Test whether there are any Undo candidates.
     *
     * @return true iff there are any Undo candidates
     *
     * @since 1.0
     */
    public boolean isEmpty();

    /**
     * Make the specified Undo candidate the most recent one, i.e., let it be
     * the one that will be returned by the next subsequent call to getNext()
     * (or peek(), for that matter).
     *
     * @since 1.0
     */
    public boolean toTop(E o);

    /**
     * Return the number of Undo candidates.
     *
     * @since 1.0
     */
    public int getSize();

}