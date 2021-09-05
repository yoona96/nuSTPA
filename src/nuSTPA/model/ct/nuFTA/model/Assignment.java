package nuSTPA.model.ct.nuFTA.model;

import java.util.ArrayList;

public class Assignment {
	private Variable output;
	private ArrayList<Condition> conditions;

	public Assignment() {
		this.conditions = new ArrayList<Condition>();
	}

	@SuppressWarnings("unchecked")
	public Assignment(Assignment assign) {
		this.output = new Variable(assign.getOutput());
		this.conditions = (ArrayList<Condition>) assign.getConditions().clone();
	}

	public Assignment(Variable output) {
		this.output = output;
		this.conditions = new ArrayList<Condition>();
	}

	public void setOutput(Variable output) {
		this.output = output;
	}

	public void addCondition(Condition condition) {
		this.conditions.add(condition);
	}

	public void addCondition(String rawCondition, boolean isNot) {
		Condition c = new Condition(rawCondition, isNot);
		this.conditions.add(c);
	}

	public Variable getOutput() {
		return this.output;
	}

	public ArrayList<Condition> getConditions() {
		return this.conditions;
	}

	@SuppressWarnings("unchecked")
	public void setConditions(ArrayList<Condition> conditions) {
		this.conditions = (ArrayList<Condition>) conditions.clone();
	}
}