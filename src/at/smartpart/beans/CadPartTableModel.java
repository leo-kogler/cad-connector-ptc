package at.smartpart.beans;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class CadPartTableModel extends AbstractTableModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1293498681719983691L;

	String[] COLUMN_NAMES = { "Filename", "local Version", "current Version", "new Version" };
	
	private static final int COLUMN_IDX_FILENAME = 0;
	private static final int COLUMN_IDX_LOCALVERSION = 1;
	private static final int COLUMN_IDX_CURVERSION = 2;
	private static final int COLUMN_IDX_NEWVERSION = 3;
//	private static final int COLUMN_IDX_FILENAME = 0;
	
	
	private List<CadPart> cadParts;
	
	public CadPartTableModel (List<CadPart> cadParts) {
		this.cadParts = cadParts;
	}

	@Override
	public int getRowCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public int getColumnCount() {
		return cadParts.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		CadPart cadPart = cadParts.get(rowIndex);
		
		if(columnIndex == COLUMN_IDX_FILENAME) return cadPart.getFileName();
		
		if(columnIndex == COLUMN_IDX_LOCALVERSION) return cadPart.getLocalVersion();
		
		if(columnIndex == COLUMN_IDX_CURVERSION) return cadPart.getCurDbVersion();
		
		if(columnIndex == COLUMN_IDX_NEWVERSION) return cadPart.getNextVersion();
		
		throw new IllegalArgumentException("invalid index" + columnIndex);
	}
	@Override
	public String getColumnName(final int columnIndex){
		return COLUMN_NAMES[columnIndex];
	}
	

}
