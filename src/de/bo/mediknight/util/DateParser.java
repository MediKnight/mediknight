/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.util;

import java.text.ParseException;
import java.util.Date;

/**
 * Das Interface <code>DateParser</code> hat die Aufgabe, einen String in ein
 * Datum zu wandeln.
 *
 * @author Jan Bernhardt
 * @version $Id$
 */
public interface DateParser {

    /**
     * Wandelt einen String in ein Datum.
     *
     * @return <code>null</code>, wenn das Format des Datums nicht erkannt
     * 		wurde, ein entsprechendes </code>Date<code>-Objekt sonst
     */
    public Date parse(String s) throws ParseException;
}
