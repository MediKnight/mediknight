package de.bo.mediknight;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.text.*;

import de.bo.mediknight.util.ErrorDisplay;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.borm.TraceConstants;
import de.bo.mediknight.widgets.UndoUtilities;

public class BillPresenter implements Presenter, Commitable, Observer {

    Bill1Panel viewPage1;
    Bill1Panel view;
    BillModel model;
    private final static double RATE_OF_TAXES = 0.16;

    public BillPresenter() {
    }

    public BillPresenter(BillModel model) {
        this.model = model;
    }

    public BillModel getModel() {
        return model;
    }

    public void activate() {
        view.activate();
    }

    public Component createView() {
        view = new Bill1Panel(model.getRechnung());
        view.setPresenter(this);

        new LockingListener(this).applyTo(view.getBillTable());
        new LockingListener(this).applyTo(UndoUtilities.getMutables(view));

        return view;
    }

    public void commit() {
        model.getRechnung().setGoae( view.isGOAE() );
        try {
            model.getRechnung().save();
        } catch( SQLException e ) {
            ErrorDisplay ed = new ErrorDisplay(e, "Fehler beim Abspeichern der Rechnung!", "Speichern...", view);
        }
    }

    public Component getResponsibleComponent() {
        return null;
    }

    public void reload(Component component,KnightObject knightObject) {
        try {
            ((TagesDiagnose)knightObject).getRechnung().recall();
            view.update();

        } catch (SQLException ex) {
        }
    }

    public void addItem() {
        /** @todo Review the following code and decide if this is still necessary... */
        // The following code is copied from BillPane:
        //
        //--- "Notloesung": nicht schoen, aber notwendig, um voruebergehend
        //    Funktionalitaet zu sichern (-> JTable-Problem beim Entfernen
        //    von Zeilen mit Eingabe-Fokus).
        //    Eigentlich muss nur in der jeweils editierbaren Spalte der
        //    entsprechende Editor modifiziert werden, aber die "get"-
        //    Methode des Spalteneditors liefert stets einen null-Wert.
        //    (Synchronisation?)).
        /* if(view.getSelectedBillItem() != null) {
            TableCellEditor editor = billTable.getDefaultEditor(billTable.getClass());
            editor.cancelCellEditing();
        } */

//            view.billModel.fireTableDataChanged();

            MainFrame app = MainFrame.getApplication();
            LockingInfo li = app.getLockingInfo();
            Patient patient = li.getPatient();
            Lock lock = null;
            try {
                if ( patient != null ) {
                    lock = patient.acquireLock(li.getAspect(), LockingListener.LOCK_TIMEOUT);
                    if ( lock != null ) {
                        RechnungsPosten[] posten = view.getSelectedRechnungsPostenItems();
                        if(posten.length == 0)
                            return;

                        BillEntry entriesToBeAdded[] = new BillEntry[posten.length];
                        for(int i = 0; i < posten.length; ++i) {
                            entriesToBeAdded[i] = new BillEntry();
                            entriesToBeAdded[i].setItem(posten[i]);
                            entriesToBeAdded[i].setCount(1.0);
                        }

                        int numberOfNewEntries = entriesToBeAdded.length;

                        BillEntry[] entries = BillEntry.loadEntries( model.getRechnung() );
                        BillEntry[] newEntries = new BillEntry[entries.length + numberOfNewEntries];

                        int referencePosition = view.getSelectedBillRow();
                        if( referencePosition != -1 )
                            System.arraycopy( entries, 0, newEntries, 0, referencePosition + 1 );

                        referencePosition += 1;

                        System.arraycopy(entriesToBeAdded, 0, newEntries, referencePosition, numberOfNewEntries);
                        System.arraycopy( entries, referencePosition, newEntries,
                                          referencePosition + numberOfNewEntries, entries.length - referencePosition );

                        BillEntry.saveEntries( getModel().getRechnung(), newEntries );
                        // Now inform the model that it has actually changed.
                        model.getRechnung().save();
                    } else
                        return;
                }
            } catch (SQLException sqle) {
            } finally {
                try {
                    lock.release();
                } catch (Exception ex) {
                }
                getModel().fireChangeEvent();
            }
    }

    public void showLetter() {
        commit();
        MainFrame.getApplication().letter();
    }

    public void showMacro() {
        MainFrame.getApplication().macro(view.getSelectedItems());
    }


    public void createMacro() {
        if (view.getSelectedItems().length == 1) {
            int action = JOptionPane.showConfirmDialog(MainFrame.getApplication().getRootPane(),
                "Der Baustein, den Sie erzeugen, wird nur einen Eintrag enthalten\n" +
                "Ist dieses erwünscht?",
                MainFrame.NAME,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if ( action == JOptionPane.NO_OPTION ) {
                return;
            }
        }
        MainFrame.getApplication().createMacro(view.getSelectedItems());
    }

    public void deleteEntry() {
        int[] row = view.getSelectedBillRows();

        BillEntry[] entries = BillEntry.loadEntries( model.getRechnung() );


        ArrayList list = new ArrayList();

        for (int i = 0; i < entries.length; i++) {
            boolean check = true;
            for (int j = 0; j < row.length; j++) {
                if (i == row[j]) {
                    check = false;
                }
            }
            if (check) {
                list.add(entries[i]);
            }
        }

        entries = new BillEntry[entries.length - row.length];

        entries = (BillEntry[])list.toArray(entries);

        BillEntry.saveEntries( getModel().getRechnung(), entries );

        // Now inform the model that it has actually changed.
        getModel().fireChangeEvent();
    }

    public void update(Observable o,Object arg) {
        try {
            ((LockingInfo)o).releaseLastLock();
        }
        catch ( SQLException x ) {
            new ErrorDisplay(x,"Speichern fehlgeschlagen!");
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame( "Bill Presenter Example" );

    //    f.getContentPane().add( example().createView() );

        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.pack();
        f.show();
    }
}
