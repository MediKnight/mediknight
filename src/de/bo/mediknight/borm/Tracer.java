/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.borm;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Common logging class.
 * <p>
 * Used for debugging purpose.
 * <p>
 * You can use Tracer simply by creating a new Object or by using a
 * default Tracer. This class provides different trace-levels such as
 * time/date stamping, line numbering or classname tracing. Tracers can be
 * masked to prevent full traces.
 * <p>
 * A Tracer is bound to a OutputStream or a PrintWriter
 * (default: System.out).
 * Other output object can be added/removed by the <code>addTee()</code> or
 * <code>removeTee()</code> functions.
 * <p>
 * Before you can use a Tracer you must specify some "Trace classes" globally.
 * When you create a new Tracer you must enable at least one of theses trace
 * classes for this Tracer instance to trace results.
 * <p>
 * Example (traces to System.err and to a logfile):
 * <p><pre>
 * // Add some trace classes (for all tracers):
 * Tracer.addTraceClass( "info" );
 * Tracer.addTraceClass( "warn" );
 * Tracer.addTraceClass( "error" );
 *
 * // Create a tracer:
 * Tracer tr = new Tracer( System.err );
 *
 * try {
 *   // Tee a logfile:
 *   tr.addTee( "test.log" );
 *   // Enable error messages for this tracer:
 *   tr.enable( "error" );
 *   // Throw an exception:
 *   throw new IllegalArgumentException( "bla bla" );
 * }
 * catch ( Exception x ) {
 *   // Trace this exception
 *   tr.trace( "error", x );
 * }
 * </pre>
 */

public class Tracer
{
  /**
   * Reports line numbers.
   */
  public final static int LINENR = 1;

  /**
   * Reports trace class.
   *
   * (set by default)
   */
  public final static int TRACECLASS = 2;

  /**
   * Reports class names (if given as the first parameter by the <code>trace</code>
   * function.
   *
   * @see #trace(String,Object,Object)
   */
  public final static int CLASSNAME = 4;

  /**
   * Reports date/time stamps.
   *
   * (set by default)
   */
  public final static int TIMESTAMP = 8;

  /**
   * Default tracing information.
   *
   * Defined by <code>TRACECLASS | TIMESTAMP</code>.
   */
  public final static int DEFAULT = TRACECLASS | TIMESTAMP;

  /**
   * Default tracer to <code>System.out</code>.
   *
   * The default tracer can only be used with the universal trace class "*".
   */
  protected static Tracer defaultTracer;

  /**
   * A list of known trace classes.
   */
  protected static List traceClasses;

  /**
   * Trace destination writer.
   */
  protected PrintWriter writer;

  /**
   * A list of teed output objects used by this tracer.
   */
  protected List teeList;

  /**
   * A list of enabled trace classes by this tracer.
   */
  protected List enabledClasses;

  /**
   * Trace info mask.
   */
  protected int mask;

  /**
   * Line number counter.
   */
  protected int lineNr;

  /**
   * Prefix (empty String by default).
   *
   * This string is reported before any other informations.
   */
  protected String prefix;

  /**
   * Infix (empty String by default).
   *
   * This string is reported after general informations
   * (like TIMESTAMP or CLASSNAME) but before the message to print.
   */
  protected String infix;

  /**
   * Suffix (empty String by default).
   *
   * This string is reported after any other informations.
   */
  protected String suffix;

  static {
    // create list for known trace classes and
    // add "*" (trace everything) to the list.
    traceClasses = new LinkedList();
    addTraceClass( "*" );

    defaultTracer = new DefaultTracer();
  }

  /**
   * Add given trace class to the list of known classes.
   */
  public static void addTraceClass(String traceClass) {
    String tc = traceClass.toUpperCase().trim();
    if ( !traceClasses.contains(tc) )
      traceClasses.add( tc );
  }

  /**
   * Retrieve all known trace classes.
   */
  public static String[] getTraceClasses() {
    int n = traceClasses.size();
    String[] sa = new String[n];
    for ( int i=0; i<n; i++ )
      sa[i] = (String)traceClasses.get( i );
    return sa;
  }

