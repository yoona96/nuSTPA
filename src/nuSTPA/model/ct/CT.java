package nuSTPA.model.ct;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class CT {
	private StringProperty controllerNameProperty;
	private StringProperty controlActionProperty;
	private ComboBox<String> casesComboBox;
	private IntegerProperty noProperty;
	private ComboBox<String> hazardousComboBox;
	private StringProperty[] contextsProperty;

	private String controllerName;
	private String controlAction;
	private String cases;
	private int no;
	private String hazardous;
	private String[] contexts;
	private String[] contextComboBoxVals;

	public CT(String controllerName, String controlAction, ComboBox<String> cases, int no, String[] contexts, String[] contextComboBoxVals, ComboBox<String> hazardous) {
		this.controllerNameProperty = new SimpleStringProperty(controllerName);
		this.controlActionProperty = new SimpleStringProperty(controlAction);
		this.casesComboBox = cases;
		this.noProperty = new SimpleIntegerProperty(no);
		this.hazardousComboBox = hazardous;
		this.contextsProperty = new StringProperty[contexts.length];
		this.contextComboBoxVals = contextComboBoxVals;
		
		cases.setOnAction(event -> {
			this.cases = cases.getValue();
		});

		hazardous.setOnAction(event -> {
			this.hazardous = hazardous.getValue();
		});


		for(int i = 0; i < contexts.length; i++) {
			this.contextsProperty[i] = new SimpleStringProperty(contexts[i]);
		}

		this.controllerName = controllerName;
		this.controlAction = controlAction;
		this.cases = cases.getValue();
		this.no = no;
		this.contexts = contexts;
		this.hazardous = hazardous.getValue();
	}
	
	public void setCTMInit(){
		this.controlActionProperty = new SimpleStringProperty(this.controlAction);
		this.noProperty = new SimpleIntegerProperty(this.no);
		this.contextsProperty = new StringProperty[contexts.length];
		for(int i=0;i<contexts.length;i++) {
			this.contextsProperty[i] = new SimpleStringProperty(contexts[i]);
		}
	}
	
	public String getControllerName() {
		return controllerNameProperty.get();
	}
	
	public StringProperty getControllerNameProperty() {
		return controllerNameProperty;
	}
	public void setControllerName(String val) {
		this.controllerNameProperty.set(val);
	}

	public String[] getContexts() {
		return contexts;
	}
	
	public String getContext(int i) {
		return contextsProperty[i].get();
	}
	
	public StringProperty getContextProperty(int i) {
		//System.out.println("property["+i+"]:"+test[i]);
		return contextsProperty[i];
	}
	public void setContext(int i, String val) {
		this.contextsProperty[i].set(val);
	}
	
	public String getControlAction() {
		return controlActionProperty.get();
	}
	
	public StringProperty getControlActionProperty() {
		return controlActionProperty;
	}
	public void setControlAction(String val) {
		this.controlActionProperty.set(val);
	}
	
	
	public int getNo() {
		return noProperty.get();
	}
	
	public IntegerProperty getNoProperty() {
		return noProperty;
	}

	public ComboBox<String> getCases() {
		return casesComboBox;
	}
	public String getCasesValue() {
		return cases;
	}

	public void setCases(ComboBox<String> val) {
		this.casesComboBox = val;
		cases = casesComboBox.getValue();
	}
	
	public void setCasesValue(String val) {
		this.casesComboBox.setValue(val);
		cases = casesComboBox.getValue();
	}

	public ComboBox<String> getHazardous() {
		return hazardousComboBox;
	}
	
	public String getHazardousValue() {
		return hazardous;
	}

	public void setHazardous(ComboBox<String>  val) {
		this.hazardousComboBox = val;
		hazardous = hazardousComboBox.getValue();
	}
	
	public void setHazardousValue(String val) {
		this.hazardousComboBox.setValue(val);
		hazardous = hazardousComboBox.getValue();
	}
	public CT get(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getContextComboBoxVals() {
		return contextComboBoxVals;
	}
	
	public void setContextComboBoxVals(String[] contextComboBoxVals) {
		this.contextComboBoxVals = contextComboBoxVals;
	}
	
	public void setContextComboBoxVal(int i, String contextComboBoxVal) {
		this.contextComboBoxVals[i] = contextComboBoxVal;
	}
}