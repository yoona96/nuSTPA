package nuSTPA.model.ct.nuFTA.controller;

import java.util.ArrayList;
import java.util.HashMap;

import nuSTPA.model.ct.nuFTA.model.AnnotatedState;
import nuSTPA.model.ct.nuFTA.model.Assignment;
import nuSTPA.model.ct.nuFTA.model.Edge;
import nuSTPA.model.ct.nuFTA.model.FSM;
import nuSTPA.model.ct.nuFTA.model.FaultTreeNode;
import nuSTPA.model.ct.nuFTA.model.GateType;
import nuSTPA.model.ct.nuFTA.model.IONode;
import nuSTPA.model.ct.nuFTA.model.Node;
import nuSTPA.model.ct.nuFTA.model.SDT;
import nuSTPA.model.ct.nuFTA.model.State;
import nuSTPA.model.ct.nuFTA.model.TTS;
import nuSTPA.model.ct.nuFTA.model.Transition;
import nuSTPA.model.ct.nuFTA.model.Variable;
import nuSTPA.model.ct.nuFTA.model.VariableNode;
import nuSTPA.model.ct.nuFTA.model.VariableType;

public class FaultTreeCreator {

	private HashMap<Integer, Node> ftanodelist;
	private ArrayList<Variable> typetable;
	private LogicManager lm;
	private ArrayList<FaultTreeNode> group = new ArrayList<FaultTreeNode>();
	// private TTS makeFSM;
	private FSM makeFSM;
	private boolean isTimeTree = false;

	public FaultTreeCreator() {
		lm = new LogicManager();
	}

	public FaultTreeCreator(HashMap<Integer, Node> ftanodelist, ArrayList<Variable> typetable) {
		this.ftanodelist = ftanodelist;
		this.typetable = typetable;
		lm = new LogicManager(typetable);
	}

	public void makeTimeTree(FaultTreeNode causeTimeNode, String oneCycleTime, String prevState) {
		int totalCycle = this.calculateCycle(causeTimeNode.getText(), oneCycleTime);
		String[] str = prevState.split(" ");
		if (causeTimeNode.getText().contains("not for [")) {
			this.timeNotSatisfyTemplate(causeTimeNode, totalCycle, str[0]);
		} else {
			this.timeSatisfyTemplate(causeTimeNode, totalCycle, str[0]);
		}
		return;
	}

	private void timeNotSatisfyTemplate(FaultTreeNode timeNode, int totalCycle, String prevState) {
		FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
		timeNode.addChild(or);
		or.setParent(timeNode);
		Variable conditionVariable = new Variable(timeNode.getVariable());
		String condition = timeNode.getText();
		for (int i = 0; i < condition.length() - 2; i++) {
			if (condition.charAt(i) == 'f' && condition.charAt(i + 1) == 'o' && condition.charAt(i + 2) == 'r') {
				condition = condition.substring(0, i);
				break;
			}
		}
		Node node = this.ftanodelist.get(timeNode.getGroupId());
		Node targetNode = null;
		for (Edge e : node.getEdges()) {
			if (timeNode.getVariable() != null) {
				if (timeNode.getVariable().getName().equals(this.ftanodelist.get(e.getSourceID()).getName())) {
					targetNode = this.ftanodelist.get(e.getSourceID());
					break;
				}
			}
		}
		for (int i = 1; i < totalCycle; i++) {
			FaultTreeNode notSatisfy = new FaultTreeNode(
					"in the case of t-" + i + " time \n the condition is not satisfy");
			notSatisfy.setInput(true);
			or.addChild(notSatisfy);
			notSatisfy.setParent(or);
			for (int j = 1; j <= i; j++) {
				FaultTreeNode periodAnd = new FaultTreeNode("&", GateType.AND);
				notSatisfy.addChild(periodAnd);
				periodAnd.setParent(notSatisfy);
				if (i == j) {
					FaultTreeNode cycle = new FaultTreeNode(
							"condition is not satisfy and State in " + prevState + " condition at t-" + j);
					cycle.setInput(true);
					cycle.setAbstract(true);
					periodAnd.addChild(cycle);
					cycle.setParent(periodAnd);
					FaultTreeNode lastNode = new FaultTreeNode(prevState);
					periodAnd.addChild(lastNode);
					lastNode.setParent(periodAnd);
					isTimeTree = true;
					this.makeTree(lastNode, null, this.ftanodelist.get(timeNode.getGroupId()));
				} else {
					FaultTreeNode cycle = new FaultTreeNode(prevState + " at t-" + j);
					cycle.setInput(true);
					periodAnd.addChild(cycle);
					cycle.setParent(periodAnd);
					FaultTreeNode conditionftNode = new FaultTreeNode(condition);
					conditionftNode.setVariable(conditionVariable);
					conditionftNode.setParent(periodAnd);
					periodAnd.addChild(conditionftNode);
					this.makeTree(conditionftNode, conditionftNode.getVariable(), targetNode);
				}
			}
		}
	}

	private void settingGroupID(FaultTreeNode ftn) {
		ftn.setGroupId(ftn.getParent().getGroupId());
		for (FaultTreeNode tmp : ftn.getChilds()) {
			settingGroupID(tmp);
		}
	}

