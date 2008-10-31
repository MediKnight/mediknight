package de.bo.mediknight.printing;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.bo.mediknight.xml.Transform;

public abstract class FOPrinter {
	private final String DIR;
	private File xmlFile;
	private File xslFile;
	private File templateFile;
	private HashMap data;
	
	
	public FOPrinter(String xmlFileName, String xslFileName) {
		DIR = "/Users/bs-macosx/Desktop/mediknight files/";//TODO über property klasse finden
		templateFile = new File(DIR, xmlFileName);
		xmlFile = new File(DIR, "output.xml");
		xslFile = new File(DIR, xslFileName);
		data = new HashMap();
	}
	
	public void setXMLFile(String fileName) {
		templateFile = new File(DIR, fileName); 
	}
	
	public void setXSLFile(String fileName) {
		xslFile = new File(DIR, fileName);
	}	
	
	public void addData(String key, String value) {
		data.put(key, value);
	}
	
	public boolean printPage() throws IOException {
		
		insertValues();
		File file = Transform.xml2pdf(xmlFile, xslFile, DIR);

		FileInputStream fis = new FileInputStream(file);

		Doc doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.PDF, null);

		PrintService printer = PrinterJob.lookupPrintServices()[0];

/*		if (printer != null) {
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
			fis.close();*/
		return false;
	}

	public abstract void print();
	
	public void insertValues() 
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
		catch (Exception e) {e.printStackTrace();}
	}


}
