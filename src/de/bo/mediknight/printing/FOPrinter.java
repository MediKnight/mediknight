package de.bo.mediknight.printing;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.bo.mediknight.MainFrame;


/**
 * Diese Klasse wird benutzt, um xml-Dateien in das pdf-Format umzuwandeln und
 * anschlie�end auszudrucken. Man erzeugt ein FOPrinter-Objekt und f�gt diesem
 * dann eine Template-Datei im xml-Format und eine xsl-Datei, die die 
 * Umwandlung von xml zu pdf vornimmt bei. Anschie�end kann man in der 
 * Template-Datei die Muster ersetzen oder neue tags hinzuf�gen. 
 * 
 * @author Benjamin Schnoor (bs@baltic-online.de)
 * @version 3.0 
 */
public class FOPrinter {
	private final String DIR;
	private final String HOME_DIR;
	private File xmlFile;
	private File xslFile;
	private File templateFile;
	private HashMap data; // f�r die Ersetzung von Templates
	private ArrayList newElements; // f�r das Einf�gen neuer Tags 
	private boolean wasPrinted;
		
	/**
	 * Erzeugt ein neues FOPrinter-Objekt.
	 * 
	 * @param xmlFileName Name der Template-Datei
	 * @param xslFileName Name der xsl-Datei
	 */
	public FOPrinter(String xmlFileName, String xslFileName) {
		
	    	DIR = System.getProperty("user.dir") + 
	    	      System.getProperty("file.separator") + 
	    	      MainFrame.getProperties().getProperty("outdir");
	    	
	    	HOME_DIR = System.getProperty("user.home") +
	    		   System.getProperty("file.separator") + 
	    		   MainFrame.getProperties().getProperty("outdir");
	    	
	    	new File(HOME_DIR).mkdir();
		templateFile = new File(xmlFileName);
		xmlFile = new File(HOME_DIR, "output.xml");
		
		xslFile = new File(xslFileName);
		data = new HashMap();
		newElements = new ArrayList();
		wasPrinted = false;
	}
	
	/**
	 * �ndert die Datei, die als Vorlage dient.
	 * 
	 * @param fileName die neue Datei
	 */
	public void setXMLFile(String fileName) {
		templateFile = new File(DIR, fileName); 
	}
	
	/**
	 * �ndert die Datei, die den xsl-Code hat.
	 * 
	 * @param fileName die neue Datei
	 */
	public void setXSLFile(String fileName) {
		xslFile = new File(DIR, fileName);
	}	
	
	/**
	 * F�gt einen neues Template zur Liste hinzu. Die Templates werden erst
	 * eingesetzt, wenn die Datei gedruckt werden soll.
	 * 
	 * @param key Name des Templates
	 * @param value Inhalt des Templates
	 */
	public void addData(String key, String value) {
		data.put(key, value);
	}
	
	/**
	 * F�gt einen neuen Tag in die Merkliste ein. Die Tags werden erst
	 * hinzugef�gt, wenn die Datei gedruckt werden soll.
	 * 
	 * @param tag Name des neuen Tags
	 * @param value Inhalt des neuen Tags
	 * @param parent Name des Vaters
	 */
	public void addTagToFather(String tag, String value, String parent) {
		String[] str = {"false", tag, value, parent};
		newElements.add(str);
	}
	
	/**
	 * F�gt einen neuen Tag in die Merkliste ein. Der Tag zu dem der neue
	 * Tag in Bezug steht ist nicht der direkte Vorg�nger, sondern der 
	 * Gro�vater. Vom Gro�vater wird das letzte Kind-Element genommen
	 * und da wird dann der neue Tag eingef�gt.
	 * 
	 * @param tag Name des neuen Tags
	 * @param value Inhalt des neuen Tags
	 * @param grandfather Name des Gro�vater-Tags
	 */
	public void addTag(String tag, String value, String grandfather) {
		String[] str = {"true", tag, value, grandfather};
		newElements.add(str);
	}
	
