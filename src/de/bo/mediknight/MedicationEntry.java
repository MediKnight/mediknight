/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight;

import java.util.*;
import java.io.*;

import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.*;

/**
 * This class is similar to BillEntry.
 *
 * @see BillEntry
 */
public class MedicationEntry {

    private String item;

    public MedicationEntry() {
        this("");
    }

    public MedicationEntry(String item) {
        this.item = item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    public Vector toVector() {
        Vector v = new Vector(1);
        v.add(item);
        return v;
    }

    public static void saveEntries(ObjectOwner owner,MedicationEntry[] entries) {
        int n = entries.length;
        Properties props = new Properties();
        for ( int i=0; i<n; i++ ) {
            String kp = getKeyPrefix(i);
            MedicationEntry me = entries[i];
            String item = me.getItem();
            props.setProperty(kp+"posten",item);
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(n*200);
            props.store(bos,"");
            owner.setObject(bos.toString());
        }
        catch (IOException x) { // should not be thrown on ByteArrayOutputStream
            x.printStackTrace();
        }
    }

    public static MedicationEntry[] loadEntries(ObjectOwner owner) {
        String o = owner.getObject();
        if ( o == null )
            return new MedicationEntry[0];

        Properties props = new Properties();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(o.getBytes());
            props.load(bais);
        }
        catch (IOException x) { // should not be thrown on ByteArrayInputStream
            x.printStackTrace();
        }

        LinkedList list = new LinkedList();
        for ( int n=0;; n++ ) {
            String kp = getKeyPrefix(n);
            String item = props.getProperty(kp+"posten");
            if ( item == null )
                break;

            MedicationEntry me = new MedicationEntry(item);
            list.add(me);
        }

        MedicationEntry[] entries = new MedicationEntry[list.size()];
        int i=0;
        for ( Iterator it=list.iterator(); it.hasNext(); i++ )
            entries[i] = (MedicationEntry)it.next();

        return entries;
    }

    private static String getKeyPrefix(int n) {
        return "me."+n+".";
    }
}