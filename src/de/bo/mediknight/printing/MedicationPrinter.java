package de.bo.mediknight.printing;

import java.io.File;

public class MedicationPrinter extends FOPrinter {

	public MedicationPrinter(String xmlFile, String xslFile) {
		super(xmlFile, xslFile);
	}
	
	public void print() {
		// TODO Auto-generated method stub

	}

	protected File transformTemplate(File template) {
		// TODO Auto-generated method stub
		return null;
	}

}
