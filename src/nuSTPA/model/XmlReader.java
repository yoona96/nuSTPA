	package nuSTPA.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class XmlReader {

	private XPath xPath = XPathFactory.newInstance().newXPath();
	private static Document doc;
	public static Node rootFod = null;
	static String rootFODName = "";
	public static Element rootElement = null;
	
	public static ArrayList<String> fodNodeNameList = new ArrayList<String>(); //list for name of FODs
	public static ObservableList<String> connectedVarSet = FXCollections.observableArrayList(); //list of variables connected to selected output variable
	public static ArrayList<String> connectedVarSetwState = new ArrayList<String>(); //list of variables connected to selected output variable with state
	static ObservableList<String> outputVars = FXCollections.observableArrayList(); //list of output variables in selected FOD

	static ArrayList<String> typeList = new ArrayList<String>(); // list of name & type of selected output variables
	
	static ArrayList<String> fsmTtsList = new ArrayList<String>(); //list of SDT/TTS nodes from connected variables

	static ArrayList<Transition> transitionList = new ArrayList<Transition>();
	static String selectedOutput = new String();
	
	static NodeList fodNodeList = null;
	static NodeList selectedNodeList = null;
	static NodeList outputNodeList = null;
	static NodeList typeNodeList = null;
	
	public XmlReader() {	}
	
	public static ArrayList<String> parseXml(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
		//factory to read document
		DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
		//create builder
		DocumentBuilder docBuilder = dbFac.newDocumentBuilder();
		//parse xml file into doc, with docBuilder
		Document doc = docBuilder.parse(xmlFile);
//		doc.getDocumentElement().normalize();
		
		//identifying fod		
		fodNodeList = doc.getElementsByTagName("FOD");
		//list of fod nodes
		
		for(int i = 0; i < fodNodeList.getLength(); i++) {
			Node fodNode = fodNodeList.item(i);
			//get all fod nodes
			
			if(fodNode.getNodeType() == Node.ELEMENT_NODE) {
				Element fodElement = (Element)fodNode;
				//elements of fod nodes
				fodNodeNameList.add(fodElement.getAttribute("name"));
				//find needed attribute from element
			}
		}
		if(fodNodeNameList.get(0).equals("Root")) {
			setRootFod(0);
		}else {
			fodNodeNameList.set(0, "");
		}
		return fodNodeNameList;
	}
	
	//get type value from NuSCR file
	public static ArrayList<String> parseNuSCR(File xmlFile) throws ParserConfigurationException, SAXException, IOException{
		//factory to read document
		DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
		//create builder
		DocumentBuilder docBuilder;
		docBuilder = dbFac.newDocumentBuilder();

		//parse xml file into doc, with docBuilder
		Document doc;
		doc = docBuilder.parse(xmlFile);
//		doc.getDocumentElement().normalize();
	
		ArrayList<String> elementList = new ArrayList<String>();
		for(int i = 0; i < doc.getElementsByTagName("TypeTable").getLength(); i++) {
			elementList.add(((Element)doc.getElementsByTagName("TypeTable").item(i)).getNodeName());
		}
		
//		System.out.println("this is element list : " + elementList);
			
//		for(int i = 0; i < doc.getDocumentElement().getChildNodes().getLength(); i ++)
//			System.out.println("these are types of document : " + ((Element)doc.getDocumentElement().getChildNodes().item(3)).getElementsByTagName("entry").item(i).getAttributes());
		
		//identifying type nodes
		typeNodeList = doc.getDocumentElement().getElementsByTagName("TypeTable").item(0).getChildNodes();
		//list of fod nodes
		
		for(int i = 0; i < typeNodeList.getLength(); i++) {
			Node typeNode = typeNodeList.item(i);
			//get all type nodes
			if(typeNode.getNodeType() == Node.ELEMENT_NODE) {
				Element typeElement = (Element)typeNode;
				//elements of type nodes
//				System.out.println("this is type : " + typeElement.getAttribute("type"));
				typeList.add(typeElement.getAttribute("type"));
				//find needed attribute from element
			}
		}
//		System.out.println("these are types : " + typeList);
		return typeList;
	}
	

	public static void setRootFod(int fodNum) {
		rootFod = fodNodeList.item(fodNum);
		rootFODName = fodNodeNameList.get(fodNum);
	}
	
	public static String getRootFod() {
		return rootFODName;
	}
	
	public static ObservableList<String> getOutputs() {
		rootElement = (Element)rootFod;
	
		//identifying output variables connected to FOD
		//nodelist는 rootFOD가 어떤 것으로 선정되었느냐에 따라 달라짐
		outputNodeList = rootElement.getElementsByTagName("output");
		ArrayList<ObservableList<String>> tempList1 = new ArrayList<ObservableList<String>>();
		ObservableList<String> tempList2 = FXCollections.observableArrayList();
		
		for(int i = 0; i < outputNodeList.getLength(); i++) {
			Node outputNode = outputNodeList.item(i);
			//get all output variables
			
			if(outputNode.getNodeType() == Node.ELEMENT_NODE) {
				Element outputElement = (Element)outputNode;
				//output variable elements
				
				String tempStr = outputElement.getAttribute("name");
				tempList2.add(tempStr);
				
				tempList1.add(tempList2);
			}
		}
		//remove duplication
		for(int i = 0; i < tempList1.size(); i++) {
			if(!outputVars.contains(tempList1.get(i))) {
				outputVars.addAll(tempList1.get(i));
			}
		}
		
		return outputVars;
	}
	

	// Get transition Node list
	public static void getTransitionNodes(String selectedOutputVar) {
//		 get nodes of selected output variables
		for(String s : outputVars) {
			if(s.equals(selectedOutputVar)) {
				int outputNum = outputVars.indexOf(s);
				Node selectedOutputNode = outputNodeList.item(outputNum);
				
				Element selectedOutputElement = (Element)selectedOutputNode;
				
				selectedOutput = selectedOutputElement.getAttribute("name");
				
				NodeList transitionNodeList = rootElement.getElementsByTagName("transition");
				//every 'transition' element is included
				addTransition(transitionNodeList);
				//transitionList has to be made based on current FOD
				checkTarget(selectedOutputElement.getAttribute("name"));
			}
		}

		//check if there are SDT/TTS node in saved connectedVarSet
		//compare SDT/TTS node extracted from FOD
		//*****************************************************************************************************
		NodeList fsmNodeList = rootElement.getElementsByTagName("FSM");
		NodeList ttsNodeList = rootElement.getElementsByTagName("TTS");
		
		for(int a = 0; a < fsmNodeList.getLength(); a++) {
			Node fsmNode = fsmNodeList.item(a);
			if(fsmNode.getNodeType() == Node.ELEMENT_NODE) {
				Element fsmNodeElement = (Element)fsmNode;
				
				fsmTtsList.add(fsmNodeElement.getAttribute("name"));
			}
		}
		for(int b = 0; b < ttsNodeList.getLength(); b++) {
			Node ttsNode = ttsNodeList.item(b);
			if(ttsNode.getNodeType() == Node.ELEMENT_NODE) {
				Element ttsNodeElement = (Element)ttsNode;
				
				fsmTtsList.add(ttsNodeElement.getAttribute("name"));
			}
		}

		/*
		 * compare each connected variable with sdtTtsList
		 * if connected variable is equal to sdt/tts node, add original variable & variable_state to connectedVarSetwState
		 * if it does not, just add original variable
		 */
		for(int d = 0; d < connectedVarSet.size(); d++) {
			connectedVarSetwState.add(connectedVarSet.get(d));
		}

		for(int c = 0; c < connectedVarSet.size(); c++) {
			for(int d = 0; d < fsmTtsList.size(); d++) {
				if(fsmTtsList.get(d).equals(connectedVarSet.get(c))) {
					connectedVarSetwState.add(fsmTtsList.get(d) + "_state");
				}else
					continue;
			}
		}
	}

	

	//add transition relation to list with form of (source, target)
	public static void addTransition(NodeList transitionNodeList) {
		//should add transition based on current FOD
		for(int i = 0; i < transitionNodeList.getLength(); i++) {
			Node transitionNode = transitionNodeList.item(i);
			if(transitionNode.getNodeType() == Node.ELEMENT_NODE) {
				Element transitionElement = (Element) transitionNode;
				//identify source&target node list
				NodeList srcNodeList = transitionElement.getElementsByTagName("source");
				NodeList tgtNodeList = transitionElement.getElementsByTagName("target");
				
				for(int j = 0; j < srcNodeList.getLength(); j++) {
					//get each node from node list
					Node srcNode = srcNodeList.item(j);
					Node tgtNode = tgtNodeList.item(j);
					
					if(srcNode.getNodeType() == Node.ELEMENT_NODE && tgtNode.getNodeType() == Node.ELEMENT_NODE) {
						Element srcElement = (Element)srcNode;
						Element tgtElement = (Element)tgtNode;
						
						Transition temp = new Transition(srcElement.getAttribute("refName"), tgtElement.getAttribute("refName"));
						
						//do not include state of SDT, TTS node in transitionList & transition that has same source and target
						if(!(temp.getSource().equals("Normal")||temp.getSource().equals("Waiting")||temp.getSource().equals("Pretrip")||temp.getSource().equals("Trip")) && !temp.getSource().equals(temp.getTarget())){
							transitionList.add(temp);
						}
					}
				}
			}
		}
	}
	
	//recursive function
	//check for transitions that has this param target as target
	//only works once. 
	public static Object checkTarget(String target1) {
		//should check target on current FOD
		//�Էµ� target�� target���� �ϰ� �ִ� source�� ã�� ���� ��ǥ
		//���� FOD�� �������� transition element�� ���� element��(source, target)�� ���캻��.
		String source1 = "", target2 = "";
		ArrayList<String> targetList = new ArrayList<String>();
		for(int i = 0; i < transitionList.size(); i++) {
			targetList.add(transitionList.get(i).getTarget());
	
	//		System.out.println("target List : " + targetList);
		}
		for(int i = 0; i < transitionList.size(); i++) {
			Transition t = transitionList.get(i);
			if(target1.equals(t.getTarget())) {
				source1 = t.getSource();
				if(!connectedVarSet.contains(source1))
					connectedVarSet.add(source1);
				checkTarget(source1);
			}
			target2 = source1;
		}
		if(!targetList.contains(target1)) {
			return null;
		}else
			return checkTarget(target2);
	}

}

