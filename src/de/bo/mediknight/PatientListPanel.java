package de.bo.mediknight;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import de.bo.mediknight.domain.Patient;
import de.bo.mediknight.printing.Transform;
import de.bo.mediknight.widgets.JButton;
import de.bo.mediknight.widgets.JPanel;
import de.bo.mediknight.widgets.JScrollPane;
import de.bo.mediknight.widgets.JTable;

public class PatientListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private PatientListPresenter presenter;
	
	private JScrollPane tableScrollpane;
	
	private JTable patientTable;
	
	private JButton printButton;
	
	private JButton selectAll;
	
	private JButton deselectAll;
	
	private JTextField filternameField;
	
	private JButton searchButton;
	
	private JButton exportButton;
	
	private LinkedList<Vector<Comparable>> patients;
	
	public PatientListPanel() {
		createUI();
		connectUI();
		
		getData();
		updateTable();
		
		setVisible(true);
	}
	
	public void setPresenter(PatientListPresenter presenter) {
		this.presenter = presenter;
	}
	
	public void createUI() {
		this.setSize(this.getMaximumSize());
		setLayout(new BorderLayout());
		
		patientTable = new JTable() {
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int col) {
				if(col == 0)
					return true;
				else
					return false;
			}
		};
		
		patientTable.setModel(new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public Class getColumnClass(int columnIndex)
			{
				if (columnIndex == 0)
					return Boolean.class;
				else
					return String.class;
			}

			public boolean isCellEditable(int row, int col) {
				if(col > 0)
					return false;
				else
					return true;
			}
		});
		
		patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		tableScrollpane = new JScrollPane(patientTable);
		tableScrollpane.setSize(tableScrollpane.getMaximumSize());
		
		//Suchleiste bauen
		searchButton = new JButton("Suchen");
		filternameField = new JTextField(30);
		
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchPanel.add(new JLabel("Ausgabe einschränken:"));
		searchPanel.add(filternameField);
		searchPanel.add(searchButton);
		
		selectAll = new JButton("Alle auswählen");
		deselectAll = new JButton("Alle abwählen");
		
		exportButton = new JButton("PDF speichern");
		printButton = new JButton("Liste drucken");
		
		JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomButtonPanel.add(exportButton);
		bottomButtonPanel.add(printButton);
		
		JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topButtonPanel.add(selectAll);
		topButtonPanel.add(deselectAll);
		
		JPanel controlPanel = new JPanel(new GridLayout(3, 1, 0, 0));
		controlPanel.add(topButtonPanel, BorderLayout.CENTER);
		controlPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
		
		add(searchPanel, BorderLayout.NORTH);
		add(tableScrollpane, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);
	}
	
	public void connectUI() {
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printAction();
			}
		});
		
		ChangeListener checkboxListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateTable();
			}
		};
		
		deselectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < patients.size(); i++) {
					patients.get(i).set(0, Boolean.FALSE);
				}
				updateTable();
			}			
		});

		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < patients.size(); i++) {
					patients.get(i).set(0, Boolean.TRUE);
				}
				updateTable();
			}			
		});

		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getData();
				updateTable();
			}			
		});
		
		filternameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                getRootPane().setDefaultButton(searchButton);
            }			
		});
		
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setSelectedFile(new File("patientenliste.pdf"));
				
				int state = fileChooser.showSaveDialog(MainFrame.getApplication());
				
				if (state == JFileChooser.APPROVE_OPTION) {
					exportAction(fileChooser.getSelectedFile());
				}
			}
		});
	}

	public void getData() {
		patients = new LinkedList<Vector<Comparable>>();
		
		List patientData;
		try {
			patientData = Patient.retrieve(filternameField.getText());
			Collections.sort(patientData);

			for (int p = 0; p < patientData.size(); p++) {
				Vector<Comparable> v = new Vector<Comparable>();
				
				Patient patient = (Patient) patientData.get(p);
				
				v.add(new Boolean(false));
				
				v.add(patient.getAnrede());
				v.add(patient.getVorname());
				v.add(patient.getName());
				
				String address = patient.getAdresse1();
				if (!patient.getAdresse2().equals("")) {
					if (!address.equals("")) {
						address += ", ";
					}
					address += patient.getAdresse2();
				}
				if (!patient.getAdresse3().equals("")) {
					if (!address.equals("")) {
						address += ", ";
					}
					address += patient.getAdresse3();
				}
				v.add(address);
				
				v.add(patient.getTelefonPrivat());
				v.add(patient.getTelefonArbeit());
				v.add(patient.getHandy());					

				v.add(patient.getFax());
				v.add(patient.getEmail());
				
				patients.addLast(v);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void updateTable() {
		MainFrame.getApplication().setWaitCursor();
		
		//Tabellenueberschriften
		DefaultTableModel tableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public Class getColumnClass(int columnIndex) {
				if (columnIndex == 0) {
					return Boolean.class;
				} else {
					return String.class;
				}
			}				
		};
		
		String[] cols = new String[] {"Ausgewählt",
									  "Anrede",
									  "Vorname",
									  "Name",
									  "Adresse",
									  "Telefon (Privat)",
									  "Telefon (Arbeit)",
									  "Handy",
									  "Fax",
									  "Email"};
		
		tableModel.setColumnIdentifiers(cols);
		
		for (int i = 0; i < patients.size(); i++) {
			tableModel.addRow(patients.get(i));
		}
		
		patientTable.setModel(tableModel);
		MainFrame.getApplication().setDefaultCursor();		
	}
	
	private void printAction() {
		presenter.getModel().printList(((DefaultTableModel) patientTable.getModel()).getDataVector());		
	}
	
	private void exportAction(File file) {
		presenter.getModel().exportPdf(((DefaultTableModel) patientTable.getModel()).getDataVector(), file);		
	}
	
	public Dimension getMinimumSize() {
		return this.getMaximumSize();
	}
	
	public Dimension getPreferredSize() {
		return this.getMaximumSize();
	}
}
