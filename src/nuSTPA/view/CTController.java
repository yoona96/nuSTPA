package nuSTPA.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import nuSTPA.Info;
import nuSTPA.MainApp;
import nuSTPA.model.XmlReader;
import nuSTPA.model.ct.CT;
import nuSTPA.model.ct.CTDataStore;
import nuSTPA.model.pmv.PmvDataStore;
import nuSTPA.model.pmv.ProcessModel;
import nuSTPA.model.step3.Step3;
import nuSTPA.model.step3.Step3DataStore;
import nuSTPA.view.popup.SelectCtOutputController;
import nuSTPA.view.popup.SelectFODController;

public class CTController {

	private MainApp mainApp;
	private File selectedMCSFile, selectedNuSCRFile;
	private PmvDataStore pmvDB;
	private PmvController pmvController;
	private XmlReader xmlReader;
	
	@FXML private AnchorPane ctPane;
	@FXML private Label filename;
	@FXML private Pane addFile;
	private TabPane tabPane = new TabPane();

	private static CTDataStore ctDataStore;
	private static ObservableList<CTDataStore> ctDataStoreList = FXCollections.observableArrayList();
	//ArrayList<ObservableList<CTM>> totalData = new ArrayList<>();
	ObservableList<CTDataStore> totalData = FXCollections.observableArrayList();
	ObservableList<CT> ctData = FXCollections.observableArrayList();
	
	private int controllerCount = 0;
	private int curTabNum = 0;
	private ObservableList<String> hazardousOX;
	private ObservableList<String> casesCombo;
	private ArrayList<String> controllerNames = new ArrayList<String>(); // Selected controller from CSE
	private ArrayList<String> controlActionNames = new ArrayList<String>();
	//private ArrayList<ArrayList<String>> outputNames = new ArrayList<>();
	private ArrayList<ObservableList<String>> processModelList = new ArrayList<>(); // pmv 단계에서 모든 process model을 가져와서 저장
	private ArrayList<String> selectedCA = new ArrayList<String>();// 선택된 control action 저장 
	private ArrayList<String> selectedOutput = new ArrayList<String>(); // 선택된 output Variables 저장 
	
	private ArrayList<ObservableList<String>> processModels = new ArrayList<>();
	
	String typeOfSelectedOutput = null;
	ArrayList<String> outputTypeList = new ArrayList<String>();
	
	public CTController() { }
	
	private void initialize(){
		addFile.setVisible(false);

		hazardousOX = FXCollections.observableArrayList();
		hazardousOX.add("X");
		hazardousOX.add("O");
		
		casesCombo = FXCollections.observableArrayList();
		casesCombo.add("Providing Causes Hazard");
		casesCombo.add("Not Providing Causes Hazard");
		casesCombo.add("Incorrect Timing/Order");
		casesCombo.add("Stopped Too Soon/Applied Too Long");
		
		this.mainApp = mainApp;
		ctDataStoreList = mainApp.ctDataStoreList;
		this.pmvDB = mainApp.pmvDB;
		totalData = ctDataStoreList;

		processModels = new ArrayList<>();
		int index = 0;
		for(ProcessModel p : pmvDB.getProcessModel()) {
			controllerNames.add(p.getControllerName());
			controlActionNames.add(p.getControlActionName());
			processModelList.add(p.getProcessModelList());
			
			index++;
		}
		
		controllerCount = controllerNames.size();
		processModels = new ArrayList<>(controllerCount);
		for(int x=0;x<controllerCount;x++) {
			ObservableList<String> temp = FXCollections.observableArrayList();
			for(int y=0;y<processModelList.get(x).size();y++) {
				temp.add(processModelList.get(x).get(y));
			}
			processModels.add(temp);
			
		}
		
		for(int i=0;i< controllerNames.size();i++) {
			tabPane.getTabs().add(makeTab(i,controllerNames.get(i),controlActionNames.get(i), processModels.get(i)));
		}
		
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        ctPane.getChildren().addAll(tabPane);
        
		tabPane.maxHeightProperty().bind(ctPane.heightProperty());
		tabPane.maxWidthProperty().bind(ctPane.widthProperty());
	}

