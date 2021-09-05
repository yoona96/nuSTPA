package nuSTPA.model.step1;


import javax.xml.bind.annotation.adapters.XmlAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nuSTPA.model.ProjectXML;

public class lossListAdapter extends XmlAdapter<ProjectXML, ObservableList<Step1>>{

	@Override
	public ObservableList<Step1> unmarshal(ProjectXML v) throws Exception {
		// TODO Auto-generated method stub
		ObservableList<Step1> lossTableList = FXCollections.observableArrayList(v.getLossList());
		return lossTableList;
	}

	@Override
	public ProjectXML marshal(ObservableList<Step1> v) throws Exception {
		// TODO Auto-generated method stub
		ProjectXML lhcWrapper = new ProjectXML();
		v.stream().forEach((item) -> {
			lhcWrapper.getLossList().add(item);
		});
		return lhcWrapper;
	}
	
	
	
}