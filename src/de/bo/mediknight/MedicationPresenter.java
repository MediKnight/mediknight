package de.bo.mediknight;

import java.util.*;
import java.util.List;
import java.io.*;
import java.awt.*;
import java.sql.*;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.*;

import de.bo.print.boxer.*;
import de.bo.print.te.*;
import de.bo.print.jpf.*;

import de.bo.mediknight.tools.*;
import de.bo.mediknight.widgets.*;
import de.bo.mediknight.borm.TraceConstants;

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
        final YinYangDialog d = new YinYangDialog( JOptionPane.getFrameForComponent( view ) , MainFrame.NAME );
        d.setStatusText("Drucke ...");
        d.run(new Runnable() {
            public void run() {
		try {
		    Patient patient = model.getDiagnose().getPatient();

		    Properties prop = MainFrame.getApplication().getProperties();
		    //Properties prop = ((NewAppWindow) AppWindow.getApplication()).getProperties();
		    String style = prop.getProperty("style");
		    String footer = prop.getProperty("footer");
		    String header = prop.getProperty("header");
		    String page = prop.getProperty("page");
		    String frame = prop.getProperty("frame");
		    String content = prop.getProperty("medication.content");
		    String[] frameArray = new String[] {frame};

		    String lf = System.getProperty("line.separator");
		    String date = "";
		    MainFrame.getApplication().setWaitCursor();
		    date = MediknightUtilities.formatDate( model.getDiagnose().getVerordnung().getDatum());

		    String title = (patient.getTitel() != null) ? patient.getTitel():"";
		    String firstName =
			(patient.getVorname() != null) ? patient.getVorname():"";
		    String address =
			patient.getAnrede()+lf+
			((title.equals(""))?"":(title+" "))+
			((firstName.equals(""))?"":(firstName+" "))+patient.getName()+lf+
			patient.getAdresse1()+lf+
			patient.getAdresse2()+lf+
			patient.getAdresse3();

		    StringBuffer sb = new StringBuffer( view.getVerordnungstext() );
/*		    Verordnung v = model.getDiagnose().getVerordnung();
		    MedicationEntry[] entries = MedicationEntry.loadEntries(v);
		    for (int i=0; i<entries.length; i++) {
			sb.append(entries[i].getItem());
			sb.append(lf);
		    }*/

		    Map map = PrintSettingsPresenter.getSettings();

		    TemplatePrinter tp = new TemplatePrinter();
		    DataProvider dProvider = new DataProvider(null);

		    dProvider.putData("DATE", date);
		    dProvider.putData("ADDRESS", XMLTool.toXMLString(address));
		    dProvider.putData("MEDICATION", XMLTool.toXMLString(sb.toString()));

		    String font = (String)map.get("print.font");

		    BufferedReader reader = new BufferedReader(new StringReader((String)map.get("print.logo")));
		    StringBuffer buffer = new StringBuffer();
		    String s;

		    if ( (s = reader.readLine()) == null)
			for (int i = 0; i < 7; i++)
			    buffer.append("<text content=\"&#160;\" style=\"Times,11pt\"/>");
		    else
			do {
			    XMLTool.parseString(s, font, buffer);
			} while ( (s = reader.readLine()) != null);

		    dProvider.putData("LOGO", buffer.toString() );

		    dProvider.putData("FONT", font);

		    reader = new BufferedReader(new StringReader((String)map.get("print.medication.final")));
		    buffer = new StringBuffer();

		    while ( (s = reader.readLine()) != null) {
			XMLTool.parseString(s, font, buffer);
		    }

		    dProvider.putData("FINAL", buffer.toString() );

		    String sender = (String)map.get("print.sender");
		    if (sender.length() < 1)
			dProvider.putData("SENDER", "");
		    else dProvider.putData("SENDER", XMLTool.toXMLString(sender));

		    try {
			tp.print( style, header, content, footer, frameArray, page, dProvider);
		    } catch (java.lang.Exception e) {
			new ErrorDisplay(e,"Fehler beim Ausdrucken!", "Drucken...", MainFrame.getApplication());
		    } finally {
			MainFrame.getApplication().setDefaultCursor();
		    }
		} catch (Exception x) {
		    new ErrorDisplay(x,"Speichern fehlgeschlagen!");
		}
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