	//set MainApp
	public void setMainApp(MainApp mainApp)  {
		this.mainApp = mainApp;
        this.initialize();
	}
	
	public Tab makeTab(int tabNum, String controllerName, String caName, ObservableList<String> processModels) {
        final TableView<CT> contextTable = this.makeTable(tabNum, processModels);
        if(totalData.size() >= tabNum+1) { 
        	ctData = totalData.get(tabNum).getCtTableList();
        	contextTable.setItems(totalData.get(tabNum).getCtTableList());
        }else {
        	CTDataStore c = new CTDataStore();
        	ctDataStoreList.add(c);
        }
//        contextTable.setPrefHeight(800.0);

		Tab tab = new Tab();
		tab.setText(controllerName+'-'+caName);
		HBox hb = new HBox();
		VBox totalhb = new VBox();
		hb.setSpacing(5);
		hb.setPadding(new Insets(3, 10, 3, 5));
		
		final Button fileButton = new Button("get MCS file");
        fileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	curTabNum = tabNum;
        		addFile.setVisible(true);
        		addFile.toFront();
            }
        });
        hb.getChildren().add(fileButton);
//        
//        ArrayList<String> pmWithVals = new ArrayList<String>();
//        for(int i = 0; i < processModels.size(); i++) {
//            if(processModels.get(i).contains(":=")) {
//            	pmWithVals.add(processModels.get(i));
//            }
//        }
//        System.out.println("these are process model with values : " + pmWithVals);
        
        final ArrayList<TextField> addContexts = new ArrayList<TextField>();
        final ArrayList<ComboBox<String>> getProcessModelVals = new ArrayList<ComboBox<String>>();
		for(int t = 0; t < processModels.size(); t++) {
			if(!processModels.get(t).contains(":")) {
		        final TextField addContext = new TextField();
		        addContext.setPromptText(processModels.get(t));
		        addContexts.add(addContext);
				hb.getChildren().add(addContext);
			}else {
				final ComboBox<String> getProcessModelVal = new ComboBox<String>();
				ObservableList<String> pmVal = FXCollections.observableArrayList();
				
				getProcessModelVal.setValue(processModels.get(t).substring(0, processModels.get(t).lastIndexOf(":")).trim()); //this is variable part
				String vals = processModels.get(t).substring(processModels.get(t).lastIndexOf("[") + 1, processModels.get(t).length() - 1).replace(" ", ""); //this is value part
				pmVal.addAll(vals.split(","));
				getProcessModelVal.setItems(pmVal);
				getProcessModelVals.add(getProcessModelVal);
				hb.getChildren().add(getProcessModelVal);
			}
		}
		
		
//		for(int i = 0; i < pmWithVals.size(); i++) {
//			final ComboBox<String> getProcessModelVal = new ComboBox<String>();
//			ObservableList<String> pmVal = FXCollections.observableArrayList();
//			
//			getProcessModelVal.setValue(pmWithVals.get(i).substring(0, pmWithVals.get(i).lastIndexOf(":")).trim()); //this is variable part
//			String vals = pmWithVals.get(i).substring(pmWithVals.get(i).lastIndexOf("[") + 1, pmWithVals.get(i).length() - 1).replace(" ", ""); //this is value part
//			pmVal.addAll(vals.split(","));
//			getProcessModelVal.setItems(pmVal);
//			getProcessModelVals[i] = getProcessModelVal;
//			hb.getChildren().addAll(getProcessModelVals[i]);
//		}

        final Button addButton = new Button("Add content");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
        		int temp = contextTable.getItems().size();
