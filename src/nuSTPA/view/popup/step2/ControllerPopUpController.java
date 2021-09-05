package nuSTPA.view.popup.step2;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nuSTPA.MainApp;
 
public class ControllerPopUpController {

	  @FXML 
	  private TextField text;
	  public String name;
	  public MainApp mainApp;
	  public boolean OKclose;
	  
	  public ControllerPopUpController() {
		  OKclose = false;
		  name = "Controller Name";
	  }
	  
	  public void setData() {
		  if(mainApp.components.findController(text.getText())==null) {
			  name = text.getText();
			  OKclose = true;
			  close();
		  }
		  else {
			  FXMLLoader loader = new FXMLLoader();
			  loader.setLocation(getClass().getResource("ErrorSameController.fxml"));
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
	       Stage pop = (Stage)text.getScene().getWindow(); 
	       pop.close();
	  }


}
