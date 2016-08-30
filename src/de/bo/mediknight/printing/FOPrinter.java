package de.bo.mediknight.printing;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import de.baltic_online.borm.Tracer;
import de.bo.mediknight.MainFrame;


/**
 * Diese Klasse wird benutzt, um xml-Dateien in das pdf-Format umzuwandeln und anschließend auszudrucken. Man erzeugt ein FOPrinter-Objekt und fügt diesem dann
 * eine Template-Datei im xml-Format und eine xsl-Datei, die die Umwandlung von xml zu pdf vornimmt bei. Anschießend kann man in der Template-Datei die Muster
 * ersetzen oder neue tags hinzufügen.
 *
 * @author Benjamin Schnoor (bs@baltic-online.de)
 * @version 3.0
 */
public class FOPrinter {

    private final String		    DIR;
    private final String		    HOME_DIR;
    private final File		      xmlFile;
    private File			    xslFile;
    private File			    templateFile;
    private final HashMap< String, String > data;	// für die Ersetzung von
							  // Templates
    private final ArrayList< String[] >     newElements; // für das Einfügen neuer
							  // Tags
    private boolean			 wasPrinted;


    /**
     * Erzeugt ein neues FOPrinter-Objekt.
     * 
     * @param xmlFileName
     *            Name der Template-Datei
     * @param xslFileName
     *            Name der xsl-Datei
     */
    public FOPrinter( final String xmlFileName, final String xslFileName ) {

	DIR = System.getProperty( "user.dir" ) + System.getProperty( "file.separator" ) + MainFrame.getProperties().getProperty( "outdir" );

	HOME_DIR = System.getProperty( "user.home" ) + System.getProperty( "file.separator" ) + MainFrame.getProperties().getProperty( "outdir" );

	new File( HOME_DIR ).mkdir();
	templateFile = new File( xmlFileName );
	xmlFile = new File( HOME_DIR, "output.xml" );

	xslFile = new File( xslFileName );
	data = new HashMap< String, String >();
	newElements = new ArrayList< String[] >();
	wasPrinted = false;
    }


    /**
     * Fügt einen neues Template zur Liste hinzu. Die Templates werden erst eingesetzt, wenn die Datei gedruckt werden soll.
     * 
     * @param key
     *            Name des Templates
     * @param value
     *            Inhalt des Templates
     */
    public void addData( final String key, final String value ) {
	data.put( key, value );
    }


    /**
     * Fügt den neuen Tag in die Output-Datei ein
     * 
     * @param tag
     *            Name des neuen Tags
     * @param value
     *            Inhalt des neuen Tags
     * @param parent
     *            Name des Vaters
     */
    private void addElement( final String tag, final String value, final String parent ) {
	final DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
	fac.setValidating( false );
	DocumentBuilder builder;
	try {
	    builder = fac.newDocumentBuilder();
	    final Document doc = builder.parse( xmlFile );

	    final NodeList par = doc.getElementsByTagName( parent );
	    final Node node = par.item( 0 );
	    final Element childnode = doc.createElement( tag );
	    final Text text = doc.createTextNode( value );

	    childnode.appendChild( text );
	    node.appendChild( childnode );

	    TransformerFactory.newInstance().newTransformer().transform( new DOMSource( doc ), new StreamResult( new FileOutputStream( xmlFile ) ) );
	} catch( final TransformerConfigurationException e1 ) {
	    Tracer.getDefaultTracer().trace( e1 );
	} catch( final FileNotFoundException e1 ) {
	    Tracer.getDefaultTracer().trace( e1 );
	} catch( final TransformerException e1 ) {
	    Tracer.getDefaultTracer().trace( e1 );
	} catch( final TransformerFactoryConfigurationError e1 ) {
	    Tracer.getDefaultTracer().trace( e1 );
	} catch( final SAXException e1 ) {
	    Tracer.getDefaultTracer().trace( e1 );
	} catch( final IOException e1 ) {
	    Tracer.getDefaultTracer().trace( e1 );
	} catch( final ParserConfigurationException e1 ) {
	    Tracer.getDefaultTracer().trace( e1 );
	}
    }


    /**
     * Fügt alle gespeicherten neuen Tags in die Output-Datei ein. Die Methode darf nur aufgerufen werden, wenn gedruckt werden soll. Damit vermeidet man
     * unvorhergesehenes Verhalten.
     */
    private void addElements() {
	for( int i = 0; i < newElements.size(); i++ ) {
	    final String[] str = newElements.get( i );
	    if( str[0].equals( "true" ) ) {
		addToLast( str[1], str[2], str[3] );
	    } else {
		addElement( str[1], str[2], str[3] );
	    }
	}
    }


    /**
     * Fügt einen neuen Tag in die Merkliste ein. Der Tag zu dem der neue Tag in Bezug steht ist nicht der direkte Vorgänger, sondern der Großvater. Vom
     * Großvater wird das letzte Kind-Element genommen und da wird dann der neue Tag eingefügt.
     * 
     * @param tag
     *            Name des neuen Tags
     * @param value
     *            Inhalt des neuen Tags
     * @param grandfather
     *            Name des Großvater-Tags
     */
    public void addTag( final String tag, final String value, final String grandfather ) {
	final String[] str = { "true", tag, value, grandfather };
	newElements.add( str );
    }


