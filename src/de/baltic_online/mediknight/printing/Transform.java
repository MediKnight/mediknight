package de.baltic_online.mediknight.printing;

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
 * Die Klasse transformiert eine xml-Datei mittels einer xsl-fo Datei in eine pdf_Datei um. Hierzu wird das fop-Projekt von apache in der Version 0.95 genutzt.
 *
 * @author bs
 *
 */
public class Transform {

    public static File xml2pdf( final File xmlFile, final File xslFile, final File pdfFile ) {
	// Step 1: Construct a FopFactory
	// (reuse if you plan to render multiple documents!)
	final FopFactory fopFactory = FopFactory.newInstance();

	// Step 2: Set up output stream.
	// Note: Using BufferedOutputStream for performance reasons
	// (helpful with FileOutputStreams).
	OutputStream out = null;

	try {
	    out = new BufferedOutputStream( new FileOutputStream( pdfFile ) );
	    // Step 3: Construct fop with desired output format
	    final Fop fop = fopFactory.newFop( MimeConstants.MIME_PDF, out );

	    // Step 4: Setup JAXP using identity transformer
	    final TransformerFactory factory = TransformerFactory.newInstance();
	    // with XSLT:
	    final Source xslt = new StreamSource( xslFile );
	    final Transformer transformer = factory.newTransformer( xslt );

	    // Step 5: Setup input and output for XSLT transformation
	    // Setup input stream
	    final Source src = new StreamSource( xmlFile );

	    // Resulting SAX events (the generated FO) must be piped through to
	    // FOP
	    final Result res = new SAXResult( fop.getDefaultHandler() );

	    // Step 6: Start XSLT transformation and FOP processing
	    transformer.transform( src, res );

	} catch( final IOException e ) {
	    e.printStackTrace();
	} catch( final FOPException e ) {
	    e.printStackTrace();
	} catch( final TransformerConfigurationException e ) {
	    e.printStackTrace();
	} catch( final TransformerException e ) {
	    e.printStackTrace();
	} finally {
	    // Clean-up
	    try {
		out.close();
	    } catch( final IOException e ) {
		e.printStackTrace();
	    }
	}
	return pdfFile;
    }
}