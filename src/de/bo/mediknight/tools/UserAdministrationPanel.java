package de.bo.mediknight.tools;

import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import de.bo.mediknight.domain.*;
import java.net.URL;
import java.io.*;
import de.bo.swing.*;
import de.bo.mediknight.*;

public class UserAdministrationPanel extends JPanel implements ChangeListener {

    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel1 = new JPanel();
    JLabel comboBoxLbl = new JLabel();
    JComboBox<User> userComboBox = new JComboBox<User>();
    FlowLayout flowLayout1 = new FlowLayout();

    UserAdministrationPresenter presenter;
    User user;
    ItemListener lst;
    ImageIcon imageIcon;
    byte[] defaultImageData;

    JPanel southPanel = new JPanel();
    JPanel userInformationPanel = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    FlowLayout flowLayout2 = new FlowLayout();
    JPanel buttonPanel = new JPanel();
    GridLayout gridLayout1 = new GridLayout();
    JButton deleteBtn = new JButton();
    JButton newBtn = new JButton();
    JButton passwortBtn = new JButton();
    JButton picBtn = new JButton();
    JLabel picLbl = new JLabel();
    javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    javax.swing.JPanel dummyPanel = new javax.swing.JPanel();
    JLabel userLbl = new JLabel();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    javax.swing.JTextField userTF = new javax.swing.JTextField();
    javax.swing.JPanel emptyPanel = new javax.swing.JPanel();


    public UserAdministrationPanel() {
        jbInit();
    }

    public void setPresenter(UserAdministrationPresenter presenter) {
	this.presenter = presenter;
	presenter.getModel().addChangeListener( this );
	boInit();
    }

    public void stateChanged( ChangeEvent e) {
	update();
    }