    /**
     * Fügt einen neuen Tag in die Merkliste ein. Die Tags werden erst hinzugefügt, wenn die Datei gedruckt werden soll.
     * 
     * @param tag
     *            Name des neuen Tags
     * @param value
     *            Inhalt des neuen Tags
     * @param parent
     *            Name des Vaters
     */
    public void addTagToFather( final String tag, final String value, final String parent ) {
	final String[] str = { "false", tag, value, parent };
	newElements.add( str );
    }


    /**
     * Fügt einen neuen Tag in die Output-Datei ein. Der Tag zu dem der neue Tag in Bezug steht ist nicht der direkte Vorgänger, sondern der Großvater. Vom
     * Großvater wird das letzte Kind-Element genommen und da wird dann der neue Tag eingefügt.
     * 
     * @param tag
     *            Name des neuen Tags
     * @param value
     *            Inhalt des neuen Tags
     * @param grandfather
     *            Name des Großvaters
     */
    private void addToLast( final String tag, final String value, final String grandfather ) {
	final DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;

	try {
	    builder = fac.newDocumentBuilder();
	    Document doc;
	    doc = builder.parse( xmlFile );
	    final NodeList par = doc.getElementsByTagName( grandfather );
	    final Node n = par.item( 0 );
	    final Node node = n.getLastChild();

	    final Element childnode = doc.createElement( tag );
	    final Text text = doc.createTextNode( value );
	    childnode.appendChild( text );
	    node.appendChild( childnode );

	    TransformerFactory.newInstance().newTransformer().transform( new DOMSource( doc ), new StreamResult( new FileOutputStream( xmlFile ) ) );
	} catch( final TransformerConfigurationException e ) {
	    Tracer.getDefaultTracer().trace( e );
	} catch( final FileNotFoundException e ) {
	    Tracer.getDefaultTracer().trace( e );
	} catch( final TransformerException e ) {
	    Tracer.getDefaultTracer().trace( e );
	} catch( final TransformerFactoryConfigurationError e ) {
	    Tracer.getDefaultTracer().trace( e );
	} catch( final SAXException e ) {
	    Tracer.getDefaultTracer().trace( e );
	} catch( final IOException e ) {
	    Tracer.getDefaultTracer().trace( e );
	} catch( final ParserConfigurationException e ) {
	    Tracer.getDefaultTracer().trace( e );
	}
    }


    public boolean exportToFile( final File file ) throws IOException {
	// wenn schon einmal dieses Objekt gedruckt wurde, dann nicht
	// nocheinmal die Werte einsetzen
	if( !wasPrinted ) {
	    insertValues();
	    addElements();
	}
	wasPrinted = true;

	Transform.xml2pdf( xmlFile, xslFile, file );

	return true;

    }


    /**
     * Ersetzt die Templates in der Templates-Datei
     * 
     * @throws IOException
     */
    private void insertValues() throws IOException {

	final FileInputStream fis = new FileInputStream( templateFile );
	final FileOutputStream fos = new FileOutputStream( xmlFile );
	final OutputStreamWriter osw = new OutputStreamWriter( fos, "UTF-8" );

	boolean end = false;

	while( !end ) {
	    int read = fis.read();

	    if( read == -1 ) {
		end = true;
	    } else {
		if( read == 36 ) {

		    String str = "";
		    read = fis.read();
		    while( read != 36 ) {
			str += String.valueOf( Character.toChars( read ) );
			read = fis.read();
		    }

		    final String value = data.get( str );
		    if( value != null ) {
			osw.write( new String( value ) );
		    }
		} else {
		    osw.write( read );
		}
	    }
	}
	osw.close();
	fis.close();
	fos.close();
    }


    /**
     * Druckt die xml-Seite aus. Erst jetzt werden alle hinzugefügten Templates und neuen Tags in die Template-Datei eingefügt.
     * 
     * @return true, wenn der Druckauftrag gesendet wurde, false sonst
     * 
     * @throws IOException
     *             wenn es Probleme mit den Dateien gibt
     */
    public boolean print() throws IOException {

	// wenn schon einmal dieses Objekt gedruckt wurde, dann nicht
	// nocheinmal die Werte einsetzen
	if( !wasPrinted ) {
	    insertValues();
	    addElements();
	}
	wasPrinted = true;

	final File file = Transform.xml2pdf( xmlFile, xslFile, new File( HOME_DIR, "pdfFile.pdf" ) );

	final FileInputStream fis = new FileInputStream( file );

	final DocAttributeSet set = new HashDocAttributeSet();
	set.add( MediaSizeName.ISO_A4 );

	final Doc doc = new SimpleDoc( fis, DocFlavor.INPUT_STREAM.PDF, set );

	final PrintService printer = PrinterJob.lookupPrintServices()[0];

	if( printer != null ) {
	    final DocPrintJob job = printer.createPrintJob();
	    try {
		job.print( doc, null );
		fis.close();
		return true;
	    } catch( final PrintException e ) {
		e.printStackTrace();
		fis.close();
		return false;
	    }
	} else {
	    fis.close();
	}
	return false;
    }


    /**
     * Ändert die Datei, die als Vorlage dient.
     * 
     * @param fileName
     *            die neue Datei
     */
    public void setXMLFile( final String fileName ) {
	templateFile = new File( DIR, fileName );
    }


    /**
     * Ändert die Datei, die den xsl-Code hat.
     * 
     * @param fileName
     *            die neue Datei
     */
    public void setXSLFile( final String fileName ) {
	xslFile = new File( DIR, fileName );
    }
}