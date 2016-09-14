package main.java.de.baltic_online.mediknight;

import java.awt.Component;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import de.baltic_online.borm.Tracer;
import main.java.de.baltic_online.mediknight.domain.KnightObject;
import main.java.de.baltic_online.mediknight.domain.Lock;
import main.java.de.baltic_online.mediknight.domain.Patient;
import main.java.de.baltic_online.mediknight.domain.Rechnung;
import main.java.de.baltic_online.mediknight.domain.RechnungsPosten;
import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;
import main.java.de.baltic_online.mediknight.printing.FOPrinter;
import main.java.de.baltic_online.mediknight.tools.PrintSettingsPresenter;
import main.java.de.baltic_online.mediknight.util.CurrencyNumber;
import main.java.de.baltic_online.mediknight.util.ErrorDisplay;
import main.java.de.baltic_online.mediknight.util.MediknightUtilities;
import main.java.de.baltic_online.mediknight.widgets.UndoUtilities;
import main.java.de.baltic_online.mediknight.widgets.YinYangDialog;


public class LetterPresenter implements Presenter, Commitable, Observer {

    LetterPanel view;
    LetterModel model;


    public LetterPresenter() {
    }


    public LetterPresenter( final LetterModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
    }


    @Override
    public void commit() {
	model.getRechnung().setText( view.getText() );
	model.getRechnung().setDatum( MediknightUtilities.parseDate( view.getDate() ) );
	model.getRechnung().setAddress( view.getAdress() );
	model.getRechnung().setGreetings( view.getGreetings() );

	final MainFrame app = MainFrame.getApplication();
	final LockingInfo li = app.getLockingInfo();
	final Patient patient = li.getPatient();
	Lock lock = null;
	try {
	    if( patient != null ) {
		lock = patient.acquireLock( li.getAspect(), LockingListener.LOCK_TIMEOUT );
		if( lock != null ) {
		    try {
			model.getRechnung().save();
		    } catch( final SQLException e ) {
			Tracer.getDefaultTracer().trace( e );
		    }
		}
	    }
	} catch( final SQLException sqle ) {
	} finally {
	    try {
		lock.release();
	    } catch( final Exception ex ) {
	    }
	}

    }


    @Override
    public Component createView() {
	view = new LetterPanel();
	view.setPresenter( this );
	view.setFocusOnAdressTA();

	new LockingListener( this ).applyTo( UndoUtilities.getMutables( view ) );

	return view;
    }


