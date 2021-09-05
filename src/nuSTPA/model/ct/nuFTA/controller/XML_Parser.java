package nuSTPA.model.ct.nuFTA.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nuSTPA.model.ct.nuFTA.model.Assignment;
import nuSTPA.model.ct.nuFTA.model.Condition;
import nuSTPA.model.ct.nuFTA.model.Edge;
import nuSTPA.model.ct.nuFTA.model.FSM;
import nuSTPA.model.ct.nuFTA.model.IONode;
import nuSTPA.model.ct.nuFTA.model.SDT;
import nuSTPA.model.ct.nuFTA.model.State;
import nuSTPA.model.ct.nuFTA.model.TTS;
import nuSTPA.model.ct.nuFTA.model.Transition;
import nuSTPA.model.ct.nuFTA.model.Variable;
import nuSTPA.model.ct.nuFTA.model.VariableNode;
import nuSTPA.model.ct.nuFTA.model.VariableType;
import nuSTPA.model.ct.nuFTA.parts.InputOutput;

public class XML_Parser {
	private HashMap<Integer, nuSTPA.model.ct.nuFTA.model.Node> ftanodelist = new HashMap<Integer, nuSTPA.model.ct.nuFTA.model.Node>();
	private ArrayList<Variable> typeTableList = new ArrayList<Variable>();
	private ArrayList<SDT> sdtlist = new ArrayList<SDT>();
	private ArrayList<FSM> fsmlist = new ArrayList<FSM>();
	private ArrayList<TTS> ttslist = new ArrayList<TTS>();
	private ArrayList<IONode> inputlist = new ArrayList<IONode>();
	private ArrayList<IONode> outputlist = new ArrayList<IONode>();
	private ArrayList<Edge> edgelist = new ArrayList<Edge>();
	private InputOutput nodeHierarchy = new InputOutput();

