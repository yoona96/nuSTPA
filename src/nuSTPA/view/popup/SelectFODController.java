package nuSTPA.view.popup;

import java.util.ArrayList;

import org.controlsfx.control.CheckListView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SelectFODController{
	@FXML
	AnchorPane selectGroupPane = new AnchorPane();
	@FXML
	Button confirmButton = new Button();
	Button cancelButton = new Button();
	
	@FXML
	ToggleGroup fodBtnGroup = new ToggleGroup();
	@FXML VBox fodBtnVbox = new VBox();

	@FXML
	AnchorPane fodBtnPane = new AnchorPane();
	
	
	@FXML
	RadioButton rootFOD = new RadioButton();
	
	public boolean confirmed = false, canceled = false;
	
	public SelectFODController() {
		//first, only set rootFODPane visible
		selectGroupPane.setVisible(true);
	}
	
	public void confirmClick() {
		confirmed = true;
		Stage pop = (Stage)selectGroupPane.getScene().getWindow();
		pop.close();
	}
	
	public void cancelClick(){
		canceled = true;
		Stage pop = (Stage)selectGroupPane.getScene().getWindow();
		pop.close();
	}
	

	//setRootFOD text from selected NuSRS file
	public void setRootFOD(String rootFOD) {
		this.rootFOD.setText(rootFOD);
	}
	
	//set group list in checkListView
	public void setGroupItems(ArrayList<String> groupList) {
		fodBtnGroup.getToggles().clear();
		for(int i = 0; i < groupList.size(); i++) {
			RadioButton newBtn = new RadioButton();
			newBtn.setText(groupList.get(i));
			newBtn.setToggleGroup(fodBtnGroup);
			fodBtnVbox.getChildren().add(newBtn);
			fodBtnVbox.setSpacing(10);
		}
	}
	
	public String selectedItem(){
		RadioButton selectedFod = (RadioButton) fodBtnGroup.getSelectedToggle();
		return selectedFod.getText();
	}

	public void clear() {
		// TODO Auto-generated method stub
		fodBtnGroup.getToggles().clear();
	}
}