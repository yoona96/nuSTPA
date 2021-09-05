package nuSTPA.model.ct.nuFTA.controller;

import java.io.FileWriter;
import java.util.ArrayList;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import nuSTPA.model.ct.nuFTA.model.CutsetNode;
import nuSTPA.model.ct.nuFTA.model.FaultTreeNode;
import nuSTPA.model.ct.nuFTA.model.IONode;
import nuSTPA.model.ct.nuFTA.model.Variable;
import nuSTPA.model.ct.nuFTA.parts.InputOutput;
import nuSTPA.model.ct.nuFTA.controller.XML_Parser;

public class MainController {
	private static MainController mc = new MainController();
	private XML_Parser parser;
	private FaultTreeCreator ft;
	private FormulaMaker fm;
	private FaultTreeNode root;
	//�ð� ������ ����
	private FaultTreeNode timeNode;
	private String period;
	private String selectedPart = "FOD";
	private String selectedTemplate;
	private boolean check = false;
	public static int count = 0;
	
	public MainController() {
	}

	public static MainController getInstance() {
		return mc;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public FaultTreeNode getTimeNode() {
		return timeNode;
	}

	public void setTimeNode(FaultTreeNode timeNode) {
		this.timeNode = timeNode;
	}

	public FaultTreeCreator getFt() {
		return ft;
	}

	public void setFt(FaultTreeCreator ft) {
		this.ft = ft;
	}

//	public void fileOpen(String url) {
//		parser = new XML_Parser();
//		parser.openNuSCR(url);
//		ft = new FaultTreeCreator(parser.getFtanodelist(), parser.getTypeTableList());
//	}

	public ArrayList<IONode> getInputList() {
		return parser.getInputlist();
	}

	public ArrayList<IONode> getOutputList() {
		return parser.getOutputlist();
	}
	public InputOutput getInputOutput(){
		return parser.getNodeHierarchy();
	}

	public FaultTreeNode getRootNode() {
		return this.root;
	}
	
	public void getTimeTreeRoot(FaultTreeNode causeTimeNode, String period, String prevState){
		this.period = period;
		this.timeNode = new FaultTreeNode(causeTimeNode.getText());
		timeNode.setGroupId(causeTimeNode.getGroupId());
		Variable timeNodeVariable;
		if(causeTimeNode.getChilds().get(0).getVariable() != null){
			timeNodeVariable= new Variable(causeTimeNode.getChilds().get(0).getVariable());
			timeNode.setVariable(timeNodeVariable);
			this.check = false;
		}else{
			timeNode.setVariable(causeTimeNode.getChilds().get(0).getChilds().get(1).getVariable());
			this.check = true;
		}
		ft.makeTimeTree(timeNode, period, prevState);
		this.check = false;
		//fm.cutsetList(timeNode);
	}

	public void getRoot(String name, int id, String value, int type) {
		root = new FaultTreeNode(name + " = " + value);
		Variable output = new Variable(name, type, value);
		long start = System.currentTimeMillis();
		ft.makeTree(root, output, parser.getFtanodelist().get(id));
		long end = System.currentTimeMillis();
		FileWriter fWriter;
		try {
			fWriter = new FileWriter(".txt", true);
			fWriter.write("make fault tree time : " + (end-start) +"ms\r\n");
			fWriter.write("Generated Node : " + this.count);
			fWriter.close();
		}catch (Exception e){
			
		}
		System.out.println("make fault tree time : " + (end-start)/1000.0);
		System.out.println(this.count);
		this.count=0;
	}
	public void saveTimeMinimalCutset(FaultTreeNode root){
		TimeMinimalCutSetCreator tMCSC = new TimeMinimalCutSetCreator();
		long start = System.currentTimeMillis();
		tMCSC.cutsetList(root);
		long end = System.currentTimeMillis();
		System.out.println("make cutset using subtree time : " + (end-start)/1000.0);
		System.out.println(this.count);
		this.count=0;
	}
	public void saveMinimalCutset(FaultTreeNode root){
		fm = new FormulaMaker();
		long start = System.currentTimeMillis();
		fm.cutsetList(root);
		long end = System.currentTimeMillis();
		System.out.println("make cutset using subtree time : " + (end-start)/1000.0);
		this.count=0;
	}
	
	public String getFormula() {
		fm = new FormulaMaker();
		String formula = fm.formulaMaker(root);
		while (true) {
			if (formula.matches(".*&&.*|.*\\|\\|.*|.*\\(\\|\\(.*|.*\\)\\|\\).*|.*\\(&\\(.*|.*\\)&\\).*")) {
				formula = formula.replaceAll("&&", "&").replaceAll("\\|\\|", "\\|").replaceAll("\\(\\|\\(", "\\(\\(")
						.replaceAll("\\)\\|\\)", "\\)\\)").replaceAll("\\(&\\(", "\\(\\(")
						.replaceAll("\\)&\\)", "\\)\\)");
			} else {
				break;
			}
		}

		return formula;
	}

	public String getSelectedPart() {
		return selectedPart;
	}

	public void setSelectedPart(String selectedPart) {
		this.selectedPart = selectedPart;
	}

	public void showFT() {
		MPartStack stack = (MPartStack) modelService.find("nufta.partstack.frame", app);

		MPart mFTPart = partService.createPart("nufta.partdescriptor.faulttree");
		mFTPart.setLabel(root.getText() + " (Fault Tree)");
		stack.getChildren().add(mFTPart);
		partService.activate(mFTPart);
		partService.showPart(mFTPart, PartState.ACTIVATE);

		MPart mAFTPart = partService.createPart("nufta.partdescriptor.abstractfaulttree");
		mAFTPart.setLabel(root.getText() + " (Abstract)");
		stack.getChildren().add(mAFTPart);
		partService.activate(mAFTPart);
		partService.showPart(mAFTPart, PartState.ACTIVATE);
	}

	public void showTemplate(String selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
		MPartStack stack = (MPartStack) modelService.find("nufta.partstack.frame", app);
		MPart mTemplatePart = partService.createPart("nufta.partdescriptor.template");
		mTemplatePart.setLabel(selectedTemplate + " (Template)");
		stack.getChildren().add(mTemplatePart);
		partService.activate(mTemplatePart);
		partService.showPart(mTemplatePart, PartState.ACTIVATE);
	}
	
	public void showTimeTemplate(String period) {
		this.period = period;
		MPartStack stack = (MPartStack) modelService.find("nufta.partstack.frame", app);
		MPart mTemplatePart = partService.createPart("nufta.partdescriptor.timetemplate");
		mTemplatePart.setLabel("One cycle : " + period);
		stack.getChildren().add(mTemplatePart);
		partService.activate(mTemplatePart);
		partService.showPart(mTemplatePart, PartState.ACTIVATE);
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getSelectedTemplate() {
		return selectedTemplate;
	}

	public void setSelectedTemplate(String selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}
}