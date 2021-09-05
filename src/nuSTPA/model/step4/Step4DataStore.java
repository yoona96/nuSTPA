package nuSTPA.model.step4;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nuSTPA.model.step1.lossListAdapter;
import nuSTPA.model.step3.Step3;

public class Step4DataStore {
	private ObservableList<Step4> lsList = FXCollections.observableArrayList();
	
	@XmlElement(name = "LS-loss-scenario")
	@XmlJavaTypeAdapter(Step4ListAdapter.class)
	public ObservableList<Step4> getLsList(){
		return this.lsList;
	}
	
	public void setLossScenarioList(ObservableList<Step4> lsList) {
		this.lsList = lsList;
	}
	
	public void addLossScenario(Step4 ls) {
		this.lsList.add(ls);
	}
}