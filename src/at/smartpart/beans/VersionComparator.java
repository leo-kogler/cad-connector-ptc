package at.smartpart.beans;

import java.util.Comparator;

public class VersionComparator implements Comparator<CadPart>{
	
	public int compare(CadPart o1, CadPart o2) {
			
	return o1.getFileName().compareTo(o2.getFileName());
	}

}
