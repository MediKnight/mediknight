/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.widgets;

/**
 * The <code>Mutable</code> interface should be implemented by all classes that wish to be manageable by MediKnight's own version of <code>JPanel</code>.
 *
 * More precisely, the <code>Mutable</code> interface describes and should be implemented by classes that have some sort of "canonical state" that can change
 * without further notice and that they wish to manage in a defined manner. To achieve this, the <code>Mutable</code> interface describes a number of methods to
 * be implemented by these classes for handling the canonical states of their instances, including methods to generate events announcing the state of change of
 * the instance's canonical state and dispatching those to registered listeners. For these methods, example code is given (which unfortunately cannot be
 * implemented here due to restrictions on inheritance in Java(tm)).
 *
 * @author chs@baltic-online.de
 * @author es@baltic-online.de
 * @version 1.5
 */
public interface Mutable {

    /**
     * Register an object to receive <code>MutableChangeEvent</code>s from us in the future. Example code (assuming that <code>listenerList</code> is an
     * instance variable of type <code>javax.swing.event.EventListenerList</code>):
     *
     * <pre>
     * 
     * public void addMutableChangeListener( MutableChangeListener l ) {
     *     listenerList.add( MutableChangeListener.class, l );
     * }
     * </pre>
     *
     * @since 1.2
     *
     * @see MutableChangeListener
     */
    public void addMutableChangeListener( MutableChangeListener l );


    /**
     * Commit changes made to this <code>Mutable</code> object (i.e., update the canonical state with the current state).
     *
     * @since 1.5
     */
    public void commit();


    /**
     * Notify all listeners that have registered interest for notification on this event type. Example code (assuming that <code>listenerList</code> is an
     * instance variable of type <code>javax.swing.event.EventListenerList)</code> and undoHandler is an instance variable of type <code>UndoHandler</code>):
     *
     * <pre>
     * 
     * public void fireMutableChanged() {
     *     if( undoHandler == null ) {
     * 	undoHandler = UndoUtilities.findUndoHandler( this );
     * 	addMutableChangeListener( undoHandler );
     *     }
     *     MutableChangeEvent fooEvent = new MutableChangeEvent( this, isChanged() );
     *     UndoUtilities.dispatchMutableChangeEvent( fooEvent, listenerList );
     * }
     * </pre>
     *
     * @since 1.2
     *
     * @see UndoUtilities
     * @see MutableChangeEvent
     */
    public void fireMutableChanged();


    /**
     * Forget the original value. Calling this method <emph>must</emph> insure that the next method call setting the value of this object will also set the
     * original value (this is most often achieved by setting the original value to null). This may seem like a strange and/or awkward thing to ask for, but it
     * really *does* make life easier - and especially, it prevents us from having to change lots and lots of code in Mediknight to make it work properly.
     *
     * @since 1.1
     */
    public void forgetOriginalValue();


    /**
     * Return the name of the <code>UndoHandler</code> widget responsible for handling Undo for this widget.
     *
     * @since 1.3
     */
    public String getResponsibleUndoHandler();


    /**
     * return whether the receiver's value has been changed.
     *
     * @since 1.0
     */
    public boolean isChanged();


    /**
     * Deregister an object to receive <code>MutableChangeEvent</code>s from us in the future. Example code (assuming that <code>listenerList</code> is an
     * instance variable of type <code>javax.swing.event.EventListenerList</code>):
     *
     * <pre>
     * 
     * public void removeMutableChangeListener( MutableChangeListener l ) {
     *     listenerList.remove( MutableChangeListener.class, l );
     * }
     * </pre>
     *
     * @since 1.2
     *
     * @see MutableChangeListener
     */
    public void removeMutableChangeListener( MutableChangeListener l );


    /**
     * Revert the receiver to its original value.
     *
     * @since 1.0
     */
    public void revert();


    /**
     * Set the name of the <code>UndoHandler</code> widget responsible for handling Undo for this widget. This method will return *WITHOUT* changing the
     * UndoHandler if the widget is still modified! It should probably generally be called only once for any given widget, anyway. Sample implementation:
     * 
     * <pre>
     * 
     * public void setResponsibleUndoHandler( String s ) {
     *     if( !isChanged() ) {
     * 	if( undoHandler != null )
     * 	    removeMutableChangeListener( undoHandler );
     * 
     * 	undoHandlerName = s;
     * 	undoHandler = null;
     *     }
     * }
     * </pre>
     *
     * @param s
     *            the name of the new <code>UndoHandler</code>
     *
     * @since 1.4
     */
    public void setResponsibleUndoHandler( String s );

}