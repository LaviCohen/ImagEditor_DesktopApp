package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import install.DefaultSettings;
import install.Resources;
import install.saveSystem.Project;
import le.gui.components.LSlider;
import le.gui.components.LTextArea;
import le.gui.components.LTextField;
import le.gui.dialogs.LDialogs;
import le.log.Logger;
import shapes.Code;
import shapes.Picture;
import shapes.Rectangle;
import shapes.Text;
import webServices.WebProjectsUtils;
/** This class is the user-actions handler.
 * The actions which handled inside this class are shortcuts and menu actions. 
 * **/
public class Actions {
	/** This method take the action command as a string and call the right method in this class.
	 * @param command - The action string command.
	 * */
	public static void action(String command) {
		if (command.equals("Export Image")) {
			exportImage();
		}else if (command.equals("Preferences")) {
			openPreferencesDialog();
		}else if (command.equals("Set Paper Size")) {
			try {
				Main.getBoard().setPaperSize(
						Integer.parseInt(LDialogs.showInputDialog(Main.f, "Enter Width:")),
						Integer.parseInt(LDialogs.showInputDialog(Main.f, "Enter Height:")));
			} catch (Exception e) {
				if (!(e instanceof NumberFormatException)) {
					e.printStackTrace();
				}
			}
		}else if (command.equals("Save Project")) {
			save();
		}else if (command.equals("Save Project As...")) {
			saveAs();
		}else if (command.equals("Open Project from this Computer")) {
			openProjectFromThisComputer();
		}else if (command.equals("Open Project from Web")) {
			openProjectFromWeb();
		}else if (command.equals("Upload Project")) {
			WebProjectsUtils.uploadProject(Main.currentProject);
		}else if (command.equals("Set Language")) {
			showChangeLanguageDialog();
		}else if (command.equals("Rectangle")) {
			addRectagle();
		}else if (command.equals("Text")) {
			addText();
		}else if (command.equals("Picture")) {
			addPicture();
		}else if (command.equals("Code")) {
			addCode();
		}else if (command.equals("Edit")) {
			edit();
		}else if (command.equals("Refresh")) {
			Main.getBoard().repaint();
			Main.updateShapeList();
		}else if(command.equals("Profile")) {
			Main.myAccount.showAccount();
		}else if (command.equals("Visit Website")) {
			Main.website.openInBrowser();
		}else if (command.equals("Send Report")) {
			sendReport();
		}else if (command.equals("Log")) {
			openLog();
		}
	}
	/**
	 * Opens the preferences dialog.
	 * */
	public static void openPreferencesDialog() {
		JDialog preferencesDialog = new JDialog(Main.f, "User Preferences");
		preferencesDialog.getContentPane().setBackground(Main.theme.getBackgroundColor());
		preferencesDialog.setLayout(new BorderLayout());
		preferencesDialog.add(Main.theme.affect(new JLabel("Change the default settings:")),
				BorderLayout.NORTH);
		
		//Using tabbed pane to sort all preferences by categories
		JTabbedPane tabbedPane = new JTabbedPane();
		Main.theme.affect(tabbedPane);
		
		//Paper's preferences tab, including width, height and zoom
		JPanel paperPrefsPanel = new JPanel(new GridLayout(3, 1));
		//Width GUI
		JPanel widthPanel = new JPanel(new BorderLayout());
		widthPanel.add(Main.theme.affect(new JLabel("Width:")), Main.translator.getBeforeTextBorder());
		JTextField widthField = new JTextField(DefaultSettings.paperWidth + "");
		Main.theme.affect(widthField);
		widthPanel.add(widthField);
		paperPrefsPanel.add(widthPanel);
		//Height GUI
		JPanel heightPanel = new JPanel(new BorderLayout());
		heightPanel.add(Main.theme.affect(new JLabel("Height:")), Main.translator.getBeforeTextBorder());
		JTextField heightField = new JTextField(DefaultSettings.paperHeight + "");
		Main.theme.affect(heightField);
		heightPanel.add(heightField);
		paperPrefsPanel.add(heightPanel);
		//Zoom GUI
		LSlider zoomSlider = new LSlider("Zoom", Main.getZoomSlider().slider.getMinimum(),
				Main.getZoomSlider().slider.getMaximum(), DefaultSettings.paperZoom);
		Main.theme.affect(zoomSlider);
		paperPrefsPanel.add(zoomSlider);
		
		tabbedPane.addTab("Paper", paperPrefsPanel);

		//Language's preferences
		JPanel languagePrefsPanel = new JPanel(new GridLayout(2, 1));
		//Default language GUI
		JPanel defLanPanel = new JPanel(new BorderLayout());
		defLanPanel.add(Main.theme.affect(new JLabel("Defalut Language:")),
				Main.translator.getBeforeTextBorder());
		//The language picking JComboBox
		File f = Main.install.getFile("Languages");
		String[] allLanguages = f.list();
		String[] displayLanguages = new String[allLanguages.length + 1];
		displayLanguages[0] = Main.translator.DEFAULT_LANGUAGE;
		for (int i = 1; i < displayLanguages.length; i++) {
			displayLanguages[i] = 
					allLanguages[i - 1].substring(0, allLanguages[i - 1].indexOf('.'));
		}
		JComboBox<String> chooseLanguge = new JComboBox<>(displayLanguages);
		Main.theme.affect(chooseLanguge);
		chooseLanguge.setSelectedItem(DefaultSettings.language);
		defLanPanel.add(chooseLanguge);
		languagePrefsPanel.add(defLanPanel);
		//Auto-set default language
		JCheckBox autoSetDefLan = new JCheckBox("Auto-set Defalut Language When Changing Language", DefaultSettings.autoSetDefLan);
		Main.theme.affect(autoSetDefLan);
		languagePrefsPanel.add(autoSetDefLan);
		
		tabbedPane.addTab("Language", languagePrefsPanel);
		
		preferencesDialog.add(tabbedPane);
		
		//Appearance tab
		JPanel appearancePrefsPanel = new JPanel(new GridLayout(1, 1));
		
		//Dark mode
		JCheckBox darkModeCheckBox = new JCheckBox("Dark Mode", DefaultSettings.darkMode);
		Main.theme.affect(darkModeCheckBox);
		appearancePrefsPanel.add(darkModeCheckBox);
		
		tabbedPane.addTab("Appearance", appearancePrefsPanel);
		
		//Advanced tab (save logs and CPU vs RAM priority)
		JPanel advancedPrefsPanel = new JPanel(new GridLayout(2, 1));
		
		//Save logs
		JCheckBox saveLogsCheckBox = new JCheckBox("Save the Logs Every time the Program is Being Used", DefaultSettings.saveLogs);
		Main.theme.affect(saveLogsCheckBox);
		advancedPrefsPanel.add(saveLogsCheckBox);
		
		//CPU vs RAM priority
		JCheckBox useRAMCheckBox = new JCheckBox("Use More RAM to Reduce CPU & GPU Usage", DefaultSettings.useMoreRAM);
		Main.theme.affect(useRAMCheckBox);
		advancedPrefsPanel.add(useRAMCheckBox);
		
		tabbedPane.addTab("Advanced", advancedPrefsPanel);
		
		//Apply button
		JButton apply = new JButton("Apply Preferences");
		Main.theme.affect(apply);
		//The code inside the listeners update the defaults to the values in the dialog's input components
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Updating paper dimension
				try {
					int width = Integer.parseInt(widthField.getText());
					int height = Integer.parseInt(heightField.getText());
					DefaultSettings.paperWidth = width;
					DefaultSettings.paperHeight = height;
				} catch (Exception e2) {
					if (e2 instanceof NumberFormatException) {
						LDialogs.showMessageDialog(Main.f, "Error while parsing paper dimensions",
								"Parsing Error", LDialogs.ERROR_MESSAGE);
					}else {
						e2.printStackTrace();
					}
				}
				
				//Updating zoom
				DefaultSettings.paperZoom = zoomSlider.getValue();
				//Updating Default Language
				DefaultSettings.language = chooseLanguge.getSelectedItem().toString();
				//Updating auto-set default language
				DefaultSettings.autoSetDefLan = autoSetDefLan.isSelected();
				//Updating dark mode
				DefaultSettings.darkMode = darkModeCheckBox.isSelected();
				//Updating save logs
				DefaultSettings.saveLogs = saveLogsCheckBox.isSelected();
				//Updating use more RAM
				DefaultSettings.useMoreRAM = useRAMCheckBox.isSelected();
				//Saving the settings to the default settings file
				try {
					DefaultSettings.saveToFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				System.out.println("Preferences Has Been Applied Successfuly");
				
				preferencesDialog.dispose();
			}
		});
		preferencesDialog.add(apply, BorderLayout.SOUTH);
		
		//Displaying the dialog
		preferencesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		preferencesDialog.pack();
		preferencesDialog.setVisible(true);
	}
	/**
	 * Shows dialog to open project from the web.
	 * */
	public static void openProjectFromWeb() {
		if (!WebProjectsUtils.isAccountConnected()) {
			LDialogs.showMessageDialog(Main.f, "You aren't logged in to your account, please "
					+ "login to your account.", "Warning", LDialogs.WARNING_MESSAGE);
			return;
		}
		
		String[] allProjects = WebProjectsUtils.getProjectsList();
		
		if (allProjects == null) {
			LDialogs.showMessageDialog(Main.f, "You haven't any projects on the web."
					, "Warning", LDialogs.WARNING_MESSAGE);
			return;
		}
		
		JDialog d = new JDialog(Main.f, "Choose Project");
		
		d.setLayout(new GridLayout(allProjects.length, 1));
		
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				d.dispose();
				JDialog loadingDialog = new JDialog(Main.f);
				loadingDialog.setTitle("Loading...");
				loadingDialog.setSize(300, 300);
				loadingDialog.setLayout(new BorderLayout());
				JLabel upSent = new JLabel("Downloading your project, please wait", SwingConstants.CENTER);
				upSent.setFont(new Font("Arial", Font.PLAIN, 18));
				loadingDialog.add(upSent, BorderLayout.NORTH);
				JLabel gif = new JLabel(Resources.loading, SwingConstants.CENTER);
				loadingDialog.add(gif);
				loadingDialog.getContentPane().setBackground(Color.WHITE);
				loadingDialog.pack();
				loadingDialog.setSize(loadingDialog.getWidth() + 200, loadingDialog.getHeight());
				loadingDialog.setVisible(true);
				loadingDialog.setResizable(false);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Project p = WebProjectsUtils.loadProjectFromWeb(e.getActionCommand());
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								Main.switchProject(p);
								loadingDialog.dispose();
							}
						});
					}
				}).start();
			}
		};
		for (String projectName : allProjects) {
			JButton b = new JButton(projectName);
			Main.theme.affect(b);
			b.addActionListener(listener);
			d.add(b);
		}
		
		d.pack();
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setVisible(true);
		
	}
	/**
	 * Shows JFileChooser to open project from the local storage.
	 * */
	public static void openProjectFromThisComputer() {
		JFileChooser fc = new JFileChooser(Main.install.getPath("/Projects"));
		fc.showOpenDialog(Main.f);
		if (fc.getSelectedFile() != null) {
			Main.switchProject(Project.loadProject(fc.getSelectedFile()));		
		}
	}
	public static void save() {
		if (Main.currentProject.hasFile()) {
			int ans = LDialogs.showConfirmDialog(Main.f, 
					"Do you want to save your project into its current location?");
			if (ans == LDialogs.YES_OPTION) {
				try {
					Main.currentProject.save();
					System.out.println("Saving project to " + 
							Main.currentProject.folder + "\\" + Main.currentProject.name + ".iep");
					LDialogs.showMessageDialog(Main.f, "Project has been saved successfuly");
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		saveAs();
	}
	/**
	 * Method that pop up a dialog to ask where to  save the project, and than save it as a project.
	 * */
	public static void saveAs() {
		JDialog saveDialog = new JDialog(Main.f);
		saveDialog.setTitle("Save As Project");
		saveDialog.setLayout(new GridLayout(3, 1));
		JPanel dirPanel = new JPanel(new BorderLayout());
		Main.theme.affect(dirPanel);
		dirPanel.add(Main.theme.affect(new JLabel("Directory:")), Main.translator.getBeforeTextBorder());
		JTextField dirField = new JTextField();
		Main.theme.affect(dirField);
		dirField.setEditable(false);
		if (Main.currentProject.folder != null) {
			dirField.setText(Main.currentProject.folder);
		}
		dirPanel.add(dirField);
		saveDialog.add(dirPanel);
		JButton browse = new JButton("Browse");
		Main.theme.affect(browse);
		browse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = dirField.getText().equals("")?
						new JFileChooser(Main.install.getPath("Projects")):new JFileChooser(new File(dirField.getText()));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showOpenDialog(saveDialog);
				File f = fc.getSelectedFile();
				dirField.setText(f.getAbsolutePath());
			}
		});
		dirPanel.add(browse, Main.translator.getAfterTextBorder());
		JPanel namePanel = new JPanel(new BorderLayout());
		Main.theme.affect(namePanel);
		namePanel.add(Main.theme.affect(new JLabel("Name:")), Main.translator.getBeforeTextBorder());
		JTextField nameField = new JTextField("project");
		Main.theme.affect(nameField);
		if (Main.currentProject.name != null) {
			nameField.setText(Main.currentProject.name);
		}
		namePanel.add(nameField);
		JLabel suffixLabel = new JLabel("<html><t/>.iep</html>");
		Main.theme.affect(suffixLabel);
		namePanel.add(suffixLabel, Main.translator.getAfterTextBorder());
		saveDialog.add(namePanel);
		JButton save = new JButton("Save");
		Main.theme.affect(save);
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.currentProject.folder = dirField.getText();
				Main.currentProject.name = nameField.getText();
				System.out.println("Saving project to " + 
						dirField.getText() + "\\" + nameField.getText() + ".iep");
				LDialogs.showMessageDialog(Main.f, "Project has been saved successfuly");
				try {
					Main.currentProject.save();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				saveDialog.dispose();
			}
		});
		saveDialog.add(save);
		saveDialog.pack();
		saveDialog.setVisible(true);
	}
	/**
	 * Method that create two GUI dialogs, one for general log and one for error log.
	 * @see Logger
	 * */
	public static void openLog() {
		//General log
		JDialog generalLog = new JDialog(Main.f);
		generalLog.getContentPane().setBackground(Main.theme.getBackgroundColor());
		generalLog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		generalLog.setTitle(Main.translator.get("General Log"));
		generalLog.setLayout(new BorderLayout());
		generalLog.add(
				Main.theme.affect(new JLabel("<html><b>" + 
					Main.translator.get("General Log") + "</b></html>")), BorderLayout.NORTH);
		String styledText = "<html>" + Main.logger.getLog().replaceAll("\n", "<br/>") + "</html>";
		generalLog.add(Main.theme.affect(new JScrollPane(Main.theme.affect(new JLabel(styledText)))));
		JButton copyGeneralLog = new JButton("Copy");
		Main.theme.affect(copyGeneralLog);
		copyGeneralLog.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Main.logger.getLog()), null);
			}
		});
		generalLog.add(copyGeneralLog, BorderLayout.SOUTH);
		generalLog.pack();
		generalLog.setSize(generalLog.getWidth() > 500 ? 500 : generalLog.getWidth(),
				generalLog.getHeight() > 500 ? 500 : generalLog.getHeight());
		generalLog.setVisible(true);
		//Error log
		JDialog errorLog = new JDialog(Main.f);
		errorLog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		errorLog.setTitle(Main.translator.get("Error Log"));
		errorLog.setLayout(new BorderLayout());
		errorLog.add(Main.theme.affect(new JLabel("<html><b>" + Main.translator.get("Error Log") + "</b></html>")), BorderLayout.NORTH);
		styledText = "<html>" + Main.logger.getErrorLog().replaceAll("\n", "<br/>") + "</html>";
		errorLog.add(Main.theme.affect(new JScrollPane(Main.theme.affect(new JLabel(styledText)))));
		JButton copyErrorLog = new JButton("Copy");
		Main.theme.affect(copyErrorLog);
		copyErrorLog.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Main.logger.getErrorLog()), null);
			}
		});
		errorLog.add(copyErrorLog, BorderLayout.SOUTH);
		errorLog.pack();
		errorLog.setBounds(generalLog.getWidth(), 0,
				errorLog.getWidth() > 500 ? 500 : errorLog.getWidth(), errorLog.getHeight() > 500 ? 500 : errorLog.getHeight());
		errorLog.setVisible(true);
	}
	/**
	 * Shows dialog to send report to the website.
	 * */
	public static void sendReport() {
		JDialog reportDialog = new JDialog(Main.f);
		reportDialog.setTitle("Report");
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(Main.theme.affect(new JLabel(Main.translator.get("Title") + ":")),
				Main.translator.getBeforeTextBorder());
		LTextField titleField = new LTextField("Your report title here");
		Main.theme.affect(titleField);
		headerPanel.add(titleField);
		reportDialog.add(headerPanel, BorderLayout.NORTH);
		LTextArea contentArea = new LTextArea("Your report content here");
		Main.theme.affect(contentArea);
		reportDialog.add(contentArea);
		JButton send = new JButton("Send");
		Main.theme.affect(send);
		reportDialog.add(send, BorderLayout.SOUTH);
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LDialogs.showMessageDialog(reportDialog,
					Main.website.sendReport(Main.myAccount.userName, 
						titleField.getText(), contentArea.getText()));
				reportDialog.dispose();
			}
		});
		reportDialog.pack();
		reportDialog.setVisible(true);
		reportDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	/**
	 * Method that will export the project as an image file (jpg, png etc.)
	 * */
	public static void exportImage() {
		JDialog saveDialog = new JDialog(Main.f);
		saveDialog.setTitle("Export As an Image");
		saveDialog.setLayout(new GridLayout(3, 1));
		JPanel dirPanel = new JPanel(new BorderLayout());
		dirPanel.add(Main.theme.affect(new JLabel("Directory:")), Main.translator.getBeforeTextBorder());
		JTextField dirField = new JTextField();
		Main.theme.affect(dirField);
		dirField.setEditable(false);
		dirPanel.add(dirField);
		saveDialog.add(dirPanel);
		JButton browse = new JButton("Browse");
		Main.theme.affect(browse);
		browse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = dirField.getText().equals("")?
						new JFileChooser(Main.install.getPath("Gallery")):new JFileChooser(new File(dirField.getText()));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showOpenDialog(saveDialog);
				File f = fc.getSelectedFile();
				dirField.setText(f.getAbsolutePath());
			}
		});
		dirPanel.add(browse, Main.translator.getAfterTextBorder());
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.add(Main.theme.affect(new JLabel("Name")), Main.translator.getBeforeTextBorder());
		JTextField nameField = new JTextField("picture");
		Main.theme.affect(nameField);
		namePanel.add(nameField);
		JComboBox<String> typeBox = new JComboBox<String>(new String[] {".png", ".jpg"});
		Main.theme.affect(typeBox);
		namePanel.add(typeBox, Main.translator.getAfterTextBorder());
		saveDialog.add(namePanel);
		JButton save = new JButton("Save");
		Main.theme.affect(save);
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = new File(
						dirField.getText() + "\\" + nameField.getText() + typeBox.getSelectedItem());
				System.out.println("Exporting image to " + f);
				try {
					BufferedImage bf = new BufferedImage(Main.getBoard().getPaperWidth(), Main.getBoard().getPaperHeight(), 
							typeBox.getSelectedItem().equals(".png")?
									BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB);
					Main.getBoard().paintShapes(bf.createGraphics());
					JDialog d = new JDialog();
					d.getContentPane().setBackground(Main.theme.getBackgroundColor());
					d.setTitle("Preview");
					d.add(new JScrollPane(new JLabel(new ImageIcon(bf))));
					d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					d.pack();
					d.setVisible(true);
					ImageIO.write(bf.getSubimage(0, 0, bf.getWidth(), bf.getHeight()), 
							typeBox.getSelectedItem().toString().substring(1), f);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				saveDialog.dispose();
			}
		});
		saveDialog.add(save);
		saveDialog.pack();
		saveDialog.setVisible(true);
	}
	/**
	 * Adds Rectangle to the current project.
	 * */
	public static void addRectagle() {
		Rectangle r = new Rectangle(0, 0, true, null, 100, 100, Color.BLUE);
		Main.getBoard().addShape(r);
		r.edit();
	}
	/**
	 * Adds Text to the current project.
	 * */
	public static void addText() {
		Text t = new Text(
				0, 0, true, null, Color.BLACK, new Font("Arial", Font.PLAIN, 20), "text");
		Main.getBoard().addShape(t);
		t.edit();
	}
	/**
	 * Adds Picture to the current project.
	 * */
	public static void addPicture() {
		Picture p = new Picture(0, 0, true, null, 150, 50, Resources.defaultImage);
		Main.getBoard().addShape(p);
		p.edit();
	}
	/**
	 * Adds Code to the current project.
	 * */
	public static void addCode() {
		Code c = new Code("<html><i>Your Code</i></html>", true);
		Main.getBoard().addShape(c);
		c.edit();
	}
	/**
	 * Edits the current selected shape.
	 * */
	public static void edit() {
		if (Main.getShapeList().getSelectedShape() == null) {
			return;
		}
		Main.getShapeList().getSelectedShape().edit();
	}
	/**
	 * Removes the current selected shape.
	 * */
	public static void remove() {
		if (Main.getShapeList().getSelectedShape() == null) {
			return;
		}
		if (LDialogs.showConfirmDialog(Main.f, "Are you sure?") == LDialogs.YES_OPTION) {
			Main.getBoard().getShapesList().remove(Main.getShapeList().getSelectedShape());
			Main.getBoard().repaint();
			Main.updateShapeList();
		}
	}
	/**
	 * Shows dialog to change language.
	 * */
	public static void showChangeLanguageDialog() {
		File f = Main.install.getFile("Languages");
		String[] allLanguages = f.list();
		String[] displayLanguages = new String[allLanguages.length + 2];
		displayLanguages[0] = Main.translator.DEFAULT_LANGUAGE;
		for (int i = 1; i < displayLanguages.length - 1; i++) {
			displayLanguages[i] = 
					allLanguages[i - 1].substring(0, allLanguages[i - 1].indexOf('.'));
		}
		displayLanguages[displayLanguages.length - 1] = "Another Language...";
		Object ans = LDialogs.showInputDialog(Main.f, "Choose Language:", "Languages",
				LDialogs.QUESTION_MESSAGE, displayLanguages, Main.translator.getLanguageName());
		if (ans == null) {
			return;
		}else if(ans.equals("Another Language...")) {
			loadLanguageFromWeb();
			return;
		}
		Main.translator.setLanguage(ans.toString());
		if (DefaultSettings.autoSetDefLan || LDialogs.showConfirmDialog(Main.f,
					"Do you want to make " + ans + " your default language?") == LDialogs.YES_OPTION) {
			DefaultSettings.language = ans.toString();
			try {
				DefaultSettings.saveToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			LDialogs.showMessageDialog(Main.f, 
				"<html>To change the language properly,<br/>"
				+ "close the program and reopen it.</html>");
		}
	}
	/**
	 * Shows dialog to load language from web.
	 * */
	public static void loadLanguageFromWeb() {
		String[] allLanguages = Main.website.getResponse("/getLanguagesList.php", "", "GET").split("&&");
		
		if (allLanguages == null || allLanguages[0].equals("")) {
			LDialogs.showMessageDialog(Main.f, "There  are no more supported languages yet."
					, "Warning", LDialogs.WARNING_MESSAGE);
			return;
		}
		JDialog d = new JDialog(Main.f, "Choose Language");
		
		d.setLayout(new GridLayout(allLanguages.length, 1));
		
		ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				d.dispose();
				JDialog loadingDialog = new JDialog(Main.f);
				loadingDialog.setTitle("Loading...");
				loadingDialog.setSize(300, 300);
				loadingDialog.setLayout(new BorderLayout());
				JLabel upSent = new JLabel("Downloading language data, please wait", SwingConstants.CENTER);
				upSent.setFont(new Font("Arial", Font.PLAIN, 18));
				loadingDialog.add(upSent, BorderLayout.NORTH);
				JLabel gif = new JLabel(Resources.loading, SwingConstants.CENTER);
				loadingDialog.add(gif);
				loadingDialog.getContentPane().setBackground(Color.WHITE);
				loadingDialog.pack();
				loadingDialog.setSize(loadingDialog.getWidth() + 200, loadingDialog.getHeight());
				loadingDialog.setVisible(true);
				loadingDialog.setResizable(false);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						File f = Main.install.getFile("/Languages/" + e.getActionCommand() + ".lng");
						try {
							f.createNewFile();
							Main.install.writeToFile(f, 
									Main.website.getResponse("/getLanguageData.php", "name=" + e.getActionCommand(), "GET"));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								loadingDialog.dispose();
								int ans = LDialogs.showConfirmDialog(Main.f, "<html>The language downloaded successfuly.<br/>"
										+ "Would you want to aplly it right now?</html>");
								if (ans == LDialogs.YES_OPTION) {
									Main.translator.setLanguage(e.getActionCommand());
								}
							}
						});
					}
				}).start();
			}
		};
		for (String projectName : allLanguages) {
			JButton b = new JButton(projectName);
			Main.theme.affect(b);
			b.addActionListener(listener);
			d.add(b);
		}
		
		d.pack();
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setVisible(true);
		
	}
}