package nuSTPA.view.popup;

import java.io.IOException;
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

public class SelectCtOutputController{
	@FXML
	AnchorPane selectOutputPane = new AnchorPane();
	@FXML
	Button confirmButton = new Button();
	@FXML
	Button cancelButton = new Button();
	
	@FXML
	ToggleGroup outputVarBtnGroup = new ToggleGroup();
	@FXML VBox outputVarBtnVbox = new VBox();

	@FXML
	AnchorPane outputVarBtnPane = new AnchorPane();
	
	@FXML
	RadioButton outputVars = new RadioButton();
	
	public boolean confirmed = false, canceled = false;
	
	public SelectCtOutputController() {
		//first, only set rootFODPane visible
		selectOutputPane.setVisible(true);
	}
	
	public void confirmClick() throws IOException {
		confirmed = true;
		Stage pop = (Stage)selectOutputPane.getScene().getWindow();
		pop.close();
	}
	
	public void cancelClick() throws IOException {
		canceled = true;
		Stage pop = (Stage)selectOutputPane.getScene().getWindow();
		pop.close();
	}
	
	//set group list in checkListView
	public void setGroupList(ObservableList<String> observableList) {
		for(int i = 0; i < observableList.size(); i++) {
			RadioButton newBtn = new RadioButton();
			newBtn.setText(observableList.get(i));
			newBtn.setToggleGroup(outputVarBtnGroup);
			outputVarBtnVbox.getChildren().add(newBtn);
			outputVarBtnVbox.setSpacing(10);
		}
	}
	
	public String selectedItem(){
		RadioButton selectedOutputVar = (RadioButton) outputVarBtnGroup.getSelectedToggle();
		return selectedOutputVar.getText();
	}
}