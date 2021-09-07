package nufta.parts;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JViewport;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import nufta.controller.MainController;
import nufta.model.FaultTreeNode;

public class FaultTreePart {
	private Frame frame;
	private mxGraphComponent com;
	private String formula;
	private FaultTreeNode root;
	private mxCompactTreeLayout layout;
	private mxGraph graph;
	private Shell shell;
	
	@Focus
	public void showFormula() {
		MainController.getInstance().setFormula(formula);
		MainController.getInstance().setSelectedPart(root.getText() + " (Fault Tree)");
	}

	@PostConstruct
	public void createComposite(Composite parent) {
		shell = parent.getShell();
		Composite swtAwtComponent = new Composite(parent, SWT.EMBEDDED);
		frame = SWT_AWT.new_Frame(swtAwtComponent);

		graph = new mxGraph();
		Object obj = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		graph.setAutoSizeCells(true);
		//graph.setCellsLocked(true);
		graph.setCellsEditable(false);
		graph.setCellsResizable(false);
		graph.setCellsDeletable(false);
		graph.setCellsDisconnectable(false);
		graph.setEventsEnabled(false);
		graph.setResetEdgesOnMove(true);

		HashMap<String, Object> or = new HashMap<String, Object>();
		or.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
		or.put(mxConstants.STYLE_IMAGE, "/img/OR.png");
		graph.getStylesheet().putCellStyle("or", or);

		HashMap<String, Object> and = new HashMap<String, Object>();
		and.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_IMAGE);
		and.put(mxConstants.STYLE_IMAGE, "/img/AND.png");
		graph.getStylesheet().putCellStyle("and", and);

		root = MainController.getInstance().getRootNode();
		formula = MainController.getInstance().getFormula();
		try {
			mxICell cell = (mxICell) graph.insertVertex(obj, null, "  "+root.getText()+"  ", 0, 0,
					0, 30, "fillColor=white;fontSize=20");
			graph.updateCellSize(cell);
			this.drawFaultTree(root, graph, obj, cell);
		} finally {
			graph.getModel().endUpdate();
		}

		layout = new mxCompactTreeLayout(graph);
		layout.setHorizontal(false);
		layout.setMoveTree(true);
		layout.execute(graph.getDefaultParent());

		com = new mxGraphComponent(graph);
		com.setAutoExtend(true);
		com.getViewport().setOpaque(true);
		com.getViewport().setBackground(Color.WHITE);
		com.setConnectable(false);
		com.setCenterZoom(true);
		com.setCenterPage(true);
		com.setDoubleBuffered(true);

		MouseAdapter mouseAdapter = new MouseAdapter() {
			private Point origin;
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 1) {
					Object cell = com.getCellAt(e.getX(), e.getY());
					if (cell != null) {

					} else {
						origin = new Point(e.getPoint());
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (origin != null) {
					JViewport viewPort = com.getViewport();
					if (viewPort != null) {
						int deltaX = origin.x - e.getX();
						int deltaY = origin.y - e.getY();
						int scrollX = com.getHorizontalScrollBar().getValue();
						int scrollY = com.getVerticalScrollBar().getValue();
						com.getHorizontalScrollBar().setValue(scrollX + deltaX);
						com.getVerticalScrollBar().setValue(scrollY + deltaY);
					}
				}
			}
		};

		com.getGraphControl().addMouseListener(mouseAdapter);
		com.getGraphControl().addMouseMotionListener(mouseAdapter);

		frame.add(com);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private void drawFaultTree(FaultTreeNode root, mxGraph graph, Object obj, mxICell parent) {
		for (FaultTreeNode n : root.getChilds()) {
			mxICell child;
			if(n.getChilds().size() == 0){
				child = (mxICell) graph.insertVertex(obj, null, "  \n  "+n.getText()+"  \n  ", 0, 0, 0, 30,"fillColor=white;fontSize=20;shape=ellipse");
			}else{
				child = (mxICell) graph.insertVertex(obj, null, "  "+n.getText()+"  ", 0, 0, 0, 30,"fillColor=white;fontSize=20");
			}
			if (n.getText().equals("&")) {
				child.setValue("");
				child.setStyle("and");
			} else if (n.getText().equals("|")) {
				child.setValue("");
				child.setStyle("or");
			} else if (!n.isInput() && n.getChilds().size() != 0){
				String text = n.getText();
				String tmp;
				if (!n.isInput()) {
					for (int i = 0; i < text.length(); i++) {
						switch (text.charAt(i)) {
						case '&':
						case '|':
						case '<':
						case '>':
							if (text.charAt(i - 1) != '-') {
								tmp = text.substring(0, i) + '\n';
								tmp += text.substring(i, text.length());
								text = tmp;
								i++;
							}
							break;
						}
					}
				}
				text = "  "+text+"  ";
				child.setValue(text);
			}
			graph.insertEdge(obj, null, "", parent, child, "startArrow=NONE;endArrow=NONE");
			graph.updateCellSize(child);
			//graph.rep
			drawFaultTree(n, graph, obj, child);
		}
	}

	public void zoomIn() {
		com.zoomIn();
		com.repaint();
	}

	public void zoomOut() {
		com.zoomOut();
		com.repaint();
	}
	public void saveMinimalCutset(){
		MainController.getInstance().saveMinimalCutset(this.root);
	}
	
	public void turn()
	{
		if(layout.isHorizontal())
		{
			layout.setHorizontal(false);
		}
		else
		{
			layout.setHorizontal(true);
		}
		
		layout.execute(graph.getDefaultParent());
		com.repaint();
	}
}