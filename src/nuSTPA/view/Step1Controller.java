package nuSTPA.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckComboBox;

import com.sun.javafx.scene.control.SelectedCellsMap;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nuSTPA.MainApp;
import nuSTPA.model.step1.Step1;
import nuSTPA.model.step1.Step1DataStore;
import javafx.fxml.*;

public class Step1Controller implements Initializable {

	private MainApp mainApp;
	private Step1DataStore lhcDB;
	
	@FXML private TableView<Step1> lossTableView;
	@FXML private TableColumn<Step1, String> lossIndexColumn, lossTextColumn, lossLinkColumn;
	@FXML private TableView<Step1> hazardTableView;
	@FXML private TableColumn<Step1, String> hazardIndexColumn, hazardTextColumn, hazardLinkColumn;
	@FXML private TableView<Step1> constraintTableView;
	@FXML private TableColumn<Step1, String> constraintIndexColumn, constraintTextColumn, constraintLinkColumn;
	@FXML private TextField lossTextField, hazardTextField, hazardLinkTextField, constraintTextField, constraintLinkTextField;
	@FXML private Button addLossButton, addHazardButton, addConstraintButton;
	@FXML private TableRow<Step1> lossRow, removeHazard, removeConstraint;
	@FXML private CheckComboBox<String> hazardLinkCB, constraintLinkCB;
	private ObservableList<CheckComboBox<String>> hazardLinkCBList, constraintLinkCBList;
	
	ObservableList<Step1> lossTableList, hazardTableList, constraintTableList;
	
	/*
	 * constructor
	 */
	public Step1Controller() {
//		lossTableList.add(new LHC("L1", "ex)Loss of life or Injury to people", ""));
//		hazardTableList.add(new LHC("H1", "ex)Nuclear power plant releases dangerous materials.", "[L1]"));
//		constraintTableList.add(new LHC("C1", "ex)Nuclear power plant must not release dangerous materials.", "[H1]"));
	}
	