    private void jbInit() {
        this.setLayout(borderLayout1);
        comboBoxLbl.setText("Bitte wählen Sie einen User aus: ");
        jPanel1.setLayout(flowLayout1);
        flowLayout1.setAlignment(0);
        userInformationPanel.setLayout(gridBagLayout1);
        southPanel.setLayout(flowLayout2);
        buttonPanel.setLayout(gridLayout1);
        deleteBtn.setText("Löschen");
        flowLayout2.setAlignment(2);
        flowLayout2.setHgap(0);
        newBtn.setText("Neu");
        gridLayout1.setHgap(6);
        passwortBtn.setText("Paßwort ändern");
        picBtn.setText("Bild ändern");
        userLbl.setText("Anwendername:");
        jPanel2.setLayout(gridBagLayout2);
        userTF.setColumns(20);
        this.setOpaque(false);
        jPanel1.setOpaque(false);
        southPanel.setOpaque(false);
        buttonPanel.setOpaque(false);
        userInformationPanel.setOpaque(false);
        jPanel2.setOpaque(false);
        dummyPanel.setOpaque(false);
        emptyPanel.setOpaque(false);
        this.add(jPanel1, BorderLayout.NORTH);
        jPanel1.add(comboBoxLbl, null);
        jPanel1.add(userComboBox, null);
        this.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(buttonPanel, null);
        buttonPanel.add(passwortBtn, null);
        buttonPanel.add(picBtn, null);
        buttonPanel.add(deleteBtn, null);
        buttonPanel.add(newBtn, null);
        this.add(userInformationPanel, BorderLayout.CENTER);
        userInformationPanel.add(picLbl, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
        userInformationPanel.add(jPanel2, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
        jPanel2.add(userLbl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 6), 0, 0));
        jPanel2.add(userTF, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        userInformationPanel.add(dummyPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        userInformationPanel.add(emptyPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void boInit() {
	picLbl.setPreferredSize(new Dimension(128, 160));

	lst = new ItemListener () {
	    public void itemStateChanged( ItemEvent e ) {
		setUser((User)e.getItem());
	    }
	};

    userComboBox.setModel(new DefaultComboBoxModel<User>(presenter.getModel().getUsers()));
	userComboBox.addItemListener( lst );
	setUser( (User) presenter.getModel().getUser( 0 ) );

	passwortBtn.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) {
		PasswordDialog dialog = new PasswordDialog( MainTool.getFrame() );
	    }
	});

	userTF.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) {
		setUserName();
	    }
	});

	userTF.addFocusListener( new FocusListener() {
	    public void focusGained( FocusEvent e) { }
	    public void focusLost ( FocusEvent e) {
		setUserName();
	    }
	});

	picBtn.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) {
		loadPicture();
	    }
	});

	newBtn.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) {
		newUser();
	    }
	});

	deleteBtn.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) {
		deleteUser();
	    }
	});

	SwingUtilities.invokeLater( new Runnable() {
	    public void run() {
		getRootPane().setDefaultButton( passwortBtn );
	    }
	});

    }

    public void save() {
	setUserName();
    }

    private void loadPicture() {
	ImageIcon newIcon = null;

	JFileChooser fc = new JFileChooser();
	fc.setMultiSelectionEnabled( false );
        fc.setCurrentDirectory(MainFrame.getAppLocation());
	fc.addChoosableFileFilter(new SimpleFileFilter(new String[] {"jpg","gif"}, "Grafikdateien (*.jpg, *.gif)"));
	int r = fc.showOpenDialog( this );

	if (r == JFileChooser.CANCEL_OPTION) {
	    return;
	}
	try {
	    byte[] imageData = null;
	    File file = fc.getSelectedFile();
	    imageData = new byte[(int)file.length()];
	    FileInputStream fis = new FileInputStream(file);
	    fis.read(imageData);
	    fis.close();

	    if ( imageData != null )
		newIcon = new ImageIcon(imageData);

	    PictureDialog dialog = new PictureDialog(MainTool.getFrame(), imageIcon, newIcon);

	    if (dialog.isOK()) {
		user.setImageData( imageData );
		presenter.getModel().saveUser( user );
	    }

	} catch (FileNotFoundException fnfe) {
	    fnfe.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}

    }

    private void update() {
	userComboBox.setModel(new DefaultComboBoxModel<User>(presenter.getModel().getUsers()));
	if (user != null) {
	    userComboBox.removeItemListener( lst );
	    userComboBox.setSelectedItem( user );
	    userComboBox.addItemListener( lst );
	} else {
	    userComboBox.removeItemListener( lst );
	    userComboBox.setSelectedItem( presenter.getModel().getUser( 0 ) );
	    userComboBox.addItemListener( lst );
	}
	setUser( user );
    }

    private void setUserName() {
	if ( userTF.getText().length() > 0) {
	    if (checkUserName( userTF.getText() )) {
		user.setName( userTF.getText() );
		presenter.getModel().saveUser( user );
	    } else {
		SwingUtilities.invokeLater( new Runnable() {
		    public void run() {
			Object[] msg = new Object[1];

			msg[0] = new JLabel("<html><font face=dialog>" +
					    "Der eingebene Name wird schon von einem anderen Anwender verwendet!"
					    );
			String[] bt = { "OK" } ;

			JOptionPane.showOptionDialog(UserAdministrationPanel.this, msg, "Fehlerhafte Eingabe....",
			    JOptionPane.DEFAULT_OPTION,
			    JOptionPane.INFORMATION_MESSAGE,
			    null,
			    bt,bt[0]);
		    }
		});
		userTF.setText( user.getName() );
	    }
	}
    }

    private void setUser(User user) {
	if (user == null) {
	    user = presenter.getModel().getUser( 0 );
	}
	this.user = user;

        byte[] imageData = (byte[])user.getImageData();
        if ( imageData != null )
            imageIcon = new ImageIcon(imageData);
        else {
            imageIcon = getDefaultImageIcon();
        }

	picLbl.setIcon( imageIcon );
	picLbl.setBorder( BorderFactory.createLineBorder( Color.black ));

	userTF.setText( user.getName() );
    }

    private ImageIcon getDefaultImageIcon() {
	URL url = UserAdministrationPanel.class.getClassLoader().
	    getResource("de/bo/mediknight/resources/defaultuser.gif");

	Image defaultUserImage = Toolkit.getDefaultToolkit().createImage(url);
	return new ImageIcon( defaultUserImage );
    }

    private void deleteUser() {
        int r = JOptionPane.showConfirmDialog( null,
            "Den Anwender " + user.getName() + " wirklich löschen ?","Anwender löschen",
            JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

        if ( r == JOptionPane.YES_OPTION ) {
	    presenter.getModel().deleteUser( user );
	    user = null;
	    update();
        }

    }

    private void newUser() {
	user = new User();
	user.setName( getName( "Neuer Anwender" ));
	user.setImageData(null);
	user.setPassword("");
	presenter.getModel().saveNewUser( user );
    }

    private String getName(String s) {
	int no = 1;
	while (!checkName( s )) {
	    s = "Neuer Anwender" + no++;
	}
	return s;
    }

    private boolean checkName(String s) {
	for (int i = 0; i < presenter.getModel().getUsers().length; i++)
	    if ( s.equals(presenter.getModel().getUser(i).getName())) {
		return false;
	    }
	return true;
    }

    private boolean checkUserName(String s) {
	for (int i = 0; i < presenter.getModel().getUsers().length; i++)
	    if (( s.equals(presenter.getModel().getUser(i).getName())) && (!(s.equals(user.getName())))) {
		return false;
	    }
	return true;
    }



    private class PasswordDialog extends JDialog {
        private static final long serialVersionUID = 1L;
        
        JPanel topPanel = new JPanel();
        JPanel downPanel = new JPanel();
        JPasswordField newTF = new JPasswordField( 10 );
        JPasswordField verTF = new JPasswordField( 10 );
        JButton cancelBtn = new JButton("Abbrechen");
        JButton okBtn = new JButton("Ändern");

        public PasswordDialog( JFrame frame ) {
	    super(frame , "Paßwort ändern", true);
            initializeComponents();
            pack();
	    setResizable(false);

	    if(frame != null) setLocationRelativeTo( frame.getContentPane() );
	    show();

        }

        void initializeComponents() {
            Container pane = getContentPane();
            pane.setLayout(new BorderLayout());

	    JPanel inputPanel = new JPanel();

	    FlexGridLayout fgl = new FlexGridLayout(2, 2);
	    fgl.setHgap( 6 );
	    fgl.setVgap( 6 );

	    inputPanel.setLayout( fgl );

	    inputPanel.add(new JLabel("Neues Paßwort:"), new FlexGridConstraints(-1, 0, FlexGridConstraints.W));
	    inputPanel.add(newTF, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
	    inputPanel.add(new JLabel("Wiederholung:"), new FlexGridConstraints(-1, 0, FlexGridConstraints.W));
	    inputPanel.add(verTF, new FlexGridConstraints(0, 0, FlexGridConstraints.W));

            topPanel.add(inputPanel);
            downPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            downPanel.add(okBtn);
            downPanel.add(cancelBtn);

            cancelBtn.addActionListener(new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        dispose();
                    }
            } );

            okBtn.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
		    if ( newTF.getPassword().length < 1 ||
			verTF.getPassword().length < 1) {
			    JOptionPane.showMessageDialog( null,
				"Sie müssen beide Felder ausfüllen!","Fehlendes Paßwort!",
				JOptionPane.INFORMATION_MESSAGE);
			    focus();
			    return;
		    }

		    String newPW = new String(newTF.getPassword());
		    String verPW = new String(verTF.getPassword());

		    if (!newPW.equals( verPW )) {
			JOptionPane.showMessageDialog( null,
			    "Die beiden Paßwörter stimmen nicht überein!","Fehlende Übereinstimmung!",
			    JOptionPane.INFORMATION_MESSAGE);
			focus();
			return;
		    }

		    user.setPassword( newPW );
		    presenter.getModel().saveUser( user );
		    dispose();
                }
            } );

            pane.add(topPanel, BorderLayout.NORTH);
            pane.add(downPanel, BorderLayout.SOUTH);
	    focus();
        }

	private void focus() {
            Runnable runnable = new Runnable() {
                public void run() {
                    newTF.requestFocus();
                }
            };
            SwingUtilities.invokeLater(runnable);
	}
    }

    private class PictureDialog extends JDialog {
        private static final long serialVersionUID = 1L;
        
        JPanel topPanel = new JPanel();
        JPanel downPanel = new JPanel();
	JLabel oldPic = new JLabel();
	JLabel newPic = new JLabel();
        JButton cancelBtn = new JButton("Abbrechen");
        JButton okBtn = new JButton("Ändern");

	boolean isOK;

        public PictureDialog( JFrame frame, Icon oldIcon, Icon newIcon ) {
	    super(frame , "Bild austauschen!", true);
	    isOK = false;
            initializeComponents(oldIcon, newIcon );
            pack();
	    setResizable(false);

	    if(frame != null) setLocationRelativeTo( frame.getContentPane() );
	    show();

        }

        void initializeComponents(Icon oldIcon, Icon newIcon) {
            Container pane = getContentPane();
            pane.setLayout(new BorderLayout());

	    JPanel inputPanel = new JPanel();

	    FlexGridLayout fgl = new FlexGridLayout(2, 2);
	    fgl.setHgap( 6 );
	    fgl.setVgap( 6 );

	    oldPic.setIcon( oldIcon );
	    newPic.setIcon( newIcon );

	    oldPic.setPreferredSize(new Dimension(128, 160));
	    newPic.setPreferredSize(new Dimension(128, 160));

	    oldPic.setBorder( BorderFactory.createLineBorder( Color.black ));
	    newPic.setBorder( BorderFactory.createLineBorder( Color.black ));

	    inputPanel.setLayout( fgl );

	    inputPanel.add(new JLabel("Altes Bild:"), new FlexGridConstraints(-1, 0, FlexGridConstraints.W));
	    inputPanel.add(new JLabel("Neues Bild:"), new FlexGridConstraints(-1, 0, FlexGridConstraints.W));
	    inputPanel.add(oldPic , new FlexGridConstraints(0, 0, FlexGridConstraints.W));
	    inputPanel.add(newPic , new FlexGridConstraints(0, 0, FlexGridConstraints.W));

            topPanel.add(inputPanel);
            downPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            downPanel.add(okBtn);
            downPanel.add(cancelBtn);

            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dispose();
                }
            } );

            okBtn.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
		    isOK = true;
		    dispose();
                }
            } );

            pane.add(topPanel, BorderLayout.NORTH);
            pane.add(downPanel, BorderLayout.SOUTH);
	    focus();
        }

	public boolean isOK() {
	    return isOK;
	}

	private void focus() {
            Runnable runnable = new Runnable() {
                public void run() {
                    okBtn.requestFocus();
                }
            };
            SwingUtilities.invokeLater(runnable);
	}
    }

    class SimpleFileFilter extends FileFilter {
	String[] extensions;
	String description;

	public SimpleFileFilter(String[] extensions, String description) {
	    this.extensions = extensions;
	    this.description = description;
	}

	public String getDescription() {
	    return this.description;
	}

	public boolean accept (File file) {
	    if (file.isDirectory())
		return true;
	    else {
		String filename = file.getName().toLowerCase();
		for (int i = 0; i < extensions.length; i++) {
		    if (filename.endsWith( extensions[i] ))
			return true;
		}
		return false;
	    }
	}
    }


}

