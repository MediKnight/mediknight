/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.widgets;

import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;


/**
 * An extension of javax.swing.JPanel that implements the <code>Mutable</code> interface and automatically manages child widgets doing so, too. The
 * <code>JPanel</code> implements <code>Mutable</code> so it is recognized by other <code>JPanel<code>s. The abilities to handle Undo (or, more
 * specifically, the implementation of the </code>UndoManager</code> and <code>UndoMediator</code> interfaces - have been removed again in version 1.10 in favor
 * of the new JUndoButton that implements <code>UndoHandler</code> .
 *
 * @author chs@baltic-online.de
 * @author es@baltic-online.de
 *
 * @version 1.10
 * @see Mutable
 */
public class JPanel extends javax.swing.JPanel implements Mutable {

    private static final long   serialVersionUID	  = 1L;

    /**
     * A <code>HashSet</code> containing those child widgets which implement the <code>Mutable</code> interface and can thus be managed by this
     * <code>JPanel</code>.
     */
    protected Set< Component >  controlledWidgets	 = Collections.synchronizedSet( new HashSet< Component >( 30, 0.8f ) );

    /**
     * A list of those interested in receiving <code>MutableChangeEvent</code>s from us.
     */
    protected EventListenerList mutableChangeListenerList = new EventListenerList();


    /**
     * Create a new <code>JPanel</code> using double buffering and a <code>FlowLayout</code>.
     *
     * @since 1.0
     */
    public JPanel() {
	super();
    }


    /**
     * Create a new <code>JPanel</code> with a <code>FlowLayout</code>, using the specified buffering strategy.
     *
     * @param isDoubleBuffered
     *            a boolean, true for double-buffering, which uses additional memory space to achieve fast, flicker-free updates
     *
     * @since 1.0
     */
    public JPanel( final boolean isDoubleBuffered ) {
	super( isDoubleBuffered );
    }


    /**
     * Create a new double-buffered <code>JPanel</code> with the specified layout manager.
     *
     * @param layout
     *            the <code>LayoutManager</code> to use
     *
     * @since 1.0
     */
    public JPanel( final LayoutManager layout ) {
	super( layout );
    }


    /**
     * Creates a new <code>JPanel</code> with the specified layout manager and buffering strategy.
     *
     * @param layout
     *            the <code>LayoutManager</code> to use
     * @param isDoubleBuffered
     *            a boolean, true for double-buffering, which uses additional memory space to achieve fast, flicker-free updates
     *
     * @since 1.0
     */
    public JPanel( final LayoutManager layout, final boolean isDoubleBuffered ) {
	super( layout, isDoubleBuffered );
    }


    /**
     * Adds the specified compoment to this <code>JPanel</code> at the specified index using the specified constraints.
     *
     * @param comp
     *            the component to be added.
     * @param constraints
     *            an object expressing layout constraints for this component
     * @param index
     *            the position in the container's list at which to insert the component, where -1 means insert at the end
     *
     * @since 1.1
     */
    @Override
    protected void addImpl( final Component comp, final Object constraints, final int index ) {
	super.addImpl( comp, constraints, index );
	register( comp );
    }


    /**
     * Register an object to receive <code>MutableChangeEvent</code>s from us in the future. Required by the <code>Mutable</code> interface.
     *
     * @since 1.3
     */
    @Override
    public void addMutableChangeListener( final MutableChangeListener l ) {
	mutableChangeListenerList.add( MutableChangeListener.class, l );
    }


    @Override
    public void commit() {
	synchronized( controlledWidgets ) {
	    final Iterator< Component > iter = controlledWidgets.iterator();
	    while( iter.hasNext() ) {
		((Mutable) iter.next()).commit();
	    }
	}
	updateBorder();
	fireMutableChanged();
    }


    // --- Implementation of <code>Mutable</code> ---