	/*
	 * set MainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	/*
	 * add/modify/delete each items in each table
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {
		lhcDB = mainApp.lhcDataStore;
		
		lossTableList = lhcDB.getLossTableList();
		hazardTableList = lhcDB.getHazardTableList();
		constraintTableList = lhcDB.getConstraintTableList();
		
		ObservableList<String> lossIndexList = FXCollections.observableArrayList();
		ObservableList<String> hazardIndexList = FXCollections.observableArrayList();

		/*
		 * 
		 * below is code part for loss
		 * 
		*/
		
		
		lossIndexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty());
		lossTextColumn.setCellValueFactory(cellData -> cellData.getValue().textProperty());
		lossLinkColumn.setCellValueFactory(cellData -> cellData.getValue().linkProperty());
		
		lossTableView.setItems(lossTableList); 
		
		/*
		 * add items to loss table
		 */
		addLossButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Step1 lhc = new Step1("L" + (lhcDB.getLossTableList().size()+1), lossTextField.getText(), "");
				//if text field is empty, warning pop up opens
				if(lossTextField.getText().isEmpty()) {
					try {
						openTextFieldPopUp();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("no text field input");
						e1.printStackTrace();
					}
				}else {
					lossTableView.getItems().add(lhc);
					lossTextField.clear();
//					lossIndexList.add(lhc.getIndex());
					hazardLinkCB.getItems().add(lhc.getIndex());
				}
			}
		});
		
		
		/*
		 * delete items from loss table
		 */
		ContextMenu lossRightClickMenu = new ContextMenu();
		MenuItem removeLossMenu = new MenuItem("Delete");
		lossRightClickMenu.getItems().add(removeLossMenu);
		
		ObservableList<Step1> allLossItems, selectedLossItem;
		allLossItems = lossTableView.getItems();
		selectedLossItem = lossTableView.getSelectionModel().getSelectedItems();
		
		/*
		 * when right-clicked, show pop up
		 */
		lossTableView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
			public void handle(ContextMenuEvent e) {
				lossRightClickMenu.show(lossTableView, e.getScreenX(), e.getScreenY());
				removeLossMenu.setOnAction(event -> {
					selectedLossItem.forEach(allLossItems::remove);
					//need to update loss index
					updateLossIndex();
					try {
						openLinkUpdateNoticePopUp();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
			}
		});
		lossRightClickMenu.hide();
		
		/*
		 * modify text in loss table
		 */
		lossTextColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		lossTextColumn.setOnEditCommit(
			(TableColumn.CellEditEvent<Step1, String> t) ->
                (t.getTableView().getItems().get(
               	t.getTablePosition().getRow())
                ).setText(t.getNewValue())
		);
		
		/*
		 * 
		 * below is code part for hazard
		 * 
		*/
		
		
		hazardIndexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty());
		hazardTextColumn.setCellValueFactory(cellData -> cellData.getValue().textProperty());
		hazardLinkColumn.setCellValueFactory(cellData -> cellData.getValue().linkProperty());
		
		hazardTableView.setItems(hazardTableList);
		
		/*
		 * add items to hazard table
		 */
		addHazardButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Step1 lhc = new Step1("H" + (lhcDB.getHazardTableList().size()+1), hazardTextField.getText(), hazardLinkCB.getCheckModel().getCheckedItems().toString());
				if(hazardTextField.getText().isEmpty()) {
					//if text field is empty, warning pop up opens
					try {
						openTextFieldPopUp();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("no text field input");
						e1.printStackTrace();
					}
				}else if(hazardLinkCB.getCheckModel().isEmpty()) {
					//if nothing is selected from checkcombobox
					try {
						openNoSelectedLinkPopUp();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("No selected link");
						e1.printStackTrace();
					}
				}else {
					//if entered value fits format and text field is not empty, continue on
					hazardTableView.getItems().add(lhc);
					constraintLinkCB.getItems().addAll(lhc.getIndex());
					hazardIndexList.add(lhc.getIndex());
					hazardTextField.clear();
					hazardLinkCB.getCheckModel().clearChecks();
				}
			}
		});
		
		/*
		 * delete items from hazard table
		 */
		ContextMenu hazardRightClickMenu = new ContextMenu();
		MenuItem removeHazardMenu = new MenuItem("Delete");
		MenuItem EditHazardLinkMenu = new MenuItem("Modify");
		hazardRightClickMenu.getItems().add(removeHazardMenu);
		hazardRightClickMenu.getItems().add(EditHazardLinkMenu);		
		
		ObservableList<Step1> allHazardItems, selectedHazardItem;
		allHazardItems = hazardTableView.getItems();
		selectedHazardItem = hazardTableView.getSelectionModel().getSelectedItems();
		
		/*
		*when mouse is right-clicked, show pop up
		*/
		hazardTableView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
			public void handle(ContextMenuEvent e) {
				hazardRightClickMenu.show(hazardTableView, e.getScreenX(), e.getScreenY());
				removeHazardMenu.setOnAction(event -> {
					selectedHazardItem.forEach(allHazardItems::remove);
					//need to update hazard index
					updateHazardIndex();
					try {
						openLinkUpdateNoticePopUp();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
				
				EditHazardLinkMenu.setOnAction(event -> {
					
				});
			}
		});
		hazardRightClickMenu.hide();
		
		/*
		 * modify text in hazard table
		*/
		hazardTextColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		hazardTextColumn.setOnEditCommit(
			(TableColumn.CellEditEvent<Step1, String> t) ->
				(t.getTableView().getItems().get(
				t.getTablePosition().getRow())
		        ).setText(t.getNewValue().toString())
		);
		
		/*
		 * modify link in hazard table
		 */
		hazardLinkColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		hazardLinkColumn.setOnEditCommit(
			(TableColumn.CellEditEvent<Step1, String> t) -> {
				if(!t.getNewValue().contains("[") || !t.getNewValue().contains("]") || !t.getNewValue().contains("L")) {
					//if edited link doesn't fit format [index from loss]
					try {
						openLinkFormatPopUp();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return;
				}else{
					(t.getTableView().getItems().get(
							t.getTablePosition().getRow())
							).setLink(t.getNewValue().toString());
				}
			});
		
		/*
		 * 
		 * below is code part for constraint
		 * 
		*/
				
		
		constraintIndexColumn.setCellValueFactory(cellData -> cellData.getValue().indexProperty());
		constraintTextColumn.setCellValueFactory(cellData -> cellData.getValue().textProperty());
		constraintLinkColumn.setCellValueFactory(cellData -> cellData.getValue().linkProperty());
		
		constraintTableView.setItems(constraintTableList); 
		
		/*
		 * add items to constraint table
		 */
		addConstraintButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				Step1 lhc = new Step1("C" + (lhcDB.getConstraintTableList().size()+1), constraintTextField.getText(), constraintLinkCB.getCheckModel().getCheckedItems().toString());
				if(constraintTextField.getText().isEmpty()) {
					//if text field is empty, warning pop up opens
					try {
						openTextFieldPopUp();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("no text field input");
						e1.printStackTrace();
					}
				}else if(constraintLinkCB.getCheckModel().isEmpty()) {
					//if nothing is selected from checkcombobox
					try {
						openNoSelectedLinkPopUp();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("No selected link");
						e1.printStackTrace();
					}
				}else {
					//if entered value fits format, continue on
					constraintTableView.getItems().add(lhc);
					constraintTextField.clear();
					constraintLinkCB.getCheckModel().clearChecks();
				}
			}
		});
		
		/*
		 * delete items from constraint table
		 */
		ContextMenu constraintRightClickMenu = new ContextMenu();
		MenuItem removeConstraintMenu = new MenuItem("Delete");
		constraintRightClickMenu.getItems().add(removeConstraintMenu);
				
		ObservableList<Step1> allConstraintItems, selectedConstraintItem;
		allConstraintItems = constraintTableView.getItems();
		selectedConstraintItem = constraintTableView.getSelectionModel().getSelectedItems();
			
		/*
		 * when right-clicked, you can delete selected row
		 */
		constraintTableView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
			public void handle(ContextMenuEvent e) {
				constraintRightClickMenu.show(constraintTableView, e.getScreenX(), e.getScreenY());
				removeConstraintMenu.setOnAction(event -> {
					selectedConstraintItem.forEach(allConstraintItems::remove);
					//need to update constraint index
					updateConstraintIndex();
				});
			}
		});
		constraintRightClickMenu.hide();
		
		/*
		 * modify text in constraint table
		 */
		constraintTextColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		constraintTextColumn.setOnEditCommit(
			(TableColumn.CellEditEvent<Step1, String> t) ->
				(t.getTableView().getItems().get(
				t.getTablePosition().getRow())
	            ).setText(t.getNewValue())
		);
		
		//modify link in constraint table
		constraintLinkColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		constraintLinkColumn.setOnEditCommit(
			(TableColumn.CellEditEvent<Step1, String> t) -> {
				if (!t.getNewValue().contains("[") || !t.getNewValue().contains("]") || !t.getNewValue().contains("H")) {
					try {
						openLinkFormatPopUp();
						t.getOldValue();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else {
					(t.getTableView().getItems().get(
							t.getTablePosition().getRow())
							).setLink(t.getNewValue().toString());
				}
			}
		);
	}
	
	//if text field is empty, this pop up opens
	private void openTextFieldPopUp() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Parent parent = loader.load(getClass().getResource("popup/Step1NoTextInputPopUpView.fxml"));
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

	//if link does not fit format of [index], this pop up opens
	private void openNoSelectedLinkPopUp() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Parent parent = loader.load(getClass().getResource("popup/Step1NoSelectedLinkPopUpView.fxml"));
		Scene scene = new Scene(parent);
		Stage dialogStage = new Stage();
		            
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		dialogStage.setTitle("No link selected");
		
		dialogStage.setScene(scene);
		dialogStage.setResizable(false);
		dialogStage.show();		
	}
	
	//if link does not fit format of [index], this pop up opens
	private void openLinkFormatPopUp() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Parent parent = loader.load(getClass().getResource("popup/Step1LinkFormatPopUpView.fxml"));
		Scene scene = new Scene(parent);
		Stage dialogStage = new Stage();
		            
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		dialogStage.setTitle("Wrong link format");
		
		dialogStage.setScene(scene);
		dialogStage.setResizable(false);
		dialogStage.show();		
	}
	
		//if link for hazard to loss does not fit format, this pop up opens
	private void openLinkModifyPopUp() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Parent parent = loader.load(getClass().getResource("popup/Step1LinkModifyPopUpView.fxml"));
		Scene scene = new Scene(parent);
		Stage dialogStage = new Stage();
		            
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		dialogStage.setTitle("Wrong link format");
		
		dialogStage.setScene(scene);
		dialogStage.setResizable(false);
		dialogStage.show();		
	}
		
	//if link for constraint to hazard does not fit format, this pop up opens
	private void openLinkUpdateNoticePopUp() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Parent parent = loader.load(getClass().getResource("popup/Step1LinkModificationNotice.fxml"));
		Scene scene = new Scene(parent);
		Stage dialogStage = new Stage();
			            
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(mainApp.getPrimaryStage());
		dialogStage.setTitle("Need update");
		
		dialogStage.setScene(scene);
		dialogStage.setResizable(false);
		dialogStage.show();		
	}	
	
	//function to update loss index when one is deleted.
	private void updateLossIndex() {
		ArrayList<String> index = new ArrayList<String>();
		int total = lossTableList.size();

		for(int i = 0; i < total; i++) {
			index.add(lossIndexColumn.getCellData(i));
		}

		for(int i = 0; i < total; i++) {
			if(!index.get(i).equals("L" + Integer.toString(i + 1))){
				for(; i < total; i++){
					lossTableList.get(i).setIndex("");
					lossTableList.get(i).setIndex("L" + Integer.toString(i + 1));
				}
			}
		}
        hazardLinkCB.getItems().remove(lossTableList.size());
	}

	//function to update hazard index when one is deleted.
	private void updateHazardIndex() {
		ArrayList<String> index = new ArrayList<String>();
		int total = hazardTableList.size();

		for(int i = 0; i < total; i++) {
			index.add(hazardIndexColumn.getCellData(i));
		}

		for(int i = 0; i < total; i++) {
			if(!index.get(i).equals("H" + Integer.toString(i + 1))){
				for(; i < total; i++){
					hazardTableList.get(i).setIndex("");
					hazardTableList.get(i).setIndex("H" + Integer.toString(i + 1));
				}
			}
		}
        constraintLinkCB.getItems().remove(hazardTableList.size());
	}
	
	//function to update constraint index when one is deleted.
	private void updateConstraintIndex() {
		ArrayList<String> index = new ArrayList<String>();
		int total = constraintTableList.size();

		for(int i = 0; i < total; i++) {
			index.add(constraintIndexColumn.getCellData(i));
		}

		for(int i = 0; i < total; i++) {
			if(!index.get(i).equals("C" + Integer.toString(i + 1))){
				for(; i < total; i++){
					constraintTableList.get(i).setIndex("");
					constraintTableList.get(i).setIndex("C" + Integer.toString(i + 1));
				}
			}
		}
	}
}