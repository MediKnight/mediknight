package de.bo.mediknight;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import java.sql.*;

import de.bo.print.boxer.*;
import de.bo.print.te.*;
import de.bo.print.jpf.*;
import de.bo.mediknight.tools.PrintSettingsPresenter;

import de.bo.mediknight.borm.TraceConstants;
import de.bo.mediknight.borm.Tracer;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.widgets.UndoUtilities;
import de.bo.mediknight.widgets.YinYangDialog;
import de.bo.mediknight.util.ErrorDisplay;
import de.bo.mediknight.util.MediknightUtilities;

public class DiagnosisPresenter implements Presenter, Commitable, Observer {

    DiagnosisModel model;
    DiagnosisPanel view;

    public static DiagnosisPresenter example() {
        return new DiagnosisPresenter() {
            public void searchFor( String s ) {
                String[] patients = new String[] {
                    "Naujok, Christoph",
                    "Mueller-Lund, Soenke",
                    "Bernhardt, Jan",
                    "von Mucheln, Eike" };

//                getModel().setFoundPatients( Arrays.asList( patients ) );
            }
        };
    }

    public DiagnosisPresenter() {
        this(new DiagnosisModel());
    }

    public DiagnosisPresenter(DiagnosisModel model) {
        this.model = model;
    }

    public DiagnosisModel getModel() {
        return model;
    }

    public void activate() {
        view.activate();
    }

    public Component createView() {
        DiagnosisPanel panel = new DiagnosisPanel();
        panel.setPresenter(this);

        new LockingListener(this).applyTo(
            UndoUtilities.getMutables(panel));

        view = panel;
        return panel;
    }

    public void printDiagnosis() {
        final YinYangDialog d = new YinYangDialog( JOptionPane.getFrameForComponent( view ) , MainFrame.NAME );
        d.setStatusText("Drucke ...");
        d.run(new Runnable() {
            public void run() {
		try {
		    TemplatePrinter tp = new TemplatePrinter();
		    DataProvider dProvider = new DataProvider(null);

		    Properties prop = MainFrame.getApplication().getProperties();
		    String style = prop.getProperty("style");
		    String footer = prop.getProperty("footer");
		    String header = prop.getProperty("header");
		    String page = prop.getProperty("page");
		    String frame = prop.getProperty("frame");
		    String content = prop.getProperty("diagnosis.content");
		    String[] frameArray = new String[] {frame};

		    String lf = System.getProperty("line.separator");

		    Patient patient = model.getPatient();
		    String erstDiagnose = model.getPatient().getErstDiagnose();
		    List tagesDiagnosen = model.getTagesDiagnosen();

		    String name = patient.getFullname();

		    dProvider.putData("NAME", name);

		    Map map = PrintSettingsPresenter.getSettings();

		    String font = (String)map.get("print.font");

		    dProvider.putData("FONT", font);

		    dProvider.putData("DAUER", erstDiagnose);
		    List printList = new ArrayList();

		    for (int i = 0; i < tagesDiagnosen.size(); i++) {
			TagesDiagnose td = (TagesDiagnose)tagesDiagnosen.get( i );
			if ((td.getText() != null) && (td.getText().length() > 0)) {
			    String[] diag = new String[2];
			    if (td.getDatum() != null)
			        diag[0] = MediknightUtilities.formatDate( td.getDatum() );
			    else
				diag[0] = "";
			    diag[1] = td.getText();
			    printList.add( diag );
			}
		    }
		    String[][] printArray = (String[][])printList.toArray( new String[0][2] );
		    dProvider.putData( "DIAGNOSEN", printArray );

		    try {
			tp.print( style, header, content, footer, frameArray, page, dProvider);
		    } catch (Exception e) {
			new ErrorDisplay(e, "Fehler beim Ausdruck!", "Drucken...", MainFrame.getApplication());
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
            }
        });
    }

    public void commit() {
        Lock lock = null;
        try {
            // We must not save all diagnosis at all because this
            // will raise concurrency conflicts.

 /*           TagesDiagnose[] diagnosen = (TagesDiagnose[])view.getDiagnosen();
            for (int i = 0; i < diagnosen.length; i++) {
                diagnosen[i].save();
            }*/

            Patient p = model.getPatient();
            lock = p.acquireLock(LockingInfo.getAspect(p,null));
            if ( lock != null ) {
                p.setErstDiagnose(view.getFirstDiagnose());
                p.save();
            }
        }
        catch (SQLException x) {
            new ErrorDisplay(x,"Speichern fehlgeschlagen!");
        }
        finally {
            try {
                lock.release();
            }
            catch ( Exception x ) {
            }
        }
    }

    public void saveTagesdiagnose( TagesDiagnose diagnose ) {
        try {
            diagnose.save();
        } catch (java.sql.SQLException sqle) {
        }
    }

    public Component getResponsibleComponent() {
        return view.getLastFocusComponent();
    }

    public void reload(Component component,KnightObject knightObject) {
        if (component == null) return;
        try {
            knightObject.recall();
            if ( knightObject instanceof TagesDiagnose ) {
                DayDiagnosisEntryPanel ddep = (DayDiagnosisEntryPanel)component;
                TagesDiagnose d = (TagesDiagnose)knightObject;
                ddep.set(d.getDatum(),d.getText());
            }
            if ( knightObject instanceof Patient ) {
                Patient p = (Patient)knightObject;
                view.setFirstDiagnosis(p.getErstDiagnose());
            }
        }
        catch (SQLException x) {
            new ErrorDisplay(x,"Neuladen der Komponente fehlgeschlagen!");
        }
    }

    private void commit(Component component,LockingInfo.Data data)
        throws SQLException {

        Tracer tracer = MainFrame.getTracer();

        try {
            if ( component instanceof DayDiagnosisEntryPanel ) {
                // Reach this if we alter a diagnosis
                TagesDiagnose diagnose = ((DayDiagnosisEntryPanel)component).getDiagnose();
                tracer.trace(TraceConstants.DEBUG,"Save diagnosis "+diagnose);
                diagnose.save();
            }
            else if ( component instanceof JTextArea ) {
                // Reach this if we alter the first diagnosis
                Patient patient = model.getPatient();
                patient.setErstDiagnose(view.getFirstDiagnose());
                tracer.trace(TraceConstants.DEBUG,"Save patient "+patient);
                patient.save();
            }
            // Otherwise nothing to do
        }
        catch ( RuntimeException x ) {
            new ErrorDisplay(x,"Speichern fehlgeschlagen!");
        }
    }

    public void showBill() {
        MainFrame.getApplication().bill();
    }

    public void showMedication() {
        MainFrame.getApplication().medication();
    }

    public void setSelectedDiagnose(TagesDiagnose diagnose) {
        MainFrame.getApplication().setCurrentDiagnosis(diagnose);
    }

    public void update(Observable o,Object arg) {
        try {
            LockingInfo li = (LockingInfo)o;
            Lock lock = li.getLastLock();
            if ( lock != null ) {
                Component c = li.getComponent();
                if ( c != null ) {
                    commit(c,(LockingInfo.Data)arg);
                }
                lock.release();
                li.setLastLock(null);
            }
        }
        catch ( SQLException x ) {
            new ErrorDisplay(x,"Speichern fehlgeschlagen!");
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame( "Search Presenter Example" );

        f.getContentPane().add( example().createView() );

        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.pack();
        f.show();
    }
}