/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import de.bo.mediknight.MediknightRuntimeException;


public class CurrentlyProhibitedException extends MediknightRuntimeException {

    private static final long serialVersionUID = 1L;


    public CurrentlyProhibitedException() {
	super();
    }


    public CurrentlyProhibitedException( final String s ) {
	super( s );
    }
}