	private void timeSatisfyTemplate(FaultTreeNode timeNode, int totalCycle, String prevState) {
		FaultTreeNode and = new FaultTreeNode("&", GateType.AND);
		timeNode.addChild(and);
		and.setParent(timeNode);
		Variable conditionVariable = new Variable(timeNode.getVariable());
		String condition = timeNode.getText();
		for (int i = 0; i < condition.length() - 2; i++) {
			if (condition.charAt(i) == 'f' && condition.charAt(i + 1) == 'o' && condition.charAt(i + 2) == 'r') {
				condition = condition.substring(0, i);
				break;
			}
		}
		Node node = this.ftanodelist.get(timeNode.getGroupId());
		Node targetNode = null;
		for (Edge e : node.getEdges()) {
			if (timeNode.getVariable() != null) {
				if (timeNode.getVariable().getName().equals(this.ftanodelist.get(e.getSourceID()).getName())) {
					targetNode = this.ftanodelist.get(e.getSourceID());
					break;
				}
			}
		}
		for (int i = 1; i < totalCycle; i++) {
			FaultTreeNode periodAnd = new FaultTreeNode("&", GateType.AND);
			and.addChild(periodAnd);
			periodAnd.setParent(and);
			FaultTreeNode cycle = new FaultTreeNode(prevState + " at t-" + i);
			periodAnd.addChild(cycle);
			cycle.setParent(periodAnd);
			cycle.setInput(true);
			FaultTreeNode conditionftNode;
			if(MainController.getInstance().isCheck()){
				conditionftNode = lm.conditionToLogic(targetNode, conditionVariable, condition);
				this.makeTree(conditionftNode.getChilds().get(1), conditionftNode.getChilds().get(1).getVariable(), targetNode);
				FaultTreeNode ftn = conditionftNode.getChilds().get(2);
				for (Edge e : node.getEdges()) {
					if (ftn.getVariable() != null) {
						if (ftn.getVariable().getName().equals(this.ftanodelist.get(e.getSourceID()).getName())) {
							targetNode = this.ftanodelist.get(e.getSourceID());
							break;
						}
					}
				}
				conditionftNode.setParent(periodAnd);
				periodAnd.addChild(conditionftNode);
				this.makeTree(conditionftNode.getChilds().get(2), conditionftNode.getChilds().get(2).getVariable(), targetNode);
			}else{
				conditionftNode = new FaultTreeNode(condition);
				for (Variable v : ((VariableNode) node).getVariables()) {
					if (conditionftNode.getText().contains(v.getName()) && !v.getName().contains("t1")) {
						conditionftNode.setText(conditionftNode.getText().replace(v.getName(), v.getValue()));
						conditionftNode.setText(conditionftNode.getText().trim());
					}
				}
				conditionftNode.setVariable(conditionVariable);
				conditionftNode.setAbstract(true);
				conditionftNode.setParent(periodAnd);
				periodAnd.addChild(conditionftNode);
				conditionftNode.setGroupId(targetNode.getId());
				this.makeTree(conditionftNode, conditionftNode.getVariable(), targetNode);
			}
		}
		FaultTreeNode lastPeriodAnd = new FaultTreeNode("&", GateType.AND);
		and.addChild(lastPeriodAnd);
		lastPeriodAnd.setParent(and);
		FaultTreeNode lastCycle = new FaultTreeNode(
				"condition is not satisfy and State in " + prevState + " condition at t-" + totalCycle);
		lastCycle.setInput(true);
		lastCycle.setAbstract(true);
		lastPeriodAnd.addChild(lastCycle);
		lastCycle.setParent(lastPeriodAnd);
		FaultTreeNode lastNode = new FaultTreeNode(prevState);
		isTimeTree = true;
		lastPeriodAnd.addChild(lastNode);
		lastNode.setParent(lastPeriodAnd);
		lastNode.setAbstract(true);
		this.makeTree(lastNode, null, this.ftanodelist.get(timeNode.getGroupId()));
	}

	private int calculateCycle(String text, String cycleTime) {
		String timeText = text;
		String minTime = "";
		String maxTime = "";
		int totalCycle = 0;
		for (int i = 0; i < timeText.length(); i++) {
			if (timeText.charAt(i) == '[') {
				for (int j = i + 1; j < timeText.length(); j++) {
					if (timeText.charAt(j) == ',') {
						minTime = timeText.substring(i + 1, j);
						maxTime = timeText.substring(j + 2, timeText.length() - 1);
						break;
					}
				}
				break;
			}
		}
		totalCycle = Integer.parseInt(maxTime) / Integer.parseInt(cycleTime);
		return totalCycle;
	}

	private void tts_timeTemplate(FaultTreeNode root, TTS tts) {
		makeFSM = tts;
		ArrayList<String> stateName = new ArrayList<String>();
		boolean check = false;
		for (AnnotatedState annotatedState : tts.getAnnotatedStates()) {
			Assignment tassign = new Assignment(annotatedState.getAssignment());
			check = false;
			for (String name : stateName) {
				if (name.equals(annotatedState.getOrignState().getName())) {
					check = true;
					break;
				}
			}
			if (annotatedState.getOrignState().getName().equals(root.getText())) {
				this.tts_template_Init(root, annotatedState, tassign.getOutput(), check);
			}
			if (!check) {
				stateName.add(annotatedState.getOrignState().getName());
			}
		}
	}

	public FaultTreeNode makeTree(FaultTreeNode root, Variable output, Node node) {
		if (root.getText().contains("!=")) {
			if (output.getValue().equals("true")) {
				output.setValue("false");
			} else if (output.getValue().equals("false")) {
				output.setValue("true");
			}
		}

		root.setAbstract(true);
		root.setGroupId(node.getId());
		if (node instanceof SDT) {
			group.add(root);
			sdt_template(root, output, (SDT) node);
		} else if (node instanceof TTS) {
			if (!((TTS) node).isAnnotated()) {
				extendFSM((TTS) node);
				((TTS) node).setAnnotated(true);
			}
			group.add(root);
			if (isTimeTree) {
				tts_timeTemplate(root, (TTS) node);
			} else {
				tts_template(root, output, (TTS) node);
			}
		} else if (node instanceof FSM) {
			if (!((FSM) node).isAnnotated()) {
				extendFSM((FSM) node);
				((FSM) node).setAnnotated(true);
			}
			group.add(root);
			fsm_template(root, output, (FSM) node);
		} else {
			this.removeAndOr(root);
			root.setFormula(true);
			root.setInput(true);
			return root;
		}
		for (FaultTreeNode ftn : this.findLeaf(root, root.getGroupId())) {
			for (Edge e : node.getEdges()) {
				if (ftn.getVariable() != null) {
					if (ftn.getVariable().getName().equals(this.ftanodelist.get(e.getSourceID()).getName())) {
						makeTree(ftn, ftn.getVariable(), this.ftanodelist.get(e.getSourceID()));
					}
				}
			}
		}
		return root;
	}

	public void extendFSM(FSM test) {
		for (State s : test.getStates()) {
			if (s.getId() == test.getInitialRefId()) {
				AnnotatedState exState = new AnnotatedState();
				exState.setOrignState(s);
				exState.setPrevTransition(null);
				Assignment ex_assignment = new Assignment();
				Variable ex_variable = null;
				if (test instanceof TTS) {
					ex_variable = new Variable(test.getName(), VariableType.BOOL, "false");
				} else {
					ex_variable = new Variable(test.getName(), VariableType.CONSTANT, "0");
				}
				ex_assignment.setOutput(ex_variable);
				exState.setAssignment(ex_assignment);
				exState.setName(s.getName());
				//exState.setName(s.getName() + "_" + String.valueOf(s.getAnnotatedCount()));
				for (Transition tmp_t : s.getTransitions()) {
					exState.addNextTransition(tmp_t);
				}
				test.addAnnotatedStates(exState);
				break;
			}
		}
		this.extendFSM(test, test.getAnnotatedStates().get(0));
	}

