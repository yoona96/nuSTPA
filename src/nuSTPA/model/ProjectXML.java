package nuSTPA.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nuSTPA.model.ct.CT;
import nuSTPA.model.ct.CTDataStore;
import nuSTPA.model.pmv.ProcessModel;
import nuSTPA.model.step1.Step1;
import nuSTPA.model.step1.Step1DataStore;
import nuSTPA.model.step2.ControlAction;
import nuSTPA.model.step2.Controller;
import nuSTPA.model.step2.Feedback;
import nuSTPA.model.step2.Text;
import nuSTPA.model.step3.Step3;
import nuSTPA.model.step3.Step3DataStore;
import nuSTPA.model.step4.Step4;
import nuSTPA.model.step4.Step4DataStore;

@XmlRootElement(name = "kutokit")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class ProjectXML {

	// --------------------------- Step1 --------------------------
	Step1DataStore Step1DB = new Step1DataStore();
	List<Step1> lossList = new ArrayList<Step1>();
	List<Step1> hazardList = new ArrayList<Step1>();
	List<Step1> constraintList = new ArrayList<Step1>();
	// --------------------------- Step1 --------------------------


	// --------------------------- Step2 --------------------------
	private ObservableList<Controller> controllers;
	private ObservableList<ControlAction> controlActions;
	private ObservableList<Feedback> feedbacks;
	private ObservableList<Text> texts;
	private int curId;
	// --------------------------- Step2 --------------------------


	// --------------------------- Step3 --------------------------
	ObservableList<Step3> UCA = FXCollections.observableArrayList();
	ObservableList<Step3DataStore> UCAList = FXCollections.observableArrayList();
	// --------------------------- Step3 --------------------------


	// --------------------------- Pmv --------------------------
	private ObservableList<ProcessModel> processModel = FXCollections.observableArrayList();
	private ArrayList<ArrayList<String>> inputVariables = new ArrayList<ArrayList<String>>();
	private ObservableList<String> outputVariables = FXCollections.observableArrayList();
	private ArrayList<String> selectedFODs = new ArrayList<String>();
	private ObservableList<String> selectedOutputs = FXCollections.observableArrayList();
	// --------------------------- Pmv --------------------------


	// --------------------------- CT --------------------------
	ObservableList<CT> CT = FXCollections.observableArrayList();
	ObservableList<CTDataStore> CTList = FXCollections.observableArrayList();
	// --------------------------- CT --------------------------


	// --------------------------- Step4 ---------------------------
	Step4DataStore Step4DB = new Step4DataStore();
	List<Step4> Step4List = new ArrayList<Step4>();
	// --------------------------- Step4 ---------------------------


	// --------------------------- Step1 --------------------------
	@XmlElement(name = "Step1 - Loss")
	public List<Step1> getLossList(){
		return this.Step1DB.getLossTableList();
	}

	public void setLossList(List<Step1> lossList) {
		this.Step1DB.getLossTableList().setAll(lossList);
	}

	@XmlElement(name = "Step1 - Hazard")
	public List<Step1> getHazardList(){
		return this.Step1DB.getHazardTableList();
	}

	public void setHazardList(List<Step1> hazardList) {
		this.Step1DB.getHazardTableList().setAll(hazardList);
	}

	@XmlElement(name = "Step1 - Safety Constraint")
	public List<Step1> getConstraintList(){
		return this.Step1DB.getConstraintTableList();
	}

	public void setConstraintList(List<Step1> constraintList) {
		this.Step1DB.getConstraintTableList().setAll(constraintList);
	}
	// --------------------------- Step1 --------------------------



	// --------------------------- Step2 --------------------------
	@XmlElement(name = "Step2 - Controller")
	public ObservableList<Controller> getControllers() {
		return controllers;
	}

	public void setControllers(ObservableList<Controller> observableList) {
		this.controllers = observableList;
	}

	@XmlElement(name = "Step2 - Control Actions")
	public ObservableList<ControlAction> getControlActions() {
		return controlActions;
	}

	public void setControlActions(ObservableList<ControlAction> observableList) {
		this.controlActions = observableList;
	}

	@XmlElement(name = "Step2 - Feedbacks")
	public ObservableList<Feedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(ObservableList<Feedback> observableList) {
		this.feedbacks = observableList;
	}

	@XmlElement(name = "Step2 - Texts")
	public ObservableList<Text> getTexts() {
		return texts;
	}

	public void setTexts(ObservableList<Text> texts) {
		this.texts = texts;
	}

	@XmlElement(name = "current Controller Id")
	public int getCurId() {
		return curId;
	}

	public void setCurId(int id) {
		this.curId = id;
	}
	// --------------------------- Step2 --------------------------




	// --------------------------- Step3 --------------------------
	@XmlElement(name = "Step3 - UCA List")
	public ObservableList<Step3DataStore> getUCADataStoreList() {
		return this.UCAList;
	}

	@XmlElement(name = "UCA")
	public ObservableList<Step3> getUCA(){
		return UCA;
	}

	public void setUCAList(ObservableList<Step3DataStore> UCAList) {
		this.UCAList = UCAList;
	}

	public void setUCA(ObservableList<Step3> UCA) {
		this.UCA = UCA;
	}
	// --------------------------- Step3 --------------------------



	// --------------------------- Pmv --------------------------
	@XmlElement(name = "Pmv - Process Model")
	public ObservableList<ProcessModel> getProcessModel(){
		return processModel;
	}

	public void setProcessModel(ObservableList<ProcessModel> pm) {
		this.processModel = pm;
	}

	@XmlElement(name = "Pmv - Total Outputs")
	public ObservableList<String> getOutputList(){
		return outputVariables;
	}

	public void setOutputList(ObservableList<String> outputList) {
		this.outputVariables = outputList;
	}

	@XmlElementWrapper(name="Pmv - Input List")
	@XmlElement(name = "Input Variables")
	public ArrayList<ArrayList<String>> getInputList(){
		return inputVariables;
	}

	public void setIntputList(ArrayList<ArrayList<String>> inputList) {
		this.inputVariables = inputList;
	}
	
	public ArrayList<String> getSelectedFODs(){
		return selectedFODs;
	}
	
	public void setSelectedFODs(ArrayList<String> fodList) {
		this.selectedFODs = fodList;
	}
	
	public ObservableList<String> getSelectedOutputs() {
		return selectedOutputs;
	}
	
	public void setSelectedOutputs(ObservableList<String> selectedOutputs) {
		this.selectedOutputs = selectedOutputs;
	}
	// --------------------------- Pmv --------------------------
	
	
	// --------------------------- CT --------------------------
	@XmlElement(name = "CT - List")
	public ObservableList<CTDataStore> getCTDataStoreList() {
		return this.CTList;
	}

	public void setCTList(ObservableList<CTDataStore> CTList) {
		this.CTList = CTList;
	}


	public ArrayList<String> getCTControllerName(){
		ArrayList<String> controllerNames = new ArrayList<String>();
		for(int i=0;i<CTList.size();i++){
			for(int j=0;j<CTList.get(i).getCtTableList().size();j++){
				controllerNames.add(CTList.get(i).getCtTableList().get(j).getControllerName());
			}
		}
		return controllerNames;
	}

	public ArrayList<String> getCTCA(){
		ArrayList<String> ca = new ArrayList<String>();
		for(int i=0;i<CTList.size();i++){
			for(int j=0;j<CTList.get(i).getCtTableList().size();j++){
				ca.add(CTList.get(i).getCtTableList().get(j).getControlAction());
			}
		}
		return ca;
	}

	public ArrayList<String> getCTCases(){
		ArrayList<String> cases = new ArrayList<String>();
		for(int i=0;i<CTList.size();i++){
			for(int j=0;j<CTList.get(i).getCtTableList().size();j++){
				cases.add(CTList.get(i).getCtTableList().get(j).getCasesValue());
			}
		}
		return cases;
	}

	public ArrayList<String[]> getCTContext(){
		ArrayList<String[]> contextsArray = new ArrayList<String[]>();
		for(int i=0;i<CTList.size();i++){
			for(int j=0;j<CTList.get(i).getCtTableList().size();j++){
				String[] contexts =  new String[CTList.get(i).getCtTableList().get(0).getTotalContexts().size()];
				for(int k=0;k<contexts.length;k++) {
					contexts[k] = CTList.get(i).getCtTableList().get(j).getTotalContext(k);
				}
				contextsArray.add(contexts);
			}
		}
		return contextsArray;
	}

	public ArrayList<String> getCTHazardous(){
		ArrayList<String> hazardous = new ArrayList<String>();
		for(int i=0;i<CTList.size();i++){
			for(int j=0;j<CTList.get(i).getCtTableList().size();j++){
				hazardous.add(CTList.get(i).getCtTableList().get(j).getHazardous().toString());
			}
		}
		return hazardous;
	}

	public void setCT(ObservableList<CT> CT) {
		this.CT = CT;
	}
	// --------------------------- CT --------------------------


	// --------------------------- Step4 ---------------------------
	@XmlElement(name = "Step4 - Loss Scenario")
	public List<Step4> getStep4List(){
		return this.Step4DB.getStep4List();
	}

	public void setStep4List(List<Step4> Step4List) {
		this.Step4DB.getStep4List().setAll(Step4List);
	}
	// --------------------------- Step4 ---------------------------
}