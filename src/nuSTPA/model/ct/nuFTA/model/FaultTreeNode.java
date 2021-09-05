package nuSTPA.model.ct.nuFTA.model;

import java.util.ArrayList;

import nuSTPA.model.ct.nuFTA.controller.MainController;

public class FaultTreeNode {
	private int gateType;
	private String text;
	private Variable variable;
	private ArrayList<FaultTreeNode> childs;
	private FaultTreeNode parent;
	private boolean isInput;
	private boolean isAbstract;
	private boolean isFormula;
	private int groupId;
	
	public FaultTreeNode(String text) {
		this.text = text;
		this.childs = new ArrayList<FaultTreeNode>();
		this.parent = null;
		this.gateType = -1;
		this.isFormula = false;
		this.isAbstract = false;
		this.isInput = false;
		MainController.getInstance().count++;
	}

	public FaultTreeNode(String text, int gateType) {
		this.text = text;
		this.gateType = gateType;
		this.childs = new ArrayList<FaultTreeNode>();
		this.parent = null;
		this.isFormula = false;
		this.isAbstract = false;
		this.isInput = false;
		MainController.getInstance().count++;
	}

	public FaultTreeNode(String text, int gateType, boolean isFormula) {
		this.gateType = gateType;
		this.text = text;
		this.isFormula = isFormula;
		this.childs = new ArrayList<FaultTreeNode>();
		this.parent = null;
		this.isAbstract = false;
		this.isInput = false;
		MainController.getInstance().count++;
	}
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public FaultTreeNode getParent() {
		return parent;
	}

	public void setParent(FaultTreeNode parent) {
		this.parent = parent;
		this.groupId = parent.getGroupId();
	}

	public void addChild(FaultTreeNode child) {
		this.childs.add(child);
	}

	public int getGateType() {
		return gateType;
	}

	public void setGateType(int gateType) {
		this.gateType = gateType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isFormula() {
		return isFormula;
	}

	public void setFormula(boolean isFormula) {
		this.isFormula = isFormula;
	}

	public ArrayList<FaultTreeNode> getChilds() {
		return childs;
	}

	public void setChilds(ArrayList<FaultTreeNode> childs) {
		this.childs = childs;
	}

	public boolean isInput() {
		return isInput;
	}

	public void setInput(boolean isInput) {
		this.isInput = isInput;
	}
}