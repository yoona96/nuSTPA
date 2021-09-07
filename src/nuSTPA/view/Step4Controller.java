package nuSTPA.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nuSTPA.MainApp;
import nuSTPA.model.step1.Step1;
import nuSTPA.model.step3.Step3;
import nuSTPA.model.step3.Step3DataStore;
import nuSTPA.model.step4.Step4;
import nuSTPA.model.step4.Step4DataStore;

public class Step4Controller {
	
	private MainApp mainApp;
	private Step4DataStore lsDB;
	private Step3DataStore ucaDB;

 	@FXML private TextField lossScenarioTextField;
	@FXML private Button addLossScenario, addNewTab;
	@FXML private ComboBox<String> UcaComboBox, lossFactorComboBox;
	@FXML private TabPane tabPane;
	@FXML private TableView tableView;
	
	ObservableList<TableView<Step4>> lsTableViewList = FXCollections.observableArrayList();
	ObservableList<TableColumn<Step4, String>> ucaColList = FXCollections.observableArrayList();
	ObservableList<TableColumn<Step4, String>> lossFactorColList = FXCollections.observableArrayList();
	ObservableList<TableColumn<Step4, String>> lossScenarioColList = FXCollections.observableArrayList();
	ObservableList<Step4> lossScenarioTableList;
	ObservableList<String> lossFactorCBList = FXCollections.observableArrayList("1) Controller Problems", "2) Feedback Problems", "3) Control Path Problems", "4) Controlled Process Problems");
	ObservableList<Step3DataStore> ucaDataStoreList = FXCollections.observableArrayList();
	ObservableList<String> ucaDatas = FXCollections.observableArrayList();
	//	ObservableList<UCA> ucaCBList = ucaDB.getUCATableList();
	/*
	 * default constructor
	 */
	public Step4Controller() {
		
	}
	
