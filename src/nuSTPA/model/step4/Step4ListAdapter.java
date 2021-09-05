package nuSTPA.model.step4;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nuSTPA.model.ProjectXML;

public class Step4ListAdapter extends XmlAdapter<ProjectXML, ObservableList<Step4>> {

	@Override
	public ObservableList<Step4> unmarshal(ProjectXML v) throws Exception {
		// TODO Auto-generated method stub
		ObservableList<Step4> lossScenarioList = FXCollections.observableArrayList(v.getLsList());
		return lossScenarioList;
	}

	@Override
	public ProjectXML marshal(ObservableList<Step4> v) throws Exception {
		// TODO Auto-generated method stub
		ProjectXML lsXml = new ProjectXML();
		v.stream().forEach((ls) -> {
			lsXml.getLsList().add(ls);
		});
		return lsXml;
	}
	
	
}