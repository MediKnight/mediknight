/*
 * @(#)$Id$
 *
 * (C)2001 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.widgets;

/**
 * Instances of this class represent events that are being fired when a <code>Mutable</code> widgets wishes to express that it has changed its canonical value
 * from its original value or reverted back to its original value after having changed its canonical value earlier.
 *
 * @author chs@baltic-online.de
 *
 * @version 1.0
 * @see Mutable
 */
public class MutableChangeEvent extends MediknightEvent {

    private static final long serialVersionUID = 1L;

    // whether the source associated with this MutableChangeEvent has changed
    // its canonical state
    protected boolean	 hasChanged;


    /**
     * Create a new <code>MutableChangeEvent</code> for the given source, indicating that the canonical state has changed
     *
     * @param source
     *            the source to create a <code>MutableChangeEvent</code> for
     *
     * @since 1.0
     */
    public MutableChangeEvent( final Object source ) {
	this( source, true );
    }


    /**
     * Create a new <code>MutableChangeEvent</code> for the given source.
     *
     * @param source
     *            the source to create a <code>MutableChangeEvent</code> for
     * @param hasChanged
     *            whether the canonical state of the source has changed
     *
     * @since 1.0
     */
    public MutableChangeEvent( final Object source, final boolean hasChanged ) {
	super( source );
	this.hasChanged = hasChanged;
    }


    /**
     * Return whether the source associated with this <code>MutableChangeEvent</code> has changed its canonical state.
     *
     * @since 1.0
     */
    public boolean hasChanged() {
	return hasChanged;
    }

}