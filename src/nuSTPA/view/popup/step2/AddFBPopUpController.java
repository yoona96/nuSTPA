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
 
public class AddFBPopUpController implements Initializable {

	@FXML 
	private ComboBox<String> controllerName = new ComboBox<String>(), controlledProcessName = new ComboBox<String>();
	public String controller, controlledProcess;
	public ArrayList<String> FB;
	  
	@FXML 
	private Button add;
	@FXML 
	private Button remove;
	@FXML 
	private ListView<String> feedbackListView;
	@FXML 
	private TextField feedbackListInput;  
	private ObservableList<String> feedbackListItems = FXCollections.observableArrayList();    
	
	public MainApp mainApp;
	public boolean OKclose;
	  
	private Components dataStore;
	  
	private ObservableList<String> controllerList = FXCollections.observableArrayList();
	  
	public AddFBPopUpController() {
		OKclose = false;
		controller = "Controller Name";
		controlledProcess = "Controlled Process Name";
		FB = new ArrayList<String>();
	}
	  
	public void setData() {
		dataStore = mainApp.components;

		if(dataStore.findController(controllerName.getValue())!=null && dataStore.findController(controlledProcessName.getValue())!=null) {
			controller = controllerName.getSelectionModel().getSelectedItem();
			controlledProcess = controlledProcessName.getSelectionModel().getSelectedItem();
			  
			if(!feedbackListItems.isEmpty()) {
				Controller  controller = mainApp.components.findController(this.controller);
				Controller  controlled = mainApp.components.findController(this.controlledProcess);
				if(controller.getFB().isEmpty() || controlled.getFB().isEmpty()) {
					OKclose = true;
    				close();
				}else {
					int i=0;
					for( Integer fbId : controller.getFB().keySet() ){
	            		if(controlled.getFB().containsKey(fbId)) {
	            			FXMLLoader loader = new FXMLLoader();
		          			loader.setLocation(getClass().getResource("ErrorSameFB.fxml"));
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
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("ErrorNoFBText.fxml"));
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
		else {
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
	private void addFB(ActionEvent action){
		if(!feedbackListInput.getText().equals("")) {
			if(feedbackListItems.contains(feedbackListInput.getText())) {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("ErrorSameFBText.fxml"));
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
				feedbackListItems.add(feedbackListInput.getText());
			    FB.add(feedbackListInput.getText());
			    feedbackListInput.clear();
			    feedbackListView.setItems(feedbackListItems);
			}
		}	 
	}
	  
	@FXML
	private void removeFB(ActionEvent action){
		int selectedItem = feedbackListView.getSelectionModel().getSelectedIndex();
	    feedbackListItems.remove(selectedItem);
	    FB.remove(selectedItem);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dataStore = mainApp.components;
		
		//set ListView
		feedbackListItems = FXCollections.observableArrayList(); 
		feedbackListView.setItems(feedbackListItems);
		
		//Disable buttons to start
		add.setDisable(true);
		remove.setDisable(true);

		controllerList.clear();
		
		for(int i = 0; i < dataStore.getControllers().size(); i++) {
			controllerList.add(dataStore.getControllers().get(i).getName());
		}
				
		System.out.println(controllerList);
		
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
		feedbackListInput.focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        if(feedbackListInput.isFocused()){
		        	add.setDisable(false);
				}
			}
		});    

		// Add a ChangeListener to ListView to look for change in focus
		feedbackListView.focusedProperty().addListener(new ChangeListener<Boolean>() {
		     public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        if(feedbackListView.isFocused()){
		          remove.setDisable(false);
		        }
		     }
		});
	}
}
