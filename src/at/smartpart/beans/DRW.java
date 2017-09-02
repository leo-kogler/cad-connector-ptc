package at.smartpart.beans;

import java.util.List;

public class DRW {
	
	private String fileName;
	private List<CadPart> members;

	public DRW (String fileName, List<CadPart> members) {
		
		this.fileName = fileName;
		this.members = members;
		
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	} 
	
	public List<CadPart> getMembers() {
		return members;
	}
	
	public void setMembers(List<CadPart> members) {
		this.members = members;
	}

}
