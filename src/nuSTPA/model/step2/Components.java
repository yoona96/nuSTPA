package nuSTPA.model.step2;

import java.util.ArrayList;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import nuSTPA.MainApp;
import nuSTPA.model.pmv.PmvDataStore;
import nuSTPA.view.components.RectangleView;

public class Components {

	private ObservableList<Controller> controllers = FXCollections.observableArrayList();
	private ObservableList<ControlAction> controlActions = FXCollections.observableArrayList();
	private ObservableList<Feedback> feedbacks = FXCollections.observableArrayList();
	private ObservableList<Text> texts = FXCollections.observableArrayList();
	private ObservableList<ListView<String>> processModels = FXCollections.observableArrayList();
	
	public int curId=1;
	public ControlAction curCA;
	public Feedback curFB;
	public Controller curController;
	private PmvDataStore pmvDB;
	
	public Components() {
		ObservableList<String> temp = FXCollections.observableArrayList();
		//===================temp========================
		controllers.add(new Controller(500, 5, "Operator", 1, temp));
		controllers.add(new Controller(300, 400, "Core Protection Calculator", 2, temp));
		controllers.add(new Controller(800, 300, "RPS", 3, temp));
		controllers.add(new Controller(50, 750, "ENFMS", 4, temp));
		controllers.add(new Controller(400, 750, "CEDMCS", 5, temp));
		controllers.add(new Controller(600, 1000, "Reactor", 6, temp));
		
		ArrayList<String> operatorRpsCA = new ArrayList<String>();
		ArrayList<String> operatorCedmcsCA = new ArrayList<String>();
		ArrayList<String> OperatorCpcFB = new ArrayList<String>();
		ArrayList<String> OperatorRpsFB = new ArrayList<String>();
		ArrayList<String> RpsCedmcsCA = new ArrayList<String>();
		ArrayList<String> CpcEnfmsFB = new ArrayList<String>();
		ArrayList<String> RpsEnfmsFB = new ArrayList<String>();
		ArrayList<String> CpcReactorFB = new ArrayList<String>();
		ArrayList<String> RpsReactorFB = new ArrayList<String>();
		ArrayList<String> CedmcsReactorCA = new ArrayList<String>();
		ArrayList<String> EnfmsReactorFB = new ArrayList<String>();

		operatorRpsCA.add("Bypass");
		operatorCedmcsCA.add("Manual trip");
		OperatorCpcFB.add("Control rod location");
		OperatorRpsFB.add("Information");
		RpsCedmcsCA.add("Trip signal");
		CpcEnfmsFB.add("Variable over\npower value");
		RpsEnfmsFB.add("Variable over\npower value");
		RpsEnfmsFB.add("\nHigh Logarithmic\nPower Level");
		CpcReactorFB.add("Reactor\npower value");
		RpsReactorFB.add("Pressurizer info.");
		RpsReactorFB.add("\nSteam generator info.");
		RpsReactorFB.add("\nReactor coolant info.");
		CedmcsReactorCA.add("Control rod\nposition control");
		
		controlActions.add(new ControlAction("Operator", "RPS", operatorRpsCA, 7, this));
		controlActions.add(new ControlAction("Operator", "CEDMCS", operatorCedmcsCA, 8, this));
		controlActions.add(new ControlAction("RPS", "CEDMCS", RpsCedmcsCA, 9, this));
		controlActions.add(new ControlAction("CEDMCS", "Reactor", CedmcsReactorCA, 10, this));
		
		feedbacks.add(new Feedback("Core Protection Calculator", "Operator", OperatorCpcFB, 11, this));
		feedbacks.add(new Feedback("RPS", "Operator", OperatorRpsFB, 12, this));
		feedbacks.add(new Feedback("ENFMS", "Core Protection Calculator", CpcEnfmsFB, 13, this));
		feedbacks.add(new Feedback("ENFMS", "RPS", RpsEnfmsFB, 14, this));
		feedbacks.add(new Feedback("Reactor", "Core Protection Calculator", CpcReactorFB, 15, this));
		feedbacks.add(new Feedback("Reactor", "RPS", RpsReactorFB, 16, this));
		feedbacks.add(new Feedback("Reactor", "ENFMS", EnfmsReactorFB, 17, this));
		
		//===================temp========================
	}
	
