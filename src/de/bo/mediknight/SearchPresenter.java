
package de.bo.mediknight;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import java.sql.*;

import de.bo.mediknight.domain.*;


public class SearchPresenter implements Presenter, Commitable  {

    SearchModel model;
    SearchPanel view;

    // specifies whether to trigger ListSelectionEvents
    boolean triggerSelectionEvents = true;

    public SearchPresenter() {
        this(new SearchModel());
    }

    public SearchPresenter( SearchModel model ) {
        this.model = model;

        try {
            PatientHistory history = PatientHistory.getInstance();
            model.setRecentPatients(history.load());
        } catch (Exception e) {
            //de.bo.mediknight.panes.MediknightPane.showExceptionDialog(e);
            e.printStackTrace();
        }
    }

    public SearchModel getModel() {
        return model;
    }

    public void activate() {
        view.setFocusOnSearchTF();
    }

    public void commit() {
        try {
            model.getPatientHistory().save();
        } catch (SQLException e) {
            e.printStackTrace(); /** @todo Exception */
        }
    }

    public Component getResponsibleComponent() {
        return null;
    }

    public void reload(Component component,KnightObject knightObject) {
    }

    public Component createView() {
        SearchPanel panel = new SearchPanel();
        panel.setPresenter( this );
        view = panel;
        view.setFocusOnSearchTF();

        return panel;
    }


    public void searchFor( String s ) {

        List<Patient> data = null;
        try {
            MainFrame.getApplication().setWaitCursor();
            data = Patient.retrieve(s);
            Collections.sort(data);

            model.setFoundPatients(data);
        } catch ( SQLException e ) {
            e.printStackTrace();
            /** @todo Exceptions have to be reported visually. */
        }
        finally {
            MainFrame.getApplication().setDefaultCursor();
//            resetState(data.length == 0);

            // don't set the focus on an empty result list
            if(data.size() > 0)
                view.setFocusOnPatientList();
            else {
//                view.setFocusOnSearchTF();

                view.setSelectedTF();
            }
        }
    }


    public void indexButtonPressed( JButton b ) {
        searchFor( b.getText() );
    }

    public void patientSelected() {
        Patient patient = (Patient) view.getSelection();
        if(patient == null) {
            JOptionPane.showMessageDialog(
                view,
                "Bitte wählen sie einen Patienten!",
                "Kein Patient gewählt",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        model.addRecentPatient(patient);

        MainFrame.getApplication().setWaitCursor() ;
        
        try {
            MainFrame.getApplication().selectPatient(patient);
        }
        catch ( Exception e ) {
            e.printStackTrace();
            /** @todo Exceptions have to be reported visually. */
        }
        finally {
            MainFrame.getApplication().setDefaultCursor();
        }
    }

    public void clearHistory() {
        model.setRecentPatients(new ArrayList<Patient>());
    }

    public void clearSelection(JList<Patient> list) {
        if(!triggerSelectionEvents)
            return;

        triggerSelectionEvents = false;
        list.setSelectedIndices(new int[] {});
        triggerSelectionEvents = true;
    }


    public static void main(String[] args) {
        JFrame f = new JFrame( "Search Presenter Example" );

 //       f.getContentPane().add( example().createView() );

        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.pack();
        f.show();
    }
}