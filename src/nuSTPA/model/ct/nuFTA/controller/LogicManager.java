package nuSTPA.model.ct.nuFTA.controller;

import java.util.ArrayList;

import nuSTPA.model.ct.nuFTA.model.Assignment;
import nuSTPA.model.ct.nuFTA.model.Condition;
import nuSTPA.model.ct.nuFTA.model.FaultTreeNode;
import nuSTPA.model.ct.nuFTA.model.GateType;
import nuSTPA.model.ct.nuFTA.model.Node;
import nuSTPA.model.ct.nuFTA.model.Variable;
import nuSTPA.model.ct.nuFTA.model.VariableNode;
import nuSTPA.model.ct.nuFTA.model.VariableType;

public class LogicManager {
	private ArrayList<Variable> typetable;
	private boolean reconstruct = false;

	public LogicManager() {
	}

	/**
	 * @param typetable
	 * 			  set whole type table extracted by xml parsing
	 * 			  reconstruction value is false
	 */
	public LogicManager(ArrayList<Variable> typetable) {
		this.typetable = typetable;
		this.reconstruct = false;
	}

	/**
	 * @param conditionNode
	 *            generated based on condition divided by unit
	 * 
	 * 			  when reconstruction value is true -> if two FODs are used in calculation, change tree structure as & - f_a>f_b, f_a, f_b
	 * 			  when reconstruction value is false -> tree structure is generated as & - f_a>f_b - f_a, f_b
	 * @return return top node "&"
	 */
	//work based on restruct flag value
	private FaultTreeNode restructFaultTree(FaultTreeNode conditionNode) {
		FaultTreeNode and = new FaultTreeNode("&");
		and.setGateType(GateType.AND);
		and.addChild(conditionNode);
		conditionNode.setFormula(true);
		conditionNode.setInput(true);
		and.addChild(conditionNode.getChilds().get(0));
		and.addChild(conditionNode.getChilds().get(1));
		conditionNode.getChilds().remove(0);
		conditionNode.getChilds().remove(0);
		conditionNode.setVariable(null);
		this.reconstruct = false;

		return and;
	}

//	/**
//	 * @param conditionNode
//	 *            Condition�� �������� ������ Node
//	 * @param temp
//	 *            Condition Node�� �̸�
//	 * @param n
//	 *            �ش� FOD node strToVariable Method�� �ش� FOD ����� ���ú����� �ʿ��� �� �ֱ⶧����
//	 *            �����ϴ� �������� �޴´�.
//	 * 
//	 *            �� Method�� ��ü raw Condition�� �ּ� ������ ���� ���ڿ��� temp�� �̿��Ͽ�
//	 *            Condition FaultTreeNode�� �����ϰ�, FaultTree�� �Ʒ��� �� Ȯ���� �� �ֵ���
//	 *            strToVariable�� ȣ���ϴ� ������ �Ѵ�.
//	 */
//	/*
//	 * public void makeConditionNode(FaultTreeNode conditionNode, String temp,
//	 * Node n){ conditionNode = new FaultTreeNode(temp);
//	 * conditionNode.setVariable(strToVariable(conditionNode, temp,
//	 * (VariableNode) n)); conditionNode.setFormula(true); }
//	 */
	