	/*
	 * set mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		this.lsDB = mainApp.lsDataStore;
	}
	
	public void addLossScenario() {
		int tabIndex = tabPane.getSelectionModel().getSelectedIndex();
		System.out.println("tabIndex: " + tabIndex);
		lossScenarioTableList = lsDB.getStep4List();
		
		ucaColList.get(tabIndex).setCellValueFactory(cellData -> cellData.getValue().getUCAProperty());
		lossFactorColList.get(tabIndex).setCellValueFactory(cellData -> cellData.getValue().getLossFactorProperty());
		lossScenarioColList.get(tabIndex).setCellValueFactory(cellData -> cellData.getValue().getLossScenarioProperty());
		
		lsTableViewList.get(tabIndex).setItems(lossScenarioTableList);
		
		/*
		 * add items to loss scenario table
		 */
		addLossScenario.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int curTabIndex = tabPane.getSelectionModel().getSelectedIndex();
				//if text field is empty, warning pop up opens
				if(lossScenarioTextField.getText().isEmpty()) {
					try {
						OpenTextFieldPopUp();
						System.out.println("no text field input");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else if(UcaComboBox.getValue().isEmpty() || lossFactorComboBox.getValue().isEmpty()) {
					try {
						OpenNoValueChoicePopUp();
						System.out.println("combo box value not selected");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else {
					ucaColList.get(curTabIndex).setCellValueFactory(cellData -> cellData.getValue().getUCAProperty());
					lossFactorColList.get(curTabIndex).setCellValueFactory(cellData -> cellData.getValue().getLossFactorProperty());
					lossScenarioColList.get(curTabIndex).setCellValueFactory(cellData -> cellData.getValue().getLossScenarioProperty());
					
					Step4 ls = new Step4(UcaComboBox.getValue(), lossFactorComboBox.getValue(), lossScenarioTextField.getText());
					System.out.println("tabIndex2: " + curTabIndex);
					lsTableViewList.get(curTabIndex).getItems().add(ls);
					lossFactorComboBox.getSelectionModel().clearSelection();
					lossScenarioTextField.clear();
					
				}
				event.consume();
			}
		});
		
		/*
		 * delete items from loss scenario table
		 */
		ContextMenu lossScenarioRightClickMenu = new ContextMenu();
		MenuItem removeLossScenarioMenu = new MenuItem("Delete");
		lossScenarioRightClickMenu.getItems().add(removeLossScenarioMenu);
		
		ObservableList<Step4> allLossScenarioItems, selectedLossScenarioItem;
		allLossScenarioItems = lsTableViewList.get(tabIndex).getItems();
		selectedLossScenarioItem = lsTableViewList.get(tabIndex).getSelectionModel().getSelectedItems();
		
		/*
		 * when right-clicked, show pop up
		 */
		lsTableViewList.get(tabIndex).setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
			public void handle(ContextMenuEvent e) {
				lossScenarioRightClickMenu.show(lsTableViewList.get(tabIndex), e.getScreenX(), e.getScreenY());
				removeLossScenarioMenu.setOnAction(event -> {
					selectedLossScenarioItem.forEach(allLossScenarioItems::remove);
					//no indexing here
				});
			}
		});
		lossScenarioRightClickMenu.hide();
		
		/*
		 * modify text in loss scenario table
		 */
		lossScenarioColList.get(tabIndex).setCellFactory(TextFieldTableCell.forTableColumn());
		lossScenarioColList.get(tabIndex).setOnEditCommit(
			(TableColumn.CellEditEvent<Step4, String> t) ->
                (t.getTableView().getItems().get(
               	t.getTablePosition().getRow())
                ).setLossScenario(t.getNewValue())
		);
	}
	
	public void initialize() {
		lsDB = mainApp.lsDataStore;
		ucaDataStoreList = mainApp.ucaDataStoreList;
		
//		tabPane.getTabs().remove(0);
		
		//set uca combobox
		
		System.out.println("uca db list : " + ucaDataStoreList);
		for(int i = 0; i < ucaDataStoreList.size(); i++) {
		    for(Step3 u : ucaDataStoreList.get(i).getUCATableList()){
		    	String ucaType1 = u.getProvidingCausesHazard().get();
		        String ucaType2 = u.getNotProvidingCausesHazard().get();
		        String ucaType3 = u.getIncorrectTimingOrOrder().get();
		        String ucaType4 = u.getStoppedTooSoonOrAppliedTooLong().get();
		        if(!ucaType1.isEmpty()) ucaDatas.add(ucaType1);
		        if(!ucaType2.isEmpty()) ucaDatas.add(ucaType2);
		        if(!ucaType3.isEmpty()) ucaDatas.add(ucaType3);
		        if(!ucaType4.isEmpty()) ucaDatas.add(ucaType4);
		    }
		}
    	System.out.println("UCA Datas : " + ucaDatas);
		
	    UcaComboBox.setItems(ucaDatas);
	    
	    //add loss factor combobox
		lossFactorComboBox.setItems(lossFactorCBList);
		
		addNewTab(0);
		
		addNewTab.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				System.out.println("add");
				addNewTab(lsTableViewList.size());
				event.consume();
			}
		});
		
	
		
		if(tabPane.getTabs().isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("No tab to work");
			alert.setContentText("You have to add tab first");
		}else {
			addLossScenario();
		}
	}
	
	/*
	 * if text field is empty, this pop up opens
	 */
	private void OpenTextFieldPopUp() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Parent parent = loader.load(getClass().getResource("popup/LsNoTextInputPopUpView.fxml"));
		Stage dialogStage = new Stage();
		Scene scene = new Scene(parent);
			
		//set dialog setting
		dialogStage.setTitle("Empty text field");            
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		dialogStage.setScene(scene);
		dialogStage.setResizable(false);
		dialogStage.show();
	}
	
	/*
	 * if text field is empty, this pop up opens
	 */
	private void OpenNoValueChoicePopUp() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Parent parent = loader.load(getClass().getResource("popup/LsNoValueChoicePopUpView.fxml"));
		Stage dialogStage = new Stage();
		Scene scene = new Scene(parent);
			
		//set dialog setting
		dialogStage.setTitle("Item not chosen");            
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		dialogStage.setScene(scene);
		dialogStage.setResizable(false);
		dialogStage.show();
	}
	
	/*
	 * add new tab
	 */
	private void addNewTab(int index) {
		System.out.println("index: " + index);
		Tab newTab = new Tab();
		newTab.setText("LS" + Integer.toString(index + 1));
		tabPane.getTabs().add(newTab);

		TableView<Step4> newTable = new TableView<Step4>();
		lsTableViewList.add(newTable);
		
		newTab.setContent(newTable);
		
		TableColumn<Step4, String> ucaCol = new TableColumn<Step4, String>();
		TableColumn<Step4, String> lossFactorCol = new TableColumn<Step4, String>();
		TableColumn<Step4, String> lossScenarioCol = new TableColumn<Step4, String>();
		
		ucaColList.add(ucaCol);
		lossFactorColList.add(lossFactorCol);
		lossScenarioColList.add(lossScenarioCol);
		
		ucaCol.setPrefWidth(160.0);
		lossFactorCol.setPrefWidth(210.0);
		lossScenarioCol.setPrefWidth(630.0);
		
		ucaCol.setResizable(false);
		lossFactorCol.setResizable(false);
		lossScenarioCol.setResizable(false);
		
		ucaCol.setText("UCA");
		lossFactorCol.setText("Loss Factor");
		lossScenarioCol.setText("Loss Scenario");
		
		newTable.getColumns().add(ucaCol);
		newTable.getColumns().add(lossFactorCol);
		newTable.getColumns().add(lossScenarioCol);
	}
}