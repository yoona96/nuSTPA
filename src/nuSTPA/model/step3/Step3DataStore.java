package nuSTPA.model.step3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Step3DataStore {

	private String controller;
	private String controllAction;
	public int size=0;

	ObservableList<Step3> UCATableList = FXCollections.observableArrayList();

	public ObservableList<Step3> getUCATableList() {
		size = UCATableList.size();
		return UCATableList;
	}
	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	public String getControllAction() {
		return controllAction;
	}

	public void setControllAction(String controllAction) {
		this.controllAction = controllAction;
	}

	public int settingSize(){
		this.size = UCATableList.size();
		return size;
	}
}

