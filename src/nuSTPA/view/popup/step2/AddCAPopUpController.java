package nuSTPA.view.popup.step2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nuSTPA.MainApp;
import nuSTPA.model.step2.Components;
import nuSTPA.model.step2.Controller;

public class AddCAPopUpController implements Initializable {

	@FXML 
	private ComboBox<String> controllerName = new ComboBox<String>(), controlledProcessName = new ComboBox<String>();
	public String controller, controlledProcess;
	public ArrayList<String> CA;
	  
	private ObservableList<String> controllerList = FXCollections.observableArrayList();
	  
	@FXML 
	private Button add, remove;
	@FXML 
	private ListView<String> controlActionListView;
	@FXML 
	private TextField controlActionInput;  
	private ObservableList<String> controlActionListItems = FXCollections.observableArrayList(); //has CA list items
	  
	public MainApp mainApp;
	public boolean OKclose = false;
	  
	private Components dataStore;
	  
	public AddCAPopUpController() {
		OKclose = false;
		controller = "Controller Name";
		controlledProcess = "Controlled Process Name";
		CA = new ArrayList<String>();
	}
	  
	public void setData() {
		dataStore = mainApp.components;			
		if(dataStore.findController(controller) != null && dataStore.findController(controlledProcess)!=null) {
			//controller와 controlled process가 전부 선택되었을 때
			if(!controlActionListItems.isEmpty()) {
				//controlActionList에 Item이 존재하지 않음
				Controller  controller = dataStore.findController(this.controller);
				Controller  controlled = dataStore.findController(this.controlledProcess);
				if(controller.getCA().isEmpty() || controlled.getCA().isEmpty()) {
					//기존에 controller가 가지고 있는 CA가 없으면? 창을 닫을 수 있다? 먼 말이지
					OKclose = true;
    				close();
				}else {
					int i=0;
					for( Integer caId : controller.getCA().keySet() ){
		            	if(controlled.getCA().containsKey(caId)) {
		            		//there already exists same arrow for selected controller to selected controlled process
		            		FXMLLoader loader = new FXMLLoader();
			          		loader.setLocation(getClass().getResource("ErrorSameCA.fxml"));
			          		Parent popUproot;
			          		try {
			          		 	popUproot = (Parent) loader.load();
			          		 	Scene scene = new Scene(popUproot);
			          			Stage stage = new Stage();
			          			stage.setScene(scene);
			          			stage.show();				
		          			}catch(IOException e) {
		          				e.printStackTrace();
		          			}
			          		break;
		            	}else {
		            		i++;
		            	}	
		            }
					if(i==controller.getCA().size()) {
						OKclose = true;
	       				close();
					}
				}  
			}else {
				//controlActionList에 Item이 존재함
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("ErrorNoCAText.fxml"));
				Parent popUproot;
				try {
					popUproot = (Parent) loader.load();
					Scene scene = new Scene(popUproot);
					Stage stage = new Stage();
					stage.setScene(scene);
					stage.show();
				}catch(IOException e) {
					e.printStackTrace();
				}  
			}  
		}else {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("ErrorNotFoundController.fxml"));
			Parent popUproot;
			try {
				popUproot = (Parent) loader.load();
				Scene scene = new Scene(popUproot);
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.show();
			}catch(IOException e) {
				e.printStackTrace();
			}  
		}
	}
	  
	public void close() { 
		Stage pop = (Stage)controllerName.getScene().getWindow(); 
	    pop.close();
	}
	  
	@FXML
	private void addCA(ActionEvent action){
		if(!controlActionInput.getText().equals("")) {
			if(controlActionListItems.contains(controlActionInput.getText())) {
				//there already exists same CA
				FXMLLoader loader = new FXMLLoader();
				  loader.setLocation(getClass().getResource("ErrorSameCAText.fxml"));
				  Parent popUproot;
				  try {
					  	popUproot = (Parent) loader.load();
						Scene scene = new Scene(popUproot);
						Stage stage = new Stage();
						stage.setScene(scene);
						stage.show();
				  }catch(IOException e) {
					  e.printStackTrace();
				  }  
			}else {
				controlActionListItems.add(controlActionInput.getText());
			    CA.add(controlActionInput.getText());
			    controlActionInput.clear();
				controlActionListView.setItems(controlActionListItems);
			}
		}
		
	}
	  
	@FXML
	private void removeCA(ActionEvent action){
		int selectedItem = controlActionListView.getSelectionModel().getSelectedIndex();
	    controlActionListItems.remove(selectedItem);
	    CA.remove(selectedItem);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dataStore = mainApp.components;
		
		//set ListView
		if(controlActionListItems.size() != 0) {
			controlActionListView.setItems(controlActionListItems);
		}
		
		//Disable buttons to start
		add.setDisable(true);
		remove.setDisable(true);
		
		controllerList.clear();
		
		for(int i = 0; i < dataStore.getControllers().size(); i++) {
			controllerList.add(dataStore.getControllers().get(i).getName());
		}
		
		for(int j = 0; j < controllerList.size(); j++) {
			controllerName.getItems().add(controllerList.get(j));			
			controlledProcessName.getItems().add(controllerList.get(j));
		}
		
		controllerName.valueProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String initVal, String newVal) {
				controller = controllerName.getValue();
			}
		});
		
		controlledProcessName.valueProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String initVal, String newVal) {
				controlledProcess = controlledProcessName.getValue();
			}
		});
		
	    // Add a ChangeListener to TextField to look for change in focus
		controlActionInput.focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        if(controlActionInput.isFocused()){
		        	add.setDisable(false);
				}
			}
		});    

		// Add a ChangeListener to ListView to look for change in focus
		controlActionListView.focusedProperty().addListener(new ChangeListener<Boolean>() {
		     public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        if(controlActionListView.isFocused()){
		          remove.setDisable(false);
		        }
		     }
		});
		
//		controlActionListItems.addListener(new ListChangeListener<String>() {
//			public void onChanged(ListChangeListener.Change<? extends String> c) {
//		        if(!controlActionListItems.isEmpty()){
//		          remove.setDisable(false);
//		        }
//		     }
//		});
	}
}
