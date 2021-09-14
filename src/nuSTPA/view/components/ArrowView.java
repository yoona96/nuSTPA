package nuSTPA.view.components;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import nuSTPA.model.step2.ControlAction;
import nuSTPA.model.step2.Feedback;

public class ArrowView extends Path{
	
	private static final double defaultArrowHeadSize = 15.0;
	
	public int id;
	public DoubleProperty startX, startY, endX, endY;
	public LabelView label;
	public String type="";
	public int[] endNum;
	
	public ArrowView(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY, double arrowHeadSize, String type, int[] startNum, int[] endNum) {
		super();
		
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.type = type;
		this.endNum = endNum;
		
		strokeProperty().bind(fillProperty());
        setFill(Color.BLACK);
        setStrokeWidth(1);
        
        MoveTo move;
        LineTo line0, line1, line2;
        if(type.equals("CA")) {
        	//Line for controlAction
            move = new MoveTo(startX.get()+100+(startNum[0]-1)*100, startY.get()+50);
            move.xProperty().bind(startX.add(100).add((startNum[0]-1)*100));
            move.yProperty().bind(startY.add(50));
        	
            line0 = new LineTo(startX.get()+100+(startNum[0]-1)*100, startY.get()+100);
            line0.xProperty().bind(startX.add(100).add((startNum[0]-1)*100));
            line0.yProperty().bind(startY.add(50).add(50));
            
            line1 = new LineTo(endX.get()+100+(endNum[0]-1)*100, startY.get()+100);
            line1.xProperty().bind(endX.add(100).add((endNum[0]-1)*100));
            line1.yProperty().bind(startY.add(50).add(50));
            
            line2 = new LineTo(endX.get()+100+(endNum[0]-1)*100, endY.get()+50);
            line2.xProperty().bind(endX.add(100).add((endNum[0]-1)*100));
            line2.yProperty().bind(endY.add(50));
            
            getElements().add(move);
            getElements().add(line0);
            getElements().add(line1);
            getElements().add(line2);
            getElements().add(line1);
            getElements().add(line0); 
        }else {
        	//Line for feedback
        	move = new MoveTo(startX.get()+50+(startNum[1]-1)*100, startY.get()+50);
            move.xProperty().bind(startX.add(50).add((startNum[1]-1)*100));
            move.yProperty().bind(startY.add(50));
        	
            line0 = new LineTo(startX.get()+50+(startNum[1]-1)*100, startY.get()+50);
            line0.xProperty().bind(startX.add(50).add((startNum[1]-1)*100));
            line0.yProperty().bind(startY.add(50).subtract(100));
            
            line1 = new LineTo(endX.get()+50+(endNum[1]-1)*100, startY.get()+50);
            line1.xProperty().bind(endX.add(50).add((endNum[1]-1)*100));
            line1.yProperty().bind(startY.add(50).subtract(100));
            
            line2 = new LineTo(endX.get()+50+(endNum[1]-1)*100, endY.get()+50);
            line2.xProperty().bind(endX.add(50).add((endNum[1]-1)*100));
            line2.yProperty().bind(endY.add(50));
            
            getElements().add(move);
            getElements().add(line0);
            getElements().add(line1);
            getElements().add(line2);
            getElements().add(line1);
            getElements().add(line0);  
        }
        
       
        
        //ArrowHead
        DoubleProperty angle = new SimpleDoubleProperty(0);
        angle.bind(Bindings.createDoubleBinding(
        	    () ->  Math.atan2((endY.get() - startY.get()), (endX.get() - endX.get())) - Math.PI / 2.0,
        	    startX, startY, endX, endY));
        
        DoubleProperty sin = new SimpleDoubleProperty(0);
        sin.bind(Bindings.createDoubleBinding(
        	    () ->  Math.sin(angle.get()),
        	    angle));
        
        DoubleProperty cos = new SimpleDoubleProperty(0);
        cos.bind(Bindings.createDoubleBinding(
        	    () ->  Math.cos(angle.get()),
        	    angle));
        
        //point1   
        DoubleProperty x1 = new SimpleDoubleProperty(0);
        x1.bind(Bindings.createDoubleBinding(
        	    () ->  (- 1.0 / 2.0 * cos.get() + Math.sqrt(3) / 2 * sin.get()) * arrowHeadSize + endX.get(),
        	    sin, cos, endX));
             
        DoubleProperty y1 = new SimpleDoubleProperty();
        y1.bind(Bindings.createDoubleBinding(
        	    () ->  (- 1.0 / 2.0 * sin.get() - Math.sqrt(3) / 2 * cos.get()) * arrowHeadSize + endY.get(),
        	    sin, cos, endY));
        
        //point2
        DoubleProperty x2 = new SimpleDoubleProperty();
        x2.bind(Bindings.createDoubleBinding(
        	    () -> (1.0 / 2.0 * cos.get() + Math.sqrt(3) / 2 * sin.get()) * arrowHeadSize + endX.get(),
        	    sin, cos, endX));
        
        DoubleProperty y2 = new SimpleDoubleProperty();
        y2.bind(Bindings.createDoubleBinding(
        	    () -> (1.0 / 2.0 * sin.get() - Math.sqrt(3) / 2 * cos.get()) * arrowHeadSize + endY.get(),
        	    sin, cos, endY));
        
        MoveTo arrow;
        LineTo line3, line4;

        //arrow part
        if(type.equals("CA")) {
        	//controlAction case
        	arrow = new MoveTo(endX.get()+100+(endNum[0]-1)*100, endY.get());
        	arrow.xProperty().bind(endX.add(100).add((endNum[0]-1)*100));
        	arrow.yProperty().bind(endY);
        	
        	line3 = new LineTo(endX.get()+100+(endNum[0]-1)*100-5, endY.get()-10);
            line3.xProperty().bind(endX.add(100).add((endNum[0]-1)*100).subtract(5));
            line3.yProperty().bind(endY.subtract(10));
            
            line4 = new LineTo(endX.get()+100+(endNum[0]-1)*100+5, endY.get()-10);
            line4.xProperty().bind(endX.add(100).add((endNum[0]-1)*100).add(5));
            line4.yProperty().bind(endY.subtract(10));            
        }else {
        	//feedback case
        	arrow = new MoveTo(endX.get()+50+(endNum[1]-1)*200, endY.get()+100);
        	arrow.xProperty().bind(endX.add(50).add((endNum[1]-1)*100));
        	arrow.yProperty().bind(endY.add(200));
        	
        	line3 = new LineTo(endX.get()+50+(endNum[1]-1)*200-5, endY.get()+100);
            line3.xProperty().bind(endX.add(50).add((endNum[1]-1)*100).subtract(5));
            line3.yProperty().bind(endY.add(200).add(10));
            
            line4 = new LineTo(endX.get()+50+(endNum[1]-1)*200+5, endY.get()+100);
            line4.xProperty().bind(endX.add(50).add((endNum[1]-1)*100).add(5));
            line4.yProperty().bind(endY.add(200).add(10));
        }
        
        getElements().add(arrow);
        getElements().add(line3);
        getElements().add(line4);
	}
	
