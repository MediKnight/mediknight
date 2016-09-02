package de.baltic_online.mediknight;

import java.awt.Component;
import java.util.Observable;
import java.util.Observer;

import de.baltic_online.mediknight.domain.KnightObject;


public class PatientListPresenter implements Presenter, Commitable, Observer {

    PatientListModel model;

    PatientListPanel view;


    public PatientListPresenter() {
	model = new PatientListModel();
    }


    @Override
    public void activate() {
    }


    @Override
    public void commit() {
    }


    @Override
    public Component createView() {
	view = new PatientListPanel();
	view.setPresenter( this );

	return view;
    }


    public PatientListModel getModel() {
	return model;
    }


    @Override
    public Component getResponsibleComponent() {
	return view;
    }


    public PatientListPanel getView() {
	return view;
    }


    @Override
    public void reload( final Component component, final KnightObject knightObject ) {
    }


    public void setModel( final PatientListModel model ) {
	this.model = model;
    }


    public void setView( final PatientListPanel view ) {
	this.view = view;
    }


    @Override
    public void update( final Observable o, final Object arg ) {
    }
}
