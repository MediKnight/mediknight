/*
 * @(#)$Id$
 *
 * (C)2001 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RootPaneContainer;

import de.baltic_online.borm.TraceConstants;
import de.baltic_online.mediknight.MainFrame;


public class ErrorDisplay {

    JFrame    errorFrame;
    JFrame    traceFrame;
    JButton   details;
    Throwable throwable;


    public ErrorDisplay( final Throwable throwable, final String displayMessage ) {
	this( throwable, displayMessage, "Fehler", null );
    }


    public ErrorDisplay( final Throwable throwable, final String displayMessage, final Container parent ) {
	this( throwable, displayMessage, "Fehler", parent );
    }


    public ErrorDisplay( final Throwable throwable, final String displayMessage, final String title ) {
	this( throwable, displayMessage, title, null );
    }


    public ErrorDisplay( final Throwable throwable, final String displayMessage, final String title, Container parent ) {
	this.throwable = throwable;

	MainFrame.getTracer().trace( TraceConstants.ERROR, throwable );

	if( parent == null ) {
	    parent = JOptionPane.getRootFrame();
	    if( parent instanceof RootPaneContainer ) {
		parent = ((RootPaneContainer) parent).getContentPane();
	    }
	}

	errorFrame = new JFrame( title );
	final JPanel pane = (JPanel) errorFrame.getContentPane();
	pane.setLayout( new BorderLayout() );
	details = new JButton( "Details >>" );
	details.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		detailsClicked();
	    }
	} );
	final JButton ok = new JButton( "Ok" );
	ok.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		okClicked();
	    }
	} );
	final JPanel buttonBar = new JPanel( new BorderLayout() );
	buttonBar.add( ok, BorderLayout.SOUTH );
	buttonBar.add( details, BorderLayout.NORTH );
	pane.add( new de.baltic_online.mediknight.widgets.JTextArea( displayMessage + "\n" + throwable.getMessage() ), BorderLayout.CENTER );
	pane.add( buttonBar, BorderLayout.EAST );
	errorFrame.pack();
	positionErrorFrame( parent );
	errorFrame.setVisible( true );
    }


    private void detailsClicked() {
	if( traceFrame == null ) {
	    details.setText( "Details <<" );
	    traceFrame = new JFrame();
	    final StringWriter trace = new StringWriter();
	    throwable.printStackTrace( new PrintWriter( trace ) );
	    final de.baltic_online.mediknight.widgets.JTextArea details = new de.baltic_online.mediknight.widgets.JTextArea( trace.toString() );
	    final JScrollPane pane = new JScrollPane( details );
	    traceFrame.getContentPane().add( pane );
	    traceFrame.pack();
	    positionAndResizeTraceFrame();
	    traceFrame.setVisible( true );
	} else {
	    traceFrame.dispose();
	    traceFrame = null;
	    details.setText( "Details >>" );
	}
    }


    private void okClicked() {
	if( traceFrame != null ) {
	    traceFrame.dispose();
	}
	errorFrame.dispose();
    }


    private void positionAndResizeTraceFrame() {
	final Point pos = errorFrame.getLocation();
	final int errorHeight = errorFrame.getHeight();
	final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final Dimension size = traceFrame.getSize();

	// if there isn't enough space below the error frame, put it above
	if( screenSize.height - (pos.y + errorHeight) < 150 ) {
	    size.height = Math.min( pos.y, size.height );
	    pos.translate( 0, -size.height );
	} else {
	    pos.translate( 0, errorHeight );
	    if( pos.y + size.height > screenSize.height ) {
		size.height = screenSize.height - pos.y;
	    }
	}
	traceFrame.setSize( size );
	traceFrame.setLocation( pos );
    }


    private void positionErrorFrame( final Container parent ) {
	final Dimension mySize = errorFrame.getSize();
	final Dimension parentSize = parent.getSize();
	final Point position = parent.getLocation();
	if( mySize.width > parentSize.width ) {
	    mySize.width = parentSize.width;
	}
	if( mySize.height > parentSize.height ) {
	    mySize.height = parentSize.height;
	}
	position.translate( (parentSize.width - mySize.width) / 2, (parentSize.height - mySize.height) / 4 );
	errorFrame.setLocation( position );
    }
}
