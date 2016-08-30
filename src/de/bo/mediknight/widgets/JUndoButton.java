/*
 * @(#)$Id$
 *
 * (C)2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.SwingUtilities;


/**
 * A specialized <code>JButton</code> that handles Undo requests from the user.
 *
 * @author chs@baltic-online.de
 *
 * @version 1.4
 */

public class JUndoButton extends de.bo.mediknight.widgets.JButton implements UndoHandler {

    /**
     *
     */
    private static final long	serialVersionUID = 1L;

    /**
     * The backend to be user for the widgets available for undoing.
     */
    protected UndoBackend< Mutable > undoBackend      = new UndoStack< Mutable >();


    /**
     * Create a new <code>JUndoButton</code> with no set text or icon.
     *
     * @since 1.0
     */
    public JUndoButton() {
	super();
	setEnabled( false );
    }


    /**
     * Create a new <codeJUndoButton</code> where properties are taken from the <code>Action</code> supplied.
     *
     * @param a
     *            the <code>Action</code> to use
     *
     * @since 1.0
     */
    public JUndoButton( final Action a ) {
	super( a );
	setEnabled( false );
    }


    /**
     * reate a new <code>JUndoButon</code> with an icon.
     *
     * @param icon
     *            the <code>Icon</code> image to display on the button.
     *
     * @since 1.0
     */
    public JUndoButton( final Icon icon ) {
	super( icon );
	setEnabled( false );
    }


    /**
     * Create a new <code>JUndoButton</code> with text.
     *
     * @param text
     *            the text of the button
     *
     * @since 1.0
     */
    public JUndoButton( final String text ) {
	super( text );
	setEnabled( false );
    }


    /**
     * Create a new <code>JUndoButton</code> with inital text and an icon.
     *
     * @param text
     *            the text of the button
     * @param icon
     *            the <code>Icon</code> image to display on the button
     *
     * @since 1.0
     */
    public JUndoButton( final String text, final Icon icon ) {
	super( text, icon );
    }


    /**
     * Commit all changes made.
     *
     * @since 1.5
     */
    @Override
    public void commit() {
	while( peekNextUndoCandidate() != null ) {
	    getNextUndoCandidate().commit();
	}
    }


    // --- Implementation of <code>UndoHandler</code> ---

    /**
     * Called when an action is performed on the button (i.e., when it is clicked).
     *
     * @since 1.1
     */
    @Override
    public void fireActionPerformed( final ActionEvent e ) {
	super.fireActionPerformed( e );
	undo();
    }


    /**
     * Return the next candidate for Undo, and remove it from the list of candidates. Implements <code>getNextUndoCandidate</code> from the
     * <code>UndoHandler</code> interface.
     *
     * @return the next candidate for Undo.
     *
     * @since 1.3
     * @see UndoHandler
     * @see Mutable
     */
    @Override
    public Mutable getNextUndoCandidate() {
	if( hasUndoCandidates() ) {
	    return undoBackend.getNext();
	} else {
	    return null;
	}
    }


    /**
     * Return the undo backend, i.e., the object responsible for handling widgets awaiting possible undo (Undo candidates). Required by the
     * <code>UndoHandler</code> interface.
     *
     * @since 1.3
     */
    @Override
    public UndoBackend< Mutable > getUndoBackend() {
	return undoBackend;
    }


    /**
     * Return whether Undo requests are currently ok or not. Required by the <code>UndoHandler</code> interface.
     *
     * @since 1.3
     */
    @Override
    public boolean getUndoEnabled() {
	return hasUndoCandidates();
    }


    /**
     * Return whether there are any candidates for Undo currently remaining. Implements <code>hasUndoCandidates</code> from the <code>UndoHandler</code>
     * interface.
     *
     * @since 1.3
     * @see UndoHandler
     */
    @Override
    public boolean hasUndoCandidates() {
	return !undoBackend.isEmpty();
    }


    /**
     * Invoked whenever a <code>MutableChangeEvent</code> occurs. Required by the <code>MutableChangeListener</code> interface (the superinterface of
     * <code>UndoHandler</code>, which we implement).
     *
     * @param e
     *            the <code>MutableChangeEvent</code> having led to the invocation of this method
     *
     * @since 1.3
     * @see Mutable
     * @see MutableChangeEvent
     * @see MutableChangeListener
     * @see UndoHandler
     */
    @Override
    public void mutableStateChanged( final MutableChangeEvent e ) {
	// it's a MutableChangeEvent, so the source must be Mutable.
	final Mutable widget = (Mutable) e.getSource();

	if( e.hasChanged() ) {
	    // widget has changed its canonical state; move it to the top
	    // of the undo stack and enable the undo button
	    registerUndoCandidate( widget );
	} else {
	    // widget has not changed its canonical state; remove it from
	    // the undo stack and disable the undo button depending on
	    // whether the stack is now empty or not.
	    unregisterUndoCandidate( widget );
	}
	setUndoEnabled();
    }


    /**
     * Return the number of candidates for Undo currently remaining. Implements <code>numberOfUndoCandidates</code> from the <code>UndoHandler</code> interface.
     *
     * @since 1.3
     * @see UndoHandler
     */
    @Override
    public int numberOfUndoCandidates() {
	return undoBackend.getSize();
    }


