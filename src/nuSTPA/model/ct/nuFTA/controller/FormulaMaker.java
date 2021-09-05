package nuSTPA.model.ct.nuFTA.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import nuSTPA.model.ct.nuFTA.model.CutsetNode;
import nuSTPA.model.ct.nuFTA.model.FaultTreeNode;
import nuSTPA.model.ct.nuFTA.model.GateType;
import nuSTPA.model.ct.nuFTA.model.Variable;
import nuSTPA.model.ct.nuFTA.model.VariableType;

public class FormulaMaker {
	private int count = 0;
	private ArrayList<CutsetNode> cnList;
	private HashMap<Integer, String> cutsetString = new HashMap<Integer, String>();
	private int csCount = 0;

	/**
	 * cutsetList �Լ��� Fault Tree�� �̿��Ͽ� Minimal Cut Sets�� �����ִ� �Լ��̴�.
	 * 
	 * @param root
	 *            : ��ü Fault Tree�� �ֻ��� root ��� �̴�.
	 * @return parent : Minimal Cut Sets�� ���ϱ� ���� �����ߴ� CutsetNode�� ���� �ȿ� �ִ�
	 *         ArrayList<String>�� ��ü Minimal Cut Set�� �ִ�.
	 */
	public CutsetNode cutsetList(FaultTreeNode root) {
		cutsetString.put(csCount, root.getText());
		CutsetNode parent = new CutsetNode(Integer.toString(csCount));
		csCount++;
		cnList = new ArrayList<CutsetNode>();
		cnList.add(parent);
		this.makeCutsetList(root, parent);
		this.getAllAbstractNode(root);
		this.makeMinimalCutsets(root);
		return parent;
	}

