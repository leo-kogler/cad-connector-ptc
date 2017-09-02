package at.smartpart.transfer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import at.smartpart.Workbench;
import at.smartpart.config.UserDefinedSettings;

public class CADConnection {

	Connection connection= null;
	
	public CADConnection() {
		try {
			connection = connectJdbc();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
public Connection getConnected() {
		return this.connection;
	}
	


	
	

public Connection connectJdbc() throws ClassNotFoundException {

	Connection connection = null;
	
	UserDefinedSettings set = Workbench.getSettings();

	try {
		Class.forName("com.mysql.jdbc.Driver");

		System.out.println("Connecting to MySQL database...");
		System.out.println(set.DB_URL + " " + set.DB_NAME + " "
				+ set.USER + " " + set.PASS);

		connection = DriverManager.getConnection(set.DB_URL + set.DB_NAME,
				set.USER, set.PASS);
	} catch (ClassNotFoundException e) {

		e.printStackTrace();
		System.out.println(e.toString());

	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println(e.toString());
	}

	return connection;

}

}
