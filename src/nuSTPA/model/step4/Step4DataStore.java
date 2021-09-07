package nuSTPA.model.step4;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nuSTPA.model.step1.lossListAdapter;
import nuSTPA.model.step3.Step3;

public class Step4DataStore {
	private ObservableList<Step4> lossScenarioList = FXCollections.observableArrayList();
	
	@XmlElement(name = "Step4 - Loss Scenario")
	@XmlJavaTypeAdapter(Step4ListAdapter.class)
	public ObservableList<Step4> getStep4List(){
		return this.lossScenarioList;
	}
	
	public void setStep4List(ObservableList<Step4> lsList) {
		this.lossScenarioList = lsList;
	}
	
	public void addStep4(Step4 ls) {
		this.lossScenarioList.add(ls);
	}
}