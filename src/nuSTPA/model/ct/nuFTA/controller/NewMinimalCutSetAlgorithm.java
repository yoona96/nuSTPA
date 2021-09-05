package nuSTPA.model.ct.nuFTA.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import nuSTPA.model.ct.nuFTA.model.CutsetNode;
import nuSTPA.model.ct.nuFTA.model.FaultTreeNode;
import nuSTPA.model.ct.nuFTA.model.GateType;

public class NewMinimalCutSetAlgorithm {
	private int count = 0;
	private ArrayList<String> minimalCutSets = new ArrayList<String>();
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
		this.reconstructionFaultTree(root, parent);
		this.orGateReconstructionFaultTree(parent);
		this.andGateReconstructionFaultTree(parent);
		this.makeMinimalCutsets(root, parent);
		return parent;
	}
	private void orGateReconstructionFaultTree(CutsetNode node){
		if(node.getText().equals("|")){
			for(int i = 0; i < node.getChilds().size(); i++){
				CutsetNode cs = node.getChilds().get(i);
				if(cs.getText().equals("|")){
					node.getCutsetChilds().remove(cs);
					for(CutsetNode childCS : cs.getChilds()){
						node.addChild(childCS);
					}
					i--;
				}else if(cs.getText().equals("&")){
					for(CutsetNode childCS : cs.getChilds()){
						this.orGateReconstructionFaultTree(childCS);
					}
				}
			}
		}else {
			for(CutsetNode cs : node.getChilds()){
				if(cs.getText().equals("|")){
					this.orGateReconstructionFaultTree(cs);
				}else if(cs.getText().equals("&")){
					for(CutsetNode childCS : cs.getChilds()){
						this.orGateReconstructionFaultTree(childCS);
					}
				}
			}
		}
	}
	private void andGateReconstructionFaultTree(CutsetNode node){
		if(node.getText().equals("&")){
			for(int i = 0; i < node.getChilds().size(); i++){
				CutsetNode cs = node.getChilds().get(i);
				if(cs.getText().equals("&")){
					node.getCutsetChilds().remove(cs);
					for(CutsetNode childCS : cs.getChilds()){
						node.addChild(childCS);
					}
					i--;
				}else if(cs.getText().equals("|")){
					for(CutsetNode childCS : cs.getChilds()){
						this.andGateReconstructionFaultTree(childCS);
					}
				}
			}
		}else {
			for(CutsetNode cs : node.getChilds()){
				if(cs.getText().equals("&")){
					this.andGateReconstructionFaultTree(cs);
				}else if(cs.getText().equals("|")){
					for(CutsetNode childCS : cs.getChilds()){
						this.andGateReconstructionFaultTree(childCS);
					}
				}
			}
		}
	}
	
	/**
	 * makeMinimalCutsets �Լ��� ��ü Minimal Cut Set�� ������ �Ŀ� ���Ͽ� �����ϴ� �Լ��̴�.
	 * 
	 * @param root
	 *            : ��ü Fault Tree�� �ֻ��� root ��� �̴�.
	 */
	private void makeMinimalCutsets(FaultTreeNode root, CutsetNode parent) {
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
			fWriter = new FileWriter(fileName + " newAlgorithm Minimal Cutsets.txt", true);
			makeMinimalCutSets(parent);
			this.printAllCutset(fWriter);
			fWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void makeMinimalCutSets(CutsetNode parent){
		ArrayList<String> mcsList;
		if(parent.getChilds().get(0).equals("|")){
			this.minimalCutSets = this.orGateMakeMCS(parent.getChilds().get(0), "");
		}else if(parent.getChilds().get(0).equals("&")){
			this.minimalCutSets = this.andGateMakeMCS(parent.getChilds().get(0), "");
		}
		return ;
	}
	
	private ArrayList<String> andGateMakeMCS(CutsetNode parent, String str){
		ArrayList<String> prevMCSList = new ArrayList<String>();
		ArrayList<String> nextMCSList = new ArrayList<String>();
		for(CutsetNode cs : parent.getChilds()){
			if(!cs.getText().equals("|")){
				str = str + "&" + cs.getText();
			}
		}
		prevMCSList.add(str);
		for(CutsetNode cs : parent.getChilds()){
			if(cs.getText().equals("|")){
				ArrayList<String> mcsTmp = null;
				for(String tmp : prevMCSList){
					mcsTmp = this.orGateMakeMCS(parent, tmp);
				}
				for(String tmp : mcsTmp){
					nextMCSList.add(tmp);
				}
			}
			this.getMinimalCutset(nextMCSList);
		}
		return null;
	}
	
	private ArrayList<String> orGateMakeMCS(CutsetNode parent, String str){
		String[] strList = str.split("&");
		ArrayList<String> mcsList = new ArrayList<String>();
		//basic event �� str�� ���ԵǴ� ���� �ִ� ���
		for(CutsetNode cs : parent.getChilds()){
			for(String tmp : strList){
				if(cs.getText().equals(tmp)){
					 mcsList.add(str);
					 return mcsList;
				}
			}
		}
		for(CutsetNode cs : parent.getChilds()){
			if(cs.getText().equals("&")){
				ArrayList<String> tmp = this.andGateMakeMCS(cs, str);
				for(String strTmp : tmp){
					//AND ����Ʈ�� MCS ��ȯ �� �� str�� ���� ���� �ִ� ���
					if(strTmp.equals(str)){
						mcsList.clear();
						mcsList.add(str);
						return mcsList;
					}
					mcsList.add(strTmp);
				}
			}else{
				mcsList.add(str+"&"+cs.getText());
			}
			this.getMinimalCutset(mcsList);
		}
		return mcsList;
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


	private int findCutSetString(String str) {
		for (int i = 0; i < this.cutsetString.size(); i++) {
			if (cutsetString.get(i).equals(str)) {
				return i;
			}
		}
		return -1;
	}

	public void reconstructionFaultTree(FaultTreeNode root, CutsetNode parent) {
		for (FaultTreeNode ft : root.getChilds()) {
			if (ft.getGateType() == GateType.AND || ft.getGateType() == GateType.OR || ft.getChilds().size() == 0) {
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
				parent.addChild(cut);
				reconstructionFaultTree(ft, cut);
			} else {
				reconstructionFaultTree(ft, parent);
			}
		}
		return;
	}

	public void printAllCutset(FileWriter fWriter) {
		String str = "";
		String[] tmp;
/*		for (CutsetNode cn : cnList) {
			ArrayList<String> cutsets = cn.getCutsets();
			count = 0;
			try {
				fWriter.write(this.cutsetString.get(Integer.parseInt(cn.getText())) + "Minimal Cut Sets {\r\n");

				for (int i = 0; i < cutsets.size(); i++) {
					tmp = cutsets.get(i).split("&");
					for (String s : tmp) {
						str += this.cutsetString.get(Integer.parseInt(s)) + "&";
					}
					str = str.substring(0, str.length());
					count++;
					fWriter.write(count + ". " + str + "\r\n");
					str = "";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}
}
