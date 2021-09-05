package nuSTPA.model.step1;

import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nuSTPA.model.ProjectXML;

public class hazardListAdapter extends XmlAdapter<ProjectXML, ObservableList<Step1>>{

	@Override
	public ObservableList<Step1> unmarshal(ProjectXML v) throws Exception {
		// TODO Auto-generated method stub
		ObservableList<Step1> hazardTableList = FXCollections.observableArrayList(v.getHazardList());
		return hazardTableList;
	}

	@Override
	public ProjectXML marshal(ObservableList<Step1> v) throws Exception {
		// TODO Auto-generated method stub
		ProjectXML lhcWrapper = new ProjectXML();
		v.stream().forEach((item) -> {
			lhcWrapper.getHazardList().add(item);
		});
		return lhcWrapper;
	}
	
	
	
}