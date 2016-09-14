package main.java.de.baltic_online.mediknight;

import java.awt.Component;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import de.baltic_online.borm.TraceConstants;
import de.baltic_online.borm.Tracer;
import main.java.de.baltic_online.mediknight.domain.KnightObject;
import main.java.de.baltic_online.mediknight.domain.Lock;
import main.java.de.baltic_online.mediknight.domain.Patient;
import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;
import main.java.de.baltic_online.mediknight.printing.FOPrinter;
import main.java.de.baltic_online.mediknight.util.ErrorDisplay;
import main.java.de.baltic_online.mediknight.util.MediknightUtilities;
import main.java.de.baltic_online.mediknight.widgets.UndoUtilities;
import main.java.de.baltic_online.mediknight.widgets.YinYangDialog;


public class DiagnosisPresenter implements Presenter, Commitable, Observer {

    DiagnosisModel model;
    DiagnosisPanel view;


    public DiagnosisPresenter() {
	this( new DiagnosisModel() );
    }


    public DiagnosisPresenter( final DiagnosisModel model ) {
	this.model = model;
    }


    @Override
    public void activate() {
	view.activate();
    }


    @Override
    public void commit() {
	Lock lock = null;
	try {
	    // We must not save all diagnosis at all because this
	    // will raise concurrency conflicts.

	    /*
	     * TagesDiagnose[] diagnosen = (TagesDiagnose[])view.getDiagnosen(); for (int i = 0; i < diagnosen.length; i++) { diagnosen[i].save(); }
	     */

	    final Patient p = model.getPatient();
	    lock = p.acquireLock( LockingInfo.getAspect( p, null ) );
	    if( lock != null ) {
		p.setErstDiagnose( view.getFirstDiagnose() );
		p.save();
	    }
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Speichern fehlgeschlagen!" );
	} finally {
	    try {
		lock.release();
	    } catch( final Exception x ) {
	    }
	}
    }


    private void commit( final Component component, final LockingInfo.Data data ) throws SQLException {
	final Tracer tracer = MainFrame.getTracer();

	try {
	    // if (component instanceof DayDiagnosisEntryPanel) {
	    // // Reach this if we alter a diagnosis
	    // TagesDiagnose diagnose = ((DayDiagnosisEntryPanel)
	    // component).getDiagnose();
	    // tracer.trace(TraceConstants.DEBUG, "Save diagnosis " + diagnose);
	    // diagnose.save();
	    // } else
	    if( component instanceof JTextArea ) {
		// Reach this if we alter the first diagnosis
		final Patient patient = model.getPatient();
		patient.setErstDiagnose( view.getFirstDiagnose() );
		tracer.trace( TraceConstants.DEBUG, "Save patient " + patient );
		patient.save();
	    }
	    // Otherwise nothing to do
	} catch( final RuntimeException x ) {
	    new ErrorDisplay( x, "Speichern fehlgeschlagen!" );
	}
    }


    @Override
    public Component createView() {
	final DiagnosisPanel panel = new DiagnosisPanel();
	panel.setPresenter( this );

	new LockingListener( this ).applyTo( UndoUtilities.getMutables( panel ) );

	view = panel;
	return panel;
    }