  /**
   * Returns the default tracer.
   */
  public static Tracer getDefaultTracer() {
    return defaultTracer;
  }

  /**
   * Creates a tracer to <code>System.out</code>.
   */
  public Tracer() {
    this( System.out );
    init();
  }

  /**
   * Creates a tracer to the given stream.
   */
  public Tracer(OutputStream os) {
    writer = new PrintWriter( os );
    mask = DEFAULT;
    init();
  }

  /**
   * Creates a tracer to the given writer.
   */
  public Tracer(PrintWriter writer) {
    this.writer = writer;
    mask = DEFAULT;
    init();
  }

  /**
   * Creates a tracer to <code>System.out</code>
   * with a given mask.
   */
  public Tracer(int mask) {
    this( System.out, mask );
    init();
  }

  /**
   * Creates a tracer to the given stream with a given mask.
   */
  public Tracer(OutputStream os,int mask) {
    writer = new PrintWriter( os );
    this.mask = mask;
    init();
  }

  /**
   * Creates a tracer to the given writer with a given mask.
   */
  public Tracer(PrintWriter writer,int mask) {
    this.writer = writer;
    this.mask = mask;
    init();
  }

  /**
   * Initialisation, called by every constructor.
   */
  protected void init() {
    lineNr = 1;
    prefix = "";
    infix  = "";
    suffix = "";
    enabledClasses = new LinkedList();
    teeList = new LinkedList();
  }

  /**
   * Tees an OutputStream to the current writer.
   */
  public void addTee(OutputStream os) {
    addTeeObject( os );
  }

  /**
   * Tees an other PrintWriter to the current writer.
   */
  public void addTee(PrintWriter pw) {
    addTeeObject( pw );
  }

  /**
   * Tees a file to the current writer.
   */
  public void addTee(String filename) {
    addTeeObject( filename );
  }

  /**
   * This function is used by the <code>addTee()</code> functions.
   */
  protected void addTeeObject(Object o) {
    if ( !teeList.contains( o ) )
      teeList.add( o );
  }

  /**
   * Untees an OutputStream from the current writer.
   */
  public void removeTee(OutputStream os) {
    removeTeeObject( os );
  }

  /**
   * Untees a PrintWriter from the current writer.
   */
  public void removeTee(PrintWriter pw) {
    removeTeeObject( pw );
  }

  /**
   * Untees a File from the current writer.
   */
  public void removeTee(String filename) {
    removeTeeObject( filename );
  }

  /**
   * Untees all output objects from the current writer.
   */
  public void removeTees() {
    int n = teeList.size();
    for ( int i=n-1; i>=0; i-- )
      teeList.remove( i );
  }

  /**
   * This function is used by the <code>removeTee()</code> functions.
   */
  protected void removeTeeObject(Object o) {
    teeList.remove( o );
  }

  /**
   * Enable given trace class.
   */
  public void enable(String traceClass) {
    enable( traceClass, true );
  }

  /**
   * Enable or disable given trace class.
   */
  public void enable(String traceClass,boolean set) {
    String tc = traceClass.toUpperCase().trim();
    if ( !traceClasses.contains(tc) ) return;

    if ( tc.equals("*") ) {
      // enable or disable all known classes
      enabledClasses = new LinkedList();
      if ( set )
    for ( int i=0; i<traceClasses.size(); i++ )
      enabledClasses.add( traceClasses.get(i) );
    }
    else {
      if ( set && !enabledClasses.contains(tc) )
    enabledClasses.add( tc );
      if ( !set && enabledClasses.contains(tc) )
    enabledClasses.remove( tc );
    }
  }

  /**
   * Check, if given trace class is enabled or not.
   */
  public boolean isEnabled(String traceClass) {
    String tc = traceClass.toUpperCase().trim();
    return traceClasses.contains( tc );
  }

  /**
   * Change current writer.
   */
  public void setWriter(PrintWriter writer) {
    if ( this.writer != writer ) {
      this.writer.close();
      this.writer = writer;
    }
  }

  /**
   * Change current writer.
   */
  public void setWriter(OutputStream os) {
    writer.close();
    writer = new PrintWriter( os );
  }

