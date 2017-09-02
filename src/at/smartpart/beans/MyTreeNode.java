package at.smartpart.beans;

import javax.swing.tree.DefaultMutableTreeNode;

public class MyTreeNode extends DefaultMutableTreeNode {

	
	private static final long serialVersionUID = -5728504476345569941L;

	private String name;
	private String version;
	public MyTreeNode left = null;
	public MyTreeNode right = null;

	private int preOrderNum = 0;
	
	
	public MyTreeNode(String name, int key) {
		this.name = name;
		this.preOrderNum = key;

	}
	
	public MyTreeNode(String name) {
		this.name = name;


	}

	public MyTreeNode(int key) {
		this.preOrderNum = key;
	}

	public String getFileName() {
		return name;
	}

	public int getId() {
		return preOrderNum;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setLeft (MyTreeNode left) {
		this.left=left;
	}
	
	public void setRight (MyTreeNode right) {
		this.right=right;
	}
	
}