	public void extendFSM(FSM test, AnnotatedState ext) {
		for (Transition t : ext.getNextTransition()) {
			if (ext.getPrevTransition() != null) {
				if (ext.getPrevTransition().equals(t)) {
					continue;
				}
			}
			boolean exist = false;
			// ������ transition�� output ���� ���� ��� �� �� AnnotatedState�� output ���� ����
			if (ext.getPrevTransition() != null) {
				if (t.getAssignment().getOutput() == null) {
					t.getAssignment().setOutput(ext.getAssignment().getOutput());
					/*
					 * t.getAssignment().setOutput(ext.getPrevTransition().
					 * getAssignment().getOutput());
					 */
				}
			}
			for (AnnotatedState extTmp : test.getAnnotatedStates()) {
				// ���� ������� AnnotatedState�� �̹� ������� �ְ� �� ���� A ��� �ϸ� A�� ���� Ȯ����¸�
				// ext�� ����
				if (extTmp.getPrevTransition() != null) {
					// transition t�� ���� ������ state�� �����ϴ°� ?(condition, output,
					// state ID�� üũ�Ѵ�.)
					if (extTmp.getPrevTransition().getAssignment().getConditions().get(0)
							.equals(t.getAssignment().getConditions().get(0))
							&& (extTmp.getAssignment().getOutput().equals(t.getAssignment().getOutput())
									&& t.getTargetRefId() == extTmp.getOrignState().getId())) {
						exist = true;
						extTmp.addPrevAs(ext);
						break;
					}
				}
			}

			if (!exist) {
				AnnotatedState tmp = new AnnotatedState();
				tmp.setPrevTransition(t);
				tmp.setAssignment(t.getAssignment());
				for (State s : test.getStates()) {
					if (t.getTargetRefId() == s.getId()) {
						s.setAnnotatedCountInc();
						tmp.setName(s.getName());
						//tmp.setName(s.getName() + "_" + String.valueOf(s.getAnnotatedCount()));
						tmp.setOrignState(s);
						for (Transition add_t : s.getTransitions()) {
							tmp.addNextTransition(add_t);
						}
						break;
					}
				}
				tmp.addPrevAs(ext);
				test.addAnnotatedStates(tmp);
				this.extendFSM(test, tmp);
			}
		}
	}

	private ArrayList<FaultTreeNode> findLeaf(FaultTreeNode root, int id) {
		ArrayList<FaultTreeNode> leaflist = new ArrayList<FaultTreeNode>();
		root.setGroupId(id);
		if (root.getChilds().size() == 0 && root.isFormula()) {
			if (!root.getText().contains("at t")) {
				leaflist.add(root);
				if (!root.isInput() && !root.getText().matches(".*at t.*|.*\\[.*")) {
					root.setFormula(false);
				}
				if (root.isInput() && root.getText().matches(".*t[0-9].*")) {
					root.setFormula(true);
				}
				return leaflist;
			}
		} else {
			for (FaultTreeNode ftn : root.getChilds()) {
				leaflist.addAll(findLeaf(ftn, id));
			}
		}
		return leaflist;
	}

	public void sdt_template(FaultTreeNode root, Variable output, SDT sdt) {
		FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
		FaultTreeNode op = null;
		root.addChild(or);
		or.setParent(root);
		or.setAbstract(true);
		for (int i = 0; i < sdt.getAssignments().size(); i++) {
			boolean flag = false;
			FaultTreeNode and = new FaultTreeNode("&", GateType.AND);
			and.setAbstract(true);
			Assignment tassign = new Assignment(sdt.getAssignments().get(i));
			if (tassign.getOutput().getType() == VariableType.CONSTANT) {
				if (output.getType() == VariableType.CONSTANT) {
					if (tassign.getOutput().getValue().equals(output.getValue())) {
						FaultTreeNode temp = lm.conditionGenerateNode((Node) sdt, output, tassign);
						or.addChild(temp);
						temp.setParent(or);
						temp.setFormula(true);
					}
				} else if (output.getType() == VariableType.RANGE) {
					if (output.getMin() <= Integer.parseInt(tassign.getOutput().getValue())
							&& output.getMax() >= Integer.parseInt(tassign.getOutput().getValue())) {
						FaultTreeNode temp = lm.conditionGenerateNode((Node) sdt, output, tassign);
						or.addChild(temp);
						temp.setParent(or);
						temp.setFormula(true);
					}
				}
			} else if (tassign.getOutput().getType() == VariableType.RANGE) {
				FaultTreeNode action = new FaultTreeNode("Action");
				Variable vtemp = lm.setChildOutput(output, tassign.getOutput(), sdt);
				FaultTreeNode childout = null;
				if (vtemp != null) {
					String outname = "";
					switch (vtemp.getType()) {
					case VariableType.RANGE:
						outname = vtemp.getName();
						vtemp.setValue("-1");
						break;
					case VariableType.CONSTANT:
						outname = vtemp.getName();
						break;
					case VariableType.BOOL:
						outname = vtemp.getName();
						break;
					}
					childout = new FaultTreeNode(outname);
					if (outname.matches(".*t[0-9].*")) {
						childout.setInput(true);
					}
					childout.setFormula(true);
					childout.setVariable(vtemp);
					or.addChild(and);
					and.setParent(or);
					and.addChild(action);
					action.setParent(and);
					action.addChild(childout);
					childout.setParent(action);
					flag = true;
				}
				
				if (output.getType() == VariableType.CONSTANT) {
					if (tassign.getOutput().getMin() <= Integer.parseInt(output.getValue())
							&& tassign.getOutput().getMax() >= Integer.parseInt(output.getValue())) {
						tassign.getOutput().setType(VariableType.CONSTANT);
						tassign.getOutput().setMax(-1);
						tassign.getOutput().setMin(-1);
						tassign.getOutput().setValue(output.getValue());
						if (flag) {
							op = and;
							childout.setText("(A)" + childout.getText() + "=" + output.getValue());
							vtemp.setType(VariableType.CONSTANT);
							vtemp.setValue(output.getValue());
						} else {
							op = or;
						}
						FaultTreeNode temp = lm.conditionGenerateNode((Node) sdt, output, tassign);
						op.addChild(temp);
						temp.setParent(op);
					} else {
						and.getParent().getChilds().remove(and);
					}
				} else if (output.getType() == VariableType.RANGE) {
					boolean rangeCheck = false;
					if (output.getMin() <= tassign.getOutput().getMin()	&& tassign.getOutput().getMax() <= output.getMax()) {
						rangeCheck = true;
					}
					if (output.getMin() >= tassign.getOutput().getMin()) {
						if (output.getMin() <= tassign.getOutput().getMax()) {
							tassign.getOutput().setMin(output.getMin());
							rangeCheck = true;
						}
					}
					if (output.getMax() <= tassign.getOutput().getMax()) {
						if (output.getMax() >= tassign.getOutput().getMin()) {
							tassign.getOutput().setMax(output.getMax());
							rangeCheck = true;
						}
					}
					if (rangeCheck) {
						FaultTreeNode temp = lm.conditionGenerateNode((Node) sdt, output, tassign);
						if (flag) {
							if (vtemp.getType() == VariableType.RANGE) {
								if (tassign.getOutput().getMin() > vtemp.getMin()) {
									if (tassign.getOutput().getMin() > -1)
										childout.getVariable().setMin(tassign.getOutput().getMin());
									if (tassign.getOutput().getMax() < vtemp.getMax()) {
										if (tassign.getOutput().getMax() > -1)
											childout.getVariable().setMax(tassign.getOutput().getMax());
									}
								} else {
									if (tassign.getOutput().getMax() < vtemp.getMax()) {
										if (tassign.getOutput().getMax() > -1)
											childout.getVariable().setMax(tassign.getOutput().getMax());
									}
								}
								if (vtemp.getMin() - vtemp.getMax() > 0) {
									and.getParent().getChilds().remove(and);
								}
								childout.setText("(A)" + childout.getVariable().getMin() + "<=" + childout.getText()
										+ "<=" + childout.getVariable().getMax());
							}
							op = and;
						} else{
							op = or;
						}
						op.addChild(temp);
						temp.setParent(op);
					} else{
						and.getParent().getChilds().remove(and);
					}
				}
			} else {
				if (tassign.getOutput().getType() == VariableType.BOOL) {
					if (output.getType() == VariableType.BOOL) {
						boolean check = false;
						for (Edge e : sdt.getEdges()) {
							if (ftanodelist.get(e.getSourceID()).getName().equals(tassign.getOutput().getName())) {
								if (ftanodelist.get(e.getSourceID()) instanceof IONode) {
									sdt_make_action(output, tassign, sdt, or, and, op, flag);
									check=true;
								}
							}
						}
						if(!check){
							if (tassign.getOutput().getValue().equals(output.getValue())) {
								sdt_make_action(output, tassign, sdt, or, and, op, flag);
							}
						}
					}
				}
			}
		}
		if (or.getChilds().size() == 1) {
			or.getChilds().get(0).setParent(root);
			root.addChild(or.getChilds().get(0));
			root.getChilds().remove(or);
		} else if (or.getChilds().size() == 0) {
			root.getChilds().remove(or);
		}
	}

