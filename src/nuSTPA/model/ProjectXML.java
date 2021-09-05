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

	// --------------------------- LHC --------------------------
	Step1DataStore lhcDB = new Step1DataStore();
	List<Step1> lossList = new ArrayList<Step1>();
	List<Step1> hazardList = new ArrayList<Step1>();
	List<Step1> constraintList = new ArrayList<Step1>();
	// --------------------------- LHC --------------------------


	// --------------------------- CSE --------------------------
	private ObservableList<Controller> controllers;
	private ObservableList<ControlAction> controlActions;
	private ObservableList<Feedback> feedbacks;
	private ObservableList<Text> texts;
	private int curId;
	// --------------------------- CSE --------------------------


	// --------------------------- UTM --------------------------
	ObservableList<Step3> UCA = FXCollections.observableArrayList();
	ObservableList<Step3DataStore> UCAList = FXCollections.observableArrayList();
	// --------------------------- UTM --------------------------


	// --------------------------- PMM --------------------------
	private ObservableList<ProcessModel> processModel = FXCollections.observableArrayList();
	private ArrayList<ArrayList<String>> inputVariables = new ArrayList<ArrayList<String>>();
	private ObservableList<String> outputVariables = FXCollections.observableArrayList();
	// --------------------------- PMM --------------------------


	// --------------------------- CTM --------------------------
	ObservableList<CT> CTM = FXCollections.observableArrayList();
	ObservableList<CTDataStore> CTMList = FXCollections.observableArrayList();
	// --------------------------- CTM --------------------------


	// --------------------------- LS ---------------------------
	Step4DataStore lsDB = new Step4DataStore();
	List<Step4> lsList = new ArrayList<Step4>();
	// --------------------------- LS ---------------------------


	// --------------------------- LHC --------------------------
	@XmlElement(name = "LHC-loss")
	public List<Step1> getLossList(){
		return this.lhcDB.getLossTableList();
	}

	public void setLossList(List<Step1> lossList) {
		this.lhcDB.getLossTableList().setAll(lossList);
	}

	@XmlElement(name = "LHC-hazard")
	public List<Step1> getHazardList(){
		return this.lhcDB.getHazardTableList();
	}

	public void setHazardList(List<Step1> hazardList) {
		this.lhcDB.getHazardTableList().setAll(hazardList);
	}

	@XmlElement(name = "LHC-constraint")
	public List<Step1> getConstraintList(){
		return this.lhcDB.getConstraintTableList();
	}

	public void setConstraintList(List<Step1> constraintList) {
		this.lhcDB.getConstraintTableList().setAll(constraintList);
	}
	// --------------------------- LHC --------------------------



	// --------------------------- CSE --------------------------
	@XmlElement(name = "CSE-controller")
	public ObservableList<Controller> getControllers() {
		return controllers;
	}

	public void setControllers(ObservableList<Controller> observableList) {
		this.controllers = observableList;
	}

	@XmlElement(name = "CSE-control-Actions")
	public ObservableList<ControlAction> getControlActions() {
		return controlActions;
	}

	public void setControlActions(ObservableList<ControlAction> observableList) {
		this.controlActions = observableList;
	}

	@XmlElement(name = "CSE-feedbacks")
	public ObservableList<Feedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(ObservableList<Feedback> observableList) {
		this.feedbacks = observableList;
	}

	@XmlElement(name = "CSE-texts")
	public ObservableList<Text> getTexts() {
		return texts;
	}

	public void setTexts(ObservableList<Text> texts) {
		this.texts = texts;
	}

	@XmlElement(name = "cur-Id")
	public int getCurId() {
		return curId;
	}

	public void setCurId(int id) {
		this.curId = id;
	}
	// --------------------------- CSE --------------------------




	// --------------------------- UTM --------------------------
	@XmlElement(name = "UCA-List")
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
	// --------------------------- UTM --------------------------



	// --------------------------- PMM --------------------------
	@XmlElement(name = "PMM-process-model")
	public ObservableList<ProcessModel> getProcessModel(){
		return processModel;
	}

	public void setProcessModel(ObservableList<ProcessModel> pm) {
		this.processModel = pm;
	}

	@XmlElement(name = "PMM-total-selected-output")
	public ObservableList<String> getOutputList(){
		return outputVariables;
	}

	public void setOutputList(ObservableList<String> outputList) {
		this.outputVariables = outputList;
	}

	@XmlElementWrapper(name="PMM-input-list")
	@XmlElement(name = "Input-variables")
	public ArrayList<ArrayList<String>> getInputList(){
		return inputVariables;
	}

	public void setIntputList(ArrayList<ArrayList<String>> inputList) {
		this.inputVariables = inputList;
	}

	// --------------------------- CTM --------------------------
	@XmlElement(name = "CTM-List")
	public ObservableList<CTDataStore> getCtmDataStoreList() {
		return this.CTMList;
	}

	public void setCTMList(ObservableList<CTDataStore> CTMList) {
		this.CTMList = CTMList;
	}


	public ArrayList<String> getCTMControllerName(){
		ArrayList<String> controllerNames = new ArrayList<String>();
		for(int i=0;i<CTMList.size();i++){
			for(int j=0;j<CTMList.get(i).getCtTableList().size();j++){
				controllerNames.add(CTMList.get(i).getCtTableList().get(j).getControllerName());
			}
		}
		return controllerNames;
	}

	public ArrayList<String> getCTMCA(){
		ArrayList<String> ca = new ArrayList<String>();
		for(int i=0;i<CTMList.size();i++){
			for(int j=0;j<CTMList.get(i).getCtTableList().size();j++){
				ca.add(CTMList.get(i).getCtTableList().get(j).getControlAction());
			}
		}
		return ca;
	}

	public ArrayList<String> getCTMCases(){
		ArrayList<String> cases = new ArrayList<String>();
		for(int i=0;i<CTMList.size();i++){
			for(int j=0;j<CTMList.get(i).getCtTableList().size();j++){
				cases.add(CTMList.get(i).getCtTableList().get(j).getCasesValue());
			}
		}
		return cases;
	}

	public ArrayList<String[]> getCTMContext(){
		ArrayList<String[]> contextsArray = new ArrayList<String[]>();
		for(int i=0;i<CTMList.size();i++){
			for(int j=0;j<CTMList.get(i).getCtTableList().size();j++){
				String[] contexts =  new String[CTMList.get(i).getCtTableList().get(0).getContexts().length];
				for(int k=0;k<contexts.length;k++) {
					contexts[k] = CTMList.get(i).getCtTableList().get(j).getContext(k);
				}
				contextsArray.add(contexts);
			}
		}
		return contextsArray;
	}

	public ArrayList<String> getCTMHazardous(){
		ArrayList<String> hazardous = new ArrayList<String>();
		for(int i=0;i<CTMList.size();i++){
			for(int j=0;j<CTMList.get(i).getCtTableList().size();j++){
				hazardous.add(CTMList.get(i).getCtTableList().get(j).getHazardous().toString());
			}
		}
		return hazardous;
	}

	public void setCTM(ObservableList<CT> CTM) {
		this.CTM = CTM;
	}
	// --------------------------- CTM --------------------------


	// --------------------------- LS ---------------------------
	@XmlElement(name = "LS-loss-scenario")
	public List<Step4> getLsList(){
		return this.lsDB.getLsList();
	}

	public void setLsList(List<Step4> lsList) {
		this.lsDB.getLsList().setAll(lsList);
	}
	// --------------------------- LS ---------------------------
}