/*
 * @(#)$Id$
 *
 * (C)2000-2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight;

import java.util.*;
import java.io.*;
import java.text.NumberFormat;

import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.*;

/**
 * Representation of a bill entry.
 * <p>
 * Instances of this class are part of the <tt>Rechnung</tt> type in
 * the domain package but stored as an object (not in a related table)
 * so this class is not a part of the domain package itself.
 */
public class BillEntry {

    // We have an item an a count
    // (dont ask why count is of type double)
    private RechnungsPosten item;
    private double count;

    public BillEntry() {
        this(null,1.0);
    }

    public BillEntry(RechnungsPosten item,double count) {
        this.item = item;
        this.count = count;
    }

    public void setItem(RechnungsPosten item) {
        this.item = item;
    }

    public RechnungsPosten getItem() {
        return item;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getCount() {
        return count;
    }

    /**
     * This method returns a JTable representation (ok, not a very good place)
     */
    public Vector<String> toVector() {

        NumberFormat nf = MediknightUtilities.getNumberFormat();
        Vector<String> v = new Vector<String>(5);

        v.add(item.getGebueH());
        v.add(item.getText());
        v.add(item.getGOAE());
        v.add(nf.format(item.getPreis()));
        v.add(nf.format(count));

        return v;
    }

    /**
     * This method puts an array of bill entries to a given owner
     * (Rechnung or Rechnungsgruppe).
     * <p>
     * The array is stored as a property file. Each entry key starts with
     * "be.number." where number is the current array index. Each key is
     * appended by the identifier (german notation) for the current member.
     * If the owner is a "Rechnung" the text of the bill will be saved
     * in the properties too.
     * <p>
     * This method use the ByteArrayOutputStream to save the Properties
     * object.
     */
    public static void saveEntries(ObjectOwner owner,BillEntry[] entries) {

        int n = entries.length;

        // build properties ...
        Properties props = new Properties();
        for ( int i=0; i<n; i++ ) {
            String kp = getKeyPrefix(i);
            BillEntry be = entries[i];
            RechnungsPosten rp = be.getItem();
            props.setProperty(kp+"gebueh",rp.getGebueH());
            props.setProperty(kp+"goae",rp.getGOAE());
            props.setProperty(kp+"text",rp.getText());
            props.setProperty(kp+"preis",String.valueOf(rp.getPreis()));
            props.setProperty(kp+"anzahl",String.valueOf(be.count));
            props.setProperty(kp+"euro",String.valueOf(rp.isEuro()));
        }

        // create stream and save properties
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(n*200);
            props.store(bos,"");
            bos.flush();        // necessary?
            owner.setObject(bos.toString());
        }
        catch (IOException x) { // should not be thrown on ByteArrayOutputStream
            x.printStackTrace();
        }
    }

    /**
     * This method retrieves entries from Rechnung or Rechnungsgruppe.
     *
     * @see #saveEntries(Rechnung,BillEntry[])
     */
    public static BillEntry[] loadEntries(ObjectOwner owner) {
        String o = owner.getObject();
        if ( o == null )
            return new BillEntry[0];

        // make stream and read properties ...
        Properties props = new Properties();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(o.getBytes());
            props.load(bais);
        }
        catch (IOException x) { // should not be thrown on ByteArrayInputStream
            x.printStackTrace();
        }

        LinkedList<BillEntry> list = new LinkedList<BillEntry>();
        for ( int n=0;; n++ ) {
            String kp = getKeyPrefix(n);
            String s = props.getProperty(kp+"anzahl");

            // No more keys?
            if ( s == null )
                break;

            BillEntry be = new BillEntry();
            RechnungsPosten rp = new RechnungsPosten();

            // double defaults
            be.count = 1.0;
            double preis = 0.0;
            try {
                be.count = Double.parseDouble(s);
                preis = Double.parseDouble(props.getProperty(kp+"preis"));
            }
            catch (NumberFormatException x) { // ignore
            }

            rp.setGebueH(props.getProperty(kp+"gebueh"));
            rp.setGOAE(props.getProperty(kp+"goae"));
            rp.setText(props.getProperty(kp+"text"));
            rp.setPreis(preis);
            rp.setEuro(Boolean.valueOf(props.getProperty(kp+"euro")).booleanValue());

            be.item = rp;
            list.add(be);
        }

        // make array from list
        BillEntry[] entries = new BillEntry[list.size()];
        int i=0;
        for ( Iterator<BillEntry> it=list.iterator(); it.hasNext(); i++ )
            entries[i] = it.next();

        return entries;
    }

    private static String getKeyPrefix(int n) {
        return "be." + n + ".";
    }
}