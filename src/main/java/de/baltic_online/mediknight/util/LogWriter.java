/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */

package main.java.de.baltic_online.mediknight.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;


/**
 * This class provides a PrintWriter binded to a group of logfiles which will be cyclical overwritten.
 * <p>
 * Pathnames of the logfiles are builded in the form
 * 
 * <pre>
 * logfile.000.log   |
 * ...               } basePath+"."+extFormat+".log"
 * logfile.199.log   |
 * </pre>
 * 
 * if a max cycle of 200 is set.
 */
public class LogWriter extends PrintWriter {

    /**
     * Default max lenght of a logfile (1MB)
     */
    protected final static long	DEFAULT_MAXLENGTH = 1024 * 1024;

    /**
     * Default min lenght of a logfile (10KB)
     */
    protected final static long	DEFAULT_MINLENGTH = 10 * 1024;

    /**
     * Default max cycle of logfiles (10)
     */
    protected final static int	DEFAULT_MAXCYCLE  = 10;

    /**
     * Base pathname. Real pathnames are builded by the function <code>buildPathName()</code>.
     *
     * @see #buildPathName()
     * @see #path
     */
    protected String		basePath;

    /**
     * Pathname builded by the function <code>buildPathName()</code>.
     *
     * @see #buildPathName()
     * @see #basePath
     */
    protected String		path;

    /**
     * The calculated length of the cycle extension of the pathname.
     */
    protected int		extLength;

    /**
     * Max file length before this class starts over with a new (or used) file, specified by the pathname.
     */
    protected long		maxLength;

    /**
     * Max cycle before this class starts over to resuse old files.
     */
    protected int		maxCycle;

    /**
     * Current cycle index.
     */
    protected int		currentCycle;

    /**
     * Format pattern used by the DecimalFormat to build the extension string.
     */
    protected String		extFormat;

    /**
     * This is the "real" PrintWriter which is used by this class. This object changes if cycling is done.
     */
    protected PrintWriter	writer;


    /**
     * Creates a new LogWriter with given basePath and default max length and default max cycle.
     *
     * @param basePath
     *            Base pathname
     *
     * @see #basePath
     */
    public LogWriter( final String basePath ) {
	this( basePath, DEFAULT_MAXLENGTH, DEFAULT_MAXCYCLE );
    }


    /**
     * Creates a new LogWriter with given basePath and max length and default max cycle.
     *
     * @param basePath
     *            Base pathname
     * @param maxLength
     *            Max file length
     *
     * @see #basePath
     * @see #maxLength
     */
    public LogWriter( final String basePath, final long maxLength ) {
	this( basePath, maxLength, DEFAULT_MAXCYCLE );
    }


    /**
     * Creates a new LogWriter with given basePath and max length and max cycle.
     *
     * @param basePath
     *            Base pathname
     * @param maxLength
     *            Max file length
     * @param maxCycle
     *            Max cycle
     *
     * @see #basePath
     * @see #maxLength
     * @see #maxCycle
     */
    public LogWriter( final String basePath, final long maxLength, final int maxCycle ) {
	super( System.err );

	validateMaxLength( maxLength );
	validateMaxCycle( maxCycle );

	this.basePath = basePath;
	this.maxLength = maxLength;
	this.maxCycle = maxCycle;

	init();
    }


    /**
     * This method builds the path name of the current logfile.
     *
     * It is implemented by
     * 
     * <pre>
     * 
     * protected void buildPathName() {
     *     DecimalFormat df = new DecimalFormat( extFormat );
     *     path = basePath + &quot;.&quot; + df.format( (long) currentCycle ) + &quot;.log&quot;;
     * }
     * </pre>
     *
     * @see #path
     */
    protected void buildPathName() {
	path = getPathName( currentCycle );
    }


    @Override
    public boolean checkError() {
	return getWriter().checkError();
    }


    /**
     * Flushes and closes the writer.
     *
     * The writer could be reopened by the method <code>flush()</code>.
     *
     * @see #flush()
     */
    @Override
    public void close() {
	flush();
	writer.close();
    }


    protected void createWriter() throws IOException {

	// First check if there is a nonexsisting logfile or if exists
	// use it if the length is not exceeded.
	// Otherwise use first filename.
	currentCycle = 0;
	boolean append = false;
	for( int cycle = 0; cycle < maxCycle; cycle++ ) {
	    final File f = new File( getPathName( cycle ) );
	    if( !f.exists() || f.length() < maxLength ) {
		currentCycle = cycle;
		append = true;
		break;
	    }
	}

	ensurePath();
	buildPathName();

	writer = new PrintWriter( new FileWriter( path, append ) );
    }


    protected void ensurePath() {
	final int fsp = basePath.lastIndexOf( File.separatorChar );
	if( fsp > 0 ) { // its all right, we do not need a separator at 0
	    new File( basePath.substring( 0, fsp ) ).mkdirs();
	}
    }


