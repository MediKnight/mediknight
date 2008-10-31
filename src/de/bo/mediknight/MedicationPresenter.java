package de.bo.mediknight;

import java.awt.Component;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.bo.mediknight.domain.KnightObject;
import de.bo.mediknight.domain.Patient;
import de.bo.mediknight.domain.TagesDiagnose;
import de.bo.mediknight.printing.MedicationPrinter;
import de.bo.mediknight.tools.PrintSettingsPresenter;
import de.bo.mediknight.util.ErrorDisplay;
import de.bo.mediknight.util.MediknightUtilities;
import de.bo.mediknight.widgets.UndoUtilities;
import de.bo.mediknight.widgets.YinYangDialog;
import de.bo.mediknight.xml.CreateXMLFile;
import de.bo.mediknight.xml.Transform;

public class MedicationPresenter implements Presenter, Commitable, Observer {

    MedicationPanel view;
    MedicationModel model;

    public MedicationPresenter() {
        this(new MedicationModel());
    }

    public MedicationPresenter(MedicationModel model) {
        this.model = model;
    }

    public MedicationModel getModel() {
        return model;
    }

    public void activate() {}

    public Component createView() {
        MedicationPanel panel = new MedicationPanel( this );
        view = panel;
        view.setFocusOnMedication();
        new LockingListener(this).applyTo(UndoUtilities.getMutables(panel));

        return panel;
    }

    public void commit() {
        // warum werden hier leerzeilen entfernt?
        StringTokenizer tokenizer = new StringTokenizer( view.getMedication(), "\n" );
        List entries = new ArrayList();

        while( tokenizer.hasMoreTokens() ) {
            entries.add( new MedicationEntry( tokenizer.nextToken() ) );
        }

        MedicationEntry[] medications = (MedicationEntry[]) entries.toArray( new MedicationEntry[0] );
        MedicationEntry.saveEntries( model.getVerordnung(), medications );

        try {
            model.getVerordnung().save();
        } catch( SQLException x ) {
            new ErrorDisplay(x,"Speichern fehlgeschlagen!");
        }
    }

    public void setFocusOnMedication() {
    }

    public Component getResponsibleComponent() {
        /** @todo: implement this! */
        return null;
    }

    public void reload(Component component,KnightObject knightObject) {
        try {
            ((TagesDiagnose)knightObject).getVerordnung().recall();
            view.update();

        } catch (SQLException ex) {
        }

    }

    public void update(Observable o,Object arg) {
        try {
            ((LockingInfo)o).releaseLastLock();
        }
        catch ( SQLException x ) {
            new ErrorDisplay(x,"Speichern fehlgeschlagen!");
        }
    }

    public void changeMedication( String text ) {
        StringBuffer sb = new StringBuffer( view.getVerordnungstext() );

        sb.append("\n" + text);
        model.getDiagnose().setText( sb.toString() );
        StringTokenizer tokenizer = new StringTokenizer( sb.toString() , "\n" );
        List entries = new ArrayList();

        while( tokenizer.hasMoreTokens() ) {
            entries.add( new MedicationEntry( tokenizer.nextToken() ) );
        }

        MedicationEntry[] medications = (MedicationEntry[]) entries.toArray( new MedicationEntry[0] );
/*        MedicationEntry.saveEntries( model.getVerordnung(), medications );

        try {
            model.getVerordnung().save();
        } catch( SQLException e ) {*/
//            e.printStackTrace(); /** @todo Exception reporting. */
//        }
        view.update();
    }