	private void sdt_make_action(Variable output, Assignment tassign, SDT sdt, FaultTreeNode or, FaultTreeNode and,
			FaultTreeNode op, boolean flag) {
		FaultTreeNode action = new FaultTreeNode("Action");
		Variable vtemp = lm.setChildOutput(output, tassign.getOutput(), sdt);
		FaultTreeNode childout = null;
		if (vtemp != null) {
			String outname = "";
			switch (vtemp.getType()) {
			case VariableType.RANGE:
				outname = vtemp.getName();
				vtemp.setValue("-1");
				break;
			case VariableType.CONSTANT:
				outname = vtemp.getName();
				break;
			case VariableType.BOOL:
				if(vtemp.getName().equals("true") || vtemp.getName().equals("false")){
					outname = sdt.getName();
				}else{
					outname = vtemp.getName();
				}
				break;
			}
			childout = new FaultTreeNode(outname);
			if (outname.matches(".*t[0-9].*")) {
				childout.setInput(true);
			}
			childout.setFormula(true);
			childout.setVariable(vtemp);
			or.addChild(and);
			and.setParent(or);
			and.addChild(action);
			action.setParent(and);
			action.addChild(childout);
			childout.setParent(action);
			flag = true;
		}
		FaultTreeNode temp = lm.conditionGenerateNode((Node) sdt, output, tassign);
		if (flag) {
			op = and;
			childout.setText("(A)"+childout.getText() + "=" + output.getValue());
			vtemp.setType(VariableType.BOOL);
			vtemp.setValue(output.getValue());
		} else {
			op = or;
		}
		op.addChild(temp);
		temp.setParent(op);
	}

	private void fsm_template_Init(FaultTreeNode root, AnnotatedState annotatedState, Variable output, boolean check) {
		// check �� true ��� �� �̹� �տ� ������� �ִٴ� ��, false�� ó�� ����� ��
		if (check) {
			for (FaultTreeNode fn : root.getChilds()) {
				if (fn.getText().contains(annotatedState.getOrignState().getName())) {
					FaultTreeNode and = fn.getChilds().get(0);
					FaultTreeNode transition = and.getChilds().get(1);
					FaultTreeNode or = transition.getChilds().get(0);
					FaultTreeNode enterState = or.getChilds().get(0);
					FaultTreeNode enterStateOr = enterState.getChilds().get(0);
					this.setFSMEnterTheState(annotatedState, output, enterStateOr);
					break;
				}
			}
		} else {
			FaultTreeNode out;
			if (output.getType() == VariableType.RANGE) {
				out = new FaultTreeNode(annotatedState.getOrignState().getName() + "\n (out:= " + output.getMin()
						+ " <= out <=" + output.getMax());
			} else {
				out = new FaultTreeNode(
						annotatedState.getOrignState().getName() + "\n (out:=" + output.getValue() + ")");
			}
			FaultTreeNode and = new FaultTreeNode("&", GateType.AND);
			root.addChild(out);
			out.addChild(and);
			out.setParent(root);
			and.setParent(out);
			FaultTreeNode tmp_state_at_t = new FaultTreeNode(
					makeFSM.getName() + "_state == " + annotatedState.getName() + " at t");
			tmp_state_at_t.setFormula(true);
			FaultTreeNode tmp_transition = new FaultTreeNode("transitions");
			and.addChild(tmp_state_at_t);
			and.addChild(tmp_transition);
			tmp_state_at_t.setParent(and);
			tmp_transition.setParent(and);
			FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
			tmp_transition.addChild(or);
			or.setParent(tmp_transition);
			or.setAbstract(true);
			FaultTreeNode tmp_Enter = new FaultTreeNode("Enter the state via state transition");
			or.addChild(tmp_Enter);
			tmp_Enter.setParent(or);
			FaultTreeNode tmp_Enter_or = new FaultTreeNode("|", GateType.OR);
			tmp_Enter.addChild(tmp_Enter_or);
			tmp_Enter_or.setParent(tmp_Enter);
			tmp_Enter_or.setAbstract(true);
			if (annotatedState.getPrevTransition() != null) {
				this.setFSMEnterTheState(annotatedState, output, tmp_Enter_or);
			}
			FaultTreeNode fsmRemain = this.setFSMRemainTheState(annotatedState, output);
			or.addChild(fsmRemain);
			fsmRemain.setParent(or);
		}
	}