    public LetterModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	/** TODO: implement this! */
	return null;
    }


    public void printBill() {
	/** TODO Make sure the contained mutables are notified also. */
	commit();
	final YinYangDialog d = new YinYangDialog( JOptionPane.getFrameForComponent( view ), MainFrame.NAME );
	d.setStatusText( "Drucke ..." );
	d.run( new Runnable() {

	    @Override
	    public void run() {
		try {
		    final Properties props = MainFrame.getProperties();
		    final FOPrinter fop = new FOPrinter( props.getProperty( "bill.xml" ), props.getProperty( "bill.xsl" ) );

		    final Rechnung rechnung = model.getRechnung();
		    final Patient patient = rechnung.getPatient();
		    final Map< String, String > printSettings = PrintSettingsPresenter.getSettings();
		    final String lf = System.getProperty( "line.separator" );
		    final NumberFormat nf = MediknightUtilities.getNumberFormat();

		    // füge Rechnungsnr und Datum hinzu
		    fop.addData( "Rechnung", String.valueOf( rechnung.getId() ) );
		    fop.addData( "Datum", MediknightUtilities.formatDate( rechnung.getDatum() ) );

		    // füge Patientendaten hinzu
		    fop.addData( "Patient/Title", patient.getTitel() );
		    fop.addData( "Patient/Anrede", patient.getAnrede() );
		    fop.addData( "Patient/Name", patient.getFullname() );

		    fop.addData( "Patient/Address1", patient.getAdresse1() );
		    fop.addData( "Patient/Address2", patient.getAdresse2() );
		    fop.addData( "Patient/Address3", patient.getAdresse3() );

		    // füge Absender hinzu
		    fop.addData( "Absender", printSettings.get( "print.sender" ) );

		    // zerlege das Logo und füge es in die neue xml-File ein
		    final String logo = printSettings.get( "print.logo" );
		    final StringTokenizer token = new StringTokenizer( logo, lf );
		    int i = 1;
		    while( token.hasMoreElements() ) {
			if( i == 1 ) {
			    final String str = token.nextToken();
			    fop.addData( "Ueberschrift", str );
			    i++;
			} else {
			    final String str = token.nextToken();
			    fop.addTagToFather( "Zeile", str, "LogoInhalt" );
			}
		    }

		    // zerlege das Vorwort und füge es hinzu
		    final String[] vorwort = model.getRechnung().getText().split( lf );
		    for( final String element : vorwort ) {
			fop.addTagToFather( "Zeile", element, "Vorwort" );
		    }

		    final String[] abschluss = printSettings.get( "print.bill.final" ).split( lf );
		    for( final String abschlus : abschluss ) {
			fop.addTagToFather( "Zeile", abschlus, "Abschluss" );
		    }

		    final String[] greetings = view.getGreetings().split( lf );
		    for( final String greeting : greetings ) {
			fop.addTagToFather( "Zeile", greeting, "Greetings" );
		    }
		    // zerlege alle Rechnungsposten und füge sie zur xml-File
		    // hinzu
		    final BillEntry[] billEntries = BillEntry.loadEntries( rechnung );
		    final CurrencyNumber sum = new CurrencyNumber( 0.0, CurrencyNumber.EUR ); // der Gesamtpreis

		    for( int y = 0; y < billEntries.length; y++ ) {
			final RechnungsPosten rp = billEntries[y].getItem();
			final double count = billEntries[y].getCount();

			fop.addTagToFather( "Row", "", "Table" );
			if( y == 0 ) {
			    fop.addTag( "Cell", MediknightUtilities.formatDate( rechnung.getDatum() ), "Table" );
			} else {
			    fop.addTag( "Cell", "", "Table" );
			}
			if( rechnung.isGoae() ) {
			    fop.addTag( "Cell", rp.getGOAE(), "Table" );
			} else {
			    fop.addTag( "Cell", rp.getGebueH(), "Table" );
			}

			fop.addTag( "Cell", rp.getText(), "Table" );
			final int currency = rp.isEuro() ? CurrencyNumber.EUR : CurrencyNumber.DM;
			final String price = new CurrencyNumber( rp.getPreis(), currency ).toCurrency( MainFrame.getApplication().getCurrency() ).toString();

			fop.addTag( "Cell", price, "Table" );
			fop.addTag( "Cell", nf.format( count ), "Table" );

			final double t = rp.getPreis() * count;
			final CurrencyNumber cn = new CurrencyNumber( t, currency ).toCurrency( MainFrame.getApplication().getCurrency() );
			final String total = cn.toString();
			fop.addTag( "Cell", total, "Table" );
			sum.add( cn.round( 2 ) );

		    }

		    // füge den Gesamtpreis hinzu
		    fop.addTagToFather( "Total", sum.toString(), "Dokument" );

		    try {
			MainFrame.getApplication().setWaitCursor();
			for( int y = 0; y < view.getCopyCount(); y++ ) {
			    fop.print();
			}
		    } catch( final Exception e ) {
			new ErrorDisplay( e, "Fehler beim Ausdruck!", "Drucken...", MainFrame.getApplication() );
		    } finally {
			MainFrame.getApplication().setDefaultCursor();
		    }

		    /*
		     * DataProvider dProvider = new DataProvider(null); Properties prop = MainFrame.getProperties(); String lf =
		     * System.getProperty("line.separator"); Rechnung rechnung = model.getRechnung(); String billID = String.valueOf(rechnung.getId()); String
		     * date = MediknightUtilities.formatDate(rechnung.getDatum()); Patient patient = model.getRechnung().getPatient(); String title =
		     * (patient.getTitel() != null) ? patient.getTitel() : ""; String firstName = (patient.getVorname() != null) ? patient.getVorname() : "";
		     * String address = patient.getAnrede() + lf + ((title.length() < 1) ? "" : (title + " ")) + ((firstName.length() < 1) ? "" : (firstName +
		     * " ")) + patient.getName() + lf + patient.getAdresse1() + lf + patient.getAdresse2() + lf + patient.getAdresse3(); Map map =
		     * PrintSettingsPresenter.getSettings(); String font = (String) map.get("print.font"); BufferedReader reader = new BufferedReader( new
		     * StringReader((String) map.get("print.logo"))); StringBuffer buffer = new StringBuffer(); String s; if ((s = reader.readLine()) == null)
		     * for (int i = 0; i < 7; i++) buffer.append( "<text content=\"&#160;\" style=\"Times,11pt\"/>"); else do { XMLTool.parseString(s, font,
		     * buffer); } while ((s = reader.readLine()) != null); dProvider.putData("LOGO", buffer.toString()); dProvider.putData("FONT", font); reader
		     * = new BufferedReader( new StringReader( (String) map.get("print.bill.final"))); buffer = new StringBuffer(); while ((s =
		     * reader.readLine()) != null) { XMLTool.parseString(s, font, buffer); } dProvider.putData("FINAL", buffer.toString()); String sender =
		     * (String) map.get("print.sender"); if (sender.length() < 1) dProvider.putData("SENDER", ""); else dProvider.putData( "SENDER",
		     * XMLTool.toXMLString(sender)); String text = model.getRechnung().getText(); if (text == null) text = ""; else text += lf; NumberFormat nf
		     * = MediknightUtilities.getNumberFormat(); //NumberFormat nf = ((NewAppWindow) AppWindow.getApplication()).getNumberFormat(); BillEntry[]
		     * billEntries = BillEntry.loadEntries(rechnung); int n = billEntries.length; StringBuffer posten = new StringBuffer(); posten.append(
		     * "<table><specification width=\"100%\" cellpadding=\"4\"/>" ); posten.append("<row><specification/><header>"); posten.append(
		     * "<specification width=\"12%\" border=\"68\"/>"); posten.append( "<text content=\"Datum\" style=\"" + font + ",9pt,bold,center\"/>");
		     * posten.append( "</header><header><specification width=\"9%\" border=\"68\"/>" ); if (rechnung.isGoae()) posten.append(
		     * "<text content=\"GoÄ\" style=\"" + font + ",9pt,bold,center\"/>"); else posten.append( "<text content=\"GebüH\" style=\"" + font +
		     * ",9pt,bold,center\"/>"); posten.append( "</header><header><specification width=\"40%\" border=\"68\"/>" ); posten.append(
		     * "<text content=\"Bezeichnung der Leistung\" style=\"" + font + ",9pt,bold,center\"/>"); posten.append(
		     * "</header><header><specification width=\"14%\" border=\"68\"/>" ); posten.append( "<text content=\"Einzelpreis\" style=\"" + font +
		     * ",9pt,bold,center\"/>"); posten.append( "</header><header><specification width=\"10%\" border=\"68\"/>" ); posten.append(
		     * "<text content=\"Anzahl\" style=\"" + font + ",9pt,bold,center\"/>"); posten.append(
		     * "</header><header><specification width=\"15%\" border=\"68\"/>" ); posten.append( "<text content=\"Gesamt\" style=\"" + font +
		     * ",9pt,bold,center\"/>"); posten.append("</header></row>"); CurrencyNumber sum = new CurrencyNumber(0.0, CurrencyNumber.EUR); for (int pos
		     * = 0; pos < n; pos++) { BillEntry be = billEntries[pos]; RechnungsPosten rp = be.getItem(); String gebueH = rp.getGebueH(); String
		     * specification = rp.getText(); String goae = rp.getGOAE(); int currency = rp.isEuro() ? CurrencyNumber.EUR : CurrencyNumber.DM; String
		     * price = new CurrencyNumber(rp.getPreis(), currency) .toCurrency( MainFrame.getApplication().getCurrency()) .toString(); String count =
		     * nf.format(be.getCount()); double t = rp.getPreis() * be.getCount(); CurrencyNumber cn = new CurrencyNumber(t, currency).toCurrency(
		     * MainFrame.getApplication().getCurrency()); String total = cn.toString(); sum.add(cn.round(2)); posten.append("<row><specification/>");
		     * posten.append("<data><specification width=\"12%\"/>"); posten.append( "<text content=\"" + ((pos == 0) ? date : "&#160;") + "\" style=\""
		     * + font + ",9pt\"/>"); posten.append( "</data><data><specification width=\"9%\"/>"); if (rechnung.isGoae()) posten.append(
		     * "<text content=\"" + XMLTool.toXMLString(goae) + "\" style=\"" + font + ",9pt,right\"/>"); else posten.append( "<text content=\"" +
		     * XMLTool.toXMLString(gebueH) + "\" style=\"" + font + ",9pt,right\"/>"); posten.append( "</data><data><specification width=\"40%\"/>");
		     * posten.append( "<text content=\"" + XMLTool.toXMLString(specification) + "\" style=\"" + font + ",9pt\"/>"); posten.append(
		     * "</data><data><specification width=\"14%\"/>"); posten.append( "<text content=\"" + XMLTool.toXMLString(price) + "\" style=\"" + font +
		     * ",9pt,right\"/>"); posten.append( "</data><data><specification width=\"10%\"/>"); posten.append( "<text content=\"" + count +
		     * "\" style=\"" + font + ",9pt,right\"/>"); posten.append( "</data><data><specification width=\"15%\"/>"); posten.append(
		     * "<text content=\"" + XMLTool.toXMLString(total) + "\" style=\"" + font + ",9pt,right\"/></data>"); posten.append("</row>"); } if
		     * (!MainFrame.getApplication().isEuro()) { sum = sum.toCurrency(CurrencyNumber.DM); } posten.append("</table>"); dProvider.putData("TABLE",
		     * posten.toString()); dProvider.putData("ID", billID); dProvider.putData("DATE", date); dProvider.putData("ADDRESS",
		     * XMLTool.toXMLString(address)); dProvider.putData("TEXT", XMLTool.toXMLString(text)); // dProvider.putData("DIAGNOSE",
		     * XMLTool.toXMLString(diagnose)); dProvider.putData( "TOTAL", XMLTool.toXMLString(sum.toString())); dProvider.putData( "CLOSING",
		     * XMLTool.toXMLString(view.getGreetings())); TemplatePrinter tp = new TemplatePrinter( prop.getProperty("style"),
		     * prop.getProperty("header"), prop.getProperty("bill.content"), prop.getProperty("footer"), new String[] { prop.getProperty("frame")},
		     * prop.getProperty("page"), dProvider); try { MainFrame.getApplication().setWaitCursor(); for (int i = 0; i < view.getCopyCount(); i++)
		     * tp.print(); } catch (Exception e) { new ErrorDisplay( e, "Fehler beim Ausdruck!", "Drucken...", MainFrame.getApplication()); } finally {
		     * MainFrame.getApplication().setDefaultCursor(); }
		     */
		} catch( final Exception e ) {
		    e.printStackTrace();
		}
	    }
	} );
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
	try {
	    ((TagesDiagnose) knightObject).getRechnung().recall();
	    view.update();

	} catch( final SQLException ex ) {
	}
    }


    public void showBill() {
	MainFrame.getApplication().bill();
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