package install;

import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JWindow;

import gui.Theme;
import le.gui.dialogs.LDialogs;
import le.install.AbstractInstall;
import main.Main;

public class Install extends AbstractInstall{
	
	public Install(String path) {
		super(path);
	}
	@Override
	public boolean install() {
		JWindow w = new JWindow();
		w.setLayout(null);
		JLabel l = new JLabel("Installing...");
		l.setBounds(220, 20, 100, 20);
		w.add(l);
		w.setSize(500, 200);
		w.setLocation(433, 284);
		w.setVisible(true);
		
		new File(path).mkdir();
		
		getFile("Gallery").mkdir();
		getFile("Projects").mkdir();
		getFile("Languages").mkdir();
		getFile("Data").mkdir();
		getFile("Data\\Settings").mkdir();
		try {
			getFile("Data\\Settings\\default_setting.txt").createNewFile();
			Preferences.paperWidth = 1000;
			Preferences.paperHeight = 600;
			Preferences.paperZoom = 100;
			Preferences.language = Main.translator.DEFAULT_LANGUAGE;
			Preferences.autoSetDefLan = true;
			Preferences.saveLogs = true;
			Preferences.keepMeLoggedIn = true;
			Preferences.useMoreRAM = true;
			Preferences.darkMode = false;
			Preferences.keepTrackOfTopLayers = true;
			Preferences.numOfBackOperations = -1;
			
			Preferences.saveToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getFile("Data\\Logs").mkdir();
		try {
			getFile("Data\\Logs\\live log.txt").createNewFile();
			getFile("Data\\Settings\\user.txt").createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		w.dispose();
		return true;
	}
	
	public void initPremiumSetting() {
		
	}
	public void initNormalSetting() {
		Preferences.updateFromFile();
		Main.theme = new Theme();
		LDialogs.theme = Main.theme;
		Main.translator.setLanguage(Preferences.language);
	}
}