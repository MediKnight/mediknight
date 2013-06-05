/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * This class implements a plugable <code>TextListener</code> to use with other
 * Swing components.
 * @see JTextField
 * @see JTextArea
 *
 * @autor sma@baltic-online.de
 * @version 1.0
 */
class TextListenerPlugin implements DocumentListener {

    private JTextComponent text;
    private TextListener listeners;

    /**
     * Creates a new TextListenerPlugin for the specified
     * <code>JTextComponent</code>.
     *
     * @param text the <code>JTextComponent</code> to plug this
     * <code>TextListenerPlugin</code> into
     *
     * @since 1.0
     */
    public TextListenerPlugin(JTextComponent text) {
        this.text = text;
    }

    /**
     * Add a <code>TextListener</code> to the <code>JTextComponent</code>
     * this <code>TextListenerPlugin</code> is plugged into.
     *
     * @param l the <code>TextListener</code> to add
     *
     * @since 1.0
     */
    public void addTextListener(TextListener l) {
        if (listeners == null)
            text.getDocument().addDocumentListener(this);
        listeners = AWTEventMulticaster.add(listeners, l);
    }

    /**
     * Remove a <code>TextListener</code> from the <code>JTextComponent</code>
     * this <code>TextListenerPlugin</code> is plugged into.
     *
     * @param l the <code>TextListener</code> to remove
     *
     * @since 1.0
     */
    public void removeTextListener(TextListener l) {
        listeners = AWTEventMulticaster.remove(listeners, l);
        if (listeners == null)
            text.getDocument().removeDocumentListener(this);
    }

    /**
     * notify listeners of a changed value
     *
     * @since 1.0
     */
    private void fireTextValueChanged() {
        listeners.textValueChanged(new TextEvent(text, 0));
    }

    // Implementation of DocumentListener

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the <code>DocumentEvent</code> that caused the call of this
     * method
     *
     * @since 1.0
     */
    public void insertUpdate(DocumentEvent e) {
        fireTextValueChanged();
    }

    /**
     * Gives notification that there was an insert into the document.
     *
     * @param e the <code>DocumentEvent</code> that caused the call of this
     * method
     *
     * @since 1.0
     */
    public void removeUpdate(DocumentEvent e) {
        fireTextValueChanged();
    }

    /**
     * Gives notification that a portion of the document has been removed.
     *
     * @param e the <code>DocumentEvent</code> that caused the call of this
     * method
     *
     * @since 1.0
     */
    public void changedUpdate(DocumentEvent e) {
        fireTextValueChanged();
    }
}