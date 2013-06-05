package de.bo.mediknight.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Euro-Währungsklasse.
 *
 * Instanzen dieser Klasse sind Währungsbeträge in Euro oder in anderen
 * noch gültigen Währungen mit konstantem Umrechnungskurs zum Euro.
 * <p>
 * Der Betrag wird durch den Datentyp <code>long</code> bis auf ein
 * hundertstel Cent dargestellt. Dies ermöglicht immerhin die Darstellung
 * von über einer Billiarde (10^15) Euro.
 * <p>
 * Für Objekte dieser Klasse stehen die vier Grundrechnungsarten sowie
 * diverse Umrechnungsfunktionen bereit.
 */

public class CurrencyNumber extends Number implements Cloneable, Comparable<CurrencyNumber> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Währung in Euro (default).
     */
    public final static int EUR = 0;

    /**
     * Währung in DM.
     */
    public final static int DM = 1;

    /**
     * Währung in DEM.
     */
    public final static int DEM = 1;

    /**
     * Währung in Belgische Franken.
     */
    public final static int BEF = 2;

    /**
     * Währung in Finnmark.
     */
    public final static int FIM = 3;

    /**
     * Währung in Französiche Franken.
     */
    public final static int FRF = 4;

    /**
     * Währung in Irische Pfund.
     */
    public final static int IEP = 5;

    /**
     * Währung in Italienische Lira.
     */
    public final static int ITL = 6;

    /**
     * Währung in Luxemburgische Franc.
     */
    public final static int LUF = 7;

    /**
     * Währung in Niederländische Gulden.
     */
    public final static int NLG = 8;

    /**
     * Währung in Östereichische Schilling.
     */
    public final static int ATS = 9;

    /**
     * Währung in Portugisiche Escudo.
     */
    public final static int PTE = 10;

    /**
     * Währung in Spanische Peseta.
     */
    public final static int ESP = 11;

    private static String[] csymbols =
        {
            "\u20AC EUR EURO EU E",
            "DM DEM",
            "BEF BFRS BF BFR",
            "FIM FM FMK",
            "FRF F FF",
            "IEP IR£ IR IRP",
            "ITL L LIT",
            "LUF LFRS LFR LF",
            "NLG HFL FL",
            "ATS ÖS S",
            "PTE ESC",
            "ESP PTS" };

    private static double[] cfactor =
        {
            1.0,
            1.95583,
            40.339,
            5.94573,
            6.55957,
            0.787564,
            1936.27,
            40.339,
            2.20371,
            13.7603,
            200.487,
            166.386 };

    private static Locale[] locale = {
        //    Locale.US,
        Locale.GERMANY,
            Locale.GERMANY,
            new Locale("fr", "BE"),
            new Locale("fi", "FI"),
            Locale.FRANCE,
            new Locale("en", "IE"),
            Locale.ITALY,
            new Locale("fr", "LU"),
            new Locale("nl", "NL"),
            new Locale("de", "AT"),
            new Locale("pt", "PT"),
            new Locale("es", "ES")};

    private long value;
    private int currency;

    /**
     * Erzeugt Währungsbetrag von 0 in Euro.
     */
    public CurrencyNumber() {
        value = 0L;
        currency = EUR;
    }

    /**
     * Erzeugt Währungsbetrag von 0 in gegebener Währung.
     *
     * @param currency Währung
     */
    public CurrencyNumber(int currency) {
        value = 0L;
        this.currency = currency;
    }

    /**
     * Erzeugt Währungsbetrag interner Darstellung in Euro.
     *
     * @param value neuer Betrag (interne Darstellung)
     */
    public CurrencyNumber(long value) {
        this.value = value;
        currency = EUR;
    }

    /**
     * Erzeugt Währungsbetrag interner Darstellung in gegebener Währung.
     *
     * @param value neuer Betrag (interne Darstellung)
     * @param currency Währung
     */
    public CurrencyNumber(long value, int currency) {
        this.value = value;
        this.currency = currency;
    }

    /**
     * Erzeugt Währungsbetrag in Euro.
     *
     * @param v neuer Betrag
     */
    public CurrencyNumber(double v) {
        this(v, EUR);
    }

    /**
     * Erzeugt Währungsbetrag in gegebener Währung.
     *
     * @param v neuer Betrag
     * @param currency Währung
     */
    public CurrencyNumber(double v, int currency) {
	    value = (long) (v * 10000.0 + (v > 0 ? 0.5 : -0.5));
		this.currency = currency;
    }

    /**
     * Erzeugt Währungsbetrag in Euro.
     *
     * @param f neuer Betrag
     */
    public CurrencyNumber(float f) {
        this(f, EUR);
    }

    /**
     * Erzeugt Währungsbetrag in gegebener Währung.
     *
     * @param f neuer Betrag
     * @param currency Währung
     */
    public CurrencyNumber(float f, int currency) {
        this((double) f, currency);
    }

    /**
     *
     */
    public int getCurrency() {
        return currency;
    }

    /**
     * Währungsbeträge können kopiert werden.
     *
     * @return identische Kopie des Objekts
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException x) {
            throw new Error("Bad clone()-Method");
        }
    }

    public CurrencyNumber createClone() {
        return (CurrencyNumber) clone();
    }
    
    /**
     * 
     */
    public CurrencyNumber round(int precision) {
    	double dv = doubleValue() * (long) Math.pow(10, precision) + (doubleValue() > 0 ? 0.5 : -0.5);
    	double rv = (long) dv / Math.pow(10, precision);
    	value = (long) (rv * 10000);
    	return this;
    }

    /**
     * Konvertiert nach Euro und liefert neues Objekt zurück.
     *
     * @return äquivalenter Währungsbetrag in Euro
     */
    public CurrencyNumber toEuro() {
        return new CurrencyNumber((long) ((double) value / cfactor[currency] + (value > 0 ? 0.5 : -0.5)));
    }

    /**
     * Konvertiert Objekt nach Euro und ändert das Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber norm() {
        CurrencyNumber cn = toEuro();
        value = cn.value;
        currency = EUR;
        return this;
    }

    /**
     * Konvertiert in gegebene Währung und liefert neues Objekt zurück.
     *
     * @param gegebene Währung
     * @return äquivalenter Währungsbetrag in gegebener Währung
     */
    public CurrencyNumber toCurrency(int newCurrency) {
        double f =
            (currency == EUR)
                ? cfactor[newCurrency]
                : cfactor[newCurrency] / cfactor[currency];

        return new CurrencyNumber((long) ((double) value * f + (value > 0 ? 0.5 : -0.5)), newCurrency);
    }

    /**
     * Liefert negiertes Objekt zurück (läßt Objekt unverändert).
     *
     * @return negativer Währungsbetrag
     */
    public CurrencyNumber neg() {
        return new CurrencyNumber(-value, currency);
    }

    /**
     * Negiert Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber negate() {
        value = -value;
        return this;
    }

    /**
     * Addiert gegebenen Währungsbetrag zum Objekt.
     *
     * @param cn Währungsbetrag
     * @return <code>this</code>
     */
    public CurrencyNumber add(CurrencyNumber cn) {
        int oc = currency;
        CurrencyNumber ce = cn.toEuro();
        norm();
        value += ce.value;

        CurrencyNumber c = toCurrency(oc);
        value = c.value;
        currency = oc;

        return this;
    }

    /**
     * Subtrahiert gegebenen Währungsbetrag vom Objekt.
     *
     * @param cn Währungsbetrag
     * @return <code>this</code>
     */
    public CurrencyNumber sub(CurrencyNumber cn) {
        int oc = currency;
        CurrencyNumber ce = cn.toEuro();
        norm();
        value -= ce.value;

        CurrencyNumber c = toCurrency(oc);
        value = c.value;
        currency = oc;

        return this;
    }

    /**
     * Multipliziert <code>int</code> zum Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber mul(int x) {
        value *= (long) x;

        return this;
    }

    /**
     * Multipliziert <code>long</code> zum Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber mul(long x) {
        value *= x;

        return this;
    }

    /**
     * Multipliziert <code>double</code> zum Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber mul(double x) {
        double v = (double) value * x;
        value = (long) (v + (v > 0 ? 0.5 : -0.5));

        return this;
    }

    /**
     * Multipliziert <code>float</code> zum Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber mul(float x) {
        return mul((double) x);
    }

    /**
     * Dividiert <code>int</code> vom Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber div(int x) {
        value /= (long) x;

        return this;
    }

    /**
     * Dividiert <code>long</code> vom Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber div(long x) {
        value /= x;

        return this;
    }

    /**
     * Dividiert <code>double</code> vom Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber div(double x) {
        double v = (double) value / x;
        value = (long) (v + (v > 0 ? 0.5 : -0.5));

        return this;
    }

    /**
     * Dividiert <code>float</code> vom Objekt.
     *
     * @return <code>this</code>
     */
    public CurrencyNumber div(float x) {
        return div((double) x);
    }

    /**
     * Dividiert gegebenes vom aktuellen Objekt.
     *
     * @return <code>(double)toEuro().value / (double)cn.toEuro().value *
     * 10000.0</code>
     */
    public double div(CurrencyNumber cn) {
        return (double) toEuro().value / (double) cn.toEuro().value;
    }

    /**
     * Objektvergleich im Sinne von <code>Comparable</code>
     *
     * @param o zu vergleichender Betrag
     *
     * @see Comparable
     */
    public int compareTo(CurrencyNumber o) {
        long v1 = toEuro().value;
        long v2 = o.toEuro().value;

        if (v1 == v2)
            return 0;
        if (v1 < v2)
            return -1;
        return 1;
    }

    /**
     * Objektvergleich.
     *
     * @param o zu vergleichender Betrag
     *
     * @return <code>toEuro().value == ((CurrencyNumber)o).toEuro().value</code>
     */
    public boolean equals(Object o) {
        return toEuro().value == ((CurrencyNumber) o).toEuro().value;
    }

    /**
     * String-Darstellung des Währungsbetrags.
     *
     * Der Währungsbetrag wird bzgl. des Landes der zugrundeliegenden Währung
     * dargestellt.
     *
     * @return String-Darstellung des Währungsbetrags inklusive Währungssymbol
     *
     * @see #toString(Locale,boolean)
     */
    public String toString() {
        return toString(locale[currency], true);
    }

    /**
     * String-Darstellung des Währungsbetrags.
     *
     * Der Währungsbetrag wird bzgl. des gegebenen <code>locale</code> Objekts
     * dargestellt.
     *
     * @param locale angewendetes <code>locale</code> Objekt
     * @return String-Darstellung des Währungsbetrags inklusive Währungssymbol
     *
     * @see #toString(Locale,boolean)
     */
    public String toString(Locale locale) {
        return toString(locale, true);
    }

    /**
     * String-Darstellung des Währungsbetrags.
     *
     * Der Währungsbetrag wird bzgl. des gegebenen <code>locale</code> Objekts
     * dargestellt.
     *
     * @param locale angewendetes <code>locale</code> Objekt
     * @param showCurrencySymbol bestimmt, ob Währungssymbol angefügt
     * werden soll
     * @return String-Darstellung des Währungsbetrags
     */
    public String toString(Locale locale, boolean showCurrencySymbol) {
        DecimalFormat form =
            new DecimalFormat("#,###,###,###,##0.00", new DecimalFormatSymbols(locale));
        StringBuffer sb = new StringBuffer();
        sb.append(form.format(doubleValue()));
        if (showCurrencySymbol) {
            sb.append(" ");
            sb.append(getCurrencySymbol());
        }
        return sb.toString();
    }

    /**
     * Liefert Währungssymbol zur Währung des Objekts.
     *
     * @return Währungssymbol
     */
    public String getCurrencySymbol() {
        return getCurrencySymbol(currency);
    }

    /**
     * Liefert Währungssymbol zur angegebenen Währung.
     *
     * @param currency angegebene Währung
     * @return Währungssymbol
     */
    public static String getCurrencySymbol(int currency) {
        StringTokenizer st = new StringTokenizer(csymbols[currency]);
        return st.nextToken();
    }

    /**
     * Parsen des gegebenen Strings auf einen Währungsbetrag.
     *
     * Falls kein Währungssymbol dem Betrag angehängt ist, wird Euro
     * als Default genommen.
     *
     * @param s Betrag in String-Darstellung
     * @return Währungsbetrag
     * @exception IllegalArgumentException, wenn Eingabe nicht geparst
     * werden kann.
     */
    public static CurrencyNumber parse(String s) {
        return parse(s, EUR);
    }

    /**
     * Parsen des gegebenen Strings auf einen Währungsbetrag.
     *
     * @param s Betrag in String-Darstellung
     * @param defaultCurreny Default-Währung, falls kein Währungssymbol
     * dem Betrag angehängt ist
     * @return Währungsbetrag
     * @exception IllegalArgumentException, wenn Eingabe nicht geparst
     * werden kann.
     */
    public static CurrencyNumber parse(String s, int defaultCurrency) {
        // finde Position des Währungssymbols:
        int n = s.length();
        int cp = -1;
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c) || c == '\u20AC') {
                cp = i;
                break;
            }
        }

        // Wenn String mit Währungssymbol beginnt, ist Eingabe ungültig
        if (cp == 0)
            throw new IllegalArgumentException("invalid currency position");

        String snum, sc;

        if (cp > 0) {
            snum = s.substring(0, cp).trim();
            sc = s.substring(cp).trim();
        } else {
            snum = s.trim();
            sc = getCurrencySymbol(defaultCurrency);
        }

        if (snum.length() == 0)
            throw new IllegalArgumentException("zerolength amount");

        if (sc.length() == 0)
            sc = getCurrencySymbol(defaultCurrency);

        int currency = parseCurrencySymbol(sc);

        DecimalFormat form =
            new DecimalFormat(
                "#,###,###,###,##0.00",
                new DecimalFormatSymbols(locale[currency]));

        try {
            Number num = form.parse(snum);
            return new CurrencyNumber(num.doubleValue(), currency);
        } catch (ParseException x) {
            throw new IllegalArgumentException("invalid number format");
        }
    }

    /**
     * This method parses a currency symbol and returns
     * a matched <tt>int</tt>.
     * <p>
     * This method is the reverse function of
     * <tt>getCurrenySymbol()</tt>
     */
    public static int parseCurrencySymbol(String symbol) {
        for (int i = 0; i < csymbols.length; i++) {
            StringTokenizer st = new StringTokenizer(csymbols[i]);
            while (st.hasMoreTokens())
                if (st.nextToken().equalsIgnoreCase(symbol)) {
                    return i;
                }
        }

        throw new IllegalArgumentException("invalid currency symbol");
    }

    /**
     * Liefert Anzahl der berücksichtigten Währungen inklusive Euro.
     *
     * @return Anzahl der Währungen
     */
    public static int getCurrencies() {
        return cfactor.length;
    }

    /**
     * Liefert ganzahligen Anteil, sofern der ganzahlige Anteil des Betrags
     * als <code>int</code> dargestellt werden kann.
     */
    public int intValue() {
        return (int) (value / 10000L);
    }

    /**
     * Liefert interne Darstellung, d.h. nicht ausschließlich den ganzzahligen
     * Anteil, sondern den gesamten Betrag.
     */
    public long longValue() {
        return value;
    }

    /**
     * Genaueste Betragsdarstellung.
     *
     * Zum rechnen mit Währungen sollten jedoch die Funktionen
     * <code>add,sub,div,mul,neg</code> verwendet werden.
     *
     * @return <code>(double)value/10000.0</code>
     */
    public double doubleValue() {
        return (double) value / 10000.0;
    }

    public float floatValue() {
        return (float) value / 10000.0f;
    }

    public byte byteValue() {
        return (byte) (value / 10000L);
    }

    public short shortValue() {
        return (short) (value / 10000L);
    }
}