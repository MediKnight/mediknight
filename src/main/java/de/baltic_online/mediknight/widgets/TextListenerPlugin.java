/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight.widgets;

import java.awt.AWTEventMulticaster;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;


/**
 * This class implements a plugable <code>TextListener</code> to use with other Swing components.
 * 
 * @see JTextField
 * @see JTextArea
 *
 * @autor sma@baltic-online.de
 * @version 1.0
 */
class TextListenerPlugin implements DocumentListener {

    private final JTextComponent text;
    private TextListener	 listeners;


    /**
     * Creates a new TextListenerPlugin for the specified <code>JTextComponent</code>.
     *
     * @param text
     *            the <code>JTextComponent</code> to plug this <code>TextListenerPlugin</code> into
     *
     * @since 1.0
     */
    public TextListenerPlugin( final JTextComponent text ) {
	this.text = text;
    }


    /**
     * Add a <code>TextListener</code> to the <code>JTextComponent</code> this <code>TextListenerPlugin</code> is plugged into.
     *
     * @param l
     *            the <code>TextListener</code> to add
     *
     * @since 1.0
     */
    public void addTextListener( final TextListener l ) {
	if( listeners == null ) {
	    text.getDocument().addDocumentListener( this );
	}
	listeners = AWTEventMulticaster.add( listeners, l );
    }


    /**
     * Gives notification that a portion of the document has been removed.
     *
     * @param e
     *            the <code>DocumentEvent</code> that caused the call of this method
     *
     * @since 1.0
     */
    @Override
    public void changedUpdate( final DocumentEvent e ) {
	fireTextValueChanged();
    }


    /**
     * notify listeners of a changed value
     *
     * @since 1.0
     */
    private void fireTextValueChanged() {
	listeners.textValueChanged( new TextEvent( text, 0 ) );
    }


    // Implementation of DocumentListener

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e
     *            the <code>DocumentEvent</code> that caused the call of this method
     *
     * @since 1.0
     */
    @Override
    public void insertUpdate( final DocumentEvent e ) {
	fireTextValueChanged();
    }


    /**
     * Remove a <code>TextListener</code> from the <code>JTextComponent</code> this <code>TextListenerPlugin</code> is plugged into.
     *
     * @param l
     *            the <code>TextListener</code> to remove
     *
     * @since 1.0
     */
    public void removeTextListener( final TextListener l ) {
	listeners = AWTEventMulticaster.remove( listeners, l );
	if( listeners == null ) {
	    text.getDocument().removeDocumentListener( this );
	}
    }


    /**
     * Gives notification that there was an insert into the document.
     *
     * @param e
     *            the <code>DocumentEvent</code> that caused the call of this method
     *
     * @since 1.0
     */
    @Override
    public void removeUpdate( final DocumentEvent e ) {
	fireTextValueChanged();
    }
}