	private FaultTreeNode setFSMEnterTheState(AnnotatedState annotatedState, Variable output, FaultTreeNode or) {
		FaultTreeNode tmp_and = new FaultTreeNode("&", GateType.AND);
		or.addChild(tmp_and);
		FaultTreeNode tmp_action = new FaultTreeNode("action");
		tmp_and.addChild(tmp_action);
		tmp_action.setParent(tmp_and);
		FaultTreeNode tmp_action_assignment = null;
		if (output.getType() == VariableType.RANGE) {
			tmp_action_assignment = new FaultTreeNode(
					"(A)" + output.getMin() + "<=" + makeFSM.getName() + "<=" + output.getMax());
		} else {
			tmp_action_assignment = new FaultTreeNode("(A)" + makeFSM.getName() + " = " + output.getValue());
		}
		tmp_action_assignment.setInput(true);
		tmp_action.addChild(tmp_action_assignment);
		tmp_action_assignment.setParent(tmp_action);
		tmp_and.setParent(or);
		FaultTreeNode tmp_tran = new FaultTreeNode("transition x -> " + annotatedState.getOrignState().getName());
		tmp_and.addChild(tmp_tran);
		tmp_tran.setParent(tmp_and);

		/*
		 * FaultTreeNode tmp_or = new FaultTreeNode("|", GateType.OR);
		 * 
		 * tmp_tran.addChild(tmp_or); tmp_or.setParent(tmp_tran);
		 */
		boolean origin_flag = false;
		for (AnnotatedState prevAnnotatedState : annotatedState.getPrevAS()) {
			origin_flag = false;
			for (FaultTreeNode fn : tmp_tran.getChilds()) {
				if (fn.getText().equals(prevAnnotatedState.getOrignState().getName() + " at t-p")) {
					origin_flag = true;
					break;
				}
			}
			if (!origin_flag) {
				FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
						prevAnnotatedState.getOrignState().getName() + " at t-p");
				tmp_tran_t_p.setFormula(true);
				tmp_tran.addChild(tmp_tran_t_p);
				tmp_tran_t_p.setParent(tmp_tran);
			}
		}