    public void printMedication() {
        final YinYangDialog d =
            new YinYangDialog(
                JOptionPane.getFrameForComponent(view),
                MainFrame.NAME);
        d.setStatusText("Drucke ...");
        d.run(new Runnable() {
            public void run() {
            	try {
            		Patient patient = model.getDiagnose().getPatient();
            		// hier sind alle Daten für das Logo usw. gespeichert
            		Map props = PrintSettingsPresenter.getSettings();    

               		MedicationPrinter printer = 
            			new MedicationPrinter("verordnung.xml", 
            								  "verordnung2.xsl");
            		printer.addData("Patient/Title", patient.getTitel());
            		printer.addData("Patient/Anrede", patient.getAnrede());
            		printer.addData("Patient/Name", patient.getFullname());
            		
            		printer.addData("Datum", MediknightUtilities.formatDate(
                              model.getDiagnose().getVerordnung().getDatum()));
            		
            		printer.addData("Patient/Address1", patient.getAdresse1());
            		printer.addData("Patient/Address2", patient.getAdresse2());
            		printer.addData("Patient/Address3", patient.getAdresse3());
            		
            		printer.addData("Abschluss",
            						(String) props.get("print.medication.final"));
            		printer.addData("Absender", 
            						(String) props.get("print.sender"));
            		printer.addData("Betreff", "Verordnung:");

               		// das Logo unterteilen und in die Datei mit aufnehmen
            		String logo = (String) props.get("print.logo");
            		String lf = System.getProperty("line.separator");
            		StringTokenizer token = new StringTokenizer(logo,lf);
            		int i=1;
            		while(token.hasMoreElements()) {
            			if(i==1) {
            				printer.addData("Ueberschrift", token.nextToken());
            				printer.insertValues();
            				i++;
            			}
            			else {
            				String str = token.nextToken();
            				
            				printer.addElement("Zeile", str, "LogoInhalt");
            			}
            		}
 
            		// den Verordnugstext unterteilen und in die Datei aufnehmen            		
            		String text = view.getVerordnungstext();
            		
            		token = new StringTokenizer(text,lf);
            		while(token.hasMoreElements()) {
            			String str = token.nextToken();
            			printer.addElement("TextBlock", str, "Text");
            		} 
            		
            		printer.printPage();
 
 /*           		HashMap map = new HashMap();
            		
            		map.put("Patient/Title", patient.getTitel());
            		map.put("Patient/Anrede", patient.getAnrede());
            		map.put("Patient/Name", patient.getFullname());
            		
            		map.put("Datum", MediknightUtilities.formatDate(
                              model.getDiagnose().getVerordnung().getDatum()));
            		
            		map.put("Patient/Address1", patient.getAdresse1());
            		map.put("Patient/Address2", patient.getAdresse2());
            		map.put("Patient/Address3", patient.getAdresse3());
            		
            		map.put("Abschluss",props.get("print.medication.final"));
            		map.put("Absender", props.get("print.sender"));
            		map.put("Betreff", "Verordnung:");
            		
            		CreateXMLFile create = new CreateXMLFile("/Users/bs-macosx/Desktop/mediknight files/verordnung.xml");;
            		//create.insertValues(map);
            		//TODO mit insertValues noch umgestalten
               		// das Logo unterteilen und in die Datei mit aufnehmen
            		String logo = (String) props.get("print.logo");
            		String lf = System.getProperty("line.separator");
            		StringTokenizer token = new StringTokenizer(logo,lf);
            		int i=1;
            		while(token.hasMoreElements()) {
            			if(i==1) {
            				map.put("Ueberschrift", token.nextToken());
            				create.insertValues(map);
            				i++;
            			}
            			else {
            				String str = token.nextToken();
            				
            				create.addElement("Zeile", str, "LogoInhalt");
            			}
            		}
            		
            		// den Verordnugstext unterteilen und in die Datei aufnehmen            		
            		String text = view.getVerordnungstext();
            		
            		token = new StringTokenizer(text,lf);
            		while(token.hasMoreElements()) {
            			String str = token.nextToken();
            			create.addElement("TextBlock", str, "Text");
            		} 
            		
            		String dir = "/Users/bs-macosx/Desktop/mediknight files/";
                    Transform.xml2pdf(new File(dir, "output.xml"), 
                    				  new File(dir, "verordnung2.xsl"),
                    				  dir);
            		*/
            	} catch(Exception e) {
            		e.printStackTrace();
            	}
                /*try {
                    Patient patient = model.getDiagnose().getPatient();

                    Properties prop =
                        MainFrame.getProperties();

                    String lf = System.getProperty("line.separator");
                    String date = "";
                    MainFrame.getApplication().setWaitCursor();
                    date =
                        MediknightUtilities.formatDate(
                            model.getDiagnose().getVerordnung().getDatum());

                    String title =
                        (patient.getTitel() != null) ? patient.getTitel() : "";
                    String firstName =
                        (patient.getVorname() != null)
                            ? patient.getVorname()
                            : "";
                    String address =
                        patient.getAnrede()
                            + lf
                            + ((title.equals("")) ? "" : (title + " "))
                            + ((firstName.equals("")) ? "" : (firstName + " "))
                            + patient.getName()
                            + lf
                            + patient.getAdresse1()
                            + lf
                            + patient.getAdresse2()
                            + lf
                            + patient.getAdresse3();

                    StringBuffer sb =
                        new StringBuffer(view.getVerordnungstext());

                    Map map = PrintSettingsPresenter.getSettings();

                    DataProvider dProvider = new DataProvider(null);

                    dProvider.putData("DATE", date);
                    dProvider.putData("ADDRESS", XMLTool.toXMLString(address));
                    dProvider.putData(
                        "MEDICATION",
                        XMLTool.toXMLString(sb.toString()));

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
                                (String) map.get("print.medication.final")));
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

                    TemplatePrinter tp =
                        new TemplatePrinter(
                            prop.getProperty("style"),
                            prop.getProperty("header"),
                            prop.getProperty("medication.content"),
                            prop.getProperty("footer"),
                            new String[] { prop.getProperty("frame")},
                            prop.getProperty("page"),
                            dProvider);

                    try {
                        tp.print();
                    } catch (java.lang.Exception e) {
                        new ErrorDisplay(
                            e,
                            "Fehler beim Ausdrucken!",
                            "Drucken...",
                            MainFrame.getApplication());
                    } finally {
                        MainFrame.getApplication().setDefaultCursor();
                    }
                } catch (Exception x) {
                    new ErrorDisplay(x, "Speichern fehlgeschlagen!");
                }*/
                MainFrame.getApplication().setDefaultCursor();
            }
        });
    }



    public static void main(String[] args) {
        JFrame f = new JFrame( "Medication Presenter Example" );

    //    f.getContentPane().add( example().createView() );

        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.pack();
        f.show();
    }
}