package nuSTPA.model.step2;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class ControlAction {

	double startX, startY, endX, endY;
	Controller controller, controlled;
	Components dataStore;
	//xml
	Integer id;
	ArrayList<String> CA;
	Integer controllerID, controlledID;
	
	public ControlAction() {
		
	}
	
	public ControlAction(String controller2, String controlledProcess,  ArrayList<String> CA, Integer id, Components dataStore) {
		this.controller = dataStore.findController(controller2);
		this.controlled = dataStore.findController(controlledProcess);
		this.controllerID = this.controller.getId();
		this.controlledID = this.controlled.getId();
		this.id = id;
		this.CA = CA;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public Controller getControlled() {
		return controlled;
	}
	
	public Integer getControllerID() {
		return controllerID;
	}
	
	public Integer getControlledID() {
		return controlledID;
	}
	
	public Integer getId() {
		return id;
	}
	
	public ArrayList<String> getCA() {
		return CA;
	}
	
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	public void setControlled(Controller controlled) {
		this.controlled = controlled;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setCA(ArrayList<String> CA) {
		this.CA = CA;
	}
	
}
