/*
 * @(#)$Id$
 *
 * (C)2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.util.EventListener;

/**
 * An interface to be implemented by those who wish to be notified whenever
 * a widget implementing the <code>Mutable</code> interface changes its
 * canonical state.
 *
 * @author chs@baltic-online.de
 *
 * @version 1.0
 */
public interface MutableChangeListener extends EventListener {

    /**
     * Invoked whenever a <code>MutableChangeEvent</code> occurs.
     *
     * @param e the <code>MutableChangeEvent</code> having led to the invocation
     * of this method
     *
     * @since 1.0
     * @see MutableChangeEvent
     */
    public void mutableStateChanged(MutableChangeEvent e);

}