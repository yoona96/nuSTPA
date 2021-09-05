package nuSTPA;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nuSTPA.model.ProjectXML;
import nuSTPA.model.ct.CT;
import nuSTPA.model.ct.CTDataStore;
import nuSTPA.model.pmv.PmvDataStore;
import nuSTPA.model.pmv.ProcessModel;
import nuSTPA.model.step1.Step1;
import nuSTPA.model.step1.Step1DataStore;
import nuSTPA.model.step2.Components;
import nuSTPA.model.step3.Step3;
import nuSTPA.model.step3.Step3DataStore;
import nuSTPA.model.step4.Step4DataStore;
import nuSTPA.view.Step2Controller;
import nuSTPA.view.CTController;
import nuSTPA.view.HomeViewController;
import nuSTPA.view.Step1Controller;
import nuSTPA.view.Step4Controller;
import nuSTPA.view.PmvController;
import nuSTPA.view.RootLayoutController;
import nuSTPA.view.Step3Controller;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private RootLayoutController rootLayoutController;
	public static Components components;
	public static Step1DataStore lhcDataStore;
	public static ObservableList<Step3DataStore> ucaDataStoreList = FXCollections.observableArrayList();
	public static ObservableList<Step3> ucadatastore = FXCollections.observableArrayList();
	public static ObservableList<CTDataStore> ctDataStoreList = FXCollections.observableArrayList();
	public static ObservableList<CT> ctmDataStore = FXCollections.observableArrayList();
	public static Step4DataStore lsDataStore;
	public static PmvDataStore pmmDB;
	 
	 
	@Override
	/**
	 * automatically executed after main function is executed
	 */
	public void start(Stage primaryStage) {
	 	this.primaryStage = primaryStage;
	    this.primaryStage.setTitle("NuSTPA");
	    this.primaryStage.setResizable(true);

	    initRootLayout();
	    initDataStore();
	}

	 /**
     * init dataStore
     */
	private void initDataStore() {
		components = new Components();
		lhcDataStore = new Step1DataStore();
		lsDataStore = new Step4DataStore();
		pmmDB = new PmvDataStore();
	}
	
	/**
	 * Init root layout
	 */

	private void initRootLayout() {
		try {
            // get root layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            //add root layout to scene
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            //add controller
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * called when Step 1 button clicked
	 */
	public void showStep1View() {
        try {
            // get table scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Step1View.fxml"));
            AnchorPane View = (AnchorPane) loader.load();

            // add scene in center of root layout
            rootLayout.setCenter(View);

            //add controller
            Step1Controller controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
     * called when Step 2 Button clicked
     */
    public void showStep2View() {
        try {
        	//Open when LHC data isn't null
//        	if(lhcDataStore.getLossTableList().isEmpty() || lhcDataStore.getHazardTableList().isEmpty() || lhcDataStore.getConstraintTableList().isEmpty()){
//    	        Alert alert = new Alert(AlertType.INFORMATION);
//        		alert.setTitle("Caution");
//        		alert.setHeaderText("Condition not satisfied");
//    	        alert.setContentText("Please fullfill previous steps");
//    	        alert.show();
//        		return;
//        	}
       		// get maker scene
       		FXMLLoader loader = new FXMLLoader();
       		loader.setLocation(MainApp.class.getResource("view/Step2View.fxml"));
       		AnchorPane View = (AnchorPane) loader.load();

       		// add scene in center of root layout
        	rootLayout.setCenter(View);

        	//add controller
        	Step2Controller controller = loader.getController();
        	controller.setMainApp(this, primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * called when CT Button clicked
     */
    public void showCTView() {
        try {
        	//Open when pmm data isn't null
//        	if(pmmDB.getProcessModel().isEmpty()){
//    	        Alert alert = new Alert(AlertType.INFORMATION);
//        		alert.setTitle("Caution");
//        		alert.setHeaderText("Conditions not satisfied");
//    	        alert.setContentText("Please fullfill previous steps");
//    	        alert.show();
//        		return;
//        	}
            // get maker scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/CTView.fxml"));
            AnchorPane View = (AnchorPane) loader.load();

            // add scene in center of root layout
            rootLayout.setCenter(View);

            //add controller
            CTController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * called when Step 3 Button clicked
     */
    public void showStep3View() {
        try {
        	//Open when CTM data isn't null
//        	if(ctmDataStoreList.isEmpty()){
//    	        Alert alert = new Alert(AlertType.INFORMATION);
//        		alert.setTitle("Caution");
//        		alert.setHeaderText("Conditions not satisfied");
//    	        alert.setContentText("Please fullfill previous steps");
//    	        alert.show();
//        		return;
//        	}

            // get maker scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Step3View.fxml"));
            AnchorPane View = (AnchorPane) loader.load();

            rootLayout.setCenter(View);

            //add controller
            Step3Controller controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();

        }

        try{
            Parent parent = FXMLLoader.load(getClass().getResource("view/popup/Step3HazardPopUpView.fxml"));
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setTitle("Hazard List");
			stage.setResizable(false);
			stage.show();
			stage.setScene(scene);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * called when PMV Button clicked
     */
    public void showPMVView() {
        try {
        	//Open when CSE data isn't null
//        	if(components.getControlActions().isEmpty() || components.getControlActions().isEmpty()){
//    	        Alert alert = new Alert(AlertType.INFORMATION);
//        		alert.setTitle("Caution");
//        		alert.setHeaderText("Conditions not satisfied");
//    	        alert.setContentText("Please fullfill previous steps");
//    	        alert.show();
//        		return;
//        	}

            // get maker scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PmvView.fxml"));
            AnchorPane View = (AnchorPane) loader.load();

            // add scene in center of root layout
            rootLayout.setCenter(View);

            //add controller
            PmvController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * called when 'Show process overview' button clicked
     */
	public void showHomeView() {
        try {
            // get maker scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/HomeView.fxml"));
            AnchorPane View = (AnchorPane) loader.load();

            // add scene in center of root layout
            rootLayout.setCenter(View);

            //add controller
            HomeViewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * called when Step 4 button clicked
	 */
	public void showStep4View() {
		try {
			//Open when UCA data isn't null
//        	if(ucaDataStoreList.isEmpty()){
//    	        Alert alert = new Alert(AlertType.INFORMATION);
//        		alert.setTitle("Caution");
//        		alert.setHeaderText("Conditions not satisfied");
//    	        alert.setContentText("Please fullfill previous steps");
//    	        alert.show();
//        		return;
//        	}
            // get maker scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Step4View.fxml"));
            AnchorPane View = (AnchorPane) loader.load();

            // add scene in center of root layout
            rootLayout.setCenter(View);
            
            System.out.println("ls");
            //add controller
            Step4Controller controller = loader.getController();
            controller.setMainApp(this);


        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    /**
	 * return main stage
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public File getFilePath() {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String filePath = prefs.get("filePath", null);
	    if (filePath != null) {
	    	//System.out.println("filePath: " + filePath);
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}

	public void setFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    if (file != null) {
	        prefs.put("filePath", file.getPath());
	    } else {
	        prefs.remove("filePath");
	    }
	}

	public void openFile(File file) {
		 try {
		        JAXBContext context = JAXBContext
		                .newInstance(ProjectXML.class);
		        Unmarshaller um = context.createUnmarshaller();

		        ProjectXML projectXML = (ProjectXML) um.unmarshal(file);

		     // --------------------------- LHC --------------------------
		        lhcDataStore.getLossTableList().addAll(projectXML.getLossList());
		        lhcDataStore.getHazardTableList().addAll(projectXML.getHazardList());
		        lhcDataStore.getConstraintTableList().addAll(projectXML.getConstraintList());
			 // --------------------------- LHC --------------------------

		     // --------------------------- CSE --------------------------
		        components.getControllers().addAll(projectXML.getControllers());
		        components.getControlActions().addAll(projectXML.getControlActions());
		        components.getFeedbacks().addAll(projectXML.getFeedbacks());
		     // --------------------------- CSE --------------------------

		     // --------------------------- UTM --------------------------
		        ucadatastore.remove(0, ucadatastore.size()-1);
		        ucaDataStoreList.remove(0, ucaDataStoreList.size()-1);

		        ucadatastore.addAll(projectXML.getUCA());
		        ucaDataStoreList.addAll(projectXML.getUCADataStoreList());
		        int i=0;
		        for(Step3DataStore udb : ucaDataStoreList){
		        	int size = udb.size;
		        	for(int j=0;j<size;j++){
		        		udb.getUCATableList().add(ucadatastore.get(i));
		        		i++;
		        	}
		        }
		      //--------------------------- UTM --------------------------

			 // --------------------------- PMM --------------------------
		        pmmDB.setProcessModel(projectXML.getProcessModel());
		        pmmDB.setInputList(projectXML.getInputList());
		        pmmDB.setOutputList(projectXML.getOutputList());
			 // --------------------------- PMM --------------------------

			 // --------------------------- CTM --------------------------
		        ctmDataStore.remove(0, ctmDataStore.size()-1);
		        ctDataStoreList.remove(0, ctDataStoreList.size()-1);
		        ctDataStoreList.addAll(projectXML.getCtmDataStoreList());
   	         // --------------------------- CTM --------------------------

		     // --------------------------- LS ---------------------------
		        lsDataStore.getLsList().addAll(projectXML.getLsList());
		     // --------------------------- LS ---------------------------

		        setFilePath(file);

		     } catch (Exception e) {
		        Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Error");
		        alert.setHeaderText("Could not load data");
		        alert.setContentText("Could not load data from file:\n" + file.getPath());

		        alert.showAndWait();
		    }
	}

	public void saveFile(File file) {
		 try {
			 JAXBContext context = JAXBContext
	                .newInstance(ProjectXML.class);

			 Marshaller m = context.createMarshaller();
			 m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			 ProjectXML projectXML = new ProjectXML();

	     // --------------------------- LHC --------------------------
	        projectXML.setLossList(lhcDataStore.getLossTableList());
	        projectXML.setHazardList(lhcDataStore.getHazardTableList());
	        projectXML.setConstraintList(lhcDataStore.getConstraintTableList());
	     // --------------------------- LHC --------------------------

	     // --------------------------- CSE --------------------------
	        projectXML.setControllers(components.getControllers());
	        projectXML.setControlActions(components.getControlActions());
	        projectXML.setFeedbacks(components.getFeedbacks());
	     // --------------------------- CSE --------------------------


	     // --------------------------- UTM --------------------------
	        ucadatastore.clear();
	        for(Step3DataStore u :ucaDataStoreList ){
	        	u.settingSize();
	        	ucadatastore.addAll(u.getUCATableList());
	        }
	        projectXML.setUCAList(ucaDataStoreList);
	        projectXML.setUCA(ucadatastore);
	     // --------------------------- UTM --------------------------

		 // --------------------------- PMM --------------------------
	        projectXML.setProcessModel(pmmDB.getProcessModel());
	        projectXML.setOutputList(pmmDB.getOutputList());
	        projectXML.setIntputList(pmmDB.getInputList());
    	 // --------------------------- PMM --------------------------

		 // --------------------------- CTM --------------------------
	        projectXML.setCTM(ctmDataStore);
	        projectXML.setCTMList(ctDataStoreList);
	     // --------------------------- CTM --------------------------

	     // --------------------------- LS ---------------------------
	        projectXML.setLsList(lsDataStore.getLsList());
	     // --------------------------- LS ---------------------------

	        m.marshal(projectXML, file);
	        setFilePath(file);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not save data");
	        alert.setContentText("Could not save data to file:\n" + file.getPath());

	        alert.showAndWait();
	    }
	}
	
	public static void main(String[] args) {
		 launch(args);
	 }
}


