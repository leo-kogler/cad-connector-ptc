package at.smartpart;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcCommand.UICommand;
import com.ptc.pfc.pfcGlobal.pfcGlobal;
import com.ptc.pfc.pfcSession.Session;

import at.smartpart.config.UserDefinedSettings;
import at.smartpart.listeners.DownloadListener;
import at.smartpart.listeners.UploadListener;

public class Workbench {

	static String proeVersion;
	static String proeBuild;
	private static String workingDir;
	static UserDefinedSettings settings;

	public static void start() throws jxthrowable {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			System.out.println(e1.toString());
		} catch (InstantiationException e1) {

			e1.printStackTrace();
			System.out.println(e1.toString());
		} catch (IllegalAccessException e1) {

			e1.printStackTrace();
			System.out.println(e1.toString());
		} catch (UnsupportedLookAndFeelException e1) {

			e1.printStackTrace();
			System.out.println(e1.toString());
		}

		settings = new UserDefinedSettings("C:\\CAD\\3d2\\CADPart\\config\\config.properties");

		try {
			System.setOut(new PrintStream(new FileOutputStream("C:\\CAD\\3d2\\CADPart\\out.log")));
			System.setErr(new PrintStream(new FileOutputStream("C:\\CAD\\3d2\\CADPart\\out.log")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		proeVersion = pfcGlobal.GetProEVersion();
		proeBuild = pfcGlobal.GetProEBuildCode();

		Session wfSession = pfcGlobal.GetProESession();

		wfSession.UIAddMenu("SmartPart", "Applications", "menu.txt", null);

		UploadListener uploadListener = new UploadListener(wfSession);

		UICommand cmd = wfSession.UICreateCommand("JL.Up", uploadListener);

		wfSession.UIAddButton(cmd, "SmartPart", null, "-J-Link-checkin", "checkin", "msg.txt");

		DownloadListener downloadListener = new DownloadListener();

		UICommand cmd1 = wfSession.UICreateCommand("JL.Down", downloadListener);

		wfSession.UIAddButton(cmd1, "SmartPart", null, "-J-Link-checkout", "checkout", "msg.txt");

		workingDir = wfSession.GetCurrentDirectory();

	}

	public static void stop() {

	}

	public static String getWorkingDir() {
		return workingDir;

	}

	public static void setWorkDir(String workDir) {

		workingDir = workDir;

	}

	public static UserDefinedSettings getSettings() {

		return settings;
	}

	public static void main(String[] args) {
		System.out.println("dummy_main");
	}

}