	/**
	 * Druckt die xml-Seite aus. Erst jetzt werden alle hinzugef�gten Templates
	 * und neuen Tags in die Template-Datei eingef�gt.
	 * 
	 * @return
	 * 			true, wenn der Druckauftrag gesendet wurde, false sonst
	 * 
	 * @throws IOException wenn es Probleme mit den Dateien gibt
	 */
	public boolean print() throws IOException {
		
		// wenn schon einmal dieses Objekt gedruckt wurde, dann nicht
		// nocheinmal die Werte einsetzen
		if(!wasPrinted) {		
			insertValues();
			addElements();
		}
		wasPrinted = true;
		
		File file = Transform.xml2pdf(xmlFile, xslFile, HOME_DIR);

		FileInputStream fis = new FileInputStream(file);

		DocAttributeSet set = new HashDocAttributeSet();
		set.add(MediaSizeName.ISO_A4);
		
		Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.PDF, set);

		PrintService printer = PrinterJob.lookupPrintServices()[0];
		
		if (printer != null) {
			DocPrintJob job = printer.createPrintJob();
			try {
			    job.print(doc, null);
			    fis.close();
			    return true;
			} catch (PrintException e) {
			    e.printStackTrace();
			    fis.close();
			    return false;
			}
		}
		else
			fis.close();
		return false;
	}
	
	/**
	 * Ersetzt die Templates in der Templates-Datei
	 * @throws IOException
	 */
	private void insertValues() 
	throws IOException {		
		
		FileInputStream fis = new FileInputStream(templateFile);
		FileOutputStream fos = new FileOutputStream(xmlFile);
		boolean end = false;
		
		while(!end) {
			int read = fis.read();
			
			if(read == -1)
				end = true;
			else {
				if(read == 36) {

					String str = "";
					read= fis.read();
					while(read != 36) {
						str += String.valueOf(Character.toChars(read));
						read = fis.read();
					}
					
					String value = (String) data.get(str);
					if(value != null)
						fos.write(value.getBytes());
				}
				else {			
					fos.write(read);				
				}			
			}
		}
		fis.close();
		fos.close();
	}
	
	/**
	 * F�gt alle gespeicherten neuen Tags in die Output-Datei ein.
	 * Die Methode darf nur aufgerufen werden, wenn gedruckt werden soll.
	 * Damit vermeidet man unvorhergesehenes Verhalten.
	 */
	private void addElements() {
		for(int i=0; i<newElements.size(); i++) {
			String[] str = (String[]) newElements.get(i);
			if(str[0].equals("true"))
				addToLast(str[1], str[2], str[3]);
			else
				addElement(str[1], str[2], str[3]);			
		}
	}
	
	/**
	 * F�gt den neuen Tag in die Output-Datei ein
	 * @param tag Name des neuen Tags
	 * @param value Inhalt des neuen Tags
	 * @param parent Name des Vaters
	 */
	private void addElement(String tag, String value, String parent) {
		try {
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fac.newDocumentBuilder();		
			Document doc = builder.parse(xmlFile); 
						
			NodeList par = doc.getElementsByTagName(parent);
			Node node = par.item(0);
			Element e = doc.createElement(tag);
			e.setTextContent(value);
			
			node.appendChild(e);		
			TransformerFactory.newInstance().newTransformer().transform(
	                new DOMSource(doc), 
	                new StreamResult(new FileOutputStream(xmlFile)));
			
		}
		catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	/**
	 * F�gt einen neuen Tag in die Output-Datei ein. Der Tag zu dem der neue
	 * Tag in Bezug steht ist nicht der direkte Vorg�nger, sondern der 
	 * Gro�vater. Vom Gro�vater wird das letzte Kind-Element genommen
	 * und da wird dann der neue Tag eingef�gt.
	 * @param tag Name des neuen Tags
	 * @param value Inhalt des neuen Tags
	 * @param grandfather Name des Gro�vaters
	 */
	private void addToLast(String tag, String value, String grandfather) {
		try {
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fac.newDocumentBuilder();		
			Document doc = builder.parse(xmlFile); 
						
			
			NodeList par = doc.getElementsByTagName(grandfather);
			Node n = par.item(0);
			
			Node node = n.getLastChild();
				
			Element e = doc.createElement(tag);
			e.setTextContent(value);
			
			node.appendChild(e);				
			TransformerFactory.newInstance().newTransformer().transform(
	                new DOMSource(doc), 
	                new StreamResult(new FileOutputStream(xmlFile)));			
		}
		catch (Exception e) {
			e.printStackTrace();			
		}
	}
}