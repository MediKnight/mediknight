package de.bo.mediknight.tools;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;
import de.bo.mediknight.*;

import de.bo.mediknight.MainFrame;

import de.bo.mediknight.widgets.*;

public class MainTool extends JFrame {

	JGradientPanel buttonPanel;
	JToggleButton printBtn = new JToggleButton();
	JToggleButton masterDataBtn = new JToggleButton();
	JToggleButton userBtn = new JToggleButton();
	JToggleButton mediBtn = new JToggleButton();
	JToggleButton colorBtn = new JToggleButton();
	JToggleButton quitButton = new JToggleButton();
	JPanel workspace = new JPanel();
	JLabel title;

	private Presenter currentPresenter;

	private static JFrame frame;

	private static final String BTN_PRINT = "Drucktexte";
	private static final String BTN_DATA = "Rechnung";
	private static final String BTN_USER = "Anwender";
	private static final String BTN_MEDI = "Verordnung";
	private static final String BTN_QUIT = "Beenden";
	private static final String BTN_COLOR = "Farbwerte";
	static Color c1, c2;

	public MainTool() {
		setTitle("Mediknight - Administration");
		setSize(800, 600);
		createUI();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame = this;
	}

	public static void main(String[] args) {

		try {
			MainFrame.initLocation();
			MainFrame.initProperties();
			MainFrame.initTracer();
			MainFrame.initDB();
			MediknightTheme.install(MainFrame.getProperties());

		} catch (Exception e) {
			e.printStackTrace();
		}

		MainTool mt = new MainTool();
		try {
			LoginDialog ld = new LoginDialog(getFrame(), true);
			ld.show();
			if (ld.getUser() == null)
				throw new IllegalAccessException("Anwender hat sich nicht authentifiziert!");
		} catch (IllegalAccessException iax) {
			System.exit(1);
		}

		mt.show();
	}

	public static JFrame getFrame() {
		return frame;
	}

	private void createUI() {
		c1 = UIManager.getColor("effect");
		if (c1 == null)
			c1 = Color.black;
		c2 = Color.white;

		setBackground(c2);

		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());

		title = new JLabel("Mediknight - Administration");
		title.setBackground(c1);
		title.setForeground(c2);
		title.setOpaque(true);
		title.setFont(title.getFont().deriveFont(20.0f));

		JPanel filler = new JPanel();
		filler.setBackground(c1);

