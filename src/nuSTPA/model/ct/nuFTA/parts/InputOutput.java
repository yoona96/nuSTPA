package nuSTPA.model.ct.nuFTA.parts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nuSTPA.model.ct.nuFTA.model.IONode;
import nuSTPA.model.ct.nuFTA.model.VariableType;

public class InputOutput {
	private InputOutput parent;
	private List<InputOutput> child = new LinkedList<>();
	private String name;
	
	public InputOutput(){
		
	}

	public InputOutput(InputOutput parent) {
		this.parent = parent;
	}

	public InputOutput getParent() {
		return parent;
	}

	public void setParent(InputOutput parent) {
		this.parent = parent;
	}

	public List<InputOutput> getChild() {
		return child;
	}

	public void setChild(List<InputOutput> child) {
		this.child = child;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	public InputOutput createInit(ArrayList<IONode> input, ArrayList<IONode> output, InputOutput c) {
		InputOutput root = new InputOutput(null);
		
		root.child.add(c);
		
		InputOutput inputRoot = new InputOutput(root);
		inputRoot.setName("Input Node");
		root.child.add(inputRoot);

		InputOutput outputRoot = new InputOutput(root);
		outputRoot.setName("Output Node");
		root.child.add(outputRoot);

		for (int i = 0; i < input.size(); i++) {
			InputOutput inputChild = new InputOutput(inputRoot);
			String type = null;
			switch (input.get(i).getValue().getType()) {
			case VariableType.BOOL: {
				type = "boolean";
				break;
			}
			case VariableType.CONSTANT: {
				type = "constant";
				break;
			}
			case VariableType.RANGE: {
				type = input.get(i).getValue().getMin() + ".." + input.get(i).getValue().getMax();
				break;
			}
			default: {
				break;
			}
			}
			inputChild.setName(input.get(i).getName() + " : " + type);
			inputRoot.child.add(inputChild);
		}

		for (int i = 0; i < output.size(); i++) {
			InputOutput outputChild = new InputOutput(outputRoot);
			String type = null;
			switch (output.get(i).getValue().getType()) {
			case VariableType.BOOL: {
				type = "boolean";
				break;
			}
			case VariableType.CONSTANT: {
				type = "constant";
				break;
			}
			case VariableType.RANGE: {
				type = output.get(i).getValue().getMin() + ".." + output.get(i).getValue().getMax();
				break;
			}
			default: {
				break;
			}
			}
			outputChild.setName(output.get(i).getName() + " : " + type);
			outputRoot.child.add(outputChild);
		}

		return root;
	}
}
