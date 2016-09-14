/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.widgets;

/**
 * <code>UndoHandler</code> is a new interface that combines the former <code>UndoManager</code> and <code>UndoMediator</code> interfaces into one. It basically
 * has the same functionality, although -- of course -- the interaction between those two interfaces has been removed.#
 *
 * The purpose of this interface is to enable widgets to act as "Undo Handlers" (hence the name) for groups of other widgets. For this purpose, a widget
 * implementing this class must have a unique name; each <code>Mutable</code> widget knows the name of it's responsible <code>UndoHandler</code> and can use
 * this to fire <code>MutableChangeEvent</code>s to it's <code>UndoHandler</code>, which -- in turn -- can use the information contained therein to act
 * accordingly.
 *
 * <em>Hints:</em>
 * <ul>
 * <li>When reverting a radio button, all buttons in this button's buttongroup should be reverted. Unfortunately, the <code>ButtonModel</code> interface does
 * not provide a method for finding out which group a button belongs to; the <code>DefaultButtonModel</code>, though, does.
 *
 * @author chs@baltic-online.de
 * @author es@baltic-online.de
 *
 * @version 1.2
 * @see Mutable
 * @see MutableChangeListener
 * @see MutableChangeEvent
 */
public interface UndoHandler extends MutableChangeListener {

    /**
     * Commit all changes made to any controlled widget.
     *
     * @since 1.2
     */
    public void commit();


    /**
     * Return the next candidate for Undo, and remove it from the list of candidates.
     *
     * @return the next candidate for Undo, null if no such candidate is present
     *
     * @since 1.0
     * @see Mutable
     */
    public Mutable getNextUndoCandidate();


    /**
     * Return the undo backend, i.e., the object responsible for handling widgets awaiting possible undo (Undo candidates).
     *
     * @since 1.0
     */
    public UndoBackend< Mutable > getUndoBackend();


    /**
     * Return whether Undo is currently possible.
     *
     * @since 1.0
     */
    public boolean getUndoEnabled();


    /**
     * Return whether there are any candidates for Undo left currently.
     *
     * @since 1.0
     */
    public boolean hasUndoCandidates();


    /**
     * Return the number of candidates for Undo currently remaining.
     *
     * @since 1.0
     */
    public int numberOfUndoCandidates();


    /**
     * Peek at the next candidate for Undo without actually removing it from the list.
     *
     * @return the next candidate for Undo, or null if no such candidate is present.
     *
     * @since 1.0
     * @see Mutable
     */
    public Mutable peekNextUndoCandidate();


    /**
     * Register a <code>Mutable</code> object as being a candidate for undoing. What this means specifically is left to the implementing class; usually, one'll
     * want to move the object to the top of an <code>UndoStack</code> of objects awaiting possible undoing, but that's just one thing that can be done, and
     * more (or less) elaborate setups are certainly possible.
     *
     * @param mutable
     *            the <code>Mutable</code> object to register
     *
     * @since 1.0
     * @see UndoStack
     * @see Mutable
     */
    public void registerUndoCandidate( Mutable mutable );


    /**
     * Set the undo backend, i.e., the object reponsible for handling widgets awaiting possible undo (Undo candidates). This method may only be called if there
     * are currently no widgets awaiting undo.
     *
     * @param b
     *            the new undo backend, which must be an instance of <code>UndoBackend</code>.
     * @throws CurrentlyProhibitedException
     *             if invoked while there still are Undo candidates.
     *
     * @since 1.0
     */
    public void setUndoBackend( UndoBackend< Mutable > b ) throws CurrentlyProhibitedException;


    /**
     * Set whether Undo is currently possible based on whether there are currently any <code>Mutable</code> objects registered for Undo or not. More precisely,
     * Undo will be possible iff there are any such objects present currently.
     *
     * @since 1.0
     */
    public void setUndoEnabled();


    /**
     * Set whether Undo is currently possible.
     *
     * @param enabled
     *            whether Undo should be possible from now on.
     *
     * @since 1.0
     */
    public void setUndoEnabled( boolean enabled );


    /**
     * Undo a change made.
     *
     * @since 1.1
     */
    public void undo();


    /**
     * Undo all changes made to all the widgets associated with this <code>UndoHandler</code>.
     *
     * @since 1.1
     */
    public void undoAll();


    /**
     * Unregister a <code>Mutable</code> object as being a candidate for undoing. See <code>registerUndoCandidate</code> above for more.
     *
     * @param mutable
     *            the <code>Mutable</code> object to unregister
     *
     * @since 1.0
     * @see Mutable
     */
    public void unregisterUndoCandidate( Mutable mutable );

}