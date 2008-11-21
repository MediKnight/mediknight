package de.bo.mediknight;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import de.bo.mediknight.domain.KnightObject;
import de.bo.mediknight.domain.Lock;
import de.bo.mediknight.domain.Patient;
import de.bo.mediknight.domain.Rechnung;
import de.bo.mediknight.domain.RechnungsPosten;
import de.bo.mediknight.domain.TagesDiagnose;
import de.bo.mediknight.printing.FOPrinter;
import de.bo.mediknight.tools.PrintSettingsPresenter;
import de.bo.mediknight.util.CurrencyNumber;
import de.bo.mediknight.util.ErrorDisplay;
import de.bo.mediknight.util.MediknightUtilities;
import de.bo.mediknight.util.XMLTool;
import de.bo.mediknight.widgets.UndoUtilities;
import de.bo.mediknight.widgets.YinYangDialog;
import de.bo.mediknight.xml.CreateXMLFile;
import de.bo.mediknight.xml.Transform;
import de.bo.print.jpf.TemplatePrinter;
import de.bo.print.te.DataProvider;

public class LetterPresenter implements Presenter, Commitable, Observer {

    LetterPanel view;
    LetterModel model;

    public LetterPresenter() {
    }

    public LetterPresenter(LetterModel model) {
        this.model = model;
    }

    public LetterModel getModel() {
        return model;
    }

    public void update(Observable o, Object arg) {
        try {
            ((LockingInfo) o).releaseLastLock();
        } catch (SQLException x) {
            new ErrorDisplay(x, "Speichern fehlgeschlagen!");
        }
    }

    public Component createView() {
        view = new LetterPanel();
        view.setPresenter(this);
        view.setFocusOnAdressTA();

        new LockingListener(this).applyTo(UndoUtilities.getMutables(view));

        return view;
    }

    public void activate() {
    }

    public void commit() {
        model.getRechnung().setText(view.getText());
        model.getRechnung().setDatum(
            MediknightUtilities.parseDate(view.getDate()));
        model.getRechnung().setAddress(view.getAdress());
        model.getRechnung().setGreetings(view.getGreetings());

        MainFrame app = MainFrame.getApplication();
        LockingInfo li = app.getLockingInfo();
        Patient patient = li.getPatient();
        Lock lock = null;
        try {
            if (patient != null) {
                lock =
                    patient.acquireLock(
                        li.getAspect(),
                        LockingListener.LOCK_TIMEOUT);
                if (lock != null) {
                    try {
                        model.getRechnung().save();
                    } catch (SQLException e) {
                        System.out.println("Isch habe ein Problem");
                        e.printStackTrace(); /** @todo Exception reporting. */
                    }
                }
            }
        } catch (SQLException sqle) {
        } finally {
            try {
                lock.release();
            } catch (Exception ex) {
            }
        }

    }

    public Component getResponsibleComponent() {
        /** @todo: implement this! */
        return null;
    }

    public void reload(Component component, KnightObject knightObject) {
        try {
            ((TagesDiagnose) knightObject).getRechnung().recall();
            view.update();

        } catch (SQLException ex) {
        }
    }

    public void showBill() {
        MainFrame.getApplication().bill();
    }

