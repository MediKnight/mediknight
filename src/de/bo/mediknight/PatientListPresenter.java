package de.bo.mediknight;

import java.awt.Component;
import java.util.Observable;
import java.util.Observer;

import de.bo.mediknight.domain.KnightObject;

public class PatientListPresenter implements Presenter, Commitable, Observer{
	PatientListModel model;
	
	PatientListPanel view;
	
	public PatientListPresenter() {
		model = new PatientListModel();
	}
	
	public void activate() {		
	}

	public Component createView() {
		view = new PatientListPanel();
		view.setPresenter(this);
		
		return view;
	}

	public void commit() {
	}

	public Component getResponsibleComponent() {
		return view;
	}

	public void reload(Component component, KnightObject knightObject) {
	}

	public void update(Observable o, Object arg) {
	}

	public PatientListModel getModel() {
		return model;
	}

	public void setModel(PatientListModel model) {
		this.model = model;
	}

	public PatientListPanel getView() {
		return view;
	}

	public void setView(PatientListPanel view) {
		this.view = view;
	}
}
