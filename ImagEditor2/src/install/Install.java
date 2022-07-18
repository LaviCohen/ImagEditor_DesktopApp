package install;

import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JWindow;

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
			DefaultSettings.paperWidth = 1000;
			DefaultSettings.paperHeight = 600;
			DefaultSettings.paperZoom = 100;
			DefaultSettings.language = Main.translator.DEFAULT_LANGUAGE;
			DefaultSettings.autoSetDefLan = true;
			DefaultSettings.saveLogs = true;
			DefaultSettings.keepMeLoggedIn = true;
			DefaultSettings.useMoreRAM = true;
			DefaultSettings.darkMode = false;
			
			DefaultSettings.saveToFile();
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
		DefaultSettings.updateFromFile();
		Main.translator.setLanguage(DefaultSettings.language);
	}
}