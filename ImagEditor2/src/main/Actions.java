package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import drawables.Layer;
import drawables.shapes.Code;
import drawables.shapes.Picture;
import drawables.shapes.Rectangle;
import drawables.shapes.Text;
import drawables.shapes.abstractShapes.Shape;
import install.Preferences;
import install.Resources;
import install.saveSystem.Project;
import le.gui.components.LTextArea;
import le.gui.components.LTextField;
import le.gui.dialogs.LDialogs;
import le.log.Logger;
import le.utils.Utils;
import multipicture.MultipictureCreator;
import operatins.AddLayerOperation;
import operatins.OperationsManager;
import operatins.RemoveLayerOperation;
import operatins.SetPaperSizeOperation;
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
			Preferences.showPreferencesdialog();
		}else if (command.equals("Set Paper Size")) {
			setPaperSize();
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
			addRectangle();
		}else if (command.equals("Text")) {
			addText();
		}else if (command.equals("Picture")) {
			addPicture();
		}else if (command.equals("Code")) {
			addCode();
		}else if (command.equals("Edit")) {
			edit();
		}else if (command.equals("Refresh")) {
			System.out.println("Manual Refreshing");
			Main.getBoard().setActiveManualRefreshing(true);
			Main.updateLayersList();
			System.out.println(Main.getBoard().isActiveManualRefreshing());
			Main.getBoard().repaint();
		}else if (command.equals("Undo")) {
			OperationsManager.undo();
			Main.getBoard().repaint();
			Main.updateLayersList();
		}else if (command.equals("Redo")) {
			OperationsManager.redo();
			Main.getBoard().repaint();
			Main.updateLayersList();
		}else if(command.equals("Profile")) {
			Main.myAccount.showAccount();
		}else if (command.equals("Visit Website")) {
			Main.website.openInBrowser();
		}else if (command.equals("Send Report")) {
			sendReport();
		}else if (command.equals("Log")) {
			openLog();
		}else if (command.equals("Multi-Picture")) {
			MultipictureCreator.openDialog();
		}
	}
	public static void setPaperSize() {
		try {
			OperationsManager.operate(new SetPaperSizeOperation(
					Main.getBoard().getPaperWidth(), Main.getBoard().getPaperHeight(),
					Integer.parseInt(LDialogs.showInputDialog(Main.f, "Enter Width:")),
					Integer.parseInt(LDialogs.showInputDialog(Main.f, "Enter Height:"))));
		} catch (Exception e) {
			if (!(e instanceof NumberFormatException)) {
				e.printStackTrace();
			}
		}
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
		dirPanel.add(new JLabel("Directory:"), Main.translator.getBeforeTextBorder());
		JTextField dirField = new JTextField(Main.install.getPath("Projects"));
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
		namePanel.add(new JLabel("Name:"), Main.translator.getBeforeTextBorder());
		JTextField nameField = new JTextField("project");
		if (Main.currentProject.name != null) {
			nameField.setText(Main.currentProject.name);
		}
		namePanel.add(nameField);
		JLabel suffixLabel = new JLabel("<html><t/>.iep</html>");
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
		Main.theme.affect(saveDialog);
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
					Main.getBoard().setExportPaintMode(true);
					Main.getBoard().paintShapes(bf.createGraphics());
					Main.getBoard().setExportPaintMode(false);
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
	public static void addRectangle() {
		addShape(Rectangle.createNewDefaultRectangle());
	}
	/**
	 * Adds Text to the current project.
	 * */
	public static void addText() {
		addShape(Text.createNewDefaultText());
	}
	/**
	 * Adds Picture to the current project.
	 * */
	public static void addPicture() {
		addShape(Picture.createNewDefaultPicture());
	}
	/**
	 * Adds Code to the current project.
	 * */
	public static void addCode() {
		addShape(Code.createNewDefaultCode());
	}
	/**
	 * Adds given shape to the current project.
	 * For GUI purposes (placing the edit window correctly) uses SwingUtilities.invokeLater
	 * method.
	 * */
	public static void addShape(Shape s) {
		OperationsManager.operate(new AddLayerOperation(new Layer(s)));
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Main.f.revalidate();
				s.edit();		
			}
		});
	}
	/**
	 * Edits the current selected shape.
	 * */
	public static void edit() {
		if (Main.getLayersList().getSelectedLayer() == null) {
			return;
		}
		Main.getLayersList().getSelectedLayer().getShape().edit();
	}
	/**
	 * Removes the current selected shape.
	 * */
	public static void remove() {
		if (Main.getLayersList().getSelectedLayer() == null) {
			return;
		}
		if (LDialogs.showConfirmDialog(Main.f, "Are you sure?") == LDialogs.YES_OPTION) {
			OperationsManager.operate(new RemoveLayerOperation(Main.getLayersList().getSelectedLayer()));
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
		if (Preferences.autoSetDefLan || LDialogs.showConfirmDialog(Main.f,
					"Do you want to make " + ans + " your default language?") == LDialogs.YES_OPTION) {
			Preferences.language = ans.toString();
			try {
				Preferences.saveToFile();
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
	public static void moveOneLayerUp(Layer layer) {
		if (Main.getBoard().getLayers().getLast() == layer) {
			LDialogs.showMessageDialog(Main.f, Main.translator.get("This is the top layer!"),
					Main.translator.get("Warning"), LDialogs.WARNING_MESSAGE);
			return;
		}
		int sIndex = Main.getBoard().getLayers().indexOf(layer);
		int upIndex = Main.getBoard().getLayers().indexOf(layer) + 1;
		Layer up = Main.getBoard().getLayers().get(upIndex);
		Main.getBoard().getLayers().set(upIndex, layer);
		Main.getBoard().getLayers().set(sIndex, up);
	}
	public static void moveOneLayerDown(Layer layer) {
		if (Main.getBoard().getLayers().getFirst() == layer) {
			LDialogs.showMessageDialog(Main.f, Main.translator.get("This is the bottom layer!"),
					Main.translator.get("Warning"), LDialogs.WARNING_MESSAGE);
			return;
		}
		int sIndex = Main.getBoard().getLayers().indexOf(layer);
		int downIndex = Main.getBoard().getLayers().indexOf(layer) - 1;
		Layer down = Main.getBoard().getLayers().get(downIndex);
		Main.getBoard().getLayers().set(downIndex, layer);
		Main.getBoard().getLayers().set(sIndex, down);
	}
	
	public static boolean handleTransferable(Transferable transferable) {
		try {
			DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
			if (Utils.contains(dataFlavors, DataFlavor.imageFlavor)) {
				Picture p = Picture.createNewDefaultPicture();
				p.setImage((BufferedImage)transferable.getTransferData(DataFlavor.imageFlavor));
				Actions.addShape(p);
			} else if (Utils.contains(dataFlavors, DataFlavor.javaFileListFlavor)) {
				@SuppressWarnings("rawtypes")
				List filesList = (List)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				for (Object object : filesList) {
					Picture p = Picture.createNewDefaultPicture();
					p.setImage(ImageIO.read(new File(object.toString())));
					Actions.addShape(p);
				}
			} else if (Utils.contains(dataFlavors, DataFlavor.allHtmlFlavor)) {
				Code c = Code.createNewDefaultCode();
				c.setText((String) transferable.getTransferData(DataFlavor.stringFlavor));
				Actions.addShape(c);
			} else if (Utils.contains(dataFlavors, DataFlavor.stringFlavor)) {
				Text t = Text.createNewDefaultText();
				t.setText((String) transferable.getTransferData(DataFlavor.stringFlavor));
				Actions.addShape(t);
			} else {
				return false;
			}
			
		} catch (UnsupportedFlavorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}
	
	public static void copyFromClipboardTo(Point point) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		if (!handleTransferable(clipboard.getContents(null))) {
			throw new IllegalArgumentException("Cannot paste the content on clipboard");
		}
	}
}