		workspace = new JPanel(new BorderLayout());
		workspace.setBackground(c2);
		workspace.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = c.gridwidth = c.gridheight;
		c.fill = GridBagConstraints.BOTH;
		pane.add(filler, c);
		c.gridx++;
		pane.add(title, c);
		c.gridy++;
		c.weightx = c.weighty = 1d;
		pane.add(workspace, c);
		c.gridx--;
		c.weightx = 0d;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.NORTH;
		// pane.add(navigator, c);
		pane.add(createButtonPanel(), c);

	}

	private JGradientPanel createButtonPanel() {
		buttonPanel = new JGradientPanel();
		buttonPanel.setLayout(new FlexGridLayout(0, 1, 5, 5));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setGradientColor(c1);
		buttonPanel.setBackground(c2);

		printBtn.setText(BTN_PRINT);
		masterDataBtn.setText(BTN_DATA);
		userBtn.setText(BTN_USER);
		mediBtn.setText(BTN_MEDI);
		quitButton.setText(BTN_QUIT);
		colorBtn.setText(BTN_COLOR);

		printBtn.setName(BTN_PRINT);
		masterDataBtn.setName(BTN_DATA);
		userBtn.setName(BTN_USER);
		mediBtn.setName(BTN_MEDI);
		quitButton.setName(BTN_QUIT);
		colorBtn.setName(BTN_COLOR);

		printBtn.setMargin(new Insets(2, 0, 2, 0));
		masterDataBtn.setMargin(new Insets(2, 0, 2, 0));
		mediBtn.setMargin(new Insets(2, 0, 2, 0));
		userBtn.setMargin(new Insets(2, 0, 2, 0));
		quitButton.setMargin(new Insets(2, 0, 2, 0));
		colorBtn.setMargin(new Insets(2, 0, 2, 0));

		mediBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				medicationDataSettings();
			}
		});

		printBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printSettings();
			}
		});

		colorBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				colorSettings();
			}
		});

		masterDataBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				masterDataSettings();
			}
		});

		userBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userSettings();
			}
		});

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		buttonPanel.add(printBtn, "10,0,c");
		buttonPanel.add(masterDataBtn, "10,0,c");
		buttonPanel.add(mediBtn, "10,0,c");
		buttonPanel.add(userBtn, "10,0,c");
		buttonPanel.add(colorBtn, "10,0,c");
		buttonPanel.add(quitButton, "10,0,c");

		return buttonPanel;
	}

	private void printSettings() {
		setVisibleNavigatorButton(BTN_DATA, true, false);
		setVisibleNavigatorButton(BTN_PRINT, true, true);
		setVisibleNavigatorButton(BTN_COLOR, true, false);
		setVisibleNavigatorButton(BTN_MEDI, true, false);
		setVisibleNavigatorButton(BTN_USER, true, false);
		setHeaderPanel("Druckbausteine");
		PrintSettingsModel model = new PrintSettingsModel();
		setPane(new PrintSettingsPresenter(model));
	}

	private void colorSettings() {
		setVisibleNavigatorButton(BTN_DATA, true, false);
		setVisibleNavigatorButton(BTN_PRINT, true, false);
		setVisibleNavigatorButton(BTN_COLOR, true, true);
		setVisibleNavigatorButton(BTN_MEDI, true, false);
		setVisibleNavigatorButton(BTN_USER, true, false);
		setHeaderPanel("Farbwerte");
		setPane(new ColorPresenter());
	}

	private void masterDataSettings() {
		setVisibleNavigatorButton(BTN_DATA, true, true);
		setVisibleNavigatorButton(BTN_COLOR, true, false);
		setVisibleNavigatorButton(BTN_PRINT, true, false);
		setVisibleNavigatorButton(BTN_MEDI, true, false);
		setVisibleNavigatorButton(BTN_USER, true, false);
		setHeaderPanel("Stammdatenpflege");
		MasterDataSupportModel model = new MasterDataSupportModel();
		setPane(new MasterDataSupportPresenter(model));
	}

	private void userSettings() {
		setVisibleNavigatorButton(BTN_DATA, true, false);
		setVisibleNavigatorButton(BTN_COLOR, true, false);
		setVisibleNavigatorButton(BTN_PRINT, true, false);
		setVisibleNavigatorButton(BTN_USER, true, true);
		setVisibleNavigatorButton(BTN_MEDI, true, false);
		setHeaderPanel("Anwenderverwaltung");
		UserAdministrationModel model = new UserAdministrationModel();
		setPane(new UserAdministrationPresenter(model));
	}

	private void medicationDataSettings() {
		setVisibleNavigatorButton(BTN_DATA, true, false);
		setVisibleNavigatorButton(BTN_PRINT, true, false);
		setVisibleNavigatorButton(BTN_COLOR, true, false);
		setVisibleNavigatorButton(BTN_USER, true, false);
		setVisibleNavigatorButton(BTN_MEDI, true, true);
		setHeaderPanel("Verodnungsstammdaten");
		MedicationSupportModel model = new MedicationSupportModel();
		setPane(new MedicationSupportPresenter(model));
	}

	private void setHeaderPanel(String title) {
		this.title.setText(title);
	}

	public void setVisibleNavigatorButton(
		String name,
		boolean visible,
		boolean selected) {

		for (int i = 0; i < buttonPanel.getComponentCount(); i++) {
			Component c = buttonPanel.getComponent(i);

			if (c.getName() != null && c.getName().equals(name)) {
				c.setVisible(visible);
				if (c instanceof JToggleButton) {
					((JToggleButton) c).setSelected(selected);
				}
			}
			repaint();
		}
	}

	public void setPane(Presenter presenter) {
		/*        if ( tracer != null )
		            tracer.trace(DEBUG,"calling setPane("+presenter.getClass().getName()+")");*/

		if (currentPresenter instanceof Commitable) {
			((Commitable) currentPresenter).commit();
		}
		currentPresenter = presenter;
		/** @todo This is pretty stupid, but should work for now. Improve! */
		/*      if( currentPresenter != null && currentPresenter.getClass() != presenter.getClass() ) {
		          backAction = currentAction;
		      }
		*/

		if (workspace.getComponentCount() > 0) {
			Component pane = workspace.getComponent(0);
			workspace.remove(pane);
		}
		workspace.add(presenter.createView());
		workspace.revalidate();
		workspace.repaint();

		//  enableEditing(true);
	}

	public void quit() {
		int action =
			JOptionPane.showConfirmDialog(
				this,
				"Alle Daten wurden gespeichert.\nMöchten sie das Programm jetzt beenden?",
				"Mediknight",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (action == JOptionPane.NO_OPTION) {
			setVisibleNavigatorButton(BTN_QUIT, true, false);
			return;
		}

		if (currentPresenter instanceof Commitable)
			 ((Commitable) currentPresenter).commit();

		System.exit(0);
	}

}