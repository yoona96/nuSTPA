package nuSTPA.model.pmv;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PmvDataStore {
	//list for each process model tabs
	private ObservableList<ProcessModel> pmvList = FXCollections.observableArrayList();
	
	//total input variables & related nodes related to each output variables
	private ArrayList<ArrayList<String>> inputList = new ArrayList<ArrayList<String>>();
	
	//total output variables
	private ObservableList<String> outputList = FXCollections.observableArrayList();
	
	//selected FOD
	private ArrayList<String> selectedFODs = new ArrayList<String>();
	
	//selected output vars
	private ObservableList<String> selectedOutputs = FXCollections.observableArrayList();
	
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
		return this.inputList;
	}
	
	public void setInputList(ArrayList<ArrayList<String>> inputList) {
		this.inputList = inputList;
	}

	public ObservableList<String> getOutputList() {
		return this.outputList;
	}

	public void setOutputList(ObservableList<String> outputlist2) {
		this.outputList = outputlist2;
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
}
