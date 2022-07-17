package webServices;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JOptionPane;

import le.web.AbstractWebsite;
import main.Main;

public class Website extends AbstractWebsite{

	public Website(String webAdress) {
		super(webAdress);
	}
	
	public void checkUpdate() {
		System.out.println("Checking internet connection");
		if (!checkInternetConnection()) {
			System.out.println("no internet connection");
			JOptionPane.showMessageDialog(Main.f, "<html>Your\'e have not an internet connection.<br/>"
					+ "Please connect for full support and features.</html>", "No Internet Connection", JOptionPane.WARNING_MESSAGE);
			return;
		}
		System.out.println("Checking if the website is avaliable");
		if (!checkWebsite()) {
			System.out.println("website isn't avaliable");
			JOptionPane.showMessageDialog(Main.f, "<html>ImagEditor web support isn't avaliable now.<br/>"
					+ "Please try again later.</html>", "Web Support Problem", JOptionPane.WARNING_MESSAGE);
			return;
		}
		URL url;
		try {
			url = new URL(webAddress + "latest-version.txt");
			InputStream is = url.openStream();
			Scanner reader = new Scanner(is);
			double version = reader.nextDouble();
			if (version <= Main.version) {
				reader.close();
				is.close();
				return;
			}
			String s = "";
			if (reader.hasNextLine()) {
				reader.nextLine();
			}
			while(reader.hasNext()) {
				s += reader.nextLine();
				if (reader.hasNext()) {
					s += "\n";
				}
			}
			int answer = JOptionPane.showConfirmDialog(Main.f, "<html>There are a new version."
					+ "<br/>Do you want to take a look?"
					+ "<br/>new features:<ol><li>" + s.replaceAll("\n", "</li><li>") + "</li></ol>"
					+ "</html>");
			if (answer == JOptionPane.YES_OPTION) {
				openInBrowser();
			}
			reader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