    public DiagnosisModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	return view.getLastFocusComponent();
    }


    /**
     * Macht einen Ausdruck von allen Tagesdiagnosen.
     */
    public void printDiagnosis() {
	final YinYangDialog d = new YinYangDialog( JOptionPane.getFrameForComponent( view ), MainFrame.NAME );
	d.setStatusText( "Drucke ..." );
	d.run( new Runnable() {

	    @Override
	    public void run() {
		try {
		    final Properties props = MainFrame.getProperties();
		    final FOPrinter fop = new FOPrinter( props.getProperty( "diagnosis.xml" ), props.getProperty( "diagnosis.xsl" ) );
		    final Patient patient = model.getPatient();
		    final String ersteDiagnose = model.getPatient().getErstDiagnose();
		    final List< TagesDiagnose > tagesDiagnosen = model.getTagesDiagnosen();

		    // füge Dauerdiagnose und Name des Patienten in die Datei
		    fop.addData( "Name", patient.getFullname() );
		    fop.addData( "Dauer", ersteDiagnose );

		    // füge die Tagesdiagnosen in die Datei ein
		    for( int i = 0; i < tagesDiagnosen.size(); i++ ) {
			final TagesDiagnose td = tagesDiagnosen.get( i );
			if( td.getText() != null && td.getText().length() > 0 ) {
			    final String[] diag = new String[2];
			    if( td.getDatum() != null ) {
				diag[0] = MediknightUtilities.formatDate( td.getDatum() );
			    } else {
				diag[0] = "";
			    }
			    diag[1] = td.getText();
			    fop.addTagToFather( "Tagesdiagnose", "", "TagesDiagnosen" );
			    fop.addTag( "Datum", diag[0], "TagesDiagnosen" );
			    fop.addTag( "Text", diag[1], "TagesDiagnosen" );
			}
		    }

		    // drucke die Datei aus
		    fop.print();

		    /*
		     * DataProvider dProvider = new DataProvider(null); Properties prop = MainFrame.getProperties(); String lf =
		     * System.getProperty("line.separator"); Patient patient = model.getPatient(); String erstDiagnose = model.getPatient().getErstDiagnose();
		     * List tagesDiagnosen = model.getTagesDiagnosen(); String name = patient.getFullname(); dProvider.putData("NAME", name); Map map =
		     * PrintSettingsPresenter.getSettings(); String font = (String) map.get("print.font"); dProvider.putData("FONT", font);
		     * dProvider.putData("DAUER", erstDiagnose); List printList = new ArrayList(); for (int i = 0; i < tagesDiagnosen.size(); i++) {
		     * TagesDiagnose td = (TagesDiagnose) tagesDiagnosen.get(i); if ((td.getText() != null) && (td.getText().length() > 0)) { String[] diag =
		     * new String[2]; if (td.getDatum() != null) diag[0] = MediknightUtilities.formatDate( td.getDatum()); else diag[0] = ""; diag[1] =
		     * td.getText(); printList.add(diag); } } String[][] printArray = (String[][]) printList.toArray(new String[0][2]);
		     * dProvider.putData("DIAGNOSEN", printArray); TemplatePrinter tp = new TemplatePrinter( prop.getProperty("style"),
		     * prop.getProperty("header"), prop.getProperty("diagnosis.content"), prop.getProperty("footer"), new String[] { prop.getProperty("frame")
		     * }, prop.getProperty("page"), dProvider); try { tp.print(); } catch (Exception e) { new ErrorDisplay( e, "Fehler beim Ausdruck!",
		     * "Drucken...", MainFrame.getApplication()); }
		     */
		} catch( final IOException e ) {
		    e.printStackTrace();
		} catch( final Exception e ) {
		    e.printStackTrace();
		}
	    }
	} );
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
	if( component == null ) {
	    return;
	}
	try {
	    knightObject.recall();
	    // if (knightObject instanceof TagesDiagnose) {
	    // DayDiagnosisEntryPanel ddep = (DayDiagnosisEntryPanel) component;
	    // TagesDiagnose d = (TagesDiagnose) knightObject;
	    // ddep.set(d.getDatum(), d.getText());
	    // }
	    if( knightObject instanceof Patient ) {
		final Patient p = (Patient) knightObject;
		view.setFirstDiagnosis( p.getErstDiagnose() );
	    }
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Neuladen der Komponente fehlgeschlagen!" );
	}
    }


    public void saveTagesdiagnose( final TagesDiagnose diagnose ) {
	try {
	    diagnose.save();
	} catch( final java.sql.SQLException sqle ) {
	}
    }


    public void setSelectedDiagnose( final TagesDiagnose diagnose ) {
	MainFrame.getApplication().setCurrentDiagnosis( diagnose );
    }


    public void showBill() {
	MainFrame.getApplication().bill();
    }


    public void showMedication() {
	MainFrame.getApplication().medication();
    }


    @Override
    public void update( final Observable o, final Object arg ) {
	try {
	    final LockingInfo li = (LockingInfo) o;
	    final Lock lock = li.getLastLock();
	    if( lock != null ) {
		final Component c = li.getComponent();
		if( c != null ) {
		    commit( c, (LockingInfo.Data) arg );
		}
		lock.release();
		li.setLastLock( null );
	    }
	} catch( final SQLException x ) {
	    new ErrorDisplay( x, "Speichern fehlgeschlagen!" );
	}
    }
}