package at.smartpart.resolve;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
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
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSolid.Solid;
import at.smartpart.beans.CadPart;

public class MyTreeTraversal {

	Model nextModel;
	Solid curSolid;
	private Session wfSession;
	Assembly memberAsm;

	List<CadPart> listPrt = new ArrayList<CadPart>();
	List<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();

	List<TreePath> pathsAsList = new ArrayList<TreePath>();

	private Map<String, DefaultMutableTreeNode> knots = new LinkedHashMap<String, DefaultMutableTreeNode>();
	private Map<String, TreeModel> drwMemberTrees = new HashMap<String, TreeModel>();
	int x = 1;
	int depth = 0;
	JPanel panel = new JPanel();;

	TreeModel treeModel = null;
	JTree tree = null;

	String topLevelAsmName = "";
	private List<TreeNode> dedupedList = new ArrayList<TreeNode>();
	private List<TreeNode> nodeList = new ArrayList<TreeNode>();
	private List<BomPrt> bom = new ArrayList<BomPrt>();
	private List<String> tempList = new ArrayList<String>();
	private List<TreeNode> tempNodes = new ArrayList<TreeNode>();
	private List<CadPart> drwNodes = new ArrayList<CadPart>();

	public MyTreeTraversal(Model curModel, Session wfSession) throws jxthrowable {

		try {
			System.out.println(curModel.GetFileName());
			topLevelAsmName = curModel.GetFileName();
		} catch (jxthrowable e2) {
			e2.printStackTrace();
		}

		this.wfSession = wfSession;

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(curModel.GetFileName());
		treeModel = new DefaultTreeModel(root);

		System.out.println("creating tree");

		try {

			walkDownDeps(curModel, root);

		} catch (jxthrowable e) {
			e.printStackTrace();
			System.out.println("problem traversing tree " + e.toString());
		}

		/*
		 * for (DefaultMutableTreeNode nod : nodes) { System.out.println("TT" +
		 * nod.getUserObject() + " " + nod.getLevel()); }
		 */
		// System.out.println(knots.toString());

		tree = new JTree(treeModel);

		getPaths(tree, false);

		panel.add(tree, BorderLayout.CENTER);
		panel.setSize(400, 400);
		panel.setVisible(true);

	}

	public MyTreeTraversal(Session session) {
		this.wfSession = session;
	}

	public void createBom() {

		for (TreePath path : pathsAsList) {
			TreeNode component = (TreeNode) path.getLastPathComponent();
			tempNodes.add(component);
		}

		System.out.println("populating list");
		for (TreeNode usrObj : dedupedList) {
			int count = countOcc(tempNodes, usrObj);
			BomPrt bomPrt = new BomPrt(usrObj.toString(), count);
			bom.add(bomPrt);
		}

		System.out.println("deduped" + dedupedList.size());
		System.out.println("temp" + tempNodes.size());
		System.out.println("deduped" + tempList.size());

		for (BomPrt bomPrt : bom) {
			String name = bomPrt.getName();
			int count = bomPrt.getCount();

			System.out.println("Object: " + name + " pcs.: " + count);

		}

	}

	public int countOcc(List<TreeNode> nodes, TreeNode node) {
		int count = 0;
		for (TreeNode item : nodes) {

			String sItem = ((DefaultMutableTreeNode) item).getUserObject().toString();
			String sNode = ((DefaultMutableTreeNode) node).getUserObject().toString();

			System.out.println("s:" + sItem);
			System.out.println("n:" + sNode);

			if (sItem.equals(sNode)) {
				count++;
			}
		}
		return count;
	}

	List<DefaultMutableTreeNode> getNodeList() {

		return this.nodes;
	}

	TreeModel cloneTree() {
		return treeModel;
	}

	public JPanel getTreePanel() {
		return this.panel;
	}

