package nuSTPA.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nuSTPA.Info;
import nuSTPA.MainApp;
import nuSTPA.model.XmlReader;
import nuSTPA.model.ct.CTDataStore;
import nuSTPA.model.pmv.PmvDataStore;
import nuSTPA.model.pmv.ProcessModel;
import nuSTPA.model.step2.Components;
import nuSTPA.model.step2.ControlAction;
import nuSTPA.model.step2.Controller;
import nuSTPA.view.components.RectangleView;
import nuSTPA.view.popup.SelectFODController;
import nuSTPA.view.popup.VariablePopUpController;

public class PmvController {
	// connect controller&control action to new tab
	private XmlReader xmlReader;
	private MainApp mainApp;
//	private File selectedFile;
	private ArrayList<File> selectedFiles = new ArrayList<File>();
	private Components components;
	private PmvDataStore pmvDB;

	private MenuItem modifyMenu, deleteMenu, abstractionMenu;

	@FXML
	private Label fileName;
	@FXML
	private Pane addFile;
	@FXML
	private ChoiceBox<String> controllerList, caList;
	@FXML
	private ListView<String> outputList;
	@FXML
	private TabPane tabPane;
	@FXML
	private Button addTabButton, toCTButton, addPmvButton, extractPmvButton;

	//to control each listview for each controller
	ObservableList<ListView<String>> listViewList = FXCollections.observableArrayList();
	ArrayList<ArrayList<String>> inputList = new ArrayList<ArrayList<String>>();

//	ListView<String> lv = new ListView<String>();

	public PmvController() {

	}
	
	public File getSelectedFile(int i) {
		return selectedFiles.get(i);
	}

	// Get Controller, all of CA from CSE DataStore
	public void selectController() {
		// CSE -> PMM
		// get data of selected controller & fix
		Controller controller = components.curController;
		controllerList.getItems().add(controller.getName().toString());
		controllerList.setValue(controller.getName().toString());
		controllerList.setDisable(true);
		if(!outputList.getItems().isEmpty()) {
			outputList.getItems();
		}

		tabPane.getTabs().clear();

		//set CAList item with selected controller
		caList.getItems().clear();
	    for(ControlAction c : components.getControlActions()){
	    	if(c.getController().getName().toString() == controllerList.getValue()){
	    		caList.getItems().addAll(c.getCA().toString());
	       }
	    }

	    //show tab for controller
	    showControllerTab(controller.getName().toString());

//		System.out.println("selected controller : " + controller.getName());
	}

	private void showControllerTab(String controller) {
		//to show tab for selected controller
		for(ProcessModel p : pmvDB.getProcessModel()) {
			if(p.getControllerName().equals(controller))
				addTab(p);
		}
	}
	
	private void showOutputList(String controller, String controlAction) {
		ObservableList<String> tempList = FXCollections.observableArrayList();
//		tempList.clear();
		for(int i = 0; i < pmvDB.getProcessModel().size(); i++) {
			tempList = pmvDB.getProcessModel().get(i).getTotalOutputs();
			if(pmvDB.getProcessModel().get(i).getControllerName().equals(controller) && pmvDB.getProcessModel().get(i).getControlActionName().equals(controlAction)) {
				if(tempList != null) {
					outputList.getItems().addAll(tempList);
					System.out.println("this is outputList of selected controller & ca : " + tempList);
				}
			}
		}
	}

	public void addTab(ProcessModel p) {
		// add new tab to tabPane
		Tab newTab = new Tab();
		newTab.setText(p.getControllerName() + '-' + p.getControlActionName());
		tabPane.getTabs().add(newTab);

		ListView newListView = new ListView();
		valueListControl(newListView);

		// add new listView in newTab
		newTab.setContent(newListView);


		//add value list into list view
		newListView.getItems().addAll(p.getProcessModelList());
		listViewList.add(newListView);
	}

	@FXML
	public void showOutput() {
//		System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡSHOW outputㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
		addFile.setVisible(true);
	}
	