	/**
	 * @param n
	 *            strToLogic�� ������ FOD Node�� ��ü
	 * @param output
	 *            ������ output
	 * @param rawCondition
	 *            �ش� output�� ������ condition���� ���ڿ��� &�� |�� ������ ��ü string
	 * 
	 *            ������ ���� conditionToLogic�� pseudo code�� ������� ����.
	 * 
	 *            gate, ��ȣ, condition unit���� ������ FaultTreeNode�� �����ϰ� list�� �߰��ϴ�
	 *            ����.
	 * 
	 * @return Condition��� ������ condition statements �κ��� root node�� ��ȯ�Ѵ�.
	 */
	public FaultTreeNode conditionToLogic(Node n, Variable output, String rawCondition) {
		ArrayList<FaultTreeNode> list = new ArrayList<FaultTreeNode>();
		int oldStart = 0;
		int count = 0;
		boolean type = false;
		FaultTreeNode conditionNode = null;
		if (output.getType() == VariableType.RANGE) {
			type = true;
		}
		for (int i = 0; i < rawCondition.length(); i++) {
			if (i == rawCondition.length() - 1) { // ���̻� ���ڿ��� �������� �ʴ� ���, ������
													// condition unit�� ���.
				String temp = rawCondition.substring(oldStart, i + 1);
				conditionNode = new FaultTreeNode(temp);
				conditionNode.setVariable(strToVariable(conditionNode, temp, (VariableNode) n));
				conditionNode.setFormula(true);
				if (this.reconstruct) { //if restruct flag is true, restruct fault tree(condition node)
					list.add(this.restructFaultTree(conditionNode));
				} else { // restruct flag is false, not restruct fault tree
					list.add(conditionNode);
				}
			} else if (rawCondition.charAt(i) == '&' || rawCondition.charAt(i) == '|') {
				// & �� | gate�� �����ٸ� �� ���������� condition unit���� ���� Fault Tree��
				// �����Ѵ�. ��, &�� |�� ���� FaultTreeNode�� �����Ͽ� List�� �߰��Ѵ�.
				if (oldStart < i) {
					String temp = rawCondition.substring(oldStart, i);
					conditionNode = new FaultTreeNode(temp);
					conditionNode.setVariable(strToVariable(conditionNode, temp, (VariableNode) n));
					conditionNode.setFormula(true);
					if (this.reconstruct) {
						list.add(this.restructFaultTree(conditionNode));
					} else {
						list.add(conditionNode);
					}
				}
				FaultTreeNode gate = new FaultTreeNode(rawCondition.charAt(i) + "");
				gate.setAbstract(true);
				//check gate type
				if (rawCondition.charAt(i) == '|')
					gate.setGateType(GateType.OR);
				else
					gate.setGateType(GateType.AND);
				list.add(gate); //add gate to the list
				oldStart = i + 1;
			} else if (rawCondition.charAt(i) == '(') { // ��ȣ�� ��� �յ� ��ȣ�� ���� �ٻ��ѹ�
														// conditionToLogic��
														// ȣ���Ѵ�.
				count++;
				for (int index = i + 1; index < rawCondition.length(); index++) {
					if (rawCondition.charAt(index) == '(')
						count++;
					else if (rawCondition.charAt(index) == ')')
						count--;
					if (count == 0) {
						FaultTreeNode bracket = conditionToLogic(n, output, rawCondition.substring(i + 1, index));
						list.add(bracket);
						i = index;
						oldStart = i + 1;
						break;
					}
				}
			}
			if (type) {
				// output�� ���� Ÿ���� ������ ���, condition���� �����Ǵ� ������ �̸��� output�� ������
				// ��, �� ���� ������ �����ϱ� ���� �κ�.
				try {
					if (output.getName().equals(conditionNode.getVariable().getName())) {
						if (output.getMax() < conditionNode.getVariable().getMax()) {
							conditionNode.getVariable().setMax(output.getMax());
							if (output.getMin() > conditionNode.getVariable().getMin()) {
								conditionNode.getVariable().setMin(output.getMin());
							}
						} else {
							if (output.getMin() > conditionNode.getVariable().getMin()) {
								conditionNode.getVariable().setMin(output.getMin());
							}
						}
					}
				} catch (NullPointerException ne) {

				}
			}
		}
		if (n instanceof VariableNode) {
			for (FaultTreeNode fn : list) {
				this.changeConstantToInteger(fn, (VariableNode) n);
				for (FaultTreeNode child : fn.getChilds()) {
					this.changeConstantToInteger(child, (VariableNode) n);
				}
			}
		}
		// ��ü list�� �����Ǿ��ٸ� ¦����°�� �������� Tree�� ���·� �����.
		for (int i = 0; i < list.size(); i++) {
			if (i % 2 != 0) {
				if (list.get(i - 1).getParent() == null) {
					list.get(i).addChild(list.get(i - 1));
					list.get(i - 1).setParent(list.get(i));
				} else {
					list.get(i).addChild(list.get(i - 2));
					list.get(i - 2).setParent(list.get(i));
				}
				if (list.size() > i + 1) {
					if (list.get(i + 1).getParent() == null) {
						list.get(i).addChild(list.get(i + 1));
						list.get(i + 1).setParent(list.get(i));
					}
				}
				count = i;
			}
		}
		merge(list, (VariableNode) n);
		return list.get(count);
	}

	private void changeConstantToInteger(FaultTreeNode root, VariableNode node) {
		for (Variable v : ((VariableNode) node).getVariables()) {
			if (root.getText().contains(v.getName()) && !v.getName().contains("t1") && !v.getName().contains("t0")) {
				root.setText(root.getText().replace(v.getName(), v.getValue()));
			}
		}
	}

	/**
	 * @param ftn
	 *            ���� Variable setting�� �ʿ��� FaultTreeNode output�� �����ϴ� �߰��� FOD
	 *            ������ �ǹ��Ѵ�. Input node�� �ֱⰡ ���Ե� �������� ������ ������
	 * @param temp
	 *            ��Ģ�������� �����ʿ� ������ ������ variable ��ü�� ���� ��.
	 * @param result
	 *            ��Ģ�������� ���ʿ� ������ ������ variable ��ü�� ���� ��.
	 * 
	 *            �� Method�� ��Ģ������ �� ������ ��� ������ ������ ��쿡 FaultTreeNode�� �� �� �� ���
	 *            �����Ѵ�. �̷��� ��Ģ������� �����ϰ� �ִ� FaultTreeNode�� ftn�� �ڽ����� ����ϴ� ������
	 *            ��ģ��.
	 */
	private void strHas2Var(FaultTreeNode ftn, Variable temp, Variable result) {
		this.reconstruct = true;
		FaultTreeNode rightNode = new FaultTreeNode(temp.getName());
		FaultTreeNode leftNode = new FaultTreeNode(result.getName());
		leftNode.setFormula(true);
		rightNode.setFormula(true);
		leftNode.setText(leftNode.getText());
		rightNode.setText(rightNode.getText());
		leftNode.setAbstract(true);
		rightNode.setAbstract(true);
		/*
		 * leftNode.setInput(true); rightNode.setInput(true);
		 */
		leftNode.setGateType(-1);
		rightNode.setGateType(-1);
		leftNode.setVariable(result);
		rightNode.setVariable(temp);
		ftn.addChild(leftNode);
		ftn.addChild(rightNode);
	}

