
package nuSTPA.view.popup;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nuSTPA.MainApp;
import nuSTPA.model.step1.Step1;


public class Step3HazardPopUpController implements Initializable {
	
	private MainApp mainApp;

	@FXML private TableView<Step1> hazardTableView = new TableView<Step1>();
	@FXML private TableColumn<Step1,String> indexColumn = new TableColumn<Step1,String>();
	@FXML private TableColumn<Step1,String> textColumn = new TableColumn<Step1,String>();
	@FXML private TableColumn<Step1,String> linkColumn = new TableColumn<Step1,String>();

	@FXML private Stage stage;

	ObservableList<Step1> hazardList = FXCollections.observableArrayList();


	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	

	public Step3HazardPopUpController(){	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb){
		hazardList = mainApp.lhcDataStore.getHazardTableList();
		hazardTableView.setItems(hazardList);
		indexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty());
		textColumn.setCellValueFactory(cellData -> cellData.getValue().textProperty());
		linkColumn.setCellValueFactory(cellData -> cellData.getValue().linkProperty());

		hazardTableView.setItems(hazardList);
	}

	public void close() {
		Stage pop = (Stage)hazardTableView.getScene().getWindow();
	    pop.close();
	}
}