	//show selected file on screen
	@FXML
	public void addNuSCRFile() {
//		if(tabPane.getSelectionModel().getSelectedItem() == null) {
//			Alert alert = new Alert(AlertType.WARNING);
//			alert.setTitle("Warning");
//			alert.setHeaderText("No selected tab");
//			alert.setContentText("You have to make tab with new tab button");
//			alert.showAndWait();
//		}else {
		//file chooser
		FileChooser fc = new FileChooser();
		fc.setTitle("Get output variables");
		fc.setInitialDirectory(new File(Info.directory));
		// Set default directory

		ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fc.getExtensionFilters().add(extFilter);

		selectedFiles.add(fc.showOpenDialog(null));
		if (selectedFiles != null) {
//			dataStore.setFilePath(selectedFile);
			fileName.setText(selectedFiles.get(selectedFiles.size()-1).getName());

			try {
				FileInputStream fis = new FileInputStream(selectedFiles.get(selectedFiles.size()-1));
				BufferedInputStream bis = new BufferedInputStream(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information");
			alert.setHeaderText("No file selected");
			alert.setContentText("You did not choose file to import");
		}
//		}
	}

	@FXML
	public void close() {
		//cancel button
		addFile.setVisible(false);
		fileName.setText("");
	}
	
	//when ok button in dialog is clicked
	@FXML
	public void applyNuSCRFile1() throws ParserConfigurationException, SAXException, IOException {
		// clear all items
		addFile.getChildren().clear();
	    outputList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// Create XmlReader constructor
		ArrayList<String> showGroupNodesItems = xmlReader.parseXml(selectedFiles.get(selectedFiles.size()-1));
		
		//no root FOD(don't fit NuSCR format)
		if(xmlReader.getRootFod().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error!");
			alert.setHeaderText("File doesn't fit NuSCR format");
			alert.setContentText("You have to apply NuSCR file");
		}

		//show popup to select group FODs from NuSRS file
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("popup/SelectFODView.fxml"));
		Parent parent = loader.load();
		Scene scene = new Scene(parent);
		Stage selectGroupStage = new Stage();

		selectGroupStage.setTitle("Select output variable groups");
		selectGroupStage.setResizable(false);
		selectGroupStage.show();

		selectGroupStage.setScene(scene);
		
		//show FODs that are available
		SelectFODController FODcontroller = loader.getController();
		FODcontroller.clear();
		FODcontroller.setGroupItems(showGroupNodesItems);
		FODcontroller.setRootFOD(XmlReader.getRootFod());
		
		selectGroupStage.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				if(FODcontroller.canceled == true) {
					selectGroupStage.close();
				}else if(FODcontroller.confirmed == true) {
					// outputList.getItems().addAll(FODcontroller.selectedItems());
					
					ObservableList<String> totalOutputList = FXCollections.observableArrayList();
					// if you select FOD, get outputs of selected FOD
					
					int i = xmlReader.fodNodeNameList.indexOf(FODcontroller.selectedItem());
//					System.out.println("index of selected item : " + i);
					xmlReader.setRootFod(i);
//					System.out.println("selected root : " + xmlReader.getRootFod());

					// VIEW
					ObservableList<String> outputs = xmlReader.getOutputs();
					// System.out.println("outputs: "+outputs);
					for (String datas : outputs) {
						totalOutputList.add(datas);
					}
				
					//if output list has redundant data, remove from list
					for(int j = 0; j < totalOutputList.size(); j++) {
						for(int k = j; k < totalOutputList.size(); k++) {
							if(totalOutputList.get(j).trim().equals(totalOutputList.get(k).trim())) {
								totalOutputList.remove(k);
							}
						}
					}
					
					//get index of selected controller in process model list
					for(int k = 0; k < pmvDB.getProcessModel().size(); k++) {
						if(pmvDB.getProcessModel().get(k).getControllerName().equals(controllerList.getValue())) {
							pmvDB.getProcessModel().get(k).setTotalOutputs(totalOutputList); // store every output list in DB
							System.out.println("adding total outputs into DB : " + pmvDB.getProcessModel().get(k).getTotalOutputs());
						}
					}
					outputList.setItems(totalOutputList);
					System.out.println("this is total output list : " + totalOutputList);
				}
			}
		});
	}
	
	@FXML
    public void addToProcessModel() {
		//add selected value from output list to value list
	    outputList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    ObservableList<String> selectedOutputList = FXCollections.observableArrayList();
	    selectedOutputList = outputList.getSelectionModel().getSelectedItems();
	//    ArrayList<String> newValues = new ArrayList<String>();
	    ObservableList<String> tempList = FXCollections.observableArrayList();
	    
	    ProcessModel PM = new ProcessModel();
	    for(ProcessModel pm : pmvDB.getProcessModel()){
	        if(pm.getControlActionName().equals(caList.getValue()) && pm.getControllerName().equals(controllerList.getValue())){
	           PM = pm;
	        }
	    }

//        System.out.println("this is connected variables : " + xmlReader.connectedVarSet);
	    
	    if(!selectedOutputList.isEmpty()) {
	    	for(String s: selectedOutputList) {
				xmlReader.getTransitionNodes(s);
			}
	    	ListView<String> lv = new ListView<String>();
	        valueListControl(lv);
	        for(Tab tab : tabPane.getTabs()){
	        	if(tab.getText().equals(controllerList.getValue() + '-' + caList.getValue())){
	        		lv = listViewList.get(tabPane.getTabs().indexOf(tab));
	            }
	        }
	        for(int i = 0; i < xmlReader.connectedVarSet.size(); i++) {
	        	tempList.add(xmlReader.connectedVarSet.get(i));
	        }

	        ObservableList<String> processModelList = FXCollections.observableArrayList();
	        //add to listview & db only when there are no duplication
	        for(String s : tempList) {
	        	if(!lv.getItems().contains(s)) {
			        lv.getItems().add(s);
			        processModelList.add(s);
		        }
	        }
	        PM.setProcessModelList(processModelList);
			System.out.println("process model list being added : " + processModelList);
			components.setProcessModel(processModelList, controllerList.getValue());
			System.out.println("which one is null? " + controllerList.getValue());
	        //add selected outputs in db
	        PM.setSelectedOutputs(selectedOutputList);
	        pmvDB.setSelectedOutputs(selectedOutputList);
	        try {
				applyNuSCRFile2();
			} catch (NullPointerException e1) {
				e1.printStackTrace();
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			} catch (SAXException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    }else {
	    	Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning!");
			alert.setHeaderText("No output variable selected");
			alert.setContentText("You have to choose one or more output variables");
	    }
    }
	

	@FXML
	public void addPMValue(MouseEvent e) {
		// Add new value
		Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
		int tabIndex = tabPane.getSelectionModel().getSelectedIndex();

		if(currentTab == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("No selected tab");
			alert.setContentText("You have to make tab with new tab button");
			alert.showAndWait();
		}else {
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("popup/VariablePopUpView.fxml"));
				Parent parent = loader.load();
				Scene scene = new Scene(parent);
				Stage addVarStage = new Stage();

				addVarStage.initModality(Modality.WINDOW_MODAL);
				addVarStage.initOwner(mainApp.getPrimaryStage());

				addVarStage.setTitle("Add Process Model variable");
				addVarStage.setResizable(false);
				addVarStage.show();

				addVarStage.setScene(scene);
				addVarStage.setOnHidden(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent e) {
						VariablePopUpController popup = loader.getController();
						if (popup.value != null) {
							//don't add same value to listview
							if(listViewList.get(tabIndex).getItems().size() > 0) {
								for(String s : listViewList.get(tabIndex).getItems()) {
									if(popup.value.equals(s)) {
										return;
//										listViewList.get(tabIndex).getItems().remove(popup.value);
									}
								}
							}
							//add to listView
							listViewList.get(tabIndex).getItems().add(popup.value);
							//add to db
							for(ProcessModel pm : pmvDB.getProcessModel()) {
//								System.out.println("add to db");
								if(pm.getControlActionName().equals(caList.getValue()) && pm.getControllerName().equals(controllerList.getValue())){
									pm.getProcessModelList().add(popup.value);
									System.out.println("process model list being added : " + listViewList.get(tabIndex).getItems());
//									for (Controller c : components.getControllers()) {
//										DoubleProperty X = new SimpleDoubleProperty(c.getX());
//									    DoubleProperty Y = new SimpleDoubleProperty(c.getY());
//									    c.clearNum();
//									    
//										RectangleView r = new RectangleView(X, Y, c.getName(), c.getId(), components);
//										c.setRectangle(r);
//									}
//									components.addPMToCurController(controllerList.getValue(), pm.getProcessModelList());
								}
							}
						}
						components.setProcessModel(listViewList.get(tabIndex).getItems(), controllerList.getValue());
					}
				});

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			e.consume();
		}
	}

	public void valueListControl(ListView<String> valueList) {
		ContextMenu rightClickMenu = new ContextMenu();
//		int listIndex = listViewList.indexOf(valueList);

		//list view has to be the list view of current tab
//		if(tabPane.getSelectionModel().getSelectedItem() != null && !listViewList.isEmpty()) {
//			Tab curTab = tabPane.getTabs().get(listViewList.indexOf(valueList));
//			int curTabIndex = tabPane.getTabs().indexOf(curTab);
//			lv = listViewList.get(curTabIndex);
//		}
		valueList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); 
		
		modifyMenu = new MenuItem("Modify");
		deleteMenu = new MenuItem("Delete");
		abstractionMenu = new MenuItem("Abstract");
		rightClickMenu.getItems().addAll(modifyMenu, deleteMenu, abstractionMenu);

		deleteMenu.setOnAction(new EventHandler<ActionEvent>() {
		     @Override
	         public void handle(ActionEvent event) {
		    	 int targetIndex = valueList.getSelectionModel().getSelectedIndex();
		    	 try{
					for(ProcessModel p : pmvDB.getProcessModel()) {
						if(p.getControllerName().equals(controllerList.getValue()) && p.getControlActionName().equals(tabPane.getTabs().get(listViewList.indexOf(valueList)).getText().replace(controllerList.getValue() + '-', ""))) {
							p.getProcessModelList().remove(targetIndex);
						}
					}
					valueList.getItems().remove(targetIndex);
		    	 }catch(Exception e){
					System.out.println("No selected data");
		    	 }
	         }
	        });

		modifyMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event)
			{
				int targetIndex = valueList.getSelectionModel().getSelectedIndex();
				try{
					for(ProcessModel p : pmvDB.getProcessModel()) {
						if(p.getControllerName().equals(controllerList.getValue()) && p.getControlActionName().equals(tabPane.getTabs().get(listViewList.indexOf(valueList)).getText().replace(controllerList.getValue() + '-', ""))) {
							modifyPopUp(p, targetIndex,valueList);
						}
					}
				}catch(Exception e){
					System.out.println("No selected data");
				}
			}
		});

		abstractionMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ObservableList<Integer> targetIndices = valueList.getSelectionModel().getSelectedIndices();
				try {
					for(ProcessModel p : pmvDB.getProcessModel()) {
						if(p.getControllerName().equals(controllerList.getValue()) && p.getControlActionName().equals(tabPane.getTabs().get(listViewList.indexOf(valueList)).getText().replace(controllerList.getValue() + '-', ""))) {
							abstractProcessModel(p, valueList, targetIndices);
						}
					}
				}catch(Exception e1) {
					System.out.println("No selected data");
				}
			}
		});

		valueList.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
	        public void handle(ContextMenuEvent event) {
				rightClickMenu.show(valueList, event.getScreenX(), event.getScreenY());
	        }
	    });
