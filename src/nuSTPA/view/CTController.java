package nuSTPA.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		filename.setText("");
        FileChooser fc = new FileChooser();
        fc.setTitle("Add File");
        fc.setInitialDirectory(new File(Info.directory));
        ExtensionFilter fileType = new ExtensionFilter("text file", "*.txt");
        fc.getExtensionFilters().addAll(fileType);
         
	    selectedMCSFile =  fc.showOpenDialog(null);
        if(selectedMCSFile != null) {
	        filename.setText(selectedMCSFile.getName());
        }
    }
	
	@FXML
	public int applyMCSFile() throws IOException {
		
		if(selectedMCSFile != null) {
			addFile.setVisible(false);

			BufferedReader br = new BufferedReader(new FileReader(selectedMCSFile));
			
			String m = null;
			ArrayList<String> eachRow = new ArrayList<String>();
			int num = 1;
			while((m = br.readLine()) != null) {
				eachRow.add(m);
//				if(selectedMCSFile.toString().contains("true")) {
//					eachRow.add("TRUE");
//				}else if(selectedMCSFile.toString().contains("false")){
//					eachRow.add("FALSE");
//				}
			}
//	        try {
//	            FileInputStream fis = new FileInputStream(selectedMCSFile);
//	            
//	            byte [] buffer = new byte[fis.available()];
//	            String mcsData="";
//	            while((fis.read(buffer)) != -1) {
//	            	mcsData = new String(buffer);
//	            }    
//	            fis.close();
//	            String[] splitMcsData = new String[1000];
//	            splitMcsData = mcsData.split("\n");
	            this.ParseMCS(eachRow);
//	            
//	        } catch (FileNotFoundException e) {
//	            e.printStackTrace();
//	        }
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
	private void ParseMCS(ArrayList<String> eachRow) {
		ArrayList<ArrayList<String>> totalContext = new ArrayList<ArrayList<String>>(); // eachRow * splitByAnd
		String[][] context = new String[processModels.get(curTabNum).size()][eachRow.size()];
		StringBuilder abstractedPM = new StringBuilder();
		ArrayList<String> abstractedPMs = new ArrayList<String>();
		boolean repeat1 = true, repeat2 = true, repeat3 = true, repeat4 = true;
		
		for(int i = 0; i < eachRow.size(); i++) {
			ArrayList<String> tempList= new ArrayList<String>();
			for(int j = 0; j < processModels.get(curTabNum).size(); j++) {
				tempList.add("N/A");
			}
			totalContext.add(tempList);
		}
		
		String state = "";
		
		int i = 0;
		while(i < eachRow.size()) {
			//i is row number
			String[] splitByAnd = eachRow.get(i).split("[&]");
			String[] splitByIneq = {}, splitByEq = {}, splitByRange = {}, splitByEqEq = {};
			for(int j = 0; j < splitByAnd.length; j++) {
				splitByAnd[j] = splitByAnd[j].replaceAll("[(][A-Z][)]", "").replaceAll("[0-9]{1,3}[.]\\s", "").replaceAll(" = ", "=").replaceAll(" == ", "==").trim();
				//for every splitted variables&values
				if(splitByAnd[j].matches(".+[!][=].?(true|false)")) {
					//if form of data is var != val, take only val
					 splitByIneq = splitByAnd[j].split("[!][=]");
				}else if(splitByAnd[j].matches(".+[=](true|false)")) {
					//if form of data is var = val, take only val
					splitByEq = splitByAnd[j].split("[=]");
				}else if(splitByAnd[j].matches("[0-9]+[<=].+[<=][0-9]+")) {
					//if form of data is val<=var<=val&change var into x
					splitByRange = splitByAnd[j].split("[<][=]");
				}else if(splitByAnd[j].matches("(Trip|Waiting|Normal).+")) {
					//form of data is Trip/Waiting/Normal at/for x
					if(!eachRow.contains(splitByAnd[j])) {
						state = splitByAnd[j];
					}
					if(state != "" && !state.contains(splitByAnd[j])) {
						state += " & " + splitByAnd[j];
					}
				}else if(splitByAnd[j].matches(".+[=]{2,2}.+")) {
					//form of data is x_state==Waiting
					splitByEqEq = splitByAnd[j].split("[=]{2,2}");
				}
				for(String s : pmvDB.getProcessModel().get(curTabNum).getProcessModelList()) {
					if(splitByIneq.length > 0) {
						if(splitByIneq[0].equals(s)) {
							//form of data x!=true
							if(splitByIneq[1].equals("true")) {
								totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), "FALSE");
							}
							else
								totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), "TRUE");
						}else if(splitByIneq[0].equals(s + "_t0")) {
							//form of data x_t0=false
							String tmp = totalContext.get(i).get(processModels.get(curTabNum).indexOf(s)+1);
							if(tmp != "N/A" && !tmp.contains("t0")) {
								if(splitByIneq[1].equals("true"))
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), tmp + "&t0=" + "FALSE");
								else
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), tmp + "&t0=" + "TRUE");
							}else {
								if(splitByIneq[1].equals("true"))
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), "t0=" + "FALSE");
								else
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), "t0=" + "TRUE");
							}
							
						}
					}
					if(splitByEq.length > 0) {
						if(splitByEq[0].equals(s)) {
							//form of data x=true
							if(splitByEq[1].equals("true"))
								totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), "TRUE");
							else
								totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), "FALSE");
						}else if(splitByEq[0].equals(s + "_t0")) {
							//form of data x_t0=false
							String tmp = totalContext.get(i).get(processModels.get(curTabNum).indexOf(s)+1);
							if(!tmp.contains(splitByEq[1]) && tmp != "N/A" && !tmp.contains("t0")) {
								if(splitByEq[1].equals("true"))
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s) + 1, tmp + "\n&t0 = TRUE");
								else
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s) + 1, tmp + "\n&t0 = FALSE");
							}else {
								if(splitByEq[1].equals("true"))
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s) + 1, "t0 = TRUE");
								else
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s) + 1, "t0 = FALSE");
							}
						}
					}
					if(splitByRange.length > 0) {
						if(splitByRange[1].equals(s)) {
							if(eachRow.get(processModels.get(curTabNum).indexOf(s)).equals("N/A")) {
								totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), splitByRange[0] + "<=x<=" + splitByRange[2]);
							}else if(repeat1 == true || repeat3 == true){
								String temp = totalContext.get(i).get(processModels.get(curTabNum).indexOf(s));
								if(!temp.contains(splitByRange[0] + "<=x<=" + splitByRange[2]) && temp != "N/A") {
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), temp + "\n&" + splitByRange[0] + "<=x<=" + splitByRange[2]);
									if(repeat1 == false && repeat3 == true) {
										repeat3 = false;
									}
									repeat1 = false;
								}else {
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), splitByRange[0] + "<=x<=" + splitByRange[2]);
									if(repeat1 == false && repeat3 == true) {
										repeat3 = false;
									}
									repeat1 = false;
								}
							}
							//TODO need to fix here
						}else if(splitByRange[1].contains(s) && splitByRange[1].contains("t0")) {
							if(eachRow.get(processModels.get(curTabNum).indexOf(s)).equals("N/A")) {
								totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), splitByRange[0] + "<=x_t0<=" + splitByRange[2]);
							}else if(repeat2 == true || repeat4 == true){
								String temp = totalContext.get(i).get(processModels.get(curTabNum).indexOf(s));
								System.out.println(temp);
								if(!temp.contains(splitByRange[0] + "<=x_t0<=" + splitByRange[2]) && temp != "N/A") {
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), temp + "\n&" +splitByRange[0] + "<=x_t0<=" + splitByRange[2]);
									if(repeat2 == false && repeat4 == true) {
										repeat4 = false;
									}
									repeat2 = false;
								}else {
									totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), splitByRange[0] + "<=x_t0<=" + splitByRange[2]);
									if(repeat2 == false && repeat4 == true) {
										repeat4 = false;
									}
									repeat2 = false;
								}
							}
						}
					}
					if(splitByEqEq.length > 0) {
						if(splitByEqEq[0].matches(s)) {
							String temp = totalContext.get(i).get(processModels.get(curTabNum).indexOf(s));
							if(!temp.contains(splitByEqEq[1]) && temp != "N/A" && !temp.contains(splitByEqEq[1]))
								totalContext.get(i).add(processModels.get(curTabNum).indexOf(s), temp + "\n& " + splitByEqEq[1] + "\n&" + state);
							else
								totalContext.get(i).set(processModels.get(curTabNum).indexOf(s), splitByEqEq[1] + "\n&" + state);
						}
					}