	public ObservableList<Controller> getControllers() {
		return this.controllers;
	}
	
	public ObservableList<ListView<String>> getProcessModels() {
		return processModels;
	}

	public void setProcessModels(ObservableList<ListView<String>> processModels) {
		this.processModels = processModels;
	}
	
	public void setProcessModel(ObservableList<String> pmList, String name) {
//		System.out.println("setting process model list...");
		int index = 0;
		for(Controller c : controllers) {
			if(c.getName().equals(name)) {
//				System.out.println("this is selected controller : " + c.getName());
				index = controllers.indexOf(c);

				DoubleProperty X = new SimpleDoubleProperty(c.getX());
			    DoubleProperty Y = new SimpleDoubleProperty(c.getY());
			    c.clearNum();
			    
				RectangleView r = new RectangleView(X, Y, c.getName(), c.getId(), MainApp.components, pmList);
				c.setRectangle(r);
				
//				System.out.println("index of selected controller : " + index);
			}
		}

		for(int i = 0; i < controllers.size(); i++) {
			if(controllers.get(i).getName().equals(name)) {
				ListView<String> lv = new ListView<String>();
				lv.setItems(pmList);
				processModels.add(i, lv);
			}else {
				ListView<String> lv = new ListView<String>();
				lv.getItems().add("");
				processModels.add(i, lv);
			}
			controllers.get(i).setProcessModel(pmList, name);
//			lv.getItems().clear();
		}
//		for(ListView<String> lv : processModels) {
//			if(processModels.indexOf(lv) == index) {
//				this.processModels.get(index).setItems(pmList);
//			}
//		}
	}

//	public void addPMToCurController(String name, ObservableList<String> pm) {
//		for(Controller c : this.controllers) {
//			if(c.getName().equals(name)) {
//				this.controllers.get(controllers.indexOf(c)).setProcessModel(pm);
//			}
//		}
//	}
//	
	public ObservableList<ControlAction> getControlActions() {
		return this.controlActions;
	}
	
	public ObservableList<Feedback> getFeedbacks() {
		return this.feedbacks;
	}
	
	public ObservableList<Text> getTexts() {
		return this.texts;
	}
	
	public int getCurId() {
		return this.curId;
	}
	
	public void setCurId(int id) {
		this.curId = id;
	}
	
	public void setTexts(ObservableList<Text> texts) {
		this.texts = texts;
	}
	
	
	public void addController(Controller controller) {
		controllers.add(controller);
		curId++;
	}
	
	public void addControlAction(ControlAction ca) {
		controlActions.add(ca);
		curId++;
	}
	
	public void addFeedback(Feedback fb) {
		feedbacks.add(fb);
		curId++;
	}
	
	public void addText(Text t) {
		texts.add(t);
		curId++;
	}
	
	public void moveController(Integer id, double d, double e) {
		if(controllers != null) {
			for (Controller c : controllers) {
	            if (c.getId()==id) {
	                c.setX(d);
	                c.setY(e);
	                return;
	            }
	        }
		}
	}
	
	public void moveText(Integer id, double d, double e) {
		if(texts != null) {
			for (Text t : texts) {
	            if (t.getId()==id) {
	            	t.setX(d);
	                t.setY(e);
	                return;
	            }
	        }
		}
	}
	
	//delete Controller -> delete Controller's ControlAction -> delete Related Controller's ControlAction
	public void deleteController(Integer id) {
		if(controllers != null) {
			for (Controller c : controllers) {
	            if (c.getId()==id) {
	            	for( Integer caId : c.getCA().keySet() ){
	            		if(c.getCA().get(caId).equals(new SimpleIntegerProperty(1))) {
	            			deleteControlActionFromController(caId);	
	            		}else {
	            			deleteControlActionFromControlled(caId);
	            		}
	            		
	                }
	            	for( Integer fbId : c.getFB().keySet() ){
	            		if(c.getFB().get(fbId).equals(new SimpleIntegerProperty(1))) {
	            			deleteFeedbackFromController(fbId);	
	            		}else {
	            			deleteFeedbackFromControlled(fbId);
	            		}
	                }
	            	
	                controllers.remove(c);
	                return;
	            }
	        }
		}
	}
	
