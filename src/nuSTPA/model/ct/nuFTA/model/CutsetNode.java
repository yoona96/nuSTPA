package nuSTPA.model.ct.nuFTA.model;

import java.util.ArrayList;

public class CutsetNode {
	private String text;
	private ArrayList<CutsetNode> childs;
	private ArrayList<String> cutsets;
	private ArrayList<CutsetNode> cutsetParents;
	private ArrayList<CutsetNode> cutsetChilds;
	private CutsetNode parent;
	public CutsetNode(){
		this.text="";
		this.childs = new ArrayList<CutsetNode>();
		this.cutsets = new ArrayList<String>();
		this.cutsetParents = new ArrayList<CutsetNode>();
		this.cutsetChilds = new ArrayList<CutsetNode>();
		this.parent =null;
	}
	public CutsetNode(String text){
		this.text = text;
		this.childs = new ArrayList<CutsetNode>();
		this.cutsets = new ArrayList<String>();
		this.cutsetParents = new ArrayList<CutsetNode>();
		this.cutsetChilds = new ArrayList<CutsetNode>();
		this.parent = null;
	}
	public CutsetNode(String text, CutsetNode parent){
		this.text = text;
		this.childs = new ArrayList<CutsetNode>();
		this.cutsets = new ArrayList<String>();
		this.cutsetParents = new ArrayList<CutsetNode>();
		this.cutsetChilds = new ArrayList<CutsetNode>();
		this.parent = parent;
	}
	
	public ArrayList<CutsetNode> getCutsetChilds() {
		return cutsetChilds;
	}
	public void setCutsetChilds(ArrayList<CutsetNode> cutsetChilds) {
		this.cutsetChilds = cutsetChilds;
	}
	public void addCutsetChilds(CutsetNode cn){
		this.cutsetChilds.add(cn);
	}
	public ArrayList<CutsetNode> getCutsetParents() {
		return cutsetParents;
	}
	public void setCutsetParents(ArrayList<CutsetNode> cutsetParents) {
		this.cutsetParents = cutsetParents;
	}
	public void addCutsetParents(CutsetNode cn){
		this.cutsetParents.add(cn);
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ArrayList<CutsetNode> getChilds() {
		return childs;
	}
	public ArrayList<String> getCutsets() {
		return cutsets;
	}
	public void setCutsets(ArrayList<String> cutsets) {
		this.cutsets = cutsets;
	}
	public void addCutsets(String cutset){
		this.cutsets.add(cutset);
	}
	public void addChild(CutsetNode c){
		this.childs.add(c);
	}
	public void setChilds(ArrayList<CutsetNode> childs) {
		this.childs = childs;
	}
	public CutsetNode getParent() {
		return parent;
	}
	public void setParent(CutsetNode parent) {
		this.parent = parent;
	}
}
