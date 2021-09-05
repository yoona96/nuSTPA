package nuSTPA.model.step1;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Step1DataStore {

	private ObservableList<Step1> lossTableList = FXCollections.observableArrayList();
	private ObservableList<Step1> hazardTableList = FXCollections.observableArrayList();
	private ObservableList<Step1> constraintTableList = FXCollections.observableArrayList();

	@XmlElement(name = "LHC-loss")
	@XmlJavaTypeAdapter(lossListAdapter.class)
	public ObservableList<Step1> getLossTableList() {
		return this.lossTableList;
	}
	
	@XmlElement(name = "LHC-hazard")
	@XmlJavaTypeAdapter(hazardListAdapter.class)
	public ObservableList<Step1> getHazardTableList() {
		return this.hazardTableList;
	}
	
	@XmlElement(name = "LHC-constraint")
	@XmlJavaTypeAdapter(constraintListAdapter.class)
	public ObservableList<Step1> getConstraintTableList() {
		return this.constraintTableList;
	}
	
	public void setLossTableList(ObservableList<Step1> lossTableList) {
		this.lossTableList = lossTableList;
	}
	
	public void setHazardTableList(ObservableList<Step1> hazardTableList) {
		this.hazardTableList = hazardTableList;
	}
	
	public void setConstraintTableList(ObservableList<Step1> constraintTableList) {
		this.constraintTableList = constraintTableList;
	}
	
	public void addLoss(Step1 lhc) {
		this.lossTableList.add(lhc);
	}
	
	public void addHazard(Step1 lhc) {
		this.hazardTableList.add(lhc);
	}
	
	public void addConstraint(Step1 lhc) {
		this.constraintTableList.add(lhc);
	}
}