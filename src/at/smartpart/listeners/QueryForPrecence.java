package at.smartpart.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import at.smartpart.beans.CadPart;
import at.smartpart.transfer.CADConnection;

import java.sql.Connection;
public class QueryForPrecence {

	Connection conn = null;
	
	public QueryForPrecence() {

		CADConnection cadcon = new CADConnection();
		conn = cadcon.getConnected();
		
	

	}

	public List<CadPart> query(String prt_number, String type) {

		List<CadPart> listWithDbVersion = new ArrayList<CadPart>();
		
		type = type+"s";

		String query = "SELECT filename, version from fingerprint." + type + " WHERE filename like '" + prt_number +
				"' ORDER BY version ASC;";
		
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
