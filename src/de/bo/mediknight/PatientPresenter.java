package de.bo.mediknight;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import java.sql.*;

import de.bo.mediknight.domain.*;
import de.bo.mediknight.widgets.UndoUtilities;
import de.bo.mediknight.util.ErrorDisplay;

public class PatientPresenter implements Presenter, Commitable, Observer {

    PatientPanel view;

    PatientModel model;

    public PatientPresenter() {
        this(new PatientModel());
    }

    public PatientPresenter(PatientModel model) {
        this.model = model;
    }

    public PatientModel getModel() {
        return model;
    }

    public void activate() {
    }

    public Component createView() {
        PatientPanel panel = new PatientPanel();
        panel.setPresenter(this);
        view = panel;
        view.setFocusOnCombo();

        new LockingListener(this).applyTo(UndoUtilities.getMutables(panel));

        return panel;
    }

    public void commit() {
        savePatient();
        if (model.getPatient().getFullname().length() > 1) {
            MainFrame.getApplication().setTitle(
                    model.getPatient().getFullname() + " - " + MainFrame.NAME);
            MainFrame.getApplication()
                    .setPatientToNavigator(model.getPatient());
        } else {
            MainFrame.getApplication().setTitle(MainFrame.NAME);
        }
    }

    public Component getResponsibleComponent() {
        return view;
    }

    public void reload(Component component, KnightObject knightObject) {
        try {
            Patient p = (Patient) knightObject;
            p.recall();
            getModel().setPatient(p);
            view.stateChanged(null);
        } catch (SQLException x) {
            new ErrorDisplay(x, "Neuladen der Komponente fehlgeschlagen!");
        }
    }

    public void deletePatient() {
        // We only can delete a patient if we could acquire ALL depending locks.
        Vector locks = new Vector();
        try {
            Patient p = model.getPatient();
            Lock lock = p.acquireLock(LockingInfo.getAspect(p, null));
            if (lock != null) {
                locks.add(lock);
                List l = p.getTagesDiagnosen();
                for (Iterator i = l.iterator(); i.hasNext();) {
                    TagesDiagnose d = (TagesDiagnose) i.next();
                    lock = p.acquireLock(LockingInfo.getAspect(p, d));
                    if (lock != null) {
                        locks.add(lock);
                    } else {
                        throw new MediException("Diagnosis in use.");
                    }
                }
                p.delete();
            } else {
                throw new MediException("Patient in use.");
            }

            MainFrame.getApplication().search();
        } catch (SQLException x) {
            new ErrorDisplay(x, "Löschen fehlgeschlagen!");
        } catch (MediException x) {
            new ErrorDisplay(x, "Löschen ist jetzt nicht möglich!");
        } finally {
            try {
                for (Iterator i = locks.iterator(); i.hasNext();) {
                    ((Lock) i.next()).release();
                }
            } catch (Exception x) {
            }
        }
    }

    public boolean savePatient() {

        if (!view.isPatientOK())
            return false;

        Lock lock = null;

        try {
            Patient p = model.getPatient();
            MainFrame app = MainFrame.getApplication();
            LockingInfo li = app.getLockingInfo();
            lock = p.acquireLock(LockingInfo.getAspect(p, null));//li.getDiagnosis()));
            if (lock != null) {
                view.getContent();
                p.save();
            }
        } catch (SQLException x) {
            new ErrorDisplay(x, "Speichern fehlgeschlagen!");
            return false;
        } finally {
            try {
                lock.release();
            } catch (Exception x) {
            }
        }
        return true;
    }

    public void patientAnlegen() {
        try {
            if (!savePatient()) {
                throw new SQLException("Cannot create patient");
            }
            MainFrame.getApplication().selectPatient(model.getPatient());
        } catch (SQLException x) {
            new ErrorDisplay(x, "Patient anlegen fehlgeschlagen!");
        }
    }

    public void update(Observable o, Object arg) {
        try {
            ((LockingInfo) o).releaseLastLock();
        } catch (SQLException x) {
            new ErrorDisplay(x, "Speichern fehlgeschlagen!");
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Patient Presenter Example");

        //    f.getContentPane().add( example().createView() );

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.show();
    }
}