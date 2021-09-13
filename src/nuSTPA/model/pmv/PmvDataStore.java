package nuSTPA.model.pmv;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PmvDataStore {
	//list for each process model tabs
	private ObservableList<ProcessModel> pmvList = FXCollections.observableArrayList();
	
	//total input variables & related nodes related to each output variables
	private ArrayList<ArrayList<String>> transitionList = new ArrayList<ArrayList<String>>();
	
	//list of abstracted process models
	private ArrayList<ArrayList<String>> abstractedList = new ArrayList<ArrayList<String>>();
	
	//selected FOD
	private ArrayList<String> selectedFODs = new ArrayList<String>();
	
	//selected output vars
	private ObservableList<String> selectedOutputs = FXCollections.observableArrayList();
	
	//FSM or TTS node from process model
	private ArrayList<String> fsmTtsList = new ArrayList<String>();
	
	public ObservableList<ProcessModel> getProcessModel(){
		return pmvList;
	}
	
	public void setProcessModel(ObservableList<ProcessModel> processModels) {
		this.pmvList = processModels;
	}
	
	public void addProcessModel(ProcessModel pm) {
		this.pmvList.add(pm);
	}
	
	public ArrayList<ArrayList<String>> getInputList(){
		return this.transitionList;
	}
	
	public void setInputList(ArrayList<ArrayList<String>> transitionList) {
		this.transitionList = transitionList;
	}

	public ArrayList<String> getSelectedFOD() {
		return selectedFODs;
	}

	public void setSelectedFOD(ArrayList<String> selectedFOD) {
		this.selectedFODs = selectedFOD;
	}

	public ObservableList<String> getSelectedOutputs() {
		return selectedOutputs;
	}

	public void setSelectedOutputs(ObservableList<String> selectedOutputList) {
		this.selectedOutputs = selectedOutputList;
	}

	public ArrayList<ArrayList<String>> getAbstractedList() {
		return abstractedList;
	}

	public void setAbstractedList(ArrayList<ArrayList<String>> abstractedList) {
		this.abstractedList = abstractedList;
	}

	public ArrayList<String> getFsmTtsList() {
		return fsmTtsList;
	}

	public void setFsmTtsList(ArrayList<String> fsmTtsList) {
		this.fsmTtsList = fsmTtsList;
	}
}
