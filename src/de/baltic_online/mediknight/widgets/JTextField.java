/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.baltic_online.mediknight.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.TextListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;


/**
 * This subclass of <code>JTextField</code> supports an easy to use TextListener. Also implements the <code>MutableTextComponent</code> interface and adds a
 * popup-menu for easy cut'n'paste etc.
 *
 * @author sma@baltic-online.de
 * @author chs@baltic-online.de
 * @author es@baltic-online.de
 * @version 1.6
 * @see MutableTextComponent
 */
public class JTextField extends javax.swing.JTextField implements MutableTextComponent, DocumentListener {

    private static final long	serialVersionUID = 1L;

    private final TextListenerPlugin plugin;
    private String		   originalText     = null;
    private final JPopupMenu	 popupMenu	= new JPopupMenu();
    private UndoHandler	      undoHandler      = null;
    private String		   undoHandlerName  = null;

    /**
     * a list of those interested in receiving <code>MutableChangeEvent</code>s from this instance.
     */
    EventListenerList		listenerList     = new EventListenerList();


    /**
     * Construct a new <code>JTextField</code> with an empty initial text.
     *
     * @since 1.0
     */
    public JTextField() {
	this( "" );
    }


    /**
     * Construct a new <code>JTextField</code> using the given initial text.
     *
     * @param initialText
     *            the initial text of the <code>JTextField</code>
     *
     * @since 1.0
     */
    public JTextField( final String initialText ) {
	super( initialText );
	plugin = new TextListenerPlugin( this );
	if( !initialText.equals( "" ) ) {
	    originalText = initialText;
	}

	this.addMouseListener( new MouseAdapter() {

	    @Override
	    public void mouseClicked( final MouseEvent e ) {
		processMouseClick( e );
	    }
	} );

	// construct a popup-menu for the sake of user convenience.
	final JMenuItem itemCut = new JMenuItem( "Ausschneiden" );
	final JMenuItem itemCopy = new JMenuItem( "Kopieren" );
	final JMenuItem itemPaste = new JMenuItem( "Einfügen" );
	final JMenuItem itemMarkAll = new JMenuItem( "Alles markieren" );
	final JMenuItem itemRevert = new JMenuItem( "Wiederherstellen" );

	itemCut.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		cut();
	    }
	} );

	itemCopy.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		copy();
	    }
	} );

	itemPaste.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		paste();
	    }
	} );

	itemMarkAll.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		selectAll();
	    }
	} );

	itemRevert.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		revert();
	    }
	} );

	popupMenu.add( itemCut );
	popupMenu.add( itemCopy );
	popupMenu.add( itemPaste );
	popupMenu.add( itemMarkAll );
	popupMenu.addSeparator();
	popupMenu.add( itemRevert );

	setBorder( new UnderlineableBorder( getBorder(), getSelectionColor() ) );
	getDocument().addDocumentListener( this );

    }


    /**
     * Register an object to receive <code>MutableChangeEvent</code>s from us in the future.
     *
     * @since 1.3
     */
    @Override
    public void addMutableChangeListener( final MutableChangeListener l ) {
	listenerList.add( MutableChangeListener.class, l );
    }


    /**
     * add a text listener to this <code>JTextField</code>, using the <code>TextListener</code> plugin.
     *
     * @param l
     *            the <code>TextListener</code> to add
     *
     * @since 1.0
     */
    public void addTextListener( final TextListener l ) {
	plugin.addTextListener( l );
    }


    /**
     * this method is called whenever the text is changed by the user.
     *
     * @since 1.5
     */
    @Override
    public void changedUpdate( final DocumentEvent e ) {
	textUpdate();
    }


    /**
     * Commit changes made.
     *
     * @since 1.6
     */
    @Override
    public void commit() {
	setOriginalText( getText() );
    }


    /**
     * Notify all listeners that have registered interest for notification on this event type.
     *
     * @since 1.3
     */
    @Override
    public void fireMutableChanged() {
	if( undoHandler == null ) {
	    undoHandler = UndoUtilities.findUndoHandler( this );
	    addMutableChangeListener( undoHandler );
	}
	final MutableChangeEvent fooEvent = new MutableChangeEvent( this, isChanged() );
	UndoUtilities.dispatchMutableChangeEvent( fooEvent, listenerList );
    }


    /**
     * Forget the original text.
     *
     * @since 1.2
     */
    @Override
    public void forgetOriginalValue() {
	setOriginalText( null );
	updateBorder();
    }


    /**
     * get the original text.
     *
     * @since 1.1
     */
    @Override
    public String getOriginalText() {
	return originalText;
    }


    /**
     * Return the name of the <code>UndoHandler</code> object responsible for handling Undo for this widget.
     *
     * @since 1.4
     */
    @Override
    public String getResponsibleUndoHandler() {
	return undoHandlerName;
    }


    /**
     * this method is called whenever text is inserted by the user.
     *
     * @since 1.5
     */
    @Override
    public void insertUpdate( final DocumentEvent e ) {
	textUpdate();
    }


    /**
     * check whether the text has been changed.
     *
     * @since 1.1
     */
    @Override
    public boolean isChanged() {
	if( getOriginalText() == null ) {
	    return false;
	}

	return this.isEditable() && !getText().equals( getOriginalText() );
    }


    /**
     * process a mouse click: show a pop-up menu on right button-click (and consume that click in the process), and hide it on any click if is showing (and
     * don't consume that click in the process).
     *
     * @param e
     *            the <code>MouseEvent</code> to process
     *
     * @since 1.1
     */
    public void processMouseClick( final MouseEvent e ) {
	if( e.getID() == MouseEvent.MOUSE_CLICKED && e.getModifiers() == InputEvent.BUTTON3_MASK ) {
	    popupMenu.show( this, e.getX(), e.getY() );
	    popupMenu.setVisible( true );
	    e.consume();
	} else if( e.getID() == MouseEvent.MOUSE_CLICKED && popupMenu.isVisible() ) {
	    popupMenu.setVisible( false );
	}
    }


    /**
     * Deregister an object to receive <code>MutableChangeEvent</code>s from us in the future.
     *
     * @since 1.3
     */
    @Override
    public void removeMutableChangeListener( final MutableChangeListener l ) {
	listenerList.remove( MutableChangeListener.class, l );
    }


    /**
     * remove a text listener from this <code>JTextField</code>, using the <code>TextListener</code> plugin.
     *
     * @param l
     *            the <code>TextListener</code> to remove
     *
     * @since 1.0
     */
    public void removeTextListener( final TextListener l ) {
	plugin.removeTextListener( l );
    }


    /**
     * this method is called whenever text is removed by the user.
     *
     * @since 1.5
     */
    @Override
    public void removeUpdate( final DocumentEvent e ) {
	textUpdate();
    }


    /**
     * revert the text to the original text.
     *
     * @since 1.1
     */
    @Override
    public void revert() {
	setText( getOriginalText() );
	updateBorder();
	fireMutableChanged();
    }


    /**
     * set the original text.
     *
     * @param originalText
     *            the new original text of the <code>JTextField</code>.
     *
     * @since 1.1
     */
    @Override
    public void setOriginalText( String originalText ) {
	if( originalText == null ) {
	    originalText = "";
	}

	this.originalText = originalText;
    }


    /**
     * Set the name of the <code>UndoHandler</code> widget responsible for handling Undo for this widget.
     *
     * @param s
     *            the name of the new <code>UndoHandler</code>
     *
     * @since 1.4
     */
    @Override
    public void setResponsibleUndoHandler( final String s ) {
	if( !isChanged() ) {
	    if( undoHandler != null ) {
		removeMutableChangeListener( undoHandler );
	    }

	    undoHandlerName = s;
	    undoHandler = null;
	}
    }


    /**
     * Set the text of this <code>JTextField</code>. Also sets the original text if necessary (i.e., if it is <code>null</code>)
     *
     * @param newText
     *            the new text of the <code>JTextField</code>
     *
     * @since 1.1
     */
    @Override
    public void setText( String newText ) {
	if( newText == null ) {
	    newText = "";
	}

	super.setText( newText );
	originalText = newText;
    }


    // --- implementation of DocumentListener ---

    /**
     * Do what's necessary when the text changes, allowing <code>MutableChangeEvents</code> to be fired.
     *
     * @since 1.5
     */
    public void textUpdate() {
	textUpdate( true );
    }


    /**
     * Do what's necessary when the text changes.
     *
     * @param fireAllowed
     *            should be true if firing <code>MutableChangeEvents</code> is allowed.
     *
     * @since 1.5
     */
    public void textUpdate( final boolean fireAllowed ) {
	if( originalText == null ) {
	    originalText = getText();
	}

	if( !fireAllowed ) {
	    return;
	}

	updateBorder();
	fireMutableChanged();
    }


    /**
     * update the <code>JTextField</code>'s border, if it is an instance of <code>UnderlineableBorder</code>
     *
     * @see UnderlineableBorder
     * @since 1.1
     */
    public void updateBorder() {
	final Border border = getBorder();
	if( border instanceof UnderlineableBorder ) {
	    ((UnderlineableBorder) border).setUnderlined( getOriginalText() == null ? false : isChanged() );
	}

	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		repaint();
	    }
	} );
    }

}