    /**
     * Flushes the writer.
     *
     * This method checks the file length of the current logfile and if this length exceeds the max length it will do cycling, i.e. building a new path name and
     * a new writer.
     *
     * @see #writer
     * @see #path
     * @see #maxLength
     * @see #close()
     */
    @Override
    public void flush() {
	getWriter();
	try {
	    final long cl = getFileLength();
	    if( cl >= maxLength ) {
		writer.close();
		currentCycle = (currentCycle + 1) % maxCycle;
		buildPathName();
		writer = new PrintWriter( new FileWriter( path, false ) );
	    } else {
		writer.flush();
	    }
	} catch( final IOException ioe ) {
	}
    }


    /**
     * Returns the current cycle.
     *
     * @return Current cycle.
     */
    public int getCurrentCycle() {
	return currentCycle;
    }


    /**
     * Returns the current length of the current logfile.
     *
     * @return Length of the current logfile
     */
    public long getFileLength() {
	final File f = new File( path );
	return f.length();
    }


    /**
     * Returns the max cycle.
     *
     * @return Max cycle.
     */
    public int getMaxCycle() {
	return maxCycle;
    }


    /**
     * Returns the max file length.
     *
     * @return Max file length
     */
    public long getMaxLength() {
	return maxLength;
    }


    protected String getPathName( final int cycle ) {
	final DecimalFormat df = new DecimalFormat( extFormat );
	return basePath + "." + df.format( cycle ) + ".log";
    }


    /**
     * Returns <code>writer</code> or setup a new PrintWriter if <code>writer == null</code>
     *
     * @return <code>writer</code>
     * @see #writer
     */
    protected PrintWriter getWriter() {
	try {
	    if( writer == null ) {
		createWriter();
	    }
	    return writer;
	} catch( final IOException ioe ) {
	    return this;
	}
    }


    /**
     * This function is called by the constructors and by <code>setMaxCycle</code> method. It recalculates the number of 0's used by <code>path</code>, sets
     * <code>currentCycle</code> to 0 and calls <code>buildPathName()</code>
     *
     * @see #buildPathName()
     */
    protected void init() {

	extLength = new Integer( maxCycle - 1 ).toString().length();

	final StringBuffer sb = new StringBuffer();
	for( int i = 0; i < extLength; i++ ) {
	    sb.append( '0' );
	}

	extFormat = sb.toString();
    }


    @Override
    public void print( final boolean b ) {
	getWriter().print( b );
    }


    @Override
    public void print( final char c ) {
	getWriter().print( c );
    }


    @Override
    public void print( final char[] a ) {
	getWriter().print( a );
    }


    @Override
    public void print( final double d ) {
	getWriter().print( d );
    }


    @Override
    public void print( final float f ) {
	getWriter().print( f );
    }


    @Override
    public void print( final int i ) {
	getWriter().print( i );
    }


    @Override
    public void print( final long l ) {
	getWriter().print( l );
    }


    @Override
    public void print( final Object o ) {
	getWriter().print( o );
    }


    @Override
    public void print( final String s ) {
	getWriter().print( s );
    }


    @Override
    public void println() {
	getWriter().println();
    }


    @Override
    public void println( final boolean b ) {
	getWriter().println( b );
    }


    @Override
    public void println( final char c ) {
	getWriter().println( c );
    }


    @Override
    public void println( final char[] a ) {
	getWriter().println( a );
    }


    @Override
    public void println( final double d ) {
	getWriter().println( d );
    }


    @Override
    public void println( final float f ) {
	getWriter().println( f );
    }


    @Override
    public void println( final int i ) {
	getWriter().println( i );
    }


    @Override
    public void println( final long l ) {
	getWriter().println( l );
    }


    @Override
    public void println( final Object o ) {
	getWriter().println( o );
    }


    @Override
    public void println( final String s ) {
	getWriter().println( s );
    }


    /**
     * Set the max cycle.
     *
     * This method applies only on cycling.
     *
     * @param maxCycle
     *            Max cycle
     */
    public void setMaxCycle( final int maxCycle ) {
	validateMaxCycle( maxCycle );
	this.maxCycle = maxCycle;
	init();
    }


    /**
     * Set the max file length.
     *
     * This method applies on <code>flush()</code>.
     *
     * @param maxLength
     *            Max file length
     * @see #flush()
     */
    public void setMaxLength( final long maxLength ) {
	validateMaxLength( maxLength );
	this.maxLength = maxLength;
    }


    private void validateMaxCycle( final int maxCycle ) {
	if( maxCycle < 2 ) {
	    throw new IllegalArgumentException( "cycling must be at least two" );
	}
    }


    private void validateMaxLength( final long maxLength ) {
	if( maxLength < DEFAULT_MINLENGTH ) {
	    throw new IllegalArgumentException( "logfile size must be at least " + DEFAULT_MINLENGTH + " bytes" );
	}
    }


    @Override
    public void write( final char[] buf ) {
	getWriter().write( buf );
    }


    @Override
    public void write( final char[] buf, final int off, final int len ) {
	getWriter().write( buf, off, len );
    }


    @Override
    public void write( final int c ) {
	getWriter().write( c );
    }


    @Override
    public void write( final String s ) {
	getWriter().write( s );
    }


    @Override
    public void write( final String s, final int off, final int len ) {
	getWriter().write( s, off, len );
    }
}
