package nuSTPA.model.step2;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Feedback {

	double startX, startY, endX, endY;
	Controller controller, controlled;
	Components dataStore;
	// xml
	Integer id;
	Integer controllerID;
	public Integer controlledID;
	ArrayList<String> FB;

	public Feedback() {

	}

	public Feedback(String controlledProcess, String controller, ArrayList<String> FB, Integer id, Components dataStore) {
		this.controller = dataStore.findController(controlledProcess);
		this.controlled = dataStore.findController(controller);
		this.controllerID = this.controller.getId();
		this.controlledID = this.controlled.getId();
		this.id = id;
		this.FB = FB;
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

	public ArrayList<String> getFB() {
		return FB;
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

	public void setFB(ArrayList<String> FB) {
		this.FB = FB;
	}
}
