package de.bo.mediknight;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JOptionPane;

import de.bo.mediknight.printing.FOPrinter;
import de.bo.mediknight.widgets.YinYangDialog;

public class PatientListModel {
	FOPrinter fop;
	
	public PatientListModel() {
		Properties props = MainFrame.getProperties();
		
		fop = new FOPrinter(props.getProperty("patients.xml"), 
				props.getProperty("patients.xsl"));		
	}
	
	public void collectData(Vector patients) {
		//Patienten einfügen
		for (int p = 0; p < patients.size(); p++) {
			Vector patient = (Vector) patients.get(p);
			
			if (patient.get(0).equals(Boolean.FALSE)) {
				continue;
			}
			
			fop.addTagToFather("Patient", "", "Patienten");
			
			fop.addTag("Vorname", (String) patient.get(2), "Patienten");
			fop.addTag("Name", (String) patient.get(3), "Patienten");
			
			String[] adresse = ((String) patient.get(4)).split(",", 3);
			for (int i = 0; i < adresse.length; i++) {
				fop.addTag("Adresse" + (i + 1), adresse[i], "Patienten");				
			}
									
			fop.addTag("Privat", (String) patient.get(5), "Patienten");
			fop.addTag("Arbeit", (String) patient.get(6), "Patienten");
			fop.addTag("Handy", (String) patient.get(7), "Patienten");
		}		
	}
	
	public void printList(final Vector patients) {
		final YinYangDialog d =
		    new YinYangDialog(
		    		JOptionPane.getFrameForComponent(MainFrame.getApplication()),
		    		MainFrame.NAME);

		d.setStatusText("Drucke ...");
		d.run(new Runnable() {
			public void run() {
				collectData(patients);
				try {
					fop.print();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});
	}
	
	public void exportPdf(final Vector patients, final File selectedFile) {
		final YinYangDialog d =
		    new YinYangDialog(
		    		JOptionPane.getFrameForComponent(MainFrame.getApplication()),
		    		MainFrame.NAME);

		d.setStatusText("Exportiere ...");
		d.run(new Runnable() {
			public void run() {
				collectData(patients);
				try {
					fop.exportToFile(selectedFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});
	}
}
