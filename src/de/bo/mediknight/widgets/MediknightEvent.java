/*
 * @(#)$Id$
 *
 * (C)2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.util.EventObject;

/**
 * <code>MediknightEvent</code> - an abstract base class for all of the Event
 * classes used in Mediknight (not many so far, but hey...)
 *
 * @author chs@baltic-online.de
 *
 * @version 1.0
 */
public class MediknightEvent extends EventObject {

    /**
     * Construct a new <code>MediknightEvent</code> for the given source object.
     *
     * @param source the source object of the new <code>MediknightEvent</code>
     *
     * @since 1.0
     */
    public MediknightEvent(Object source) {
        super(source);
    }

}