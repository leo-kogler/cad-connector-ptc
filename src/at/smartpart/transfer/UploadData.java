package at.smartpart.transfer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JTree;
import at.smartpart.Workbench;
import at.smartpart.beans.CadPart;
import at.smartpart.config.UserDefinedSettings;
import at.smartpart.listeners.UploadListener;

public class UploadData {

	private List<CadPart> drwNodes = new ArrayList<CadPart>();

	public void send(Map<String, JTree> subAsms, Map<String, CadPart> cadParts) {

		Connection connection = null;
		try {
			connection = connectJdbc();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();

			System.out.println(e.toString());
		}

		List<String> list = UploadListener.getList();

		for (String fn : list) {
			System.out.println(fn);
		}

		try {
			upload(list, subAsms, cadParts, connection);
		} catch (FileNotFoundException e) {

			e.printStackTrace();

			System.out.println(e.toString());
		} catch (SQLException e) {

			e.printStackTrace();

			System.out.println(e.toString());
		} catch (IOException e) {

			e.printStackTrace();

			System.out.println(e.toString());
		}

	}

	public Connection connectJdbc() throws ClassNotFoundException {

		Connection connection = null;

		UserDefinedSettings set = Workbench.getSettings();

		try {
			Class.forName("com.mysql.jdbc.Driver");

			System.out.println("Connecting to MySQL database...");
			System.out.println(set.DB_URL + " " + set.DB_NAME + " " + set.USER + " " + set.PASS);

			connection = DriverManager.getConnection(set.DB_URL + set.DB_NAME, set.USER, set.PASS);
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

	public boolean upload(List<String> list, Map<String, JTree> subAsms, Map<String, CadPart> cadParts, Connection conn)
			throws SQLException, IOException {

		String insert = "";
		String dir = Workbench.getWorkingDir();

	/*	for (String file : list) {
			System.out.println(" uploading " + file.toString());
		}
*/
		for (String file : list) {

			PreparedStatement stmt = null;

			String uploadPath = dir + file;

			System.out.println("pth: " + uploadPath);

			CadPart cadprt = cadParts.get(file);
			
			if (cadprt.getUpload()) {

				if (file.contains(".drw")) {

					System.out.println("File" + file);

					insert = "insert into drws (filename, container, version, mydrw) values (?,?,?,?);";
					stmt = conn.prepareStatement(insert);

					// stmt.setInt(1, id);
					stmt.setString(1, file);

					int version = getLatest(file);

					System.out.println("drw ul :" + uploadPath + "." + version);

					File drw = new File(uploadPath + "." + version);

					FileInputStream fis = new FileInputStream(drw);
					stmt.setBinaryStream(2, fis, (int) drw.length());

					int uploadVersion = cadParts.get(file).getNextVersion();

					stmt.setInt(3, uploadVersion);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);

					oos.writeObject(drwNodes);
					oos.flush();
					oos.close();
					bos.close();

					byte[] data = bos.toByteArray();

					stmt.setObject(4, data);

					stmt.executeUpdate();
					stmt.close();

				} else

				if (file.contains(".asm")) {

					System.out.println("File" + file);

					insert = "insert into asms (filename, container, version, myasm) values (?,?,?,?);";
					stmt = conn.prepareStatement(insert);

					// stmt.setInt(1, id);
					stmt.setString(1, file);

					int version = getLatest(file);

					System.out.println("asm ul :" + uploadPath + "." + version);

					File asm = new File(uploadPath + "." + version);

					FileInputStream fis = new FileInputStream(asm);
					stmt.setBinaryStream(2, fis, (int) asm.length());

					int uploadVersion = cadParts.get(file).getNextVersion();

					stmt.setInt(3, uploadVersion);

					JTree subTree = null;
					for (Entry<String, JTree> sub : subAsms.entrySet()) {
						if (file.equals(sub.getKey())) {
							subTree = sub.getValue();
						}
					}

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);

					oos.writeObject(subTree);
					oos.flush();
					oos.close();
					bos.close();

					byte[] data = bos.toByteArray();

					stmt.setObject(4, data);

					stmt.executeUpdate();
					stmt.close();

				} else

				if (file.contains(".prt")) {
					insert = "insert into prts (filename, solid, version) values (?,?,?);";

					stmt = conn.prepareStatement(insert);

					// stmt.setInt(1, id);
					stmt.setString(1, file);

					int version = getLatest(file);

					System.out.println(uploadPath + "." + version);

					File solid = new File(uploadPath + "." + version);

					FileInputStream fis = new FileInputStream(solid);
					stmt.setBinaryStream(2, fis, (int) solid.length());

					int uploadVersion = cadParts.get(file).getNextVersion();
					stmt.setInt(3, uploadVersion);

					stmt.executeUpdate();
					stmt.close();

				}
			}
		}
		return true;

	}

