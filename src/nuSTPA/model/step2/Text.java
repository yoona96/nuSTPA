package nuSTPA.model.step2;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Text {

	double x, y;
	String content;
	int id;
	
	public Text() {
		
	}
	
	public Text(double x, double y, String content, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.content = content;
	}
	
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public String getContent() {
		return content;
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
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
}
