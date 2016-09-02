/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.widgets;

/**
 * A sub-interface of <code>Mutable</code> that adds some special methods for text components.
 *
 * @author chs@baltic-online.de
 * @author es@baltic-online.de
 * @version 1.0
 * @see Mutable
 */
public interface MutableTextComponent extends Mutable {

    /**
     * get the original text.
     *
     * @since 1.0
     */
    public String getOriginalText();


    /**
     * set the original text.
     *
     * @param t
     *            the new original text.
     *
     * @since 1.0
     */
    public void setOriginalText( String t );

}