	public ArrowView(ControlAction ca, DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY, int id, int[] startNum, int[] endNum){
		this(startX, startY, endX, endY, 15, "CA", startNum, endNum);
		this.id = ca.getId();
	}
	
	public ArrowView(Feedback fb, DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY, int id, int[] startNum, int[] endNum){
	    this(endX, endY, startX, startY, 15, "FB", startNum, endNum);
		this.id = fb.getId();
	}
	
	public ArrowView(ControlAction ca, DoubleProperty startX2, DoubleProperty startY2, DoubleProperty endX2,
			DoubleProperty endY2, IntegerProperty id2, int[] startNum, int[] endNum2) {
		// TODO Auto-generated constructor stub
		this(endX2, endY2, startX2, startY2, 15, "FB", startNum, endNum2);
		this.id = ca.getId();
	}

	public ArrowView(Feedback fb, DoubleProperty startX2, DoubleProperty startY2, DoubleProperty endX2,
			DoubleProperty endY2, IntegerProperty id2, int[] startNum, int[] endNum2) {
		// TODO Auto-generated constructor stub
		this(endX2, endY2, startX2, startY2, 15, "FB", startNum, endNum2);
		this.id = fb.getId();
	}

	public void setLabel(LabelView label) {
		this.label = label;
	}
	
	public Integer getID() {
    	return id;
    }
}
