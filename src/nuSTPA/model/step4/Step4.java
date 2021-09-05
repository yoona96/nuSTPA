package nuSTPA.model.step4;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Step4 {
	
	private StringProperty UCA, lossFactor, lossScenario;
	
	/*
	 * default constructor
	 */
	public Step4() {
		this(null, null, null);
	}
	
	/*
	 * initialize constructor
	 */
	public Step4(String UCA, String lossFactor, String lossScenario) {
		this.UCA = new SimpleStringProperty(UCA);
		this.lossFactor = new SimpleStringProperty(lossFactor);
		this.lossScenario = new SimpleStringProperty(lossScenario);
	}
	
	public String getUCA() {
		return UCA.get();
	}
	
	public StringProperty getUCAProperty() {
		return UCA;
	}
	
	public void setUCA(String UCA) {
		this.UCA.set(UCA);
	}
	
	public String getLossFactor() {
		return lossFactor.get();
	}
	
	public StringProperty getLossFactorProperty() {
		return lossFactor;
	}
	
	public void setLossFactor(String lossFactor) {
		this.lossFactor.set(lossFactor);
	}
	
	public String getLossScenario() {
		return lossScenario.get();
	}
	
	public StringProperty getLossScenarioProperty() {
		return lossScenario;
	}
	
	public void setLossScenario(String lossScenario) {
		this.lossScenario.set(lossScenario);
	}
}