	/*
	 * public boolean sendImage(String filename, Connection conn, int id) {
	 * 
	 * try {
	 * 
	 * String[] fNa = filename.split(Pattern.quote("."));
	 * 
	 * String thumbName = WfWorker.tempImageFile;
	 * 
	 * System.out.println(thumbName);
	 * 
	 * String insert =
	 * "insert into thumbs (id, filename, thumbnail, version) values (?,?,?,?);"
	 * ;
	 * 
	 * PreparedStatement ps = conn.prepareStatement(insert);
	 * 
	 * ps.setInt(1, id);
	 * 
	 * ps.setString(2, fNa[0] + ".jpg");
	 * 
	 * ps.setString(4, "0");// MenuButtonListener.finalVersion);
	 * 
	 * File thumbnail = new File(WfWorker.tempImageFile);
	 * 
	 * FileInputStream fis = new FileInputStream(thumbnail);
	 * ps.setBinaryStream(3, fis, (int) thumbnail.length());
	 * 
	 * ps.executeUpdate(); ps.close();
	 * 
	 * thumbnail.delete();
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * System.out.println("insert Image " + e.toString());
	 * System.out.println("insert Image " + e.toString()); return false;
	 * 
	 * }
	 * 
	 * System.out.println("image sucess..."); return true; }
	 * 
	 * public boolean sendSolid(String filename, Connection conn, int id2) {
	 * 
	 * try {
	 * 
	 * String[] fNa = filename.split(Pattern.quote("."));
	 * 
	 * String solidName = WfWorker.tempSTEPFile;
	 * 
	 * System.out.println(solidName);
	 * 
	 * String insert =
	 * "insert into solids (id, filename, solid, version) values (?,?,?,?);";
	 * 
	 * PreparedStatement ps = conn.prepareStatement(insert);
	 * 
	 * ps.setInt(1, id2);
	 * 
	 * ps.setString(2, fNa[0] + ".stp");
	 * 
	 * ps.setString(4, "0");// MenuButtonListener.finalVersion);
	 * 
	 * File solid = new File(solidName);
	 * 
	 * FileInputStream fis = new FileInputStream(solid); ps.setBinaryStream(3,
	 * fis, (int) solid.length());
	 * 
	 * ps.executeUpdate(); ps.close();
	 * 
	 * solid.getAbsoluteFile().delete();
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * System.out.println("insert solid " + e.toString());
	 * System.out.println("insert solid " + e.toString()); return false;
	 * 
	 * } System.out.println("solid success..."); return true; }
	 */

	public int getLatest(String fileForUpload) throws IOException {

		int innerIndex = 0;
		File directory = new File(Workbench.getWorkingDir());

		System.out.println("wd:" + directory);

		int[] fileVersionOnFileSystem = new int[300];
		/*
		 * String ext =
		 * fileForUpload.substring(fileForUpload.lastIndexOf("\\."),
		 * fileForUpload.length());
		 * 
		 * String fileName = fileForUpload.substring(1,
		 * fileForUpload.lastIndexOf("\\.")); // 0 oder 1 ???
		 */

		File[] fList = directory.listFiles();

		/*
		 * for (int i = 0; i < fList.length; i++) {
		 * System.out.println(fList[i].getAbsolutePath()); }
		 */
		if (fList.length != 0) {

			for (int index = 0; index < fList.length; index++) {

				if (fList[index].getAbsolutePath().contains(fileForUpload)) {

					System.out.println(fList[index].getAbsolutePath());

					String absolutePath = fList[index].getAbsolutePath();

					System.out.println(absolutePath);

					String[] versions = absolutePath.split("\\.");
					String versionOnFileSystem = versions[versions.length - 1];

					// String versionFs =
					// absolutePath.substring(absolutePath.lastIndexOf("\\."),
					// absolutePath.length());

					System.out.println("*********" + versionOnFileSystem);

					int intVersion = Integer.parseInt(versionOnFileSystem);

					System.out.println(fileForUpload + " " + intVersion);

					fileVersionOnFileSystem[innerIndex] = intVersion;
					innerIndex++;

				}

			}
		}

		if (fileVersionOnFileSystem.length != 0) {
			Arrays.sort(fileVersionOnFileSystem);
			return (fileVersionOnFileSystem[fileVersionOnFileSystem.length - 1]);
		} else {
			fileVersionOnFileSystem[0] = 1;
			return fileVersionOnFileSystem[0];
		}
	}

	public void setDrwNodes(List<CadPart> drwNodes) {

		this.drwNodes = drwNodes;

	}

}
