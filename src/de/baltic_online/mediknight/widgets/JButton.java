/*
 * @(#)$Id$
 *
 * (C)2001 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.widgets;

import javax.swing.Action;
import javax.swing.Icon;


/**
 * A general base class for all of Mediknight's specialized <code>JButton</code> s. All extensions of the "standard" <code>JButton</code> that are supposed to
 * affect all of our own <code>JButton</code>s should go in here. Note: for now, there's not much in here (our <code>JButtons</code> really aren't that
 * different), but this class does act as a base class for <code>JUndoButton</code> at least.
 *
 * @author chs@baltic-online.de
 *
 * @version 1.0
 * @see javax.swing.JButton
 */

public class JButton extends javax.swing.JButton {

    private static final long serialVersionUID = 1L;


    public JButton() {
	super();
    }


    public JButton( final Action a ) {
	super( a );
    }


    public JButton( final Icon icon ) {
	super( icon );
    }


    public JButton( final String text ) {
	super( text );
    }


    public JButton( final String text, final Icon icon ) {
	super( text, icon );
    }
}