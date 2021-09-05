package nuSTPA.view.popup;

import org.controlsfx.control.CheckListView;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Step1LinkModifyPopUpController {
	@FXML 
	CheckListView<String> linkItems = new CheckListView<String>();
	@FXML
	Button confirmButton = new Button(), cancelButton = new Button();
	
	public Step1LinkModifyPopUpController() {
		if(linkItems.getSelectionModel().isEmpty()) {
			return;
		}else {
			
		}
	}
	
	public ObservableList<String> getLinkItems(){
		return linkItems.getItems();
	}
	
	public void setLinkItems(ObservableList<String> linkItemDatas) {
		this.linkItems.getItems().addAll(linkItemDatas);
	}
}