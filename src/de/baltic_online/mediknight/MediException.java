/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight;

public class MediException extends Exception {

    private static final long serialVersionUID = -1726809782262596497L;


    public MediException() {
	super();
    }


    public MediException( final String msg ) {
	super( msg );
    }
}