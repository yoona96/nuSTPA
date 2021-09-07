package nuSTPA.model.step2;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Rectangle;
import nuSTPA.view.components.RectangleView;
import nuSTPA.MainApp;
import nuSTPA.model.pmv.PmvDataStore;

public class Controller {

	double x;
	double y;
	String name;
	int id;
	Map<Integer, Integer> CA = new HashMap<Integer, Integer>(); //key: CA id, value: 0->controller, 1->controlled
	Map<Integer, Integer> FB = new HashMap<Integer, Integer>(); //key: FB id, value: 1->controller, 0->controlled
	int num[] = new int[2]; //0: CA, 1: FB
	RectangleView r;
	private ObservableList<String> processModel = FXCollections.observableArrayList();
	
//	PmvDataStore pmvDB = new PmvDataStore();
	
	public Controller() {
		
	}
	
	public Controller(double x, double y, String name, Integer id, ObservableList<String> pmList) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.id = id;
		this.processModel = pmList;
	}
	
	public void setRectangle(RectangleView r) {
		this.r=r;
	}
	
	public RectangleView getRectangle() {
		return this.r;
	}
	
	public void resizeRectangle(String type) {
		if(type.equals("ca")) {
			num[0] = num[0]+1;
		}else {
			num[1] = num[1]+1;
		}
		r.resizeRectangle(num);
	}
	
	public ObservableList<String> getProcessModel() {
		return processModel;
	}

	public void setProcessModel(ObservableList<String> processModel, String controllerName) {
//		System.out.println(processModel);
		this.processModel = processModel;
		if(name.equals(controllerName)) {
			//대박. 해결했음.
			RectangleView r = new RectangleView(new SimpleDoubleProperty(x), new SimpleDoubleProperty(y), controllerName, id, MainApp.components, processModel);
		}
	}
	
	public void setProcessModels(ObservableList<String> processModels) {
		this.processModel = processModels;
	}
//	public void addPM(String name, ObservableList<String> pm) {
//		if(pmvDB.getProcessModel().get(0).getControllerName().equals(name)) {
//			for(int i = 0; i < pmvDB.getProcessModel().size(); i++) {
//				r.addPM(pm);
//			}
//		}
//	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setX(double d) {
		this.x = d;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Map<Integer, Integer> getCA(){
		return this.CA;
	}

	public void addCA(Integer id, int i) {
		this.CA.put(id, i);
		//num++;
	}
	
	public void removeCA(Integer caId) {
		this.CA.remove(caId);
		num[0] = num[0]-1;
	}
	
	public Map<Integer, Integer> getFB(){
		return this.FB;
	}

	public void addFB(Integer id, Integer type) {
		this.FB.put(id, type);
		//num++;
	}
	
	public void removeFB(Integer fbId) {
		this.FB.remove(fbId);
		num[1] = num[1]+1;
	}

	public int[] getNum() {
		return num;
	}
	
	public void clearNum() {
		this.num[0] = 0;
		this.num[1] = 0;
	}
}
