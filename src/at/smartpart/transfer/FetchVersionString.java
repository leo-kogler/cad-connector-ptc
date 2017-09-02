package at.smartpart.transfer;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import at.smartpart.beans.CadPart;

import java.sql.Connection;
public class FetchVersionString {

	Connection conn = null;
	private boolean containsDrw = false;
	
	public FetchVersionString() {

		CADConnection cadcon = new CADConnection();
		conn = cadcon.getConnected();

	}

	public  List<CadPart> fetch(List<String> toUpload) {
		
		System.out.println("...fetching_sql!");
		
		StringBuilder sbPRT = new StringBuilder();
		StringBuilder sbASM = new StringBuilder();
		StringBuilder sbDRW = new StringBuilder();

		@SuppressWarnings("unused")
		String type = "";
		List<CadPart> curAsmVList = new ArrayList<CadPart>();
		List<CadPart> curPrtVList = new ArrayList<CadPart>();
		List<CadPart> curDrwVList = new ArrayList<CadPart>();

		//find the root = drawing in the list
		
		int drwListIndex = 0;
		for (int x = 0; x < toUpload.size(); x++) {
			if(toUpload.get(x).contains(".drw")){
				drwListIndex = x;
			}
		}
		
		System.out.println( "found drawing at position " + drwListIndex);
		
		if (toUpload.get(drwListIndex).contains(".drw")){
	

		
		
				sbDRW.append("(filename = '");
		
	//	for (int i = 0; i <toUpload.size(); i++) {

			String drw = toUpload.get(drwListIndex);
			
				sbDRW.append(drw + "') or (filename = '");
				type = "drw";
	//		}
		
			containsDrw  = true;
			
			String fileNameAsStringDRW = sbDRW.toString();
			
			System.out.println(fileNameAsStringDRW);
			
			curDrwVList = query(fileNameAsStringDRW, "drw");
			
		}
		
		
			sbPRT.append("(filename = '");
		
		for (int i = 0; i <toUpload.size(); i++) {
			
			String prt = toUpload.get(i);
		
				sbPRT.append(prt + "') or (filename = '");
				type = "prt";
			}
		
		String fileNameAsStringPRT = sbPRT.toString();
		System.out.println(fileNameAsStringPRT);

		curPrtVList = query(fileNameAsStringPRT, "prt");
		
				sbASM.append("(filename = '");
		
		for (int i = 0; i <toUpload.size(); i++) {

			String asm = toUpload.get(i);
			
				sbASM.append(asm + "') or (filename = '");
				type = "asm";
			}
		

		String fileNameAsStringASM = sbASM.toString();
		System.out.println(fileNameAsStringASM);
		
		curAsmVList = query(fileNameAsStringASM, "asm");

		curPrtVList.addAll(curAsmVList);
		
		if (containsDrw) curPrtVList.addAll(curDrwVList);
		
		for(CadPart prt : curPrtVList) {
		
		System.out.println(prt.getFileName()+ "   " + prt.getLocalVersion() + "   " +  prt.getCurDbVersion());
		}
		return curPrtVList;
	}

	public List<CadPart> query(String fileNamesAsString, String type) {

		List<CadPart> listWithDbVersion = new ArrayList<CadPart>();
		
		System.out.println("string: " + fileNamesAsString);
		
		type = type + "s";

		String filenames = fileNamesAsString.substring(0, fileNamesAsString.lastIndexOf(" or (filename = '"));

		String query = "SELECT filename, version from fingerprint." + type + " WHERE " + filenames
				+ " ORDER BY version ASC;";
		
		System.out.println("QUERY : " + query);

		Statement stmt = null;

		try {
			stmt = conn.createStatement();

			ResultSet resultSet = stmt.executeQuery(query);

			while (resultSet.next()) {
				
				System.out.println("filename in db: " + resultSet.getString("filename") + " version in db:  " + resultSet.getString("version"));

				
						CadPart cadPart = new CadPart(resultSet.getString("filename"),0);
						//cadPart.setCurVersion(resultSet.getString("version"));
						//int version = Integer.parseInt(resultSet.getString("version")); //find better solution e.g. set DB to integer itself
						int version = resultSet.getInt("version");
						//String newVersion = ""+version;
						cadPart.setCurVersion(version);
						int nextVersion = version+1;
						cadPart.setNextVersion(nextVersion);
						listWithDbVersion.add(cadPart);

			}

			stmt.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return listWithDbVersion;

	}

}