	/**
	 * getAllMinimalCutsets �Լ��� FormulaMaker �Լ����� ������ �ִ� subtree�� �ֻ��� ����� ������
	 * ArrayList<CutsetNode> cnList�� �̿��Ͽ� ��ü Minimal Cut Sets �� ���ϴ� �Լ��̴�.
	 */
	private void getAllMinimalCutsets() {
		CutsetNode cn;
		while (cnList.size() > 1) {
			for (int i = cnList.size() - 1; i > 0; i--) {
				cn = cnList.get(i);
				if (cn.getCutsetChilds().size() == 0) {
					for (CutsetNode parent : cn.getCutsetParents()) {
						this.cardisionProduct(parent.getCutsets(), cn);
						this.getMinimalCutset(parent.getCutsets());
						parent.getCutsetChilds().remove(cn);
					}
					cn.getCutsets().clear();
					cn.setCutsets(null);
					cnList.remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * cardisionProduct �Լ��� �ϳ��� Subtree(A)�� Minimal Cut Set �ȿ� �ٸ� Subtree(B)��
	 * root ��尡 �� ���� ��� Minimal Cut Set�ȿ� �ִ� node�� ������ ����� B�� Minimal Cut Set
	 * ������ �߰��ϴ� �Լ��̴�.
	 * 
	 * @param ArrayList<String>
	 *            cutset : �θ� Subtree�� ��ü Minimal Cut Set�� ����Ʈ�̴�. CutsetNode cn
	 *            : �ڽ� Subtree�� �ֻ��� root ��� �̴�.
	 * @return parent
	 */
	private void cardisionProduct(ArrayList<String> cutset, CutsetNode cn) {
		String[] cause;
		String cnText = cn.getText();
		String tmp = "";
		for (int i = 0; i < cutset.size(); i++) {
			cause = cutset.get(i).split("&");
			for (int j = 0; j < cause.length; j++) {
				if (cause[j].equals(cnText)) {
					cutset.remove(i);
					i--;
					for (String tmpNode : cn.getCutsets()) {
						tmp = "";
						cause[j] = tmpNode;
						for (String str : cause) {
							tmp += str + "&";
						}
						tmp = tmp.substring(0, tmp.length() - 1);
						cutset.add(tmp);
					}
					break;
				}
			}
		}
		cause = null;
		tmp = null;
		cnText = null;
	}

	/**
	 * makeMinimalCutsets �Լ��� ��ü Minimal Cut Set�� ������ �Ŀ� ���Ͽ� �����ϴ� �Լ��̴�.
	 * 
	 * @param root
	 *            : ��ü Fault Tree�� �ֻ��� root ��� �̴�.
	 */
	private void makeMinimalCutsets(FaultTreeNode root) {
		FileWriter fWriter;
		try {
			String fileName = root.getText();
			if (fileName.contains("for [")) {
				for (int i = 0; i < fileName.length(); i++) {
					if (fileName.charAt(i) == '[') {
						fileName = fileName.substring(i, fileName.length());
						break;
					}
				}
				fileName += MainController.getInstance().getPeriod() + " Cycle Time";
			}
			fWriter = new FileWriter(fileName + " cardisionProduct Minimal Cutsets.txt", true);
			for (CutsetNode cn : cnList) {
				CutsetNode tree = new CutsetNode();
				makeCutsetTree(cn, tree);
				this.getCutsetList(cn, tree);
				tree = null;
				this.getMinimalCutset(cn.getCutsets());
			}
			this.getAllMinimalCutsets();
			this.printAllCutset(fWriter);
			fWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * getMinimalCutset �Լ��� ��ü cut set�� �̿��Ͽ� minimal cut set�� ���ϴ� �Լ�
	 * 
	 * @param cutsets
	 *            class���� ������ �ִ� �����μ� ���ڷ� �ѱ��� �ʰ� �׳� ����Ѵ�.
	 */
	private void getMinimalCutset(ArrayList<String> cutsets) {
		String[] tmp; // ����
		String[] cmp; // �񱳴��
		boolean mcsCheck = false; // ������ �Ǿ��ֳ� Ȯ��
		for (int i = 0; i < cutsets.size(); i++) {
			tmp = cutsets.get(i).split("&"); // ������ ����
			for (int j = i + 1; j < cutsets.size(); j++) { // ������ ��ü�� ��
				cmp = cutsets.get(j).split("&"); // �񱳴�� ����
				if (tmp.length > cmp.length) { // ������ �񱳴�󺸴� Ŭ ���
					mcsCheck = true;
					for (String cmpStr : cmp) { // ������ �񱳴���� �����ϴ��� Ȯ��
						for (String standStr : tmp) {
							if (cmpStr.equals(standStr)) {
								mcsCheck = true;
								break;
							} else {
								mcsCheck = false;
							}
						}
						if (!mcsCheck) {
							break;
						}
					}
					if (mcsCheck) { // ������ �� ����� �����ϴ� ���
						cutsets.remove(i); // ������ minimal cutset���� ������
						i--; // ������ ���� ������ �ٽ� ������ ����
						break;
					}
				} else { // ������ �񱳴�󺸴� �۰ų� ���� ���
					mcsCheck = true;
					for (String standStr : tmp) { // �񱳴���� ������ �����ϴ��� Ȯ��
						for (String cmpStr : cmp) {
							if (standStr.equals(cmpStr)) {
								mcsCheck = true;
								break;
							} else {
								mcsCheck = false;
							}
						}
						if (!mcsCheck) {
							break;
						}
					}
					if (mcsCheck) { // �� ����� ������ �����ϴ� ���
						cutsets.remove(j); // �񱳴���� minimal cutset ���� ����
						j--; // ���� �񱳴������ �ٽ� �񱳽���.
					}
				}
			}
		}
		tmp = null;
		cmp = null;
	}

	/**
	 * settingCNList �Լ��� subtree�� �ϳ��� Cut Set�� �̿��Ͽ� �ٸ� Subtree���� ���踦 �����ϴ� �Լ��̴�.
	 * 
	 * @param CutsetNode
	 *            cn : cn�� ���� Subtree�� �ֻ��� root ����̴�. String cutset : ����
	 *            Subtree�� �ϳ��� Cut set�̴�.
	 */
	private void settingCNList(CutsetNode cn, String cutset) {
		String[] cutsetArray = cutset.split("&");
		boolean check = false;
		for (CutsetNode parent : cnList) {
			check = false;
			for (CutsetNode p : cn.getCutsetChilds()) {
				if (p.getText().equals(parent.getText())) {
					check = true;
					break;
				}
			}
			if (!check) {
				for (String tmp : cutsetArray) {
					if (tmp.equals(parent.getText())) {
						parent.addCutsetParents(cn);
						cn.addCutsetChilds(parent);
						break;
					}
				}
			}
		}
	}

	private void getCutsetList(CutsetNode cn, CutsetNode tree) {
		if (tree.getChilds().size() == 0) {
			if (tree.getText().startsWith("&")) {
				tree.setText(tree.getText().substring(1, tree.getText().length()));
			}
			cn.addCutsets(tree.getText());
			this.settingCNList(cn, tree.getText());
			tree.setText(null);
		} else {
			tree.setText(null);
			for (CutsetNode tmp : tree.getChilds()) {
				getCutsetList(cn, tmp);
			}
		}
	}

	private void getAllAbstractNode(FaultTreeNode ft) {
		boolean check = false;
		for (FaultTreeNode childFt : ft.getChilds()) {
			if (ft.getGroupId() != childFt.getGroupId()) {
				if (!childFt.isInput()) {
					check = false;
					for (CutsetNode cn : cnList) {
						if (this.cutsetString.get(Integer.parseInt(cn.getText())).equals(childFt.getText())) {
							check = true;
							break;
						}
					}
					if (!check) {
						CutsetNode cut;
						int key = this.findCutSetString(childFt.getText());
						if (key != -1) {
							cut = new CutsetNode(Integer.toString(key));
						} else {
							this.cutsetString.put(this.csCount, childFt.getText());
							cut = new CutsetNode(Integer.toString(csCount));
							csCount++;
						}
						cnList.add(cut);
						this.makeCutsetList(childFt, cut);
						getAllAbstractNode(childFt);
						continue;
					}
				}
			}
			getAllAbstractNode(childFt);
		}
	}

	private int findCutSetString(String str) {
		for (int i = 0; i < this.cutsetString.size(); i++) {
			if (cutsetString.get(i).equals(str)) {
				return i;
			}
		}
		return -1;
	}

	public void makeCutsetList(FaultTreeNode root, CutsetNode parent) {
		for (FaultTreeNode ft : root.getChilds()) {
			if (ft.getGateType() == GateType.AND || ft.getGateType() == GateType.OR || ft.isFormula() || ft.isInput()
					|| ft.isAbstract() || ft.getChilds().size() == 0) {
				CutsetNode cut;
				if (ft.getGateType() == GateType.AND || ft.getGateType() == GateType.OR) {
					cut = new CutsetNode(ft.getText());
				} else {
					int key = this.findCutSetString(ft.getText());
					if (key != -1) {
						cut = new CutsetNode(Integer.toString(key));
					} else {
						this.cutsetString.put(this.csCount, ft.getText());
						cut = new CutsetNode(Integer.toString(csCount));
						csCount++;
					}
				}
				// CutsetNode cut = new CutsetNode(ft.getText());
				parent.addChild(cut);
				if (root.getGroupId() == ft.getGroupId()) {
					makeCutsetList(ft, cut);
				}
			} else {
				if (root.getGroupId() == ft.getGroupId()) {
					makeCutsetList(ft, parent);
				}
			}
		}
		return;
	}
	
	private boolean checkMCS(String str){
		String[] tmp = str.split("&");
		Variable[] v = new Variable[tmp.length];
		Variable tmpVariable = null;
		int count = 0;
		for(int i = 0; i < tmp.length; i++){
			int index = 0;
			boolean check = false;
			String s = tmp[i];
			if(s.contains("true")){
				if(s.contains("!=")){
					for(int j = 0; j<s.length(); j++){
						if(s.charAt(j) == '!'){
							index = j;
						}
					}
					tmpVariable = new Variable(s.substring(0, index-1), VariableType.BOOL, "false");
				}else{
					for(int j = 0; j<s.length(); j++){
						if(s.charAt(j) == '='){
							index = j;
						}
					}
					tmpVariable = new Variable(s.substring(0, index-1), VariableType.BOOL, "true");
				}
				check = true;
			}else if(s.contains("false")){
				if(s.contains("!=")){
					for(int j = 0; j<s.length(); j++){
						if(s.charAt(j) == '!'){
							index = j;
						}
					}
					tmpVariable = new Variable(s.substring(0, index-1), VariableType.BOOL, "true");
				}else{
					for(int j = 0; j<s.length(); j++){
						if(s.charAt(j) == '='){
							index = j;
						}
					}
					tmpVariable = new Variable(s.substring(0, index-1), VariableType.BOOL, "false");
				}
				check = true;
			}
			if(check){
				for(int j =0; j < count; j++){
					if(v[j].getName().equals(tmpVariable.getName())){
						if(!(v[j].getValue().equals(tmpVariable.getValue()))){
							return false;
						}
					}
				}
				v[count]=tmpVariable;
				count++;
			}
		}
		return true;
	}

	public void printAllCutset(FileWriter fWriter) {
		Shell shell = new Shell(Display.getCurrent());
		shell.setText("Minimal Cut-Sets of the Software Fault Tree");
		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);
		Text mcsText = new Text(composite, SWT.V_SCROLL | SWT.WRAP);
		String allMCS = "";
		String str = "";
		String[] tmp;
		int strCount = 0;
		
		for (CutsetNode cn : cnList) {
			ArrayList<String> cutsets = cn.getCutsets();
			count = 0;
			try {
				fWriter.write(this.cutsetString.get(Integer.parseInt(cn.getText())) + "Minimal Cut Sets (���� ��) {\r\n");
				for (int i = 0; i < cutsets.size(); i++) {
					count++;
					tmp = cutsets.get(i).split("&");
					allMCS += count + ". ";
					strCount = 0;
					for (String s : tmp) {
						str += this.cutsetString.get(Integer.parseInt(s)) + "&";
						if(strCount==2){
							allMCS+="\n";
							strCount=0;
						}
						allMCS += this.cutsetString.get(Integer.parseInt(s)) + "&";
						strCount++;
					}
					str = str.substring(0, str.length());
					fWriter.write(count + ". " + str + "\r\n");
					allMCS+="\n";
					str = "";
				}
				System.out.println("Minimal Cut Set Count :" + count);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (CutsetNode cn : cnList) {
			ArrayList<String> cutsets = cn.getCutsets();
			count = 0;
			try {
				fWriter.write(this.cutsetString.get(Integer.parseInt(cn.getText())) + "Minimal Cut Sets (���� ��) {\r\n");
				for (int i = 0; i < cutsets.size(); i++) {
					count++;
					tmp = cutsets.get(i).split("&");
					allMCS += count + ". ";
					strCount = 0;
					for (String s : tmp) {
						str += this.cutsetString.get(Integer.parseInt(s)) + "&";
						if(strCount==2){
							allMCS+="\n";
							strCount=0;
						}
						allMCS += this.cutsetString.get(Integer.parseInt(s)) + "&";
						strCount++;
					}
					str = str.substring(0, str.length());
					if(checkMCS(str)){
						fWriter.write(count + ". " + str + "\r\n");
						allMCS+="\n";
					}else{
						count--;
					}
					str = "";
				}
				System.out.println("Minimal Cut Set Count :" + count);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mcsText.setText(allMCS);
		composite.pack();
		shell.pack();
		shell.open();
	}

	private void leafSetting(CutsetNode parent, CutsetNode tree) {
		if (tree.getChilds().size() == 0) {
			makeCutsetTree(parent, tree);
		} else {
			for (CutsetNode tmp : tree.getChilds()) {
				leafSetting(parent, tmp);
			}
		}
	}

	private void leafAddText(CutsetNode tree, String str) {
		if (tree.getChilds().size() == 0) {
			String[] cause = tree.getText().split("&");
			boolean check = false;
			for (String strTmp : cause) {
				if (str.equals(strTmp)) {
					check = true;
					break;
				}
			}
			if (!check) {
				tree.setText(tree.getText() + "&" + str);
			}
			// tree.setText(tree.getText() + "&" + str);
		} else {
			for (CutsetNode tmp : tree.getChilds()) {
				leafAddText(tmp, str);
			}
		}
	}

	public void makeCutsetTree(CutsetNode parent, CutsetNode tree) {
		if (!parent.getText().contains("|")) {
			for (CutsetNode cn : parent.getChilds()) {
				if (!(cn.getText().contains("|") || cn.getText().contains("&"))) {
					this.leafAddText(tree, cn.getText());
				}
				this.leafSetting(cn, tree);
			}
		} else {
			for (CutsetNode cn : parent.getChilds()) {
				CutsetNode child = new CutsetNode(tree.getText());
				tree.addChild(child);
				if (!(cn.getText().contains("&") || cn.getText().contains("|"))) {
					String[] cause = tree.getText().split("&");
					boolean check = false;
					for (String strTmp : cause) {
						if (cn.getText().equals(strTmp)) {
							check = true;
							break;
						}
					}
					if (!check) {
						child.setText(tree.getText() + "&" + cn.getText());
					} else {
						tree.getChilds().clear();
						//tree.getChilds().remove(child);
						break;
					}
					// child.setText(tree.getText() + "&" + cn.getText());
				}
				makeCutsetTree(cn, child);
			}
		}
	}

	public String formulaMaker(FaultTreeNode root) {
		String result = "";
		if (root.getGateType() == GateType.AND || root.getGateType() == GateType.OR) {
			result += "(";
			int size = result.length();
			for (FaultTreeNode ft : root.getChilds()) {
				result += formulaMaker(ft);
				if (!ft.equals(root.getChilds().get(root.getChilds().size() - 1))) {
					if (result.length() != size)
						result += root.getText();
				}
			}
			if (size != result.length())
				result += ")";
			else {
				StringBuffer str = new StringBuffer(result);
				str.deleteCharAt(result.length() - 1);
				result = str.toString();
			}
		} else if (root.isFormula()) {
			result += root.getText();
			for (FaultTreeNode ft : root.getChilds()) {
				result += formulaMaker(ft);
			}
		} else {
			for (FaultTreeNode ft : root.getChilds()) {
				result += formulaMaker(ft);
			}
		}
		return result;
	}
}