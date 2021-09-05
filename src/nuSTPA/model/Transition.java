package nuSTPA.model;

public class Transition {

	private String source, target;
	
	public Transition() {
		this.source = null;
		this.target = null;
	}
	
	public Transition(String source, String target) {
		this.source = source;
		this.target = target;
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Transition(String target) {
		this.target = target;
	}
}