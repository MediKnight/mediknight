package de.bo.mediknight;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import java.sql.*;

import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.*;


public class MacroPresenter implements Presenter, Commitable {

    MacroPanel view;
    MacroModel model;


    public MacroPresenter() {
        this(new MacroModel());
    }

    public MacroPresenter(MacroModel model) {
        this.model = model;
    }

    public MacroModel getModel() {
        return model;
    }

    public Component createView() {
        MacroPanel panel = new MacroPanel();
        panel.setPresenter( this );
        view = panel;
	view.setFocusOnList();

        return panel;
    }


    public void activate() {}

    public void commit() {
        try {
            getModel().getRechnung().save();
        } catch( SQLException e ) {
            ErrorDisplay ed = new ErrorDisplay(e, "Fehler beim Abspeichern der Rechnung!", "Speichern...", view);
        }
    }


    public Component getResponsibleComponent() {
        /** @todo: implement this! */
        return null;
    }

    public void reload(Component component,KnightObject knightObject) {
    }

    public void createMacro() {
        String input = JOptionPane.showInputDialog(view,
            "Bausteinkennung eingeben", "Baustein erzeugen",
            JOptionPane.PLAIN_MESSAGE);
        if ( input != null ) {
            try {
                input = input.trim();
                if ( input.length() > 0 ) {
                    RechnungsGruppe macro = new RechnungsGruppe();
                    Rechnung rechnung = new Rechnung();
                    BillEntry.saveEntries( rechnung, model.getEntries());
                    model.setRechnung(rechnung);
                    macro.setAbk(input);
                    macro.setObject(model.getRechnung().getObject());
                    macro.setText("");
                    macro.save();

                    showBill();
                }
            }
            catch (SQLException e) {
                e.printStackTrace(); /** @todo Exception reporting. */
            }
        }
    }


    void deleteMacro() {
        RechnungsGruppe macro = view.getSelectedRechnungsGruppe();
        if ( macro != null ) {
            int r = JOptionPane.showConfirmDialog( view,
                "Baustein " + macro.getAbk() + " wirklich löschen?",
                "Baustein löschen",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE );

            if ( r == JOptionPane.YES_OPTION ) {
                try {
                    macro.delete();
                    model.fireChangeEvent();
                }
                catch (SQLException e) {
                    e.printStackTrace(); /** @todo Exception reporting. */
                }
            }
        }
    }



    public void addMacro() {
        RechnungsGruppe macro = view.getSelectedRechnungsGruppe();
        if( macro != null ) {
            BillEntry[] entries = BillEntry.loadEntries( model.getRechnung() );
            BillEntry[] macroEntries = BillEntry.loadEntries( macro );
            BillEntry[] newEntries = new BillEntry[entries.length + macroEntries.length];

            System.arraycopy( entries, 0, newEntries, 0, entries.length );
            System.arraycopy( macroEntries, 0, newEntries, entries.length, macroEntries.length );

            BillEntry.saveEntries( getModel().getRechnung(), newEntries );

            /** @todo The following code actually belongs in the commit() method,
             *  but that doesn't work. Fix it.
             */
            try {
                getModel().getRechnung().save();
            } catch( SQLException e ) {
                ErrorDisplay ed = new ErrorDisplay(e, "Fehler beim Abspeichern der Rechnung!", "Speichern...", view);
            }

            showBill();
        }
    }

    void showBill() {
        MainFrame.getApplication().bill();
        //((NewAppWindow) AppWindow.getApplication()).bill();
    }
}