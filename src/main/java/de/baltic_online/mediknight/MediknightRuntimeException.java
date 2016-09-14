/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package main.java.de.baltic_online.mediknight;

public class MediknightRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;


    public MediknightRuntimeException() {
	super();
    }


    public MediknightRuntimeException( final String s ) {
	super( s );
    }

}
