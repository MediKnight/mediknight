package de.bo.mediknight.xml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

/**
 * Die Klasse transformiert eine xml-Datei mittels einer xsl-fo Datei in eine
 * pdf_Datei um. Hierzu wird das fop-Projekt von apache in der Version 0.95 
 * genutzt.
 * 
 * @author bs
 *
 */
public class Transform {

	public static File xml2pdf(File xmlFile, File xslFile, String dir) {
		
	   File pdfFile = new File(dir, "pdfFile.pdf");
	   // Step 1: Construct a FopFactory
	   // (reuse if you plan to render multiple documents!)
	   FopFactory fopFactory = FopFactory.newInstance();

	   // Step 2: Set up output stream.
	   // Note: Using BufferedOutputStream for performance reasons 
	   // (helpful with FileOutputStreams).
	   OutputStream out = null;

	   try {
		   out = new BufferedOutputStream(new FileOutputStream(pdfFile));
		   // Step 3: Construct fop with desired output format
		   Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

		   // Step 4: Setup JAXP using identity transformer
		   TransformerFactory factory = TransformerFactory.newInstance();
		   //with XSLT:
		   Source xslt = new StreamSource(xslFile);
		   Transformer transformer = factory.newTransformer(xslt);

		   // Step 5: Setup input and output for XSLT transformation 
		   // Setup input stream    	  
		   Source src = new StreamSource(xmlFile);

		   // Resulting SAX events (the generated FO) must be piped through to FOP
		   Result res = new SAXResult(fop.getDefaultHandler());

		   // Step 6: Start XSLT transformation and FOP processing
		   transformer.transform(src, res);
		   
	   } catch(IOException e) {
		   e.printStackTrace();
	   } catch(FOPException e) {
		   e.printStackTrace();
	   } catch (TransformerConfigurationException e) {
		   e.printStackTrace();
	   } catch (TransformerException e) {
		   e.printStackTrace();
	   } finally {
		   //Clean-up
		   try {
			   out.close();
		   } catch(IOException e) {
			   e.printStackTrace();
		   }
	   }
	   return pdfFile;
   }
}