  /**
   * Change current mask.
   */
  public void setMask(int mask) {
    this.mask = mask;
  }

  /**
   * Change current mask (bitwise).
   */
  public void setMask(int mask,boolean set) {
    if ( set )
      this.mask |= mask;
    else
      this.mask &= (~mask);
  }

  /**
   * Get current writer.
   */
  public PrintWriter getWriter() {
    return writer;
  }

  /**
   * Get current mask.
   */
  public int getMask() {
    return mask;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setInfix(String infix) {
    this.infix = infix;
  }

  public String getInfix() {
    return infix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public String getSuffix() {
    return suffix;
  }

  /**
   * Print given line to the current writer appending all depending data.
   */
  protected void printLine(String traceClass,Object parent,String line) {

    String tc = traceClass.toUpperCase().trim();

    if ( !(enabledClasses.contains(tc) || tc.equals("*") ) )
      return;

    StringBuffer sb = new StringBuffer();
    String s = getPrefix();

    if ( s.length() > 0 )
      sb.append( s+" " );

    if ( (mask&LINENR) != 0 )
      sb.append( new Integer(lineNr)+" " );

    if ( (mask&TRACECLASS) != 0 )
      sb.append( tc+" " );

    if ( (mask&CLASSNAME) != 0 && parent != null )
      sb.append( parent.getClass().getName()+" " );

    if ( (mask&TIMESTAMP) != 0 ) {
      SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd/HH:mm:ss" );
      sb.append( sdf.format(new Date())+" " );
    }

    s = getInfix();
    if ( s.length() > 0 )
      sb.append( s+" " );

    sb.append( line );

    s = getSuffix();
    if ( s.length() > 0 )
      sb.append( " "+s );

    writeLine( sb.toString() );

    lineNr++;
  }

    protected void writeLine(String s) {
        writer.println(s);
        writer.flush();

        Iterator it = teeList.iterator();
        while ( it.hasNext() ) {
            Object o = it.next();
            if ( o instanceof PrintWriter ) {
                PrintWriter pw = (PrintWriter)o;
                pw.println(s);
                pw.flush();
            }
            if ( o instanceof OutputStream ) {
                PrintWriter pw = new PrintWriter((OutputStream)o);
                pw.println(s);
                pw.flush();
            }
            if ( o instanceof String ) {
                try {
                    FileOutputStream fos = new FileOutputStream(o.toString(),true);
                    PrintWriter pw = new PrintWriter(fos);
                    pw.println(s);
                    pw.flush();
                    fos.close();
                }
                catch ( IOException ioe ) {
                }
            }
        }
    }

    /**
    * Appending stack traces, generated by Throwables with additional data
    * by writing the stack trace to a string stream.
    */
    protected void printThrowable(String traceClass,
            Object parent,
            final Throwable t) {

        printMultiLineObject(traceClass,parent,new MultiLineWrapper() {
            public void print(PrintWriter pw) {
                t.printStackTrace(pw);
            }
        });
    }

//   protected void printProperties(String traceClass,Object parent,
// 				 final Properties p) {
//     printMultiLineObject( traceClass, parent, new MultiLineWrapper() {
//       public void print(PrintWriter pw) {
// 	p.list( pw );
//       }
//     } );
//   }

  /**
   * Appending properties lists with additional data
   * by writing the stack trace to a string stream.
   */
  protected void printProperties(String traceClass,Object parent,
                 final Properties p) {

    // Normally, we want to use the printMultiLineObject() method to
    // trace properties. But unfortunally, the method list() truncates
    // data with no chance to change this behavior.
    int n = p.size();
    String[] pna = new String[n];
    Enumeration e = p.propertyNames();
    for ( int i=0; i<n; i++ )
      pna[i] = e.nextElement().toString();

    Arrays.sort( pna );

    for ( int i=0; i<n; i++ ) {
      String line = pna[i]+"="+p.getProperty(pna[i]);
      printLine( traceClass, parent, line );
    }
  }

  /**
   * Catching special implemented object output
   * (i.e. Throwable.printStackTrace()) by a string stream,
   * appending lines and redirect the lines to the tracers
   * writer.
   */
  protected void printMultiLineObject(String traceClass,Object parent,
                      MultiLineWrapper mlw) {
    // create string stream based PrintWriter ...
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter( sw );

    // do the work (implemented by an interface)
    mlw.print( pw );
    pw.close();

    // get contents by a string stream based reader
    BufferedReader br = new BufferedReader( new StringReader(sw.toString()) );
    String line;
    try {
      while ( (line=br.readLine()) != null )
    // print line by line
    printLine( traceClass, parent, line );
      br.close();
    }
    catch ( IOException x ) {
      // never happens on string streams.
    }
  }

  /**
   * Wrapper used by printMultiLineObject.
   * <p>
   * Example of using  this interface:
   * <pre>
   * protected void printProperties(String traceClass,Object parent,
   *                                final Properties p) {
   *   printMultiLineObject( traceClass, parent, new MultiLineWrapper() {
   *     public void print(PrintWriter pw) {
   *       p.list( pw );
   *     }
   *   } );
   * }
   * </pre>
   *
   * @see #printMultiLineObject
   */
  protected static interface MultiLineWrapper {
    /**
     * Defines objects print method using a PrintWriter.
     */
    public void print(PrintWriter pw);
  }

  /**
   * Tracing.
   *
   * Print given message to the current writer.
   *
   * @param traceClass used trace class for this message
   * @param parent caller object (can be null)
   * @param msg given message
   */
  public void trace(String traceClass,Object parent,Object msg) {
    if ( msg == null )
      printLine( traceClass, parent, "null" );
    else if ( msg instanceof String )
      printLine( traceClass, parent, (String)msg );
    else if ( msg instanceof Throwable )
      printThrowable( traceClass, parent, (Throwable)msg );
    else if ( msg instanceof Properties )
      printProperties( traceClass, parent, (Properties)msg );
    else
      printLine( traceClass, parent, msg.toString() );
  }

  /**
   * Tracing.
   *
   * Print given message to the current writer
   * in the common trace class "*".
   *
   * @param parent caller object (can be null)
   * @param msg given message
   */
  public void trace(Object parent,Object msg) {
    trace( "*", parent, msg );
  }

  /**
   * Tracing.
   *
   * Print given message to the current writer.
   *
   * @param traceClass used trace class for this message
   * @param msg given message
   */
  public void trace(String traceClass,Object msg) {
    trace( traceClass, null, msg );
  }

  /**
   * Tracing.
   *
   * Print given message to the current writer
   * in the common trace class "*".
   *
   * @param msg given message
   */
  public void trace(Object msg) {
    trace( "*", null, msg );
  }

  /**
   * This class defines a nonmutable Tracer used by the
   * <code>getDefaultTracer()</code> method.
   * <p>
   * Mutable calls to the getDefaultTracer() object will
   * throw a UnsupportedOperationException.
   */
  protected static class DefaultTracer extends Tracer
  {
    /**
     * Creates the default tracer with all known trace classes enabled.
     */
    public DefaultTracer() {
      super( System.out );
      for ( int i=0; i<traceClasses.size(); i++ )
    enabledClasses.add( traceClasses.get(i) );
    }

    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    protected void addTeeObject(Object o) {
      throw new UnsupportedOperationException();
    }
    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    public void removeTees() {
      throw new UnsupportedOperationException();
    }
    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    protected void removeTeeObject(Object o) {
      throw new UnsupportedOperationException();
    }
    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    public void enable(String traceClass,boolean set) {
      throw new UnsupportedOperationException();
    }
    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    public void setWriter(PrintWriter writer) {
      throw new UnsupportedOperationException();
    }
    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    public void setWriter(OutputStream os) {
      throw new UnsupportedOperationException();
    }
    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    public void setMask(int mask,boolean set) {
      throw new UnsupportedOperationException();
    }
    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    public void setPrefix(String prefix) {
      throw new UnsupportedOperationException();
    }
    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    public void setInfix(String prefix) {
      throw new UnsupportedOperationException();
    }
    /**
     * Overrided to prevent changes on the default tracer.
     * @exception UnsupportedOperationException
     */
    public void setSuffix(String prefix) {
      throw new UnsupportedOperationException();
    }
  }
}
