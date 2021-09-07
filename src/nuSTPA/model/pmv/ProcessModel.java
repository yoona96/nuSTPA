package nuSTPA.model.pmv;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProcessModel {
	//controller & control action related to this process model
	private String controllerName, controlActionName;
	
	//total output variables related to selected controller
	private ObservableList<String> totalOutputs = FXCollections.observableArrayList();
	
	//selected output variables
	private ObservableList<String> selectedOutputs = FXCollections.observableArrayList();

	//related variables & nodes for selected output
	private ObservableList<String> processModelList = FXCollections.observableArrayList();

	/*
	 * default constructor
	 */
	public ProcessModel() {
	}

	public ProcessModel(String controller, String ca, ObservableList<String> totalOutputs, ObservableList<String> selectedOutputs, ObservableList<String> processModelList) {
		this.controllerName = controller;
		this.controlActionName = ca;
		this.totalOutputs = totalOutputs;
		this.selectedOutputs = selectedOutputs;
		this.processModelList = processModelList;
	}

	public String getControllerName() {
		return this.controllerName;
	}

	public void setControllerName(String controller) {
		this.controllerName = controller;
	}

	public String getControlActionName() {
		return this.controlActionName;
	}

	public void setControlActionName(String ca) {
		this.controlActionName = ca;
	}
	
	public ObservableList<String> getTotalOutputs() {
		return totalOutputs;
	}

	public void setTotalOutputs(ObservableList<String> totalOutputs) {
		this.totalOutputs = totalOutputs;
	}

	public ObservableList<String> getSelectedOutputs(){
		return this.selectedOutputs;
	}
	
	public void setSelectedOutputs(ObservableList<String> selectedOutputs) {
		this.selectedOutputs = selectedOutputs;
	}

	public ObservableList<String> getProcessModelList() {
		return this.processModelList;
	}

	public void setProcessModelList(ObservableList<String> processModelList) {
		this.processModelList = processModelList;
	}
}
