package webServices;

import javax.swing.JOptionPane;

import install.saveSystem.Project;
import le.gui.dialogs.LDialogs;
import main.Main;

public class WebProjectsUtils {
	
	public static boolean isAccountConnected() {
		return Main.myAccount != Account.LOCAL_ACCOUNT;
	}
	public static Project loadProjectFromWeb(String name) {
		if (!isAccountConnected()) {
			JOptionPane.showMessageDialog(Main.f, "You aren't logged in to your account, please "
					+ "login to your account.", "Warning", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		String params = "username=" + Main.myAccount.userName + "&password=" + Main.myAccount.password
				+ "&projectName=" + name;
		String response = Main.website.getResponse("/getProjectData.php", params, "POST");
		return new Project(response);
	}
	public static String[] getProjectsList() {
		if (!isAccountConnected()) {
			JOptionPane.showMessageDialog(Main.f, "You aren't logged in to your account, please "
					+ "login to your account.", "Warning", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		String params = "username=" + Main.myAccount.userName + "&password=" + Main.myAccount.password;
		String response = Main.website.getResponse("/getProjectsList.php", params, "POST");
		System.out.println(response);
		if (response.equals("")) {
			return null;
		}
		return response.split("&&");
	}
	public static void uploadProject(Project p) {
		if (!isAccountConnected()) {
			LDialogs.showMessageDialog(Main.f, "You aren't logged in to your account, please "
					+ "login to your account.", "Warning", LDialogs.WARNING_MESSAGE);
			return;
		}
		if (p.name == null) {
			p.name = LDialogs.showInputDialog("Please enter your project name:");
		}
		String params = "projectName=" + p.name + "&" + "data=" + p.getData() + "&username=" + 
				Main.myAccount.userName + "&password=" + Main.myAccount.password + "&app=yes&"
				 + "&quiet=yes";
		System.out.println(params);
		String response = Main.website.getResponse("/uploadProject.php", params, "POST");
		if(response.equals("Your username or password are incorrect.")) {
			JOptionPane.showMessageDialog(Main.f, "We had issues with your request, please try to "
					+ "logout and re-login to your account.", "Warning", JOptionPane.WARNING_MESSAGE);
		}else if(response.equals("Project uploaded successfuly")) {
			JOptionPane.showMessageDialog(null, "We've uploaded your project successfuly");
		}else {
			try {
				throw new Exception("Can't upload project " + p.name + "\n" + response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
