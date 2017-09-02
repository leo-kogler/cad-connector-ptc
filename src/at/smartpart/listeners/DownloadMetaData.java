package at.smartpart.listeners;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;

import at.smartpart.beans.CadPart;
import at.smartpart.transfer.CADConnection;

public class DownloadMetaData {

	Connection conn = null;
	
	public DownloadMetaData() {

		CADConnection cadcon = new CADConnection();
		conn = cadcon.getConnected();
		
		
	}
	
	public CadPart queryAsm(String prt_number, int version) {

		CadPart cadPart = null;

		String query = "SELECT filename, version, myasm from fingerprint.asms WHERE filename like '" + prt_number +
				"' AND version = '" + version + "';";
		
		System.out.println("QUERY : " + query);

		Statement stmt = null;

		try {
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				
				System.out.println("filename in db: " + rs.getString("filename") + " version in db:  " + rs.getString("version"));

				
				
						cadPart = new CadPart(rs.getString("filename"),
												rs.getInt("version"),
													rs.getObject("myasm"));
						//PRT cadPart = new PRT(resultSet.getString("filename"),0);
						//cadPart.setCurVersion(resultSet.getString("version"));
						//int version = Integer.parseInt(resultSet.getString("version")); //find better solution e.g. set DB to integer itself
						//int version = resultSet.getInt("version");
						//String newVersion = ""+version;
						//cadPart.setCurVersion(version);
						//int nextVersion = version+1;
						//cadPart.setNextVersion(nextVersion);
						//listWithDbVersion.add(cadPart);

			}

			stmt.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return cadPart;
	}

}