    public void printBill() {
        /** @todo Make sure the contained mutables are notified also. */
        commit();
        final YinYangDialog d =
            new YinYangDialog(
                JOptionPane.getFrameForComponent(view),
                MainFrame.NAME);
        d.setStatusText("Drucke ...");
        d.run(new Runnable() {
            public void run() {
                try {                	
                	FOPrinter fop = new FOPrinter("rechnung.xml", 
                								  "rechnung.xsl");
                	
                	Rechnung rechnung = model.getRechnung();
                	Patient patient = rechnung.getPatient();
                	Map props = PrintSettingsPresenter.getSettings();
                	String lf = System.getProperty("line.separator");
                	NumberFormat nf = MediknightUtilities.getNumberFormat();
                	
                	//füge Rechnungsnr und Datum hinzu
                	fop.addData("Rechnung", String.valueOf(rechnung.getId()));
                	fop.addData("Datum", MediknightUtilities.formatDate(rechnung.getDatum()));                 	    	
                	
                	//füge Patientendaten hinzu
                	fop.addData("Patient/Title", patient.getTitel());
            		fop.addData("Patient/Anrede", patient.getAnrede());
            		fop.addData("Patient/Name", patient.getFullname());
 
              		fop.addData("Patient/Address1", patient.getAdresse1());
            		fop.addData("Patient/Address2", patient.getAdresse2());
            		fop.addData("Patient/Address3", patient.getAdresse3());
 
            		//füge Absender hinzu               		
            		fop.addData("Absender", (String)props.get("print.sender"));
            		
            		// zerlege das Logo und füge es in die neue xml-File ein
               		String logo = (String) props.get("print.logo");
            		StringTokenizer token = new StringTokenizer(logo,lf);
            		int i=1;
            		while(token.hasMoreElements()) {
            			if(i==1) {
            				String str = token.nextToken();
            				fop.addData("Ueberschrift", str);
            				i++;
            			}
            			else {
            				String str = token.nextToken();
            				fop.addTagToFather("Zeile", str, "LogoInhalt");
            			}
            		}
            		
            		//zerlege das Vorwort und füge es hinzu
            		String[] vorwort = model.getRechnung().getText().split(lf);
            		for(int y=0; y<vorwort.length; y++) {
            			fop.addTagToFather("Zeile", vorwort[y], "Vorwort");
            		}        
            		
            		String[] abschluss = ((String)props.get("print.bill.final")).split(lf);
            		for(int y=0; y<abschluss.length; y++) {
            			fop.addTagToFather("Zeile", abschluss[y], "Abschluss");
            		}
            		
            		String[] greetings = view.getGreetings().split(lf);
            		for(int y=0; y<greetings.length; y++)
            			fop.addTagToFather("Zeile", greetings[y], "Greetings");
            		// zerlege alle Rechnungsposten und füge sie zur xml-File hinzu
            		BillEntry[] billEntries = BillEntry.loadEntries(rechnung);
            		CurrencyNumber sum =
                        new CurrencyNumber(0.0, CurrencyNumber.EUR); // der Gesamtpreis
            		
            		for(int y=0; y<billEntries.length; y++) {
            			RechnungsPosten rp = billEntries[y].getItem();
            			double count = billEntries[y].getCount();
            			
            			fop.addTagToFather("Row", "", "Table");
            			if(y == 0) {
            				fop.addTag("Cell", 
            						MediknightUtilities.formatDate(
            									rechnung.getDatum()), "Table");
            			} else {
            				fop.addTag("Cell", "", "Table");
            			}
            			if(rechnung.isGoae()) {
            				fop.addTag("Cell", rp.getGOAE(), "Table");
            			}
            			else {
            				fop.addTag("Cell", rp.getGebueH(), "Table"); 
            			}
            			
            			fop.addTag("Cell", rp.getText(), "Table");
            			int currency =
                            rp.isEuro()
                                ? CurrencyNumber.EUR
                                : CurrencyNumber.DM;
                        String price =
                            new CurrencyNumber(rp.getPreis(), currency)
                                .toCurrency(
                                    MainFrame.getApplication().getCurrency())
                                .toString();
 
                        fop.addTag("Cell", price, "Table");
                        fop.addTag("Cell", nf.format(count), "Table");
            			
            			double t = rp.getPreis() * count;
                        CurrencyNumber cn =
                            new CurrencyNumber(t, currency).toCurrency(
                                MainFrame.getApplication().getCurrency());
                        String total = cn.toString();
                        fop.addTag("Cell", total, "Table");
                        sum.add(cn.round(2));
            			
            		}
            			
            		//füge den Gesamtpreis hinzu
            		fop.addTagToFather("Total", sum.toString(), "Dokument");
            		
            		try {
                        MainFrame.getApplication().setWaitCursor();
                        for (int y = 0; y < view.getCopyCount(); y++)
                        	fop.print();
                    } catch (Exception e) {
                        new ErrorDisplay(
                            e,
                            "Fehler beim Ausdruck!",
                            "Drucken...",
                            MainFrame.getApplication());
                    } finally {
                        MainFrame.getApplication().setDefaultCursor();
                    }
                	
                    /*DataProvider dProvider = new DataProvider(null);

                    Properties prop = MainFrame.getProperties();

                    String lf = System.getProperty("line.separator");

                    Rechnung rechnung = model.getRechnung();

                    String billID = String.valueOf(rechnung.getId());
                    String date =
                        MediknightUtilities.formatDate(rechnung.getDatum());

                    Patient patient = model.getRechnung().getPatient();

                    String title =
                        (patient.getTitel() != null) ? patient.getTitel() : "";
                    String firstName =
                        (patient.getVorname() != null)
                            ? patient.getVorname()
                            : "";
                    String address =
                        patient.getAnrede()
                            + lf
                            + ((title.length() < 1) ? "" : (title + " "))
                            + ((firstName.length() < 1) ? "" : (firstName + " "))
                            + patient.getName()
                            + lf
                            + patient.getAdresse1()
                            + lf
                            + patient.getAdresse2()
                            + lf
                            + patient.getAdresse3();

                    Map map = PrintSettingsPresenter.getSettings();

                    String font = (String) map.get("print.font");

                    BufferedReader reader =
                        new BufferedReader(
                            new StringReader((String) map.get("print.logo")));
                    StringBuffer buffer = new StringBuffer();
                    String s;

                    if ((s = reader.readLine()) == null)
                        for (int i = 0; i < 7; i++)
                            buffer.append(
                                "<text content=\"&#160;\" style=\"Times,11pt\"/>");
                    else
                        do {
                            XMLTool.parseString(s, font, buffer);
                        } while ((s = reader.readLine()) != null);

                    dProvider.putData("LOGO", buffer.toString());
                    dProvider.putData("FONT", font);

                    reader =
                        new BufferedReader(
                            new StringReader(
                                (String) map.get("print.bill.final")));
                    buffer = new StringBuffer();

                    while ((s = reader.readLine()) != null) {
                        XMLTool.parseString(s, font, buffer);
                    }

                    dProvider.putData("FINAL", buffer.toString());

                    String sender = (String) map.get("print.sender");
                    if (sender.length() < 1)
                        dProvider.putData("SENDER", "");
                    else
                        dProvider.putData(
                            "SENDER",
                            XMLTool.toXMLString(sender));

                    String text = model.getRechnung().getText();

                    if (text == null)
                        text = "";
                    else
                        text += lf;

                    NumberFormat nf = MediknightUtilities.getNumberFormat();
                    //NumberFormat nf = ((NewAppWindow) AppWindow.getApplication()).getNumberFormat();

                    BillEntry[] billEntries = BillEntry.loadEntries(rechnung);
                    int n = billEntries.length;

                    StringBuffer posten = new StringBuffer();

                    posten.append(
                        "<table><specification width=\"100%\" cellpadding=\"4\"/>");
                    posten.append("<row><specification/><header>");
                    posten.append(
                        "<specification width=\"12%\" border=\"68\"/>");
                    posten.append(
                        "<text content=\"Datum\" style=\""
                            + font
                            + ",9pt,bold,center\"/>");
                    posten.append(
                        "</header><header><specification width=\"9%\" border=\"68\"/>");

                    if (rechnung.isGoae())
                        posten.append(
                            "<text content=\"GoÄ\" style=\""
                                + font
                                + ",9pt,bold,center\"/>");
                    else
                        posten.append(
                            "<text content=\"GebüH\" style=\""
                                + font
                                + ",9pt,bold,center\"/>");

                    posten.append(
                        "</header><header><specification width=\"40%\" border=\"68\"/>");
                    posten.append(
                        "<text content=\"Bezeichnung der Leistung\" style=\""
                            + font
                            + ",9pt,bold,center\"/>");
                    posten.append(
                        "</header><header><specification width=\"14%\" border=\"68\"/>");
                    posten.append(
                        "<text content=\"Einzelpreis\" style=\""
                            + font
                            + ",9pt,bold,center\"/>");
                    posten.append(
                        "</header><header><specification width=\"10%\" border=\"68\"/>");
                    posten.append(
                        "<text content=\"Anzahl\" style=\""
                            + font
                            + ",9pt,bold,center\"/>");
                    posten.append(
                        "</header><header><specification width=\"15%\" border=\"68\"/>");
                    posten.append(
                        "<text content=\"Gesamt\" style=\""
                            + font
                            + ",9pt,bold,center\"/>");
                    posten.append("</header></row>");

                    CurrencyNumber sum =
                        new CurrencyNumber(0.0, CurrencyNumber.EUR);

                    for (int pos = 0; pos < n; pos++) {
                        BillEntry be = billEntries[pos];
                        RechnungsPosten rp = be.getItem();

                        String gebueH = rp.getGebueH();
                        String specification = rp.getText();
                        String goae = rp.getGOAE();
                        int currency =
                            rp.isEuro()
                                ? CurrencyNumber.EUR
                                : CurrencyNumber.DM;
                        String price =
                            new CurrencyNumber(rp.getPreis(), currency)
                                .toCurrency(
                                    MainFrame.getApplication().getCurrency())
                                .toString();
                        String count = nf.format(be.getCount());

                        double t = rp.getPreis() * be.getCount();
                        CurrencyNumber cn =
                            new CurrencyNumber(t, currency).toCurrency(
                                MainFrame.getApplication().getCurrency());
                        String total = cn.toString();
                        sum.add(cn.round(2));

                        posten.append("<row><specification/>");
                        posten.append("<data><specification width=\"12%\"/>");
                        posten.append(
                            "<text content=\""
                                + ((pos == 0) ? date : "&#160;")
                                + "\" style=\""
                                + font
                                + ",9pt\"/>");
                        posten.append(
                            "</data><data><specification width=\"9%\"/>");

                        if (rechnung.isGoae())
                            posten.append(
                                "<text content=\""
                                    + XMLTool.toXMLString(goae)
                                    + "\" style=\""
                                    + font
                                    + ",9pt,right\"/>");
                        else
                            posten.append(
                                "<text content=\""
                                    + XMLTool.toXMLString(gebueH)
                                    + "\" style=\""
                                    + font
                                    + ",9pt,right\"/>");
                        posten.append(
                            "</data><data><specification width=\"40%\"/>");
                        posten.append(
                            "<text content=\""
                                + XMLTool.toXMLString(specification)
                                + "\" style=\""
                                + font
                                + ",9pt\"/>");
                        posten.append(
                            "</data><data><specification width=\"14%\"/>");
                        posten.append(
                            "<text content=\""
                                + XMLTool.toXMLString(price)
                                + "\" style=\""
                                + font
                                + ",9pt,right\"/>");
                        posten.append(
                            "</data><data><specification width=\"10%\"/>");
                        posten.append(
                            "<text content=\""
                                + count
                                + "\" style=\""
                                + font
                                + ",9pt,right\"/>");
                        posten.append(
                            "</data><data><specification width=\"15%\"/>");
                        posten.append(
                            "<text content=\""
                                + XMLTool.toXMLString(total)
                                + "\" style=\""
                                + font
                                + ",9pt,right\"/></data>");
                        posten.append("</row>");
                    }

                    if (!MainFrame.getApplication().isEuro()) {
                        sum = sum.toCurrency(CurrencyNumber.DM);
                    }

                    posten.append("</table>");

                    dProvider.putData("TABLE", posten.toString());
                    dProvider.putData("ID", billID);
                    dProvider.putData("DATE", date);
                    dProvider.putData("ADDRESS", XMLTool.toXMLString(address));
                    dProvider.putData("TEXT", XMLTool.toXMLString(text));
                    //            dProvider.putData("DIAGNOSE", XMLTool.toXMLString(diagnose));
                    dProvider.putData(
                        "TOTAL",
                        XMLTool.toXMLString(sum.toString()));
                    dProvider.putData(
                        "CLOSING",
                        XMLTool.toXMLString(view.getGreetings()));
                    

                    TemplatePrinter tp =
                        new TemplatePrinter(
                            prop.getProperty("style"),
                            prop.getProperty("header"),
                            prop.getProperty("bill.content"),
                            prop.getProperty("footer"),
                            new String[] { prop.getProperty("frame")},
                            prop.getProperty("page"),
                            dProvider);

                    try {
                        MainFrame.getApplication().setWaitCursor();
                        for (int i = 0; i < view.getCopyCount(); i++)
                        	tp.print();
                    } catch (Exception e) {
                        new ErrorDisplay(
                            e,
                            "Fehler beim Ausdruck!",
                            "Drucken...",
                            MainFrame.getApplication());
                    } finally {
                        MainFrame.getApplication().setDefaultCursor();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}