package nuSTPA.view;

import javafx.scene.control.Button;
import nuSTPA.MainApp;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class HomeViewController{
	private MainApp mainApp;
	
	//constructor
	public HomeViewController() {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	//click button 'Step 1. Define Purpose of the Analysis'
	@FXML
	private void toStep1Controller() {
		this.mainApp.showStep1View();
	}

	//click button 'Step 2. Model the Control Structure'
	@FXML
	private void toStep2Controller() {
		this.mainApp.showStep2View();
	}

	//click button '(Step 2) Extract the information needed to create process model'
	@FXML
	private void toPMVController() {
		this.mainApp.showPMVView();
	}

	//click button '(Step 5) Generate Combinations of PMV for CT'
	@FXML
	private void toCTController() {
		this.mainApp.showCTView();
	}

	//click button 'Step 3. Identify Unsafe Control Actions'
	@FXML
	private void toStep3Controller() {
		this.mainApp.showStep3View();
	}

	//click button 'Step 4. Identify loss scenario'
	@FXML
	private void toStep4Controller() {
		this.mainApp.showStep4View();
	}
}
