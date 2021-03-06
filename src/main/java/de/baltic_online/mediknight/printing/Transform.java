package main.java.de.baltic_online.mediknight.printing;

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
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;


/**
 * Die Klasse transformiert eine xml-Datei mittels einer xsl-fo Datei in eine pdf_Datei um. Hierzu wird das fop-Projekt von apache in der Version 0.95 genutzt.
 *
 * @author bs
 *
 */
public class Transform {

    public static File xml2pdf( final File xmlFile, final File xslFile, final File pdfFile ) {
	OutputStream out = null;

	try {
	    final FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder( new File( "." ).toURI() );
	    final FopFactory fopFactory = fopFactoryBuilder.build();
	    final TransformerFactory factory = TransformerFactory.newInstance();

	    out = new BufferedOutputStream( new FileOutputStream( pdfFile ) );
	    final Fop fop = fopFactory.newFop( org.apache.xmlgraphics.util.MimeConstants.MIME_PDF, out );

	    // Step 4: Setup JAXP using identity transformer with XSLT:
	    final Source xslt = new StreamSource( xslFile );
	    final Transformer transformer = factory.newTransformer( xslt );

	    // Step 5: Setup input and output for XSLT transformation setup input stream
	    final Source src = new StreamSource( xmlFile );

	    // Resulting SAX events (the generated FO) must be piped through to FOP
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