package de.bo.mediknight.util;

import java.io.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

 /**
  * Simple Tool Class for XML handling.
  * <p>
  * This tools may exists in other class/packages but now they are here.
  */
public class XMLTool
{
    private final static String specChars = "&<>\"\'";
    private final static String[] replacement = new String[] {
        "&amp;", "&lt;", "&gt;", "&quot;", "&apos;"
    };

    /**
     * Converts a "ordinary" String source in an ISO-8859-* XML source, e.g.
     * modifying only ISO controls and <tt>&amp;&lt;&gt;&quot;&apos;</tt>
     * characters and leaving umlauts as is.
     */
    public static String toXMLString(String text) {
        int n = text.length();
        StringBuffer sb = new StringBuffer(n*2);

        for ( int i=0; i<n; i++ ) {
            char c = text.charAt(i);
            int pos = specChars.indexOf(c);
            if ( pos >= 0 )
                sb.append(replacement[pos]);
            else {
                boolean big = ((int)c & 0xFF00) != 0;
                if ( Character.isISOControl(c) || big ) {
                    int digits = big ? 4 : 2;
                    sb.append("&#x"+hexValue((int)c,digits)+";");
                }
                else
                    sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String hexValue(int n,int digits) {
        String s = Integer.toHexString(n).toUpperCase();
        int l = s.length();
        StringBuffer sb = new StringBuffer();
        for ( int i=0; i<digits-l; i++ )
            sb.append('0');
        sb.append(s);
        return sb.toString();
    }

    public static void parseString(String s, String font, StringBuffer buffer) {
        if (s.startsWith("<b>")) {
            s = s.substring(3, s.length() );
            buffer.append("<text content=\"");
            if (s.length() < 1) s = "&#160;";
            else s = XMLTool.toXMLString(s);
            buffer.append(s + "\" style=\"" + font + ",11pt,bold\"/>");
        } else if (s.startsWith("<i>")) {
            s = s.substring(3, s.length() );
            buffer.append("<text content=\"");
            if (s.length() < 1) s = "&#160;";
            else s = XMLTool.toXMLString(s);
            buffer.append(s + "\" style=\"" + font + ",11pt,kursiv\"/>");
        } else if ((s.startsWith("<bi>")) || (s.startsWith("<ib>"))) {
            s = s.substring(4, s.length() );
            buffer.append("<text content=\"");
            if (s.length() < 1) s = "&#160;";
            else s = XMLTool.toXMLString(s);
            buffer.append(s + "\" style=\"" + font + ",11pt,boldkursiv\"/>");
        } else {
            buffer.append("<text content=\"");
            if (s.length() < 1) s = "&#160;";
            else s = XMLTool.toXMLString(s);
            buffer.append(s + "\" style=\"" + font + ",11pt\"/>");
        }
    }
}
