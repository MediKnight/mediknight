package de.bo.mediknight.widgets;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.net.URL;
import java.util.*;
import java.sql.SQLException;

import de.bo.mediknight.domain.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class LoginDialog extends JDialog {
	// This is the default image of a newly created user.
	// It is set by the initializer below.
	private static Image defaultUserImage;

	static {
		URL url =
			LoginDialog.class.getClassLoader().getResource(
				"de/bo/mediknight/resources/defaultuser.gif");
		defaultUserImage = Toolkit.getDefaultToolkit().createImage(url);
	}

	// Login controls
	private JPanel controlPanel;
	private IconPanel iconPanel;

	private JButton okButton;
	private JButton cancelButton;

	private javax.swing.JTextField userField;
	private javax.swing.JPasswordField passwordField;

	private java.util.List userList;

	// The "return object" of the dialog. It the user cancels the dialog
	// the value will be null.
	private User user;

	/**
	 * Creates the login dialog with the given parent frame.
	 */
	public LoginDialog(Frame owner) {
		this(owner, false);
	}

	public LoginDialog(Frame owner, boolean admin) {
		super(owner, true);

		user = null;

		retrieveUserList();

		// do some GUI stuff
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

		if (admin)
			createAdminIconPanel();
		else
			createIconPanel();

		JScrollPane sp =
			new JScrollPane(
				iconPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		Dimension bsize = IconPanel.getButtonSize();
		sp.getHorizontalScrollBar().setUnitIncrement(bsize.width);
		mainPanel.add(sp, BorderLayout.CENTER);

		createControlPanel();
		mainPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(mainPanel);

		// Resizing the mainPanel for exactly 3 user icons (insets related)
		Dimension msize = mainPanel.getPreferredSize();
		mainPanel.setPreferredSize(new Dimension(bsize.width * 3 + 64, msize.height));

		pack();

		// Centering the position of this dialog
		Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle rect = getBounds();
		rect.x = (ssize.width - rect.width) / 2;
		rect.y = (ssize.height - rect.height) / 2;
		setBounds(rect);

		setFocusTo(userField);
	}

	private void retrieveUserList() {
		try {
			userList = User.retrieve();
			if (userList.size() == 0) {
				User.getDefaultAdmin();
				userList = User.retrieve();
			}
		} catch (SQLException x) {
			x.printStackTrace();
		}
	}

	// Builds icon panel and populates it
	private void createIconPanel() {
		iconPanel = new IconPanel(this);

		Iterator i = userList.iterator();
		while (i.hasNext()) {
			iconPanel.add((User) i.next());
		}
	}

	private void createAdminIconPanel() {
		iconPanel = new IconPanel(this);
		try {

			java.util.List list = User.retrieve();
			if (list.size() == 0) {
				User.getDefaultAdmin();
				list = User.retrieve();
			}
			Iterator i = list.iterator();
			while (i.hasNext()) {
				User u = (User) i.next();
				if (u.isAdmin())
					iconPanel.add(u);
			}
		} catch (SQLException x) {
			x.printStackTrace();
		}
	}

	// Builds panel for controls
	private void createControlPanel() {
		controlPanel = new JPanel(new BorderLayout(8, 8));

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel pane = new JPanel(gbl);

		gbc.insets = new Insets(8, 8, 8, 8);

		JLabel l = new JLabel("Benutzer:");
		gbl.setConstraints(l, gbc);
		pane.add(l);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		//gbc..WEST
		userField = new javax.swing.JTextField(20);
		gbl.setConstraints(userField, gbc);
		pane.add(userField);

		gbc.gridwidth = 1;
		l = new JLabel("Passwort:");
		gbl.setConstraints(l, gbc);
		pane.add(l);
		
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		passwordField = new javax.swing.JPasswordField(20);
		gbl.setConstraints(passwordField, gbc);
		pane.add(passwordField);

		controlPanel.add(new JLabel(" "), BorderLayout.NORTH);
		controlPanel.add(pane, BorderLayout.WEST);

		pane = new JPanel(new GridLayout(2, 1, 18, 18));
		okButton = new JButton("Anmelden");
		//okButton.setBackground(new Color(255, 224, 0));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				validateUser();
			}
		});
		pane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Beenden");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				user = null;
				dispose();
				setVisible(false);
			}
		});
		pane.add(cancelButton);

		controlPanel.add(pane, BorderLayout.EAST);

		userField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				enableOkButton();
			}
			public void removeUpdate(DocumentEvent e) {
				enableOkButton();
			}
			public void changedUpdate(DocumentEvent e) {
				enableOkButton();
			}
		});
		enableOkButton();
	}

	/**
	 * Returns the selected user by the dialog.
	 *
	 * @return the selected user by the dialog.
	 */
	public User getUser() {
		return user;
	}

	// Validates the input.
	// This method ends the dialog, if the input is correct or the user
	// cancels a retry.
	private void validateUser() {
		try {
			String inputUser = userField.getText().trim();
			String inputPass = new String(passwordField.getPassword());
			Iterator i = User.retrieve().iterator();
			while (i.hasNext()) {
				User u = (User) i.next();
				if (u.getName().toLowerCase().equals(inputUser.toLowerCase())
					&& u.getPassword().equals(inputPass)) {
					user = u;
				}
			}
		} catch (SQLException x) {
			x.printStackTrace();
		}

		if (user == null) {
			int r =
				JOptionPane.showConfirmDialog(
					getContentPane(),
					"Der Benutzer oder das Passwort sind ungültig.\n"
						+ "Soll die Eingabe wiederholt werden?",
					"Achtung",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (r != JOptionPane.YES_OPTION) {
				dispose();
				setVisible(false);
			}
		} else {
			dispose();
			setVisible(false);
		}

		setFocusTo(passwordField);
	}

	private User findUserByName(String s) {
		String userInput = s.trim().toLowerCase();
		if (userInput.length() > 0) {
			Iterator it = userList.iterator();

			while (it.hasNext()) {
				User u = (User) it.next();

				if (u.getName().toLowerCase().equals(userInput)) {
					return u;
				}
			}
		}

		return null;
	}

	private void enableOkButton() {
		boolean match = false;
		User u = findUserByName(userField.getText());
		iconPanel.selectUser(u);
		okButton.setEnabled(u != null);
	}

	// Called by iconPanel (ActionListener). This method fills the controls
	// from the selected icon and moves the focus to the password field.
	private void fillUserControls(User user) {
		userField.setText(user.getName());
		passwordField.setText("");

		setFocusTo(passwordField);
	}

	public void setFocusTo(final JComponent component) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				component.requestFocus();
			}
		});
	}

	/**
	 * The panel which is holding the user icons.
	 * <p>
	 * This class could not be inner because we use static members.
	 */
	private static class IconPanel extends JPanel implements ActionListener {
		private static Dimension buttonSize = null;

		public static Dimension getButtonSize() {
			return buttonSize;
		}

		private LoginDialog dialog;
		private ButtonGroup buttonGroup;

		public IconPanel(LoginDialog dialog) {
			super();

			this.dialog = dialog;

			buttonGroup = new ButtonGroup();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		}

		public void add(User user) {
			UserIcon icon = new UserIcon(user);
			UserButton b = new UserButton(user, icon);
			b.addActionListener(this);
			buttonGroup.add(b);

			super.add(b);

			if (buttonSize == null) {
				buttonSize = b.getPreferredSize();
			}
		}

		public void selectUser(User user) {
			for (int i = 0; i < getComponentCount(); i++) {
				UserButton b = (UserButton) getComponent(i);

				if (b.user == user) {
					b.setSelected(true);
					scrollRectToVisible(b.getBounds());
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			User user = ((UserButton) e.getSource()).getUser();
			dialog.fillUserControls(user);
		}
	}

	/**
	 * We subclass JButton because we do not need an extra storage
	 * for the users.
	 */
	private static class UserButton extends JToggleButton {
		private User user;

		public UserButton(User user, Icon icon) {
			super(user.getName(), icon);

			this.user = user;

			setVerticalTextPosition(AbstractButton.BOTTOM);
			setHorizontalTextPosition(AbstractButton.CENTER);
		}

		public User getUser() {
			return user;
		}
	}

	/**
	 * Simple implementation of an <tt>Icon</tt> which data source differs
	 * from an URL.
	 */
	public static class UserIcon implements Icon, ImageObserver {
		/**
		 * Fixed width of this icon.
		 */
		public static final int WIDTH = 128;

		/**
		 * Fixed height of this icon.
		 */
		public static final int HEIGHT = 160;

		private byte[] imageData;
		private Image image;

		// We use it to do a repaint if this ImageObserver notifies that
		// asynchronous loading is done.
		private Component component;

		public UserIcon(User user) {
			component = null;
			imageData = (byte[]) user.getImageData();
			if (imageData != null) {
				image = Toolkit.getDefaultToolkit().createImage(imageData);
			} else {
				image = LoginDialog.defaultUserImage;
			}
		}

		/**
		 * Paints this.
		 * <p>
		 * Fortunally, this interface method holds the <tt>Component</tt> so
		 * we can use it later for repaint().
		 */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			this.component = c;
			Graphics2D g2 = (Graphics2D) g;

			// This calls may invoke imageUpdate()
			int w = image.getWidth(this);
			int h = image.getHeight(this);

			// Scale image if necessary
			if (w != WIDTH || h != HEIGHT) {
				AffineTransform at =
					new AffineTransform(
						(double) WIDTH / w,
						0.0,
						0.0,
						(double) HEIGHT / h,
						(double) x,
						(double) y);
				g2.drawImage(image, at, this);
			} else {
				g2.drawImage(image, x, y, this);
			}
		}

		/**
		 * Returns the fixed height of this icon.
		 */
		public int getIconHeight() {
			return HEIGHT;
		}

		/**
		 * Returns the fixed width of this icon.
		 */
		public int getIconWidth() {
			return WIDTH;
		}

		/**
		 * Invoked by the image methods.
		 * <p>
		 * If the ALLBITS flag of the infoflags is done a repaint will follow.
		 */
		public boolean imageUpdate(
			Image img,
			int infoflags,
			int x,
			int y,
			int width,
			int height) {
			boolean ready = (infoflags & ALLBITS) != 0;
			if (ready && component != null) {
				component.repaint();
			}
			return !ready;
		}
	}
}