	/**
	 * @param ftn
	 *            ������ FaultTreeNode
	 * @param text
	 *            ������ ���ڿ�.
	 * @param node
	 *            �ش� FOD ���
	 * @return ���ڿ��� ���� ������ FaultTreeNode�� �ʿ��� ������ ������ �ش�.
	 */
	public Variable strToVariable(FaultTreeNode ftn, String text, VariableNode node) {
		Variable result = null;
		String left = null;
		String right = null;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '>' || text.charAt(i) == '<') {
				if (text.charAt(i + 1) == '=') {
					left = text.substring(0, i);
					right = text.substring(i + 2, text.length());
				} else {
					left = text.substring(0, i);
					right = text.substring(i + 1, text.length());
				}
				if (left.matches(".*\\+.*|.*\\-.*")) {
					result = this.arithmeticExpr(left, node);
				} else {
					result = this.constantVar(left, node);
					ftn.setFormula(true);
				}
				if (right.startsWith("f_")) {
					Variable temp;
					if (right.matches(".*\\+.*|.*\\-.*")) {
						temp = this.arithmeticExpr(right, node);
					} else {
						temp = this.constantVar(right, node);
					}
					if (text.charAt(i) == '=') {
						result.setType(VariableType.CONSTANT);
						result.setValue(temp.getValue());
					} else if (text.charAt(i) == '>') {
						if (text.charAt(i + 1) == '=') {
							if (result.getType() == VariableType.RANGE) {
								if (!result.getValue().isEmpty()) {
									if (Integer.parseInt(temp.getValue()) != 0)
										result.setMin(Integer.parseInt(temp.getValue())
												- Integer.parseInt(result.getValue()));
									else {
										result.setMin(Integer.parseInt(temp.getValue()));
										result.setMax(result.getMax() - Integer.parseInt(result.getValue()));
									}
								} else {
									if (temp.getMax() < result.getMax()) {
										result.setMax(temp.getMax());
										if (temp.getMin() > result.getMin())
											result.setMin(temp.getMin());
									} else {
										if (temp.getMin() > result.getMin())
											result.setMin(temp.getMin());
									}
								}
								this.strHas2Var(ftn, temp, result);
							} else
								result.setMin(Integer.parseInt(temp.getValue()));
						} else {
							if (result.getType() == VariableType.RANGE) {
								if (!result.getValue().isEmpty()) {
									if (result.getMax() == 30000) {
										result.setMax(result.getMax() - Integer.parseInt(result.getValue()));
									}
									result.setMin(Integer.parseInt(temp.getValue())
											- Integer.parseInt(result.getValue()) + 1);
								} else {
									if (temp.getMax() < result.getMax()) {
										result.setMax(temp.getMax());
										if (temp.getMin() > result.getMin())
											result.setMin(temp.getMin());
									} else {
										if (temp.getMin() > result.getMin())
											result.setMin(temp.getMin());
									}
								}
								this.strHas2Var(ftn, temp, result);
							} else {
								result.setMin(Integer.parseInt(temp.getValue()) + 1);
							}
						}
					} else if (text.charAt(i) == '<') {
						if (text.charAt(i + 1) == '=') {
							if (result.getType() == VariableType.RANGE) {
								if (!result.getValue().isEmpty()) {
									if (!temp.getValue().isEmpty()) {
										result.setMax(Integer.parseInt(temp.getValue())
												- Integer.parseInt(result.getValue()));
									} else {
										result.setMax(Integer.parseInt(result.getValue()));
									}
								} else {
									if (temp.getMax() < result.getMax()) {
										result.setMax(temp.getMax());
										if (temp.getMin() > result.getMin())
											result.setMin(temp.getMin());
									} else {
										if (temp.getMin() > result.getMin())
											result.setMin(temp.getMin());
									}
								}
								this.strHas2Var(ftn, temp, result);
							} else {
								if (!temp.getValue().isEmpty())
									result.setMax(Integer.parseInt(temp.getValue()));
							}
						} else {
							if (result.getType() == VariableType.RANGE && !result.getValue().isEmpty())
								if (!temp.getValue().isEmpty()) {
									result.setMax(Integer.parseInt(temp.getValue())
											- Integer.parseInt(result.getValue()) - 1);
								} else {
									result.setMax(Integer.parseInt(result.getValue()) - 1);
								}
							else {
								if (!temp.getValue().isEmpty())
									result.setMax(Integer.parseInt(temp.getValue()) - 1);
							}
							this.strHas2Var(ftn, temp, result);
						}
					}
					result.setType(VariableType.RANGE);
					return result;
				} else if (right.matches(".*\\+.*|.*\\-.*")) {
					if (text.charAt(i) == '>') {
						Variable temp = this.arithmeticExpr(right, node);
						if (text.charAt(i + 1) == '=') {
							if (temp.getType() == VariableType.RANGE) {
								if (result.getMin() < (temp.getMax()))
									result.setMin(temp.getMax());
							} else {
								result.setMin(Integer.parseInt(temp.getValue()));
							}
						} else {
							if (temp.getType() == VariableType.RANGE) {
								if (result.getMin() < (temp.getMax() + 1))
									result.setMin(temp.getMax() + 1);
							} else {
								result.setMin(Integer.parseInt(temp.getValue()) + 1);
							}
						}
						return result;
					} else if (text.charAt(i) == '<') {
						Variable temp = this.arithmeticExpr(right, node);
						if (text.charAt(i + 1) == '=') {
							if (temp.getType() == VariableType.RANGE) {
								if (result.getMax() > temp.getMin())
									result.setMax(temp.getMin());
							} else {
								result.setMax(Integer.parseInt(temp.getValue()));
							}
						} else {
							if (temp.getType() == VariableType.RANGE) {
								if (result.getMax() > (temp.getMin() - 1))
									result.setMax(temp.getMin() - 1);
							} else {
								result.setMax(Integer.parseInt(temp.getValue()) - 1);
							}
						}
						return result;
					}
				} else if (right.startsWith("k_")) {
					return this.constCheck(right, result, node, i, text);
				} else if (right.startsWith("true") | right.startsWith("false")) {
					result.setType(VariableType.BOOL);
					result.setValue(right);
					return result;
				}
			} else if (text.charAt(i) == '!') {
				left = text.substring(0, i);
				left.replaceAll(".*\\!.*", "");
				right = text.substring(i + 2, text.length());

				if (left.matches(".*\\+.*|.*\\-.*")) {
					result = this.arithmeticExpr(left, node);
				} else {
					result = this.constantVar(left, node);
				}

				if (right.matches(".*\\+.*|.*\\-.*")) {
					result = this.rangeSetting(text, i, right, node, result);
				} else if (right.startsWith("k_")) {
					return this.constCheck(right, result, node, i, text);
				} else if (right.startsWith("true") | right.startsWith("false")) {
					result.setType(VariableType.BOOL);
					result.setValue(right);
					return result;
				}
			} else if (text.charAt(i) == '=') {
				left = text.substring(0, i);
				right = text.substring(i + 1, text.length());

				if (left.matches(".*\\+.*|.*\\-.*")) {
					result = this.arithmeticExpr(left, node);
				} else {
					result = this.constantVar(left, node);
				}

				if (right.matches(".*\\+.*|.*\\-.*")) {
					result = this.rangeSetting(text, i, right, node, result);
				} else if (right.startsWith("k_")) {
					return this.constCheck(right, result, node, i, text);
				} else if (right.startsWith("true") | right.startsWith("false")) {
					result.setType(VariableType.BOOL);
					result.setValue(right);
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * @param text
	 *            ������ ���ڿ�
	 * @param i
	 *            ���ڿ������� ��ġ
	 * @param right
	 *            ������ �ǿ�����
	 * @param node
	 *            �ش� FOD ���
	 * @param result
	 *            ���� �ǿ����ڸ� �̿��Ͽ� ������ Variable
	 * @return ������ �ǿ������� �̸��� �̿��Ͽ� Variable ��ü�� ����� �̸� result�� ������ ������� ��ȯ�Ѵ�.
	 */
	private Variable rangeSetting(String text, int i, String right, VariableNode node, Variable result) {
		if (text.charAt(i) == '>') {
			Variable temp = this.arithmeticExpr(right, node);
			result.setType(VariableType.RANGE);
			if (text.charAt(i + 1) == '=') {
				result.setMin(temp.getMax());
			} else {
				result.setMin(temp.getMax() + 1);
			}
		} else if (text.charAt(i) == '<') {
			Variable temp = this.arithmeticExpr(right, node);
			result.setType(VariableType.RANGE);
			if (text.charAt(i + 1) == '=') {
				result.setMax(temp.getMin());
			} else {
				result.setMax(temp.getMin() - 1);
			}
		}
		return result;
	}

	/**
	 * @param right
	 *            ������ �ǿ����� �̸�
	 * @param result
	 *            ���� �ǿ����ڸ� �̿��Ͽ� ������ Variable
	 * @param node
	 *            �ش� FOD ���
	 * @param i
	 *            ������ ���ڿ������� ��ġ
	 * @param text
	 *            ������ ���ڿ�
	 * @return ���� �ǿ����ڰ� ����� ��쿡, �̸� ���� �ǿ����ڷ� ���� Variable�� ���� ������� ��ȯ�Ѵ�.
	 */
	private Variable constCheck(String right, Variable result, VariableNode node, int i, String text) {
		Variable temp = this.constantVar(right, node);
		if (text.charAt(i) == '=') {
			result.setType(VariableType.CONSTANT);
			result.setValue(temp.getValue());
		} else if (text.charAt(i) == '>') {
			if (text.charAt(i + 1) == '=') {
				if (result.getType() == VariableType.RANGE && !result.getValue().isEmpty()) {
					/*
					 * if(Integer.parseInt(temp.getValue()) -
					 * Integer.parseInt(result.getValue()) < 0){
					 * result.setMin(0); }
					 */
					result.setMin(Integer.parseInt(temp.getValue()) + Integer.parseInt(result.getValue()));
				} else
					result.setMin(Integer.parseInt(temp.getValue()));
			} else {
				if (result.getType() == VariableType.RANGE && !result.getValue().isEmpty())
					result.setMin(Integer.parseInt(temp.getValue()) + Integer.parseInt(result.getValue()) + 1);
				else
					result.setMin(Integer.parseInt(temp.getValue()) + 1);
			}
		} else if (text.charAt(i) == '<') {
			if (text.charAt(i + 1) == '=') {
				if (result.getType() == VariableType.RANGE && !result.getValue().isEmpty())
					if (!temp.getValue().isEmpty()) {
						result.setMax(Integer.parseInt(temp.getValue()) + Integer.parseInt(result.getValue()));
					} else {
						result.setMax(Integer.parseInt(result.getValue()));
					}
				else
					result.setMax(Integer.parseInt(temp.getValue()));
			} else {
				if (result.getType() == VariableType.RANGE && !result.getValue().isEmpty())
					if (!temp.getValue().isEmpty()) {
						result.setMax(Integer.parseInt(temp.getValue()) + Integer.parseInt(result.getValue()) - 1);
					} else {
						result.setMax(Integer.parseInt(result.getValue()) - 1);
					}
				else
					result.setMax(Integer.parseInt(temp.getValue()) - 1);
			}
		}
		result.setType(VariableType.RANGE);
		return result;
	}

	/**
	 * @param text
	 *            ����� �̸�
	 * @param node
	 *            �ش� FOD ��尴ü�� �޾� �� �ȿ� �����ϴ� Variable���� list���� ã�� ����. ���ú������ �����ϸ�
	 *            ��. memoVar, prevStateVar ��
	 * @return ������� Variable ��ü�� ����� ��ȯ�Ѵ�.
	 */
	private Variable constantVar(String text, VariableNode node) {
		text = text.trim();
		Variable constvar = null;
		for (Variable v : node.getVariables()) {
			if (text.equals(v.getName())) {
				constvar = new Variable(v);
				return constvar;
			}
		}
		for (Variable v : this.typetable) {
			if (text.equals(v.getName())) {
				constvar = new Variable(v);
				return constvar;
			}
		}
		if (text.matches("true|false")) {
			constvar = new Variable(text, VariableType.BOOL, text);
		}
		return constvar;
	}

	/**
	 * @param text
	 *            ��Ģ������ �����ϴ� ���ڿ�
	 * @param node
	 *            �ش� FOD ��尴ü�� �޾� �� �ȿ� �����ϴ� Variable���� list���� ã�� ����. ���ú������ �����ϸ�
	 *            ��. memoVar, prevStateVar ��
	 * @return ��Ģ������ �����ϴ� ���ڿ� �� ����� ã�Ƴ��� Variable ��ü�� ����� ��ȯ�Ѵ�.
	 */
	private Variable findConstantInArithmeticExpr(String text, VariableNode node) {
		text = text.trim();
		Variable rtemp = null;
		int delimeter = 0;
		String right = null;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '+') {
				delimeter = i;
			} else if (text.charAt(i) == '-') {
				delimeter = i;
			}
		}
		right = text.substring(delimeter + 1, text.length());
		for (Variable v : this.typetable) {
			if (right.equals(v.getName())) {
				rtemp = new Variable(v);
				break;
			}
		}
		for (Variable v : node.getVariables()) {
			if (right.equals(v.getName())) {
				rtemp = new Variable(v);
				break;
			}
		}
		return rtemp;
	}

