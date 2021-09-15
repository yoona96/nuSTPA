package nuSTPA.model.ct;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class CTDataStore {

	private String ctController, ctControlAction;
	private ArrayList<String> ctCases = new ArrayList<String>();
	private ArrayList<ArrayList<String>> ctContexts = new ArrayList<ArrayList<String>>();
	private ArrayList<String> ctHazardous = new ArrayList<String>();
	private ArrayList<ArrayList<String>> ctContextComboBoxs = new ArrayList<ArrayList<String>>();
	private ArrayList<ArrayList<String>> totalContexts = new ArrayList<ArrayList<String>>();
	private int rowSize;
	
	private static ArrayList<String> outputTypeList = new ArrayList<String>();
	private static ArrayList<String> pmTypeList = new ArrayList<String>();
	
	private ObservableList<CT> ctTableList = FXCollections.observableArrayList();

	public ObservableList<CT> getCtTableList() {
		rowSize = ctTableList.size();
		if(rowSize > 0) {
			setCtController(this.ctTableList.get(0).getControllerName());
			ctControlAction = this.ctTableList.get(0).getControlAction();
			ArrayList<String> tempCases = new ArrayList<String>();
			ArrayList<String> tempHazardous = new ArrayList<String>();
			ArrayList<ArrayList<String>> tempContexts = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> tempContextComboBoxs = new ArrayList<ArrayList<String>>();
			for(int i = 0; i < rowSize; i++) {
				tempCases.add(this.ctTableList.get(i).getCasesValue());
				tempHazardous.add(this.ctTableList.get(i).getHazardousValue());
				tempContexts.add(this.ctTableList.get(i).getTotalContexts());
//				tempContextComboBoxs.add(this.ctTableList.get(i).getContextComboBoxVals());
			}
			ctCases = tempCases;
			ctHazardous = tempHazardous;
			ctContexts = tempContexts;
			setCtContextComboBoxs(tempContextComboBoxs);
			
		}
		
		if(this.ctTableList.size()==0 && ctCases.size()>0) {
			rowSize = ctCases.size();
			
			ObservableList<String>hazardousOX = FXCollections.observableArrayList();
			hazardousOX.add("X");
			hazardousOX.add("O");
			
			ObservableList<String>casesCombo = FXCollections.observableArrayList();
			casesCombo.add("Providing Causes Hazard");
			casesCombo.add("Not Providing Causes Hazard");
			casesCombo.add("Incorrect Timing/Order");
			casesCombo.add("Stopped Too Soon/Applied Too Long");
			
			
			
			for(int i = 0; i < rowSize; i++) {
	    		ComboBox<String> casesComboBox = new ComboBox<String> (casesCombo);
	    		ComboBox<String> hazardousComboBox = new ComboBox(hazardousOX);
	    		ArrayList<String> contextComboBoxs = new ArrayList<String>();
				casesComboBox.setValue(this.ctCases.get(i));
				hazardousComboBox.setValue(this.ctHazardous.get(i));
				this.ctTableList.add(new CT(getCtController(), ctControlAction, casesComboBox, i+1, totalContexts.get(i), hazardousComboBox));
				ctTableList.get(i).setCasesValue(this.ctCases.get(i));
				ctTableList.get(i).setHazardousValue(this.ctHazardous.get(i));
				final int temp = i;
	      		casesComboBox.valueProperty().addListener(new ChangeListener<String>() {
	  			      @Override
	  			      public void changed(ObservableValue observable, String oldValue, String newValue) {
	  			    	ctTableList.get(temp).setCasesValue(newValue);
	  			    	ctCases.set(temp, newValue);
	  			    	getCtTableList();
	  			      }
  			    });
        		hazardousComboBox.valueProperty().addListener(new ChangeListener<String>() {
  			      @Override
  			      public void changed(ObservableValue observable, String oldValue, String newValue) {
	  			    	ctTableList.get(temp).setHazardousValue(newValue);
	  			    	ctHazardous.set(temp, newValue);
	  			    	getCtTableList();
  			      }
  			    });
			}
		}
		return this.ctTableList;
	}
	
	public String getController() {
		return this.getCtController();
	}

	public void setController(String controller) {
		this.setCtController(controller);
	}

	public String getControlAction() {
		return this.ctControlAction;
	}

	public void setControlAction(String controlAction) {
		this.ctControlAction = controlAction;
	}
	
	public ArrayList<String> getCases() {
		return this.ctCases;
	}

	public void setCases(ArrayList<String> cases) {
		this.ctCases = cases;
	}
	
	public ArrayList<ArrayList<String>> getContexts() {
		return this.ctContexts;
	}

	public void setContexts(ArrayList<ArrayList<String>> contexts) {
		this.ctContexts = contexts;
	}
	
	public ArrayList<ArrayList<String>> getCtContextComboBoxs() {
		return ctContextComboBoxs;
	}

	public void setCtContextComboBoxs(ArrayList<ArrayList<String>> ctContextComboBoxs) {
		this.ctContextComboBoxs = ctContextComboBoxs;
	}

	public ArrayList<ArrayList<String>> getTotalContexts() {
		return totalContexts;
	}

	public void setTotalContexts(ArrayList<ArrayList<String>> totalContexts) {
		this.totalContexts = totalContexts;
	}

	public ArrayList<String> getHazardous() {
		return this.ctHazardous;
	}

	public void setHazardous(ArrayList<String> hazardous) {
		this.ctHazardous = hazardous;
	}

	public String getCtController() {
		return ctController;
	}

	public void setCtController(String ctController) {
		this.ctController = ctController;
	}

	public static ArrayList<String> getOutputTypeList() {
		return outputTypeList;
	}

	public static void setOutputTypeList(ArrayList<String> outputType) {
		outputTypeList = outputType;
	}

	public static ArrayList<String> getPmTypeList() {
		return pmTypeList;
	}

	public static void setPmTypeList(ArrayList<String> pmTypeList) {
		CTDataStore.pmTypeList = pmTypeList;
	}
} 