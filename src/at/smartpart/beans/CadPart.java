package at.smartpart.beans;

// CadPart is the multi mode object for db communication, it might be a drw, asm, prt, or mfg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;

public class CadPart implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fileName;
	private int localVersion;
	private int curVersion;
	private int nextVersion;
	private JTree myAsm;
	@SuppressWarnings("unused")
	private List<CadPart> drwMembers = new ArrayList<CadPart>();
	private boolean upload = false;


	// im a prt here !!!
	
	public CadPart(String fileName, int localVersion) {

		this.fileName = fileName;
		this.localVersion = localVersion;

	}

	
	// im a asm here, obj should be  a tree here
	
	public CadPart(String fileName, int curVersion, Object object) {
		
		
		this.fileName=fileName;
		this.curVersion=curVersion;
		
		try {
			byte[] st = (byte[])object;
			ByteArrayInputStream boi = new ByteArrayInputStream(st);
			ObjectInputStream ois = new ObjectInputStream(boi);
			
			this.myAsm = (JTree) ois.readObject();
		} catch (ClassNotFoundException e) {
			
			System.out.println("//////////clnf\\\\\\\\\\");

			e.printStackTrace();
		} catch (IOException e) {
			
			System.out.println("//////////ioex\\\\\\\\\\");

			e.printStackTrace();
		}
	}
	
	// im a drw here, objcet should be a memberlist here
	
@SuppressWarnings("unchecked")
public void	setMemberList (Object object) {
		try {
			byte[] st = (byte[])object;
			ByteArrayInputStream boi = new ByteArrayInputStream(st);
			ObjectInputStream ois = new ObjectInputStream(boi);
			
			this.drwMembers = (List<CadPart>) ois.readObject();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	//im a drawing here !!
	public CadPart(String fileName, int curVersion, List<CadPart> drwMembers) {
	this.fileName = fileName; this.curVersion=curVersion;
	this.drwMembers = drwMembers;
	}


	public void setCurVersion(int version) {
		this.curVersion = version;
	}

	public int getCurDbVersion() {
		return this.curVersion;
	}

	public void setNextVersion(int nextVersion) {
		this.nextVersion = nextVersion;
	}

	public int getNextVersion() {
		return nextVersion;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLocalVersion() {
		return localVersion;

	}

	public JTree getJTree() {
		return myAsm;
	}
	
	public void setUpload (String string) {
		if(string.equals("true")) {
			upload = true;
		} else {
			upload = false;
		}
	}
	
	public boolean getUpload () {
		return upload;
	}

}
