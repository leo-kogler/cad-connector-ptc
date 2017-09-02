package at.smartpart.beans;

import java.util.Comparator;

public class NameComparator implements Comparator<CadPart>{
	
	public int compare(CadPart o1, CadPart o2) {
			
	//	if (o1.getFileName().equals(o2.getFileName())){
			return (o2.getCurDbVersion() - o1.getCurDbVersion());
		//}
		//return 0;
		
	}

}
