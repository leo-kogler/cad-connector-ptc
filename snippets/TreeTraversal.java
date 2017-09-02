package at.smartpart.resolve;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcAssembly.Assembly;
import com.ptc.pfc.pfcComponentFeat.ComponentFeat;
import com.ptc.pfc.pfcDrawing.Drawing;
import com.ptc.pfc.pfcFeature.FeatureType;
import com.ptc.pfc.pfcFeature.Features;
import com.ptc.pfc.pfcModel.Dependencies;
import com.ptc.pfc.pfcModel.Model;
import com.ptc.pfc.pfcModel.ModelDescriptor;
import com.ptc.pfc.pfcModel.ModelType;
import com.ptc.pfc.pfcModel2D.Model2D;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSolid.Solid;

import at.smartpart.beans.ASM;
import at.smartpart.beans.MyTreeNode;
import at.smartpart.beans.CadPart;

public class TreeTraversal {

	Model nextModel;
	Solid curSolid;
	private Session wfSession;
	Assembly memberAsm;

	List<CadPart> listPrt = new ArrayList<CadPart>();
	List<MyTreeNode> nodes = new ArrayList<MyTreeNode>();
	Map<String, MyTreeNode> knots = new LinkedHashMap<String, MyTreeNode>();
	int x = 1;
	int depth = 0;
	JPanel panel = new JPanel();;

	TreeModel treeModel = null;

	String topLevelAsmName = "";

	public TreeTraversal(Model curModel, Session wfSession) throws jxthrowable {

		try {
			System.out.println(curModel.GetFileName());
			topLevelAsmName = curModel.GetFileName();
		} catch (jxthrowable e2) {
			e2.printStackTrace();
		}

		this.wfSession = wfSession;

		MyTreeNode root = new MyTreeNode("root");

		System.out.println("creating tree");

		try {

			walkDownDeps(curModel, root);

			//printArray();

		} catch (jxthrowable e) {
			e.printStackTrace();
			System.out.println("problem traversing tree " + e.toString());
		}

		for (MyTreeNode nod : nodes) {
			System.out.println("TT" + nod.getFileName() + " " + nod.getLevel());
		}

		System.out.println(knots.toString());

		treeModel = new DefaultTreeModel(root);
		JTree tree = new JTree(treeModel);
		panel.add(tree, BorderLayout.CENTER);
		panel.setSize(400, 400);
		panel.setVisible(true);

	}

	List<MyTreeNode> getNodeList() {

		return this.nodes;
	}

	TreeModel cloneTree() {
		return treeModel;
	}

	public JPanel getTreePanel() {
		return this.panel;
	};

	public void walkDownDeps(Model curModel, MyTreeNode node) throws jxthrowable {

		MyTreeNode leaf;

		// if (memberList.length != 0 ) vaadin [k] = memberList;

		// System.out.println(curModel.GetOId());

		if (curModel.GetType() == ModelType.MDL_ASSEMBLY) {

			memberAsm = (Assembly) curModel;
			
			leaf = new MyTreeNode(memberAsm.GetFileName());

			node.add(leaf);
			nodes.add(leaf);

			System.out.println("level: " + leaf.getLevel() + "   " + leaf.getFileName());

			if (!knots.containsKey(leaf.getFileName()))
				knots.put(leaf.getFileName(), leaf);

			listPrt.add(new CadPart(memberAsm.GetFileName(), 0));

			Features comps = memberAsm.ListFeaturesByType(Boolean.FALSE, FeatureType.FEATTYPE_COMPONENT);
			
				for (int index = 0; index < comps.getarraysize(); index++) {

					ComponentFeat compFeature = (ComponentFeat) comps.get(index);

					ModelDescriptor description = compFeature.GetModelDescr();

					nextModel = wfSession.GetModelFromDescr(description);
							
					walkDownDeps(nextModel, leaf);
				}

			//}
		} else {

			
			nextModel = curModel;
			
			leaf = new MyTreeNode(curModel.GetFileName());

			// int revision = Integer.parseInt(nextModel.GetRevision());

			listPrt.add(new CadPart(nextModel.GetFileName(), 0));

			node.add(leaf);

			System.out.println("level:" + leaf.getLevel() + "   " + leaf.getFileName() );

			//MyTreeNode parent = (MyTreeNode) leaf.getParent();

			//walkDownDeps(nextModel, parent);

			

			nodes.add(leaf);

		}

	}

	public List<CadPart> getPRTList() {
		return this.listPrt;
	}

	public void clearAll() {
		listPrt.clear();
		nodes.clear();
	}

	public Map<String, TreeModel> getModelList() {

		Map<String, TreeModel> map = new HashMap<String, TreeModel>();
		map.put(topLevelAsmName, treeModel);
		return map;

	}

	public Map<String, MyTreeNode> getResultMap() {

		return this.knots;
	}
	
	
}
