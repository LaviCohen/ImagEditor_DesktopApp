package languages;


import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import install.DefaultSettings;
import le.install.AbstractInstall;
import le.languages.AbstractTranslator;
import main.Main;

public class Translator extends AbstractTranslator{
	
	public Translator(AbstractInstall install) {
		super(install);
	}

	@Override
	public void showChangeLanguageDialog() {
		File f = this.install.getFile("Languages");
		String[] allLanguages = f.list();
		String[] displayLanguages = new String[allLanguages.length + 1];
		displayLanguages[0] = DEFAULT_LANGUAGE;
		for (int i = 1; i < displayLanguages.length; i++) {
			displayLanguages[i] = 
					allLanguages[i - 1].substring(0, allLanguages[i - 1].indexOf('.'));
		}
		Object ans = JOptionPane.showInputDialog(Main.f, "Choose Language:", "Languages",
				JOptionPane.QUESTION_MESSAGE, null, displayLanguages, 
				getLanguageName());
		if (ans == null) {
			return;
		}
		setLanguage(ans.toString());
		if (DefaultSettings.autoSetDefLan || JOptionPane.showConfirmDialog(Main.f,
					"Do you want to make " + ans + " your default language?") == JOptionPane.YES_OPTION) {
			DefaultSettings.language = ans.toString();
			try {
				DefaultSettings.saveToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(Main.f, 
				"<html>To change the language properly,<br/>"
				+ "close the program and reopen it.</html>");
		}
	}
}