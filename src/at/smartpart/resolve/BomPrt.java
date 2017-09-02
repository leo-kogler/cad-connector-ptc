package at.smartpart.resolve;

public class BomPrt {
	
	private int count;
	private String name;

	public BomPrt (String name, int count){
		this.name = name;
		this.count = count;
	}

	public String getName(){
		return name;
	}
	
	public int getCount () {
		return count;
	}
	
}
