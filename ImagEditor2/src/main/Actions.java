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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import install.DefaultSettings;
import install.Resources;
import install.saveSystem.Project;
import le.gui.LSlider;
import le.gui.LTextArea;
import le.gui.LTextField;
import le.log.Logger;
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
						Integer.parseInt(JOptionPane.showInputDialog("Enter Width:")),
						Integer.parseInt(JOptionPane.showInputDialog("Enter Height:")));
			} catch (Exception e) {
				if (!(e instanceof NumberFormatException)) {
					e.printStackTrace();
				}
			}
		}else if (command.equals("Save As Project")) {
			save();
		}else if (command.equals("Open Project from this Computer")) {
			openProjectFromThisComputer();
		}else if (command.equals("Open Project from Web")) {
			openProjectFromWeb();
		}else if (command.equals("Upload Project")) {
			WebProjectsUtils.uploadProject(Main.currentProject);
		}else if (command.equals("Set Language")) {
			Main.translator.showChangeLanguageDialog();
		}else if (command.equals("Rectangle")) {
			addRectagle();
		}else if (command.equals("Text")) {
			addText();
		}else if (command.equals("Picture")) {
			addPicture();
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
	private static void openPreferencesDialog() {
		JDialog preferencesDialog = new JDialog(Main.f, "User Preferences");
		preferencesDialog.setLayout(new BorderLayout());
		preferencesDialog.add(new JLabel("Change the default settings:"), BorderLayout.NORTH);
		
		//Using tabbed pane to sort all preferences by categories
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//Paper's preferences tab, including width, height and zoom
		JPanel paperPrefsPanel = new JPanel(new GridLayout(3, 1));
		//Width GUI
		JPanel widthPanel = new JPanel(new BorderLayout());
		widthPanel.add(new JLabel("Width:"), Main.translator.getBeforeTextBorder());
		JTextField widthField = new JTextField(DefaultSettings.paperWidth + "");
		widthPanel.add(widthField);
		paperPrefsPanel.add(widthPanel);
		//Height GUI
		JPanel heightPanel = new JPanel(new BorderLayout());
		heightPanel.add(new JLabel("Height:"), Main.translator.getBeforeTextBorder());
		JTextField heightField = new JTextField(DefaultSettings.paperHeight + "");
		heightPanel.add(heightField);
		paperPrefsPanel.add(heightPanel);
		//Zoom GUI
		LSlider zoomSlider = new LSlider("Zoom", Main.getZoomSlider().slider.getMinimum(),
				Main.getZoomSlider().slider.getMaximum(), DefaultSettings.paperZoom);
		paperPrefsPanel.add(zoomSlider);
		
		tabbedPane.addTab("Paper", paperPrefsPanel);

		//Language's preferences
		JPanel languagePrefsPanel = new JPanel(new GridLayout(2, 1));
		//Default language GUI
		JPanel defLanPanel = new JPanel(new BorderLayout());
		defLanPanel.add(new JLabel("Defalut Language:"), Main.translator.getBeforeTextBorder());
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
		chooseLanguge.setSelectedItem(DefaultSettings.language);
		defLanPanel.add(chooseLanguge);
		languagePrefsPanel.add(defLanPanel);
		//Auto-set default language
		JCheckBox autoSetDefLan = new JCheckBox("Auto-set Defalut Language When Changing Language", DefaultSettings.autoSetDefLan);
		languagePrefsPanel.add(autoSetDefLan);
		
		tabbedPane.addTab("Language", languagePrefsPanel);
		
		preferencesDialog.add(tabbedPane);
		
		//Advanced tab (save logs and CPU vs RAM priority)
		JPanel advancedPrefsPanel = new JPanel(new GridLayout(2, 1));
		
		//Save logs
		JCheckBox saveLogsCheckBox = new JCheckBox("Save the Logs Every time the Program is Being Used", DefaultSettings.saveLogs);
		advancedPrefsPanel.add(saveLogsCheckBox);
		
		//CPU vs RAM priority
		JCheckBox useRAMCheckBox = new JCheckBox("Use More RAM to Reduce CPU & GPU Usage", DefaultSettings.useMoreRAM);
		advancedPrefsPanel.add(useRAMCheckBox);
		
		tabbedPane.addTab("Advanced", advancedPrefsPanel);
		
		//Apply button
		JButton apply = new JButton("Apply Preferences");
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
						JOptionPane.showMessageDialog(Main.f, "Error while parsing paper dimensions",
								"Parsing Error", JOptionPane.ERROR_MESSAGE);
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
	private static void openProjectFromWeb() {
		if (!WebProjectsUtils.isAccountConnected()) {
			JOptionPane.showMessageDialog(Main.f, "You aren't logged in to your account, please "
					+ "login to your account.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		String[] allProjects = WebProjectsUtils.getProjectsList();
		
		if (allProjects == null) {
			JOptionPane.showMessageDialog(Main.f, "You haven't projects on the web."
					, "Warning", JOptionPane.WARNING_MESSAGE);
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
			b.addActionListener(listener);
			d.add(b);
		}
		
		d.pack();
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setVisible(true);
		
	}
	private static void openProjectFromThisComputer() {
		JFileChooser fc = new JFileChooser(Main.install.getPath("/Projects"));
		fc.showOpenDialog(Main.f);
		Main.switchProject(Project.loadProject(fc.getSelectedFile()));
	}
	/**
	 * Method that pop up a dialog to ask where to  save the project, and than save it as a project.
	 * */
	private static void save() {
		if (Main.currentProject.hasFile()) {
			int ans = JOptionPane.showConfirmDialog(Main.f, 
					"Do you want to save your project into its current location?");
			if (ans == JOptionPane.YES_OPTION) {
				try {
					Main.currentProject.save();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		JDialog saveDialog = new JDialog(Main.f);
		saveDialog.setTitle("Save As Project");
		saveDialog.setLayout(new GridLayout(3, 1));
		JPanel dirPanel = new JPanel(new BorderLayout());
		dirPanel.add(new JLabel("Directory:"), Main.translator.getBeforeTextBorder());
		JTextField dirField = new JTextField();
		dirField.setEditable(false);
		if (Main.currentProject.folder != null) {
			dirField.setText(Main.currentProject.folder);
		}
		dirPanel.add(dirField);
		saveDialog.add(dirPanel);
		JButton browse = new JButton("Browse");
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
		namePanel.add(new JLabel("Name"), Main.translator.getBeforeTextBorder());
		JTextField nameField = new JTextField("project");
		if (Main.currentProject.name != null) {
			nameField.setText(Main.currentProject.name);
		}
		namePanel.add(nameField);
		JLabel suffixLabel = new JLabel("\t.iep");
		namePanel.add(suffixLabel, Main.translator.getAfterTextBorder());
		saveDialog.add(namePanel);
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.currentProject.folder = dirField.getText();
				Main.currentProject.name = nameField.getText();
				System.out.println("Saving project to " + 
						dirField.getText() + "\\" + nameField.getText() + ".iep");
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
		generalLog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		generalLog.setTitle(Main.translator.get("General Log"));
		generalLog.setLayout(new BorderLayout());
		generalLog.add(new JLabel("<html><b>" + Main.translator.get("General Log") + "</b></html>"), BorderLayout.NORTH);
		String styledText = "<html>" + Main.logger.getLog().replaceAll("\n", "<br/>") + "</html>";
		generalLog.add(new JScrollPane(new JLabel(styledText)));
		JButton copyGeneralLog = new JButton("Copy");
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
		errorLog.add(new JLabel("<html><b>" + Main.translator.get("Error Log") + "</b></html>"), BorderLayout.NORTH);
		styledText = "<html>" + Main.logger.getErrorLog().replaceAll("\n", "<br/>") + "</html>";
		errorLog.add(new JScrollPane(new JLabel(styledText)));
		JButton copyErrorLog = new JButton("Copy");
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
	private static void sendReport() {
		JDialog reportDialog = new JDialog(Main.f);
		reportDialog.setTitle("Report");
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(new JLabel(Main.translator.get("Title") + ":"), Main.translator.getBeforeTextBorder());
		LTextField titleField = new LTextField("Your report title here");
		headerPanel.add(titleField);
		reportDialog.add(headerPanel, BorderLayout.NORTH);
		LTextArea contentArea = new LTextArea("Your report content here");
		reportDialog.add(contentArea);
		JButton done = new JButton("done");
		reportDialog.add(done, BorderLayout.SOUTH);
		done.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(reportDialog,
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
		dirPanel.add(new JLabel("Directory:"), Main.translator.getBeforeTextBorder());
		JTextField dirField = new JTextField();
		dirField.setEditable(false);
		dirPanel.add(dirField);
		saveDialog.add(dirPanel);
		JButton browse = new JButton("Browse");
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
		namePanel.add(new JLabel("Name"), Main.translator.getBeforeTextBorder());
		JTextField nameField = new JTextField("picture");
		namePanel.add(nameField);
		JComboBox<String> typeBox = new JComboBox<String>(new String[] {".png", ".jpg"});
		namePanel.add(typeBox, Main.translator.getAfterTextBorder());
		saveDialog.add(namePanel);
		JButton save = new JButton("Save");
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
					Main.getBoard().paintShapes(bf.getGraphics());
					JDialog d = new JDialog();
					d.setTitle("Preview");
					d.add(new JLabel(new ImageIcon(bf)));
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
	public static void addRectagle() {
		Rectangle r = new Rectangle(0, 0, true, null, 100, 100, Color.BLUE);
		Main.getBoard().addShape(r);
		r.edit();
	}
	public static void addText() {
		Text t = new Text(
				0, 0, true, null, Color.BLACK, new Font("Arial", Font.PLAIN, 20), "text");
		Main.getBoard().addShape(t);
		t.edit();
	}
	public static void addPicture() {
		Picture p = new Picture(0, 0, true, null,
				new BufferedImage(150, 50, BufferedImage.TYPE_INT_RGB), 100, 100);
		Main.getBoard().addShape(p);
		p.edit();
	}
	public static void edit() {
		if (Main.getShapeList().getSelectedShape() == null) {
			return;
		}
		Main.getShapeList().getSelectedShape().edit();
	}
	public static void remove() {
		if (Main.getShapeList().getSelectedShape() == null) {
			return;
		}
		if (JOptionPane.showConfirmDialog(Main.f, "Are you sure?") == JOptionPane.YES_OPTION) {
			Main.getBoard().getShapesList().remove(Main.getShapeList().getSelectedShape());
			Main.getBoard().repaint();
			Main.updateShapeList();
		}
	}
}