	/**
	 * @param text
	 *            ��Ģ������ �����ϴ� ���ڿ�
	 * @param node
	 *            �ش� FOD ��尴ü�� �޾� �� �ȿ� �����ϴ� Variable���� list���� ã�� ����. ���ú������ �����ϸ�
	 *            ��. memoVar, prevStateVar ��
	 * @return ��Ģ���� �� ������ �����ϴ� ��� e.g) f_a-k_lim �� ��� f_a�� ������ ������ ����� ������ ������
	 *         Variable ��ü�� �����Ͽ� ��ȯ�Ѵ�.
	 */
	private Variable arithmeticExpr(String text, VariableNode node) {
		text = text.trim();
		Variable ltemp = null;
		Variable rtemp = null;
		int delimeter = 0;
		String left = null;
		String right = null;
		boolean isPlus = false;

		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '+') {
				delimeter = i;
				isPlus = true;
			} else if (text.charAt(i) == '-') {
				delimeter = i;
				isPlus = false;
			}
		}
		// �������� ���� ���������� ���ڿ��� �и��Ѵ�.
		left = text.substring(0, delimeter);
		right = text.substring(delimeter + 1, text.length());

		// �������� ���ʰ� �����ʿ��� ����� �����ϴ��� ã�� �ش� ���� �����Ѵ�.
		for (Variable v : this.typetable) {
			if (left.equals(v.getName())) {
				ltemp = new Variable(v);
			} else if (right.equals(v.getName())) {
				rtemp = new Variable(v);
			}
		}

		for (Variable v : node.getVariables()) {
			if (left.equals(v.getName())) {
				ltemp = new Variable(v);
			} else if (right.equals(v.getName())) {
				rtemp = new Variable(v);
			}
		}

		// NuSCR�� ���ǵ� xml���� ������ ���ʿ� ������ �����ϰ� �ۼ��Ǿ� �־����Ƿ�, ������ ����, �������� ���ó�� ����Ͽ�
		// ������ ������ ���ڿ��� �ش��ϴ� ������ �����Ͽ� �� ���� ������ ������ ���� ���Ͽ� �����Ѵ�.
		if (ltemp.getType() == VariableType.CONSTANT) {
			if (rtemp.getType() == VariableType.CONSTANT) {
				// ������ ���ʰ� ������ �������� ���� ��� ����϶�, ������ ������ ���� �������� ���� ���ϰ� ���� �����
				// �����Ѵ�.
				if (isPlus) {
					ltemp.setValue(
							Integer.toString(Integer.parseInt(ltemp.getValue()) + Integer.parseInt(rtemp.getValue())));
				} else {
					ltemp.setValue(
							Integer.toString(Integer.parseInt(ltemp.getValue()) - Integer.parseInt(rtemp.getValue())));
				}
			} else if (rtemp.getType() == VariableType.RANGE) {
				// ������ ���, �������� ������ ��. ������ type�� range�� �����Ͽ� ������ ��������� �������� min,
				// max�� ���� ������ �����Ѵ�.
				ltemp.setType(VariableType.RANGE);
				if (isPlus) {
					ltemp.setMin(Integer.parseInt(ltemp.getValue()) + rtemp.getMin());
					ltemp.setMax(Integer.parseInt(ltemp.getValue()) + rtemp.getMax());
				} else {
					ltemp.setMin(rtemp.getMin() - Integer.parseInt(ltemp.getValue()));
					ltemp.setMax(rtemp.getMax() - Integer.parseInt(ltemp.getValue()));
				}
				ltemp.setValue(null);
			}
		} else if (ltemp.getType() == VariableType.RANGE) {
			if (rtemp.getType() == VariableType.CONSTANT) {
				// ������ ������ type�� �����̰� �������� constant�� ��, ó������ �ٸ� ó���Ϸ� �Ͽ�����, ����
				// output�� ���Ҷ� �ش� ������� �ʿ��ϴ� ��찡 �߻��Ѵ�. ���� ������ ���� �״�� �ΰ� ������� �ʴ�
				// value�� �������� ��������� �����Ѵ�.
				/*
				 * if (isPlus) { ltemp.setMin(ltemp.getMin() +
				 * Integer.parseInt(rtemp.getValue()));
				 * ltemp.setMax(ltemp.getMax() +
				 * Integer.parseInt(rtemp.getValue())); } else {
				 * ltemp.setMin(ltemp.getMin() -
				 * Integer.parseInt(rtemp.getValue()));
				 * ltemp.setMax(ltemp.getMax() -
				 * Integer.parseInt(rtemp.getValue())); }
				 */
				ltemp.setValue(rtemp.getValue());
			} else if (rtemp.getType() == VariableType.RANGE) {
				// ������ ������ Ÿ���� ��� �����϶� ������ ������ �������� ���������� �����Ѵ�.
				if (isPlus) {
					ltemp.setMin(rtemp.getMin() + ltemp.getMin());
					ltemp.setMax(rtemp.getMax() + ltemp.getMax());
				} else {
					ltemp.setMin(ltemp.getMin() - rtemp.getMax());
					ltemp.setMax(ltemp.getMax() - rtemp.getMin());
				}
			}
		}
		return ltemp;
	}

	/**
	 * @param list
	 *            subtree�� �����ϱ� ���� FaultTreeNode���� list
	 * @param n
	 *            �ش� subtree�� ������ VariableNoe
	 * 
	 *            list���� ¦����°�� �׻� gate�� �����Ѵ�. �̸� Ȯ���Ͽ� �¿��� ����̸��� ���Ͽ� �ϳ��� ��ġ��
	 *            ����غ��Ҵ�.
	 */
	public void merge(ArrayList<FaultTreeNode> list, VariableNode n) {
		for (int i = 0; i < list.size() - 2; i++) {
			try {
				if (i % 2 == 0 && list.get(i).getVariable().getType() == -1) {
					if (list.get(i).getVariable().getType() == VariableType.RANGE) {
						if (list.get(i).getVariable().getName().equals(list.get(i + 2).getVariable().getName())
								&& list.get(i + 1).getGateType() == GateType.AND) {
							if (list.get(i).getVariable().getMax() > list.get(i + 2).getVariable().getMax()) {
								list.get(i).getVariable().setMax(list.get(i + 2).getVariable().getMax());
								if (list.get(i).getVariable().getMin() < list.get(i + 2).getVariable().getMin()) {
									list.get(i).getVariable().setMin(list.get(i + 2).getVariable().getMax());
								}
							} else {
								if (list.get(i).getVariable().getMin() < list.get(i + 2).getVariable().getMin()) {
									list.get(i).getVariable().setMin(list.get(i + 2).getVariable().getMax());
								}
							}
							list.remove(i + 1);
							list.remove(i + 2);
						}
					}
				}
			} catch (NullPointerException ne) {

			}
		}

	}

	/**
	 * @param node
	 *            Condition�� ���� ��带 �����Ϸ��� FOD node (SDT�� �����.)
	 * @param output
	 *            ����ڰ� ������ fault Ȥ�� ���� FOD ���� ���� ������ output��
	 * @param tassign
	 *            Condition���� ������ �ִ� assignment ��ü
	 * 
	 *            output�� �����ϴ� assignment�� ã�Ƽ� �����ϱ� ������ �ش� assignment�� ��������
	 *            condition���� raw condition���� �ϳ��� �����Ͽ� conditionToLogic�� �����Ѵ�.
	 *            ���� �׳� �̾���� raw condition�� output�� �̸��� �����Ѵٸ�, variable setting��
	 *            �����Ѵ�.
	 * 
	 * @return SDT template�� condition statements �κ��� �����ϰ�, �� root�� ��ȯ�Ѵ�.
	 */
	public FaultTreeNode conditionGenerateNode(Node node, Variable output, Assignment tassign) {
		FaultTreeNode tRoot = new FaultTreeNode("conditions");
		String rawcond = "";
		for (int i = 0; i < tassign.getConditions().size(); i++) {
			Condition tcond = new Condition(tassign.getConditions().get(i).getRawCondition(),
					tassign.getConditions().get(i).isNot()); // condition�� ����
																// ���縦 �����Ѵ�.
			if (!tcond.isNot()) { // ���� Not ��ȯ�� �ʿ��ϴٸ�. not translation�� �����Ͽ�
									// ��ȯ�Ѵ�.
				tcond.setRawCondition(this.notTranslation(tcond.getRawCondition()));
				tcond.setNot(true); // ��ȯ���� not�� �̹� ����Ǿ��ٴ� ǥ�÷� true�� setting�Ѵ�.
			}
			rawcond += tcond.getRawCondition();
			if (i != tassign.getConditions().size() - 1)
				rawcond += "&";
		}
		FaultTreeNode temp = conditionToLogic(node, output, rawcond);
		tRoot.addChild(temp);

		if (rawcond.contains(output.getName()) && tassign.getOutput().getName() != null) {
			if (tassign.getOutput().getName().matches(".*\\+.*|.*\\-.*")) {
				temp.setVariable(this.arithmeticExpr(tassign.getOutput().getName(), (VariableNode) node));
			} else {
				temp.setVariable(this.constantVar(tassign.getOutput().getName(), (VariableNode) node));
			}
			temp.setFormula(true);
		}
		temp.setParent(tRoot);

		return tRoot;
	}

	public String notTranslation(String text) {
		String translateString = "";
		int check = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '=') {
				if (text.charAt(i - 1) != '<' && text.charAt(i - 1) != '>' && text.charAt(i - 1) != '!') {
					translateString += text.substring(check, i).trim();
					translateString += "!=";
					check = i + 1;
				}
			} else if (text.charAt(i) == '<') {
				translateString += text.substring(check, i).trim();
				if (text.charAt(i + 1) == '=') {
					translateString += ">";
					check = i + 2;
				} else {
					translateString += ">=";
					check = i + 1;
				}
			} else if (text.charAt(i) == '>') {
				translateString += text.substring(check, i).trim();
				if (text.charAt(i + 1) == '=') {
					translateString += "<";
					check = i + 2;
				} else {
					translateString += "<=";
					check = i + 1;
				}
			} else if (text.charAt(i) == '&') {
				translateString += text.substring(check, i).trim();
				translateString += "|";
				check = i + 1;
			} else if (text.charAt(i) == '|') {
				translateString += text.substring(check, i).trim();
				translateString += "&";
				check = i + 1;
			} else if (text.charAt(i) == '!') {
				translateString += text.substring(check, i).trim();
				translateString += text.substring(i + 1, i + 2).trim();
				check = i + 2;
			}
		}
		translateString += text.substring(check, text.length()).trim();
		return translateString;
	}

	/**
	 * @param output
	 *            ����ڰ� ������ fault Ȥ�� ���� FOD ���� ���� ������ fault tree�� output
	 * @param assignedoutput
	 *            assignment�� �����ϴ� output
	 * @param node
	 *            �ش� FOD node�� ��ü
	 * 
	 *            action�� output�� �̸��� action statement��� �� �� �ִ�. ���⿡ ��Ģ������ �����Ѵٸ�
	 *            arithmeticExpr method�� ���� �������� ���ʰ����� Variable�� �����Ѵ�. ����
	 *            findConstantInArithmeticExpr method�� ���� ��Ģ����Ŀ� ���Ե� ������� ã�ƿ´�.
	 * 
	 *            �� Method�� output���� ������ ���� assignment�� ǥ�õ� output���� ������ ���Ͽ�
	 *            �����ϴ� �κ��̴�.
	 * 
	 * @return action �κп� �����ϴ� FOD node�� �̸��� ã�� variable ��ü�� ����� ��ȯ�Ѵ�.
	 */
	public Variable setChildOutput(Variable output, Variable assignedoutput, VariableNode node) {
		Variable temp = null;
		String rawdata = assignedoutput.getName();

		if (rawdata.matches(".*\\+.*|.*\\-.*")) {
			temp = this.arithmeticExpr(rawdata, node);

			Variable constantvar = this.findConstantInArithmeticExpr(rawdata, node);

			if (output.getType() == VariableType.RANGE) {
				temp.setType(VariableType.RANGE);
				if (rawdata.contains("+")) {
					temp.setMin(assignedoutput.getMin() - Integer.parseInt(constantvar.getValue()));
					temp.setMax(assignedoutput.getMax() - Integer.parseInt(constantvar.getValue()));
				} else if (rawdata.contains("-")) {
					temp.setMin(assignedoutput.getMin() + Integer.parseInt(constantvar.getValue()));
					temp.setMax(assignedoutput.getMax() + Integer.parseInt(constantvar.getValue()));
				}
			} else {
				temp.setType(VariableType.CONSTANT);
				int outval = Integer.parseInt(output.getValue());
				if (rawdata.contains("+")) {
					outval -= Integer.parseInt(constantvar.getValue());
					temp.setValue("" + outval);
				} else if (rawdata.contains("-")) {
					outval += Integer.parseInt(constantvar.getValue());
					temp.setValue("" + outval);
				}
			}
		} else {
			temp = constantVar(rawdata, node);
			if (output.getType() == VariableType.RANGE) {
				temp.setType(VariableType.RANGE);
			} else if (output.getType() == VariableType.CONSTANT) {
				temp.setType(VariableType.CONSTANT);
				temp.setValue(output.getValue());
			} else if (output.getType() == VariableType.BOOL) {
				temp.setType(VariableType.BOOL);
				temp.setValue(output.getValue());
			}
		}

		return temp;
	}

	public String settingCondition(String rawCondition, String outputName) {
		String changeCondition = rawCondition;
		String[] tmp = rawCondition.split("&");
		for (String str : tmp) {
			if (str.contains(outputName)) {
				changeCondition = null;
				String tmp_outputName = outputName.replaceAll("\\_", "\\\\_").replaceAll("\\+", "\\\\+")
						.replaceAll("\\-", "\\\\-");
				rawCondition = rawCondition.replaceAll(tmp_outputName, ".");
				for (int i = 0; i < rawCondition.length(); i++) {
					if (rawCondition.charAt(i) == '.') {
						for (int j = i + 1; j < rawCondition.length(); j++) {
							if (rawCondition.charAt(j) == ')') {
								if (j + 1 == rawCondition.length()) {
									if (i - 1 != 0) {
										changeCondition = rawCondition.substring(0, i - 2);
										rawCondition = changeCondition;
										changeCondition = null;
									} else {
										rawCondition = "";
									}

								} else if (rawCondition.charAt(j + 1) == ')') {
									changeCondition = rawCondition.substring(0, i - 2);
									changeCondition += rawCondition.substring(j + 1, rawCondition.length());
									rawCondition = changeCondition;
									changeCondition = null;
									i = i - 2;
									for (int k = i - 2; k > 0; k--) {
										if (rawCondition.charAt(k) == '&' | rawCondition.charAt(k) == '|') {
											break;
										}
										if (rawCondition.charAt(k) == '(') {
											if (rawCondition.charAt(k - 1) == '(') {
												changeCondition = rawCondition.substring(0, k);
												changeCondition += rawCondition.substring(k + 1, i - 1);
												changeCondition += rawCondition.substring(i, rawCondition.length());
												rawCondition = changeCondition;
												changeCondition = null;
											}
										}
									}
								} else {
									if (i != 1) {
										changeCondition = rawCondition.substring(0, i - 1);
										changeCondition += rawCondition.substring(j + 2, rawCondition.length());
										i = i - 1;
										rawCondition = changeCondition;
										changeCondition = null;
										if (rawCondition.charAt(i) == '(' && rawCondition.charAt(i - 1) == '('
												&& rawCondition.charAt(j + 1) != '&'
												&& rawCondition.charAt(j + 1) != '|') {
											for (int k = j - 1; k >= i; k--) {
												if (rawCondition.charAt(k) == '&' | rawCondition.charAt(k) == '|') {
													break;
												}
												if (rawCondition.charAt(k) == '(') {
													if (rawCondition.charAt(k - 1) == '(') {
														changeCondition = rawCondition.substring(0, k);
														changeCondition += rawCondition.substring(k + 1, j + 1);
														changeCondition += rawCondition.substring(j + 2,
																rawCondition.length());
														rawCondition = changeCondition;
														changeCondition = null;
														i = i - 1;
														break;
													}
												}
											}
										}
									} else {
										changeCondition = rawCondition.substring(j + 2, rawCondition.length());
										rawCondition = changeCondition;
										changeCondition = null;
										i = 0;
									}
								}
								break;
							}
						}
					}
				}
			}
		}

		return rawCondition;
	}
}