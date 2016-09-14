package main.java.de.baltic_online.mediknight.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.java.de.baltic_online.mediknight.domain.User;


/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class LoginDialog extends JDialog {

    /**
     * The panel which is holding the user icons.
     * <p>
     * This class could not be inner because we use static members.
     */
    private static class IconPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static Dimension  buttonSize       = null;


	public static Dimension getButtonSize() {
	    return buttonSize;
	}

	private final LoginDialog dialog;
	private final ButtonGroup buttonGroup;


	public IconPanel( final LoginDialog dialog ) {
	    super();

	    this.dialog = dialog;

	    buttonGroup = new ButtonGroup();
	    setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
	}


	@Override
	public void actionPerformed( final ActionEvent e ) {
	    final User user = ((UserButton) e.getSource()).getUser();
	    dialog.fillUserControls( user );
	}


	public void add( final User user ) {
	    final UserIcon icon = new UserIcon( user );
	    final UserButton b = new UserButton( user, icon );
	    b.addActionListener( this );
	    buttonGroup.add( b );

	    super.add( b );

	    if( buttonSize == null ) {
		buttonSize = b.getPreferredSize();
	    }
	}


	public void selectUser( final User user ) {
	    for( int i = 0; i < getComponentCount(); i++ ) {
		final UserButton b = (UserButton) getComponent( i );

		if( b.user == user ) {
		    b.setSelected( true );
		    scrollRectToVisible( b.getBounds() );
		}
	    }
	}
    }

    /**
     * We subclass JButton because we do not need an extra storage for the users.
     */
    private static class UserButton extends JToggleButton {

	private static final long serialVersionUID = 1L;

	private final User	user;


	public UserButton( final User user, final Icon icon ) {
	    super( user.getName(), icon );

	    this.user = user;

	    setVerticalTextPosition( SwingConstants.BOTTOM );
	    setHorizontalTextPosition( SwingConstants.CENTER );
	}


	public User getUser() {
	    return user;
	}
    }

    /**
     * Simple implementation of an <tt>Icon</tt> which data source differs from an URL.
     */
    public static class UserIcon implements Icon, ImageObserver {

	/**
	 * Fixed width of this icon.
	 */
	public static final int WIDTH  = 128;

	/**
	 * Fixed height of this icon.
	 */
	public static final int HEIGHT = 160;

	private final byte[]    imageData;
	private Image	   image;

	// We use it to do a repaint if this ImageObserver notifies that
	// asynchronous loading is done.
	private Component       component;


	public UserIcon( final User user ) {
	    component = null;
	    imageData = (byte[]) user.getImageData();
	    if( imageData != null ) {
		image = Toolkit.getDefaultToolkit().createImage( imageData );
	    } else {
		image = LoginDialog.defaultUserImage;
	    }
	}


	/**
	 * Returns the fixed height of this icon.
	 */
	@Override
	public int getIconHeight() {
	    return HEIGHT;
	}


	/**
	 * Returns the fixed width of this icon.
	 */
	@Override
	public int getIconWidth() {
	    return WIDTH;
	}


	/**
	 * Invoked by the image methods.
	 * <p>
	 * If the ALLBITS flag of the infoflags is done a repaint will follow.
	 */
	@Override
	public boolean imageUpdate( final Image img, final int infoflags, final int x, final int y, final int width, final int height ) {
	    final boolean ready = (infoflags & ALLBITS) != 0;
	    if( ready && component != null ) {
		component.repaint();
	    }
	    return !ready;
	}


	/**
	 * Paints this.
	 * <p>
	 * Fortunally, this interface method holds the <tt>Component</tt> so we can use it later for repaint().
	 */
	@Override
	public void paintIcon( final Component c, final Graphics g, final int x, final int y ) {
	    component = c;
	    final Graphics2D g2 = (Graphics2D) g;

	    // This calls may invoke imageUpdate()
	    final int w = image.getWidth( this );
	    final int h = image.getHeight( this );

	    // Scale image if necessary
	    if( w != WIDTH || h != HEIGHT ) {
		final AffineTransform at = new AffineTransform( (double) WIDTH / w, 0.0, 0.0, (double) HEIGHT / h, x, y );
		g2.drawImage( image, at, this );
	    } else {
		g2.drawImage( image, x, y, this );
	    }
	}
    }

    private static final long	  serialVersionUID = 1L;
    // This is the default image of a newly created user. It is set by the initializer below.
    private static Image	       defaultUserImage;

    static {
	defaultUserImage = Toolkit.getDefaultToolkit().createImage( "src/main/resources/images/defaultuser.gif" );
    }
    // Login controls
    private JPanel		     controlPanel;

    private IconPanel		  iconPanel;
    private JButton		    okButton;

    private JButton		    cancelButton;

    private javax.swing.JTextField     userField;

    private javax.swing.JPasswordField passwordField;

    private List< User >	       userList;

    // The "return object" of the dialog. It the user cancels the dialog
    // the value will be null.
    private User		       user;


    /**
     * Creates the login dialog with the given parent frame.
     */
    public LoginDialog( final Frame owner ) {
	this( owner, false );
    }


    public LoginDialog( final Frame owner, final boolean admin ) {
	super( owner, true );

	user = null;

	retrieveUserList();

	// do some GUI stuff
	final JPanel mainPanel = new JPanel( new BorderLayout() );
	mainPanel.setBorder( new EmptyBorder( 16, 16, 16, 16 ) );

	if( admin ) {
	    createAdminIconPanel();
	} else {
	    createIconPanel();
	}

	final JScrollPane sp = new JScrollPane( iconPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );

	final Dimension bsize = IconPanel.getButtonSize();
	sp.getHorizontalScrollBar().setUnitIncrement( bsize.width );
	mainPanel.add( sp, BorderLayout.CENTER );

	createControlPanel();
	mainPanel.add( controlPanel, BorderLayout.SOUTH );

	getContentPane().add( mainPanel );

	// Resizing the mainPanel for exactly 3 user icons (insets related)
	final Dimension msize = mainPanel.getPreferredSize();
	mainPanel.setPreferredSize( new Dimension( bsize.width * 3 + 64, msize.height ) );

	pack();

	// Centering the position of this dialog
	final Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
	final Rectangle rect = getBounds();
	rect.x = (ssize.width - rect.width) / 2;
	rect.y = (ssize.height - rect.height) / 2;
	setBounds( rect );

	setFocusTo( userField );
    }


    private void createAdminIconPanel() {
	iconPanel = new IconPanel( this );
	try {

	    List< User > list = User.retrieve();
	    if( list.size() == 0 ) {
		User.getDefaultAdmin();
		list = User.retrieve();
	    }
	    final Iterator< User > i = list.iterator();
	    while( i.hasNext() ) {
		final User u = i.next();
		if( u.isAdmin() ) {
		    iconPanel.add( u );
		}
	    }
	} catch( final SQLException x ) {
	    x.printStackTrace();
	}
    }


    // Builds panel for controls
    private void createControlPanel() {
	controlPanel = new JPanel( new BorderLayout( 8, 8 ) );

	final GridBagLayout gbl = new GridBagLayout();
	final GridBagConstraints gbc = new GridBagConstraints();
	JPanel pane = new JPanel( gbl );

	gbc.insets = new Insets( 8, 8, 8, 8 );

	JLabel l = new JLabel( "Benutzer:" );
	gbl.setConstraints( l, gbc );
	pane.add( l );

	gbc.gridwidth = GridBagConstraints.REMAINDER;
	// gbc..WEST
	userField = new javax.swing.JTextField( 20 );
	gbl.setConstraints( userField, gbc );
	pane.add( userField );

	gbc.gridwidth = 1;
	l = new JLabel( "Passwort:" );
	gbl.setConstraints( l, gbc );
	pane.add( l );

	gbc.gridwidth = GridBagConstraints.REMAINDER;
	passwordField = new javax.swing.JPasswordField( 20 );
	gbl.setConstraints( passwordField, gbc );
	pane.add( passwordField );

	controlPanel.add( new JLabel( " " ), BorderLayout.NORTH );
	controlPanel.add( pane, BorderLayout.WEST );

	pane = new JPanel( new GridLayout( 2, 1, 18, 18 ) );
	okButton = new JButton( "Anmelden" );
	// okButton.setBackground(new Color(255, 224, 0));
	okButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		validateUser();
	    }
	} );
	pane.add( okButton );
	getRootPane().setDefaultButton( okButton );

	cancelButton = new JButton( "Beenden" );
	cancelButton.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		user = null;
		dispose();
		setVisible( false );
	    }
	} );
	pane.add( cancelButton );

	controlPanel.add( pane, BorderLayout.EAST );

	userField.getDocument().addDocumentListener( new DocumentListener() {

	    @Override
	    public void changedUpdate( final DocumentEvent e ) {
		enableOkButton();
	    }


	    @Override
	    public void insertUpdate( final DocumentEvent e ) {
		enableOkButton();
	    }


	    @Override
	    public void removeUpdate( final DocumentEvent e ) {
		enableOkButton();
	    }
	} );
	enableOkButton();
    }


    // Builds icon panel and populates it
    private void createIconPanel() {
	iconPanel = new IconPanel( this );

	final Iterator< User > i = userList.iterator();
	while( i.hasNext() ) {
	    iconPanel.add( i.next() );
	}
    }


    private void enableOkButton() {
	final User u = findUserByName( userField.getText() );
	iconPanel.selectUser( u );
	okButton.setEnabled( u != null );
    }


    // Called by iconPanel (ActionListener). This method fills the controls
    // from the selected icon and moves the focus to the password field.
    private void fillUserControls( final User user ) {
	userField.setText( user.getName() );
	passwordField.setText( "" );

	setFocusTo( passwordField );
    }


    private User findUserByName( final String s ) {
	final String userInput = s.trim().toLowerCase();
	if( userInput.length() > 0 ) {
	    final Iterator< User > it = userList.iterator();

	    while( it.hasNext() ) {
		final User u = it.next();

		if( u.getName().toLowerCase().equals( userInput ) ) {
		    return u;
		}
	    }
	}

	return null;
    }


    /**
     * Returns the selected user by the dialog.
     *
     * @return the selected user by the dialog.
     */
    public User getUser() {
	return user;
    }


    private void retrieveUserList() {
	try {
	    userList = User.retrieve();
	    if( userList.size() == 0 ) {
		User.getDefaultAdmin();
		userList = User.retrieve();
	    }
	} catch( final SQLException x ) {
	    x.printStackTrace();
	}
    }


    public void setFocusTo( final JComponent component ) {
	SwingUtilities.invokeLater( new Runnable() {

	    @Override
	    public void run() {
		component.requestFocus();
	    }
	} );
    }


    // Validates the input.
    // This method ends the dialog, if the input is correct or the user
    // cancels a retry.
    private void validateUser() {
	try {
	    final String inputUser = userField.getText().trim();
	    final String inputPass = new String( passwordField.getPassword() );
	    final Iterator< User > i = User.retrieve().iterator();
	    while( i.hasNext() ) {
		final User u = i.next();
		if( u.getName().toLowerCase().equals( inputUser.toLowerCase() ) && u.getPassword().equals( inputPass ) ) {
		    user = u;
		}
	    }
	} catch( final SQLException x ) {
	    x.printStackTrace();
	}

	if( user == null ) {
	    final int r = JOptionPane.showConfirmDialog( getContentPane(), "Der Benutzer oder das Passwort sind ungültig.\n"
		    + "Soll die Eingabe wiederholt werden?", "Achtung", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
	    if( r != JOptionPane.YES_OPTION ) {
		dispose();
		setVisible( false );
	    }
	} else {
	    dispose();
	    setVisible( false );
	}

	setFocusTo( passwordField );
    }
}