package nuSTPA.view.popup.step2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nuSTPA.MainApp;

public class AddTextPopUpController{

	  @FXML 
	  private TextField text;
	  public String content;
	  public MainApp mainApp;
	  public boolean OKclose;
	  
	  public AddTextPopUpController() {
		  OKclose = false;
		  content = "Text Content";
	  }
	  
	  public void setData() {
		  OKclose = true;
		  content = text.getText();
		  close();
	  }
	  
	  public void close() { 
	       Stage pop = (Stage)text.getScene().getWindow(); 
	       pop.close();
	  }
}
