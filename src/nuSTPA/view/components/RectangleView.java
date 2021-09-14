package nuSTPA.view.components;

import java.util.Arrays;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import nuSTPA.model.step2.Components;

public class RectangleView extends StackPane {

	Rectangle rect;
	Circle drag, resizeT, resizeB, resizeL, resizeR;
	public int id, name;
	public DoubleProperty x, y;
	Components dataStore;
	private DoubleProperty num;
//	public DoubleProperty width;
	private Label label;	
	
	//also add process model to controller rectangle view
	public RectangleView(DoubleProperty x, DoubleProperty y, String string, Integer integer, Components dataStore, ObservableList<String> pmList) {
	
		
		this.dataStore = dataStore;
		this.id = integer;
		
//		width = new SimpleDoubleProperty(150);
		this.rect = new Rectangle(200, 200);
		this.rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 1;");
		
		this.x = x;
		this.y = y;
		x.bind(rect.layoutXProperty());
		y.bind(rect.layoutYProperty());
		
//		this.num = num;
//		
//		width.bind(Bindings.createDoubleBinding(
//        	    () ->  150 + 50*num.get(),
//        	   num));
//	
//		rect.widthProperty().bind(width);
		
		VBox vbox = new VBox();
		HBox hbox = new HBox();
		
		label = new Label(string);
		label.setFont(new Font(18));
		label.setWrapText(true);
//		label.setPrefWidth(this.rect.getWidth()*2);
		label.setPadding(new Insets(5));
		label.setAlignment(Pos.TOP_LEFT);
//		label.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1.0), new CornerRadii(0.0), new Insets(0.0))));
		getChildren().addAll(rect, vbox);
		
		vbox.setPrefHeight(rect.getHeight());
//		vbox.setPrefWidth(rect.getWidth());
//		vbox.prefHeight(rect.getHeight()/2);
		
		vbox.getChildren().add(label);
		
		ListView<String> pmListView = new ListView<String>();
		if(pmListView.getItems() != null) {
//			this.rect.setHeight(this.rect.getHeight()*1.5);
//			pmListView.setPrefHeight(this.rect.getHeight()*0.5);
//			pmListView.setPrefWidth(this.rect.getWidth()*0.5);
			vbox.getChildren().add(hbox);
			hbox.getChildren().add(pmListView);
			hbox.setAlignment(Pos.CENTER);
			hbox.setPadding(new Insets(10));
			pmListView.getItems().addAll(pmList);
		}
//		else {
//			this.rect.setHeight(this.rect.getHeight());
//		}
		
		enableDrag();
	}
	
	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public void resizeRectangle(int[] num) {
		if(num[0]>=1 || num[1]>=1) {	
			if(num[0]<1 && num[1]>=1) {
				this.rect.setWidth(100 * num[1]);
			}else if(num[0]>=1 && num[1]<1){
				this.rect.setWidth(100 * num[0]);
			}
			else if(num[0]>=1 && num[1]>=1){
				this.rect.setWidth(50 * num[0] + 100 * num[1]);
			}
		}	
	}
	
	//make a node movable by dragging it around with the mouse.
	private void enableDrag() {
		final Delta dragDelta = new Delta();

		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				// record a delta distance for the drag and drop operation.
				dragDelta.x = getLayoutX() - mouseEvent.getX();
				dragDelta.y = getLayoutY() - mouseEvent.getY();
				getScene().setCursor(Cursor.MOVE);
			}
		});
		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				getScene().setCursor(Cursor.HAND);
				
				dataStore.moveController(id, getLayoutX(), getLayoutY());
			}
		});
		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				setLayoutX(mouseEvent.getX() + dragDelta.x);
				setLayoutY(mouseEvent.getY() + dragDelta.y);	
			}
		});
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (!mouseEvent.isPrimaryButtonDown()) {
					getScene().setCursor(Cursor.HAND);
				}
			}
		});
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (!mouseEvent.isPrimaryButtonDown()) {
					getScene().setCursor(Cursor.DEFAULT);		
				}
			}
		});
	}
	
	static class Delta {
		double x, y;
	}
}