    /**
     * Peek at the next candidate for Undo without actually removing it from the list of candidates. Implements <code>peekNextUndoCandidate</code> from the
     * <code>UndoHandler</code> interface.
     *
     * @return the next candidate for Undo.
     *
     * @since 1.3
     * @see UndoHandler
     * @see Mutable
     */
    @Override
    public Mutable peekNextUndoCandidate() {
	if( hasUndoCandidates() ) {
	    return undoBackend.peek();
	} else {
	    return null;
	}
    }


    /**
     * Register a <code>Mutable</code> object as being a candidate for undoing. Implements <code>registerUndoCandidate</code> from the <code>UndoHandler</code>
     * interface.
     *
     * @param mutable
     *            the <code>Mutable</code> object to register
     *
     * @since 1.3
     * @see UndoHandler
     * @see Mutable
     */
    @Override
    public void registerUndoCandidate( final Mutable mutable ) {
	unregisterUndoCandidate( mutable );
	if( mutable.isChanged() ) {
	    undoBackend.toTop( mutable );
	}
    }


    /**
     * Set the undo backend, i.e., the object reponsible for handling widgets awaiting possible undo (Undo candidates). This method may only be called if there
     * are currently no widgets awaiting undo. Required by the <code>UndoHandler</code> interface.
     *
     * @param b
     *            the new undo backend, which must be an instance of <code>UndoBackend</code>.
     * @throws Exception
     *             if invoked while there still are Undo candidates.
     *
     * @since 1.3
     */
    @Override
    public synchronized void setUndoBackend( final UndoBackend< Mutable > b ) throws CurrentlyProhibitedException {
	if( hasUndoCandidates() ) {
	    throw new CurrentlyProhibitedException( "Call to setUndoBackend(UndoBackend) not allowed while Undo candidates are present" );
	}
	undoBackend = b;
    }


    /**
     * Set whether Undo is currently possible. Implements <code>undoSetEnabled</code> from the <code>UndoHandler</code> interface.
     *
     * @since 1.3
     * @see UndoHandler
     */
    @Override
    public void setUndoEnabled() {
	setUndoEnabled( hasUndoCandidates() );
    }


    /**
     * Set whether Undo is currently possible. Implements <code>undoSetEnabled</code> from the <code>UndoHandler</code> interface.
     *
     * @param enabled
     *            whether Undo should be possible from now on.
     *
     * @since 1.3
     * @see UndoHandler
     */
    @Override
    public void setUndoEnabled( final boolean enabled ) {
	this.setEnabled( enabled );
    }


    /**
     * Undo a change made.
     *
     * @since 1.4
     */
    @Override
    public void undo() {
	// get the next candidate for Undo, and undo it.
	undo( getNextUndoCandidate() );
    }


    /**
     * Undo changes made to the specified <code>Mutable</code> widget.
     *
     * @param widget
     *            the widget to undo
     *
     * @since 1.4
     */
    protected void undo( final Mutable widget ) {
	if( widget != null ) {
	    if( !(widget instanceof de.bo.mediknight.widgets.JRadioButton) ) {
		// if it's not a radio button, simply revert it.
		widget.revert();
	    } else {
		// if it *is* a radio button, find out which button group it
		// belongs to and revert all buttons in that group.
		final ButtonModel buttonModel = ((JRadioButton) widget).getModel();
		if( buttonModel instanceof DefaultButtonModel ) {
		    final ButtonGroup buttonGroup = ((DefaultButtonModel) buttonModel).getGroup();
		    final Enumeration< AbstractButton > buttons = buttonGroup.getElements();
		    while( buttons.hasMoreElements() ) {
			((Mutable) buttons.nextElement()).revert();
		    }
		}
	    }
	    // if the widget's a Component (which it should probably always
	    // be), also have it request the focus later on.
	    if( widget instanceof Component ) {
		SwingUtilities.invokeLater( new Runnable() {

		    @Override
		    public void run() {
			((Component) widget).requestFocus();
		    }
		} );
	    }
	}

	// en-/disable undo button depending on whether there's still
	// undoable changes left or not.
	setUndoEnabled();
    }


    /**
     * Undo all changes made to all the widgets associated with this <code>JUndoButton</code>.
     *
     * @since 1.4
     */
    @Override
    public void undoAll() {
	while( peekNextUndoCandidate() != null ) {
	    undo( getNextUndoCandidate() );
	}
    }


    // --- protected methods ---

    /**
     * Unregister a <code>Mutable</code> object as being a candidate for undoing. Implements <code>unregisterUndoCandidate</code> from the
     * <code>UndoHandler</code> interface.
     *
     * @param mutable
     *            the <code>Mutable</code> object to unregister
     *
     * @since 1.3
     * @see UndoHandler
     * @see Mutable
     */
    @Override
    public void unregisterUndoCandidate( final Mutable mutable ) {
	if( mutable instanceof de.bo.mediknight.widgets.JRadioButton ) {
	    final ButtonModel buttonModel = ((JRadioButton) mutable).getModel();
	    if( buttonModel instanceof DefaultButtonModel ) {
		final ButtonGroup buttonGroup = ((DefaultButtonModel) buttonModel).getGroup();
		final Enumeration< AbstractButton > buttons = buttonGroup.getElements();
		while( buttons.hasMoreElements() ) {
		    undoBackend.remove( (Mutable) buttons.nextElement() );
		}
	    }
	} else {
	    undoBackend.remove( mutable );
	}
    }

}