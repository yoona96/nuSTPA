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
//	private ArrayList<StringProperty> contextsProperty;
//	private ArrayList<StringProperty> contextsWithValsProperty;
	private ArrayList<StringProperty> totalContextsProperty;

	private String controllerName;
	private String controlAction;
	private String cases;
	private int no;
	private String hazardous;
//	private ArrayList<String> contexts;
//	private ArrayList<String> contextComboBoxVals;
	private ArrayList<String> totalContexts;

	public CT(String controllerName, String controlAction, ComboBox<String> cases, int no, ArrayList<String> totalContexts, ComboBox<String> hazardous) {
		this.controllerNameProperty = new SimpleStringProperty(controllerName);
		this.controlActionProperty = new SimpleStringProperty(controlAction);
		this.casesComboBox = cases;
		this.noProperty = new SimpleIntegerProperty(no);
		this.hazardousComboBox = hazardous;
//		this.contextsProperty = new ArrayList<StringProperty>();
//		this.contextComboBoxVals = contextComboBoxVals;
		
		cases.setOnAction(event -> {
			this.cases = cases.getValue();
		});

		hazardous.setOnAction(event -> {
			this.hazardous = hazardous.getValue();
		});


//		if(totalContexts != null) {
//			for(int i = 0; i < totalContexts.size(); i++) {
//				System.out.println(i + " : " + totalContexts.get(i));
//				this.totalContextsProperty.get(i).set(totalContexts.get(i)); //TODO: nullpointerException Occurs
//			}
//		}
		if(totalContexts != null) {
			ArrayList<StringProperty> temp = new ArrayList<StringProperty>();
			for(int i = 0; i < totalContexts.size(); i++) {
				temp.add(new SimpleStringProperty(totalContexts.get(i)));
			}
			this.totalContextsProperty = temp;
		}

		this.controllerName = controllerName;
		this.controlAction = controlAction;
		this.cases = cases.getValue();
		this.no = no;
		this.totalContexts = totalContexts;
//		this.contexts = contexts;
		this.hazardous = hazardous.getValue();
	}
	
	public void setCTInit(){
		this.controlActionProperty = new SimpleStringProperty(this.controlAction);
		this.noProperty = new SimpleIntegerProperty(this.no);
		this.totalContextsProperty = new ArrayList<StringProperty>();
		for(int i = 0; i < totalContexts.size(); i++) {
			this.totalContextsProperty.add(i, new SimpleStringProperty(totalContexts.get(i)));
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

//	public ArrayList<String> getContexts() {
//		return contexts;
//	}
//	
//	public String getContext(int i) {
//		return contextsProperty.get(i).get();
//	}
//	
//	public ArrayList<StringProperty> getContextsWithValsProperty() {
//		return contextsWithValsProperty;
//	}
//
//	public void setContextsWithValsProperty(ArrayList<StringProperty> contextsWithValsProperty) {
//		this.contextsWithValsProperty = contextsWithValsProperty;
//	}
	
	public ArrayList<StringProperty> getTotalContextsProperty() {
		return totalContextsProperty;
	}
	
	public StringProperty getTotalContextProperty(int i){
		StringProperty temp = new SimpleStringProperty("");
		if(totalContextsProperty.get(i) != null) {
			return totalContextsProperty.get(i);
		}else {
			return temp;
		}
	}

	public void setTotalContextsProperty(ArrayList<StringProperty> totalContextsProperty) {
		this.totalContextsProperty = totalContextsProperty;
	}

	public String getTotalContext(int i) {
		return totalContextsProperty.get(i).get();
	}
	
	public void setContext(int i, String val) {
		this.totalContextsProperty.add(i, new SimpleStringProperty(val));
	}
	
//
//	public void setContext(int i, String val) {
//		this.contextsProperty.add(i, new SimpleStringProperty(val));
//	}
	
	public ArrayList<String> getTotalContexts() {
		return totalContexts;
	}

	public void setTotalContexts(ArrayList<String> totalContexts) {
		this.totalContexts = totalContexts;
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

//	public ArrayList<String> getContextComboBoxVals() {
//		return contextComboBoxVals;
//	}
//	
//	public void setContextComboBoxVals(ArrayList<String> contextComboBoxVals) {
//		this.contextComboBoxVals = contextComboBoxVals;
//	}
//	
//	public void setContextComboBoxVal(int i, String contextComboBoxVal) {
//		this.contextComboBoxVals.add(i, contextComboBoxVal);
//	}

}