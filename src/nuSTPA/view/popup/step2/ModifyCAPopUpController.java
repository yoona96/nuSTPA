package nuSTPA.view.popup.step2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nuSTPA.MainApp;
 
public class ModifyCAPopUpController implements Initializable {

	  public ArrayList<String> CA;
	  
	  @FXML 
	  private Button add;
	  @FXML 
	  private Button remove;
	  @FXML 
	  private ListView<String> listView;
	  @FXML 
	  private TextField listInput;  
	  private ObservableList<String> listItems;   
	  
	  public MainApp mainApp;
	  public boolean OKclose;
	  
	  public ModifyCAPopUpController() {
		 OKclose = false;
	  }
	  
	  public void setData() {
			  if(!listItems.isEmpty()) {
				  OKclose = true;
				  close();
			  }else {
				  FXMLLoader loader = new FXMLLoader();
				  loader.setLocation(getClass().getResource("ErrorNoCA.fxml"));
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
	       Stage pop = (Stage)add.getScene().getWindow(); 
	       pop.close();
	  }
	  
	  @FXML
	  private void addCA(ActionEvent action){
		  if(!listInput.getText().equals("")) {
				if(listItems.contains(listInput.getText())) {
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
					listItems.add(listInput.getText());
				    CA.add(listInput.getText());
				    listInput.clear();
				}
			}
	  }
	  
	  @FXML
	  private void removeCA(ActionEvent action){
	    int selectedItem = listView.getSelectionModel().getSelectedIndex();
	    listItems.remove(selectedItem);
	    CA.remove(selectedItem);
	  }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//set ListView
		CA = MainApp.components.curCA.getCA();
		listItems = FXCollections.observableArrayList();
		for(int i=0; i<CA.size(); i++) {
			listItems.add(CA.get(i).toString());
		}
		listView.setItems(listItems);
		
		//Disable buttons to start
		add.setDisable(true);
		remove.setDisable(true);

	    // Add a ChangeListener to TextField to look for change in focus
		listInput.focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        if(listInput.isFocused()){
		        	add.setDisable(false);
				}
			}
		});    

		// Add a ChangeListener to ListView to look for change in focus
		listView.focusedProperty().addListener(new ChangeListener<Boolean>() {
		     public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		        if(listView.isFocused()){
		          remove.setDisable(false);
		        }
		     }
		});
	}
}