    /**
     * Notify all listeners that have registered interest for notification on this event type. Required by the <code>Mutable</code> interface.
     *
     * @since 1.3
     */
    @Override
    public void fireMutableChanged() {
	final MutableChangeEvent fooEvent = new MutableChangeEvent( this, isChanged() );
	UndoUtilities.dispatchMutableChangeEvent( fooEvent, mutableChangeListenerList );
    }


    /**
     * Forget the original values of all managed child widgets of this <code>JPanel</code>.
     *
     * @since 1.0
     */
    @Override
    public void forgetOriginalValue() {
	synchronized( controlledWidgets ) {
	    final Iterator< Component > iter = controlledWidgets.iterator();
	    while( iter.hasNext() ) {
		((Mutable) iter.next()).forgetOriginalValue();
	    }
	}
    }


    /**
     * Return the name of the <code>UndoHandler</code> object responsible for handling Undo for this widget.
     *
     * @since 1.9
     */
    @Override
    public String getResponsibleUndoHandler() {
	// always return null - JPanels have no undo handler themselves.
	return null;
    }


    /**
     * Check whether this <code>JPanel</code> has changed its state by checking all managed child widgets. Required by the <code>Mutable</code> interface.
     *
     * @since 1.0
     */
    @Override
    public boolean isChanged() {
	synchronized( controlledWidgets ) {
	    final Iterator< Component > i = controlledWidgets.iterator();
	    while( i.hasNext() ) {
		final Component c = i.next();
		if( ((Mutable) c).isChanged() ) {
		    return true;
		}
	    }
	}
	return false;
    }


    /**
     * Check to see if a new child widgets has any special functionality beyond that of any <code>Component</code>, and, if so, register it in an appropriate
     * way. Currently, this means that child widgets implementing <code>Mutable</code> will be recognized as such and added to a set of controlled widgets that
     * are queried when this instance of <code>
     * JPanel</code>'s own <code>Mutable</code> methods are invoked; also, child widgets that implement <code>UndoMediator</code> are recognized and handled
     * appropriately [1].
     *
     * @param c
     *            the component to register (maybe).
     *
     * @since 1.0
     */
    protected synchronized void register( final Component c ) {
	if( c instanceof Mutable ) {
	    controlledWidgets.add( c );
	}
    }


    /**
     * Deregister an object to receive <code>MutableChangeEvent</code>s from us in the future. Required by the <code>Mutable</code> interface.
     *
     * @since 1.3
     */
    @Override
    public void removeMutableChangeListener( final MutableChangeListener l ) {
	mutableChangeListenerList.remove( MutableChangeListener.class, l );
    }


    // --- Implementation of <code>UndoManager</code> ---
    // removed in version 1.10

    // --- Implementation of the <code>UndoMediator</code> interface ---
    // removed in version 1.10

    /**
     * Revert this <code>JPanel</code> to its original state by reverting all managed child widgets. Required by the <code>Mutable</code> interface.
     *
     * @since 1.0
     */
    @Override
    public void revert() {
	synchronized( controlledWidgets ) {
	    final Iterator< Component > iter = controlledWidgets.iterator();
	    while( iter.hasNext() ) {
		((Mutable) iter.next()).revert();
	    }
	}
	updateBorder();
	fireMutableChanged();
    }


    /**
     * Set the name of the <code>UndoHandler</code> widget responsible for handling Undo for this widget.
     *
     * @param s
     *            the name of the new <code>UndoHandler</code>
     *
     * @since 1.10
     */
    @Override
    public void setResponsibleUndoHandler( final String s ) {
	// do nothing - JPanels have no undo handlers themselves.
    }


    /**
     * update the <code>JPanel</code>'s border, if it is an instance of <code>UnderlineableBorder</code>
     *
     * @see UnderlineableBorder
     * @since 1.1
     */
    public void updateBorder() {
	final Border border = getBorder();
	if( border instanceof UnderlineableBorder ) {
	    ((UnderlineableBorder) border).setUnderlined( isChanged() );
	}

	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		repaint();
	    }
	} );
    }

}