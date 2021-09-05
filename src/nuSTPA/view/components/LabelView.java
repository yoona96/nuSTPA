package nuSTPA.view.components;

import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LabelView extends Label {

//	StringProperty label;
	String label;
	ArrayList<String> CA;
	ObservableList<String> listItems;
	String type;
	
	public LabelView(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY, ArrayList<String> arrayList, String type, int[] endNum) {
		this.type = type;
		
		if(type=="CA") {
			layoutXProperty().bind(endX.add(50).add((endNum[0]-1)*50));
			layoutYProperty().bind(endY.subtract(endY.subtract(startY.add(100)).divide(2)).subtract(20));
		}else {
			layoutXProperty().bind(endX.add(50).add((endNum[1]-1)*100));
			layoutYProperty().bind(endY.add(100).subtract(endY.add(100).subtract(startY).divide(2)).subtract(20));
		}
		
		
		updateArrayCA(arrayList);

		setFont(new Font(18));
		setText(this.label.toString());
		setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1.0), new CornerRadii(0.0), new Insets(0.0))));
	}
	
	public void updateArrayCA(ArrayList<String> arrayList) {
		String label = "";
		
		for(int i=0; i<arrayList.size(); i++) {
			if(i==0) {
				label = arrayList.get(0);
			}else {
				label = label + ", " + arrayList.get(i);
			}
			
		}
		
		this.label = label;
	}	
}
