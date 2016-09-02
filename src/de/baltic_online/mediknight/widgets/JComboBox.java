/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.widgets;

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
 * A specialized <code>JComboBox</code> for Mediknight that implements the <code>Mutable</code> interface.
 *
 * @author chs@baltic-online.de
 * @author es@baltic-online.de
 * @version 1.6
 *
 */
public class JComboBox< E > extends javax.swing.JComboBox< E > implements Mutable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private UndoHandler       undoHandler      = null;
    private String	    undoHandlerName  = null;

    /**
     * the original value of the <code>JComboBox</code>. When set to null, a subsequent call to <code>setItem</code> will set this attribute as well.
     */
    protected Object	  originalItem     = null;

    /**
     * a list of those interested in receiving <code>MutableChangeEvent</code>s from this instance.
     */
    EventListenerList	 listenerList     = new EventListenerList();


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
     * Creates a <code>JComboBox</code> that takes its items from an existing <code>ComboBoxModel</code>.
     *
     * @param aModel
     *            the <code>ComboBoxModel</code> to use
     *
     * @since 1.0
     */
    public JComboBox( final ComboBoxModel< E > aModel ) {
	super( aModel );
	initialize();
    }


    /**
     * Crates a <code>JComboBox</code> that contains the elements in the specified array.
     *
     * @param items
     *            the array to use
     *
     * @since 1.0
     */
    public JComboBox( final E[] items ) {
	super( items );
	initialize();
    }


    /**
     * Creates a <code>JComboBox/code> that contains the elements in the
     * specified Vector.
     *
     * @param items
     *            the vector to use
     *
     * @since 1.1
     */
    public JComboBox( final Vector< E > items ) {
	super( items );
	initialize();
    }


    /**
     * Register an object to receive <code>MutableChangeEvent</code>s from us in the future.
     *
     * @since 1.2
     */
    @Override
    public void addMutableChangeListener( final MutableChangeListener l ) {
	listenerList.add( MutableChangeListener.class, l );
    }


    /**
     * Commit changes made.
     *
     * @since 1.5
     */
    @Override
    public void commit() {
	setSelectedItem( getOriginalItem() );
    }


    /**
     * Notify all listeners that have registered interest for notification on this event type. The event instance is lazily created using the parameters passed
     * into the fire method.
     * <p>
     * This code was copied from the documentation for the class <code>EventListenerList</code>.
     *
     * @since 1.2
     */
    @Override
    public void fireMutableChanged() {
	if( undoHandler == null ) {
	    undoHandler = UndoUtilities.findUndoHandler( this );
	    addMutableChangeListener( undoHandler );
	}
	final MutableChangeEvent fooEvent = new MutableChangeEvent( this, isChanged() );
	UndoUtilities.dispatchMutableChangeEvent( fooEvent, listenerList );
    }


    /**
     * Forget the original value. The next call to <code>SetItem</code> will set the original value as well as the current one.
     *
     * @see #originalItem
     * @since 1.0
     */
    @Override
    public void forgetOriginalValue() {
	setOriginalItem( null );
	updateBorder();
    }


    /**
     * Return the edited item
     *
     * @since 1.0
     */
    public Object getItem() {
	return getItemAt( getSelectedIndex() );
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
     * Return the name of the <code>UndoHandler</code> object responsible for handling Undo for this widget.
     *
     * @since 1.3
     */
    @Override
    public String getResponsibleUndoHandler() {
	return undoHandlerName;
    }


    // Implementation of Mutable

    /**
     * Additional initialization. Subclasses that override this method should always call super.initialize().
     *
     * @since 1.0
     */
    protected void initialize() {
	setBorder( new UnderlineableBorder( getBorder(), getForeground() ) );
	getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {

	    @Override
	    public void keyReleased( final KeyEvent e ) {
		updateBorder();
		fireMutableChanged();
	    }
	} );
	addItemListener( new ItemListener() {

	    @Override
	    public void itemStateChanged( final ItemEvent e ) {
		updateBorder();
		fireMutableChanged();
	    }
	} );

    }


    /**
     * return whether the <code>JComboBox</code>'s value has been changed.
     *
     * @since 1.0
     */
    @Override
    public boolean isChanged() {
	return getOriginalItem() == null ? false : !getOriginalItem().equals( getItem() );
    }


    /**
     * Deregister an object to receive <code>MutableChangeEvent</code>s from us in the future.
     *
     * @since 1.2
     */
    @Override
    public void removeMutableChangeListener( final MutableChangeListener l ) {
	listenerList.remove( MutableChangeListener.class, l );
    }


    /**
     * Revert the item being edited to the original value
     *
     * @since 1.0
     */
    @Override
    public void revert() {
	setSelectedItem( getOriginalItem() );
	fireMutableChanged();
    }


    /**
     * Set the original item
     *
     * @param item
     *            the new original item
     *
     * @since 1.0
     */
    public void setOriginalItem( final Object item ) {
	originalItem = item;
    }


    /**
     * Set the name of the <code>UndoHandler</code> widget responsible for handling Undo for this widget.
     *
     * @param s
     *            the name of the new <code>UndoHandler</code>
     *
     * @since 1.4
     */
    @Override
    public void setResponsibleUndoHandler( final String s ) {
	if( !isChanged() ) {
	    if( undoHandler != null ) {
		removeMutableChangeListener( undoHandler );
	    }

	    undoHandlerName = s;
	    undoHandler = null;
	}
    }


    /**
     * Selects the item at index <code>anIndex</code>.
     *
     * @param anIndex
     *            an integer specifying the list item to select, where 0 specifies the first item in the list
     * @exception IllegalArgumentException
     *                if <code>anIndex</code> < -1 or <code>anIndex</code> is greater than or equal to size
     */
    @Override
    public void setSelectedIndex( final int anIndex ) {
	setSelectedItem( getItemAt( anIndex ) );
    }


    /**
     * Set the item that should be edited.
     *
     * @param item
     *            the item that should be edited
     *
     * @since 1.0
     * @see #originalItem
     */
    @Override
    public void setSelectedItem( final Object item ) {
	super.setSelectedItem( item );
	if( originalItem == null ) {
	    setOriginalItem( item );
	} else if( isChanged() ) {
	    fireMutableChanged();
	}
    }


    /**
     * update the <code>JComboBox</code>'s border, if it is an instance of <code>UnderlineableBorder</code>
     *
     * @see UnderlineableBorder
     * @since 1.0
     */
    public void updateBorder() {
	final Border border = getBorder();
	if( border instanceof UnderlineableBorder ) {
	    ((UnderlineableBorder) border).setUnderlined( getOriginalItem() == null ? false : isChanged() );
	}

	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		repaint();
	    }
	} );
    }

}