/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import de.bo.mediknight.MediknightRuntimeException;

public class CurrentlyProhibitedException extends MediknightRuntimeException {

    public CurrentlyProhibitedException() {
        super();
    }

    public CurrentlyProhibitedException(String s) {
        super(s);
    }
}