	public void walkDownDeps(Model curModel, DefaultMutableTreeNode node) throws jxthrowable {

		DefaultMutableTreeNode leaf;

		if (curModel.GetType() == ModelType.MDL_ASSEMBLY) {

			memberAsm = (Assembly) curModel;

			leaf = new DefaultMutableTreeNode();
			leaf.setUserObject(memberAsm.GetFileName());

			node.add(leaf);
			nodes.add(leaf);

			System.out.println("level: " + leaf.getLevel() + "   " + leaf.getUserObject());

			if (!knots.containsKey(leaf.getUserObject()))
				knots.put(leaf.getUserObject().toString(), leaf);

			listPrt.add(new CadPart(memberAsm.GetFileName(), 0));

			Features comps = memberAsm.ListFeaturesByType(Boolean.FALSE, FeatureType.FEATTYPE_COMPONENT);

			for (int index = 0; index < comps.getarraysize(); index++) {

				ComponentFeat compFeature = (ComponentFeat) comps.get(index);

				ModelDescriptor description = compFeature.GetModelDescr();

				String wfFileName = description.GetFileName();

				System.out.println("wfModel " + wfFileName);

				// nextModel = wfSession.GetModelFromDescr(description);

				nextModel = wfSession.GetModelFromFileName(wfFileName);

				if (nextModel != null) {
					System.out.println("wfModel " + wfFileName);
					walkDownDeps(nextModel, leaf);
				} else {
					System.out.println("model is null!");
				}

			}

		} else {

			nextModel = curModel;

			leaf = new DefaultMutableTreeNode();// curModel.GetFileName());
			leaf.setUserObject(curModel.GetFileName());

			listPrt.add(new CadPart(nextModel.GetFileName(), 0));

			node.add(leaf);

			System.out.println("level:" + leaf.getLevel() + "   " + leaf.getUserObject());

			nodes.add(leaf);

		}

		System.out.println("counter: " + x);
		x++;
	}

	public void getDRWMembers(Model model) throws jxthrowable {

		List<Model> drwMembers = new ArrayList<Model>();

		System.out.println("generating Members Array for Drawing");

		Drawing drw = (Drawing) model;
		int noOfSheets = drw.GetNumberOfSheets();

		System.out.println("Number of Sheets = " + noOfSheets);

		Dependencies deps = drw.ListDependencies();

		// RESOLVE ALL MEMBERS DECLARED IN DRAWING AND PUT IN ARRAY

		for (int y = 0; y < deps.getarraysize(); y++) {
			ModelDescriptor desc = deps.get(y).GetDepModel();
			String filename = desc.GetFileName();
			Model mdl = wfSession.GetModelFromFileName(filename);
			drwMembers.add(mdl);
			drwNodes.add(new CadPart(filename, -3));

		}

		// CREATE ALL METADATA FOR CHECK IN :

		nodes.clear();

		if (drwMembers.size() > 0) {
			for (Model mdl : drwMembers) {
				System.out.println("Model : " + mdl.GetFileName().toString());

				if (mdl.GetType() == ModelType.MDL_ASSEMBLY) {

					DefaultMutableTreeNode root = new DefaultMutableTreeNode(mdl.GetFileName());
					TreeModel tm = new DefaultTreeModel(root);

					nodes.clear();
					walkDownDeps(mdl, root);

					drwMemberTrees.put(mdl.GetFileName(), tm);

				}

				if (mdl.GetType() == ModelType.MDL_PART) {

					listPrt.add(new CadPart(mdl.GetFileName(), 0));
				}

			}
		}

	}

	public TreePath[] getPaths(JTree tree, boolean expanded) {
		TreeNode node = (TreeNode) tree.getModel().getRoot();
		List<TreePath> pathList = new ArrayList<TreePath>();
		getPaths(tree, new TreePath(node), expanded, pathList);
		pathsAsList = pathList;
		return (TreePath[]) pathList.toArray(new TreePath[pathList.size()]);
	}

	public void getPaths(JTree tree, TreePath parent, boolean expanded, List<TreePath> list) {
		if (expanded && !tree.isVisible(parent)) {
			return;
		}
		list.add(parent);
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		nodeList.add(node);
		if (node.getChildCount() > 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
				////////////////////////////////////////////////////////////
				//
				// String userObj = (String) n.getUserObject();
				// System.out.println("obj : " + userObj);
				if (!tempList.contains(n.getUserObject().toString())) {
					tempList.add(n.getUserObject().toString());
					dedupedList.add(n);

				}

				////////////////////////////////////////////////////////////
				TreePath path = parent.pathByAddingChild(n);
				getPaths(tree, path, expanded, list);
			}
		}
	}

	public void countOccurencesForItem(TreePath usrObj) {
		int occurence = Collections.frequency(pathsAsList, usrObj);
		System.out.println(usrObj.getLastPathComponent() + " occ " + occurence);
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

	public Map<String, DefaultMutableTreeNode> getResultMap() {

		return this.knots;
	}

	public List<TreeNode> getDedupedList() {
		return this.dedupedList;
	}

	public JTree getJTree() {
		return tree;
	}

	public Map<String, TreeModel> getDRWMemberObject() {
		return this.drwMemberTrees;
	}
	
	public List<CadPart> getDrwNodes () {
		return this.drwNodes;
	}

}
