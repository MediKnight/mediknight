package de.bo.mediknight.xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class CreateXMLFile {
	private File xmlFile;
	private File template;
	private final String DIR; 
	
	public CreateXMLFile(String templateFile) throws IOException {
		DIR = "/Users/bs-macosx/Desktop/mediknight files/";
		xmlFile = new File(DIR, "output.xml");
		xmlFile.createNewFile();
		template = new File(DIR, templateFile);
	}
	
	public void insertValues(HashMap table) 
	throws IOException {		
		
		FileInputStream fis = new FileInputStream(template);
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
					
					String value = (String) table.get(str);
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
	
	public static void main(String[] args) throws Exception {
		CreateXMLFile create = new CreateXMLFile("/Users/bs-macosx/Desktop/mediknight files/verordnung.xml");
		
		//create.insertValues(create.getHashtable());	
		for(int i=0; i<6; i++)
			create.addElement("Zeile", "B. Schnoor", "LogoInhalt");
	}
	
	private Hashtable getHashtable() {
		Hashtable table = new Hashtable();
		
		table.put("Ueberschrift", "PRAXIS f�r NATURHEILKUNDE");
		table.put("Bezeichnung/Zeile1", "Michael Vette");
		table.put("Bezeichnung/Zeile2", "Heilpraktiker");
		table.put("Anschrift/Zeile1","Kirchenstra�e 12-14");
		table.put("Anschrift/Zeile2","24211 Preetz");
		table.put("Anschrift/Zeile3","Telefon: 04342/789880");
		table.put("Anschrift/Zeile4","Telefax: 04342/789884");
		table.put("Absender","Praxis f. Naturheilkunde M.Vette, Kirchenstr. 12-14, 24211 Preetz");
		table.put("Patient/Anrede","Herr");
		table.put("Patient/Name","Manni Mustermann");
		table.put("Patient/Anschrift/Zeile1","Musterstra�e 1");
		table.put("Patient/Anschrift/Zeile2","24211 Preetz");
		table.put("Datum","25.09.2008");
		table.put("Betreff", "Verordnung:");
		table.put("Text", "Beginnen Sie den Tag mit Obst.\n Essen Sie anschlie�end Obst. \n Zum Mittag essen Sie Salat. \n Nach 18Uhr essen Sie nichts mehr! \n");
		table.put("Abschluss","Wir danken f�r Ihren Besuch!");
		return table;
	}

	public void addElement(String tag, String value, String parent) {
		try {
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = fac.newDocumentBuilder();		
			Document doc = builder.parse(xmlFile); 
						
			NodeList par = doc.getElementsByTagName(parent);
			Node node = par.item(0);
			Element e = doc.createElement(tag);
			e.setTextContent(value);
			
		node.appendChild(e);			
			//add(doc.getFirstChild(),tag,value,parent);
			TransformerFactory.newInstance().newTransformer().transform(
	                new DOMSource(doc), new StreamResult(new FileOutputStream(xmlFile)));
			
		}
		catch (Exception e) {
			e.printStackTrace();			
		}
	}
	
	public void addToLast(String tag, String value, String grandfather) {
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
			//add(doc.getFirstChild(),tag,value,parent);
			TransformerFactory.newInstance().newTransformer().transform(
	                new DOMSource(doc), new StreamResult(new FileOutputStream(xmlFile)));
			
		}
		catch (Exception e) {
			e.printStackTrace();			
		}
	}		
}