package de.bo.mediknight;

import java.awt.*;
import java.sql.*;

import java.util.*;
import javax.swing.*;

import de.bo.mediknight.domain.*;

public class CreateMacroPresenter extends AbstractPresenter {

    CreateMacroPanel view;
    CreateMacroModel model;

    public CreateMacroPresenter( CreateMacroModel model ) {
        this.model = model;
    }

    public Component createView() {
        view = new CreateMacroPanel();
        view.setPresenter(this);

        return view;
    }

    public void activate() {
        view.activate();
    }


    public void macroSelected() {
        view.setMacroName( view.getSelectedMacro().toString() );
    }


    public RechnungsGruppe findMacroByName( String name ) {
        Iterator it = model.getComponentList().iterator();

        while( it.hasNext() ) {
            RechnungsGruppe macro = (RechnungsGruppe) it.next();
            if( macro.getAbk().equals( name ) )
                return macro;
        }

        return null;
    }

    public void createMacro() {
        String name = view.getMacroName();
        RechnungsGruppe macro = findMacroByName( name );

        if( macro != null ) {
            int result = JOptionPane.showConfirmDialog( null,
                "Es existiert bereits ein Baustein mit diesem Namen.\nSoll dieser Baustein überschrieben werden?",
                "Baustein vorhanden",
                JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

            if ( result == JOptionPane.YES_OPTION ) {
                try {
                    macro.delete();
                } catch( SQLException e ) {
                    e.printStackTrace(); /** @todo Error reporting. */
                }
            } else {
                return;
            }
        }

        try {
            macro = new RechnungsGruppe();
            Rechnung rechnung = new Rechnung();
            BillEntry.saveEntries( rechnung, model.getEntries() );
            macro.setAbk( name );
            macro.setObject( rechnung.getObject() );
            macro.setText( "" );
            macro.save();
        } catch( SQLException e ) {
            e.printStackTrace(); /** @todo Error reporting! */
        }

        MainFrame.getApplication().bill();
    }


    public CreateMacroModel getModel() {
        return model;
    }
}