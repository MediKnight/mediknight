package de.baltic_online.mediknight;

import java.awt.Component;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.baltic_online.mediknight.domain.KnightObject;
import de.baltic_online.mediknight.domain.Patient;
import de.baltic_online.mediknight.domain.TagesDiagnose;
import de.baltic_online.mediknight.printing.FOPrinter;
import de.baltic_online.mediknight.tools.PrintSettingsPresenter;
import de.baltic_online.mediknight.util.ErrorDisplay;
import de.baltic_online.mediknight.util.MediknightUtilities;
import de.baltic_online.mediknight.widgets.UndoUtilities;
import de.baltic_online.mediknight.widgets.YinYangDialog;


public class MedicationPresenter implements Presenter, Commitable, Observer {

    public static void main( final String[] args ) {
	final JFrame f = new JFrame( "Medication Presenter Example" );

	// f.getContentPane().add( example().createView() );

	f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	f.pack();
	f.setVisible( true );
    }

    MedicationPanel view;

    MedicationModel model;


    public MedicationPresenter() {
	this( new MedicationModel() );
    }


    public MedicationPresenter( final MedicationModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
    }


    public void changeMedication( final String text ) {
	final StringBuffer sb = new StringBuffer( view.getVerordnungstext() );

	sb.append( "\n" + text );
	model.getDiagnose().setText( sb.toString() );
	final StringTokenizer tokenizer = new StringTokenizer( sb.toString(), "\n" );
	final List< MedicationEntry > entries = new ArrayList< MedicationEntry >();

	while( tokenizer.hasMoreTokens() ) {
	    entries.add( new MedicationEntry( tokenizer.nextToken() ) );
	}

	// MedicationEntry[] medications = (MedicationEntry[]) entries.toArray(
	// new MedicationEntry[0] );
	/*
	 * MedicationEntry.saveEntries( model.getVerordnung(), medications ); try { model.getVerordnung().save(); } catch( SQLException e ) {
	 */
	// e.printStackTrace(); /** @todo Exception reporting. */
	// }
	view.update();
    }


    @Override
    public void commit() {
	// warum werden hier leerzeilen entfernt?
	final StringTokenizer tokenizer = new StringTokenizer( view.getMedication(), "\n" );
	final List< MedicationEntry > entries = new ArrayList< MedicationEntry >();

	while( tokenizer.hasMoreTokens() ) {
	    entries.add( new MedicationEntry( tokenizer.nextToken() ) );
	}

	final MedicationEntry[] medications = entries.toArray( new MedicationEntry[0] );
	MedicationEntry.saveEntries( model.getVerordnung(), medications );

	try {
	    model.getVerordnung().save();
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Speichern fehlgeschlagen!" );
	}
    }


    @Override
    public Component createView() {
	final MedicationPanel panel = new MedicationPanel( this );
	view = panel;
	view.setFocusOnMedication();
	new LockingListener( this ).applyTo( UndoUtilities.getMutables( panel ) );

	return panel;
    }