//                ArrayList<String> contexts = new ArrayList<String>();
//        		ArrayList<String> contextComboBoxVals = new ArrayList<String>();
        		ArrayList<String> totalContexts = new ArrayList<String>();
				int a = 0, b = 0;
        		for(int t = 0; t < processModels.size(); t++) {
        			if(processModels.get(t).contains(":")) {
        				if(getProcessModelVals.get(b).getValue().equals(processModels.get(t).substring(0, processModels.get(t).lastIndexOf(":")).trim())) {
        					System.out.println(getProcessModelVals.get(b).getValue());
                			totalContexts.add(getProcessModelVals.get(b).getValue());//TODO not working, cannot add selected value in table	
                			System.out.println("current total contexts for process model with value : " + getProcessModelVals.get(b).getValue());
                			b++;
        				}
        			}else {
        				if(addContexts.get(a).getText() != null) {
    	        			totalContexts.add(addContexts.get(a).getText());
    	        			addContexts.get(a).clear();
    	        			System.out.println("current total contexts for process model : " + addContexts.get(a).getText());
    	        			a++;
        				}
        			}
        		}
        		System.out.println("total contexts : " + totalContexts);
//        		for(int i = 0; i < pmWithVals.size(); i++) {
//        		}
        		ComboBox<String> casesComboBox = new ComboBox<String> (casesCombo);
        		ComboBox<String> hazardousComboBox = new ComboBox(hazardousOX);
				casesComboBox.setValue("Providing Causes Hazard");
				hazardousComboBox.setValue("X");
        		ctData = totalData.get(tabNum).getCtTableList();
        		ctData.add(new CT(controllerName, caName, casesComboBox, temp + 1, totalContexts, hazardousComboBox));
        		casesComboBox.valueProperty().addListener(new ChangeListener<String>() {
    			      @Override
      			      public void changed(ObservableValue observable, String oldValue, String newValue) {
      			    	totalData.get(tabNum).getCtTableList().get(temp).setCasesValue(newValue);
      			      }
      			    });
            		hazardousComboBox.valueProperty().addListener(new ChangeListener<String>() {
      			      @Override
      			      public void changed(ObservableValue observable, String oldValue, String newValue) {
      			    	totalData.get(tabNum).getCtTableList().get(temp).setHazardousValue(newValue);
      			      }
      			    });
                CTDataStore ctm = new CTDataStore();  
            	for(CT c : ctData) {
            		ctm.getCtTableList().add(c);
            	}
            	totalData.set(tabNum, ctm);
    			contextTable.setItems(totalData.get(tabNum).getCtTableList());
            }
        });
        hb.getChildren().add(addButton);
        
        final Button deleteButton = new Button("Delete line");
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	ctData = totalData.get(tabNum).getCtTableList();
            	if(ctData.size() != 0) {
                	ctData.remove(contextTable.getSelectionModel().getSelectedIndex());
                    CTDataStore ctm = new CTDataStore();  
                	for(CT c : ctData) {
                		ctm.getCtTableList().add(c);
                	}
                	totalData.set(tabNum, ctm);
        			contextTable.setItems(totalData.get(tabNum).getCtTableList());
            	}
            }
        });
        hb.getChildren().add(deleteButton);
                
        //TODO: NuFTA를 내부에서 사용하기 위한 버튼
        //이걸 통해서 mcs를 보길 원하는 .. pmv? ca?를 선택하고 값을 고르면 mcs가 출력되어 나오도록 해야한다.
        //이..알고리즘을 잘 파악해서 적용해야 함
        final Button nuFTAButton = new Button("Execute NuFTA");
        nuFTAButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
				try {
					if(!pmvDB.getProcessModel().get(0).getSelectedOutputs().isEmpty()) {
		            	// add & apply NuSCR file
						outputTypeList.addAll(ctDataStore.getOutputTypeList());
						//show popup to select output variable to extract MCSs
						FXMLLoader loader = new FXMLLoader();
		        		loader.setLocation(getClass().getResource("popup/SelectCtOutputView.fxml"));
		        		Parent parent;
		        		parent = loader.load();
		        		Scene scene = new Scene(parent);
		        		Stage selectOutputVarStage = new Stage();

		        		selectOutputVarStage.setTitle("Select output variable groups");
		        		selectOutputVarStage.setResizable(false);
		        		selectOutputVarStage.show();

		        		selectOutputVarStage.setScene(scene);
		        		
		        		//show FODs that are availableCtOutputController
		        		SelectCtOutputController CtOutputController = loader.getController();
		        		CtOutputController.setGroupList(pmvDB.getSelectedOutputs());
		        		
		        		selectOutputVarStage.setOnHidden(new EventHandler<WindowEvent>() {
		        			@Override
		        			public void handle(WindowEvent e) {
		        				if(CtOutputController.canceled == true) {
		        					selectOutputVarStage.close();
		        				}else if(CtOutputController.confirmed == true) {
//		    		        		for(int i = 0; i < outputTypeList.size(); i++) {
//		    		        			if(outputTypeList.get(i).contains(CtOutputController.selectedItem())) {
//		    		        				typeOfSelectedOutput = outputTypeList.get(i).replace(CtOutputController + " : ", "");
//		    		        				System.out.println("this is type of selected output : " + typeOfSelectedOutput);
//		    		        				//this is type of selected output
//		    		        				//if this is boolean, need to get MCS file when output is true/false
//		    		        				//if this is int, need to get MCS file of each int?
//		    		        			}
//		    		        		}
		        					for(int i = 0; i < outputTypeList.size() - 1; i++) {
		        						for(int j = i; j < outputTypeList.size(); j++) {
		        							if(outputTypeList.get(i).equals(outputTypeList.get(j))) {
		        								outputTypeList.remove(j);
		        							}
		        						}
		        					}
		    		        		System.out.println("this is outputTypeList : " + outputTypeList);
		        				}
		        			}
		        		});
					}else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("WARNING!");
						alert.setHeaderText("Output list is empty");
						alert.setContentText("You did not choose NuSCR in previous step");
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//            	addNuSCRFile();
//            	applyNuSCRFile();
            }
        });
        hb.getChildren().add(nuFTAButton);
        
        final Button backToPmvButton = new Button("Edit Process Model");
        backToPmvButton.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent e) {
        		mainApp.showPMVView();
        	}
        });
        hb.getChildren().add(backToPmvButton);

        final Button toStep3Button = new Button("Edit UCA Table");
        toStep3Button.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent e) {
        		mainApp.showStep3View();
        	}
        });
        hb.getChildren().add(toStep3Button);
        
        totalhb.getChildren().addAll(hb,contextTable);
        tab.setContent(totalhb);
        
        CTDataStore ctm = new CTDataStore();  
        if(totalData.size()<= tabNum) {
    		ctData = totalData.get(tabNum).getCtTableList();
        	for(CT c : ctData) {
        		ctm.getCtTableList().add(c);
        	}
        	totalData.add(ctm);
        } else {
        	ctData = FXCollections.observableArrayList();
        	for(int i = 0; i < totalData.get(tabNum).getCtTableList().size(); i++) {
        		final int temp = i;
//        		ArrayList<String> contextComboBoxs = new ArrayList<String>();
	    		ComboBox<String> casesComboBox = new ComboBox<String> (casesCombo);
	    		ComboBox<String> hazardousComboBox = new ComboBox(hazardousOX);
				casesComboBox.setValue(totalData.get(tabNum).getCtTableList().get(i).getCasesValue());
				hazardousComboBox.setValue(totalData.get(tabNum).getCtTableList().get(i).getHazardousValue());
        		ctData.add(new CT(controllerName, caName, casesComboBox, 1+temp, totalData.get(tabNum).getCtTableList().get(i).getTotalContexts(), hazardousComboBox));
	    		casesComboBox.valueProperty().addListener(new ChangeListener<String>() {
				      @Override
	  			      public void changed(ObservableValue observable, String oldValue, String newValue) {
	  			    	totalData.get(tabNum).getCtTableList().get(temp).setCasesValue(newValue);
	  			    	totalData.get(tabNum).getCases().set(temp, newValue);
	  			      }
	  			    });
	        		hazardousComboBox.valueProperty().addListener(new ChangeListener<String>() {
	  			      @Override
	  			      public void changed(ObservableValue observable, String oldValue, String newValue) {
	  			    	totalData.get(tabNum).getCtTableList().get(temp).setHazardousValue(newValue);
	  			    	totalData.get(tabNum).getHazardous().set(temp, newValue);
	  			      }
	  			    });
        	}
        	for(CT c : ctData) {
        		ctm.getCtTableList().add(c);
        	}
        	totalData.set(tabNum, ctm);
        }
    	contextTable.setItems(totalData.get(tabNum).getCtTableList());
        return tab;
	}
	
	public TableView<CT> makeTable(int tabNum, ObservableList<String> processModels) {
		TableView<CT> contextTable = new TableView<CT>();
		
		contextTable.prefWidthProperty().bind(tabPane.widthProperty());
		contextTable.prefHeightProperty().bind(tabPane.heightProperty());
//		contextTable.prefWidth(1000.0);
		contextTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<CT, String> CAColumn = new TableColumn<CT,String>("Control Action");
		TableColumn casesColumn = new TableColumn("cases");
		TableColumn<CT, Integer> noColumn = new TableColumn<CT,Integer>("No.");
		TableColumn hazardousColumn = new TableColumn("Hazardous?");

		CAColumn.setPrefWidth(100.0);
		casesColumn.setPrefWidth(100.0);
		noColumn.setPrefWidth(30.0);
		hazardousColumn.setPrefWidth(100.0);
		
		contextTable.prefWidthProperty().bind(ctPane.widthProperty());
		contextTable.prefHeightProperty().bind(ctPane.heightProperty());
		contextTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // 4. Set table row 
		CAColumn.setCellValueFactory(new PropertyValueFactory<CT, String>("controlAction"));
		casesColumn.setCellValueFactory(new PropertyValueFactory<CT, String>("cases"));
		noColumn.setCellValueFactory(new PropertyValueFactory<CT, Integer>("no"));
		hazardousColumn.setCellValueFactory(new PropertyValueFactory<CT, String>("hazardous"));

		CAColumn.setCellValueFactory(cellData -> cellData.getValue().getControlActionProperty());
		noColumn.setCellValueFactory(cellData -> cellData.getValue().getNoProperty().asObject());

		contextTable.setEditable(true);
	    
		contextTable.getColumns().addAll(CAColumn, casesColumn, noColumn);
	    
		//TODO when adding into table, array index out of bounds exception occurs
		//seems to occur when creating columns of context table
 		for(int x = 0; x < processModels.size(); x ++) {
 			TableColumn<CT, String> contextColumn = new TableColumn<>();
 			if(processModels.get(x).contains(":")) {
 				contextColumn = new TableColumn<>(processModels.get(x).substring(0, processModels.get(x).lastIndexOf(":")).trim());
 			}else {
 				contextColumn = new TableColumn<>(processModels.get(x));
 			}
 			
 			contextTable.getColumns().add(contextColumn);
// 			contextColumn.setPrefWidth(80.0);
 			contextColumn.setCellValueFactory(new PropertyValueFactory<CT, String>(processModels.get(x)));
 			int temp = x;
// 			if(ctDataStore.getC)
 			contextColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalContextProperty(temp));
 			contextColumn.setCellFactory(TextFieldTableCell.forTableColumn());
 			contextColumn.setOnEditCommit(
 	            new EventHandler<CellEditEvent<CT, String>>() {
 	                @Override
 	                public void handle(CellEditEvent<CT, String> t) {
 	                    ((CT) t.getTableView().getItems().get(
 	                        t.getTablePosition().getRow())
 	                        ).setContext(temp, t.getNewValue());
 	                    
 	                   ctData = t.getTableView().getItems();
 	 	                CTDataStore ctm = new CTDataStore();
 	 	              	for(CT c : ctData) {
 	 	              		ctm.getCtTableList().add(c);
 	 	              	}
 	 	                totalData.set(tabNum, ctm);
 	                    

 	           		/*ObservableList<UCA> ucadata = t.getTableView().getItems();
 	           		UCA uca = ucadata.get(t.getTablePosition().getRow());
 	           		uca.setUCA(t.getTableColumn().getText(), t.getNewValue(), null);
 	           		
 	                mcsData = totalData.get(tabNum).getCTMTableList();
 	                CTMDataStore ctm = new CTMDataStore();
 	              	for(CTM c : mcsData) {
 	              		ctm.getCTMTableList().add(c);
 	              	}
 	              	totalData.set(tabNum, ctm);
 	              	totalData.get(tabNum).getCTMTableList();*/
// 	      			System.out.println("table:"+(t.getTableView().getItems().get(t.getTablePosition().getRow()).getContext(temp)));
// 	      			System.out.println("total:"+totalData.get(tabNum).getCtTableList().get(t.getTablePosition().getRow()).getContext(temp));
 	                
 	                }
 	            }
 	        );
 		}
 		contextTable.getColumns().add(hazardousColumn);
 		
 		return contextTable;
	}
	
	@FXML
	public void addMCSFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Add File");
        fc.setInitialDirectory(new File(Info.directory));
        ExtensionFilter txtType = new ExtensionFilter("text file", "*.txt", "*.doc");
        fc.getExtensionFilters().addAll(txtType);
         
	    selectedMCSFile =  fc.showOpenDialog(null);
        if(selectedMCSFile != null) {
	        filename.setText(selectedMCSFile.getName());
        }
    }
	
	@FXML
	public int applyMCSFile() throws IOException {
		
		if(selectedMCSFile != null) {
			addFile.setVisible(false);
	
	        try {
	            FileInputStream fis = new FileInputStream(selectedMCSFile);
	            
	            byte [] buffer = new byte[fis.available()];
	            String mcsData="";
	            while((fis.read(buffer)) != -1) {
	            	mcsData = new String(buffer);
	            }    
	            fis.close();
	           
	            String[] splitMcsData = new String[1000];
	            splitMcsData = mcsData.split("\n");
	            this.ParseMCS(splitMcsData);
	            
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
		}
		return 0;
	}
	
//	public void addNuSCRFile() {
//		//get selected xml(NuSCR) file from pmvController class(to get same file without reopening it)
//		FileChooser fc = new FileChooser();
//		fc.setTitle("Add NuSCR File");
//		fc.setInitialDirectory(new File(Info.directory));
//		ExtensionFilter fileType = new ExtensionFilter("xml file", "*.xml");
////		
////		for(tabPane.getSelectionModel().getSelectedItem() != null) {
////			
////		}
//		
//		File selectedFile = fc.showOpenDialog(null);
////		if(selectedFile != null) {
////			if(selectedFile == pmvController.getSelectedFile(0)) {
////				
////			}
////		}
//	}
//	
	//put mcs values in process model variables
	private void ParseMCS(String[] splitMcsData) {
		String[][] context = new String[processModels.get(curTabNum).size()][splitMcsData.length];
		StringBuilder abstractedPM = new StringBuilder();
		ArrayList<String> abstractedPMs = new ArrayList<String>();
		
		int i=0;
		while(i < splitMcsData.length) {
			String[] splits = splitMcsData[i].split("&");
			int j = 0;
			int currentPM = -1;
			
			while(j < splits.length) {
				int equalIndex = splits[j].lastIndexOf("=");
				
				if(pmvDB.getAbstractedList() != null) {
					for(ArrayList<String> abstractedPMList : pmvDB.getAbstractedList()) {
						for(int k = 1; k < abstractedPMList.size(); k++) {
							if(splits[j].contains(abstractedPMList.get(k))) {
								if(splits[j].contains("!=")) {
									if(splits[j].substring(equalIndex).trim().equals("false")) {
										abstractedPM.append("TRUE&");
									}else {
										abstractedPM.append("FALSE&");
									}
								}else if(splits[j].contains("==")) {
									abstractedPM.append(splits[j].substring(1).trim() + "&");
								}else if(splits[j].substring(0,1).contains("=")) {
									if(splits[j].substring(equalIndex).trim().equals("false")) {
										abstractedPM.append("FALSE&");
									}else {
										abstractedPM.append("TRUE&");
									}
								}else if(splits[j].contains("<=")) {
									splits[j].replace(abstractedPMList.get(k), "x").replace("(A)", "");
									abstractedPM.append(splits[j] + "&");
								}else {
									abstractedPM.append(splits[j].substring(equalIndex+1));
								}
							}
						}
						abstractedPMs.addAll(abstractedPMList);
					}
				}
				
				
				//only get values 
				if(equalIndex >= 0) {
					for(int t=0;t<processModels.get(curTabNum).size();t++) {
						if(splits[j].contains(processModels.get(curTabNum).get(t))) {
							if(context[t][i] == null) {
								context[t][i] = splits[j].substring(equalIndex); //equalIndex = last index of '=' in splits[j], context[t][i] = substring of splits starting from '='
								if(splits[j].contains("!=")) {
									if(splits[j].contains("false")) context[t][i] = "TRUE";
									else if(splits[j].contains("true")) context[t][i] = "FALSE";
								}else if(context[t][i].substring(0,1).contains("=") && context[t][i].contains("true") || context[t][i].contains("false")) {
									if(context[t][i].contains("true")) context[t][i] = "TRUE";
									else context[t][i] = "FALSE";
								}else if(context[t][i].substring(0,2).contains("==")) {
									context[t][i].replace("==", "").trim();
								}else if(splits[j].contains("<=")){
									context[t][i] = splits[j].replace(processModels.get(curTabNum).get(t), "x").replace("(A)", "");
								}
							} else if(!splits[j].contains("true") && !splits[j].contains("false")) {
								context[t][i] += (" & " + splits[j].substring(equalIndex+1));
							}
							currentPM = t;
							break;
						}
					}
				} else if(equalIndex < 0 && currentPM >= 0) { //when currentPM = t && equal index does not exist
					if(context[currentPM][i]==null || context[currentPM][i].contains("true") || context[currentPM][i].contains("false")) {
						if(splits[j].length()!=1) {
							context[currentPM][i]=splits[j];
						}
					}else if(!context[currentPM][i].isEmpty() && !context[currentPM][i].contains("<=")){
						context[currentPM][i] += (" &\n" +splits[j]);
					}
					currentPM = -1;
				}		
				j++;
			}
			i++;
		}
		
		for(int x = 0; x < processModels.get(curTabNum).size(); x++) {
			for(int y=0;y<splitMcsData.length;y++) {
				if(context[x][y]==null) {
					context[x][y] = "N/A";
				}
			}
		}
		
		int curtableSize = totalData.get(curTabNum).getCtTableList().size();
		for(int y=0;y < splitMcsData.length;y++) {
	        ArrayList<String> totalContexts = new ArrayList<String>();
			if(!abstractedPMs.isEmpty()) {
				for(int a = 0; a < pmvDB.getAbstractedList().size(); a++) {
					if(splitMcsData[y].equals(pmvDB.getAbstractedList().get(a).get(0))) {
						totalContexts.add(y, abstractedPMs.get(a));
					}else {
						for(int x = 0; x < processModels.get(curTabNum).size(); x++) {
							totalContexts.add(x, context[x][y]);
						}
					}
				}
			}else {
				for(int x = 0; x < processModels.get(curTabNum).size(); x++) {
					totalContexts.add(x, context[x][y]);
				}
			}
			
			final int a=y;
//			ArrayList<String> contextComboBoxs = new ArrayList<String>();
	   		ComboBox<String> casesComboBox = new ComboBox(casesCombo);
	   		ComboBox<String> hazardousComboBox = new ComboBox(hazardousOX);
			casesComboBox.setValue("Providing Causes Hazard");
			hazardousComboBox.setValue("X");
			totalData.get(curTabNum).getCtTableList().add(
					new CT(controllerNames.get(curTabNum), controlActionNames.get(curTabNum), casesComboBox, curtableSize+a+1, totalContexts, hazardousComboBox)
			);
			ctData = totalData.get(curTabNum).getCtTableList();
    		casesComboBox.valueProperty().addListener(new ChangeListener<String>() {
			      @Override
			      public void changed(ObservableValue observable, String oldValue, String newValue) {
			    	totalData.get(curTabNum).getCtTableList().get(curtableSize+a).setCasesValue(newValue);
			      	}
			    });
    		hazardousComboBox.valueProperty().addListener(new ChangeListener<String>() {
			      @Override
			      public void changed(ObservableValue observable, String oldValue, String newValue) {
			    	totalData.get(curTabNum).getCtTableList().get(curtableSize+a).setHazardousValue(newValue);
			      }
			    });
		}
	}
	
	@FXML
	public void closeAddFile(ActionEvent actionEvent) {
		addFile.setVisible(false);
	}
	
}