		FaultTreeNode tmp_raw_condition = new FaultTreeNode(
				annotatedState.getPrevTransition().getAssignment().getConditions().get(0).getRawCondition() + " at t");
		tmp_and.addChild(tmp_raw_condition);
		tmp_raw_condition.setParent(tmp_and);
		this.setConditionbyAction(annotatedState, output, tmp_raw_condition,
				annotatedState.getPrevTransition().getAssignment().getConditions().get(0).getRawCondition());
		return tmp_and;
	}

	private FaultTreeNode setFSMRemainTheState(AnnotatedState annotatedState, Variable output) {
		FaultTreeNode tmp_Remain = new FaultTreeNode("Remain at the state because of\n disabled outgoing transition");
		FaultTreeNode tmp_and = new FaultTreeNode("&", GateType.AND);
		tmp_Remain.addChild(tmp_and);

		FaultTreeNode tmp_action = new FaultTreeNode("action");
		tmp_and.addChild(tmp_action);
		tmp_action.setParent(tmp_and);
		FaultTreeNode tmp_action_assignment = null;
		if (output.getType() == VariableType.RANGE) {
			tmp_action_assignment = new FaultTreeNode(
					"(A)" + output.getMin() + "<=" + makeFSM.getName() + "_t0" + "<=" + output.getMax());
		} else {
			tmp_action_assignment = new FaultTreeNode("(A)" + makeFSM.getName() + "_t0" + " = " + output.getValue());
		}
		tmp_action_assignment.setInput(true);
		tmp_action.addChild(tmp_action_assignment);
		tmp_action_assignment.setParent(tmp_action);

		tmp_and.setParent(tmp_Remain);
		tmp_and.setAbstract(true);
		for (Transition t : annotatedState.getNextTransition()) {
			FaultTreeNode tmp_tran = new FaultTreeNode(
					"not transition " + annotatedState.getOrignState().getName() + " -> " + t.getTargetName());
			tmp_and.addChild(tmp_tran);
			tmp_tran.setParent(tmp_and);
			FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
			tmp_tran.addChild(tmp_tran_and);
			tmp_tran_and.setParent(tmp_tran);
			FaultTreeNode tmp_tran_t_p = new FaultTreeNode(annotatedState.getOrignState().getName() + " at t-p");
			tmp_tran_t_p.setFormula(true);
			// tmp_tran_t_p.setFormula(true);
			FaultTreeNode tmp_tran_condition = new FaultTreeNode(
					"not " + t.getAssignment().getConditions().get(0).getRawCondition() + " at t");
			tmp_tran_and.addChild(tmp_tran_t_p);
			tmp_tran_t_p.setParent(tmp_tran_and);
			tmp_tran_and.addChild(tmp_tran_condition);
			tmp_tran_condition.setParent(tmp_tran_and);
			String notCondition = lm.notTranslation(t.getAssignment().getConditions().get(0).getRawCondition());
			FaultTreeNode temp = lm.conditionToLogic(makeFSM, output, notCondition);
			tmp_tran_condition.addChild(temp);
			temp.setParent(tmp_tran_condition);
		}
		return tmp_Remain;
	}

	public void fsm_template(FaultTreeNode root, Variable output, FSM fsm) {
		FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
		root.addChild(or);
		or.setParent(root);
		or.setAbstract(true);
		makeFSM = fsm;
		ArrayList<String> stateName = new ArrayList<String>();
		boolean check = false;
		for (AnnotatedState annotatedState : fsm.getAnnotatedStates()) {
			if (!annotatedState.getName().contains("_0")) {
				check = false;
				for (String name : stateName) {
					if (name.equals(annotatedState.getOrignState().getName())) {
						check = true;
						break;
					}
				}
				Assignment tassign = new Assignment(annotatedState.getAssignment());
				if (tassign.getOutput().getType() == VariableType.CONSTANT) {
					if (output.getType() == VariableType.CONSTANT) {
						if (tassign.getOutput().getValue().equals(output.getValue())) {
							this.fsm_template_Init(or, annotatedState, tassign.getOutput(), check);
						}
					} else if (output.getType() == VariableType.RANGE) {
						if (output.getMin() <= Integer.parseInt(tassign.getOutput().getValue())
								&& output.getMax() >= Integer.parseInt(tassign.getOutput().getValue())) {
							this.fsm_template_Init(or, annotatedState, tassign.getOutput(), check);
						}
					}
				} else if (tassign.getOutput().getType() == VariableType.RANGE) {
					if (output.getType() == VariableType.CONSTANT) {
						if (tassign.getOutput().getMin() <= Integer.parseInt(output.getValue())
								&& tassign.getOutput().getMax() >= Integer.parseInt(output.getValue())) {
							tassign.getOutput().setType(VariableType.CONSTANT);
							tassign.getOutput().setMax(-1);
							tassign.getOutput().setMin(-1);
							tassign.getOutput().setValue(output.getValue());
							this.fsm_template_Init(or, annotatedState, tassign.getOutput(), check);
						}
					} else if (output.getType() == VariableType.RANGE) {
						boolean rangeCheck = false;
						if (output.getMin() <= tassign.getOutput().getMin()
								&& tassign.getOutput().getMax() <= output.getMax()) {
							rangeCheck = true;
						}
						if (output.getMin() >= tassign.getOutput().getMin()) {
							if (output.getMin() <= tassign.getOutput().getMax()) {
								tassign.getOutput().setMin(output.getMin());
								rangeCheck = true;
							}
						}
						if (output.getMax() <= tassign.getOutput().getMax()) {
							if (output.getMax() >= tassign.getOutput().getMin()) {
								tassign.getOutput().setMax(output.getMax());
								rangeCheck = true;
							}
						}
						if (rangeCheck) {
							this.fsm_template_Init(or, annotatedState, tassign.getOutput(), check);
						}
					}
				}
				if (!check) {
					stateName.add(annotatedState.getOrignState().getName());
				}
			}
		}
	}

	private void tts_template_Init(FaultTreeNode root, AnnotatedState annotatedState, Variable output, boolean check) {
		if (check) {
			for (FaultTreeNode fn : root.getChilds()) {
				if (fn.getText().contains(annotatedState.getOrignState().getName())) {
					FaultTreeNode and = fn.getChilds().get(0);
					FaultTreeNode transition = and.getChilds().get(1);
					FaultTreeNode or = transition.getChilds().get(0);
					FaultTreeNode enterState = or.getChilds().get(0);
					FaultTreeNode enterStateOr = enterState.getChilds().get(0);
					this.setTTSEnterTheState(annotatedState, output, enterStateOr);
					break;
				}
			}
		} else {
			FaultTreeNode out = new FaultTreeNode(
					annotatedState.getOrignState().getName() + "\n (out:=" + output.getValue() + ")");

			FaultTreeNode and = new FaultTreeNode("&", GateType.AND);
			root.addChild(out);
			out.addChild(and);
			out.setParent(root);
			and.setParent(out);
			FaultTreeNode tmp_state_at_t = new FaultTreeNode(
					makeFSM.getName() + "_state == " + annotatedState.getName() + " at t");
			tmp_state_at_t.setFormula(true);
			FaultTreeNode tmp_transition = new FaultTreeNode("transitions");
			and.addChild(tmp_state_at_t);
			and.addChild(tmp_transition);
			tmp_state_at_t.setParent(and);
			tmp_transition.setParent(and);
			FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
			tmp_transition.addChild(or);
			or.setParent(tmp_transition);
			or.setAbstract(true);
			FaultTreeNode tmp_Enter = new FaultTreeNode("Enter the state via state transition");
			or.addChild(tmp_Enter);
			tmp_Enter.setParent(or);
			FaultTreeNode tmp_Enter_or = new FaultTreeNode("|", GateType.OR);
			tmp_Enter.addChild(tmp_Enter_or);
			tmp_Enter_or.setParent(tmp_Enter);
			tmp_Enter_or.setAbstract(true);
			if (annotatedState.getPrevTransition() != null) {
				this.setTTSEnterTheState(annotatedState, output, tmp_Enter_or);
			}
			FaultTreeNode ttsRemain = this.setTTSRemainTheState(annotatedState, output);
			or.addChild(ttsRemain);
			ttsRemain.setParent(or);
		}
	}

	private FaultTreeNode setTTSEnterTheState(AnnotatedState annotatedState, Variable output, FaultTreeNode or) {
		FaultTreeNode tmp_and = new FaultTreeNode("&", GateType.AND);
		or.addChild(tmp_and);
		tmp_and.setParent(or);
		FaultTreeNode tmp_action = new FaultTreeNode("action");
		tmp_and.addChild(tmp_action);
		tmp_action.setParent(tmp_and);
		FaultTreeNode tmp_action_assignment = null;
		if (output.getType() == VariableType.RANGE) {
			tmp_action_assignment = new FaultTreeNode(
					"(A)" + output.getMin() + "<=" + makeFSM.getName() + "<=" + output.getMax());
		} else {
			tmp_action_assignment = new FaultTreeNode("(A)" + makeFSM.getName() + " = " + output.getValue());
		}
		tmp_action_assignment.setInput(true);
		tmp_action.addChild(tmp_action_assignment);
		tmp_action_assignment.setParent(tmp_action);
		FaultTreeNode tmp_tran = new FaultTreeNode("transition x -> " + annotatedState.getOrignState().getName());
		tmp_and.addChild(tmp_tran);
		tmp_tran.setParent(tmp_and);
		/*
		 * FaultTreeNode tmp_tran_or = new FaultTreeNode("|", GateType.OR);
		 * tmp_tran.addChild(tmp_tran_or); tmp_tran_or.setParent(tmp_tran);
		 */
		boolean origin_flag = false;
		for (AnnotatedState prevAnnotatedState : annotatedState.getPrevAS()) {
			FaultTreeNode tmp_tran_t_p;
			origin_flag = false;
			if (annotatedState.getPrevTransition().isTTS()) {
				for (FaultTreeNode fn : tmp_tran.getChilds()) {
					if (fn.getText()
							.equals(prevAnnotatedState.getOrignState().getName() + " for ["
									+ annotatedState.getPrevTransition().getTimeStart().getValue() + ", "
									+ annotatedState.getPrevTransition().getTimeEnd().getValue() + "]")) {
						origin_flag = true;
						break;
					}
				}
				if (!origin_flag) {
					tmp_tran_t_p = new FaultTreeNode(prevAnnotatedState.getOrignState().getName() + " for ["
							+ annotatedState.getPrevTransition().getTimeStart().getValue() + ", "
							+ annotatedState.getPrevTransition().getTimeEnd().getValue() + "]");
					tmp_tran_t_p.setFormula(true);
					tmp_tran.addChild(tmp_tran_t_p);
					tmp_tran_t_p.setParent(tmp_tran);
				}
			} else {
				for (FaultTreeNode fn : tmp_tran.getChilds()) {
					if (fn.getText().equals(prevAnnotatedState.getOrignState().getName() + " at t-p")) {
						origin_flag = true;
						break;
					}
				}
				if (!origin_flag) {
					tmp_tran_t_p = new FaultTreeNode(prevAnnotatedState.getOrignState().getName() + " at t-p");
					tmp_tran_t_p.setFormula(true);
					tmp_tran.addChild(tmp_tran_t_p);
					tmp_tran_t_p.setParent(tmp_tran);
				}
			}
		}
		FaultTreeNode tmp_tran_condition;
		String rawCondition = annotatedState.getPrevTransition().getAssignment().getConditions().get(0)
				.getRawCondition();
		if (annotatedState.getPrevTransition().isTTS()) {
			tmp_tran_condition = new FaultTreeNode(
					rawCondition + " for [" + annotatedState.getPrevTransition().getTimeStart().getValue() + ", "
							+ annotatedState.getPrevTransition().getTimeEnd().getValue() + "]");
		} else {
			tmp_tran_condition = new FaultTreeNode(rawCondition + " at t");
		}
		tmp_and.addChild(tmp_tran_condition);
		tmp_tran_condition.setParent(tmp_and);
		this.setConditionbyAction(annotatedState, output, tmp_tran_condition, rawCondition);
		return or;
	}

	private void setConditionbyAction(AnnotatedState annotatedState, Variable output, FaultTreeNode tmp_raw_condition,
			String condition) {
		String rawCondition = condition;
		String outputName = annotatedState.getAssignment().getOutput().getName();
		// k_, true, false �̸� ����
		if (!((output.getName().startsWith("k_") || output.getName().equals("true") || output.getName().equals("false")))) {
			rawCondition = lm.settingCondition(rawCondition, outputName);
		}
		FaultTreeNode tmp_conditions = lm.conditionToLogic(makeFSM, output, rawCondition);
		this.settingParent(tmp_conditions);
		Variable new_variable = lm.setChildOutput(output, annotatedState.getAssignment().getOutput(), makeFSM);
		boolean existPrevNode = false;
		for (Edge e : makeFSM.getEdges()) {
			if (ftanodelist.get(e.getSourceID()).getName().equals(new_variable.getName())) {
				existPrevNode = true;
				break;
			}
		}
		if (existPrevNode) {
			if (tmp_conditions.getChilds().size() == 0) {
				FaultTreeNode condition_and = new FaultTreeNode("&", GateType.AND);
				tmp_raw_condition.addChild(condition_and);
				condition_and.setParent(tmp_raw_condition);
				condition_and.setAbstract(true);
				condition_and.addChild(tmp_conditions);
				tmp_conditions.setParent(condition_and);
				FaultTreeNode new_condition = null;
				if (new_variable.getType() == VariableType.RANGE) {
					new_condition = new FaultTreeNode(
							new_variable.getMin() + "<=" + new_variable.getName() + "<=" + new_variable.getMax());
				} else {
					new_condition = new FaultTreeNode(new_variable.getName() + " = " + new_variable.getValue());
				}
				new_condition.setFormula(true);
				new_condition.setAbstract(true);
				new_condition.setVariable(new_variable);
				condition_and.addChild(new_condition);
				new_condition.setParent(condition_and);
			} else {
				tmp_raw_condition.addChild(tmp_conditions);
				tmp_conditions.setParent(tmp_raw_condition);
				FaultTreeNode new_condition = null;
				if (new_variable.getType() == VariableType.RANGE) {
					new_condition = new FaultTreeNode(
							new_variable.getMin() + " <= " + new_variable.getName() + " <= " + new_variable.getMax());
				} else {
					new_condition = new FaultTreeNode(new_variable.getName() + " = " + new_variable.getValue());
				}
				new_condition.setFormula(true);
				new_condition.setAbstract(true);
				new_condition.setVariable(new_variable);
				tmp_conditions.addChild(new_condition);
				new_condition.setParent(tmp_conditions);
			}
		} else {
			tmp_raw_condition.addChild(tmp_conditions);
			tmp_conditions.setParent(tmp_raw_condition);
		}
	}

	private FaultTreeNode setTTSRemainTheState(AnnotatedState annotatedState, Variable output) {
		FaultTreeNode tmp_Remain = new FaultTreeNode("Remain at the state because of\n disabled outgoing transition");
		FaultTreeNode tmp_and = new FaultTreeNode("&", GateType.AND);
		tmp_Remain.addChild(tmp_and);
		tmp_and.setParent(tmp_Remain);
		FaultTreeNode tmp_action = new FaultTreeNode("action");
		tmp_and.addChild(tmp_action);
		tmp_action.setParent(tmp_and);
		FaultTreeNode tmp_action_assignment = null;
		if (output.getType() == VariableType.RANGE) {
			tmp_action_assignment = new FaultTreeNode(
					"(A)" + output.getMin() + "<=" + makeFSM.getName() + "_t0" + "<=" + output.getMax());
		} else {
			tmp_action_assignment = new FaultTreeNode("(A)" + makeFSM.getName() + "_t0" + " = " + output.getValue());
		}
		tmp_action_assignment.setInput(true);
		tmp_action.addChild(tmp_action_assignment);
		tmp_action_assignment.setParent(tmp_action);
		for (Transition t : annotatedState.getNextTransition()) {
			FaultTreeNode tmp_tran = new FaultTreeNode(
					"not transition " + annotatedState.getOrignState().getName() + " -> " + t.getTargetName());
			tmp_and.addChild(tmp_tran);
			tmp_tran.setParent(tmp_and);
			if (t.isTTS()) {
				FaultTreeNode tmp_tran_or = new FaultTreeNode("|", GateType.OR);
				tmp_tran.addChild(tmp_tran_or);
				tmp_tran_or.setParent(tmp_tran);
				tmp_tran_or.setAbstract(true);
				// disabled �߰�
				FaultTreeNode tmp_disabled = new FaultTreeNode(
						"Remain at the state because of\n disabled outgoing transition");
				tmp_tran_or.addChild(tmp_disabled);
				tmp_disabled.setParent(tmp_tran_or);
				FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
				tmp_disabled.addChild(tmp_tran_and);
				tmp_tran_and.setParent(tmp_disabled);
				FaultTreeNode tmp_tran_t_p = new FaultTreeNode(annotatedState.getOrignState().getName() + " at t-p");
				/*
				 * FaultTreeNode tmp_tran_t_p = new
				 * FaultTreeNode(annotatedState.getOrignState().getName() +
				 * " for [" + t.getTimeStart().getValue() + ", " +
				 * t.getTimeEnd().getValue() + "]");
				 */
				tmp_tran_t_p.setFormula(true);
				FaultTreeNode tmp_tran_condition = new FaultTreeNode(
						"Exists not " + t.getAssignment().getConditions().get(0).getRawCondition() + " at t");
				/*
				 * FaultTreeNode tmp_tran_condition = new FaultTreeNode(
				 * "Exists not " +
				 * t.getAssignment().getConditions().get(0).getRawCondition() +
				 * " for [" + t.getTimeStart().getValue() + ", " +
				 * t.getTimeEnd().getValue() + "]");
				 */
				tmp_tran_and.addChild(tmp_tran_t_p);
				tmp_tran_and.addChild(tmp_tran_condition);
				tmp_tran_t_p.setParent(tmp_tran_and);
				tmp_tran_condition.setParent(tmp_tran_and);

				String notCondition = lm.notTranslation(t.getAssignment().getConditions().get(0).getRawCondition());
				FaultTreeNode notTranCondition = lm.conditionToLogic(makeFSM, output, notCondition);
				tmp_tran_condition.addChild(notTranCondition);
				notTranCondition.setParent(tmp_tran_condition);
				if (!isTimeTree) {
					// though �߰�
					FaultTreeNode tmp_though = new FaultTreeNode(
							"Remain at the state though enabled outgoing transition");
					tmp_tran_or.addChild(tmp_though);
					tmp_though.setParent(tmp_tran_or);
					FaultTreeNode tmp_tran_and_though = new FaultTreeNode("&", GateType.AND);
					tmp_though.addChild(tmp_tran_and_though);
					tmp_tran_and_though.setParent(tmp_though);

					FaultTreeNode tmp_tran_t_p_though = new FaultTreeNode(annotatedState.getOrignState().getName()
							+ " not for [" + t.getTimeStart().getValue() + ", " + t.getTimeEnd().getValue() + "]");
					tmp_tran_t_p_though.setFormula(true);

					FaultTreeNode tmp_tran_condition_though = new FaultTreeNode(
							t.getAssignment().getConditions().get(0).getRawCondition() + " not for ["
									+ t.getTimeStart().getValue() + ", " + t.getTimeEnd().getValue() + "]");
					tmp_tran_and_though.addChild(tmp_tran_t_p_though);
					tmp_tran_and_though.addChild(tmp_tran_condition_though);
					tmp_tran_t_p_though.setParent(tmp_tran_and_though);
					tmp_tran_condition_though.setParent(tmp_tran_and_though);

					FaultTreeNode doFaultTree = lm.conditionToLogic(makeFSM, output,
							t.getAssignment().getConditions().get(0).getRawCondition());
					tmp_tran_condition_though.addChild(doFaultTree);
					doFaultTree.setParent(tmp_tran_condition_though);
				} else {
					tmp_tran.addChild(tmp_disabled);
					tmp_disabled.setParent(tmp_tran);
					tmp_tran.getChilds().remove(0);
					isTimeTree = false;
				}
			} else {
				FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
				tmp_tran.addChild(tmp_tran_and);
				tmp_tran_and.setParent(tmp_tran);
				FaultTreeNode tmp_tran_t_p = new FaultTreeNode(annotatedState.getOrignState().getName() + " at t-p");
				tmp_tran_t_p.setFormula(true);
				FaultTreeNode tmp_tran_condition = new FaultTreeNode(
						"not " + t.getAssignment().getConditions().get(0).getRawCondition() + " at t");
				tmp_tran_and.addChild(tmp_tran_t_p);
				tmp_tran_and.addChild(tmp_tran_condition);
				tmp_tran_t_p.setParent(tmp_tran_and);
				tmp_tran_condition.setParent(tmp_tran_and);
				String notCondition = lm.notTranslation(t.getAssignment().getConditions().get(0).getRawCondition());
				//
				FaultTreeNode notTranCondition = lm.conditionToLogic(makeFSM, output, notCondition);
				tmp_tran_condition.addChild(notTranCondition);
				notTranCondition.setParent(tmp_tran_condition);
			}
		}
		return tmp_Remain;
	}

	public void tts_template(FaultTreeNode root, Variable output, TTS tts) {
		FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
		root.addChild(or);
		or.setParent(root);
		or.setAbstract(true);
		makeFSM = tts;
		ArrayList<String> stateName = new ArrayList<String>();
		boolean check = false;
		for (AnnotatedState annotatedState : tts.getAnnotatedStates()) {
			check = false;
			for (String name : stateName) {
				if (name.equals(annotatedState.getOrignState().getName())) {
					check = true;
					break;
				}
			}
			Assignment tassign = new Assignment(annotatedState.getAssignment());
			if (tassign.getOutput().getType() == VariableType.BOOL) {
				if (output.getType() == VariableType.BOOL) {
					if (tassign.getOutput().getValue().equals(output.getValue())) {
						this.tts_template_Init(or, annotatedState, tassign.getOutput(), check);
					}
				}
			} else if (tassign.getOutput().getType() == VariableType.CONSTANT) {
				if (output.getType() == VariableType.CONSTANT) {
					if (tassign.getOutput().getValue().equals(output.getValue())) {
						this.tts_template_Init(or, annotatedState, tassign.getOutput(), check);
					}
				} else if (output.getType() == VariableType.RANGE) {
					if (output.getMin() <= Integer.parseInt(tassign.getOutput().getValue())
							&& output.getMax() >= Integer.parseInt(tassign.getOutput().getValue())) {
						this.tts_template_Init(or, annotatedState, tassign.getOutput(), check);
					}
				}
			} else if (tassign.getOutput().getType() == VariableType.RANGE) {
				if (output.getType() == VariableType.CONSTANT) {
					if (tassign.getOutput().getMin() <= Integer.parseInt(output.getValue())
							&& tassign.getOutput().getMax() >= Integer.parseInt(output.getValue())) {
						tassign.getOutput().setType(VariableType.CONSTANT);
						tassign.getOutput().setMax(-1);
						tassign.getOutput().setMin(-1);
						tassign.getOutput().setValue(output.getValue());
						this.tts_template_Init(or, annotatedState, tassign.getOutput(), check);
					}
				} else if (output.getType() == VariableType.RANGE) {
					if (output.getMin() >= tassign.getOutput().getMin()) {
						tassign.getOutput().setMin(output.getMin());
						if (output.getMax() <= tassign.getOutput().getMax()) {
							tassign.getOutput().setMax(output.getMax());
							this.tts_template_Init(or, annotatedState, tassign.getOutput(), check);
						}
					} else {
						if (output.getMax() <= tassign.getOutput().getMax()) {
							tassign.getOutput().setMax(output.getMax());
							this.tts_template_Init(or, annotatedState, tassign.getOutput(), check);
						}
					}
				}
			}
			if (!check) {
				stateName.add(annotatedState.getOrignState().getName());
			}
		}
	}

	private void removeAndOr(FaultTreeNode input) {
		if (input.getParent().getText().equals("&") || input.getParent().getText().equals("|")) {
			input.getParent().setAbstract(false);
			return;
		}
		removeAndOr(input.getParent());
	}

	private void settingParent(FaultTreeNode ft) {
		for (FaultTreeNode tmp : ft.getChilds()) {
			tmp.setParent(ft);
			this.settingParent(tmp);
		}
	}

	public ArrayList<FaultTreeNode> getGroup() {
		return group;
	}

	public void setGroup(ArrayList<FaultTreeNode> group) {
		this.group = group;
	}

	public ArrayList<Variable> getTypetable() {
		return typetable;
	}

	public void setTypetable(ArrayList<Variable> typetable) {
		this.typetable = typetable;
	}
}