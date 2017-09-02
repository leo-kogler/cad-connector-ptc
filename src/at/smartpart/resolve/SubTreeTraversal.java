package at.smartpart.resolve;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcModel.Model;
import com.ptc.pfc.pfcModel.ModelType;
import com.ptc.pfc.pfcSession.Session;

import at.smartpart.beans.MyTreeNode;

public class SubTreeTraversal {
	Map<String, JTree> map = new HashMap<String, JTree>();

	public SubTreeTraversal(Session wfSession, Map<String, DefaultMutableTreeNode> knots) throws jxthrowable {

		

		for (Map.Entry<String, DefaultMutableTreeNode> asm : knots.entrySet()) {
			
			String assembly = asm.getKey();
			Model mdl = wfSession.GetModel(assembly, ModelType.MDL_ASSEMBLY);
			
			System.out.println("creating subasm:" + assembly);
					
				MyTreeTraversal subTraverse = new MyTreeTraversal(mdl, wfSession);
				map.put(assembly, subTraverse.getJTree());
				
			
		}

	}

	public Map<String, JTree> getResultMap() {

		return map;
		
	}
	
public void	clearAll() {
	this.map.clear();
}

}