//		rightClickMenu.hide();
	}
	
	//execute abstraction
	private void abstractProcessModel(ProcessModel p, ListView<String> processModelList, ObservableList<Integer> targetIndices) {
//		StringBuilder commonString1 = new StringBuilder(); //String before diffString
//		StringBuilder commonString2 = new StringBuilder(); //String after diffString
//		StringBuilder diffString = new StringBuilder();
		
		//그냥...사람이..손으로 수정하게 해도 된다.....
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("popup/VariablePopUpView.fxml"));
			Parent root;
			root = loader.load();
			Scene s = new Scene(root);
			Stage modVarStage = new Stage();


			modVarStage.setScene(s);
			modVarStage.setTitle("Abstract Process Model variables");
			modVarStage.setResizable(false);
			modVarStage.show();

			modVarStage.setOnHidden(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent e) {
					VariablePopUpController popup = loader.getController();
					if (popup.value != null) {
						//modify data in listView
						try{
							int currentTabIndex = listViewList.indexOf(processModelList);
							listViewList.get(currentTabIndex).getItems().set(targetIndices.get(0), popup.value);
							//modify data in db
							p.getProcessModelList().set(targetIndices.get(0), popup.value);
							for(int i = 1; i < targetIndices.size(); i++) {
								listViewList.get(currentTabIndex).getItems().set(targetIndices.get(i), "");
								p.getProcessModelList().set(targetIndices.get(i), "");
							}
							p.getProcessModelList().removeAll("");
							listViewList.get(currentTabIndex).getItems().removeAll("");
						} catch(Exception ex){
							System.out.println("Abstract Process Model List Error");
						}
					}
				}
			});
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
		
		
	// Show value edit popup
	public void modifyPopUp(ProcessModel p, int oldvalueIndex,ListView<String> curList) {
//		System.out.println("modify pop up executing...");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("popup/VariablePopUpView.fxml"));
			Parent root;
			root = loader.load();
			Scene s = new Scene(root);
			Stage modVarStage = new Stage();


			modVarStage.setScene(s);
			modVarStage.setTitle("Modify Process Model variable");
			modVarStage.setResizable(false);
			modVarStage.show();

			modVarStage.setOnHidden((new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent e) {
					VariablePopUpController popup = loader.getController();
					if (popup.value != null) {
						//modify data in listView
						try{
							int currentTabIndex = listViewList.indexOf(curList);
							listViewList.get(currentTabIndex).getItems().set(oldvalueIndex, popup.value);
							//modify data in db
							p.getProcessModelList().set(oldvalueIndex, popup.value);
						} catch(Exception ex){
							System.out.println("Modify ProcessModelList Error");
						}
					} 
				}
			}));

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void initialize() {
//		System.out.println("\n**********************");
//		System.out.println("initialize!! ");
		// PMM, CSE Data Store
		pmvDB = this.mainApp.pmvDB;
		components = this.mainApp.components;
		
		addFile.setVisible(false);

		controllerList.getItems().clear();
		controllerList.requestFocus();

		// From Dashboard to PMM
		if (components.curController == null) {
			// When through file open,

			//set Controller ,ControlAction ChoiceBox
		    for(Controller c : components.getControllers()){
		       controllerList.getItems().add(c.getName().toString());
		    }
		    //if Controller Select, Change ControlAction ChoiceBox
		    controllerList.setOnAction(event->{
		       //clear all views
				outputList.getItems().clear();
		        tabPane.getTabs().clear();
		        listViewList.clear();
		        showControllerTab(controllerList.getValue());

		        //clear CAList view
		        caList.getItems().clear();
//		        String controller = controllerList.getValue();
		        //ERROR! Null pointer exception -> don't bring controller name from CSE
		        for(ControlAction c : components.getControlActions()) {
//		    	    System.out.println(c.getController() + " : controller");
//		    	    System.out.println(c.getCA() + " : control actions");
		    	    if(c.getController() == null) {
		    		    continue;
		    	    }else if(c.getController().getName().equals(controllerList.getValue())) {
		    	        caList.getItems().addAll(c.getCA().toString());
		    	    }
		        }
		    });
		} else {
			// CSE -> PMM
//			controllerName = dataStore.getControllerName();
			selectController();
		}
		//show total output when controller & control action is selected
		caList.setOnAction(event -> {
			outputList.getItems().clear();
			showOutputList(controllerList.getValue(), caList.getValue());
		});
		
		addTabButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				//check if tab for selected controller & control action already exists
				for(ProcessModel p : pmvDB.getProcessModel()) {
					if(p.getControllerName().equals(controllerList.getValue()) && p.getControlActionName().equals(caList.getValue())) {
						return;
					}
				}
				
				if(caList.getValue() != null && controllerList.getValue() != null) {
					Tab newTab = new Tab();
					tabPane.getTabs().add(newTab);
					ListView<String> newListView = new ListView<String>();
					valueListControl(newListView);
					listViewList.add(newListView);
					newTab.setText(controllerList.getValue() + '-' + caList.getValue());
					
					ProcessModel newProcessModel = new ProcessModel();
					newProcessModel.setControllerName(controllerList.getValue());
					newProcessModel.setControlActionName(caList.getValue());
					pmvDB.getProcessModel().add(newProcessModel);

					newTab.setContent(newListView);
				}
//				else {
//					Alert alert = new Alert(AlertType.ERROR);
//					alert.setTitle("Error!");
//					alert.setHeaderText("Wrong way of adding new tab");
//					alert.setContentText("You have to select both controller and control action to create new tab!");
//					alert.showAndWait();
//					return;
//				}
			}
		});
		
		toCTButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				for(Tab tab : tabPane.getTabs()){
					if(listViewList.get(tabPane.getTabs().indexOf(tab)) != null) {
						mainApp.showCTView();
					}
				}
			}
		});
	}

	//applying NuSCR File, used on CTController
	public void applyNuSCRFile2() throws ParserConfigurationException, SAXException, IOException, NullPointerException {
		//save output types in CT DB
//		selectedNuSCRFile = pmvController.getSelectedFile();
		ArrayList<String> variableTypeList = xmlReader.parseNuSCR(selectedFiles.get(selectedFiles.size() - 1));
		ArrayList<String> tempList = new ArrayList<String>();
		ArrayList<String> tempOutputList = new ArrayList<String>();
		ListView<String> lv = new ListView<String>();
		
		for(Tab tab : tabPane.getTabs()){
        	if(tab.getText().equals(controllerList.getValue() + '-' + caList.getValue())){
        		lv = listViewList.get(tabPane.getTabs().indexOf(tab));
            }
        }
		
		//compare total variables with selected output variables and add to tempOutputList
		for(String s : outputList.getSelectionModel().getSelectedItems()) {
			for(int i = 0; i < variableTypeList.size(); i++) {
				if(variableTypeList.get(i).contains(s.trim()) && !tempOutputList.contains(variableTypeList.get(i))) {
					tempOutputList.add(variableTypeList.get(i));
				}
			}
		}
		
		System.out.println("these are variables in process model list view : " + lv.getItems());
		
//		ObservableList<String> processModelTypeList = FXCollections.observableArrayList();
		//compare process model with total variables and add to tempList
		for(String s : lv.getItems()) {
			for(int i = 0; i < variableTypeList.size(); i++) {
				if(variableTypeList.get(i).contains(s.trim()) && !tempList.contains(variableTypeList.get(i))) {
			        //add to listview & db only when there are no duplication
					tempList.add(variableTypeList.get(i));
				}
			}
		}
		
		//if there exists duplication between tempList & tempOutputList, remove to solve redundancy
		for(String s : tempOutputList) {
			if(tempList.contains(s)) {
				tempList.remove(tempList.indexOf(s));
			}
		}
		
		CTDataStore.getOutputTypeList().addAll(tempOutputList);
		CTDataStore.getPmTypeList().addAll(tempList);
		System.out.println("these are types of selected output variables : " + tempOutputList);
		System.out.println("these are types of process model : " + tempList);
		
//		return tempList;
	}
	
	// set MainApp
		public void setMainApp(MainApp mainApp) {
			this.mainApp = mainApp;
			this.initialize();
		}
	}