//					System.out.println(totalContext.get(i));
				}
			}
			i++;
		}
		
		int curtableSize = totalData.get(curTabNum).getCtTableList().size();
		for(int y = 0; y < eachRow.size(); y++) {
//	        ArrayList<String> totalContexts = new ArrayList<String>();
//			if(!abstractedPMs.isEmpty()) {
//				for(int a = 0; a < pmvDB.getAbstractedList().size(); a++) {
//					if(eachRow.get(y).equals(pmvDB.getAbstractedList().get(a).get(0))) {
//						eachRow.add(y, abstractedPMs.get(a));
//					}else {
//						for(int x = 0; x < processModels.get(curTabNum).size(); x++) {
//							eachRow.add(x, totalContext.get(x).get(y));
//						}
//					}
//				}
//			}else {
//				for(int x = 0; x < processModels.get(curTabNum).size(); x++) {
//					totalContext.get(y).add(x, totalContext.get(y).get(x));
//				}
//			}
			
			final int a=y;
//			ArrayList<String> contextComboBoxs = new ArrayList<String>();
	   		ComboBox<String> casesComboBox = new ComboBox(casesCombo);
	   		ComboBox<String> hazardousComboBox = new ComboBox(hazardousOX);
			casesComboBox.setValue("Providing Causes Hazard");
			hazardousComboBox.setValue("X");
			totalData.get(curTabNum).getCtTableList().add(
					new CT(controllerNames.get(curTabNum), controlActionNames.get(curTabNum), casesComboBox, curtableSize + y + 1, totalContext.get(y), hazardousComboBox)
			);
			ctData = totalData.get(curTabNum).getCtTableList();
    		casesComboBox.valueProperty().addListener(new ChangeListener<String>() {
			      @Override
			      public void changed(ObservableValue observable, String oldValue, String newValue) {
			    	totalData.get(curTabNum).getCtTableList().get(curtableSize + a).setCasesValue(newValue);
			      	}
			    });
    		hazardousComboBox.valueProperty().addListener(new ChangeListener<String>() {
			      @Override
			      public void changed(ObservableValue observable, String oldValue, String newValue) {
			    	totalData.get(curTabNum).getCtTableList().get(curtableSize + a).setHazardousValue(newValue);
			      }
			    });
		}
	}
	
	@FXML
	public void closeAddFile(ActionEvent actionEvent) {
		addFile.setVisible(false);
	}
	
}
