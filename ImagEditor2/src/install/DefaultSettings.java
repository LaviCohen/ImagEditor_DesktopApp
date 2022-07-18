package install;

import java.io.FileNotFoundException;
import java.io.IOException;

import le.install.DataFile;
import main.Main;


public class DefaultSettings {

	public static DataFile default_setting;
	
	public static int paperWidth;
	public static int paperHeight;
	public static int paperZoom;
	
	public static String language;
	public static boolean autoSetDefLan;
	
	public static boolean saveLogs;
	
	public static boolean keepMeLoggedIn;

	public static boolean useMoreRAM;
	
	public static boolean darkMode;
	
	static {
		System.out.println("Reading");
		default_setting = new DataFile(Main.install.getFile("Data\\Settings\\default_setting.txt"));
	}
	
	public static void updateFromFile() {
		paperWidth = Integer.parseInt(default_setting.get("paper_width"));
		paperHeight = Integer.parseInt(default_setting.get("paper_height"));
		paperZoom = Integer.parseInt(default_setting.get("zoom"));
		language = default_setting.get("language");
		autoSetDefLan = Boolean.parseBoolean(default_setting.get("auto_set_def_lan"));
		saveLogs = Boolean.parseBoolean(default_setting.get("save_log_files"));
		keepMeLoggedIn = Boolean.parseBoolean(default_setting.get("keep_me_logged_in"));
		useMoreRAM = Boolean.parseBoolean(default_setting.get("use_more_ram"));
		darkMode = Boolean.parseBoolean(default_setting.get("dark_mode"));
	}
	public static void saveToFile() throws FileNotFoundException, IOException {
		default_setting.putWithoutSave("paper_width", paperWidth);
		default_setting.putWithoutSave("paper_height", paperHeight);
		default_setting.putWithoutSave("zoom", paperZoom);
		default_setting.putWithoutSave("language", language);
		default_setting.putWithoutSave("auto_set_def_lan", autoSetDefLan);
		default_setting.putWithoutSave("save_log_files", saveLogs);
		default_setting.putWithoutSave("keep_me_logged_in", keepMeLoggedIn);
		default_setting.putWithoutSave("use_more_ram", useMoreRAM);
		default_setting.putWithoutSave("dark_mode", darkMode);
		default_setting.save("Original Default Settings");
	}
}