	public void deleteControlActionFromController(Integer caId) {
		if(controlActions != null) {
			for (ControlAction c : controlActions) {
	            if (c.getId()==caId) {
	            	c.getControlled().removeCA(caId);
	            	
	            	controlActions.remove(c);
	                return;
	            }
	        }
		}
	}
	
	public void deleteControlActionFromControlled(Integer caId) {
		if(controlActions != null) {
			for (ControlAction c : controlActions) {
	            if (c.getId()==caId) {
	            	c.getController().removeCA(caId);
	            	
	            	controlActions.remove(c);
	                return;
	            }
	        }
		}
	}
	
	public void deleteFeedbackFromController(Integer fbId) {
		if(feedbacks != null) {
			for (Feedback c : feedbacks) {
	            if (c.getId()==fbId) {
	            	c.getControlled().removeCA(fbId);
	            	
	            	feedbacks.remove(c);
	                return;
	            }
	        }
		}
	}
	
	public void deleteFeedbackFromControlled(Integer fbId) {
		if(feedbacks != null) {
			for (Feedback c : feedbacks) {
	            if (c.getId()==fbId) {
	            	c.getController().removeCA(fbId);
	            	
	            	feedbacks.remove(c);
	                return;
	            }
	        }
		}
	}
		
	//delete ControlAction -> delete Controller, Controlled's ControlAction
	public void deleteControlAction(Integer id) {
		if(controlActions != null) {
			for (ControlAction c : controlActions) {
	            if (c.getId()==id) {
	            	c.getController().removeCA(id);
	            	c.getControlled().removeCA(id);

	            	controlActions.remove(c);
	                return;
	            }
	        }
		}
	}
	
	public void deleteFeedback(Integer id) {
		if(feedbacks != null) {
			for (Feedback c : feedbacks) {
	            if (c.getId()==id) {
	            	c.getController().removeFB(id);
	            	c.getControlled().removeFB(id);
	            	
	            	feedbacks.remove(c);
	                return;
	            }
	        }
		}
	}
	
	public void deleteText(Integer id) {
		if(texts != null) {
			for (Text t : texts) {
	            if (t.getId()==id) {
	            	texts.remove(t);
	                return;
	            }
	        }
		}
	}
	
	public void modifyController(Integer id, String name) {
		if(controllers != null) {
			for (Controller c : controllers) {
	            if (c.getId()==id) {
	                c.setName(name);
	                return;
	            }
	        }
		}
	}
	
	public void modifyControlAction(Integer id, ArrayList<String> CA) {
		if(controlActions != null) {
			for (ControlAction ca : controlActions) {
	            if (ca.getId()==id) {
	                ca.setCA(CA);
	                return;
	            }
	        }
		}
	}
	
	public void modifyFeedback(Integer id, ArrayList<String> FB) {
		if(feedbacks != null) {
			for (Feedback fb : feedbacks) {
				if (fb.getId()==id) {
	            	fb.setFB(FB);
	                return;
	            }
			}
		}
	}
	
	public void modifyText(Integer id, String content) {
		if(texts != null) {
			for (Text t : texts) {
	            if (t.getId()==id) {
	            	t.content = content;
	                return;
	            }
	        }
		}
	}
	
	public Controller findController(String controller) {
		if(controllers != null) {
			for (Controller c : controllers) {
	            if (c.getName().equals(controller)) {
	                return c;
	            }
	        }
		}
		return null;
	}
	
	public Controller findController(Integer id) {
		if(controllers != null) {
			for (Controller c : controllers) {
	            if (c.getId()==id) {
	                return c;
	            }
	        }
		}
		return null;
	}
	
	public ControlAction findControlAction(Integer id) {
		if(controlActions != null) {
			for (ControlAction ca : controlActions) {
	            if (ca.getId()==id) {
	                return ca;
	            }
	        }
		}
		return null;
	}
	
	public Feedback findFeedback(Integer id) {
		if(feedbacks != null) {
			for (Feedback fb : feedbacks) {
	            if (fb.getId()==id) {
	                return fb;
	            }
	        }
		}
		return null;
	}
	
	public Text findText(Integer id) {
		if(texts != null) {
			for (Text t : texts) {
	            if (t.getId()==id) {
	            	return t;
	            }
	        }
		}
		return null;
	}
}