	public void openNuSCR(String url) {
		try {
			DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
			//create builder
			DocumentBuilder docBuilder = dbFac.newDocumentBuilder();
			//parse xml file into doc, with docBuilder
			Document doc = docBuilder.parse(url);
			NodeList node = doc.getElementsByTagName("FOD");
			Node root = node.item(1);
			NodeList type = doc.getElementsByTagName("TypeTable");
			Node typeTable = type.item(0);
			getTypeTable(typeTable);
			parser(root);
			linkedge();
			nodeHierarchy.setName("Root");
			InputOutput child = new InputOutput();
			NamedNodeMap attr = root.getAttributes();
			child.setName(attr.item(5).getNodeValue());
			nodeHierarchy.getChild().add(child);
			setNodeHierarchy(root,child);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public InputOutput getNodeHierarchy() {
		return nodeHierarchy;
	}

	public void setNodeHierarchy(InputOutput nodeHierarchy) {
		this.nodeHierarchy = nodeHierarchy;
	}

	private void setNodeHierarchy(Node list, InputOutput parent){
		NodeList child = list.getChildNodes();
		for (int i = 0; i < child.getLength(); i++) {
			if (child.item(i).getNodeName().equals("SDT")) {
				NamedNodeMap attr = child.item(i).getAttributes();
				String name = attr.item(5).getNodeValue();
				InputOutput nodeChild = new InputOutput();
				nodeChild.setName(name);
				//nodeChild.setId(Integer.parseInt(attr.item(3).getNodeValue()));
				parent.getChild().add(nodeChild);
			} else if (child.item(i).getNodeName().equals("FSM")) {
				NamedNodeMap attr = child.item(i).getAttributes();
				String name = attr.item(6).getNodeValue();
				InputOutput nodeChild = new InputOutput();
				nodeChild.setName(name);
				//nodeChild.setId(Integer.parseInt(attr.item(3).getNodeValue()));
				parent.getChild().add(nodeChild);
			} else if (child.item(i).getNodeName().equals("TTS")) {
				NamedNodeMap attr = child.item(i).getAttributes();
				String name = attr.item(6).getNodeValue();
				InputOutput nodeChild = new InputOutput();
				nodeChild.setName(name);
				//nodeChild.setId(Integer.parseInt(attr.item(3).getNodeValue()));
				parent.getChild().add(nodeChild);
			} else if (child.item(i).getNodeName().equals("FOD")) {
				InputOutput nodeChild = new InputOutput();
				NamedNodeMap attr = child.item(i).getAttributes();
				nodeChild.setName(attr.item(5).getNodeValue());
				parent.getChild().add(nodeChild);
				setNodeHierarchy(child.item(i), nodeChild);
			}else if (child.item(i).getNodeName().equals("nodes")){
				setNodeHierarchy(child.item(i), parent);
			}
		}
	}
	// TypeTable�� node �Ʒ����� ���� �о typetablelist ������ �߰��ϴ� �Լ�
	private void getTypeTable(Node typeTable) {
		NodeList entryList = typeTable.getChildNodes();
		for (int i = 0; i < entryList.getLength(); i++) {
			// node �̸��� entry�̸� ����.
			if (entryList.item(i).getNodeName().equals("entry")) {
				NamedNodeMap attr = entryList.item(i).getAttributes();
				String[] tempType = attr.item(1).getNodeValue().split(" : ");
				// type�� boolean���� �������� ���ϴ� ��. enum�� �߰��Ϸ��� ����ٰ� �߰��ϸ� �� ��
				if (tempType[1].equals("boolean")) {
					Variable tempVar = new Variable(attr.item(0).getNodeValue(), VariableType.BOOL, "true");
					typeTableList.add(tempVar);
				} else {
					String[] range = tempType[1].split("\\..");
					Variable tempVar = new Variable(attr.item(0).getNodeValue(), VariableType.RANGE,
							Integer.parseInt(range[1]), Integer.parseInt(range[0]));
					typeTableList.add(tempVar);
				}
			}
		}
	}

	private void setConstants(NamedNodeMap attr, VariableNode n, int index) {
		if (!attr.item(index).getNodeValue().isEmpty()) {
			String[] constants = attr.item(index).getNodeValue().split("; ");
			for (int i = 0; i < constants.length; i++) {
				String[] stringConstant = constants[i].split(" := ");
				Variable varConstant = new Variable(stringConstant[0], VariableType.CONSTANT,
						String.valueOf(stringConstant[1]));
				n.addVariable(varConstant);
			}
		}
	}

	private void setPrevStateVar(NamedNodeMap attr, VariableNode n, int index) {
		if (!attr.item(index).getNodeValue().isEmpty()) {
			String[] prevStateVar = attr.item(index).getNodeValue().split("; ");
			for (int j = 0; j < prevStateVar.length; j++) {
				Variable varPrev = null;
				String[] stringPrev = prevStateVar[j].split(" : ");
				if (stringPrev[1].equals("boolean")) {
					varPrev = new Variable(stringPrev[0], VariableType.BOOL, "true");
				} else {
					String[] range = stringPrev[1].split("\\..");
					varPrev = new Variable(stringPrev[0], VariableType.RANGE, Integer.parseInt(range[1]),
							Integer.parseInt(range[0]));
				}
				n.addVariable(varPrev);
			}
		}
	}

	private void setMemoVar(NamedNodeMap attr, VariableNode n, int index) {
		if (!attr.item(index).getNodeValue().isEmpty()) {
			String[] memoVar = attr.item(index).getNodeValue().split("; ");
			for (int i = 0; i < memoVar.length; i++) {
				Variable varMemo = null;
				String[] stringMemo = memoVar[i].split(" : ");
				if (stringMemo[1].equals("boolean")) {
					varMemo = new Variable(stringMemo[0], VariableType.BOOL, "true");
				} else {
					String[] range = stringMemo[1].split("\\..");
					varMemo = new Variable(stringMemo[0], VariableType.RANGE, Integer.parseInt(range[1]),
							Integer.parseInt(range[0]));
				}
				n.addVariable(varMemo);
			}
		}
	}

	private void setAllPrevStateVar(VariableNode node) {
		int flag[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
		Variable myVar = null;

		// typeTableList�� VariableNode�� �������� ��, Variable�� ������ ���� for��
		for (int i = 0; i < typeTableList.size(); i++) {
			if (typeTableList.get(i).getName().equals(node.getName())) {
				if (typeTableList.get(i).getType() == VariableType.BOOL) {
					myVar = new Variable(typeTableList.get(i).getName(), VariableType.CONSTANT, "true");
				} else {
					myVar = new Variable(typeTableList.get(i).getName(), VariableType.RANGE,
							typeTableList.get(i).getMax(), typeTableList.get(i).getMin());
				}
				break;
			}
		}

		//
		for (int i = 0; i < node.getVariables().size(); i++) {
			Variable tempVar = node.getVariable(i);
			for (int j = 0; j < flag.length; j++) {
				if (tempVar.getName().equals(node.getName() + "_t" + String.valueOf(j))) {
					flag[j] = 1;
				}
			}
		}

		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == 0) {
				Variable var = null;
				if (myVar.getType() == VariableType.BOOL) {
					var = new Variable(myVar.getName() + "_t" + String.valueOf(i), myVar.getType(), "true");
				} else {
					var = new Variable(myVar.getName() + "_t" + String.valueOf(i), myVar.getType(), myVar.getMax(),
							myVar.getMin());
				}
				node.addVariable(var);
			}
		}
	}

	private void setOutput(Variable output, String name, String temp, VariableNode node) {
		String[] checkType = temp.split("_");

		if (checkType[checkType.length - 1].startsWith("t")) {
			ArrayList<Variable> varList = node.getVariables();
			for (int i = 0; i < varList.size(); i++) {
				if (varList.get(i).getName().equals(temp)) {
					output.setName(name);
					output.setMax(varList.get(i).getMax());
					output.setMin(varList.get(i).getMin());
					output.setValue(varList.get(i).getValue());
					if (varList.get(i).getType() == VariableType.BOOL) {
						output.setType(VariableType.BOOL);
					} else if (varList.get(i).getType() == VariableType.RANGE) {
						output.setType(VariableType.RANGE);
					}
					break;
				}
			}
		} else if (checkType[0].startsWith("k")) {
			ArrayList<Variable> varList = node.getVariables();
			for (int i = 0; i < varList.size(); i++) {
				if (varList.get(i).getName().equals(temp)) {
					output.setName(name);
					output.setType(VariableType.CONSTANT);
					output.setMax(varList.get(i).getMax());
					output.setMin(varList.get(i).getMin());
					output.setValue(varList.get(i).getValue());
					break;
				}
			}
		} else {
			for (int i = 0; i < typeTableList.size(); i++) {
				if (typeTableList.get(i).getName().equals(temp)) {
					output.setName(name);
					output.setMax(typeTableList.get(i).getMax());
					output.setMin(typeTableList.get(i).getMin());
					output.setValue(typeTableList.get(i).getValue());
					if (typeTableList.get(i).getType() == VariableType.BOOL) {
						output.setType(VariableType.BOOL);
					} else if (typeTableList.get(i).getType() == VariableType.RANGE) {
						output.setType(VariableType.RANGE);
					}
					break;
				}
			}
		}
	}

	private Assignment setAssignment(String assignments, VariableNode node) {
		Variable output = new Variable("", 0, " ");
		String[] outputString = assignments.split(":=");

		int myMin = 0;
		int myMax = 0;
		for (int i = 0; i < typeTableList.size(); i++) {
			if (typeTableList.get(i).getName().equals(outputString[0])) {
				myMin = typeTableList.get(i).getMin();
				myMax = typeTableList.get(i).getMax();
			}
		}

		String temp = outputString[1];
		if (temp.equals("true") || temp.equals("false")) {
			output = new Variable(temp, 2, temp);
		} else {
			if (temp.matches(".*\\-.*")) {
				String[] minus = temp.split("-");
				setOutput(output, temp, minus[0], node);

				String[] minusSplit = minus[1].split("_");
				if (minusSplit[0].startsWith("k")) {
					ArrayList<Variable> varList = node.getVariables();
					for (int l = 0; l < varList.size(); l++) {
						if (varList.get(l).getName().equals(minus[1])) {
							output.setMax(output.getMax() - Integer.parseInt(varList.get(l).getValue()));
							output.setMin(output.getMin() - Integer.parseInt(varList.get(l).getValue()));
							if (output.getMin() < myMin) {
								output.setMin(myMin);
							}
							break;
						}
					}
				} else {
					ArrayList<Variable> varList = node.getVariables();
					for (int l = 0; l < varList.size(); l++) {
						if (varList.get(l).getName().equals(minus[1])) {
							output.setMax(output.getMax() - varList.get(l).getMin());
							output.setMin(output.getMin() - varList.get(l).getMax());
							if (output.getMin() < myMin) {
								output.setMin(myMin);
							}
							break;
						}
					}
				}
			} else if (temp.matches(".*\\+.*")) {
				String[] plus = temp.split("\\+");
				setOutput(output, temp, plus[0], node);

				String[] plusSplit = plus[1].split("_");
				if (plusSplit[0].startsWith("k")) {
					ArrayList<Variable> varList = node.getVariables();
					for (int l = 0; l < varList.size(); l++) {
						if (varList.get(l).getName().equals(plus[1])) {
							output.setMax(output.getMax() + Integer.parseInt(varList.get(l).getValue()));
							output.setMin(output.getMin() + Integer.parseInt(varList.get(l).getValue()));
							if (output.getMax() > myMax) {
								output.setMax(myMax);
							}
							break;
						}
					}
				} else {
					ArrayList<Variable> varList = node.getVariables();
					for (int l = 0; l < varList.size(); l++) {
						if (varList.get(l).getName().equals(plus[1])) {
							output.setMax(output.getMax() + varList.get(l).getMax());
							output.setMin(output.getMin() + varList.get(l).getMin());
							if (output.getMax() > myMax) {
								output.setMax(myMax);
							}
							break;
						}
					}
				}
				// }
			} else {
				setOutput(output, temp, temp, node);
			}
		}

		Assignment assignment = new Assignment(output);

		return assignment;
	}

	// assignment�� ����ǿ� assignment output ���� ���� ���� ������ ��� output ���� ������ �������ִ�
	// �Լ�.
	private void checkCondition(Assignment assignment, VariableNode node) {
		ArrayList<Condition> conditions = assignment.getConditions();
		Variable output = assignment.getOutput();

		for (int i = 0; i < conditions.size(); i++) {
			String[] temp = conditions.get(i).getRawCondition().split("&|\\|");

			for (int j = 0; j < temp.length; j++) {
				String condition = temp[j].replaceAll("\\)", "").replaceAll("\\(", "");
				if (condition.contains(output.getName())) {
					boolean flag = true;
					String[] split = null;
					if (condition.contains(">=")) {
						split = condition.split(">=");
					} else if (condition.contains("<=")) {
						split = condition.split("<=");
						flag = false;
					} else {
						break;
					}

					if (split[0].equals(output.getName())) {
						for (int k = 0; k < node.getVariables().size(); k++) {
							if (split[1].equals(node.getVariable(k).getName())) {
								if (flag) {
									if (assignment.getOutput().getMin() < Integer
											.parseInt(node.getVariable(k).getValue())) {
										assignment.getOutput().setMin(Integer.parseInt(node.getVariable(k).getValue()));
									}
								} else {
									if (assignment.getOutput().getMax() > Integer
											.parseInt(node.getVariable(k).getValue())) {
										assignment.getOutput().setMax(Integer.parseInt(node.getVariable(k).getValue()));
									}
								}
								break;
							}
						}
					}
				}
			}
		}
	}

	public void parser(Node list) {
		NodeList child = list.getChildNodes();
		for (int i = 0; i < child.getLength(); i++) {
			parser(child.item(i));
			if (child.item(i).getNodeName().equals("SDT")) {
				parseSDT(child.item(i));
			} else if (child.item(i).getNodeName().equals("FSM")) {
				parseFSM(child.item(i));
			} else if (child.item(i).getNodeName().equals("TTS")) {
				parseTTS(child.item(i));
			} else if (child.item(i).getNodeName().equals("input")) {
				parseInput(child.item(i));
			} else if (child.item(i).getNodeName().equals("output")) {
				parseOutput(child.item(i));
			} else if (child.item(i).getNodeName().equals("FOD")) {
				NodeList fodchild = child.item(i).getChildNodes();
				makeEdge(fodchild);
			}
		}
	}

	private void parseInput(Node inputnode) {
		NamedNodeMap inputInfo = inputnode.getAttributes();
		if (!inputInfo.item(0).getNodeValue().isEmpty()) {
			if (!inputInfo.item(1).getNodeValue().isEmpty()) {
				for (int i = 0; i < typeTableList.size(); i++) {
					// typeTableList�� ���� input ������ ������ ������ inputlist�� �߰��Ѵ�.
					if (inputInfo.item(1).getNodeValue().equals(typeTableList.get(i).getName())) {
						IONode input = new IONode(inputInfo.item(1).getNodeValue(),
								Integer.parseInt(inputInfo.item(0).getNodeValue()), true, typeTableList.get(i));
						// ftanodelist(��ü ��� ����Ʈ)�� input�� �������� ������ ftanode����Ʈ��
						// �߰��ϰ� inputlist���� ���� �߰��Ѵ�.
						if (ftanodelist.get(input.getId()) == null) {
							ftanodelist.put(input.getId(), (nuSTPA.model.ct.nuFTA.model.Node) input);
							inputlist.add(input);
						}
						break;
					}
				}
			}
		}
	}

	// input�� �Ȱ��� �˰������� �����Ѵ�.
	private void parseOutput(Node outputnode) {
		NamedNodeMap outputInfo = outputnode.getAttributes();
		if (!outputInfo.item(0).getNodeValue().isEmpty()) {
			if (!outputInfo.item(1).getNodeValue().isEmpty()) {
				for (int i = 0; i < typeTableList.size(); i++) {
					if (outputInfo.item(1).getNodeValue().equals(typeTableList.get(i).getName())) {
						IONode output = new IONode(outputInfo.item(1).getNodeValue(),
								Integer.parseInt(outputInfo.item(0).getNodeValue()), false, typeTableList.get(i));
						if (ftanodelist.get(output.getId()) == null) {
							ftanodelist.put(output.getId(), (nuSTPA.model.ct.nuFTA.model.Node) output);
							outputlist.add(output);
						}
						break;
					}
				}
			}
		}
	}

	private void parseSDT(Node sdtnode) {
		String conditionTable[][] = null;
		NamedNodeMap attr = sdtnode.getAttributes();
		SDT sdt = new SDT(attr.item(5).getNodeValue(), Integer.parseInt(attr.item(3).getNodeValue()));
		setConstants(attr, sdt, 1);
		setPrevStateVar(attr, sdt, 6);
		setMemoVar(attr, sdt, 4);
		setAllPrevStateVar(sdt); // setPrevStateVar�Լ��� ���ؼ� ������ ���� variable�� ������
									// t0~t12������ prevStateVar�� �����ϴ� �Լ�

		NodeList conac = sdtnode.getChildNodes();
		for (int i = 0; i < conac.getLength(); i++) {
			Node con = conac.item(i);
			if (con.getNodeName().equals("condition")) {
				NamedNodeMap rowCol = con.getAttributes();

				int nRow = Integer.parseInt(rowCol.item(1).getNodeValue());
				int nCol = Integer.parseInt(rowCol.item(0).getNodeValue());
				conditionTable = new String[nRow][nCol];

				NodeList cellList = con.getChildNodes();
				for (int j = 0; j < cellList.getLength(); j++) {
					Node cell = cellList.item(j);
					if (cell.getNodeName().equals("cell")) {
						NamedNodeMap value = cell.getAttributes();
						conditionTable[Integer.parseInt(value.item(1).getNodeValue())][Integer
								.parseInt(value.item(0).getNodeValue())] = value.item(2).getNodeValue();
					}
				}
			} else if (con.getNodeName().equals("action")) {
				NodeList cellList = con.getChildNodes();
				NamedNodeMap rowCol = con.getAttributes();
				int nRow = Integer.parseInt(rowCol.item(1).getNodeValue());
				int nCol = Integer.parseInt(rowCol.item(0).getNodeValue());

				String[][] actionTable = new String[nRow][nCol];

				for (int j = 0; j < cellList.getLength(); j++) {
					Node cell = cellList.item(j);
					if (cell.getNodeName().equals("cell")) {
						NamedNodeMap value = cell.getAttributes();
						actionTable[Integer.parseInt(value.item(1).getNodeValue())][Integer
								.parseInt(value.item(0).getNodeValue())] = value.item(2).getNodeValue();
					}
				}

				for (int j = 0; j < actionTable.length; j++) {
					for (int k = 1; k < actionTable[j].length; k++) {
						if (actionTable[j][k].equals("O") || actionTable[j][k].equals("o")) {
							Assignment assignment = setAssignment(actionTable[j][0].replace(" ", ""), sdt);
							for (int l = 0; l < conditionTable.length; l++) {
								Condition condition = new Condition(conditionTable[l][0].replaceAll(" ", ""), true);
								if (conditionTable[l][k].equals("F")) {
									condition.setNot(false);
								}
								assignment.addCondition(condition);
							}
							checkCondition(assignment, sdt);
							sdt.addAssignment(assignment);
						}
					}
				}
			}
		}

		ftanodelist.put(sdt.getId(), (nuSTPA.model.ct.nuFTA.model.Node) sdt);
		sdtlist.add(sdt);
	}

	private void parseFSM(Node fsmnode) {
		NamedNodeMap attr = fsmnode.getAttributes();

		FSM fsm = new FSM(attr.item(6).getNodeValue(), Integer.parseInt(attr.item(3).getNodeValue()));
		fsm.setInitialRefId(Integer.parseInt(attr.item(4).getNodeValue()));

		setConstants(attr, fsm, 1);
		setPrevStateVar(attr, fsm, 7);
		setMemoVar(attr, fsm, 5);
		setAllPrevStateVar(fsm);

		NodeList childnode = fsmnode.getChildNodes();
		Node states = null;
		Node transitions = null;
		for (int i = 0; i < childnode.getLength(); i++) {
			if (childnode.item(i).getNodeName().equals("states")) {
				states = childnode.item(i);
			} else if (childnode.item(i).getNodeName().equals("transitions")) {
				transitions = childnode.item(i);
			}
		}

		NodeList tempStates = states.getChildNodes();
		for (int i = 0; i < tempStates.getLength(); i++) {
			if (tempStates.item(i).getNodeName().equals("state")) {
				NamedNodeMap stateAttr = tempStates.item(i).getAttributes();
				State state = new State(stateAttr.item(1).getNodeValue(),
						Integer.parseInt(stateAttr.item(0).getNodeValue()));
				fsm.addState(state);
			}
		}

		NodeList tempTransitions = transitions.getChildNodes();
		for (int i = 0; i < tempTransitions.getLength(); i++) {
			if (tempTransitions.item(i).getNodeName().equals("transition")) {
				NamedNodeMap transitionAttr = tempTransitions.item(i).getAttributes();
				int id = Integer.parseInt(transitionAttr.item(0).getNodeValue());
				int sourceRefId = 0;
				int targetRefId = 0;
				Assignment assignment = null;
				Condition condition = null;

				NodeList transitionChild = tempTransitions.item(i).getChildNodes();
				for (int j = 0; j < transitionChild.getLength(); j++) {
					if (transitionChild.item(j).getNodeName().equals("source")) {
						NamedNodeMap sourceAttr = transitionChild.item(j).getAttributes();
						sourceRefId = Integer.parseInt(sourceAttr.item(0).getNodeValue());
					} else if (transitionChild.item(j).getNodeName().equals("target")) {
						NamedNodeMap targetAttr = transitionChild.item(j).getAttributes();
						targetRefId = Integer.parseInt(targetAttr.item(0).getNodeValue());
					} else if (transitionChild.item(j).getNodeName().equals("conditions")) {
						condition = new Condition(transitionChild.item(j).getTextContent().replaceAll("\n", "").replaceAll(" ", ""), false);
					} else if (transitionChild.item(j).getNodeName().equals("assignments")) {
						assignment = setAssignment(transitionChild.item(j).getTextContent().replaceAll("\n", "").replaceAll(" ", ""), fsm);
						assignment.addCondition(condition);
						checkCondition(assignment, fsm);
					}
				}

				Transition transition = new Transition(id, targetRefId, assignment, false);
				for (int j = 0; j < fsm.getStates().size(); j++) {
					if (targetRefId == fsm.getStates().get(j).getId()) {
						transition.setTargetName(fsm.getStates().get(j).getName());
					}
				}
				for (int j = 0; j < fsm.getStates().size(); j++) {
					if (sourceRefId == fsm.getStates().get(j).getId()) {
						fsm.getStates().get(j).addTransition(transition);
					}
				}
			}
		}
		ftanodelist.put(fsm.getId(), (nuSTPA.model.ct.nuFTA.model.Node) fsm);
		fsmlist.add(fsm);
	}

	private void parseTTS(Node ttsnode) {
		NamedNodeMap attr = ttsnode.getAttributes();

		TTS tts = new TTS(attr.item(6).getNodeValue(), Integer.parseInt(attr.item(3).getNodeValue()));
		tts.setInitialRefId(Integer.parseInt(attr.item(4).getNodeValue()));

		setConstants(attr, tts, 1);
		setPrevStateVar(attr, tts, 7);
		setMemoVar(attr, tts, 5);
		setAllPrevStateVar(tts);

		if (!attr.item(0).getNodeValue().equals("")) {
			String[] split = attr.item(0).getNodeValue().replaceAll(";", "").replaceAll(" ", "").split(":");
			String[] range = split[1].split("\\..");
			Variable clock = new Variable(split[0], VariableType.RANGE, Integer.parseInt(range[1]),
					Integer.parseInt(range[0]));
			tts.setClock(clock);
		}

		NodeList childnode = ttsnode.getChildNodes();
		Node states = null;
		Node transitions = null;
		for (int i = 0; i < childnode.getLength(); i++) {
			if (childnode.item(i).getNodeName().equals("states")) {
				states = childnode.item(i);
			} else if (childnode.item(i).getNodeName().equals("transitions")) {
				transitions = childnode.item(i);
			}
		}

		NodeList tempStates = states.getChildNodes();
		for (int i = 0; i < tempStates.getLength(); i++) {
			if (tempStates.item(i).getNodeName().equals("state")) {
				NamedNodeMap stateAttr = tempStates.item(i).getAttributes();
				State state = new State(stateAttr.item(1).getNodeValue(),
						Integer.parseInt(stateAttr.item(0).getNodeValue()));
				tts.addState(state);
			}
		}

		NodeList tempTransitions = transitions.getChildNodes();
		for (int i = 0; i < tempTransitions.getLength(); i++) {
			if (tempTransitions.item(i).getNodeName().equals("transition")) {
				NamedNodeMap transitionAttr = tempTransitions.item(i).getAttributes();
				int id = Integer.parseInt(transitionAttr.item(0).getNodeValue());
				int sourceRefId = 0;
				int targetRefId = 0;
				Variable timeStart = null;
				Variable timeEnd = null;
				Assignment assignment = null;
				Condition condition = null;
				boolean isTTS = false;

				NodeList transitionChild = tempTransitions.item(i).getChildNodes();
				for (int j = 0; j < transitionChild.getLength(); j++) {
					if (transitionChild.item(j).getNodeName().equals("source")) {
						NamedNodeMap sourceAttr = transitionChild.item(j).getAttributes();
						sourceRefId = Integer.parseInt(sourceAttr.item(0).getNodeValue());
					} else if (transitionChild.item(j).getNodeName().equals("target")) {
						NamedNodeMap targetAttr = transitionChild.item(j).getAttributes();
						targetRefId = Integer.parseInt(targetAttr.item(0).getNodeValue());
					} else if (transitionChild.item(j).getNodeName().equals("time")) {
						NamedNodeMap timeAttr = transitionChild.item(j).getAttributes();
						isTTS = true;
						if (timeAttr.item(1).getNodeValue().equals("0")) {
							timeStart = new Variable("timeStart", VariableType.CONSTANT, "0");
						}
						ArrayList<Variable> varList = tts.getVariables();
						for (int k = 0; k < varList.size(); k++) {
							if (varList.get(k).getName().equals(timeAttr.item(0).getNodeValue())) {
								timeEnd = varList.get(k);
							}
							if (varList.get(k).getName().equals(timeAttr.item(1).getNodeValue())) {
								timeStart = varList.get(k);
							}
						}
					} else if (transitionChild.item(j).getNodeName().equals("conditions")) {
						condition = new Condition(
								transitionChild.item(j).getTextContent().replaceAll("\n", "").replaceAll(" ", ""),
								false);
					} else if (transitionChild.item(j).getNodeName().equals("assignments")) {
						assignment = setAssignment(
								transitionChild.item(j).getTextContent().replaceAll("\n", "").replaceAll(" ", ""), tts);
						assignment.addCondition(condition);
						checkCondition(assignment, tts);
					}
				}

				Transition transition = new Transition(id, targetRefId, assignment, isTTS, timeStart, timeEnd);
				for (int j = 0; j < tts.getStates().size(); j++) {
					if (targetRefId == tts.getStates().get(j).getId()) {
						transition.setTargetName(tts.getStates().get(j).getName());
					}
				}
				for (int j = 0; j < tts.getStates().size(); j++) {
					if (sourceRefId == tts.getStates().get(j).getId()) {
						tts.getStates().get(j).addTransition(transition);
					}
				}
			}
		}
		ftanodelist.put(tts.getId(), (nuSTPA.model.ct.nuFTA.model.Node) tts);
		ttslist.add(tts);
	}

	private void linkedge() {
		for (Edge e : this.edgelist) {
			boolean flag = false;
			for (IONode input : this.inputlist) {
				if (e.getDestination_id() == input.getId()) {
					input.addEdge(e);
					flag = true;
					break;
				}
			}
			if (flag)
				continue;
			for (IONode output : this.outputlist) {
				if (e.getDestination_id() == output.getId()) {
					output.addEdge(e);
					flag = true;
					break;
				}
			}
			if (flag)
				continue;
			for (SDT sdt : this.sdtlist) {
				if (e.getDestination_id() == sdt.getId()) {
					sdt.addEdge(e);
					flag = true;
					break;
				}
			}
			if (flag)
				continue;
			for (FSM fsm : this.fsmlist) {
				if (e.getDestination_id() == fsm.getId()) {
					fsm.addEdge(e);
					flag = true;
					break;
				}
			}
			if (flag)
				continue;
			for (TTS tts : this.ttslist) {
				if (e.getDestination_id() == tts.getId()) {
					tts.addEdge(e);
					break;
				}
			}
		}
	}

	private void makeEdge(NodeList fodchilds) {
		// FOD�� �ƴ� transition������ �������� ��� transition�� ���� ����, source�� target ������
		// �����Ѵ�.
		// �� ������ edge�� ǥ���Ǹ� edgelist�� edge������ �� ����ȴ�.
		// edge�� ����Ǵ� ������ source, target�� �̸��� id�� ����ȴ�.
		// FOD���� node�� ���� transition �Ӹ��� �ƴ� ��� ���ο� ����Ǿ� �ִ� state�鰣�� transition��
		// ����ȴ�.
		// state�鰣�� transition�� ������ �� source, target�� �̸��� id�� ����ȴ�. �ٸ� ������ �������
		// �ʴ´�.
		int count = 0;
		for (int i = 0; i < fodchilds.getLength(); i++) {
			if (fodchilds.item(i).getNodeName().equals("FOD")) {
				count++;
				makeEdge(fodchilds.item(i).getChildNodes());
			}
		}
		if (count == 0) {
			for (int i = 0; i < fodchilds.getLength(); i++) {
				if (fodchilds.item(i).getNodeName().equals("transitions")) {
					NodeList transitions = fodchilds.item(i).getChildNodes();
					for (int j = 0; j < transitions.getLength(); j++) {
						if (transitions.item(j).getNodeName().equals("transition")) {
							NodeList transition = transitions.item(j).getChildNodes();
							Edge edge = new Edge();
							for (int k = 0; k < transition.getLength(); k++) {
								Node sourcedest = transition.item(k);
								if (sourcedest.getNodeName().equals("source")) {
									NamedNodeMap sourceAttr = sourcedest.getAttributes();
									edge.setSourceID(Integer.parseInt(sourceAttr.item(0).getNodeValue()));
								} else if (sourcedest.getNodeName().equals("target")) {
									NamedNodeMap destinationAttr = sourcedest.getAttributes();
									edge.setDestination_id(Integer.parseInt(destinationAttr.item(0).getNodeValue()));
								}
							}
							edgelist.add(edge);
						}
					}
				}
			}
		}
	}

	public ArrayList<SDT> getSdtlist() {
		return sdtlist;
	}

	public void setSdtlist(ArrayList<SDT> sdtlist) {
		this.sdtlist = sdtlist;
	}

	public ArrayList<FSM> getFsmlist() {
		return fsmlist;
	}

	public void setFsmlist(ArrayList<FSM> fsmlist) {
		this.fsmlist = fsmlist;
	}

	public ArrayList<IONode> getInputlist() {
		return inputlist;
	}

	public void setInputlist(ArrayList<IONode> inputlist) {
		this.inputlist = inputlist;
	}

	public ArrayList<IONode> getOutputlist() {
		return outputlist;
	}

	public void setOutputlist(ArrayList<IONode> outputlist) {
		this.outputlist = outputlist;
	}

	public ArrayList<Edge> getEdgelist() {
		return edgelist;
	}

	public void setEdgelist(ArrayList<Edge> edgelist) {
		this.edgelist = edgelist;
	}

	public ArrayList<Variable> getTypeTableList() {
		return typeTableList;
	}

	public void setTypeTableList(ArrayList<Variable> typeTableList) {
		this.typeTableList = typeTableList;
	}

	public ArrayList<TTS> getTtslist() {
		return ttslist;
	}

	public void setTtslist(ArrayList<TTS> ttslist) {
		this.ttslist = ttslist;
	}

	public HashMap<Integer, nuSTPA.model.ct.nuFTA.model.Node> getFtanodelist() {
		return ftanodelist;
	}

	public void setFtanodelist(HashMap<Integer, nuSTPA.model.ct.nuFTA.model.Node> ftanodelist) {
		this.ftanodelist = ftanodelist;
	}
}