    public MedicationModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	/** @todo: implement this! */
	return null;
    }


    public void printMedication() {
	final YinYangDialog d = new YinYangDialog( JOptionPane.getFrameForComponent( view ), MainFrame.NAME );
	d.setStatusText( "Drucke ..." );
	d.run( new Runnable() {

	    @Override
	    public void run() {
		try {
		    final Patient patient = model.getDiagnose().getPatient();
		    final Map< String, String > printSettings = PrintSettingsPresenter.getSettings();
		    final Properties props = MainFrame.getProperties();
		    final FOPrinter fop = new FOPrinter( props.getProperty( "medication.xml" ), props.getProperty( "medication.xsl" ) );

		    // füge Patientendaten hinzu
		    fop.addData( "Patient/Title", patient.getTitel() );
		    fop.addData( "Patient/Anrede", patient.getAnrede() );
		    fop.addData( "Patient/Name", patient.getFullname() );

		    // füge das Datum hinzu
		    fop.addData( "Datum", MediknightUtilities.formatDate( model.getDiagnose().getVerordnung().getDatum() ) );

		    // füge Adresse des Patienten hinzu
		    fop.addData( "Patient/Address1", patient.getAdresse1() );
		    fop.addData( "Patient/Address2", patient.getAdresse2() );
		    fop.addData( "Patient/Address3", patient.getAdresse3() );

		    // füge Betreff, Absender und Abschlusssatz hinzu
		    fop.addData( "Abschluss", printSettings.get( "print.medication.final" ) );
		    fop.addData( "Absender", printSettings.get( "print.sender" ) );
		    fop.addData( "Betreff", "Verordnung:" );

		    // das Logo unterteilen und in die Datei mit aufnehmen
		    final String logo = printSettings.get( "print.logo" );
		    final String lf = System.getProperty( "line.separator" );
		    StringTokenizer token = new StringTokenizer( logo, lf );
		    int i = 1;
		    while( token.hasMoreElements() ) {
			if( i == 1 ) {
			    fop.addData( "Ueberschrift", token.nextToken() );
			    i++;
			} else {
			    fop.addTagToFather( "Zeile", token.nextToken(), "LogoInhalt" );
			}
		    }

		    // den Verordnugstext unterteilen und in die Datei aufnehmen
		    final String text = view.getVerordnungstext();

		    token = new StringTokenizer( text, lf );
		    while( token.hasMoreElements() ) {
			fop.addTagToFather( "TextBlock", token.nextToken(), "Text" );
		    }

		    // Datei drucken
		    fop.print();
		} catch( final SQLException e ) {
		    e.printStackTrace();
		} catch( final IOException e ) {
		    e.printStackTrace();
		}

		/*
		 * try { // Patient patient = model.getDiagnose().getPatient(); Properties prop = MainFrame.getProperties(); String lf =
		 * System.getProperty("line.separator"); String date = ""; MainFrame.getApplication().setWaitCursor(); date = MediknightUtilities.formatDate(
		 * model.getDiagnose().getVerordnung().getDatum()); String title = (patient.getTitel() != null) ? patient.getTitel() : ""; String firstName =
		 * (patient.getVorname() != null) ? patient.getVorname() : ""; String address = patient.getAnrede() + lf + ((title.equals("")) ? "" : (title +
		 * " ")) + ((firstName.equals("")) ? "" : (firstName + " ")) + patient.getName() + lf + patient.getAdresse1() + lf + patient.getAdresse2() + lf
		 * + patient.getAdresse3(); StringBuffer sb = new StringBuffer(view.getVerordnungstext()); Map map = PrintSettingsPresenter.getSettings();
		 * DataProvider dProvider = new DataProvider(null); dProvider.putData("DATE", date); dProvider.putData("ADDRESS", XMLTool.toXMLString(address));
		 * dProvider.putData( "MEDICATION", XMLTool.toXMLString(sb.toString())); String font = (String) map.get("print.font"); BufferedReader reader =
		 * new BufferedReader( new StringReader((String) map.get("print.logo"))); StringBuffer buffer = new StringBuffer(); String s; if ((s =
		 * reader.readLine()) == null) for (int i = 0; i < 7; i++) buffer.append( "<text content=\"&#160;\" style=\"Times,11pt\"/>"); else do {
		 * XMLTool.parseString(s, font, buffer); } while ((s = reader.readLine()) != null); dProvider.putData("LOGO", buffer.toString());
		 * dProvider.putData("FONT", font); reader = new BufferedReader( new StringReader( (String) map.get("print.medication.final"))); buffer = new
		 * StringBuffer(); while ((s = reader.readLine()) != null) { XMLTool.parseString(s, font, buffer); } dProvider.putData("FINAL",
		 * buffer.toString()); String sender = (String) map.get("print.sender"); if (sender.length() < 1) dProvider.putData("SENDER", ""); else
		 * dProvider.putData( "SENDER", XMLTool.toXMLString(sender)); TemplatePrinter tp = new TemplatePrinter( prop.getProperty("style"),
		 * prop.getProperty("header"), prop.getProperty("medication.content"), prop.getProperty("footer"), new String[] { prop.getProperty("frame")},
		 * prop.getProperty("page"), dProvider); try { tp.print(); } catch (java.lang.Exception e) { new ErrorDisplay( e, "Fehler beim Ausdrucken!",
		 * "Drucken...", MainFrame.getApplication()); } finally { MainFrame.getApplication().setDefaultCursor(); } } catch (Exception x) { new
		 * ErrorDisplay(x, "Speichern fehlgeschlagen!"); }
		 */
		MainFrame.getApplication().setDefaultCursor();
	    }
	} );
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
	try {
	    ((TagesDiagnose) knightObject).getVerordnung().recall();
	    view.update();

	} catch( final SQLException ex ) {
	}

    }


    public void setFocusOnMedication() {
    }


    @Override
    public void update( final Observable o, final Object arg ) {
	try {
	    ((LockingInfo) o).releaseLastLock();
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Speichern fehlgeschlagen!" );
	}
    }
}